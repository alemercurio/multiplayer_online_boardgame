package it.polimi.ingsw.supply;

import org.junit.Test;

import static org.junit.Assert.*;

public class ResourcePackTest {

    @Test
    public void testSize() {
        ResourcePack rp1 = new ResourcePack(3,1,4,0,5);
        ResourcePack rp2 = new ResourcePack(3,1,4,5,0);
        assertEquals(rp1.size(),8); //5 faith points are not counted
        assertEquals(rp2.size(),13); //5 voids are counted
    }

    @Test
    public void testGet() {
        ResourcePack rp = new ResourcePack(1,2,3,4,5,6);
        assertEquals(rp.get(Resource.COIN),1);
        assertEquals(rp.get(Resource.STONE),2);
        assertEquals(rp.get(Resource.SERVANT),3);
        assertEquals(rp.get(Resource.SHIELD),4);
        assertEquals(rp.get(Resource.FAITHPOINT), 5);
        assertEquals(rp.get(Resource.VOID),6);
    }

    @Test
    public void testAdd() {
        ResourcePack rp1 = new ResourcePack(1,1,1,1,1);
        ResourcePack rp2 = new ResourcePack(2,2,2,2,2);
        //expected
        ResourcePack rp3 = new ResourcePack(3,3,3,3,3); //1 + 2
        ResourcePack rp4 = new ResourcePack(3,7,3,3,3); //1 + 2 + 4 stones

        rp1.add(rp2);
        assertEquals(rp1, rp3);

        rp1.add(Resource.STONE, 4);
        assertEquals(rp1, rp4);
    }

    @Test
    public void testIsConsumable() {
        ResourcePack small1 = new ResourcePack(1,1,1,0,0);
        ResourcePack small2 = new ResourcePack(0,0,1,1,1);
        ResourcePack medium = new ResourcePack(1,1,1,1,1);
        ResourcePack big = new ResourcePack(2,2,2,1,1);

        assertFalse(small1.isConsumable(small2));
        assertTrue(medium.isConsumable(small1));
        assertTrue(medium.isConsumable(small2));
        assertFalse(medium.isConsumable(big));
        assertTrue(big.isConsumable(medium));
    }

    @Test
    public void testConsume() {
        ResourcePack rp1 = new ResourcePack(6,4,2,2,2);
        ResourcePack rp2 = new ResourcePack(6,2,1,1,1);
        ResourcePack rp3 = new ResourcePack(0,0,7,0,0);
        //expected
        ResourcePack result1 = new ResourcePack(0,2,1,1,1);
        ResourcePack result2 = new ResourcePack(0,0,4,0,0);

        try {
            rp1.consume(rp2);
            assertEquals(rp1, result1);
        } catch (NonConsumablePackException e) {
            fail();
        }

        try {
            rp1.consume(result1);
            assertTrue(rp1.isEmpty());
        } catch (NonConsumablePackException e) {
            fail();
        }

        try {
            rp3.consume(Resource.SERVANT, 3);
            assertEquals(rp3, result2);
        } catch (NonConsumablePackException e) {
            fail();
        }

        // Tests that the exception is correctly thrown in case of non-consumable.
        try {
            result1.consume(result2);
        } catch (NonConsumablePackException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testNonConsumable() {
        ResourcePack rp1 = new ResourcePack(6,4,2,2,2);
        ResourcePack rp2 = new ResourcePack(5,5,1,1,1);

        try {
            rp1.consume(rp2);
        } catch (NonConsumablePackException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testDiscount() {
        ResourcePack rp1 = new ResourcePack(6,1,4,0,5);
        ResourcePack rp1clone = new ResourcePack(6,1,4,0,5);
        ResourcePack rp2 = new ResourcePack(9,0,8,1,2);
        //expected
        ResourcePack result = new ResourcePack(0,1,0,0,3);

        rp1.discount(new ResourcePack());
        assertEquals(rp1, rp1clone);
        rp1.discount(rp2);
        assertEquals(rp1, result);
    }

    @Test
    public void testFlush() {
        ResourcePack rp1 = new ResourcePack(1,2,3,4,5);
        ResourcePack rp2 = new ResourcePack(1,2,3,4,5);

        int size = rp1.size();
        int flushed = rp1.flush();
        assertEquals(size, flushed);
        assertTrue(rp1.isEmpty());

        assertEquals(3, rp2.flush(Resource.SERVANT));
        assertEquals(0, rp2.get(Resource.SERVANT));
    }

    @Test
    public void testGetRandom() {
        ResourcePack rp = new ResourcePack(6, 1, 4, 0, 5);
        ResourcePack toConsume = rp.getCopy();
        ResourcePack toBuild = new ResourcePack();

        while (!toConsume.isEmpty()) {
            toBuild.add(toConsume.getRandom(), 1);
        }

        assertEquals(rp, toBuild);
    }

    @Test
    public void testIsEmpty() {
        ResourcePack empty1 = new ResourcePack();
        ResourcePack empty2 = new ResourcePack(0,0,0,0,0,0);
        ResourcePack notEmpty = new ResourcePack(1);

        assertTrue(empty1.isEmpty());
        assertTrue(empty2.isEmpty());
        assertFalse(notEmpty.isEmpty());
    }

    @Test
    public void testGetCopy() {
        ResourcePack rp = new ResourcePack(6,0,1,1,2);
        ResourcePack clone = rp.getCopy();

        assertEquals(rp, clone);

        clone.discount(new ResourcePack(1,2,3,4,5));

        assertNotEquals(rp, clone);
    }

    @Test
    public void testSerialization() {
        ResourcePack rp = new ResourcePack(1,2,3,4);

        String serialized = rp.toString();
        ResourcePack gotBack = ResourcePack.fromString(serialized);

        assertEquals(rp,gotBack);
    }
}