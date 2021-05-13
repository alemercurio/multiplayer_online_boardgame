package it.polimi.ingsw;
import it.polimi.ingsw.cards.*;
import it.polimi.ingsw.supply.*;
import it.polimi.ingsw.faith.*;

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
        this.playerBoard = new PlayerBoard(market,faithTrack);
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

    public String receive() {
        return this.messageIn.nextLine();
    }

    private void newGame() {
        String msg;
        String nickname;
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
        nickname = mp.getStringParameter(0);
        this.send("OK");

        // Set Number of Players
        msg = this.receive();
        mp.parse(msg);
        while(!mp.getOrder().equals("setNumPlayer") || mp.getNumberOfParameters() != 1) {
            this.send("InvalidNumPlayer");
            msg = this.receive();
            mp.parse(msg);
        }

        this.game = Game.newGame(this,nickname,mp.getIntParameter(0));
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
            this.game.setNickname(this, mp.getStringParameter(0));
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
        this.send("PLAY");
        cmd = this.receive();

        switch(cmd) {
            case "buyDevCard":
                System.out.println(">> " + this.game.getNickname(this) + " wants to buy a DevCard");
                if(!this.game.isSinglePlayer())
                    this.game.broadCast(this.game.getNickname(this) + " bought a DevCard...");
                this.send("OK");
                try {
                    this.buyDevelopmentCard();
                } catch (NoSuchDevelopmentCardException e) {
                    e.printStackTrace();
                }
                break;
            case "takeResources":
                System.out.println(">> " + this.game.getNickname(this) + " wants resources");
                this.takeResources();
                if(!this.game.isSinglePlayer())
                    this.game.broadCast(this.game.getNickname(this) + " has gathered resources...");
                break;
        }
    }

    public void buyDevelopmentCard() throws NoSuchDevelopmentCardException {
        MessageParser currentCommand = new MessageParser();
        currentCommand.parse(this.receive());
        if (currentCommand.getOrder().equals("esc"))
            return;

        int cardLevel = 0;
        Color cardColor = null;
        while(currentCommand.getOrder().equals("Buy")) {
            System.out.println("livello: " + currentCommand.getIntParameter(0) + " colore: " + getColor(currentCommand.getIntParameter(1)));
            // TODO: add the condition: (!playerBoard.canBuyDevCard(currentCommand.getIntParameter(0), getColor(currentCommand.getIntParameter(1))))
            if(false) {
                this.send("KO");
            } else {
                cardLevel = currentCommand.getIntParameter(0);
                cardColor = getColor(currentCommand.getIntParameter(1));
                this.send("OK");
            }
            currentCommand.parse(this.receive());
            if (currentCommand.getOrder().equals("esc"))
                return;
        }

        // Now, currentCommand.getOrder().equals("position") is True
        boolean bought = false;
        while ((currentCommand.getOrder().equals("position")) && (!bought)) {
            if (!buy(cardLevel,cardColor,currentCommand.getIntParameter(0)))
                this.send("KO");
            else {
                this.send("OK");
                bought = true;
            }
            if (!bought)
                currentCommand.parse(this.receive());
            if (currentCommand.getOrder().equals("esc"))
                return;
       }
    }

    public boolean buy(int level, Color color, int position) {
        DevelopmentCard card;
        try {
            card = game.market.getDevelopmentCard(level,color);
            System.out.println("the card to buy is : " + card);
            //if it's possible to position the card then buy it
            if (this.playerBoard.canBeStored(card, position)){
                card = this.playerBoard.buyDevelopmentCard(level,color);
                System.out.println("the card bought is : " + card);
                playerBoard.storeDevelopmentCard(card, position);
                return true;
            }
            else return false;
        } catch (NoSuchDevelopmentCardException | NonPositionableCardException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Color getColor(int i) {
        switch (i) {
            case 0:
                return Color.GREEN;
            case 1:
                return Color.BLUE;
            case 2:
                return Color.YELLOW;
            case 3:
                return Color.PURPLE;
            default:
                return null;
        }
    }

    public void takeResources() {
        MessageParser parser = new MessageParser();

        this.send("Ack");

        String msg = this.receive();
        if(msg.equals("Quit")) {
            System.out.println("Back to action choice...");
        }
        else {
            parser.parse(msg);
            if(parser.getOrder().equals("TakeRow")) {
                int row = parser.getIntParameter(0);
                System.out.println("RIGA SCELTA: numero "+row);
                ResourcePack gatheredResources = game.market.takeRow(row);
                sendResources(gatheredResources);
            }
            else if(parser.getOrder().equals("TakeColumn")) {
                int column = parser.getIntParameter(0);
                System.out.println("COLONNA SCELTA: numero "+column);
                ResourcePack gatheredResources = game.market.takeColumn(column);
                sendResources(gatheredResources);

            }
        }
    }

    private void sendResources(ResourcePack gatheredResources) {
        MessageParser parser = new MessageParser();

        this.send("UpdateResourcesView("+gatheredResources+","+"RESOURCEMARKET"+")");
        int numVoid = gatheredResources.get(Resource.VOID);
        if(numVoid > 0) {
            String request = this.receive();
            parser.parse(request);
            if(parser.getOrder().equals("ExchangeWhitesWith")) {
                ResourcePack prova = ResourcePack.fromString(parser.getStringParameter(0));
                //TODO: replace 'true' with 'playerBoard.convertResources(numVoid, ResourcePack.fromString(parser.getStringParameter(0)))'
                if(true) {
                    this.send("OK");
                }
            }
        }
    }
}
