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
        Scanner input = new Scanner(System.in);

        this.message.send("NewGame");
        if(!this.message.receive().equals("OK")) {
            System.out.println(">> Something went wrong...");
            return;
        }

        System.out.print("\tChoose a nickname! << ");
        this.message.send("setNickname(" + input.nextLine() + ")");
        if(!this.message.receive().equals("OK")) {
            System.out.println(">> Something went wrong...");
            return;
        }

        System.out.print("\tHow many players? << ");
        this.message.send("setNumPlayer(" + input.nextInt() + ")");

        answer = this.message.receive();
        while(answer.equals("invalidNumPlayer")) {
            System.out.print("\tChoose another number of player << ");
            this.message.send("setNumPlayer(" + input.nextInt() + ")");
        }

        if(!answer.equals("WAIT")) System.out.println("Something went wrong... >> " + answer);

        this.playGame();
    }

    public void joinGame() {
        Scanner input = new Scanner(System.in);

        this.message.send("JoinGame");

        if(!this.message.receive().equals("OK")) {
            System.out.println(">> No game available!");
            return;
        }

        System.out.print("\tChoose a nickname! << ");
        this.message.send("setNickname(" + input.nextLine() + ")");
        while(!this.message.receive().equals("WAIT")) {
            System.out.print("\tChoose another nickname! << ");
            this.message.send("setNickname(" + input.nextLine() + ")");
        }

        this.playGame();
    }

    public void playGame() {
        String answer;
        Scanner input = new Scanner(System.in);

        do { answer = this.message.receive(); } while(!answer.equals("GameStart"));

        boolean active = true;
        while(active) {
            do {
                answer = this.message.receive();
                if(answer.equals("GameEnd")) active = false;
                else if(!answer.equals("PLAY")) System.out.println(">> " + answer);
            } while(!answer.equals("PLAY") && active);

            if(answer.equals("PLAY")) {
                String cmd;
                System.out.print("playing >> ");
                cmd = input.nextLine();

                switch(cmd) {
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

                System.out.println(">> " + this.message.receive());
            }
        }

        System.out.println(">> Game has ended...");
    }

    public void buyDevelopmentCard() {
        String answer = " ";
        boolean existsCard = false;
        Scanner input = new Scanner(System.in);
        String msg = "";
        MessageParser mp = new MessageParser();
        int cardLevel;
        Color cardColor;

        //TODO: create a market view that allows the player to see the card the he satisfies the requirements
        MarketBoard marketBoard = new MarketBoard();

        answer = this.message.receive();
        if (answer.equals("OK")) {
            do {
                // if the server doesn't allows the card to be bought
                if (answer.equals("KO")){
                    System.out.println("It's not possible to buy this card, please choose another one.");
                    existsCard = false;
                }
                System.out.println(">> This is the market board, please choose a card..\n" + marketBoard.toString());
                        //at this step, the command must be "buy" or "esc"
                while (!existsCard) {
                    msg = checkCommand("G1", "G2", "G3", "B1", "B2", "B3", "Y1", "Y2", "Y3", "P1", "P2", "P3", "esc");
                    if (msg.equals("esc")) {
                        System.out.println(">> Command canceled..");
                        // message 2
                        this.message.send(msg);
                        return;
                    }
                    existsCard = checkCardExists(marketBoard, msg);
                    if (!existsCard)
                        System.out.println(">> Please, choose a correct card.");
                    System.out.print(">> ");
                }
                cardLevel = Character.getNumericValue(msg.charAt(1));
                cardColor = Color.toColor(Character.toString(msg.charAt(0)));
                System.out.println("cardLevel :" + cardLevel +" color: " + cardColor);
                this.message.send(MessageParser.message("Buy",cardLevel,cardColor));
                answer = this.message.receive();
            } while(answer.equals("KO"));

            //the card to buy exists
            System.out.println("Please, choose a position for the new card");
            msg = input.nextLine();
            if (msg.equals("esc")){
                System.out.println(">> Command canceled..");
                // message 2
                this.message.send(msg);
                return;
            }
            while (!(0 < Character.getNumericValue(msg.charAt(0)) && Character.getNumericValue(msg.charAt(0)) < 4)){
                System.out.println(">> Please choose a position between 1 and 3.");
                System.out.print(">> ");
                msg = input.nextLine();
            }
            this.message.send("position(" +  Character.getNumericValue(msg.charAt(0)) + ")");
            answer = this.message.receive();
            while (answer.equals("KO")) {

                System.out.println(">> It's not possible to append the new card at this position, please enter a new position..");
                System.out.print(">> ");
                msg = input.nextLine();
                if (msg.equals("esc")){
                    System.out.println(">> Command canceled..");
                    // message 2
                    this.message.send(msg);
                    return;
                }
                while (!(0 < Character.getNumericValue(msg.charAt(0)) && Character.getNumericValue(msg.charAt(0)) < 4)){
                    System.out.println(">> Please choose a position between 1 and 3.");
                    System.out.print(">> ");
                    msg = input.nextLine();
                }
                this.message.send("position(" +  Character.getNumericValue(msg.charAt(0)) + ")");
                answer = this.message.receive();
            }
        }
        else {
            System.out.println("Something went wrong..");
            return;
        }
        System.out.println("the card have been successfully bought!!");
        // TODO: update the development card stack
        DevelopmentCardStack stack = new DevelopmentCardStack();
        System.out.println("this is the development card stack on your board:" + stack);
    }

    public String checkCommand(String... acceptedCommands) {
        Scanner input = new Scanner(System.in);
        System.out.print(">>");
        String command = input.nextLine();
        MessageParser mp = new MessageParser();
        mp.parse(command);
        while (!Arrays.asList(acceptedCommands).contains(mp.getOrder())) {
            System.out.println(">> unacceptable command, please try again..");
            System.out.print(">> ");
            command = input.nextLine();
            mp.parse(command);
        }
        return command;
    }

    public boolean checkCardExists(MarketBoard marketBoard, String command) {
        // if the level is not correct
        if (!(0 < Character.getNumericValue(command.charAt(1)) && Character.getNumericValue(command.charAt(1)) < 4))
            return false;
        if (Color.toColor(Character.toString(command.charAt(0))) == null)
            return false;
        else {
                //TODO: nella marketView per il client questo metodo deve considerare le carte di cui il giocatore non soddisfa i requisiti
            try {
                marketBoard.getDevelopmentCard(Character.getNumericValue(command.charAt(1)),Color.toColor(Character.toString(command.charAt(0))));
            } catch (NoSuchDevelopmentCardException e) {
                return false;
            }

        }
        return true;
    }

    public void takeResources() {
        //TODO: remove this initializations
        marketTray = new MarketBoard.ResourceMarket();
        whiteExchange = new ArrayList<>();
        whiteExchange.add(Resource.COIN); // Added VoidPowers to test
        whiteExchange.add(Resource.SHIELD); // Added VoidPowers to test

        MessageParser parser = new MessageParser();
        Scanner input = new Scanner(System.in);

        String answer = this.message.receive();
        if(!answer.equals("Ack"))
            System.out.println("ServerError");

        else {
            System.out.println(marketTray);
            boolean correctInput = false;
            while(!correctInput) {
                System.out.print("Do you want a row or a column? << ");
                String rowOrCol = input.nextLine();
                rowOrCol = rowOrCol.toLowerCase();
                if (rowOrCol.equals("row")) {
                    correctInput = true;
                    boolean correctIndex = false;
                    String index = "";
                    while (!correctIndex && !index.equals("back")) {
                        System.out.print("\nWhich row do you want to take? << ");
                        index = input.nextLine();
                        try {
                            int rowIndex = Integer.parseInt(index);
                            if (rowIndex > 0 && rowIndex < 4) {
                                correctIndex = true;
                                this.message.send("TakeRow(" + rowIndex + ")");
                                receiveResources();
                            } else {
                                System.out.println("\nPlease choose a row between 1 and 3.");
                            }
                        } catch (NumberFormatException e) {
                            if (index.equals("back")) {
                                this.message.send("Quit");
                            }
                            else {
                                System.out.println("\nPlease choose a row between 1 and 3.");
                            }
                        }
                    }
                }
                else if (rowOrCol.equals("column")) {
                    correctInput = true;
                    boolean correctIndex = false;
                    while (!correctIndex) {
                        System.out.print("\nWhich column do you want to take? << ");
                        String index = input.nextLine();
                        try {
                            int colIndex = Integer.parseInt(index);
                            if (colIndex > 0 && colIndex < 5) {
                                correctIndex = true;
                                this.message.send("TakeColumn(" + colIndex + ")");
                                receiveResources();
                            } else {
                                System.out.println("\nPlease choose a column between 1 and 4.");
                            }
                        } catch (NumberFormatException e) {
                            if (index.equals("back")) {
                                this.message.send("Quit");
                            }
                            else {
                                System.out.println("\nPlease choose a column between 1 and 4.");
                            }
                        }
                    }
                }
                else {
                    System.out.println("\nPlease type 'row' or 'column', or 'back' to change action.\n");
                }
            }
        }
    }

    public void receiveResources() {
        MessageParser parser = new MessageParser();
        Scanner input = new Scanner(System.in);

        String update = this.message.receive();
        parser.parse(update);
        if(parser.getOrder().equals("UpdateResourcesView")) {
            String resourcesString = parser.getStringParameter(0);
            System.out.println(">> You gathered "+resourcesString);
            ResourcePack pendingResources = ResourcePack.fromString(resourcesString);
            int numVoid = pendingResources.get(Resource.VOID);
            if(!whiteExchange.isEmpty() && numVoid>0) {
                ResourcePack changes = new ResourcePack();
                System.out.println("It seems you have taken some white marbles.\nYou can exchange them with "+whiteExchange);
                while(numVoid > 0) {
                    System.out.println(">> What do you want to change a white marble with? (remaining: "+numVoid+") << ");
                    String change = input.nextLine();
                    boolean match = false;
                    for(Resource resource : whiteExchange) {
                        if(resource.getAlias().equalsIgnoreCase(change)) {
                            changes.add(resource, 1);
                            match = true;
                            numVoid--;
                        }
                    }
                    if(!match) {
                        System.out.println(">> There is no Resource with that alias you can get...");
                    }
                }
                this.message.send("ExchangeWhitesWith("+changes+")");
                String answer = this.message.receive();
                if(answer.equals("OK")) {
                    pendingResources.flush(Resource.VOID);
                    pendingResources.add(changes);
                }
            }
            //TODO: transform the string in the resource market update
            System.out.println(parser.getStringParameter(1));
        }
        //TODO: print the current view of the Warehouse
        System.out.println("WAREHOUSE");
        for(int shelf=1; shelf<4; shelf++) {
            //TODO: change the way the player do the movement
            System.out.println(">> Do you want to store something on shelf "+shelf+"? y/n << ");
            String answer = input.nextLine();
            if(answer.equalsIgnoreCase("y")) {
                Resource resource = Resource.VOID;
                while (resource.equals(Resource.VOID)) {
                    System.out.println(">> Which Resource do you want to store? << ");
                    String resourceAlias = input.nextLine();
                    resource = Resource.toResource(resourceAlias);
                }
                //TODO: send request to the Server for the movement or do it locally?
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
