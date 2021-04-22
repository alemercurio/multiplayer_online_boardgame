package it.polimi.ingsw.cards;

import org.junit.Test;

import static org.junit.Assert.*;

public class ColorPackTest {

    @Test
    public void testAddColor() {

        ColorPack colorPack = new ColorPack();
        assertEquals(colorPack.toString(), "{\n}");

        colorPack.addColor(Color.GREEN, 1);
        assertEquals(colorPack.toString(), "{\n\t{GREEN,1}: 1\n}");

        colorPack.addColor(Color.GREEN, 1);
        assertEquals(colorPack.toString(), "{\n\t{GREEN,1}: 2\n}");

        colorPack.addColor(Color.GREEN, 2);
        assertEquals(colorPack.toString(), "{\n\t{GREEN,1}: 2\n\t{GREEN,2}: 1\n}");

        colorPack.addColor(Color.BLUE, 1);
        assertEquals(colorPack.toString(), "{\n\t{BLUE,1}: 1\n\t{GREEN,1}: 2\n\t{GREEN,2}: 1\n}");

        colorPack.addColor(Color.BLUE, 1);
        assertEquals(colorPack.toString(), "{\n\t{BLUE,1}: 2\n\t{GREEN,1}: 2\n\t{GREEN,2}: 1\n}");

        colorPack.addColor(Color.BLUE, 2);
        assertEquals(colorPack.toString(), "{\n\t{BLUE,1}: 2\n\t{GREEN,1}: 2\n\t{GREEN,2}: 1\n\t{BLUE,2}: 1\n}");

        colorPack.addColor(Color.PURPLE, 3);
        assertEquals(colorPack.toString(), "{\n\t{BLUE,1}: 2\n\t{PURPLE,3}: 1\n\t{GREEN,1}: 2\n\t{GREEN,2}: 1\n\t{BLUE,2}: 1\n}");


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
}