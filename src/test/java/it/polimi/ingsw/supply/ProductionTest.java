package it.polimi.ingsw.supply;

import org.junit.Test;

import static org.junit.Assert.*;

public class ProductionTest {

    @Test
    public void testGetRequired() {
        ResourcePack input = new ResourcePack(1,2); //1 coin, 2 stones
        ResourcePack output = new ResourcePack(0,0,7); //7 servants
        Production p = new Production(input,output);

        assertEquals(p.getRequired(),input);
    }

    @Test
    public void testProduce() {
        ResourcePack input = new ResourcePack(1,2); //1 coin, 2 stones
        ResourcePack output = new ResourcePack(0,0,7); //7 servants
        Production p = new Production(input,output);

        assertEquals(p.produce(),output);
    }

    @Test
    public void testEquals() {
        String str = "Not a Production";
        ResourcePack input = new ResourcePack(1,2); //1 coin, 2 stones
        ResourcePack output = new ResourcePack(4,0,7); //4 coins, 7 servants
        ResourcePack inputWithFaith = new ResourcePack(1,2,0,0,1); //1 coin, 2 stones, 1 faithpoint
        ResourcePack outputWithFaith = new ResourcePack(4,0,7,0,2); //4 coins, 7 servants, 2 faithpoints
        Production p = new Production(input,output);
        Production anotherP = new Production(input,output);
        Production notP = new Production(output,input);
        Production pWithFaith = new Production(inputWithFaith, output);
        Production notPWithFaith = new Production(input, outputWithFaith);

        assertNotEquals(p, null);
        assertNotEquals(p, str);
        assertEquals(p, p);
        assertEquals(p, anotherP);
        assertNotEquals(p, notP);
        assertEquals(p, pWithFaith);
        assertNotEquals(p, notPWithFaith);
    }
}