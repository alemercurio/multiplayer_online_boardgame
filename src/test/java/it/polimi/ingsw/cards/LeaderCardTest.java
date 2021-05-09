package it.polimi.ingsw.cards;

import it.polimi.ingsw.supply.Production;
import it.polimi.ingsw.supply.Resource;
import it.polimi.ingsw.supply.ResourcePack;

import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class LeaderCardTest {

    @Test
    public void testGetReqResources() {
        ResourcePack rp1_cost = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_cost = new ResourcePack(1,1,1,0,0);
        ResourcePack rp3_cost = new ResourcePack(2,2,0,0,0);
        ResourcePack rp4_cost = new ResourcePack(2,0,1,0,0);
        ResourcePack discount = new ResourcePack(0,2,0,0,0);

        ColorPack colorPack1 = new ColorPack();
        colorPack1.addColor(Color.GREEN, 1);
        colorPack1.addColor(Color.GREEN, 1);
        colorPack1.addColor(Color.GREEN, 2);
        colorPack1.addColor(Color.BLUE, 1);
        colorPack1.addColor(Color.BLUE, 1);
        colorPack1.addColor(Color.BLUE, 2);
        colorPack1.addColor(Color.PURPLE, 3);

        ColorPack colorPack2 = new ColorPack();
        colorPack2.addColor(Color.PURPLE, 1);
        colorPack2.addColor(Color.GREEN, 1);

        ColorPack colorPack3 = new ColorPack();
        colorPack3.addColor(Color.PURPLE, 1);
        colorPack3.addColor(Color.BLUE, 3);

        ColorPack colorPack4 = new ColorPack();
        colorPack4.addColor(Color.PURPLE, 3);
        colorPack4.addColor(Color.GREEN, 1);

        ResourcePack rp1_input = new ResourcePack(0,0,3,0,0);
        ResourcePack rp1_output = new ResourcePack(1,1,0,4,0);
        Production p1 = new Production(rp1_input, rp1_output);

        Power power1 = new ProductionPower(p1);
        Power power2 = new StockPower(2, Resource.SERVANT);
        Power power3 = new VoidPower(Resource.COIN);
        Power power4 = new DiscountPower(discount);

        LeaderCard leader1 = new LeaderCard(2, rp1_cost, colorPack1, power1);
        LeaderCard leader2 = new LeaderCard(3, rp2_cost, colorPack2, power2);
        LeaderCard leader3 = new LeaderCard(1, rp3_cost, colorPack3, power3);
        LeaderCard leader4 = new LeaderCard(1, rp4_cost, colorPack4, power4);

        assertEquals(leader1.getReqResources(), rp1_cost);
        assertEquals(leader2.getReqResources(), rp2_cost);
        assertEquals(leader3.getReqResources(), rp3_cost);
        assertEquals(leader4.getReqResources(), rp4_cost);
    }

    @Test
    public void testGetReqDevCards() {
        ResourcePack rp1_cost = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_cost = new ResourcePack(1,1,1,0,0);
        ResourcePack rp3_cost = new ResourcePack(2,2,0,0,0);
        ResourcePack rp4_cost = new ResourcePack(2,0,1,0,0);
        ResourcePack discount = new ResourcePack(0,2,0,0,0);

        ColorPack colorPack1 = new ColorPack();
        colorPack1.addColor(Color.GREEN, 1);
        colorPack1.addColor(Color.GREEN, 1);
        colorPack1.addColor(Color.GREEN, 2);
        colorPack1.addColor(Color.BLUE, 1);
        colorPack1.addColor(Color.BLUE, 1);
        colorPack1.addColor(Color.BLUE, 2);
        colorPack1.addColor(Color.PURPLE, 3);

        ColorPack colorPack2 = new ColorPack();
        colorPack2.addColor(Color.PURPLE, 1);
        colorPack2.addColor(Color.GREEN, 1);

        ColorPack colorPack3 = new ColorPack();
        colorPack3.addColor(Color.PURPLE, 1);
        colorPack3.addColor(Color.BLUE, 3);

        ColorPack colorPack4 = new ColorPack();
        colorPack4.addColor(Color.PURPLE, 3);
        colorPack4.addColor(Color.GREEN, 1);

        ResourcePack rp1_input = new ResourcePack(0,0,3,0,0);
        ResourcePack rp1_output = new ResourcePack(1,1,0,4,0);
        Production p1 = new Production(rp1_input, rp1_output);

        Power power1 = new ProductionPower(p1);
        Power power2 = new StockPower(2, Resource.SERVANT);
        Power power3 = new VoidPower(Resource.COIN);
        Power power4 = new DiscountPower(discount);

        LeaderCard leader1 = new LeaderCard(2, rp1_cost, colorPack1, power1);
        LeaderCard leader2 = new LeaderCard(3, rp2_cost, colorPack2, power2);
        LeaderCard leader3 = new LeaderCard(1, rp3_cost, colorPack3, power3);
        LeaderCard leader4 = new LeaderCard(1, rp4_cost, colorPack4, power4);

        assertEquals(leader1.getReqDevCards(), colorPack1);
        assertEquals(leader2.getReqDevCards(), colorPack2);
        assertEquals(leader3.getReqDevCards(), colorPack3);
        assertEquals(leader4.getReqDevCards(), colorPack4);
    }

    @Test
    public void testSerialization() {
        ResourcePack reqResources = new ResourcePack(1,2,3);
        ColorPack colorPack = new ColorPack();
        colorPack.addColor(Color.GREEN,1);

        ResourcePack discount = new ResourcePack(0,1,0,2);
        Power power = new DiscountPower(discount);
        LeaderCard leaderDiscount = new LeaderCard(1,reqResources,colorPack,power);

        String leaderCard = leaderDiscount.toString();

        LeaderCard gotBack = LeaderCard.fromString(leaderCard);

        assertEquals(leaderDiscount.getPoints(),gotBack.getPoints());
        assertEquals(leaderDiscount.getReqResources(),gotBack.getReqResources());
        assertEquals(leaderDiscount.getReqDevCards(),gotBack.getReqDevCards());
    }

    @Test
    public void testGetLeaderCardDeck() {
        List<LeaderCard> deck = LeaderCard.getLeaderCardDeck("src/test/resources/JSON/LeaderCardTest.json");

        ResourcePack requiredRes1 = new ResourcePack();
        ColorPack requiredCards1 = new ColorPack();
        requiredCards1.addColor(Color.GREEN, 1);
        requiredCards1.addColor(Color.YELLOW, 1);
        ResourcePack discount1 = new ResourcePack(0,0,1);
        Power power1 = new DiscountPower(discount1);

        ResourcePack requiredRes2 = new ResourcePack(5);
        ColorPack requiredCards2 = new ColorPack();
        Power power2 = new StockPower(2, Resource.STONE);

        LeaderCard card1 = new LeaderCard(2, requiredRes1, requiredCards1, power1);
        LeaderCard card2 = new LeaderCard(3, requiredRes2, requiredCards2, power2);

        assertEquals(card1.toString(), deck.get(0).toString());
        assertEquals(card2.toString(), deck.get(1).toString());
    }
}