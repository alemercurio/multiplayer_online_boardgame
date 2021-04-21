package it.polimi.ingsw.cards;

import it.polimi.ingsw.supply.Production;
import it.polimi.ingsw.supply.ResourcePack;
import org.junit.Test;

import static org.junit.Assert.*;

public class DevelopmentCardTest {

    @Test
    public void getCost() {
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
    public void getProduction() {
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
    public void getColor() {
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
    public void getLevel() {

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

}