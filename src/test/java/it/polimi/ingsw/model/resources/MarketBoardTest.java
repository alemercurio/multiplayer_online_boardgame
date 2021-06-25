package it.polimi.ingsw.model.resources;

import it.polimi.ingsw.model.cards.Color;
import it.polimi.ingsw.model.cards.DevelopmentCard;

import org.junit.Test;

import java.lang.reflect.GenericArrayType;

import static org.junit.Assert.*;

public class MarketBoardTest {

    @Test
    public void testTakeRow() {
        MarketBoard market = new MarketBoard();

        for(int j=0; j<10; j++) {

            String marketFirstRow = market.getResourceMarketView().substring(3,7);

            ResourcePack row = market.takeRow(0);

            for(Resource res : Resource.values()) {
                int current = row.get(res);
                if (current > 0) {
                    int occurrences = 0;
                    for (int i = 0; i < marketFirstRow.length(); i++) {
                        if (res.getAlias().charAt(0) == (marketFirstRow.charAt(i))) {
                            occurrences++;
                        }
                    }
                    assertEquals(0, occurrences);
                }
            }
        }
    }

    @Test
    public void testTakeColumn() {
        MarketBoard market = new MarketBoard();

        for(int j=0; j<10; j++) {

            char mfc1 = market.getResourceMarketView().charAt(3);
            char mfc2 = market.getResourceMarketView().charAt(9);
            char mfc3 = market.getResourceMarketView().charAt(15);
            String marketFirstColumn = "" + mfc1 + mfc2 + mfc3;

            ResourcePack column = market.takeColumn(0);

            for(Resource res : Resource.values()) {
                int current = column.get(res);
                if (current > 0) {
                    int occurrences = 0;
                    for (int i = 0; i < marketFirstColumn.length(); i++) {
                        if (res.getAlias().charAt(0) == (marketFirstColumn.charAt(i))) {
                            occurrences++;
                        }
                    }
                    assertEquals(0, occurrences);
                }
            }
        }
    }

    @Test
    public void testGetCost() {
        MarketBoard market = new MarketBoard();

        for(int i=0; i<4; i++) {
            for (int level = 1; level < 4; level++) {
                for (Color color : Color.values()) {
                    try {
                        ResourcePack cost = market.getCost(level, color);
                        DevelopmentCard card = market.buyDevelopmentCard(level, color);

                        assertEquals(cost, card.getCost());
                    } catch (NoSuchDevelopmentCardException e) {
                        fail();
                    }
                }
            }
        }

        try {
            market.getCost(1, Color.GREEN);
        } catch (NoSuchDevelopmentCardException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testGetDevelopmentCard() {
        MarketBoard market = new MarketBoard();

        try {
            DevelopmentCard greenCard1 = market.buyDevelopmentCard(1, Color.GREEN);
            DevelopmentCard greenCard2 = market.buyDevelopmentCard(1, Color.GREEN);
            DevelopmentCard blueCard = market.buyDevelopmentCard(2, Color.BLUE);
            DevelopmentCard yellowCard = market.buyDevelopmentCard(2, Color.YELLOW);
            DevelopmentCard purpleCard = market.buyDevelopmentCard(3, Color.PURPLE);

            assertEquals(Color.GREEN, greenCard1.getColor());
            assertEquals(Color.GREEN, greenCard2.getColor());
            assertEquals(Color.BLUE, blueCard.getColor());
            assertEquals(Color.YELLOW, yellowCard.getColor());
            assertEquals(Color.PURPLE, purpleCard.getColor());

            assertEquals(1, greenCard1.getLevel());
            assertEquals(1, greenCard2.getLevel());
            assertEquals(2, blueCard.getLevel());
            assertEquals(2, yellowCard.getLevel());
            assertEquals(3, purpleCard.getLevel());
            market.discard(Color.GREEN,1);
            assertNotNull(market.getDevelopmentCard(1,Color.GREEN));

        } catch (NoSuchDevelopmentCardException e) {
            fail();
        }
    }
}