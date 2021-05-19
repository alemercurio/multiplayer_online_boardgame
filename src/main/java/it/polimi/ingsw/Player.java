package it.polimi.ingsw;
import it.polimi.ingsw.cards.*;
import it.polimi.ingsw.supply.*;
import it.polimi.ingsw.faith.*;
import it.polimi.ingsw.util.MessageParser;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Player implements Runnable {
    private final int ID;
    private String nickname;
    private final Socket socket;
    private final Scanner messageIn;
    private final PrintWriter messageOut;

    private Game game;
    private PlayerBoard playerBoard;
    private boolean isActive;
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
        this.send("welcome");
        do {
            msg = receive();
            switch(msg) {
                case "JoinGame":
                    if(this.joinGame()) this.playGame();
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

            this.isActive = false;
            this.hasEnded = false;

        } while(!msg.equals("esc"));
    }

    public void send(String message) {
        this.messageOut.println(message);
        this.messageOut.flush();
    }

    public String receive() { return this.messageIn.nextLine(); }

    private void newGame() {
        String msg;
        MessageParser mp = new MessageParser();

        this.send("OK");

        // Set Player Name
        msg = this.receive();
        mp.parse(msg);
        while(!mp.getOrder().equals("setNickname") || mp.getNumberOfParameters() != 1) {
            this.send("InvalidOption");
            msg = this.receive();
            mp.parse(msg);
        }
        this.nickname = mp.getStringParameter(0);
        this.send("OK");

        // Set Number of Players
        msg = this.receive();
        mp.parse(msg);
        while(!mp.getOrder().equals("setNumPlayer") || mp.getNumberOfParameters() != 1) {
            this.send("InvalidNumPlayer");
            msg = this.receive();
            mp.parse(msg);
        }

        this.game = Game.newGame(this,this.nickname,mp.getIntParameter(0));
        this.send("WAIT");

        this.game.start();
    }

    private boolean joinGame() {
        if(Game.hasNewGame()) {
            String msg;
            MessageParser mp = new MessageParser();

            this.game = Game.join(this);
            this.send("OK");

            msg = this.receive();
            mp.parse(msg);
            while(!mp.getOrder().equals("setNickname") || mp.getNumberOfParameters() != 1 || !this.game.nameAvailable(mp.getStringParameter(0))) {
                this.send("InvalidNickname");
                msg = this.receive();
                mp.parse(msg);
            }
            this.nickname = mp.getStringParameter(0);
            this.game.setNickname(this,this.nickname);
            this.send("WAIT");

            return true;
        }
        else {
            this.send("NoGameAvailable");
            return false;
        }
    }

    // Round Management

    public synchronized void setActive() {
        this.isActive = true;
        notifyAll();
    }

    public synchronized void waitForActive() {
        while(!this.isActive) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void setHasEnded() {
        this.hasEnded = true;
    }

    public void playGame() {
        while(!this.hasEnded) {
            this.waitForActive();
            if(!this.hasEnded) {
                this.playRound();
                this.isActive = false;
                this.game.nextPlayer(this);
            }
        }
    }

    public void playSoloGame() {
        while(!this.hasEnded) {
            this.playRound();
            if(!this.hasEnded) this.game.nextPlayer(this);
        }
        this.send("GameEnd");
    }

    public void playRound() {
        String cmd;
        // TODO: perfect
        this.game.broadCast("Turno di " + this.nickname);
        this.send("PLAY");
        cmd = this.receive();

        switch(cmd) {
            case "buyDevCard":
                System.out.println(">> " + this.game.getNickname(this) + " wants to buy a DevCard");
                if(!this.game.isSinglePlayer())
                    this.game.broadCast(this.game.getNickname(this) + " bought a DevCard...");
                this.send("OK");
                this.buyDevelopmentCard();
                break;

            case "takeResources":
                System.out.println(">> " + this.game.getNickname(this) + " wants resources");
                this.takeResources();
                if(!this.game.isSinglePlayer())
                    this.game.broadCast(this.game.getNickname(this) + " has gathered resources...");
                break;

            case "activateProduction":
                if(!this.game.isSinglePlayer())
                    this.game.broadCast(this.nickname + " has activated production...");
                this.activateProduction();
                break;

            // TODO: remove
            case "test":
                this.playerBoard.storage.strongbox.add(new ResourcePack(10,10,10,10));
                this.playerBoard.factory.addProductionPower(new Production(
                        new ResourcePack(2),
                        new ResourcePack(0,2,0,0,0,6)));
                this.playerBoard.addWhite(Resource.COIN);
                this.playerBoard.addWhite(Resource.SHIELD);
                this.playerBoard.addLeaderStock(new StockPower(2,Resource.COIN));
                break;
        }
    }

    public void buyDevelopmentCard() {

        MessageParser cmd = new MessageParser();
        int cardLevel = 0;
        Color cardColor = null;
        boolean cardSelected = false;

        // TODO: update del mercato delle carte

        do {
            cmd.parse(this.receive());

            if(cmd.getOrder().equals("esc")) return;
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

            if(cmd.getOrder().equals("esc")) return;
            else if(cmd.getOrder().equals("position"))
            {
                if(this.playerBoard.canBeStored(cardLevel,cmd.getIntParameter(0)))
                {
                    try {
                        DevelopmentCard devCard = this.game.market.buyDevelopmentCard(cardLevel,cardColor);
                        this.playerBoard.storeDevelopmentCard(devCard,cmd.getIntParameter(0));
                    } catch (NonPositionableCardException | NoSuchDevelopmentCardException ignored) {
                        /* this should never happen */
                    }

                    // TODO: update del DevCardStack
                    this.send("OK");
                    return;
                }
                else this.send("KO");
            }

        } while(true);
    }

    public void takeResources() {

        MessageParser parser = new MessageParser();

        this.send("OK");
        String msg = this.receive();

        if(msg.equals("Quit")) {
            System.out.println("Back to action choice...");
        }
        else {
            parser.parse(msg);

            if(parser.getOrder().equals("TakeRow")) {

                int row = parser.getIntParameter(0);
                ResourcePack gatheredResources = game.market.takeRow(row);
                sendResources(gatheredResources);
            }

            else if(parser.getOrder().equals("TakeColumn")) {

                int column = parser.getIntParameter(0);
                ResourcePack gatheredResources = game.market.takeColumn(column);
                sendResources(gatheredResources);
            }
        }
    }

    private void sendResources(ResourcePack gatheredResources) {

        MessageParser parser = new MessageParser();

        this.send(MessageParser.message("Taken",gatheredResources));
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
                    int wasted = this.playerBoard.storage.warehouse.done();
                    if(wasted != 0)
                        this.send(MessageParser.message("wasted",wasted));
                    else this.send("Complete");
                    return;
                }
                else this.send("InvalidConfiguration");
            }
            else this.send("InvalidOperation");
        }
    }

    public void activateProduction() {
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

            if(mp.getOrder().equals("esc")) return;
            else if(mp.getOrder().equals("active")) {
                this.playerBoard.factory.setActiveProduction(mp.getObjectParameter(0,int[].class));

                try {
                    whiteToConvert = this.playerBoard.activateProduction();
                    this.send("OK");

                } catch (NonConsumablePackException e) {
                    this.send("NotEnoughResources");
                    toRepeat = true;
                }

            } else {
                this.send("InvalidOperation");
                toRepeat = true;
            }

        } while(toRepeat);

        if(whiteToConvert != 0) {
            this.playerBoard.storage.strongbox.add(this.selectResources(whiteToConvert));
        }

        this.send("COMPLETE");
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