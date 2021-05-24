package it.polimi.ingsw.view;

import it.polimi.ingsw.cards.*;
import it.polimi.ingsw.supply.Production;
import it.polimi.ingsw.supply.Resource;
import it.polimi.ingsw.supply.ResourcePack;

import java.sql.SQLOutput;

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

    public static void printError(String error)
    {
        Screen.setColor(196);
        System.out.println("\t>> " + error);
        Screen.reset();
    }

    public static void print(Resource res) {

        switch(res) {

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

    public static void print(ResourcePack resourcePack) {

        System.out.print("{ ");
        for(Resource res : Resource.values())
            if(resourcePack.get(res) != 0)
            {
                Screen.print(res);
                System.out.print("\u00d7" + resourcePack.get(res) + " ");
            }
        System.out.print("}");
    }

    public static void print(Production production) {
        Screen.print(production.getRequired());
        System.out.print(" \u2192 ");
        Screen.print(production.produce());
    }

    public static void print(Color color) {

        switch(color) {

            case GREEN:
                Screen.setColor(40);
                System.out.print("\u2663");
                break;

            case BLUE:
                Screen.setColor(69);
                System.out.print("\u2660");
                break;

            case YELLOW:
                Screen.setColor(220);
                System.out.print("\u2666");
                break;

            case PURPLE:
                Screen.setColor(135);
                System.out.print("\u2665");
                break;
        }
        Screen.reset();
    }

    public static void print(ColorPack colorPack) {

        System.out.print("{ ");

        for(int i = 1; i <= 3; i++)
            for(Color color : Color.values())
                if(colorPack.get(color,i) != 0) {
                    System.out.print("(");
                    Screen.print(color);
                    if(i != 1) System.out.print(":" + i + ")\u00d7" + colorPack.get(color,i) + " ");
                    else System.out.print(")\u00d7" + colorPack.get(color,i) + " ");
                }

        System.out.print("} ");
    }

    public static void print(Power power) {

        switch(power.getClass().getSimpleName()) {

            case "StockPower":
                StockPower stock = (StockPower) power;
                System.out.print("grants additional " + stock.getType() + " depot with " + stock.getLimit() + " spaces");
                break;

            case "ProductionPower":
                ProductionPower prod = (ProductionPower) power;
                System.out.print("gives the following production: ");
                Screen.print(prod.getProduction());
                break;

            case "VoidPower":
                VoidPower white = (VoidPower) power;
                System.out.print("makes you able to convert white marbles with " + white.getResource());
                break;

            case "DiscountPower":
                DiscountPower discount = (DiscountPower) power;
                System.out.print("gives you a discount of ");
                Screen.print(discount.getDiscount());
                System.out.print(" to buy a Development Card");
                break;
        }
    }

    public static void print(LeaderCard leader) {

        System.out.print("  PV: " + leader.getPoints());
        System.out.print(" requirements: ");

        ColorPack cp = leader.getReqDevCards();
        if(cp != null && !cp.isEmpty())
            Screen.print(leader.getReqDevCards());

        ResourcePack rp = leader.getReqResources();
        if(rp != null && !rp.isEmpty())
            Screen.print(leader.getReqResources());

        System.out.print("\n    power: ");
        Screen.print(leader.getPower());

    }

    public static void print(DevelopmentCard devCard) {

        System.out.print("{ ");
        Screen.print(devCard.getColor());
        System.out.print(" level " + devCard.getLevel() + " } cost:");
        Screen.print(devCard.getCost());
        System.out.print(" production: ");
        Screen.print(devCard.getProduction());
        System.out.print(" PV: " + devCard.getPoints());
    }
}
