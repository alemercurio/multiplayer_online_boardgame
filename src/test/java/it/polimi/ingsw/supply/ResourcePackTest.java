package it.polimi.ingsw.supply;

import org.junit.Test;

import static org.junit.Assert.*;

public class ResourcePackTest {

    @Test
    public void size() {
        ResourcePack rp = new ResourcePack(3,1,4,0,5);
        assertEquals(rp.size(),8);
    }

    @Test
    public void get() {
        ResourcePack rp = new ResourcePack(1,2,3);
        assertEquals(rp.get(Resource.COIN),1);
        assertEquals(rp.get(Resource.STONE),2);
        assertEquals(rp.get(Resource.SERVANT),3);
        assertEquals(rp.get(Resource.SHIELD),0);
        assertEquals(rp.get(Resource.FAITHPOINT), 0);
        assertEquals(rp.get(Resource.VOID),0);
    }

    @Test
    public void add() {
        ResourcePack rp1 = new ResourcePack(3,1,4,0,5);
        ResourcePack rp2 = new ResourcePack(6,0,0,1,2);
        ResourcePack rp3 = new ResourcePack(9,1,4,1,7);
        ResourcePack rp4 = new ResourcePack(9,5,4,1,7);

        rp1.add(rp2);
        assertTrue(rp1.equals(rp3));

        rp1.add(Resource.STONE, 4);
        assertTrue(rp1.equals(rp4));
    }

    @Test
    public void isConsumable() {
        ResourcePack rp1 = new ResourcePack(6,1,4,0,5);
        ResourcePack rp2 = new ResourcePack(6,0,1,0,2);
        ResourcePack rp3 = new ResourcePack(6,0,1,1,2);

        assertTrue(rp1.isConsumable(rp2));
        assertFalse(rp1.isConsumable(rp3));
    }

    @Test
    public void consume() {
        ResourcePack rp1 = new ResourcePack(6,1,4,0,5);
        ResourcePack rp2 = new ResourcePack(6,0,1,0,2);
        ResourcePack rp3 = new ResourcePack(2,5,7,3,4);

        ResourcePack result1 = new ResourcePack(0,1,3,0,3);
        ResourcePack result2 = new ResourcePack(2,5,4,3,4);

        rp1.consume(rp2);
        assertTrue(rp1.equals(result1));

        rp1.consume(result1);
        assertTrue(rp1.isEmpty());

        rp3.consume(Resource.SERVANT,3);
        assertTrue(rp3.equals(result2));
    }

    @Test
    public void discountPack() {
        ResourcePack rp1 = new ResourcePack(6,1,4,0,5);
        ResourcePack rp2 = new ResourcePack(9,0,8,1,2);

        ResourcePack result = new ResourcePack(0,1,0,0,3);

        rp1.discountPack(rp2);
        assertTrue(rp1.equals(result));
    }

    @Test
    public void getRandom() {
        ResourcePack rp = new ResourcePack(6,1,4,0,5);
        ResourcePack toConsume = rp.getCopy();
        ResourcePack toBuild = new ResourcePack();

        while(!toConsume.isEmpty())
        {
            toBuild.add(toConsume.getRandom(),1);
        }

        assertEquals(rp,toBuild);
    }

    @Test
    public void consumeFaithPoints() {
        ResourcePack rp = new ResourcePack(6,0,1,1,2);

        int fp = rp.consumeFaithPoints();

        assertEquals(fp,2);
        assertEquals(rp.get(Resource.FAITHPOINT),0);
    }

    @Test
    public void flush() {
        ResourcePack rp = new ResourcePack(6,0,1,1,2);
        int a = rp.size();
        int b = rp.flush();

        assertEquals(a,b);
        assertTrue(rp.isEmpty());
    }

    @Test
    public void getCopy()
    {
        ResourcePack rp = new ResourcePack(6,0,1,1,2);
        ResourcePack copy = rp.getCopy();

        assertTrue(rp.equals(copy));

        copy.discountPack(new ResourcePack(1,2,3,4,5));

        assertFalse(rp.equals(copy));
    }
}