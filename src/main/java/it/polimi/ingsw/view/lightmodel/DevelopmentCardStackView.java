package it.polimi.ingsw.view.lightmodel;

import com.google.gson.Gson;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.resources.MarketBoard;
import it.polimi.ingsw.model.resources.NoSuchDevelopmentCardException;
import it.polimi.ingsw.util.Screen;
import javafx.scene.image.Image;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.DevelopmentCardStack;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.util.ArrayList;
import java.util.List;

public class DevelopmentCardStackView implements Observable {

    private DevelopmentCardStack developmentCards;
    private final List<InvalidationListener> observers = new ArrayList<>();

    public DevelopmentCardStackView() {
        this.developmentCards = new DevelopmentCardStack();
    }

    public DevelopmentCardStack getCardStack() {
        return developmentCards;
    }

    public DevelopmentCard getCard(int index) {
        return developmentCards.getDevCard(index);
    }

    public void update(String state) {
        this.developmentCards = new Gson().fromJson(state,DevelopmentCardStack.class);

        synchronized(this.observers) {
            for(InvalidationListener observer : this.observers)
                observer.invalidated(this);
        }
    }

    public Image getImageForCard(DevelopmentCard card) {
        if(card!=null) {
            String url = String.format("/PNG/cardfront/DevCardFront%s%d.png", card.getColor().getAlias(), card.getPoints());
            return new Image(url);
        }
        else return null;
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
