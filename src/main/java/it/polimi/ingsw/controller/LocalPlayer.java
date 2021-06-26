package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.resources.*;
import it.polimi.ingsw.model.vatican.FaithTrack;
import it.polimi.ingsw.network.DisconnectedPlayerException;
import it.polimi.ingsw.network.Talkie;
import it.polimi.ingsw.util.MessageBridge;
import it.polimi.ingsw.util.MessageParser;
import it.polimi.ingsw.view.lightmodel.PlayerView;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class LocalPlayer implements Runnable,Talkie {

    private final int ID;
    private String nickname;
    private final MessageBridge.Port port;

    private LocalSoloGame game;
    private PlayerBoard playerBoard;
    private boolean hasEnded;

    public LocalPlayer(int ID) {
        this.ID = ID;
        this.nickname = null;
        this.port = MessageBridge.getBridge().get();
        this.hasEnded = false;
    }

    public int getID() {
        return this.ID;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setForGame(FaithTrack faithTrack, MarketBoard market) {
        this.playerBoard = new PlayerBoard(this,market,faithTrack);
    }

    @Override
    public void run() {
        String msg;

        this.send(MessageParser.message("welcome",this.ID));
        this.login();
        this.send("noLeftGame");

        do {
            msg = receive();
            if(msg.equals("NewGame")) {
                this.game = new LocalSoloGame(this);
                this.hasEnded = false;
                this.play();
            } else this.send("UnknownCommand");
        } while(!msg.equals("esc"));
    }

    public void login() {
        MessageParser mp = new MessageParser();
        do {
            mp.parse(this.receive());
            if(mp.getOrder().equals("login") && mp.getNumberOfParameters() == 1) {
                if(!mp.getStringParameter(0).equals("Lorenzo il Magnifico")) {
                    this.nickname = mp.getStringParameter(0);
                    this.send("OK");
                } else this.send("nameAlreadyTaken");
            } else this.send("InvalidOption");
        } while(this.nickname == null);
    }

    public void send(String message) {
        this.port.send(message);
    }

    public String receive() {
        return this.port.receive();
    }

    public void close() {
        this.port.close();
    }

    // Game Management

    public PlayerView getPlayerStat() {
        return new PlayerView(this.ID,this.nickname,
                this.playerBoard.storage.getAllResource(),
                this.playerBoard.faithTrack.getFaithMarker(),
                this.playerBoard.countPoints());
    }

    public synchronized void setHasEnded() {
        this.hasEnded = true;
    }

    public void play() {
        this.game.start();
        this.selectLeader(this.game.getLeaders());
        this.initialAdvantage();
        this.runSoloGame();
    }

    private void runSoloGame() {
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
        this.send(MessageParser.message("advantage",new ResourcePack()));
        this.send("OK");
    }

    public void playRound() {

        String cmd;
        this.send("PLAY");
        boolean endRound;

        do {
            endRound = false;
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
                        if(this.playerBoard.devCards.getDevCardNumber() == 7) {
                            this.game.endGame();
                        }
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
}
