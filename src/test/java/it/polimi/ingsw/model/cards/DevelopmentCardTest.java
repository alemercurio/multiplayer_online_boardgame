package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.resources.Production;
import it.polimi.ingsw.model.resources.ResourcePack;
import org.junit.Test;

import static org.junit.Assert.*;

public class DevelopmentCardTest {

    @Test
    public void testGetCost() {
        ResourcePack rp1_input = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_input = new ResourcePack(1,0,2,0,0);
        ResourcePack rp3_input = new ResourcePack(1,4,3,5,3);

        ResourcePack rp1_output = new ResourcePack(1,1,0,4,0);
        ResourcePack rp2_output = new ResourcePack(0,2,0,3,0);
        ResourcePack rp3_output = new ResourcePack(0,0,0,1,1);

        Production p1 = new Production(rp1_input, rp1_output);
        Production p2 = new Production(rp2_input, rp2_output);
        Production p3 = new Production(rp3_input, rp3_output);

        ResourcePack rp1_cost = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_cost = new ResourcePack(1,1,1,0,0);
        ResourcePack rp3_cost = new ResourcePack(2,2,0,0,0);

        DevelopmentCard devCard1 = new DevelopmentCard(3, rp1_cost, Color.BLUE,2, p1);
        DevelopmentCard devCard2 = new DevelopmentCard(2, rp2_cost, Color.GREEN,1, p2);
        DevelopmentCard devCard3 = new DevelopmentCard(3, rp3_cost, Color.PURPLE,3, p3);

        assertEquals( devCard1.getCost(),(rp1_cost));
        assertEquals( devCard2.getCost(),(rp2_cost));
        assertEquals( devCard3.getCost(),(rp3_cost));
    }

    @Test
    public void testGetProduction() {
        ResourcePack rp1_input = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_input = new ResourcePack(1,0,2,0,0);
        ResourcePack rp3_input = new ResourcePack(1,4,3,5,3);

        ResourcePack rp1_output = new ResourcePack(1,1,0,4,0);
        ResourcePack rp2_output = new ResourcePack(0,2,0,3,0);
        ResourcePack rp3_output = new ResourcePack(0,0,0,1,1);

        Production p1 = new Production(rp1_input, rp1_output);
        Production p2 = new Production(rp2_input, rp2_output);
        Production p3 = new Production(rp3_input, rp3_output);

        ResourcePack rp1_cost = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_cost = new ResourcePack(1,1,1,0,0);
        ResourcePack rp3_cost = new ResourcePack(2,2,0,0,0);

        DevelopmentCard devCard1 = new DevelopmentCard(3, rp1_cost, Color.BLUE,2, p1);
        DevelopmentCard devCard2 = new DevelopmentCard(2, rp2_cost, Color.GREEN,1, p2);
        DevelopmentCard devCard3 = new DevelopmentCard(3, rp3_cost, Color.PURPLE,3, p3);

        assertEquals( devCard1.getProduction(),p1);
        assertEquals( devCard2.getProduction(),p2);
        assertEquals( devCard3.getProduction(),p3);
    }

    @Test
    public void testGetColor() {
        ResourcePack rp1_input = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_input = new ResourcePack(1,0,2,0,0);
        ResourcePack rp3_input = new ResourcePack(1,4,3,5,3);

        ResourcePack rp1_output = new ResourcePack(1,1,0,4,0);
        ResourcePack rp2_output = new ResourcePack(0,2,0,3,0);
        ResourcePack rp3_output = new ResourcePack(0,0,0,1,1);

        Production p1 = new Production(rp1_input, rp1_output);
        Production p2 = new Production(rp2_input, rp2_output);
        Production p3 = new Production(rp3_input, rp3_output);

        ResourcePack rp1_cost = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_cost = new ResourcePack(1,1,1,0,0);
        ResourcePack rp3_cost = new ResourcePack(2,2,0,0,0);

        DevelopmentCard devCard1 = new DevelopmentCard(3, rp1_cost, Color.BLUE,2, p1);
        DevelopmentCard devCard2 = new DevelopmentCard(2, rp2_cost, Color.GREEN,1, p2);
        DevelopmentCard devCard3 = new DevelopmentCard(3, rp3_cost, Color.PURPLE,3, p3);

        assertEquals( devCard1.getColor(), Color.BLUE);
        assertEquals( devCard2.getColor(), Color.GREEN);
        assertEquals( devCard3.getColor(), Color.PURPLE);
    }

    @Test
    public void testGetLevel() {
        ResourcePack rp1_input = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_input = new ResourcePack(1,0,2,0,0);
        ResourcePack rp3_input = new ResourcePack(1,4,3,5,3);

        ResourcePack rp1_output = new ResourcePack(1,1,0,4,0);
        ResourcePack rp2_output = new ResourcePack(0,2,0,3,0);
        ResourcePack rp3_output = new ResourcePack(0,0,0,1,1);

        Production p1 = new Production(rp1_input, rp1_output);
        Production p2 = new Production(rp2_input, rp2_output);
        Production p3 = new Production(rp3_input, rp3_output);

        ResourcePack rp1_cost = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_cost = new ResourcePack(1,1,1,0,0);
        ResourcePack rp3_cost = new ResourcePack(2,2,0,0,0);

        DevelopmentCard devCard1 = new DevelopmentCard(3, rp1_cost, Color.BLUE,2, p1);
        DevelopmentCard devCard2 = new DevelopmentCard(2, rp2_cost, Color.GREEN,1, p2);
        DevelopmentCard devCard3 = new DevelopmentCard(3, rp3_cost, Color.PURPLE,3, p3);

        assertEquals( devCard1.getLevel(), 2);
        assertEquals( devCard2.getLevel(), 1);
        assertEquals( devCard3.getLevel(), 3);
    }

    @Test
    public void testSerialization() {
        ResourcePack cost = new ResourcePack(1,2);

        ResourcePack input = new ResourcePack(1,2);
        ResourcePack output = new ResourcePack(1,2);
        Production p = new Production(input,output);

        DevelopmentCard devCard = new DevelopmentCard(1,cost,Color.GREEN,1,p);

        String developmentCard = devCard.toString();

        DevelopmentCard gotBack = DevelopmentCard.fromString(developmentCard);

        assertEquals(devCard.getLevel(),gotBack.getLevel());
        assertEquals(devCard.getCost(),gotBack.getCost());
        assertEquals(devCard.getColor(),gotBack.getColor());
        assertEquals(devCard.getLevel(),gotBack.getLevel());
        assertEquals(devCard.getProduction(),gotBack.getProduction());
    }

    @Test
    public void testGetPathForCard() {
        ResourcePack rp1_input = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_input = new ResourcePack(1,0,2,0,0);
        ResourcePack rp3_input = new ResourcePack(1,4,3,5,3);
        ResourcePack rp4_input = new ResourcePack(0,4,0,0,0);
        ResourcePack rp5_input = new ResourcePack(11,4,3,0,3);
        ResourcePack rp6_input = new ResourcePack(2,4,3,21,3);
        ResourcePack rp7_input = new ResourcePack(4,4,3,5,0);
        ResourcePack rp8_input = new ResourcePack(1,4,2,5,3);
        ResourcePack rp9_input = new ResourcePack(5,4,5,5,3);
        ResourcePack rp10_input = new ResourcePack(1,10,3,5,3);
        ResourcePack rP11_input = new ResourcePack(1,50,100,5,3);
        ResourcePack rp12_input = new ResourcePack(1,4,3,2324,3);

        ResourcePack rp1_output = new ResourcePack(1,1,0,4,0);
        ResourcePack rp2_output = new ResourcePack(0,2,0,3,0);
        ResourcePack rp3_output = new ResourcePack(12,0,0,1,1);
        ResourcePack rp4_output = new ResourcePack(2,0,0,6,4);
        ResourcePack rp5_output = new ResourcePack(3,0,0,15,6);
        ResourcePack rp6_output = new ResourcePack(5,0,0,11,1);
        ResourcePack rp7_output = new ResourcePack(5,0,0,1,15);
        ResourcePack rp8_output = new ResourcePack(6,0,0,1,13);
        ResourcePack rp9_output = new ResourcePack(7,0,0,1,1);
        ResourcePack rp10_output = new ResourcePack(6,0,12,1,1);
        ResourcePack rp11_output = new ResourcePack(0,6,0,1,1);
        ResourcePack rp12_output = new ResourcePack(5,0,5,1,1);

        Production p1 = new Production(rp1_input, rp1_output);
        Production p2 = new Production(rp2_input, rp2_output);
        Production p3 = new Production(rp3_input, rp3_output);
        Production p4 = new Production(rp4_input, rp4_output);
        Production p5 = new Production(rp5_input, rp5_output);
        Production p6 = new Production(rp6_input, rp6_output);
        Production p7 = new Production(rp7_input, rp7_output);
        Production p8 = new Production(rp8_input, rp8_output);
        Production p9 = new Production(rp9_input, rp9_output);
        Production p10 = new Production(rp10_input, rp10_output);
        Production p11 = new Production(rP11_input, rp11_output);
        Production p12 = new Production(rp12_input, rp12_output);

        ResourcePack rp1_cost = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_cost = new ResourcePack(1,1,1,0,0);
        ResourcePack rp3_cost = new ResourcePack(7,2,0,0,0);
        ResourcePack rp4_cost = new ResourcePack(6,2,0,0,0);
        ResourcePack rp5_cost = new ResourcePack(7,3,0,5,0);
        ResourcePack rp6_cost = new ResourcePack(6,2,5,5,5);
        ResourcePack rp7_cost = new ResourcePack(1,2,0,0,0);
        ResourcePack rp8_cost = new ResourcePack(6,2,0,50,0);
        ResourcePack rp9_cost = new ResourcePack(6,2,0,0,0);
        ResourcePack rp1O_cost = new ResourcePack(2,2,0,30,0);
        ResourcePack rp11_cost = new ResourcePack(6,6,2,5,6);
        ResourcePack rp12_cost = new ResourcePack(2,2,0,7,5);

        DevelopmentCard devCard1 = new DevelopmentCard(1, rp1_cost, Color.GREEN,1, p1);
        DevelopmentCard devCard2 = new DevelopmentCard(2, rp2_cost, Color.GREEN,2, p2);
        DevelopmentCard devCard3 = new DevelopmentCard(3, rp3_cost, Color.GREEN,3, p3);
        DevelopmentCard devCard4 = new DevelopmentCard(1, rp4_cost, Color.BLUE,1, p3);
        DevelopmentCard devCard5 = new DevelopmentCard(2, rp5_cost, Color.BLUE,2, p3);
        DevelopmentCard devCard6 = new DevelopmentCard(3, rp6_cost, Color.BLUE,3, p3);
        DevelopmentCard devCard7 = new DevelopmentCard(1, rp7_cost, Color.YELLOW,1, p3);
        DevelopmentCard devCard8 = new DevelopmentCard(2, rp8_cost, Color.YELLOW,2, p3);
        DevelopmentCard devCard9 = new DevelopmentCard(3, rp9_cost, Color.YELLOW,3, p3);
        DevelopmentCard devCard10 = new DevelopmentCard(1, rp1O_cost, Color.PURPLE,1, p3);
        DevelopmentCard devCard11 = new DevelopmentCard(2, rp11_cost, Color.PURPLE,2, p3);
        DevelopmentCard devCard12 = new DevelopmentCard(3, rp12_cost, Color.PURPLE,3, p3);

        assertEquals("/PNG/cardfront/DevCardFrontG1.png", devCard1.getPathForCard());
        assertEquals("/PNG/cardfront/DevCardFrontG2.png", devCard2.getPathForCard());
        assertEquals("/PNG/cardfront/DevCardFrontG3.png", devCard3.getPathForCard());
        assertEquals("/PNG/cardfront/DevCardFrontB1.png", devCard4.getPathForCard());
        assertEquals("/PNG/cardfront/DevCardFrontB2.png", devCard5.getPathForCard());
        assertEquals("/PNG/cardfront/DevCardFrontB3.png", devCard6.getPathForCard());
        assertEquals("/PNG/cardfront/DevCardFrontY1.png", devCard7.getPathForCard());
        assertEquals("/PNG/cardfront/DevCardFrontY2.png", devCard8.getPathForCard());
        assertEquals("/PNG/cardfront/DevCardFrontY3.png", devCard9.getPathForCard());
        assertEquals("/PNG/cardfront/DevCardFrontP1.png", devCard10.getPathForCard());
        assertEquals("/PNG/cardfront/DevCardFrontP2.png", devCard11.getPathForCard());
        assertEquals("/PNG/cardfront/DevCardFrontP3.png", devCard12.getPathForCard());
    }

    @Test
    public void testGetDevelopmentCardDeck(){
        assertEquals(0, DevelopmentCard.getDevelopmentCardDeck("Gesu è Signore e salva").size());
    }
}