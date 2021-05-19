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
    public void testStock() {
        ResourcePack rp = new ResourcePack(0,0,3);
        Warehouse wh = new Warehouse();

        wh.add(rp);

        wh.stock(1, Resource.COIN, 1);
        wh.stock(2, Resource.STONE, 2);
        wh.stock(3, Resource.SERVANT, 3);


        String expectedConfig = "{\"resources\":[\"COIN\",\"STONE\",\"SERVANT\"],\"amounts\":[1,2,3],\"pending\":{\"resources\":{\"SERVANT\":3}}}";
        assertEquals(expectedConfig, wh.getConfig());
    }

    @Test
    public void testMove() {
        Warehouse wh = new Warehouse();
        wh.addStockPower(new StockPower(2,Resource.SHIELD));

        wh.add(new ResourcePack(1,2,3,4));

        wh.move(1,Resource.COIN,1);
        wh.move(2,Resource.STONE,2);
        wh.move(3,Resource.SERVANT,3);

        String expectedConfig = "{\"resources\":[\"COIN\",\"STONE\",\"SERVANT\"],\"amounts\":[1,2,3,0],\"pending\":{\"resources\":{\"SHIELD\":4}}}";
        assertEquals(expectedConfig,wh.getConfig());

        wh.move(2,1,2);
        wh.done();

        expectedConfig = "{\"resources\":[\"VOID\",\"COIN\",\"SERVANT\"],\"amounts\":[0,1,3,0],\"pending\":{\"resources\":{}}}";
        assertEquals(expectedConfig,wh.getConfig());
    }

    @Test
    public void testGetResources() {
        ResourcePack rp = new ResourcePack(1,4,3,6,7);
        ResourcePack result = new ResourcePack(1,2,0,3);
        Warehouse wh = new Warehouse();

        wh.add(rp);

        wh.stock(1, Resource.COIN, 1);
        wh.stock(2, Resource.STONE, 2);
        wh.stock(3, Resource.SHIELD, 3);

        assertEquals(wh.getResources(),result);
    }

    @Test
    public void testConsume() throws NonConsumablePackException {
        ResourcePack cost = new ResourcePack(3,0,3,5);
        ResourcePack result = new ResourcePack(2,0,0,5);
        Warehouse wh = new Warehouse();

        wh.stock(1, Resource.COIN, 1);
        wh.stock(2, Resource.STONE, 2);
        wh.stock(3, Resource.SERVANT, 3);

        assertEquals(wh.consume(cost),result);
        String expectedConfig = "{\"resources\":[\"VOID\",\"STONE\",\"VOID\"],\"amounts\":[0,2,0],\"pending\":{\"resources\":{}}}";
        assertEquals(wh.getConfig(),expectedConfig);
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

        wh.stock(1, Resource.COIN, 1);
        wh.stock(2, Resource.STONE, 2);
        wh.stock(3, Resource.SERVANT, 3);

        assertTrue(wh.isConsumable(cost1));
        assertFalse(wh.isConsumable(cost2));
        assertFalse(wh.isConsumable(cost3));
        assertTrue(wh.isConsumable(cost4));
    }

    @Test
    public void testDone() {
        ResourcePack rp = new ResourcePack(1,2,3);
        Warehouse wh = new Warehouse();

        wh.add(rp); //all resources set as pending
        assertTrue(wh.getPendingResources().toString().contains("\"SERVANT\":3"));
        assertTrue(wh.getPendingResources().toString().contains("\"STONE\":2"));
        assertTrue(wh.getPendingResources().toString().contains("\"COIN\":1"));
        wh.done(); //all resources deleted
        assertEquals("{\"resources\":{}}", wh.getPendingResources().toString());
    }
}