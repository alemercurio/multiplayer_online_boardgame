package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.MultiGame;
import it.polimi.ingsw.controller.PlayerBoard;
import it.polimi.ingsw.model.resources.MarketBoard;
import it.polimi.ingsw.model.resources.Production;
import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.model.resources.ResourcePack;
import it.polimi.ingsw.model.vatican.FaithTrack;
import it.polimi.ingsw.model.vatican.Vatican;
import it.polimi.ingsw.network.Player;
import it.polimi.ingsw.network.Server;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        String leaderState = stack.toString();
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
        assertNotEquals(leaderState, stack.toString());
        assertEquals(stack.getInactiveLeader(), list);
    }
}