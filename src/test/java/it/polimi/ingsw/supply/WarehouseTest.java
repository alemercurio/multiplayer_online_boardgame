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
        try {
            wh.stock(0, Resource.COIN, 1);
            wh.stock(1, Resource.STONE, 2);
            wh.stock(2, Resource.SERVANT, 3);
        } catch (NonConsumablePackException e) {
            fail();
        }

        wh.switchShelves(0,1);

        assertEquals("{\n\t1 {STONE:1}\n\t2 {COIN:1}\n\t3 {SERVANT:3}\n}", wh.toString());
        assertEquals("{STONE:1}", wh.getPendingView());

        wh.switchShelves(0, 2);

        assertEquals("{\n\t1 {SERVANT:1}\n\t2 {COIN:1}\n\t3 {STONE:1}\n}", wh.toString());
        assertTrue(wh.getPendingView().contains("SERVANT:2"));
        assertTrue(wh.getPendingView().contains("STONE:1"));
    }

    @Test
    public void testStock() {
        ResourcePack rp = new ResourcePack(1,3,3);
        Warehouse wh = new Warehouse();

        wh.add(rp);
        try {
            wh.stock(0, Resource.COIN, 1);
            wh.stock(1, Resource.STONE, 2);
            wh.stock(2, Resource.SERVANT, 3);
        } catch (NonConsumablePackException e) {
            fail();
        }

        assertEquals("{\n\t1 {COIN:1}\n\t2 {STONE:2}\n\t3 {SERVANT:3}\n}", wh.toString());
        assertEquals("{STONE:1}", wh.getPendingView());

        try {
            wh.stock(2, Resource.STONE, 4);
        } catch (NonConsumablePackException e) {
            fail();
        }

        assertEquals("{\n\t1 {COIN:1}\n\t2 {VOID:0}\n\t3 {STONE:3}\n}", wh.toString());
        assertEquals("{SERVANT:3}", wh.getPendingView());
    }

    @Test
    public void testGetResources() {
        ResourcePack rp = new ResourcePack(1,4,3,6,7);
        ResourcePack result = new ResourcePack(1,2,0,3);
        Warehouse wh = new Warehouse();

        wh.add(rp);
        try {
            wh.stock(0, Resource.COIN, 1);
            wh.stock(1, Resource.STONE, 2);
            wh.stock(2, Resource.SHIELD, 3);
        } catch (NonConsumablePackException e) {
            fail();
        }

        assertEquals(wh.getResources(),result);
    }

    @Test
    public void testConsume() {
        ResourcePack rp = new ResourcePack(1,2,3,4,5);
        ResourcePack cost = new ResourcePack(3,0,3,5);
        ResourcePack result = new ResourcePack(2,0,0,5);
        Warehouse wh = new Warehouse();

        wh.add(rp);
        try {
            wh.stock(0, Resource.COIN, 1);
            wh.stock(1, Resource.STONE, 2);
            wh.stock(2, Resource.SERVANT, 3);
        } catch (NonConsumablePackException e) {
            fail();
        }

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
        try {
            wh.stock(0, Resource.COIN, 1);
            wh.stock(1, Resource.STONE, 2);
            wh.stock(2, Resource.SERVANT, 3);
        } catch (NonConsumablePackException e) {
            fail();
        }

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
        assertTrue(wh.getPendingView().contains("SERVANT:3"));
        assertTrue(wh.getPendingView().contains("STONE:2"));
        assertTrue(wh.getPendingView().contains("COIN:1"));
        wh.done(); //all resources deleted
        assertEquals("{}", wh.getPendingView());
    }
}