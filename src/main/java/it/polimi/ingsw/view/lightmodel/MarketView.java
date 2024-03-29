package it.polimi.ingsw.view.lightmodel;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.cards.Color;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.util.Screen;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MarketView implements Observable {

    // Resource Market
    private Resource[][] marketTray;
    private Resource remaining;

    // Card Market
    private Map<Color,List<DevelopmentCard>> decksMap;

    // Util
    private final List<InvalidationListener> observers = new ArrayList<>();

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

        synchronized(this.observers) {
            for(InvalidationListener observer : this.observers)
                observer.invalidated(this);
        }
    }

    public void updateCardMarket(String state) {
        Type decksMapType = new TypeToken<Map<Color,List<DevelopmentCard>>>() {}.getType();
        this.decksMap = new Gson().fromJson(state,decksMapType);

        synchronized(this.observers) {
            for(InvalidationListener observer : this.observers)
                observer.invalidated(this);
        }
    }

    public Resource[][] getMarketTray() {
        Resource[][] tray = new Resource[3][4];
        for(int i = 0; i < 3;i++)
            System.arraycopy(marketTray[i], 0, tray[i], 0, 4);
        return tray;
    }

    public Resource getRemaining() {
        return remaining;
    }

    public Map<Color,List<DevelopmentCard>> getDecksMap() {
        return Collections.unmodifiableMap(decksMap);
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

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        synchronized(this.observers) {
            this.observers.add(invalidationListener);
        }
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        synchronized(this.observers) {
            this.observers.remove(invalidationListener);
        }
    }
}
