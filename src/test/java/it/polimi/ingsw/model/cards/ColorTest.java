package it.polimi.ingsw.model.cards;

import org.junit.Test;

import static org.junit.Assert.*;

public class ColorTest {

    @Test
    public void testGetAlias() {
        Color color1 = Color.GREEN;
        Color color2 = Color.BLUE;
        Color color3 = Color.YELLOW;
        Color color4 = Color.PURPLE;
        assertEquals("G", color1.getAlias());
        assertEquals("B", color2.getAlias());
        assertEquals("Y", color3.getAlias());
        assertEquals("P", color4.getAlias());
    }

    @Test
    public void testGetValue() {
        Color color1 = Color.GREEN;
        Color color2 = Color.BLUE;
        Color color3 = Color.YELLOW;
        Color color4 = Color.PURPLE;
        assertEquals("137035", color1.getValue());
        assertEquals("19719F", color2.getValue());
        assertEquals("A88E1F", color3.getValue());
        assertEquals("574776", color4.getValue());
    }

    @Test
    public void testToColor() {
        assertEquals(Color.GREEN, Color.toColor("G"));
        assertEquals(Color.BLUE, Color.toColor("B"));
        assertEquals(Color.YELLOW, Color.toColor("Y"));
        assertEquals(Color.PURPLE, Color.toColor("P"));
        assertNull(Color.toColor("V"));
    }

    @Test
    public void testToColorFromValue() {
        assertEquals(Color.GREEN, Color.toColorFromValue("137035"));
        assertEquals(Color.BLUE, Color.toColorFromValue("19719F"));
        assertEquals(Color.YELLOW, Color.toColorFromValue("A88E1F"));
        assertEquals(Color.PURPLE, Color.toColorFromValue("574776"));
        assertNull(Color.toColorFromValue("Gesù è Signore e salva"));

    }
}