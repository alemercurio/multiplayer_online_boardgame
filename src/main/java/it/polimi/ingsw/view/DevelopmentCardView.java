package it.polimi.ingsw.view;

import com.google.gson.Gson;
import it.polimi.ingsw.cards.*;
import it.polimi.ingsw.supply.MarketBoard;
import it.polimi.ingsw.supply.NoSuchDevelopmentCardException;
import javafx.scene.image.Image;

import java.util.List;

public class DevelopmentCardView {

    private DevelopmentCardStack developmentCards;

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
}
