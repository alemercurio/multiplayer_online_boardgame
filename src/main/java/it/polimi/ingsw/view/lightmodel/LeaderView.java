package it.polimi.ingsw.view.lightmodel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.LeaderStack;
import it.polimi.ingsw.model.cards.Power;
import it.polimi.ingsw.util.Screen;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.util.ArrayList;
import java.util.List;

public class LeaderView implements Observable {

    private LeaderStack leaders;
    private final List<InvalidationListener> observers = new ArrayList<>();

    public void update(String leaders) {

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Power.class,new LeaderCard.PowerReader());
        builder.enableComplexMapKeySerialization();
        Gson parser = builder.create();

        this.leaders = parser.fromJson(leaders,LeaderStack.class);

        for(InvalidationListener observer : this.observers)
            observer.invalidated(this);
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
        this.observers.add(invalidationListener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        this.observers.remove(invalidationListener);
    }
}
