package it.polimi.ingsw.view;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Scanner;

public class View {

    public static String[] otherPlayers;
    public static FactoryView factory = new FactoryView();

    // METODI DI INTERFACCIA

    public static void tell(String message)
    {
        System.out.println(">> " + message);
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
        }
    }
}