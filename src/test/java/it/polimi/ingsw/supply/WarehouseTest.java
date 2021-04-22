package it.polimi.ingsw.supply;

import org.junit.Test;

import static org.junit.Assert.*;

public class WarehouseTest {
    @Test
    public void testAdd() {
        ResourcePack rp = new ResourcePack(1,2,3);
        Warehouse wh = new Warehouse();

        wh.add(rp);
        assertEquals(wh.done(),6);
    }

    @Test
    public void testSwitchShelves() {
        ResourcePack rp = new ResourcePack(1,2,3);
        Warehouse wh = new Warehouse();

        wh.add(rp);
        //wh.stock(0,Resource.COIN,1);
        //wh.stock(1,Resource.STONE,2);
        //wh.stock(2,Resource.SERVANT,3);

        wh.switchShelves(0,1);

        assertEquals(wh.toString(),"{\n\t1 {STONE:1}\n\t2 {COIN:1}\n\t3 {SERVANT:3}\n}");
        assertEquals(wh.getPendingView(),"{STONE:1}");
    }

    @Test
    public void testStock() {
        ResourcePack rp = new ResourcePack(1,3,3);
        Warehouse wh = new Warehouse();

        wh.add(rp);
        //wh.stock(0,Resource.COIN,1);
        //wh.stock(1,Resource.STONE,2);
        //wh.stock(2,Resource.SERVANT,3);

        assertEquals(wh.toString(),"{\n\t1 {COIN:1}\n\t2 {STONE:2}\n\t3 {SERVANT:3}\n}");
        assertEquals(wh.getPendingView(),"{STONE:1}");

        //wh.stock(2,Resource.STONE,4);

        assertEquals(wh.toString(),"{\n\t1 {COIN:1}\n\t2 {VOID:0}\n\t3 {STONE:3}\n}");
        assertEquals(wh.getPendingView(),"{SERVANT:3}");
    }

    @Test
    public void testGetResources() {
        ResourcePack rp = new ResourcePack(1,4,3,6,7);
        ResourcePack result = new ResourcePack(1,2,0,3);
        Warehouse wh = new Warehouse();

        wh.add(rp);
        //wh.stock(0,Resource.COIN,1);
        //wh.stock(1,Resource.STONE,2);
        //wh.stock(2,Resource.SHIELD,3);

        assertEquals(wh.getResources(),result);
    }

    @Test
    public void testConsume() {
        ResourcePack rp = new ResourcePack(1,4,3,6,7);
        ResourcePack cost = new ResourcePack(3,0,5,3);
        ResourcePack result = new ResourcePack(2,0,5,0);
        Warehouse wh = new Warehouse();

        wh.add(rp);
        //wh.stock(0,Resource.COIN,1);
        //wh.stock(1,Resource.STONE,2);
        //wh.stock(2,Resource.SHIELD,3);

        //assertEquals(wh.consume(cost),result);
        assertEquals(wh.toString(),"{\n\t1 {VOID:0}\n\t2 {STONE:2}\n\t3 {VOID:0}\n}");
    }

    @Test
    public void testIsConsumable() {
    }

    @Test
    public void testDone() {
    }
}