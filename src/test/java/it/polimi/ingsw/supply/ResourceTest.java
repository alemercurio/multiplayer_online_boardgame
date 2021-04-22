package it.polimi.ingsw.supply;

import org.junit.Test;

import static org.junit.Assert.*;

public class ResourceTest {

    @Test
    public void testIsSpecial() {
        assertFalse(Resource.COIN.isSpecial());
        assertFalse(Resource.STONE.isSpecial());
        assertFalse(Resource.SERVANT.isSpecial());
        assertFalse(Resource.SHIELD.isSpecial());
        assertTrue(Resource.FAITHPOINT.isSpecial());
        assertTrue(Resource.VOID.isSpecial());
    }

    @Test
    public void testGetAlias() {
        assertEquals("Y", Resource.COIN.getAlias());
        assertEquals("G", Resource.STONE.getAlias());
        assertEquals("P", Resource.SERVANT.getAlias());
        assertEquals("B", Resource.SHIELD.getAlias());
        assertEquals("R", Resource.FAITHPOINT.getAlias());
        assertEquals("W", Resource.VOID.getAlias());
    }

    @Test
    public void testToResource() {
        assertEquals(Resource.COIN, Resource.toResource("Y"));
        assertEquals(Resource.STONE, Resource.toResource("G"));
        assertEquals(Resource.SERVANT, Resource.toResource("P"));
        assertEquals(Resource.SHIELD, Resource.toResource("B"));
        assertEquals(Resource.FAITHPOINT, Resource.toResource("R"));
        assertEquals(Resource.VOID, Resource.toResource("W"));
    }
}