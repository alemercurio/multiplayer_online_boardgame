package it.polimi.ingsw.supply;

import it.polimi.ingsw.cards.StockPower;
import org.junit.Test;

import static org.junit.Assert.*;

public class StorageTest {

    @Test
    public void testGetAllResource() {
        Storage storage = new Storage();
        StockPower power1 = new StockPower(2, Resource.STONE);
        StockPower power2 = new StockPower(1, Resource.SHIELD);
        storage.warehouse.addStockPower(power1);
        storage.warehouse.addStockPower(power2);

        ResourcePack rp = new ResourcePack(5);

        storage.warehouse.stock(1, Resource.COIN, 2);
        storage.warehouse.stock(2, Resource.SHIELD, 1);
        storage.warehouse.stock(3, Resource.SERVANT, 1);
        assertEquals(2,storage.warehouse.stock(4,Resource.STONE,2));
        assertEquals(0,storage.warehouse.stock(5,Resource.STONE,1)); // Should not do anything
        storage.stockStrongbox(rp);

        assertEquals(new ResourcePack(6,2,1,1), storage.getAllResource());
    }

    @Test
    public void testStockWarehouse() {
        Storage storage = new Storage();
        ResourcePack pack = new ResourcePack(1,2,3,4,5,6);
        storage.warehouse.add(pack);

        assertEquals(10, storage.done());
    }

    @Test
    public void testStockStrongbox() {
        Storage storage = new Storage();

        ResourcePack rp = new ResourcePack(5,2,5,2,1,1);

        storage.stockStrongbox(rp);

        assertNotEquals(rp, storage.getAllResource());

        rp.flush(Resource.FAITHPOINT);
        rp.flush(Resource.VOID);

        assertEquals(rp, storage.getAllResource());
    }

    @Test
    public void testIsConsumable() {
        Storage storage = new Storage();
        StockPower power1 = new StockPower(2, Resource.STONE);
        StockPower power2 = new StockPower(1, Resource.SHIELD);
        storage.warehouse.addStockPower(power1);
        storage.warehouse.addStockPower(power2);

        ResourcePack rp = new ResourcePack(1,2,3,4);

        storage.warehouse.stock(4,Resource.STONE,2);
        storage.warehouse.stock(5,Resource.SHIELD,1);

        ResourcePack rpVoid1 = new ResourcePack(1,2,3,5,0,2);
        ResourcePack rpVoid2 = new ResourcePack(1,4,3,5,0,2);

        storage.stockStrongbox(rp);

        assertTrue(storage.isConsumable(new ResourcePack(1,2,3)));
        assertTrue(storage.isConsumable(new ResourcePack(1,4,3,5)));
        assertTrue(storage.isConsumable(rpVoid1));
        assertFalse(storage.isConsumable(new ResourcePack(1,4,4)));
        assertFalse(storage.isConsumable(new ResourcePack(1,4,3,6)));
        assertFalse(storage.isConsumable(rpVoid2));
    }

    @Test
    public void testAutoConsume() {
        Storage storage = new Storage();
        StockPower power = new StockPower(2, Resource.STONE);
        storage.warehouse.addStockPower(power);

        ResourcePack rp = new ResourcePack(5);

        storage.warehouse.stock(1, Resource.COIN, 2);
        storage.warehouse.stock(2, Resource.SHIELD, 1);
        storage.warehouse.stock(3, Resource.SERVANT, 1);
        storage.warehouse.stock(4,Resource.STONE,2);
        storage.stockStrongbox(rp);

        try {
            storage.autoConsume(new ResourcePack(8,9,10,2));
        } catch (NonConsumablePackException e) {
            assertTrue(true);
        }

        try {
            storage.autoConsume(new ResourcePack(3,1,0,1));
        } catch (NonConsumablePackException e) {
            fail();
        }

        assertEquals(new ResourcePack(3,1,1), storage.getAllResource());

        try {
            storage.autoConsume(new ResourcePack(3,1,1));
        } catch (NonConsumablePackException e) {
            fail();
        }

        assertEquals(0, storage.getAllResource().size());
    }

    @Test
    public void testConsumeWarehouse() {
        Storage storage = new Storage();

        ResourcePack toConsume = new ResourcePack(2,3,4,5);

        storage.warehouse.stock(1, Resource.COIN, 1);
        storage.warehouse.stock(2, Resource.STONE, 2);
        storage.warehouse.stock(3, Resource.SERVANT, 3);

        assertEquals(new ResourcePack(1,2,3), storage.getAllResource());
        assertEquals(new ResourcePack(1,1,1,5), storage.warehouse.consume(toConsume));
        assertTrue(storage.getAllResource().isEmpty());
    }

    @Test
    public void testConsumeStrongbox() {
        Storage storage = new Storage();

        ResourcePack stored = new ResourcePack(10,20,30,40);
        ResourcePack toConsume1 = new ResourcePack(11);
        ResourcePack toConsume2 = new ResourcePack(10,20,0,35);

        storage.stockStrongbox(stored);

        assertEquals(new ResourcePack(10,20,30,40), storage.getAllResource());

        try {
            storage.strongbox.consume(toConsume1);
        } catch (NonConsumablePackException e) {
            assertTrue(true);
        }

        try {
            storage.strongbox.consume(toConsume2);
        } catch (NonConsumablePackException e) {
            fail();
        }

        assertEquals(new ResourcePack(0,0,30,5), storage.getAllResource());
    }
}