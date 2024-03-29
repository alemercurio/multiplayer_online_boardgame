package it.polimi.ingsw.view.cli;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.controller.Action;
import it.polimi.ingsw.controller.GameEvent;
import it.polimi.ingsw.model.cards.Color;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.StockPower;
import it.polimi.ingsw.model.resources.NonConsumablePackException;
import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.model.resources.ResourcePack;
import it.polimi.ingsw.model.singleplayer.SoloCross;
import it.polimi.ingsw.model.singleplayer.SoloDiscard;
import it.polimi.ingsw.util.Screen;
import it.polimi.ingsw.view.*;
import it.polimi.ingsw.controller.Error;
import it.polimi.ingsw.view.lightmodel.*;

import java.util.*;
import java.util.regex.Pattern;

public class CliView implements View {

    private final GameView players = new GameView();
    public MarketView market = new MarketView();
    public LeaderView leaderStack = new LeaderView();
    public DevelopmentCardStackView devCardStack = new DevelopmentCardStackView();
    public FactoryView factory = new FactoryView();
    public WarehouseView warehouse = new WarehouseView();
    public ResourcePack strongbox = new ResourcePack();
    private final FaithView faithTrack = new FaithView(this.players);
    public PlayerBoardView playerBoard = new PlayerBoardView();

    private static View cliView;
    private boolean gameEvent = true;
    private final Map<GameEvent,String> gameEventCache = new HashMap<>();

    public static View getCliView() {
        if(cliView == null) cliView = new CliView();
        return cliView;
    }

    @Override
    public void throwEvent(GameEvent event,String eventData) {
        if(!this.gameEvent) {
            synchronized(this.gameEventCache) {
                this.gameEventCache.put(event,eventData);
            }
        }
        else this.showEvent(event,eventData);
    }

    private void showEvent(GameEvent event,String eventData) {
        switch(event) {
            case PLAYER_JOIN:
                System.out.println("\t>> " + eventData + " joins the game.");
                break;

            case JOINED_GAME:
                this.players.printPlayers();
                break;

            case PLAYER_DISCONNECT:
                Screen.setColor(197);
                System.out.println(">> " + eventData + " has disconnected.");
                Screen.reset();
                break;

            case PLAYER_RECONNECT:
                Screen.setColor(227);
                System.out.println(">> " + eventData + " has reconnected.");
                Screen.reset();
                break;

            case POPE_FAVOUR:
                Screen.setColor(105);
                System.out.println("\n>> REPORT SECTION REACHED!");
                Screen.reset();
                this.faithTrack.print();
                System.out.print("\n\n");
                break;

            case ROUND:
                System.out.println("\n>> it is " + eventData + "'s round");
                break;

            case GAME_START:
                this.gameStart();
                break;
        }
    }

    @Override
    public void disableGameEvent() {
        this.gameEvent = false;
    }

    @Override
    public void enableGameEvent() {
        this.gameEvent = true;
        this.flushGameEvent();
    }

    @Override
    public void flushGameEvent() {
        synchronized(this.gameEventCache) {
            for(Map.Entry<GameEvent,String> event : this.gameEventCache.entrySet()) {
                this.showEvent(event.getKey(),event.getValue());
            }
            this.gameEventCache.clear();
        }
    }

    @Override
    public void tell(String message) {
        System.out.println(">> " + message);
    }

    @Override
    public String selectConnection() {

        Scanner input = new Scanner(System.in);

        Screen.setColor(105);
        System.out.println("\t\t~ Master of Renaissance ~");
        Screen.reset();

        System.out.println("\tTo use this game's online features you should specifify the IP Address and port of the server.");
        System.out.println("\tBut do not worry! You can do it now or use \"offline\" instead: ");

        while(true) {

            System.out.print("\t(IP Port | esc) >> ");
            String selection = input.nextLine();
            if(Pattern.matches("(?:(?:[0-9]{1,3}.){3}(?:[0-9]{1,3})[ ]*[0-9]{1,5})|esc",selection)) {
                System.out.print("\n");
                return selection;
            }
            else if(selection.equals("offline")) {
                System.out.print("\n");
                return "offline";
            }
            else Screen.printError("Invalid expression.. please try again!");
        }
    }

    @Override
    public void setID(int playerID) {
        this.players.setCurrentPlayerID(playerID);
    }

    @Override
    public String selectGame() {
        Scanner input = new Scanner(System.in);
        while(true) {
            System.out.print("(new|join) >> ");
            String msg = input.nextLine();
            if(msg.equals("esc")) {
                System.out.println(">> Thank you for playing with us! :)");
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
    public String getNickname() {
        return this.players.getNickname(this.players.getCurrentPlayerID());
    }

    @Override
    public boolean selectResume() {
        Scanner input = new Scanner(System.in);
        System.out.print("\tYou have left a game, do you want to resume it? (Y|N) << ");
        while(true) {
            String selection = input.nextLine().toUpperCase();
            if(selection.matches("[ ]*[Y][ ]*")) return true;
            else if(selection.matches("[ ]*[N][ ]*")) return false;
            else {
                this.showError(Error.INVALID_SELECTION);
                System.out.print("Please, try again! << ");
            }
        }
    }

    @Override
    public int selectNumberOfPlayer() {
        Scanner input = new Scanner(System.in);
        System.out.print("\tHow many players? << ");
        while(true) {
            try {
                return input.nextInt();
            } catch(InputMismatchException e) {
                System.out.print("\tPlease, insert a number between 1 and 4 << ");
                input.nextLine();
            }
        }
    }

    @Override
    public void gameStart() {
        System.out.println("\n>> Game starts!");
        this.players.printPlayers();
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
        if(advantage.isEmpty()) System.out.println(">> You received the Inkwell! You will be the first player!");
        else {
            System.out.print(">> You received: ");
            Screen.print(advantage);
            System.out.println(" as your initial advantage!\n");
        }
    }

    @Override
    public void showAction(String...actionData) {

        Gson parser = new Gson();
        Action action;

        try { action = Action.valueOf(actionData[0]); }
        catch(Exception e) {
            this.showError(Error.UNKNOWN_ERROR);
            return;
        }

        switch(action) {
            case SOLO_ACTION:
                if(actionData.length < 2) {
                    this.showError(Error.UNKNOWN_ERROR);
                    return;
                }
                System.out.print("\n>> Lorenzo il Magnifico: ");
                JsonObject data = parser.fromJson(actionData[1],JsonElement.class).getAsJsonObject();
                if(data.get("type").getAsString().equals("SoloCross"))
                    Screen.print(parser.fromJson(data.get("description"),SoloCross.class));
                else Screen.print(parser.fromJson(data.get("description"),SoloDiscard.class));
                System.out.print("\n");
                break;

            case PLAY_LEADER:
                if(actionData.length < 2) {
                    this.showError(Error.UNKNOWN_ERROR);
                    return;
                }
                System.out.println("\t>> " + actionData[1] + " plays a leader.");
                break;

            case DISCARD_LEADER:
                if(actionData.length < 2) {
                    this.showError(Error.UNKNOWN_ERROR);
                    return;
                }
                System.out.println("\t>> " + actionData[1] + " discards a leader.");
                break;

            case BUY_DEVELOPMENT_CARD:
                if(actionData.length < 4) {
                    this.showError(Error.UNKNOWN_ERROR);
                    return;
                }
                System.out.print("\t>> " + actionData[1] + " buys a ");
                Screen.print(Color.valueOf(actionData[2]));
                System.out.println("\u00d7" + actionData[3] + " Development Card.");
                break;

            case TAKE_RESOURCES:
                if(actionData.length < 3) {
                    this.showError(Error.UNKNOWN_ERROR);
                    return;
                }
                System.out.print("\t>> " + actionData[1] + " takes these resources ");
                Screen.print(ResourcePack.fromString(actionData[2]));
                System.out.println(" from the market.");
                break;

            case WASTED_RESOURCES:
                if(actionData.length < 3) {
                    this.showError(Error.UNKNOWN_ERROR);
                    return;
                }
                System.out.println("\t>> " + actionData[1] + " wastes " + actionData[2] + " resources.");
                break;

            case ACTIVATE_PRODUCTION:
                if(actionData.length < 2) {
                    this.showError(Error.UNKNOWN_ERROR);
                    return;
                }
                System.out.println("\t>> " + actionData[1] + " activates production");
                break;
        }
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

            String order;
            Scanner selection;
            try {
                selection = new Scanner(input.nextLine().toLowerCase());
                order = selection.next();
            } catch(NoSuchElementException e) {
                order = "NOP";
                selection = new Scanner(order);
            }

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
                            this.players.printRank();
                            break;

                        case "option":
                            System.out.println("\n>> You can show any of the following:");
                            System.out.println("\t~ players");
                            System.out.println("\t~ resources");
                            System.out.println("\t~ market:(resource|card)");
                            System.out.println("\t~ devCard");
                            System.out.println("\t~ leaders");
                            System.out.println("\t~ faith\n");
                            break;

                        default:
                            Screen.printError("Ops... I cannot understand what you want to see!");
                            break;
                    }

                } else Screen.printError("Please, specify what you want to see!");
            }
            else if(order.matches("[rbplht]")) {

                switch(order.charAt(0)) {

                    case 'r':
                        return "takeResources";

                    case 'b':
                        return "buyDevCard";

                    case 'p':
                        return "activateProduction";

                    case 'l':
                        return "leader";

                    case 'h':
                        System.out.println("\n>> You can do one of the following action!");
                        System.out.println("\tR) Take resources from the market;");
                        System.out.println("\tB) Buy a Development Card;");
                        System.out.println("\tP) Activate Production;");
                        System.out.println("\tL) Play or Discard a LeaderCard.");
                        System.out.println("You can also use \"show\"\n");
                        break;

                    case 't':
                        return "test";
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

        System.out.println(">> This is the market board, please choose a card!\n");
        this.market.printCardMarket();

        if(this.playerBoard.hasDiscount()) {
            System.out.print("\n>> You have the following discount: ");
            Screen.print(this.playerBoard.getDiscount());
            System.out.print("\n\n");
        }

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
                            System.out.print("(row) >> ");
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
                            System.out.print("(column) >> ");
                        }
                    }

                case "back":
                    return "back";

                default:
                    this.showError(Error.INVALID_ROW_OR_COLUMN);
                    System.out.print("Please, try again << ");
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

                    if(parameter.equals("INTO") && read.hasNextInt()) {
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

                    if(parameter.equals("INTO") && read.hasNextInt()) {
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
            ResourcePack requirement = this.factory.productionRequirements();
            requirement.flush(Resource.VOID);
            available.consume(requirement); }
        catch(NonConsumablePackException ignored) { /* this should not happen */ }

        Scanner input = new Scanner(System.in);

        String cmd;
        int toAdd;

        System.out.println(">> To activate these productions you have to select " + amount + " more resources.");
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
    public ResourcePack selectProduct(int amount) {
        return this.selectResources(amount);
    }

    @Override
    public boolean playLeaderAction() {
        Scanner input = new Scanner(System.in);
        while(true) {
            System.out.print("\nDo you want to play a Leader action? (Y|N) <<  ");
            String answer = input.nextLine().toUpperCase();
            if(answer.matches("[ ]*Y[ ]*")) return true;
            else if(answer.matches("[ ]*N[ ]*")) return false;
            else this.showError(Error.INVALID_SELECTION);
        }
    }

    @Override
    public void gameEnd() {
        System.out.println(">> Game has ended!");
        this.players.printRank();
        System.out.println(">> Thank you for playing!\n");
    }

    @Override
    public void update(String target,String state) {

        if(target == null || state == null) return;

        switch(target) {

            case "fact":
                this.factory.update(state);
                break;

            case "playerID":
                this.players.setCurrentPlayerID(Integer.parseInt(state));
                break;

            case "player":
                this.players.update(state);
                break;

            case "white":
                this.playerBoard.updateWhite(state);
                break;

            case "discount":
                this.playerBoard.updateDiscount(state);
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
                this.faithTrack.update(state);
                break;
        }
    }
}