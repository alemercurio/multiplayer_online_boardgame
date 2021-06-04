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

public class DevelopmentCardView implements Observable {

    private DevelopmentCardStack developmentCards;
    private final List<InvalidationListener> observers = new ArrayList<>();

    public DevelopmentCardView() {
        this.developmentCards = new DevelopmentCardStack();
    }

    public void test() {
        MarketBoard.CardMarket market = new MarketBoard.CardMarket();
        try {
            developmentCards.storeDevCard(market.getDevelopmentCard(1, Color.YELLOW), 1);
            developmentCards.storeDevCard(market.getDevelopmentCard(1, Color.GREEN), 2);
            developmentCards.storeDevCard(market.getDevelopmentCard(1, Color.PURPLE), 3);
        } catch (NoSuchDevelopmentCardException | NonPositionableCardException e) {
            e.printStackTrace();
        }
    }

    public DevelopmentCard getCard(int index) {
        return developmentCards.getDevCard(index);
    }

    public void update(String state) {
        this.developmentCards = new Gson().fromJson(state,DevelopmentCardStack.class);

        for(InvalidationListener observer : this.observers)
            observer.invalidated(this);
    }

    public Image getImageForCard(DevelopmentCard card) {
        String url = String.format("/PNG/cardfront/DevCardFront%s%d.png", card.getColor().getAlias(), card.getPoints());
        return new Image(url);
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
