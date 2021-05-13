package it.polimi.ingsw.supply;

import it.polimi.ingsw.cards.StockPower;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class WarehouseTest {

    @Test
    public void testAddStockPower() {
        Warehouse wh = new Warehouse();
        StockPower power1 = new StockPower(2, Resource.STONE);
        wh.addStockPower(power1);

        assertTrue(wh.getStockPower().contains(power1));

        StockPower power2 = new StockPower(1, Resource.STONE);
        StockPower power3 = new StockPower(3, Resource.SERVANT);
        wh.addStockPower(power2);
        wh.addStockPower(power3);

        LinkedList<StockPower> listOfPowersCorrect = new LinkedList<>();
        listOfPowersCorrect.add(power1);
        listOfPowersCorrect.add(power2);
        listOfPowersCorrect.add(power3);

        LinkedList<StockPower> listOfPowersWrong = new LinkedList<>();
        listOfPowersWrong.add(new StockPower(3, Resource.STONE));
        listOfPowersWrong.add(power3);

        assertEquals(listOfPowersCorrect, wh.getStockPower());
        assertNotEquals(listOfPowersWrong, wh.getStockPower());
    }

    @Test
    public void testGetLeaderStockLimit() {
        Warehouse wh = new Warehouse();
        StockPower power1 = new StockPower(2, Resource.STONE);
        StockPower power2 = new StockPower(1, Resource.STONE);
        StockPower power3 = new StockPower(3, Resource.SERVANT);
        StockPower power4 = new StockPower(2, Resource.SHIELD);
        wh.addStockPower(power1);
        wh.addStockPower(power2);
        wh.addStockPower(power3);
        wh.addStockPower(power4);

        ResourcePack pack = new ResourcePack(0,3,3,2);

        assertEquals(pack, wh.getLeaderStockLimit());
        assertEquals(3, wh.getLeaderStockLimit(Resource.SERVANT));
        assertEquals(3, wh.getLeaderStockLimit(Resource.STONE));
        assertEquals(0, wh.getLeaderStockLimit(Resource.COIN));
    }

    @Test
    public void testAdd() {
        ResourcePack rp = new ResourcePack(1,2,3);
        Warehouse wh = new Warehouse();

        wh.add(rp);
        assertEquals(wh.done(),6);
    }

    @Test
    public void testGetPendingResources() {
        ResourcePack rp = new ResourcePack(1,2,3);
        Warehouse wh = new Warehouse();

        wh.add(rp);
        assertEquals(rp, wh.getPendingResources());
    }

    @Test
    public void testSwitchShelves() {
        ResourcePack rp = new ResourcePack(1,2,3);
        Warehouse wh = new Warehouse();

        wh.add(rp);

        wh.stock(0, Resource.COIN, 1);
        wh.stock(1, Resource.STONE, 2);
        wh.stock(2, Resource.SERVANT, 3);

        wh.switchShelves(0,1);

        assertEquals("{\n\t1 {STONE:1}\n\t2 {COIN:1}\n\t3 {SERVANT:3}\n}", wh.toString());
        assertEquals("{\"resources\":{\"STONE\":1}}", wh.getPendingView());

        wh.switchShelves(0, 2);

        assertEquals("{\n\t1 {SERVANT:1}\n\t2 {COIN:1}\n\t3 {STONE:1}\n}", wh.toString());
        assertTrue(wh.getPendingView().contains("\"SERVANT\":2"));
        assertTrue(wh.getPendingView().contains("\"STONE\":1"));
    }

    @Test
    public void testStock() {
        ResourcePack rp = new ResourcePack(1,3,3);
        Warehouse wh = new Warehouse();

        wh.add(rp);

        wh.stock(0, Resource.COIN, 1);
        wh.stock(1, Resource.STONE, 2);
        wh.stock(2, Resource.SERVANT, 3);

        assertEquals("{\n\t1 {COIN:1}\n\t2 {STONE:2}\n\t3 {SERVANT:3}\n}", wh.toString());
        assertEquals("{\"resources\":{\"STONE\":1}}", wh.getPendingView());


        wh.stock(2, Resource.STONE, 4);

        assertEquals("{\n\t1 {COIN:1}\n\t2 {VOID:0}\n\t3 {STONE:3}\n}", wh.toString());
        assertEquals("{\"resources\":{\"SERVANT\":3}}", wh.getPendingView());
    }

    @Test
    public void testGetResources() {
        ResourcePack rp = new ResourcePack(1,4,3,6,7);
        ResourcePack result = new ResourcePack(1,2,0,3);
        Warehouse wh = new Warehouse();

        wh.add(rp);

        wh.stock(0, Resource.COIN, 1);
        wh.stock(1, Resource.STONE, 2);
        wh.stock(2, Resource.SHIELD, 3);

        assertEquals(wh.getResources(),result);
    }

    @Test
    public void testConsume() {
        ResourcePack rp = new ResourcePack(1,2,3,4,5);
        ResourcePack cost = new ResourcePack(3,0,3,5);
        ResourcePack result = new ResourcePack(2,0,0,5);
        Warehouse wh = new Warehouse();

        wh.add(rp);

        wh.stock(0, Resource.COIN, 1);
        wh.stock(1, Resource.STONE, 2);
        wh.stock(2, Resource.SERVANT, 3);

        assertEquals(wh.consume(cost), result);
        assertEquals(wh.toString(),"{\n\t1 {VOID:0}\n\t2 {STONE:2}\n\t3 {VOID:0}\n}");
    }

    @Test
    public void testIsConsumable() {
        ResourcePack rp = new ResourcePack(1,2,3,0,0);
        ResourcePack cost1 = new ResourcePack(1,1,2);
        ResourcePack cost2 = new ResourcePack(1,0,5);
        ResourcePack cost3 = new ResourcePack(1,1,2,4);
        ResourcePack cost4 = new ResourcePack(1,1,2,0,1,2); //with special resources
        Warehouse wh = new Warehouse();

        wh.add(rp);

        wh.stock(0, Resource.COIN, 1);
        wh.stock(1, Resource.STONE, 2);
        wh.stock(2, Resource.SERVANT, 3);

        assertTrue(wh.isConsumable(cost1));
        assertFalse(wh.isConsumable(cost2));
        assertFalse(wh.isConsumable(cost3));
        assertTrue(wh.isConsumable(cost4));
    }

    @Test
    public void testWastedIfDone() {
        ResourcePack rp = new ResourcePack(1,2,3);
        Warehouse wh = new Warehouse();

        wh.add(rp);
        assertEquals(6, wh.wastedIfDone());
    }

    @Test
    public void testDone() {
        ResourcePack rp = new ResourcePack(1,2,3);
        Warehouse wh = new Warehouse();

        wh.add(rp); //all resources set as pending
        assertTrue(wh.getPendingView().contains("\"SERVANT\":3"));
        assertTrue(wh.getPendingView().contains("\"STONE\":2"));
        assertTrue(wh.getPendingView().contains("\"COIN\":1"));
        wh.done(); //all resources deleted
        assertEquals("{\"resources\":{}}", wh.getPendingView());
    }
}