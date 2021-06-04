package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.controller.PlayerBoard;
import it.polimi.ingsw.model.resources.MarketBoard;
import it.polimi.ingsw.model.resources.Production;
import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.model.resources.ResourcePack;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class LeaderStackTest {

    @Test
    public void testAddLeaders() {
        LeaderStack stack = new LeaderStack();

        ResourcePack rp1_cost = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_cost = new ResourcePack(1,1,1,0,0);
        ResourcePack rp3_cost = new ResourcePack(2,2,0,0,0);

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

        ResourcePack rp1_input = new ResourcePack(0,0,3,0,0);
        ResourcePack rp1_output = new ResourcePack(1,1,0,4,0);
        Production p1 = new Production(rp1_input, rp1_output);

        Power power1 = new ProductionPower(p1);
        Power power2 = new StockPower(2, Resource.SERVANT);
        Power power3 = new VoidPower(Resource.COIN);

        LeaderCard leader1 = new LeaderCard(2, rp1_cost, colorPack1, power1);
        LeaderCard leader2 = new LeaderCard(3, rp2_cost, colorPack2, power2);
        LeaderCard leader3 = new LeaderCard(1, rp3_cost, colorPack3, power3);

        List<LeaderCard> list = new LinkedList<>();
        list.add(leader1);
        list.add(leader2);
        list.add(leader3);

        stack.addLeaders(list);
        assertEquals(stack.getInactiveLeader(), list);

        LeaderCard leader4 = new LeaderCard(5, rp1_cost, colorPack3, power3);
        list.add(leader4);
        assertNotEquals(stack.getInactiveLeader(), list);

    }

    @Test
    public void testActivate() {
        MarketBoard marketBoard = new MarketBoard();
        PlayerBoard playerBoard = new PlayerBoard(null,marketBoard, null);


        LeaderStack stack = new LeaderStack();

        ResourcePack rp1_cost = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_cost = new ResourcePack(1,1,1,0,0);
        ResourcePack rp3_cost = new ResourcePack(2,2,0,0,0);

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

        ResourcePack rp1_input = new ResourcePack(0,0,3,0,0);
        ResourcePack rp1_output = new ResourcePack(1,1,0,4,0);
        Production p1 = new Production(rp1_input, rp1_output);

        Power power1 = new ProductionPower(p1);
        Power power2 = new StockPower(2, Resource.SERVANT);
        Power power3 = new VoidPower(Resource.COIN);

        LeaderCard leader1 = new LeaderCard(2, rp1_cost, colorPack1, power1);
        LeaderCard leader2 = new LeaderCard(3, rp2_cost, colorPack2, power2);
        LeaderCard leader3 = new LeaderCard(1, rp3_cost, colorPack3, power3);

        List<LeaderCard> list = new LinkedList<>();
        list.add(leader1);
        list.add(leader2);
        list.add(leader3);

        stack.addLeaders(list);


        // check with an invalid index
        stack.activate(3, playerBoard);
        assertEquals(stack.getActiveLeader().size(),0 );

        stack.activate(0, playerBoard);
        assertEquals(stack.getActiveLeader().size(), 1);
        assertEquals(stack.getActiveLeader(0), leader1);
        assertFalse(stack.getInactiveLeader().contains(leader1));
        assertEquals(stack.getInactiveLeader().size(), 2);

        stack.activate(0, playerBoard);
        assertEquals(stack.getActiveLeader().size(), 2);
        assertEquals(stack.getActiveLeader(1), leader2);
        assertFalse(stack.getInactiveLeader().contains(leader2));
        assertEquals(stack.getInactiveLeader().size(), 1);

        stack.activate(0, playerBoard);
        assertEquals(stack.getActiveLeader().size(), 3);
        assertEquals(stack.getActiveLeader(2), leader3);
        assertFalse(stack.getInactiveLeader().contains(leader3));
        assertEquals(stack.getInactiveLeader().size(), 0);
    }

    @Test
    public void testDiscard() {

        LeaderStack stack = new LeaderStack();

        ResourcePack rp1_cost = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_cost = new ResourcePack(1,1,1,0,0);
        ResourcePack rp3_cost = new ResourcePack(2,2,0,0,0);

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

        ResourcePack rp1_input = new ResourcePack(0,0,3,0,0);
        ResourcePack rp1_output = new ResourcePack(1,1,0,4,0);
        Production p1 = new Production(rp1_input, rp1_output);

        Power power1 = new ProductionPower(p1);
        Power power2 = new StockPower(2, Resource.SERVANT);
        Power power3 = new VoidPower(Resource.COIN);

        LeaderCard leader1 = new LeaderCard(2, rp1_cost, colorPack1, power1);
        LeaderCard leader2 = new LeaderCard(3, rp2_cost, colorPack2, power2);
        LeaderCard leader3 = new LeaderCard(1, rp3_cost, colorPack3, power3);

        List<LeaderCard> list = new LinkedList<>();
        list.add(leader1);
        list.add(leader2);
        list.add(leader3);

        stack.addLeaders(list);

        stack.discard(3);
        assertEquals(stack.getInactiveLeader().size(), 3);

        stack.discard(1);
        assertFalse(stack.getInactiveLeader().contains(leader2));
        assertEquals(stack.getInactiveLeader().size(), 2);

        stack.discard(1);
        assertFalse(stack.getInactiveLeader().contains(leader3));
        assertEquals(stack.getInactiveLeader().size(), 1);

        stack.discard(0);
        assertFalse(stack.getInactiveLeader().contains(leader1));
        assertEquals(stack.getInactiveLeader().size(), 0);
    }

    @Test
    public void testGetActiveLeader() {

        MarketBoard marketBoard = new MarketBoard();
        PlayerBoard playerBoard = new PlayerBoard(null, marketBoard, null);

        LeaderStack stack = new LeaderStack();

        ResourcePack rp1_cost = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_cost = new ResourcePack(1,1,1,0,0);
        ResourcePack rp3_cost = new ResourcePack(2,2,0,0,0);

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

        ResourcePack rp1_input = new ResourcePack(0,0,3,0,0);
        ResourcePack rp1_output = new ResourcePack(1,1,0,4,0);
        Production p1 = new Production(rp1_input, rp1_output);

        Power power1 = new ProductionPower(p1);
        Power power2 = new StockPower(2, Resource.SERVANT);
        Power power3 = new VoidPower(Resource.COIN);

        LeaderCard leader1 = new LeaderCard(2, rp1_cost, colorPack1, power1);
        LeaderCard leader2 = new LeaderCard(3, rp2_cost, colorPack2, power2);
        LeaderCard leader3 = new LeaderCard(1, rp3_cost, colorPack3, power3);

        List<LeaderCard> list = new LinkedList<>();
        list.add(leader1);
        list.add(leader2);
        list.add(leader3);

        stack.addLeaders(list);

        assertNull(stack.getActiveLeader(0));

        int index = 0;
        for (int i = 0; i < 3; i++)
            stack.activate(index,playerBoard);

        assertEquals(stack.getActiveLeader(0), leader1);
        assertEquals(stack.getActiveLeader(1), leader2);
        assertEquals(stack.getActiveLeader(2), leader3);
    }

    @Test
    public void testGetActiveLeaderOverload() {
        MarketBoard marketBoard = new MarketBoard();
        PlayerBoard playerBoard = new PlayerBoard(null, marketBoard, null);

        LeaderStack stack = new LeaderStack();

        ResourcePack rp1_cost = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_cost = new ResourcePack(1,1,1,0,0);
        ResourcePack rp3_cost = new ResourcePack(2,2,0,0,0);

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

        ResourcePack rp1_input = new ResourcePack(0,0,3,0,0);
        ResourcePack rp1_output = new ResourcePack(1,1,0,4,0);
        Production p1 = new Production(rp1_input, rp1_output);

        Power power1 = new ProductionPower(p1);
        Power power2 = new StockPower(2, Resource.SERVANT);
        Power power3 = new VoidPower(Resource.COIN);

        LeaderCard leader1 = new LeaderCard(2, rp1_cost, colorPack1, power1);
        LeaderCard leader2 = new LeaderCard(3, rp2_cost, colorPack2, power2);
        LeaderCard leader3 = new LeaderCard(1, rp3_cost, colorPack3, power3);

        List<LeaderCard> list = new LinkedList<>();
        list.add(leader1);
        list.add(leader2);
        list.add(leader3);

        stack.addLeaders(list);

        assertEquals(stack.getActiveLeader().size(), 0);

        int index = 0;
        for (int i = 0; i < 3; i++)
            stack.activate(index,playerBoard);

        assertEquals(stack.getActiveLeader(), list);
    }

    @Test
    public void testGetInactiveLeader() {

        LeaderStack stack = new LeaderStack();

        ResourcePack rp1_cost = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_cost = new ResourcePack(1,1,1,0,0);
        ResourcePack rp3_cost = new ResourcePack(2,2,0,0,0);

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

        ResourcePack rp1_input = new ResourcePack(0,0,3,0,0);
        ResourcePack rp1_output = new ResourcePack(1,1,0,4,0);
        Production p1 = new Production(rp1_input, rp1_output);

        Power power1 = new ProductionPower(p1);
        Power power2 = new StockPower(2, Resource.SERVANT);
        Power power3 = new VoidPower(Resource.COIN);

        LeaderCard leader1 = new LeaderCard(2, rp1_cost, colorPack1, power1);
        LeaderCard leader2 = new LeaderCard(3, rp2_cost, colorPack2, power2);
        LeaderCard leader3 = new LeaderCard(1, rp3_cost, colorPack3, power3);

        List<LeaderCard> list = new LinkedList<>();
        list.add(leader1);
        list.add(leader2);
        list.add(leader3);

        stack.addLeaders(list);

        assertNull(stack.getInactiveLeader(3));

        assertEquals(stack.getInactiveLeader(0), leader1);
        assertEquals(stack.getInactiveLeader(1), leader2);
        assertEquals(stack.getInactiveLeader(2), leader3);
    }

    @Test
    public void testGetInactiveLeaderOverload() {

        LeaderStack stack = new LeaderStack();
        assertEquals(stack.getInactiveLeader().size(), 0);

        ResourcePack rp1_cost = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_cost = new ResourcePack(1,1,1,0,0);
        ResourcePack rp3_cost = new ResourcePack(2,2,0,0,0);

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

        ResourcePack rp1_input = new ResourcePack(0,0,3,0,0);
        ResourcePack rp1_output = new ResourcePack(1,1,0,4,0);
        Production p1 = new Production(rp1_input, rp1_output);

        Power power1 = new ProductionPower(p1);
        Power power2 = new StockPower(2, Resource.SERVANT);
        Power power3 = new VoidPower(Resource.COIN);

        LeaderCard leader1 = new LeaderCard(2, rp1_cost, colorPack1, power1);
        LeaderCard leader2 = new LeaderCard(3, rp2_cost, colorPack2, power2);
        LeaderCard leader3 = new LeaderCard(1, rp3_cost, colorPack3, power3);

        List<LeaderCard> list = new LinkedList<>();
        list.add(leader1);
        list.add(leader2);
        list.add(leader3);

        stack.addLeaders(list);
        assertEquals(stack.getInactiveLeader(), list);
    }

    @Test
    public void testGetPoints() {
        MarketBoard marketBoard = new MarketBoard();
        PlayerBoard playerBoard = new PlayerBoard(null, marketBoard, null);

        LeaderStack stack = new LeaderStack();
        assertEquals(stack.getPoints(),0);

        ResourcePack rp1_cost = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_cost = new ResourcePack(1,1,1,0,0);
        ResourcePack rp3_cost = new ResourcePack(2,2,0,0,0);

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

        ResourcePack rp1_input = new ResourcePack(0,0,3,0,0);
        ResourcePack rp1_output = new ResourcePack(1,1,0,4,0);
        Production p1 = new Production(rp1_input, rp1_output);

        Power power1 = new ProductionPower(p1);
        Power power2 = new StockPower(2, Resource.SERVANT);
        Power power3 = new VoidPower(Resource.COIN);

        LeaderCard leader1 = new LeaderCard(2, rp1_cost, colorPack1, power1);
        LeaderCard leader2 = new LeaderCard(3, rp2_cost, colorPack2, power2);
        LeaderCard leader3 = new LeaderCard(1, rp3_cost, colorPack3, power3);

        List<LeaderCard> list = new LinkedList<>();
        list.add(leader1);
        list.add(leader2);
        list.add(leader3);

        stack.addLeaders(list);
        int index = 0;
        for (int i = 0; i < 3; i++)
            stack.activate(index,playerBoard);

        assertEquals(stack.getPoints(),6);
    }
}