package it.polimi.ingsw;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.cards.*;
import it.polimi.ingsw.supply.*;
import it.polimi.ingsw.util.MessageManager;
import it.polimi.ingsw.util.MessageParser;
import it.polimi.ingsw.view.Screen;
import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.Error;

import java.io.IOException;

import java.lang.reflect.Type;
import java.util.*;

public class Client {

    private MessageManager message;

    public static void main(String[] args) {

        Client client = new Client();

        if(args.length != 2) {

            String selection = View.selectConnection();

            if(selection.equals("esc")) return;

            Scanner connectionInfo = new Scanner(selection);
            try {
                client.message = new MessageManager(connectionInfo.next(),connectionInfo.nextInt());
            } catch (IOException e) {
                Screen.printError("Server unavailable...");
                return;
            }
        }
        else {
            try {
                client.message = new MessageManager(args[0],Integer.parseInt(args[1]));
            } catch (IOException e) {
                Screen.printError("Server unavailable...");
                return;
            }
        }



        client.message.start();

        if(!client.message.receive().equals("welcome"))
            System.out.println(">> Unable to be welcomed..");
        else System.out.println(">> Successfully connected..");

        String msg;

        do {

            msg = View.selectGame();

            switch(msg) {
                case "new":
                    client.newGame();
                    break;
                case "join":
                    client.joinGame();
                    break;
            }

        } while(!msg.equals("esc"));

        try {
            client.message.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void newGame() {
        String answer;

        this.message.send("NewGame");
        if(!this.message.receive().equals("OK")) {
            View.showError(Error.UNABLE_TO_START_A_NEW_GAME);
            return;
        }

        this.message.send(MessageParser.message("setNickname",View.selectNickname()));
        if(!this.message.receive().equals("OK")) {
            View.showError(Error.INVALID_NICKNAME);
            return;
        }

        this.message.send(MessageParser.message("setNumPlayer",View.selectNumberOfPlayer()));

        answer = this.message.receive();
        while(answer.equals("invalidNumPlayer")) {
            View.showError(Error.INVALID_NUMBER_OF_PLAYER);
            this.message.send(MessageParser.message("setNumPlayer",View.selectNumberOfPlayer()));
        }

        if(!answer.equals("WAIT")) View.showError(Error.UNKNOWN_ERROR);

        this.playGame();
    }

    public void joinGame() {

        this.message.send("JoinGame");

        if(!this.message.receive().equals("OK")) {
            View.tell("No game available!");
            return;
        }

        this.message.send(MessageParser.message("setNickname",View.selectNickname()));

        while(!this.message.receive().equals("WAIT")) {
            View.showError(Error.NICKNAME_TAKEN);
            this.message.send(MessageParser.message("setNickname",View.selectNickname()));
        }

        this.playGame();
    }

    public void selectLeader() {

        String answer;
        MessageParser mp = new MessageParser();

        mp.parse(this.message.receive());

        if(!mp.getOrder().equals("selectLeader"))
        {
            View.showError(Error.UNKNOWN_ERROR);
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

            int[] selection = View.selectLeader(leaders);
            this.message.send(MessageParser.message("keepLeaders",Arrays.toString(selection)));

            answer = this.message.receive();
            if(!answer.equals("OK")) View.showError(Error.INVALID_SELECTION);
            else return;

        }
    }

    public void initialAdvantage() {

        String answer;
        MessageParser mp = new MessageParser();

        mp.parse(this.message.receive());

        if(mp.getOrder().equals("advantage")) View.showInitialAdvantage(mp.getObjectParameter(0,ResourcePack.class));

        mp.parse(this.message.receive());

        if(mp.getOrder().equals("convert"))
        {
            do {
                this.message.send(MessageParser.message("selected",View.selectResources(mp.getIntParameter(0))));

                answer = this.message.receive();
                if(!answer.equals("OK"))
                    View.showError(Error.UNKNOWN_ERROR);

            }while(!answer.equals("OK"));

            answer = this.message.receive();
            if(answer.equals("setWarehouse")) {

                do {

                    String newConfig = View.selectWarehouse();
                    this.message.send(MessageParser.message("config",newConfig));
                    answer = this.message.receive();

                } while(!answer.equals("OK"));
            }
        }
    }

    public void playGame() {

        String answer;
        MessageParser mp = new MessageParser();

        View.fancyTell("Waiting for other players!");
        do { answer = this.message.receive(); } while(!answer.equals("GameStart"));
        View.gameStart();

        this.selectLeader();
        this.initialAdvantage();

        boolean active = true;
        while(active) {

            do {
                answer = this.message.receive();
                if(answer.equals("GameEnd")) active = false;
                else if(!answer.equals("PLAY")) View.showAction(answer);
            } while(!answer.equals("PLAY") && active);

            if(answer.equals("PLAY")) {

                boolean endRound = true;
                do {
                    switch(View.selectAction()) {

                        case "buyDevCard":
                            this.message.send("buyDevCard");
                            endRound = this.buyDevelopmentCard();
                            break;

                        case "takeResources":
                            this.message.send("takeResources");
                            endRound = this.takeResources();
                            break;

                        case "activateProduction":
                            this.message.send("activateProduction");
                            endRound = this.activateProduction();
                            break;

                        case "leader":
                            this.message.send("leader");
                            this.leaderAction();
                            endRound = false;
                            break;

                        // TODO: remove
                        case "test":
                            this.message.send("test");
                            endRound = false;
                            break;

                    }
                } while(!endRound);
            }
        }

        View.gameEnd();
    }

    public boolean buyDevelopmentCard() {

        // TODO: gli errori sono poco significativi... occorre far capire cosa è successo!

        String answer;
        String selection;
        int cardLevel;
        Color cardColor;

        answer = this.message.receive();
        if (!answer.equals("OK")) {
            View.showError(Error.UNABLE_TO_PLAY_ACTION);
            return false;
        }

        do {
            selection = View.selectDevCard();
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
                View.showError(Error.INVALID_CARD_SELECTION);
            }

        } while(!answer.equals("OK"));

        //the card to buy exists

        do {
            selection = View.selectDevCardPosition();
            if (selection.equals("back")) {
                this.message.send("esc");
                return false;
            }

            this.message.send(MessageParser.message("position",Character.getNumericValue(selection.charAt(0))));
            answer = this.message.receive();

            if (answer.equals("KO")) {
                View.showError(Error.INVALID_POSITION);
            }

        } while(!answer.equals("OK"));

        View.tell("Card successfully bought!");
        // TODO: mostrare il DevCardStack

        return true;
    }

    public boolean takeResources() {

        String answer;

        answer = this.message.receive();
        if(!answer.equals("OK")) {
            View.showError(Error.UNABLE_TO_PLAY_ACTION);
            return false;
        }

        Scanner selection = new Scanner(View.selectMarbles());

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
            View.showError(Error.FAIL_TO_GET_RESOURCES);
            return;
        }

        ResourcePack pendingResources = parser.getObjectParameter(0,ResourcePack.class);
        View.showGatheredResources(pendingResources);

        parser.parse(this.message.receive());

        if(parser.getOrder().equals("convert"))
        {
            do {
                ResourcePack selected = View.selectWhite(parser.getIntParameter(0));
                this.message.send(MessageParser.message("ExchangeWhitesWith",selected));

                answer = this.message.receive();
                if(!answer.equals("OK")) View.showError(Error.CANNOT_CONVERT_WHITE);

            } while(!answer.equals("OK"));
        }
        else if(!parser.getOrder().equals("OK"))
            View.showError(Error.UNKNOWN_ERROR);

        // Place resources into the Warehouse.
        while(true)
        {
            String newConfig = View.selectWarehouse();
            this.message.send(MessageParser.message("configWarehouse",newConfig));

            parser.parse(this.message.receive());

            switch (parser.getOrder()) {

                case "Complete":
                    View.tell("Resources taken successfully!");
                    return;

                case "wasted":
                    View.tell("You have wasted " + parser.getIntParameter(0) + " resources...");
                    return;

                case "InvalidConfiguration":
                    View.showError(Error.WRONG_CONFIGURATION);
                    break;
            }
        }
    }

    public boolean activateProduction() {

        if(!this.message.receive().equals("OK")) {
            View.showError(Error.UNABLE_TO_PLAY_ACTION);
            return false;
        }

        String cmd;
        String answer;
        MessageParser mp = new MessageParser();
        boolean toRepeat;

        do {
            toRepeat = false;

            cmd = View.selectProduction();

            if(cmd.equals("back")) {
                this.message.send("esc");
                return false;
            }

            this.message.send(MessageParser.message("active",View.factory.getActive()));

            mp.parse(this.message.receive());
            answer = mp.getOrder();

            if(answer.equals("convert")) {

                do {
                    ResourcePack selected = View.selectFreeRequirement(mp.getIntParameter(0));
                    this.message.send(MessageParser.message("selected",selected));

                    if(this.message.receive().equals("SelectionNotValid")) {
                        View.showError(Error.INVALID_SELECTION);
                        toRepeat = true;
                    }
                    else toRepeat = false;

                } while(toRepeat);

                answer = this.message.receive();
            }

            if(answer.equals("NotEnoughResources")) {
                View.showError(Error.NOT_ENOUGH_RESOURCES);
                toRepeat = true;
            }
            else if(!answer.equals("OK")) {
                View.showError(Error.UNKNOWN_ERROR);
                toRepeat = true;
            }

        } while(toRepeat);

        answer = this.message.receive();
        mp.parse(answer);

        if(mp.getOrder().equals("convert")) {

             do {

                ResourcePack selected = View.selectResources(mp.getIntParameter(0));
                this.message.send(MessageParser.message("selected",selected));

                if(this.message.receive().equals("SelectionNotValid")) {
                    View.showError(Error.INVALID_SELECTION);
                    toRepeat = true;
                }
                else toRepeat = false;

            } while(toRepeat);

            answer = this.message.receive();
            mp.parse(answer);
        }

        View.factory.clear();

        if(answer.equals("COMPLETE"))
            View.tell("Successfully activated production!");

        return true;
    }

    public void leaderAction() {
        String answer = this.message.receive();

        if(!answer.equals("OK")) {
            View.showError(Error.UNKNOWN_ERROR);
        }

        while(true) {

            String selection = View.selectLeaderAction();

            if(selection.equals("back")) {
                this.message.send("esc");
                return;
            }

            Scanner selected = new Scanner(selection);
            this.message.send(MessageParser.message(selected.next(),selected.nextInt()));

            answer = this.message.receive();
            switch(answer) {
                case "OK":
                    View.tell("Done!");
                    return;

                case "Error":
                    View.showError(Error.UNKNOWN_ERROR);
                    break;

                case "UnableToPlay":
                    View.showError(Error.UNABLE_TO_PLAY_LEADER);
                    break;
            }
        }
    }
}
