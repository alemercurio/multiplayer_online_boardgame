package it.polimi.ingsw.view;

import com.google.gson.Gson;
import it.polimi.ingsw.cards.StockPower;
import it.polimi.ingsw.supply.Resource;
import it.polimi.ingsw.supply.ResourcePack;

import java.util.*;
import java.util.regex.Pattern;

public class View {

    public static String[] otherPlayers;
    public static FactoryView factory = new FactoryView();
    public static WarehouseView warehouse = new WarehouseView();
    public static PlayerBoardView playerBoard = new PlayerBoardView();

    // METODI DI INTERFACCIA

    public static void tell(String message)
    {
        System.out.println("\t" + message);
    }

    public static void fancyTell(String message)
    {
        System.out.println("FANCY >> " + message);
    }

    public static void showError(String error)
    {
        Screen.printError(error);
    }

    public static String selectNickname()
    {
        Scanner input = new Scanner(System.in);
        System.out.print("\tChoose a nickname! << ");
        return input.nextLine();
    }

    public static int selectNumberOfPlayer()
    {
        Scanner input = new Scanner(System.in);
        System.out.print("\tHow many players? << ");
        return input.nextInt();
    }

    public static void gameStart()
    {
        System.out.println(">> Game starts!");
        System.out.println("\tPlayers: " + Arrays.toString(View.otherPlayers));
    }

    public static void showAction(String action)
    {
        System.out.println(">> " + action);
    }

    public static String selectAction()
    {
        Scanner input = new Scanner(System.in);
        System.out.print("playing >> ");
        return input.nextLine();
    }

    public static String selectDevCard()
    {
        Scanner input = new Scanner(System.in);
        System.out.print(">> ");
        String selected = input.nextLine();

        while(!Pattern.matches("(?:(?<color>[BGPY])(?<level>[123]))|esc",selected))
        {
            View.showError("Your selection does not seem correct... please try again!");
            System.out.print(">> ");
            selected = input.nextLine();
        }

        return selected;
    }

    public static String selectDevCardPosition()
    {
        Scanner input = new Scanner(System.in);
        System.out.print(">> ");
        String position = input.nextLine();

        while(!Pattern.matches("[123]|esc",position))
        {
            View.showError("Please choose a position between 1 and 3.");
            System.out.print(">> ");
            position = input.nextLine();
        }

        return position;
    }

    public static String selectMarbles()
    {
        Scanner input = new Scanner(System.in);
        System.out.print("Do you want a row or a column? << ");
        String rowOrCol;
        String index;

        while(true)
        {
            rowOrCol = input.nextLine().toLowerCase();

            switch(rowOrCol)
            {
                case "row":
                    System.out.print("\nWhich row do you want to take? << ");
                    int rowIndex;

                    while(true)
                    {
                        index = input.nextLine();

                        if(index.equals("back")) return "back";

                        try {
                            rowIndex = Integer.parseInt(index);
                            if(rowIndex >= 1 && rowIndex < 4) return "row " + rowIndex;
                            else View.showError("Please choose a row between 1 and 3.");

                        } catch(NumberFormatException e) {
                            View.showError("Your selection is not correct, please try again!");
                        }
                    }

                case "column":
                    System.out.print("\nWhich column do you want to take? << ");
                    int columnIndex;

                    while(true)
                    {
                        index = input.nextLine();

                        if(index.equals("back")) return "back";

                        try {
                            columnIndex = Integer.parseInt(index);
                            if(columnIndex >= 1 && columnIndex < 4) return "row " + columnIndex;
                            else View.showError("Please choose a row between 1 and 3.");

                        } catch(NumberFormatException e) {
                            View.showError("Your selection is not correct, please try again!");
                        }
                    }

                case "back":
                    return "back";

                default:
                    View.showError("Please type 'row' or 'column', or 'back' to change action.");
                    break;
            }
        }
    }

    public static void showGatheredResources(ResourcePack gathered)
    {
        System.out.print(">> You gathered ");
        Screen.print(gathered);
        System.out.print("\n");
    }

    public static ResourcePack selectWhite(int amount)
    {
        System.out.println("\tIt seems you have taken some white marbles.");

        if(View.playerBoard.whitePower.size() == 1)
        {
            Resource fromWhite = View.playerBoard.whitePower.get(0);
            System.out.println("\tThey will be converted to " + fromWhite);
            return new ResourcePack().add(fromWhite,amount);
        }
        else
        {
            List<Resource> fromWhite = View.playerBoard.whitePower;

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
                    if(amount == 0) return result;
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

    public static String selectWarehouse()
    {
        String cmd;
        Scanner input = new Scanner(System.in);

        do {
            View.warehouse.print();
            System.out.print(">> ");
            cmd = input.nextLine().toUpperCase();

            Scanner read = new Scanner(cmd);

            String order = read.next();

            if(order.equals("MOVE"))
            {
                int amount = 1;
                if(read.hasNextInt()) amount = read.nextInt();

                String parameter = read.next();

                try {
                    // Resources should be moved from the pending ones.
                    Resource resource = Resource.valueOf(parameter);
                    int destination = 0; // should be overwritten

                    parameter = read.next();

                    if(parameter.equals("INTO"))
                    {
                        destination = read.nextInt();
                    }

                    if(parameter.equals("FROM")) System.out.println(">> Extraneous \"from\" option...");
                    else if(destination == 0) System.out.println(">> Missing \"into\" option...");
                    else View.warehouse.move(destination,resource,amount);
                } catch(IllegalArgumentException e) {

                    // Resources should be moved from another shelf.
                    int source = 0; // indicates pending resources
                    int destination = 0; // should be overwritten

                    if(parameter.equals("FROM"))
                    {
                        source = read.nextInt();
                        parameter = read.next();
                    }

                    if(parameter.equals("INTO"))
                    {
                        destination = read.nextInt();
                        System.out.println("><>> " + destination);
                    }

                    if(destination == 0)
                    {
                        System.out.println(">> Missing \"into\" option...");
                    }
                    else
                    {
                        if(source != 0) View.warehouse.move(destination,source,amount);
                        else System.out.println("Missing \"source\" option or a resource type...");
                    }
                } catch(InputMismatchException e) {
                    System.out.println(">> Invalid command, please try again!");
                }
            }

        } while(!cmd.equals("DONE"));

        return View.warehouse.getConfig();
    }

    public static void gameEnd()
    {
        System.out.println(">> Game has ended!");
    }

    // FINE

    public static void update(String target,String state)
    {
        if(target == null || state == null) return;

        switch(target)
        {
            case "fact":
                View.factory.update(state);
                break;

            case "player":
                Gson parser = new Gson();
                View.otherPlayers = parser.fromJson(state,String[].class);
                break;

            case "white":
                View.playerBoard.updateWhite(state);
                break;

            case "WHConfig":
                View.warehouse.update(state);
                break;

            case "WH":
                View.warehouse.addStockPower(new Gson().fromJson(state,StockPower.class));
                break;
        }
    }
}