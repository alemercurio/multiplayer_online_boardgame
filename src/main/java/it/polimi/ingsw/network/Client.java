package it.polimi.ingsw.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.controller.LocalPlayer;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.resources.*;
import it.polimi.ingsw.util.MessageManager;
import it.polimi.ingsw.util.MessageParser;
import it.polimi.ingsw.util.Screen;
import it.polimi.ingsw.view.*;
import it.polimi.ingsw.controller.Error;
import it.polimi.ingsw.view.cli.CliView;

import java.io.IOException;

import java.lang.reflect.Type;
import java.util.*;

public class Client implements Runnable {

    private View view;
    private MessageManager message;
    private boolean isConnected = true;

    public void setMessageManager(MessageManager manager,boolean isConnected) {
        this.message = manager;
        this.isConnected = isConnected;
    }

    public void setView(View view) {
        this.view = view;
    }

    public static void main(String[] args) {

        Client client = new Client();
        client.setView(CliView.getCliView());

        if(args.length != 2) {

            String selection = client.view.selectConnection();
            switch(selection) {

                case "esc":
                    return;

                case "offline":
                    client.message = new MessageManager(client.view);
                    client.isConnected = false;
                    break;

                default:
                    Scanner connectionInfo = new Scanner(selection);
                    try {
                        client.message = new MessageManager(connectionInfo.next(),connectionInfo.nextInt(),client.view);
                        client.isConnected = true;
                    } catch (IOException e) {
                        client.view.showError(Error.SERVER_OFFLINE);
                        return;
                    }
            }
        }
        else {
            try {
                client.message = new MessageManager(args[0],Integer.parseInt(args[1]),client.view);
            } catch (IOException e) {
                client.view.showError(Error.SERVER_OFFLINE);
                return;
            }
        }

        client.run();
    }

    public void run() {

        this.message.start();

        String msg;
        MessageParser mp = new MessageParser();

        mp.parse(this.message.receive());
        if(!mp.getOrder().equals("welcome")) {
            this.view.showError(Error.UNABLE_TO_BE_WELCOMED);
            return;
        }
        else {
            this.view.setID(mp.getIntParameter(0));
        }

        this.login();

        msg = this.message.receive();
        if(msg.equals("resumeGame?")) {
            boolean resume = this.view.selectResume();
            this.message.send(MessageParser.message("resume",resume));
            if(resume) this.runGame();
        }

        do {

            msg = this.view.selectGame();

            switch(msg) {
                case "new":
                    if(this.isConnected) this.newGame();
                    else this.newLocalGame();
                    break;
                case "join":
                    if(this.isConnected) this.joinGame();
                    else this.view.showError(Error.UNABLE_TO_PLAY_ONLINE);
                    break;
            }

        } while(!msg.equals("esc"));

        this.message.close();
    }

    public void login() {
        while(true) {
            this.message.send(MessageParser.message("login",this.view.selectNickname()));
            String answer = this.message.receive();

            switch(answer) {
                case "InvalidOption":
                    this.view.showError(Error.UNKNOWN_ERROR);
                    break;
                case "nameAlreadyTaken":
                    this.view.showError(Error.NICKNAME_TAKEN);
                    break;
                case "OK":
                    return;
            }
        }
    }

    public void newGame() {
        String answer;

        this.message.send("NewGame");
        if(!this.message.receive().equals("OK")) {
            this.view.showError(Error.UNABLE_TO_START_A_NEW_GAME);
            return;
        }

        this.message.send(MessageParser.message("setNumPlayer",this.view.selectNumberOfPlayer()));
        answer = this.message.receive();

        while(answer.equals("InvalidNumPlayer")) {
            this.view.showError(Error.INVALID_NUMBER_OF_PLAYER);
            this.message.send(MessageParser.message("setNumPlayer",this.view.selectNumberOfPlayer()));
            answer = this.message.receive();
        }

        if(!answer.equals("WAIT")) {
            System.out.println("Merc non rompere le balle");
            this.view.showError(Error.UNKNOWN_ERROR);
        }

        this.playGame();
    }

    public void newLocalGame() {
        this.message.send("NewGame");
        this.playGame();
    }

    public void joinGame() {
        this.message.send("JoinGame");

        String answer = this.message.receive();

        if(answer.equals("waiting")) {
            this.view.tell("Please, wait for other players to join!");
            answer = this.message.receive();
        }

        if(answer.equals("Joined")) this.playGame();
        else view.showError(Error.UNKNOWN_ERROR);
    }

    public void selectLeader() {

        String answer;
        MessageParser mp = new MessageParser();

        mp.parse(this.message.receive());

        if(!mp.getOrder().equals("selectLeader")) {
            this.view.showError(Error.UNKNOWN_ERROR);
            return;
        }

        Type listOfLeaderCard = new TypeToken<List<LeaderCard>>() {}.getType();
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Power.class,new LeaderCard.PowerReader());
        builder.enableComplexMapKeySerialization();
        Gson parser = builder.create();

        String leadersData = mp.getStringParameter(0);
        List<LeaderCard> leaders = parser.fromJson(leadersData,listOfLeaderCard);

        while(true) {

            int[] selection = this.view.selectLeader(leaders);
            this.message.send(MessageParser.message("keepLeaders",Arrays.toString(selection)));

            answer = this.message.receive();
            if(!answer.equals("OK")) this.view.showError(Error.INVALID_SELECTION);
            else return;

        }
    }

    public void initialAdvantage() {

        String answer;
        MessageParser mp = new MessageParser();

        mp.parse(this.message.receive());

        if(mp.getOrder().equals("advantage")) this.view.showInitialAdvantage(mp.getObjectParameter(0,ResourcePack.class));

        mp.parse(this.message.receive());

        if(mp.getOrder().equals("convert"))
        {
            do {
                this.message.send(MessageParser.message("selected",this.view.selectResources(mp.getIntParameter(0))));

                answer = this.message.receive();
                if(!answer.equals("OK"))
                    this.view.showError(Error.UNKNOWN_ERROR);

            }while(!answer.equals("OK"));

            answer = this.message.receive();
            if(answer.equals("setWarehouse")) {

                do {

                    String newConfig = this.view.selectWarehouse();
                    this.message.send(MessageParser.message("config",newConfig));
                    answer = this.message.receive();

                } while(!answer.equals("OK"));
            }
        }
    }

    public void playGame() {

        String answer;

        do { answer = this.message.receive(); } while(!answer.equals("GameStart"));
        this.view.gameStart();

        this.view.disableGameEvent();
        this.selectLeader();
        this.initialAdvantage();
        this.view.enableGameEvent();

        this.runGame();
    }

    private void runGame() {
        String answer;
        boolean active = true;
        while(active) {

            do {
                answer = this.message.receive();
                if(answer.equals("GameEnd")) active = false;
                else if(!answer.equals("PLAY")) { this.view.showError(Error.UNKNOWN_ERROR); }
            } while(!answer.equals("PLAY") && active);

            if(answer.equals("PLAY")) {

                boolean endRound = true;
                this.view.disableGameEvent();

                do {
                    switch(this.view.selectAction()) {

                        case "buyDevCard":
                            this.message.send("buyDevCard");
                            endRound = this.buyDevelopmentCard();
                            this.view.flushGameEvent();
                            if(endRound && this.view.playLeaderAction()) {
                                this.message.send("leader");
                                this.leaderAction();
                            } else this.message.send("pass");
                            break;

                        case "takeResources":
                            this.message.send("takeResources");
                            endRound = this.takeResources();
                            this.view.flushGameEvent();
                            if(endRound && this.view.playLeaderAction()) {
                                this.message.send("leader");
                                this.leaderAction();
                            } else this.message.send("pass");
                            break;

                        case "activateProduction":
                            this.message.send("activateProduction");
                            endRound = this.activateProduction();
                            this.view.flushGameEvent();
                            if(endRound && this.view.playLeaderAction()) {
                                this.message.send("leader");
                                this.leaderAction();
                            } else this.message.send("pass");
                            break;

                        case "leader":
                            this.message.send("leader");
                            this.leaderAction();
                            endRound = false;
                            this.view.flushGameEvent();
                            break;
                    }
                } while(!endRound);

                this.view.enableGameEvent();
            }
        }

        this.view.gameEnd();
    }

    public boolean buyDevelopmentCard() {
        String answer;
        String selection;
        int cardLevel;
        Color cardColor;

        answer = this.message.receive();
        if (!answer.equals("OK")) {
            this.view.showError(Error.UNABLE_TO_PLAY_ACTION);
            return false;
        }

        do {
            selection = this.view.selectDevCard();
            if(selection.equals("back"))
            {
                this.message.send("esc");
                return false;
            }

            cardLevel = Character.getNumericValue(selection.charAt(1));
            cardColor = Color.toColor(Character.toString(selection.charAt(0)));
            this.message.send(MessageParser.message("Buy", cardLevel, cardColor));
            answer = this.message.receive();

            // if the server doesn't allows the card to be bought
            if (answer.equals("KO")) {
                this.view.showError(Error.INVALID_CARD_SELECTION);
            }

        } while(!answer.equals("OK"));

        //the card to buy exists

        do {
            selection = this.view.selectDevCardPosition();
            if (selection.equals("back")) {
                this.message.send("esc");
                return false;
            }

            this.message.send(MessageParser.message("position",Character.getNumericValue(selection.charAt(0))));
            answer = this.message.receive();

            if (answer.equals("KO")) {
                this.view.showError(Error.INVALID_POSITION);
            }

        } while(!answer.equals("OK"));

        return true;
    }

    public boolean takeResources() {

        String answer;

        answer = this.message.receive();
        if(!answer.equals("OK")) {
            this.view.showError(Error.UNABLE_TO_PLAY_ACTION);
            return false;
        }

        Scanner selection = new Scanner(this.view.selectMarbles());

        switch(selection.next())
        {
            case "back":
                this.message.send("Quit");
                return false;

            case "row":
                this.message.send(MessageParser.message("TakeRow",selection.nextInt()));
                break;

            case "column":
                this.message.send(MessageParser.message("TakeColumn",selection.nextInt()));
                break;
        }

        receiveResources();

        return true;
    }

    public void receiveResources() {

        MessageParser parser = new MessageParser();

        String answer = this.message.receive();
        parser.parse(answer);

        if(!parser.getOrder().equals("Taken")) {
            this.view.showError(Error.FAIL_TO_GET_RESOURCES);
            return;
        }

        ResourcePack pendingResources = parser.getObjectParameter(0,ResourcePack.class);
        this.view.showGatheredResources(pendingResources);

        parser.parse(this.message.receive());

        if(parser.getOrder().equals("convert"))
        {
            do {
                ResourcePack selected = this.view.selectWhite(parser.getIntParameter(0));
                this.message.send(MessageParser.message("ExchangeWhitesWith",selected));

                answer = this.message.receive();
                if(!answer.equals("OK")) this.view.showError(Error.CANNOT_CONVERT_WHITE);

            } while(!answer.equals("OK"));
        }
        else if(!parser.getOrder().equals("OK"))
            this.view.showError(Error.UNKNOWN_ERROR);

        // Place resources into the Warehouse.
        while(true)
        {
            String newConfig = this.view.selectWarehouse();
            this.message.send(MessageParser.message("configWarehouse",newConfig));

            parser.parse(this.message.receive());

            switch (parser.getOrder()) {

                case "Complete":
                    return;

                case "wasted":
                    this.view.tell("You have wasted " + parser.getIntParameter(0) + " resources...");
                    return;

                case "InvalidConfiguration":
                    this.view.showError(Error.WRONG_CONFIGURATION);
                    break;
            }
        }
    }

    public boolean activateProduction() {

        if(!this.message.receive().equals("OK")) {
            this.view.showError(Error.UNABLE_TO_PLAY_ACTION);
            return false;
        }

        String cmd;
        String answer;
        MessageParser mp = new MessageParser();
        boolean toRepeat;

        do {
            toRepeat = false;

            cmd = this.view.selectProduction();

            if(cmd.equals("back")) {
                this.message.send("esc");
                this.view.clearFactory();
                return false;
            }

            this.message.send(MessageParser.message("active",this.view.getActiveProductions()));

            mp.parse(this.message.receive());
            answer = mp.getOrder();

            if(answer.equals("convert")) {

                do {
                    ResourcePack selected = this.view.selectFreeRequirement(mp.getIntParameter(0));
                    this.message.send(MessageParser.message("selected",selected));

                    if(this.message.receive().equals("SelectionNotValid")) {
                        this.view.showError(Error.INVALID_SELECTION);
                        toRepeat = true;
                    }
                    else toRepeat = false;

                } while(toRepeat);

                answer = this.message.receive();
            }

            if(answer.equals("NotEnoughResources")) {
                this.view.showError(Error.NOT_ENOUGH_RESOURCES);
                toRepeat = true;
            }
            else if(!answer.equals("OK")) {
                this.view.showError(Error.UNKNOWN_ERROR);
                toRepeat = true;
            }

        } while(toRepeat);

        answer = this.message.receive();
        mp.parse(answer);

        if(mp.getOrder().equals("convert")) {
             do {
                ResourcePack selected = this.view.selectProduct(mp.getIntParameter(0));
                this.message.send(MessageParser.message("selected",selected));

                if(this.message.receive().equals("SelectionNotValid")) {
                    this.view.showError(Error.INVALID_SELECTION);
                    toRepeat = true;
                }
                else toRepeat = false;

            } while(toRepeat);

            answer = this.message.receive();
            mp.parse(answer);
        }

        this.view.clearFactory();

        if(!answer.equals("COMPLETE")) this.view.showError(Error.UNKNOWN_ERROR);
        else view.tell("Done!");

        return true;
    }

    public void leaderAction() {
        String answer = this.message.receive();

        if(!answer.equals("OK")) {
            this.view.showError(Error.UNKNOWN_ERROR);
        }

        while(true) {

            String selection = this.view.selectLeaderAction();

            if(selection.equals("back")) {
                this.message.send("esc");
                return;
            }

            Scanner selected = new Scanner(selection);
            this.message.send(MessageParser.message(selected.next(),selected.nextInt()));

            answer = this.message.receive();
            switch(answer) {
                case "OK":
                    this.view.tell("Done!");
                    return;

                case "Error":
                    this.view.showError(Error.UNKNOWN_ERROR);
                    break;

                case "UnableToPlay":
                    this.view.showError(Error.UNABLE_TO_PLAY_LEADER);
                    break;
            }
        }
    }
}