package it.polimi.ingsw.view.lightmodel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.util.Screen;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderView implements Observable {

    private LeaderStack leaders;
    private final List<InvalidationListener> observers = new ArrayList<>();

    public void update(String leaders) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Power.class,new LeaderCard.PowerReader());
        builder.enableComplexMapKeySerialization();
        Gson parser = builder.create();

        this.leaders = parser.fromJson(leaders,LeaderStack.class);

        synchronized(this.observers) {
            for(InvalidationListener observer : this.observers)
                observer.invalidated(this);
        }
    }

    // NEW

    public Map<LeaderCard,Boolean> getLeader() {
        Map<LeaderCard,Boolean> leaders = new HashMap<>();
        for(LeaderCard card : this.leaders.getActiveLeader()) {
            leaders.put(card,false);
        }
        for(LeaderCard card : this.leaders.getInactiveLeader()) {
            leaders.put(card,true);
        }
        return leaders;
    }

    public List<LeaderCard> getInactive() {
        return this.leaders.getInactiveLeader();
    }

    public List<LeaderCard> getActive() {
        return this.leaders.getActiveLeader();
    }

    // -----

    public void showChoices(List<LeaderCard> leaders) {
        this.leaders = new LeaderStack();
        this.leaders.addLeaders(leaders);

        synchronized(this.observers) {
            for(InvalidationListener observer : this.observers) {
                observer.invalidated(this);
            }
        }
    }

    public LeaderCard getCard(int index) {
        return leaders.getInactiveLeader(index);
    }

    public Image getImageForCard(LeaderCard card) {
        String url = null;
        if(card.getReqDevCards().isEmpty()) {
            for(Resource res : Resource.values()) {
                int amount = card.getReqResources().get(res);
                if (amount != 0) {
                    url = String.format("/PNG/cardfront/LeaderCardFront%s%d.png", res.getAlias(), amount);
                }
            }
        }
        else {
            StringBuilder colors = new StringBuilder();
            for(Color color : Color.values()) {
                for(int i=1; i<=3; i++) {
                    int amount = card.getReqDevCards().get(color, i);
                    if(amount>0) {
                        colors.append(color.getAlias()).append(amount);
                    }
                }
            }
            url = String.format("/PNG/cardfront/LeaderCardFront%s.png", colors);
        }
        return new Image(url);
    }

    public void printActive() {
        if(this.leaders == null) {
            Screen.printError("Leaders unavailable...");
            return;
        }

        List<LeaderCard> active = this.leaders.getActiveLeader();
        if(!active.isEmpty()) {
            System.out.println(">> Active Leaders: ");
            for(LeaderCard leader : active) {
                System.out.print("~ ");
                Screen.print(leader);
                System.out.print("\n");
            }
        } else System.out.println(">> No active Leaders :(");
    }

    public int printInactive() {

        if(this.leaders == null) {
            Screen.printError("Leaders unavailable...");
            return 0;
        }

        List<LeaderCard> inactive = this.leaders.getInactiveLeader();

        if(!inactive.isEmpty()) {
            System.out.println("\n>> These are the Leaders that are still inactive: ");
            System.out.print("\n");
            for(int i = 0; i < inactive.size(); i++) {
                System.out.print((i + 1) + ")");
                Screen.print(inactive.get(i));
                System.out.print("\n");
            }
            System.out.print("\n");
            return inactive.size();
        } else {
            System.out.println(">> No inactive Leaders :)");
            return 0;
        }
    }

    public void print() {

        if(this.leaders == null) Screen.printError("Leaders unavailable...");

        List<LeaderCard> active = this.leaders.getActiveLeader();
        if(!active.isEmpty()) {
            System.out.println(">> Active Leaders: ");
            for(LeaderCard leader : active) {
                System.out.print("~ ");
                Screen.print(leader);
                System.out.print("\n");
            }
        } else System.out.println(">> No active Leaders :(");

        List<LeaderCard> inactive = this.leaders.getInactiveLeader();
        if(!inactive.isEmpty()) {
            System.out.println(">> These are the Leaders that are still inactive: ");
            for(LeaderCard leader : inactive) {
                System.out.print("~ ");
                Screen.print(leader);
                System.out.print("\n");
            }
        } else System.out.println(">> No inactive Leaders :)");
    }

    public boolean noLeaderToPlay() {
        return this.leaders.getInactiveLeader().isEmpty();
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
