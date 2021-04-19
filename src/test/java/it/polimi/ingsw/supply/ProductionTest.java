package it.polimi.ingsw.supply;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class ProductionTest {

    @Test
    public void getRequired() {
        ResourcePack rp1 = new ResourcePack(1,2);
        ResourcePack rp2 = new ResourcePack(4,0,7);
        Production p = new Production(rp1,rp2);

        assertEquals(p.getRequired(),rp1);
        assertEquals(p.produce(),rp2);
    }

    @Test
    public void testEquals() {
        String str = "Casa";
        ResourcePack rp1 = new ResourcePack(1,2);
        ResourcePack rp2 = new ResourcePack(4,0,7);
        Production p = new Production(rp1,rp2);
        Production anotherP = new Production(rp1,rp2);

        assertFalse(p.equals(str));
        assertTrue(p.equals(p));
        assertTrue(p.equals(anotherP));
    }
}