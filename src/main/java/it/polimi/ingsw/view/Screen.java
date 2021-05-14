package it.polimi.ingsw.view;

import it.polimi.ingsw.supply.Production;
import it.polimi.ingsw.supply.Resource;
import it.polimi.ingsw.supply.ResourcePack;

public class Screen {

    private static final char[] circledNum = {'\u24ea','\u245f','\u323c','\u328d'};

    public static void setColor(int color)
    {
        System.out.print("\u001B[38;5;" + color + "m");
    }

    public static void reset()
    {
        System.out.print("\u001B[0m");
    }

    public static void printCircledNumber(int n)
    {
        if(n == 0) System.out.print(circledNum[0]);
        else if(n > 0 && n <= 20) System.out.print((char) (circledNum[1] + n));
        else if(n > 20 && n <= 35) System.out.print((char) (circledNum[2] + n));
        else if(n > 35 && n <= 50) System.out.print((char) (circledNum[3] + n));
        else System.out.print(n);
    }

    public static void print(Resource res)
    {
        switch(res)
        {
            case COIN:
                Screen.setColor(220);
                System.out.print("\u25c9");
                break;
            case STONE:
                Screen.setColor(248);
                System.out.print("\u25c9");
                break;
            case SERVANT:
                Screen.setColor(128);
                System.out.print("\u25c9");
                break;
            case SHIELD:
                Screen.setColor(27);
                System.out.print("\u25c9");
                break;
            case VOID:
                Screen.setColor(231);
                System.out.print("\u039f");
                break;
            case FAITHPOINT:
                Screen.setColor(196);
                System.out.print("\u2020");
                break;
        }
        Screen.reset();
    }

    public static void print(ResourcePack resourcePack)
    {
        System.out.print("{ ");
        for(Resource res : Resource.values())
            if(resourcePack.get(res) != 0)
            {
                Screen.print(res);
                System.out.print("\u00d7" + resourcePack.get(res) + " ");
            }
        System.out.print("}");
    }

    public static void print(Production production)
    {
        Screen.print(production.getRequired());
        System.out.print(" \u2192 ");
        Screen.print(production.produce());
    }
}
