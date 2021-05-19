package it.polimi.ingsw;

import it.polimi.ingsw.cards.*;
import it.polimi.ingsw.supply.*;
import it.polimi.ingsw.util.MessageManager;
import it.polimi.ingsw.util.MessageParser;
import it.polimi.ingsw.view.Screen;
import it.polimi.ingsw.view.View;

import java.io.IOException;

import java.util.*;

public class Client {

    private MessageManager message;

    //TODO: decide actual type of local objects client-side (View classes)
    private MarketBoard.ResourceMarket marketTray;
    private List<Resource> whiteExchange;

    public static void main(String[] args) {

        Client client = new Client();

        try {
            client.message = new MessageManager("127.0.0.1",2703);
        } catch (IOException e) {
            Screen.printError("Server unavailable...");
            return;
        }

        client.message.start();

        if(!client.message.receive().equals("welcome"))
            System.out.println(">> Unable to be welcomed..");
        else System.out.println(">> Successfully connected..");

        Scanner input = new Scanner(System.in);
        String msg = "";

        while(!msg.equals("esc")) {
            System.out.print(">> ");
            msg = input.nextLine();

            switch(msg) {
                case "new":
                    client.newGame();
                    break;
                case "join":
                    client.joinGame();
                    break;
            }
        }

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
            View.showError("Something went wrong...");
            return;
        }

        this.message.send(MessageParser.message("setNickname",View.selectNickname()));
        if(!this.message.receive().equals("OK")) {
            View.showError("Something went wrong...");
            return;
        }

        this.message.send(MessageParser.message("setNumPlayer",View.selectNumberOfPlayer()));

        answer = this.message.receive();
        while(answer.equals("invalidNumPlayer")) {
            View.showError("Invalid number of player...");
            this.message.send(MessageParser.message("setNumPlayer",View.selectNumberOfPlayer()));
        }

        if(!answer.equals("WAIT")) View.showError("Something went wrong: " + answer);

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
            View.showError("Name already taken!");
            this.message.send(MessageParser.message("setNickname",View.selectNickname()));
        }

        this.playGame();
    }

    public void playGame() {

        String answer;

        View.fancyTell("Waiting for other players!");
        do { answer = this.message.receive(); } while(!answer.equals("GameStart"));
        View.gameStart();

        boolean active = true;
        while(active) {

            do {
                answer = this.message.receive();
                if(answer.equals("GameEnd")) active = false;
                else if(!answer.equals("PLAY")) View.showAction(answer);
            } while(!answer.equals("PLAY") && active);

            if(answer.equals("PLAY")) {

                switch(View.selectAction()) {
                    case "buyDevCard":
                        this.message.send("buyDevCard");
                        this.buyDevelopmentCard();
                        break;
                    case "takeResources":
                        this.message.send("takeResources");
                        this.takeResources();
                        break;
                    case "activateProduction":
                        this.message.send("activateProduction");
                        this.activateProduction();
                        break;

                    // TODO: remove
                    case "test":
                        this.message.send("test");
                        break;
                }
            }
        }

        View.gameEnd();
    }

    public void buyDevelopmentCard() {

        // TODO: gli errori sono poco significativi... occorre far capire cosa è successo!

        String answer;
        String selection;
        int cardLevel;
        Color cardColor;

        answer = this.message.receive();
        if (!answer.equals("OK")) {
            View.showError("This action cannot be performed due to some unknown error...");
            return;
        }

        View.tell("This is the market board, please choose a card!");

        do {
            // TODO: mostrare il mercato

            selection = View.selectDevCard();
            if(selection.equals("esc"))
            {
                View.tell("Command cancelled...");
                this.message.send("esc");
                return;
            }

            cardLevel = Character.getNumericValue(selection.charAt(1));
            cardColor = Color.toColor(Character.toString(selection.charAt(0)));
            this.message.send(MessageParser.message("Buy", cardLevel, cardColor));
            answer = this.message.receive();

            // if the server doesn't allows the card to be bought
            if (answer.equals("KO")) {
                View.showError("It's not possible to buy this card, please choose another one.");
            }

        } while(!answer.equals("OK"));

        // TODO: mostrare il DevCardStack

        //the card to buy exists

        View.tell("Please, choose a position for the new card!");
        do {
            selection = View.selectDevCardPosition();
            if (selection.equals("esc")) {
                View.tell("Command canceled..");
                this.message.send("esc");
                return;
            }

            this.message.send(MessageParser.message("position",Character.getNumericValue(selection.charAt(0))));
            answer = this.message.receive();

            if (answer.equals("KO")) {
                View.showError("Please choose another position!");
            }

        } while(!answer.equals("OK"));

        View.tell("Card successfully bought!");
        // TODO: mostrare il DevCardStack
    }

    public void takeResources() {

        String answer;

        answer = this.message.receive();
        if(!answer.equals("OK")) {
            View.showError("This action cannot be performed due to some unknown error...");
            // return;
        }

        // TODO: mostare il mercato delle risorse
        Scanner selection = new Scanner(View.selectMarbles());

        switch(selection.next())
        {
            case "back":
                this.message.send("Quit");
                return;

            case "row":
                this.message.send(MessageParser.message("TakeRow",selection.nextInt()));
                break;

            case "column":
                this.message.send(MessageParser.message("TakeColumn",selection.nextInt()));
                break;
        }

        receiveResources();
    }

    public void receiveResources() {

        MessageParser parser = new MessageParser();
        Scanner input = new Scanner(System.in);

        String answer = this.message.receive();
        parser.parse(answer);

        if(!parser.getOrder().equals("Taken"))
        {
            View.showError("Unable to get resources due to some unknown error...");
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
                if(!answer.equals("OK")) View.showError("Please, try again...");

            } while(!answer.equals("OK"));
        }
        else if(!parser.getOrder().equals("OK"))
            View.showError("Something went wrong...");

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
                    View.showError("It seems that you're configuration was somehow wrong...");
                    break;
            }
        }
    }

    public void activateProduction() {
        if(!this.message.receive().equals("OK")) {
            Screen.printError("Unable to activate production");
            return;
        }

        Scanner input = new Scanner(System.in);
        String cmd;
        String answer;
        MessageParser mp = new MessageParser();
        boolean toRepeat;

        do {
            toRepeat = false;

            do {

                View.factory.print();
                System.out.print("\n>> ");
                cmd = input.nextLine().toUpperCase();
                Scanner order = new Scanner(cmd);

                if(order.next().equals("ACTIVE")) {
                    if(order.hasNext()) {
                        try {
                            String[] toActive = order.next().split(",");
                            for(String index : toActive) {
                                View.factory.setActive(Integer.parseInt(index));
                            }
                        }
                        catch(NumberFormatException e) {
                            Screen.printError("Some arguments are wrong...");
                        }
                    }
                }

            } while(!cmd.equals("DONE"));

            this.message.send(MessageParser.message("active",View.factory.getActive()));

            answer = this.message.receive();
            if(answer.equals("NotEnoughResources")) {
                Screen.printError("Seems that you do not have enough resources...");
                toRepeat = true;
            }
            else if(!answer.equals("OK")) {
                Screen.printError("Something went wrong...");
                toRepeat = true;
            }

        } while(toRepeat);

        answer = this.message.receive();
        mp.parse(answer);
        if(mp.getOrder().equals("convert")) {
            System.out.println(">> You have " + mp.getIntParameter(0) + " resources to choose!");
            this.selectResourcesWithServer(mp.getIntParameter(0));
            answer = this.message.receive();
            mp.parse(answer);
        }

        if(answer.equals("COMPLETE"))
            System.out.println(">> Successfully activated production!");
    }

    public void selectResourcesWithServer(int amount) {
        while(true) {
            ResourcePack selected = selectResources(amount);
            this.message.send(MessageParser.message("selected",selected));

            if(this.message.receive().equals("SelectionNotValid")) {
                Screen.printError("Selection not valid... try again!");
            }
            else return;
        }
    }

    public static ResourcePack selectResources(int amount) {

        LinkedList<Resource> selected = new LinkedList<>();
        LinkedList<Integer> quantity = new LinkedList<>();

        ResourcePack result = new ResourcePack();
        Scanner input = new Scanner(System.in);

        String cmd;
        int toAdd;

        do {
            Screen.print(result);
            if(amount != 0) System.out.print(" (" + amount + " x RESOURCE) >> ");
            else System.out.print(" (DONE?) >> ");
            cmd = input.nextLine().toUpperCase();

            if(cmd.equals("BACK")) {
                if(!selected.isEmpty()) {
                    try {
                        result.consume(selected.poll(),quantity.getFirst());
                        if(!quantity.isEmpty()) amount = amount + quantity.poll();
                    } catch(Exception ignored) { /* Cannot happen! */ }
                }
            }
            else if(cmd.equals("DONE")) {
                if(amount == 0) return result;
                else System.out.println("\t>> There are still resource left to decide!");
            }
            else {
                try {
                    Scanner scanner = new Scanner(cmd);
                    Resource res = Resource.valueOf(scanner.next());

                    if(res.isSpecial())
                        Screen.printError("Cannot convert to a special resource!");
                    else {
                        if(scanner.hasNextInt()) toAdd = scanner.nextInt();
                        else toAdd = 1;

                        if(toAdd > amount) System.out.println("\t>> You are asking for too many resources...");
                        else {
                            result.add(res,toAdd);
                            selected.addFirst(res);
                            quantity.addFirst(toAdd);

                            amount = amount - toAdd;
                        }
                    }
                } catch(IllegalArgumentException e) {
                    System.out.println("\t>> Unknown resource :(");
                }
            }

        } while(true);
    }
}
