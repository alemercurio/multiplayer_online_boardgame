package it.polimi.ingsw.supply;

import it.polimi.ingsw.cards.StockPower;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class StorageTest {

    @Test
    public void testAddStockPower() {
        Storage storage = new Storage();
        StockPower power1 = new StockPower(2, Resource.STONE);
        storage.addStockPower(power1);

        assertTrue(storage.getStockPower().contains(power1));

        StockPower power2 = new StockPower(1, Resource.STONE);
        StockPower power3 = new StockPower(3, Resource.SERVANT);
        storage.addStockPower(power2);
        storage.addStockPower(power3);

        LinkedList<StockPower> listOfPowersCorrect = new LinkedList<>();
        listOfPowersCorrect.add(power1);
        listOfPowersCorrect.add(power2);
        listOfPowersCorrect.add(power3);

        LinkedList<StockPower> listOfPowersWrong = new LinkedList<>();
        listOfPowersWrong.add(new StockPower(3, Resource.STONE));
        listOfPowersWrong.add(power3);

        assertEquals(listOfPowersCorrect, storage.getStockPower());
        assertNotEquals(listOfPowersWrong, storage.getStockPower());
    }

    @Test
    public void testGetLeaderStockLimit() {
        Storage storage = new Storage();
        StockPower power1 = new StockPower(2, Resource.STONE);
        StockPower power2 = new StockPower(1, Resource.STONE);
        StockPower power3 = new StockPower(3, Resource.SERVANT);
        StockPower power4 = new StockPower(2, Resource.SHIELD);
        storage.addStockPower(power1);
        storage.addStockPower(power2);
        storage.addStockPower(power3);
        storage.addStockPower(power4);

        ResourcePack pack = new ResourcePack(0,3,3,2);

        assertEquals(pack, storage.getLeaderStockLimit());
        assertEquals(3, storage.getLeaderStockLimit(Resource.SERVANT));
        assertEquals(3, storage.getLeaderStockLimit(Resource.STONE));
        assertEquals(0, storage.getLeaderStockLimit(Resource.COIN));
    }

    @Test
    public void testGetAllResource() {
        Storage storage = new Storage();
        StockPower power1 = new StockPower(2, Resource.STONE);
        StockPower power2 = new StockPower(1, Resource.SHIELD);
        storage.addStockPower(power1);
        storage.addStockPower(power2);

        ResourcePack rp1 = new ResourcePack(5,5,5,0,0);
        ResourcePack rp2 = new ResourcePack(2,2,2,0,0);
        ResourcePack rp3 = new ResourcePack(5);
        ResourcePack all = rp1.getCopy().add(rp2).add(rp3);

        storage.stockWarehouse(rp1);
        ResourcePack remaining = storage.stockLeaderStock(rp2);
        storage.stockStrongbox(rp3);

        assertNotEquals(all, storage.getAllResource());

        try {
            all.consume(rp1); //resources are pending in the warehouse, not stocked
            all.consume(remaining);
        } catch (NonConsumablePackException e) {
            fail();
        }
        assertEquals(all, storage.getAllResource());
    }

    @Test
    public void testStockWarehouse() {
        Storage storage = new Storage();
        ResourcePack pack = new ResourcePack(1,2,3,4,5,6);
        storage.stockWarehouse(pack);

        assertEquals(10, storage.done());
    }

    @Test
    public void testStockLeaderStock() {
        Storage storage = new Storage();
        StockPower power1 = new StockPower(2, Resource.STONE);
        StockPower power2 = new StockPower(1, Resource.SHIELD);
        storage.addStockPower(power1);
        storage.addStockPower(power2);

        ResourcePack rp = new ResourcePack(5,2,5,2,1,1);

        ResourcePack remaining2 = storage.stockLeaderStock(rp);
        assertEquals(new ResourcePack(0,2,0,1), storage.getAllResource());
        assertEquals(new ResourcePack(5,0,5,1,1,1), remaining2);
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
        storage.addStockPower(power1);
        storage.addStockPower(power2);

        ResourcePack rp1 = new ResourcePack(0,2,0,1);
        ResourcePack rp2 = new ResourcePack(1,2,3,4);

        storage.stockLeaderStock(rp1);
        storage.stockStrongbox(rp2);

        assertTrue(storage.isConsumable(new ResourcePack(1,2,3)));
        assertTrue(storage.isConsumable(new ResourcePack(1,4,3,5)));
        assertFalse(storage.isConsumable(new ResourcePack(1,4,4)));
        assertFalse(storage.isConsumable(new ResourcePack(1,4,3,6)));
    }

    @Test
    public void testAutoConsume() {
    }

    @Test
    public void testConsumeWarehouse() {
    }

    @Test
    public void testConsumeLeaderStock() {
    }

    @Test
    public void testConsumeStrongbox() {
    }
}