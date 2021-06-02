package it.polimi.ingsw.view;

import com.google.gson.Gson;
import it.polimi.ingsw.cards.LeaderCard;
import it.polimi.ingsw.cards.StockPower;
import it.polimi.ingsw.faith.FaithView;
import it.polimi.ingsw.supply.NonConsumablePackException;
import it.polimi.ingsw.supply.Resource;
import it.polimi.ingsw.supply.ResourcePack;

import java.util.*;
import java.util.regex.Pattern;

public class CliView implements View {

    public PlayerView[] otherPlayers;
    public MarketView market = new MarketView();
    public LeaderView leaderStack = new LeaderView();
    public DevelopmentCardView devCardStack = new DevelopmentCardView();
    public FactoryView factory = new FactoryView();
    public WarehouseView warehouse = new WarehouseView();
    public ResourcePack strongbox = new ResourcePack();
    private final FaithView faithTrack = new FaithView();
    public PlayerBoardView playerBoard = new PlayerBoardView();

    private static View cliView;

    public static View getCliView() {
        if(cliView == null) cliView = new CliView();
        return cliView;
    }

    // METODI DI INTERFACCIA

    @Override
    public void tell(String message) {
        System.out.println("\t" + message);
    }

    @Override
    public void fancyTell(String message) {
        System.out.println("FANCY >> " + message);
    }

    @Override
    public String selectConnection() {

        Scanner input = new Scanner(System.in);

        Screen.setColor(105);
        System.out.println("\t\t~ Master of Renaissance ~");
        Screen.reset();

        System.out.println("\tTo use this game you should specifify the IP Address and port of the server.");
        System.out.println("\tBut do not worry! You can do it now: ");

        while(true) {

            System.out.print("\t(IP Port | esc) >> ");
            String selection = input.nextLine();
            if(Pattern.matches("(?:(?:[0-9]{1,3}.){3}(?:[0-9]{1,3})[ ]*[0-9]{1,5})|esc",selection))
                return selection;
            else Screen.printError("Invalid expression.. please try again!");

        }
    }

    @Override
    public String selectGame() {

        Scanner input = new Scanner(System.in);

        while(true) {

            System.out.print(">> ");

            String msg = input.nextLine();

            if(msg.equals("esc")) {
                System.out.println(">> Ciao e a presto! :)");
                return msg;
            }
            else if(msg.equals("new") || msg.equals("join")) return msg;
        }
    }

    @Override
    public void showError(Error error) {
        Screen.printError(error.toString());
    }

    @Override
    public String selectNickname() {
        Scanner input = new Scanner(System.in);
        System.out.print("\tChoose a nickname! << ");
        return input.nextLine();
    }

    @Override
    public int selectNumberOfPlayer() {
        Scanner input = new Scanner(System.in);
        System.out.print("\tHow many players? << ");
        return input.nextInt();
    }

    @Override
    public void gameStart() {
        System.out.println(">> Game starts!");
        if(this.otherPlayers.length != 0)
            System.out.print("\tPlayers: ");
        for(PlayerView player : this.otherPlayers) System.out.print(player.getNickname() + " ");
        System.out.print("\n");
    }

    @Override
    public int[] selectLeader(List<LeaderCard> leaders) {

        Scanner input = new Scanner(System.in);

        System.out.print("\n");

        for(int i = 1; i <= leaders.size(); i++) {
            System.out.print(i + ")");
            Screen.print(leaders.get(i - 1));
            System.out.print("\n");
        }

        while(true) {

            System.out.print("\n(keep n,m) >> ");
            Scanner selection = new Scanner(input.nextLine());

            if(!selection.next().equals("keep") || !selection.hasNext())
                Screen.printError("Please, use \"keep\" followed by the indexes of the leaders you want to keep!");
            else {
                try {

                    int[] selected = Arrays.stream(selection.next().split(","))
                            .mapToInt(Integer::parseInt).distinct().filter(n -> (n > 0 && n <= 4)).toArray();

                    if(selected.length != 2)
                        Screen.printError("You can keep only two Leaders; Their indexes must be between one and four.");
                    else return selected;

                } catch(NumberFormatException e) {
                    Screen.printError("Please, use \"keep\" followed by the indexes of the leaders you want to keep!");
                }
            }
        }
    }

    @Override
    public void showInitialAdvantage(ResourcePack advantage) {
        if(advantage.isEmpty()) System.out.println(">> You received nothing as your initial advantage!");
        else {
            System.out.print(">> You received: ");
            Screen.print(advantage);
            System.out.println(" as your initial advantage!");
        }
    }

    @Override
    public void showAction(String action) {
        System.out.println(">> " + action);
    }

    @Override
    public String selectAction() {

        Scanner input = new Scanner(System.in);

        System.out.println("\n>> It is your turn! Please choose one of the following actions: ");
        System.out.println("\tR) Take resources from the market;");
        System.out.println("\tB) Buy a Development Card;");
        System.out.println("\tP) Activate Production;");
        System.out.println("\tL) Play or Discard a LeaderCard.");
        System.out.println("You can also use \"show\"\n");

        while(true) {

            System.out.print("playing >> ");
            Scanner selection = new Scanner(input.nextLine().toLowerCase());
            String order = selection.next();

            if(order.equals("show")) {

                if(selection.hasNext()) {

                    switch(selection.next()) {

                        case "resources":
                            this.warehouse.printWithoutPending();
                            System.out.print("Strongbox: ");
                            Screen.print(this.strongbox);
                            System.out.print("\n\n");
                            break;

                        case "market:resource":
                            this.market.printResourceMarket();
                            System.out.print("\n");
                            break;

                        case "market:card":
                            this.market.printCardMarket();
                            System.out.print("\n");
                            break;

                        case "market":
                            System.out.println("\t>> Please use \"market:resource\" or \"market:card\"!");
                            break;

                        case "devcard":
                            this.devCardStack.print();
                            break;

                        case "leaders":
                            this.leaderStack.printActive();
                            this.leaderStack.printInactive();
                            break;

                        case "faith":
                            System.out.print("\n");
                            this.faithTrack.print();
                            System.out.print("\n\n");
                            break;

                        case "players":
                            System.out.println("\n>> You are playing against:");
                            for(PlayerView player : this.otherPlayers)
                            {
                                player.print();
                            }
                            System.out.print("\n");
                            break;

                        default:
                            Screen.printError("Ops... I cannot understand what you want to see!");
                            break;
                    }

                } else Screen.printError("Please, specify what you want to see!");
            }
            else if(order.matches("[rbpl]")) {

                switch(order.charAt(0)) {

                    case 'r':
                        return "takeResources";

                    case 'b':
                        return "buyDevCard";

                    case 'p':
                        return "activateProduction";

                    case 'l':
                        return "leader";
                }

            } else Screen.printError("It seems that you have choose an invalid option... please try again!");
        }
    }

    @Override
    public String selectLeaderAction() {

        if(this.leaderStack.noLeaderToPlay()) {
            System.out.println("It seems that you have no inactive leaders left to play!");
            return "back";
        }

        int inactiveLeaderCount = this.leaderStack.printInactive();
        Scanner input = new Scanner(System.in);

        while(true) {
            System.out.print("(play|discard) >> ");
            Scanner selection = new Scanner(input.nextLine().toLowerCase());

            String order = selection.next();
            if(Pattern.matches("play|discard",order))
            {
                if(selection.hasNextInt())
                {
                    int index = selection.nextInt();
                    if(index < 1 || index > inactiveLeaderCount)
                        Screen.printError("Invalid index... please try again!");
                    else return order + " " + index;
                }
                else if(inactiveLeaderCount == 1)
                {
                    return order + " 1";
                }
                else Screen.printError("Please, use \"play\" or \"discard\" followed by the index of the card!");
            }
            else if(order.equals("back")) return "back";
            else Screen.printError("Please, use \"play\" or \"discard\" followed by the index of the card!");
        }
    }

    @Override
    public String selectDevCard() {

        Scanner input = new Scanner(System.in);

        System.out.println(">> This is the market board, please choose a card!");
        this.market.printCardMarket();

        System.out.print(">> ");
        String selected = input.nextLine();

        while(!Pattern.matches("(?:(?<color>[BGPY])(?<level>[123]))|back",selected)) {
            this.showError(Error.INVALID_SELECTION);
            System.out.print(">> ");
            selected = input.nextLine();
        }

        return selected;
    }

    @Override
    public String selectDevCardPosition() {

        Scanner input = new Scanner(System.in);

        System.out.println(">> Please, choose a position for the new card!");
        this.devCardStack.print();

        System.out.print(">> ");
        String position = input.nextLine();

        while(!Pattern.matches("[123]|back",position)) {
            this.showError(Error.INVALID_POSITION);
            System.out.print(">> ");
            position = input.nextLine();
        }

        return position;
    }

    @Override
    public String selectMarbles() {

        Scanner input = new Scanner(System.in);

        this.market.printResourceMarket();

        System.out.print("\nDo you want a row or a column? << ");
        String rowOrCol;
        String index;

        while(true)
        {
            rowOrCol = input.nextLine().toLowerCase();

            switch(rowOrCol)
            {
                case "row":
                    System.out.print("Which row do you want to take? << ");
                    int rowIndex;

                    while(true)
                    {
                        index = input.nextLine();

                        if(index.equals("back")) return "back";

                        try {
                            rowIndex = Integer.parseInt(index);
                            if(rowIndex >= 1 && rowIndex < 4) return "row " + rowIndex;
                            else this.showError(Error.INVALID_ROW);

                        } catch(NumberFormatException e) {
                            this.showError(Error.INVALID_SELECTION);
                        }
                    }

                case "column":
                    System.out.print("Which column do you want to take? << ");
                    int columnIndex;

                    while(true)
                    {
                        index = input.nextLine();

                        if(index.equals("back")) return "back";

                        try {
                            columnIndex = Integer.parseInt(index);
                            if(columnIndex >= 1 && columnIndex < 5) return "column " + columnIndex;
                            else this.showError(Error.INVALID_COLUMN);

                        } catch(NumberFormatException e) {
                            this.showError(Error.INVALID_SELECTION);
                        }
                    }

                case "back":
                    return "back";

                default:
                    this.showError(Error.INVALID_ROW_OR_COLUMN);
                    break;
            }
        }
    }

    @Override
    public void showGatheredResources(ResourcePack gathered) {
        System.out.print(">> You gathered ");
        Screen.print(gathered);
        System.out.print("\n");
    }

    @Override
    public ResourcePack selectWhite(int amount) {

        System.out.println("\tIt seems you have taken some white marbles.");

        if(this.playerBoard.whitePower.size() == 1) {
            Resource fromWhite = this.playerBoard.whitePower.get(0);
            System.out.println("\tThey will be converted to " + fromWhite);
            return new ResourcePack().add(fromWhite,amount);
        }
        else {
            List<Resource> fromWhite = this.playerBoard.whitePower;

            LinkedList<Resource> selected = new LinkedList<>();
            LinkedList<Integer> quantity = new LinkedList<>();

            ResourcePack result = new ResourcePack();
            Scanner input = new Scanner(System.in);

            String cmd;
            int toAdd;

            System.out.println("\tYou can exchange them with " + fromWhite);

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
                    if(amount == 0) {
                        System.out.print("\n");
                        return result;
                    }
                    else System.out.println("\t>> There are still resource left to decide!");
                }
                else {
                    try {
                        Scanner scanner = new Scanner(cmd);
                        Resource res = Resource.valueOf(scanner.next());

                        if(res.isSpecial())
                            System.out.println("\t>> Cannot convert to a special resource!");
                        else if(!fromWhite.contains(res))
                            System.out.println("\t>> You can exchange white marbles only with " + fromWhite);
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

    @Override
    public String selectWarehouse() {

        String cmd;
        Scanner input = new Scanner(System.in);

        do {
            this.warehouse.print();
            System.out.print(">> ");
            cmd = input.nextLine().toUpperCase();

            Scanner read = new Scanner(cmd);

            String order = read.next();

            if(order.equals("MOVE")) {

                int amount = 1;
                if(read.hasNextInt()) amount = read.nextInt();

                String parameter = read.next();

                try {
                    // Resources should be moved from the pending ones.
                    Resource resource = Resource.valueOf(parameter);
                    int destination = 0; // should be overwritten

                    parameter = read.next();

                    if(parameter.equals("INTO")) {
                        destination = read.nextInt();
                    }

                    if(parameter.equals("FROM")) System.out.println(">> Extraneous \"from\" option...");
                    else if(destination == 0) System.out.println(">> Missing \"into\" option...");
                    else this.warehouse.move(destination,resource,amount);
                } catch(IllegalArgumentException e) {

                    // Resources should be moved from another shelf.
                    int source = 0; // indicates pending resources
                    int destination = 0; // should be overwritten

                    if(parameter.equals("FROM")) {
                        source = read.nextInt();
                        parameter = read.next();
                    }

                    if(parameter.equals("INTO")) {
                        destination = read.nextInt();
                        System.out.println("><>> " + destination);
                    }

                    if(destination == 0) {
                        System.out.println(">> Missing \"into\" option...");
                    } else {
                        if(source != 0) this.warehouse.move(destination,source,amount);
                        else System.out.println("Missing \"source\" option or a resource type...");
                    }
                } catch(InputMismatchException e) {
                    System.out.println(">> Invalid command, please try again!");
                }
            }

        } while(!cmd.equals("DONE"));

        return this.warehouse.getConfig();
    }

    @Override
    public String selectProduction() {

        String order;
        Scanner input = new Scanner(System.in);

        while(true) {

            this.factory.print();
            System.out.print("\n\tTotal Cost: ");
            Screen.print(this.factory.productionRequirements());
            System.out.print("\n\tAvailable: ");
            Screen.print(this.strongbox.getCopy().add(this.warehouse.getResources()));
            System.out.print("\n\tProduct: ");
            Screen.print(this.factory.productionResult());

            System.out.print("\n");

            System.out.print("\n>> ");

            Scanner cmd = new Scanner(input.nextLine().toUpperCase());
            order = cmd.next();

            switch(order) {
                case "ACTIVE":
                    if (cmd.hasNext()) {
                        try {
                            String[] toActive = cmd.next().split(",");
                            for (String index : toActive) {
                                this.factory.setActive(Integer.parseInt(index));
                            }
                        } catch (NumberFormatException e) {
                            Screen.printError("Some arguments are wrong...");
                        }
                    }
                    break;
                case "REMOVE":
                    if (cmd.hasNext()) {
                        try {
                            String[] toRemove = cmd.next().split(",");
                            for (String index : toRemove) {
                                this.factory.setInactive(Integer.parseInt(index));
                            }
                        } catch (NumberFormatException e) {
                            Screen.printError("Some arguments are wrong...");
                        }
                    }
                    break;
                case "BACK":
                    return "back";

                case "DONE":
                    if(this.factory.numberOfActive() == 0) return "back";
                    else return "active";

                default:
                    Screen.printError("Invalid command, please try again!");
                    break;
            }
        }
    }

    @Override
    public String getActiveProductions() {
        return this.factory.getActive();
    }

    @Override
    public void clearFactory() {
        this.factory.clear();
    }

    @Override
    public ResourcePack selectResources(int amount) {

        LinkedList<Resource> selected = new LinkedList<>();
        LinkedList<Integer> quantity = new LinkedList<>();

        ResourcePack result = new ResourcePack();
        Scanner input = new Scanner(System.in);

        String cmd;
        int toAdd;

        System.out.println(">> You have " + amount + " resources to choose!");

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
                if(amount == 0) {
                    System.out.print("\n");
                    return result;
                }
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

    @Override
    public ResourcePack selectFreeRequirement(int amount) {

        LinkedList<Resource> selected = new LinkedList<>();
        LinkedList<Integer> quantity = new LinkedList<>();

        ResourcePack result = new ResourcePack();

        ResourcePack available = this.strongbox.getCopy().add(this.warehouse.getResources());

        try {
            available.consume(this.factory.productionRequirements());
        } catch (NonConsumablePackException ignored) { /* this should not happen */ }

        Scanner input = new Scanner(System.in);

        String cmd;
        int toAdd;

        System.out.println(">> To activate production you have to select " + amount + " more resources.");
        System.out.print("\tYou should choose between these: ");
        Screen.print(available);
        System.out.println("\n");

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
                if(amount == 0) {
                    System.out.print("\n");
                    return result;
                }
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

                            if(!available.isConsumable(result)) {
                                Screen.printError("Your selection is not correct :(");
                                try {
                                    result.consume(res,toAdd);
                                } catch (NonConsumablePackException ignored) { /* this should not happen */ }
                            }
                            else {
                                selected.addFirst(res);
                                quantity.addFirst(toAdd);
                                amount = amount - toAdd;
                            }
                        }
                    }
                } catch(IllegalArgumentException e) {
                    System.out.println("\t>> Unknown resource :(");
                }
            }

        } while(true);
    }

    @Override
    public void gameEnd() {
        System.out.println(">> Game has ended!");
    }

    @Override
    public void update(String target,String state) {

        if(target == null || state == null) return;

        switch(target) {

            case "fact":
                this.factory.update(state);
                break;

            case "player":
                Gson parser = new Gson();
                this.otherPlayers = parser.fromJson(state,PlayerView[].class);
                break;

            case "white":
                this.playerBoard.updateWhite(state);
                break;

            case "WHConfig":
                this.warehouse.update(state);
                break;

            case "WH":
                this.warehouse.addStockPower(new Gson().fromJson(state,StockPower.class));
                break;

            case "strongbox":
                this.strongbox = new Gson().fromJson(state,ResourcePack.class);
                break;

            case "leaders":
                this.leaderStack.update(state);
                break;

            case "market:res":
                this.market.updateResourceMarket(state);
                break;

            case "market:card":
                this.market.updateCardMarket(state);
                break;

            case "devCards":
                this.devCardStack.update(state);
                break;

            case "faith:track":
                this.faithTrack.setTrack(state);
                break;

            case "faith:config":
                this.faithTrack.setConfig(state);
                break;
        }
    }
}