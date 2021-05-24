package it.polimi.ingsw.view;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.cards.Color;
import it.polimi.ingsw.cards.DevelopmentCard;
import it.polimi.ingsw.supply.Resource;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketView {

    // Resource Market
    private Resource[][] marketTray;
    private Resource remaining;

    // Card Market
    private Map<Color,List<DevelopmentCard>> decksMap;

    public MarketView() {

        this.marketTray = new Resource[3][4];
        for(int i = 0; i < 3; i++)
            for(int n = 0; n < 4; n++)
                this.marketTray[i][n] = Resource.VOID;

        this.remaining = Resource.VOID;
        this.decksMap = new HashMap<>();
    }

    public void updateResourceMarket(String state) {

        Gson parser = new Gson();
        JsonElement element = parser.fromJson(state,JsonElement.class);
        JsonObject jsonObj = element.getAsJsonObject();

        this.marketTray = parser.fromJson(jsonObj.get("marketTray"),Resource[][].class);
        this.remaining = parser.fromJson(jsonObj.get("remaining"),Resource.class);
    }

    public void updateCardMarket(String state) {
        Type decksMapType = new TypeToken<Map<Color,List<DevelopmentCard>>>() {}.getType();
        this.decksMap = new Gson().fromJson(state,decksMapType);
    }

    public void printResourceMarket() {

        for(int i = 0; i < this.marketTray.length; i++) {

            System.out.print("\n\t");
            for(int n = 0; n < this.marketTray[i].length; n++) {

                Screen.print(this.marketTray[i][n]);
                System.out.print(" ");
            }
            System.out.print((i + 1));
        }
        System.out.print("\t");
        Screen.print(this.remaining);
        System.out.print("\n\t");
        for(int i = 0; i < this.marketTray[0].length; i++) {

            System.out.print((i + 1) + " ");
        }
        System.out.print("\n");
    }

    public void printCardMarket() {

        for(Color color : Color.values())
        {
            Screen.print(color);
            System.out.print(" " + color + "\n");

            List<DevelopmentCard> column = this.decksMap.get(color);
            if(column == null || column.isEmpty()) System.out.println("\tEmpty.");
            else {
                for(DevelopmentCard card : column) {
                    if(card == null) System.out.println("\tEmpty.");
                    else {
                        System.out.print("\t~ ");
                        Screen.print(card);
                        System.out.print("\n");
                    }
                }
            }
        }

    }
}