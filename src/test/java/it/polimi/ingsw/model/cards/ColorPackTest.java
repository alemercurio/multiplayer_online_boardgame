package it.polimi.ingsw.model.cards;

import org.junit.Test;

import static org.junit.Assert.*;

public class ColorPackTest {

    @Test
    public void testAddColor() {

        ColorPack colorPack = new ColorPack();
        assertTrue(colorPack.toString().contains("{\n}"));

        colorPack.addColor(Color.GREEN, 1);
        assertTrue(colorPack.toString().contains("{GREEN,1}: 1"));

        colorPack.addColor(Color.GREEN, 1);
        assertTrue(colorPack.toString().contains("{GREEN,1}: 2"));

        colorPack.addColor(Color.GREEN, 2);
        assertTrue(colorPack.toString().contains("{GREEN,2}: 1"));

        colorPack.addColor(Color.BLUE, 1);
        assertTrue(colorPack.toString().contains("{BLUE,1}: 1"));

        colorPack.addColor(Color.BLUE, 1);
        assertTrue(colorPack.toString().contains("{BLUE,1}: 2"));

        colorPack.addColor(Color.BLUE, 2);
        assertTrue(colorPack.toString().contains("{BLUE,2}: 1"));

        colorPack.addColor(Color.PURPLE, 3);
        assertTrue(colorPack.toString().contains("{PURPLE,3}: 1"));
    }

    @Test
    public void testRequirements() {

        ColorPack colorPack = new ColorPack();
        ColorPack required = new ColorPack();
        assertTrue(colorPack.testRequirements(required));

        required.addColor(Color.BLUE, 2);
        assertFalse(colorPack.testRequirements(required));

        colorPack.addColor(Color.GREEN, 1);
        colorPack.addColor(Color.GREEN, 1);
        colorPack.addColor(Color.GREEN, 2);
        colorPack.addColor(Color.BLUE, 1);
        colorPack.addColor(Color.BLUE, 1);
        colorPack.addColor(Color.BLUE, 2);
        colorPack.addColor(Color.PURPLE, 3);

        //check if the answer is true despite the fact that colorPack has a level 3 PURPLE card and required a level 2 PURPLE card.
        required.addColor(Color.PURPLE, 2);
        assertTrue(colorPack.testRequirements(required));

        required.addColor(Color.GREEN, 1);
        required.addColor(Color.GREEN, 1);
        required.addColor(Color.GREEN, 2);
        assertTrue(colorPack.testRequirements(required));

        required.addColor(Color.BLUE,3);
        assertFalse(colorPack.testRequirements(required));

        colorPack.addColor(Color.BLUE,3);
        assertTrue(colorPack.testRequirements(required));

        required.addColor(Color.YELLOW,2);
        assertFalse(colorPack.testRequirements(required));
    }

    @Test
    public void testGetCopy() {

        ColorPack colorPack = new ColorPack();
        colorPack.addColor(Color.GREEN, 1);
        colorPack.addColor(Color.GREEN, 1);
        colorPack.addColor(Color.GREEN, 2);
        colorPack.addColor(Color.BLUE, 1);
        colorPack.addColor(Color.BLUE, 1);
        colorPack.addColor(Color.BLUE, 2);
        colorPack.addColor(Color.PURPLE, 3);

        ColorPack copy = colorPack.getCopy();
        assertEquals(colorPack, copy);

        copy.addColor(Color.BLUE,1);
        assertNotEquals(colorPack,copy);
    }

    @Test
    public void testGet() {
        ColorPack colorPack = new ColorPack();
        colorPack.addColor(Color.GREEN, 1);
        colorPack.addColor(Color.GREEN, 1);
        colorPack.addColor(Color.GREEN, 2);
        colorPack.addColor(Color.BLUE, 1);
        colorPack.addColor(Color.BLUE, 1);
        colorPack.addColor(Color.BLUE, 2);
        colorPack.addColor(Color.YELLOW, 1);
        colorPack.addColor(Color.YELLOW, 2);
        colorPack.addColor(Color.YELLOW, 3);
        colorPack.addColor(Color.YELLOW, 1);
        colorPack.addColor(Color.YELLOW, 2);
        colorPack.addColor(Color.YELLOW, 3);
        colorPack.addColor(Color.GREEN, 1);
        colorPack.addColor(Color.GREEN, 1);
        colorPack.addColor(Color.GREEN, 2);
        colorPack.addColor(Color.BLUE, 1);
        colorPack.addColor(Color.BLUE, 1);
        colorPack.addColor(Color.BLUE, 2);
        colorPack.addColor(Color.YELLOW, 1);
        colorPack.addColor(Color.YELLOW, 2);
        colorPack.addColor(Color.YELLOW, 3);
        colorPack.addColor(Color.YELLOW, 1);
        colorPack.addColor(Color.YELLOW, 2);
        colorPack.addColor(Color.GREEN, 1);
        colorPack.addColor(Color.GREEN, 1);
        colorPack.addColor(Color.GREEN, 2);
        colorPack.addColor(Color.BLUE, 1);
        colorPack.addColor(Color.BLUE, 1);
        colorPack.addColor(Color.BLUE, 2);
        colorPack.addColor(Color.YELLOW, 1);
        colorPack.addColor(Color.YELLOW, 2);
        colorPack.addColor(Color.YELLOW, 3);
        colorPack.addColor(Color.YELLOW, 1);
        colorPack.addColor(Color.YELLOW, 2);
        colorPack.addColor(Color.GREEN, 1);
        colorPack.addColor(Color.GREEN, 1);
        colorPack.addColor(Color.GREEN, 2);
        colorPack.addColor(Color.BLUE, 1);
        colorPack.addColor(Color.BLUE, 1);
        colorPack.addColor(Color.BLUE, 2);
        colorPack.addColor(Color.YELLOW, 1);
        colorPack.addColor(Color.YELLOW, 2);
        colorPack.addColor(Color.YELLOW, 3);
        colorPack.addColor(Color.YELLOW, 1);
        colorPack.addColor(Color.YELLOW, 2);
        colorPack.addColor(Color.YELLOW, 3);


        assertEquals(8,colorPack.get(Color.GREEN,1));
        assertEquals(4,colorPack.get(Color.GREEN,2));
        assertEquals(8,colorPack.get(Color.BLUE,1));
        assertEquals(4,colorPack.get(Color.BLUE,2));
        assertEquals(4,colorPack.get(Color.BLUE,2));
        assertEquals(8,colorPack.get(Color.YELLOW,1));
        assertEquals(8,colorPack.get(Color.YELLOW,2));
        assertEquals(6,colorPack.get(Color.YELLOW,3));
        assertEquals(0,colorPack.get(Color.GREEN,3));
        assertEquals(0,colorPack.get(Color.BLUE,3));
    }

    @Test
    public void testIsEmpty() {
        ColorPack colorPack = new ColorPack();
        assertTrue(colorPack.isEmpty());
        colorPack.addColor(Color.GREEN, 1);
        colorPack.addColor(Color.GREEN, 1);
        colorPack.addColor(Color.GREEN, 2);
        colorPack.addColor(Color.BLUE, 1);
        colorPack.addColor(Color.BLUE, 1);
        colorPack.addColor(Color.BLUE, 2);
        colorPack.addColor(Color.YELLOW, 1);
        colorPack.addColor(Color.YELLOW, 2);
        colorPack.addColor(Color.YELLOW, 3);
        colorPack.addColor(Color.YELLOW, 1);
        colorPack.addColor(Color.YELLOW, 2);
        colorPack.addColor(Color.YELLOW, 3);
        assertFalse(colorPack.isEmpty());
    }
}