package it.polimi.ingsw.view;

import com.google.gson.Gson;
import it.polimi.ingsw.cards.DevelopmentCard;
import it.polimi.ingsw.cards.DevelopmentCardStack;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.util.ArrayList;
import java.util.List;

public class DevelopmentCardView implements Observable {

    private DevelopmentCardStack developmentCards;
    private final List<InvalidationListener> observers = new ArrayList<>();

    public DevelopmentCardView()
    {
        this.developmentCards = new DevelopmentCardStack();
    }

    public void update(String state) {
        this.developmentCards = new Gson().fromJson(state,DevelopmentCardStack.class);

        for(InvalidationListener observer : this.observers)
            observer.invalidated(this);
    }

    public void print() {

        List<DevelopmentCard> devCards = this.developmentCards.getDevCard();

        System.out.print("\n");
        for(int i = 0; i < 3; i++) {

            System.out.print("\t" + (i + 1) + ") ");

            if(devCards.get(i) == null) System.out.print("Empty.");
            else Screen.print(devCards.get(i));

            System.out.print("\n");
        }
        System.out.print("\n");

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
