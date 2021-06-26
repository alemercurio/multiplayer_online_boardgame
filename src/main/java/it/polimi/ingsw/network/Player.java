package it.polimi.ingsw.network;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.resources.*;
import it.polimi.ingsw.model.vatican.*;
import it.polimi.ingsw.util.MessageParser;
import it.polimi.ingsw.view.lightmodel.PlayerView;
import it.polimi.ingsw.util.Screen;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Player implements Runnable, Talkie {
    private int ID;
    private String nickname;
    private final Socket socket;
    private final Scanner messageIn;
    private final PrintWriter messageOut;

    private Game game;
    private PlayerBoard playerBoard;
    private final AtomicBoolean isActive = new AtomicBoolean(false);
    private boolean hasEnded;

    public Player(int ID,Socket client) throws IOException {
        this.ID = ID;
        this.socket = client;
        this.messageIn = new Scanner(this.socket.getInputStream());
        this.messageOut = new PrintWriter(this.socket.getOutputStream());
    }

    public int getID() {
        return this.ID;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setForGame(FaithTrack faithTrack,MarketBoard market) {
        this.playerBoard = new PlayerBoard(this,market,faithTrack);
    }

    @Override
    public void run() {
        String msg;
        this.send(MessageParser.message("welcome",this.ID));

        this.login();

        if(PlayerController.getPlayerController().hasLeftGame(this.nickname)) {
            this.send("resumeGame?");
            MessageParser mp = new MessageParser();
            mp.parse(this.receive());

            if(mp.getOrder().equals("resume")) {
                if(Boolean.parseBoolean(mp.getStringParameter(0))) {
                    PlayerController.getPlayerController().resumeGame(this.nickname).resumePlayer(this);
                    this.runSoloGame();
                }
            }
            else this.send("InvalidOption");
        } else this.send("noLeftGame");

        do {
            msg = receive();
            switch(msg) {
                case "JoinGame":
                    this.joinGame();
                    this.playGame();
                    break;
                case "NewGame":
                    this.newGame();
                    if(this.game.isSinglePlayer()) this.playSoloGame();
                    else this.playGame();
                    break;
                default:
                    this.send("UnknownCommand");
                    break;
            }

            this.isActive.set(false);
            this.hasEnded = false;

        } while(!msg.equals("esc"));
    }

    public void send(String message) {
        try {
            this.messageOut.println(message);
            this.messageOut.flush();
        } catch(Exception e) {
            throw new DisconnectedPlayerException(this);
        }
    }

    public String receive() {
        while(true) {
            String received;
            try { received = this.messageIn.nextLine(); }
            catch(Exception e) { throw new DisconnectedPlayerException(this); }
            if(received.equals("alive")) {
                this.disconnectCounter.set(3);
                this.game.isAlive(this);
            }
            else return received;
        }
    }

    public void close() {
        this.messageIn.close();
        this.messageOut.close();
        try {
            this.socket.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void login() {
        MessageParser mp = new MessageParser();
        PlayerController pc = PlayerController.getPlayerController();

        do {
            mp.parse(this.receive());
            if(mp.getOrder().equals("login") && mp.getNumberOfParameters() == 1) {
                if(pc.registerNickname(mp.getStringParameter(0))) {
                    this.nickname = mp.getStringParameter(0);
                    this.send("OK");
                } else this.send("nameAlreadyTaken");
            } else this.send("InvalidOption");
        } while(this.nickname == null);
    }

    private void newGame() {
        String msg;
        MessageParser mp = new MessageParser();

        this.send("OK");

        // Set Number of Players
        msg = this.receive();
        mp.parse(msg);
        while(!mp.getOrder().equals("setNumPlayer")
                || mp.getNumberOfParameters() != 1
                || mp.getIntParameter(0) < 0
                || mp.getIntParameter(0) > 4)  {
            this.send("InvalidNumPlayer");
            System.out.println("invalid");
            msg = this.receive();
            mp.parse(msg);
        }

        this.game = Game.newGame(this,this.nickname,mp.getIntParameter(0));
        this.send("WAIT");
    }

    private void joinGame() {
        this.game = Game.join(this);
        if(this.game == null) {
            this.isActive.set(false);
            this.send("waiting");
            synchronized(this.isActive) {
                while(!this.isActive.get()) {
                    try {
                        this.isActive.wait();
                    } catch(InterruptedException ignored) { }
                }
            }
        }
        this.isActive.set(false);
        this.send("Joined");
    }

    public void hasJoined(Game game) {
        synchronized(this.isActive) {
            this.game = game;
            this.isActive.set(true);
            this.isActive.notifyAll();
        }
    }

    // Round Management

    public PlayerView getPlayerStat() {
        return new PlayerView(this.ID,this.nickname,
                this.playerBoard.storage.getAllResource(),
                this.playerBoard.faithTrack.getFaithMarker(),
                this.playerBoard.countPoints());
    }

    public void setActive() {
        this.isActive.set(true);
    }

    public synchronized void setHasEnded() {
        this.hasEnded = true;
    }

    public void playSoloGame() {

        ((SoloGame) this.game).start();

        try {
            this.selectLeader(this.game.getLeaders());
            this.initialAdvantage();
        } catch(DisconnectedPlayerException e) {
            return;
        }

        this.runSoloGame();
    }

    private void runSoloGame() {
        ((SoloGame) this.game).setInitialised();
        try {
            while(!this.hasEnded) {
                this.playRound();
                if(!this.hasEnded) this.game.nextPlayer();
            }
            this.send("GameEnd");
        } catch(DisconnectedPlayerException ignored) { }
    }

    public void selectLeader(List<LeaderCard> leaders) {
        Type listOfLeaderCard = new TypeToken<List<LeaderCard>>() {}.getType();
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Power.class,new LeaderCard.PowerReader());
        builder.enableComplexMapKeySerialization();
        Gson parser = builder.create();

        String leadersData = parser.toJson(leaders,listOfLeaderCard);
        this.send(MessageParser.message("selectLeader",leadersData));

        while(true) {

            MessageParser mp = new MessageParser();
            mp.parse(this.receive());

            if(mp.getOrder().equals("keepLeaders"))
            {
                int[] selection = mp.getObjectParameter(0,int[].class);

                if(selection.length != 2) this.send("InvalidSelection");
                else
                {
                    List<LeaderCard> selectedLeaders = new LinkedList<>();

                    try {
                        selectedLeaders.add(leaders.get(selection[0] - 1));
                        selectedLeaders.add(leaders.get(selection[1] - 1));

                        this.playerBoard.giveLeaders(selectedLeaders);
                        this.send(MessageParser.message("update","leaders", parser.toJson(this.playerBoard.leaders)));

                        this.send("OK");
                        return;

                    } catch(IndexOutOfBoundsException e)
                    {
                        this.send("InvalidSelection");
                    }
                }
            }
            else this.send("InvalidOption");
        }
    }

    public void initialAdvantage() {

        MessageParser mp = new MessageParser();
        ResourcePack advantage = this.game.getAdvantage(this);

        this.send(MessageParser.message("advantage",advantage));

        if(!advantage.isEmpty()) {

            int white = this.playerBoard.storeResources(advantage);
            this.send(MessageParser.message("convert",white));

            boolean toRepeat;
            do {
                mp.parse(this.receive());
                if(mp.getOrder().equals("selected")) {

                    ResourcePack selected = mp.getObjectParameter(0,ResourcePack.class);
                    if(selected.get(Resource.VOID) == 0 && selected.get(Resource.FAITHPOINT) == 0 && selected.size() == white) {

                        this.playerBoard.storage.warehouse.add(selected);
                        this.send("OK");
                        toRepeat = false;

                    } else {

                        this.send("KO");
                        toRepeat = true;

                    }

                } else {

                    this.send("InvalidOption");
                    toRepeat = true;

                }
            } while(toRepeat);

            this.send(MessageParser.message("update","WHConfig",this.playerBoard.storage.warehouse.getConfig()));
            this.send("setWarehouse");

            while(true) {
                mp.parse(this.receive());
                if(mp.getOrder().equals("config"))
                {
                    if(this.playerBoard.storage.warehouse.update(mp.getStringParameter(0))) {

                        this.playerBoard.storage.warehouse.done();
                        this.send(MessageParser.message("update","WHConfig",this.playerBoard.storage.warehouse.getConfig()));
                        this.send("OK");
                        return;

                    } else this.send("KO");
                } else this.send("InvalidOption");
            }
        } else this.send("OK");
    }

    public void playRound() {

        String cmd;
        this.send("PLAY");
        boolean endRound = true;

        do {

            cmd = this.receive();
            switch(cmd) {

                case "buyDevCard":
                    endRound = this.buyDevelopmentCard();
                    if(endRound && this.receive().equals("leader")) this.leaderAction();
                    break;

                case "takeResources":
                    endRound = this.takeResources();
                    if(endRound && this.receive().equals("leader")) this.leaderAction();
                    break;

                case "activateProduction":
                    if(this.activateProduction()) {
                        endRound = true;
                        if(!this.game.isSinglePlayer())
                            this.game.broadCast(MessageParser.message("action",Action.ACTIVATE_PRODUCTION,this.nickname));
                    } else endRound = false;
                    if(endRound && this.receive().equals("leader")) this.leaderAction();
                    break;

                case "leader":
                    this.leaderAction();
                    endRound = false;
                    break;

                // TODO: remove
                case "test":
                    Screen.printError(this.nickname + " is cheating!");
                    this.playerBoard.storage.strongbox.add(new ResourcePack(10,10,10,10));
                    this.playerBoard.factory.addProductionPower(new Production(
                            new ResourcePack(2),
                            new ResourcePack(0,2,0,0,0,6)));
                    this.playerBoard.addWhite(Resource.COIN);
                    this.playerBoard.addWhite(Resource.SHIELD);
                    this.playerBoard.addDiscount(new ResourcePack(10,10,10,10));
                    this.playerBoard.addLeaderStock(new StockPower(2,Resource.COIN));
                    this.send(MessageParser.message("update","strongbox",this.playerBoard.storage.strongbox));
                    endRound = false;
                    break;
            }

        } while(!endRound);
    }

    public void leaderAction() {

        this.send(MessageParser.message("update","leaders",this.playerBoard.leaders));
        this.send("OK");

        while(true) {

            MessageParser mp = new MessageParser();
            mp.parse(this.receive());

            switch (mp.getOrder()) {
                case "esc":
                    return;

                case "play":
                    if (this.playerBoard.playLeaderCard(mp.getIntParameter(0) - 1)) {
                        this.send(MessageParser.message("update", "leaders",this.playerBoard.leaders));
                        this.send("OK");
                        if(!this.game.isSinglePlayer())
                            this.game.broadCast(MessageParser.message("action",Action.PLAY_LEADER,this.nickname));
                        return;
                    } else this.send("UnableToPlay");
                    break;

                case "discard":
                    if (this.playerBoard.discardLeader(mp.getIntParameter(0) - 1)) {
                        this.send(MessageParser.message("update", "leaders",this.playerBoard.leaders));
                        this.send("OK");
                        if(!this.game.isSinglePlayer())
                            this.game.broadCast(MessageParser.message("action",Action.DISCARD_LEADER,this.nickname));
                        return;
                    } else this.send("Error");
                    break;

                default:
                    this.send("Error");
                    break;
            }
        }
    }

    public boolean buyDevelopmentCard() {

        MessageParser cmd = new MessageParser();
        int cardLevel = 0;
        Color cardColor = null;
        boolean cardSelected = false;

        this.game.market.updateCardMarket();
        this.send("OK");

        do {
            cmd.parse(this.receive());

            if(cmd.getOrder().equals("esc")) return false;
            else if(cmd.getOrder().equals("Buy"))
            {
                cardLevel = cmd.getIntParameter(0);
                cardColor = cmd.getObjectParameter(1,Color.class);

                if(playerBoard.canBuyDevCard(cardLevel,cardColor)) {
                    this.send("OK");
                    cardSelected = true;
                } else {
                    this.send("KO");
                }
            }

        } while(!cardSelected);

        // Receive the selected position
        do {
            cmd.parse(this.receive());

            if(cmd.getOrder().equals("esc")) return false;
            else if(cmd.getOrder().equals("position"))
            {
                if(this.playerBoard.canBeStored(cardLevel,cmd.getIntParameter(0)))
                {
                    try {
                        this.playerBoard.buyDevCard(cardLevel,cardColor,cmd.getIntParameter(0));
                        if(!this.game.isSinglePlayer())
                            this.game.broadCast(MessageParser.message("action",Action.BUY_DEVELOPMENT_CARD,this.nickname,cardColor,cardLevel));
                    } catch (NonPositionableCardException | NoSuchDevelopmentCardException | NonConsumablePackException ignored) {
                        /* this should never happen */
                    }
                    this.send("OK");
                    return true;
                }
                else this.send("KO");
            }

        } while(true);
    }

    public boolean takeResources() {

        MessageParser parser = new MessageParser();

        this.game.market.updateResourceMarket();

        this.send("OK");
        String msg = this.receive();

        if (!msg.equals("Quit")) {

            parser.parse(msg);

            if (parser.getOrder().equals("TakeRow")) {

                int row = parser.getIntParameter(0) - 1;
                ResourcePack gatheredResources = this.game.market.takeRow(row);
                sendResources(gatheredResources);
                return true;

            } else if (parser.getOrder().equals("TakeColumn")) {

                int column = parser.getIntParameter(0) - 1;
                ResourcePack gatheredResources = this.game.market.takeColumn(column);
                sendResources(gatheredResources);
                return true;
            }

        }
        return false;
    }

    private void sendResources(ResourcePack gatheredResources) {

        MessageParser parser = new MessageParser();

        this.send(MessageParser.message("Taken",gatheredResources));

        if(!this.game.isSinglePlayer())
            this.game.broadCast(MessageParser.message("action",Action.TAKE_RESOURCES,this.nickname,gatheredResources));

        int numVoid = this.playerBoard.storeResources(gatheredResources);

        if(numVoid != 0 && this.playerBoard.hasWhitePower())
        {
            this.send(MessageParser.message("update","white",this.playerBoard.getWhiteExchange()));
            this.send(MessageParser.message("convert",numVoid));

            boolean correct = false;
            do {
                parser.parse(this.receive());

                if(parser.getOrder().equals("ExchangeWhitesWith"))
                {
                    ResourcePack selected = parser.getObjectParameter(0,ResourcePack.class);
                    if(this.playerBoard.convertResources(numVoid,selected))
                    {
                        this.send(MessageParser.message("update","WHConfig",this.playerBoard.storage.warehouse.getConfig()));
                        this.send("OK");
                        correct = true;
                    }
                    else
                    {
                        this.send(MessageParser.message("update","white",this.playerBoard.getWhiteExchange()));
                        this.send("KO");
                    }
                }
                else this.send("InvalidOption");

            } while(!correct);
        }
        else
        {
            this.send(MessageParser.message("update","WHConfig",this.playerBoard.storage.warehouse.getConfig()));
            this.send("OK");
        }

        // Receive new Warehouse's configuration

        while(true)
        {
            parser.parse(this.receive());
            if(parser.getOrder().equals("configWarehouse"))
            {
                if(this.playerBoard.storage.warehouse.update(parser.getStringParameter(0)))
                {
                    int wasted = this.playerBoard.pendingResources();
                    if(wasted != 0) {
                        this.send(MessageParser.message("wasted",wasted));
                        if(!this.game.isSinglePlayer())
                            this.game.broadCast(MessageParser.message("action",Action.WASTED_RESOURCES,this.nickname,wasted));
                    } else this.send("Complete");
                    this.playerBoard.done();
                    return;
                }
                else this.send("InvalidConfiguration");
            }
            else this.send("InvalidOperation");
        }
    }

    public boolean activateProduction() {

        String msg;
        MessageParser mp = new MessageParser();

        this.send(MessageParser.message("update","fact",this.playerBoard.factory));
        this.send("OK");

        boolean toRepeat;
        int whiteToConvert = 0;
        do {
            toRepeat = false;
            msg = this.receive();
            mp.parse(msg);

            if(mp.getOrder().equals("esc")) return false;
            else if(mp.getOrder().equals("active")) {
                this.playerBoard.factory.setActiveProduction(mp.getObjectParameter(0,int[].class));

                if(!this.playerBoard.storage.isConsumable(this.playerBoard.factory.productionRequirements())) {
                    this.send("NotEnoughResources");
                    toRepeat = true;

                } else {

                    whiteToConvert = this.playerBoard.factory.productionRequirements().get(Resource.VOID);

                    if(whiteToConvert != 0)
                    {
                        ResourcePack freeRequirement = selectResources(whiteToConvert);

                        try {
                            whiteToConvert = this.playerBoard.activateProduction(freeRequirement);
                            this.send("OK");

                        } catch (NonConsumablePackException e) {
                            this.send("NotEnoughResources");
                            toRepeat = true;
                        }
                    }
                    else
                    {
                        try {
                            whiteToConvert = this.playerBoard.activateProduction();
                            this.send("OK");

                        } catch (NonConsumablePackException e) {
                            this.send("NotEnoughResources");
                            toRepeat = true;
                        }
                    }
                }

            } else {
                this.send("InvalidOperation");
                toRepeat = true;
            }

        } while(toRepeat);

        if(whiteToConvert != 0) {
            this.playerBoard.storage.strongbox.add(this.selectResources(whiteToConvert));
            this.send(MessageParser.message("update","strongbox",this.playerBoard.storage.strongbox));
        }

        this.send("COMPLETE");
        return true;
    }

    public ResourcePack selectResources(int amount) {

        String msg;
        MessageParser mp = new MessageParser();

        this.send(MessageParser.message("convert",amount));

        while(true) {
            msg = this.receive();
            mp.parse(msg);

            ResourcePack selected = mp.getObjectParameter(0,ResourcePack.class);
            if(selected.get(Resource.VOID) != 0 || selected.get(Resource.FAITHPOINT) != 0 || selected.size() != amount) {
                this.send("SelectionNotValid");
            }
            else {
                this.send("OK");
                return selected;
            }
        }
    }


    // NEW

    private final AtomicInteger disconnectCounter = new AtomicInteger(3);

    public boolean isDisconnected() {
        return (this.disconnectCounter.get() < 0);
    }

    public void reduceDisconnectCounter() {
        if(disconnectCounter.get() == 0) {
            this.game.hasDisconnected(this);
            this.close();
            disconnectCounter.set(-1);
        }
        else if(disconnectCounter.get() > 0) this.disconnectCounter.decrementAndGet();
    }

    private void idle() {
        while(!this.isActive.get()) {
            String received;
            try { received = this.messageIn.nextLine(); }
            catch(Exception e) { throw new DisconnectedPlayerException(this); }

            if(received.equals("alive")) {
                this.disconnectCounter.set(3);
                this.game.isAlive(this);
            } else System.out.println("(GAME) >> " + this.nickname + " sent \"" + received + "\" while it was not permitted...");
        }
    }

    public void playGame() {

        try {
            this.idle();

            this.selectLeader(this.game.getLeaders());
            this.initialAdvantage();

            this.isActive.set(false);

            this.game.waitForOtherPlayer();
            this.idle();

            while(!this.hasEnded) {
                this.idle();

                if(!this.hasEnded) {
                    this.playRound();
                    this.isActive.set(false);
                    this.game.nextPlayer();
                }
            }
        } catch(DisconnectedPlayerException disconnected) {
            if(this.isActive.get()) this.game.nextPlayer();
        }

    }

    public void resume(Player oldPlayer) {
        if(this.nickname.equals(oldPlayer.nickname)) {
            this.ID = oldPlayer.ID;
            this.send(MessageParser.message("update","playerID",this.ID));
            this.game = oldPlayer.game;
            this.playerBoard = oldPlayer.playerBoard;
            this.playerBoard.setPlayer(this);
            this.isActive.set(false);
        }
    }

    public void updateAll() {
        this.playerBoard.updateAll();
    }
}