package it.polimi.ingsw.supply;

import org.junit.Test;

import static org.junit.Assert.*;

public class FactoryTest
{
    @Test
    public void testSetActive() {
        ResourcePack input = new ResourcePack(1); //1 coin
        ResourcePack output = new ResourcePack(0,1); //1 stone
        Production p = new Production(input,output);
        Factory f = new Factory();

        assertTrue(f.productionRequirements().isEmpty());
        assertTrue(f.productionChain().isEmpty());

        f.addProductionPower(p);
        f.setActiveProduction(0);

        assertEquals(f.productionRequirements(), input);
        assertEquals(f.productionChain(), output);
        assertTrue(f.productionRequirements().isEmpty());
        assertTrue(f.productionChain().isEmpty());
    }

    @Test
    public void testAddProductionPower() {
        ResourcePack input1 = new ResourcePack(1); //1 coin
        ResourcePack input2 = new ResourcePack(0,1); //1 stone
        ResourcePack output1 = new ResourcePack(0,0,1,1); //1 servant, 1 shield
        ResourcePack output2 = new ResourcePack(0,0,0,3); //3 shields
        //expected
        ResourcePack requirements = new ResourcePack(1,1);
        ResourcePack products = new ResourcePack(0,0,1,4);

        Production p1 = new Production(input1,output1);
        Production p2 = new Production(input2,output2);

        Factory f = new Factory();
        f.addProductionPower(p1);
        f.addProductionPower(p2);
        f.setActiveProduction(0,1); //set both to active

        assertEquals(f.productionRequirements(), requirements);
        assertEquals(f.productionChain(), products);
    }

    @Test
    public void testDiscardProductionPower() {
        ResourcePack input1 = new ResourcePack(1); //1 coin
        ResourcePack input2 = new ResourcePack(1); //1 coin
        ResourcePack output1 = new ResourcePack(0,0,1,1); //1 servant, 1 shield
        ResourcePack output2 = new ResourcePack(0,0,1,1); //1 servant, 1 shield
        ResourcePack requirements = new ResourcePack().add(input1).add(input2);

        Production p1 = new Production(input1,output1);
        Production p2 = new Production(input2,output2);

        Factory f = new Factory();
        f.addProductionPower(p1);
        f.addProductionPower(p2);
        f.setActiveProduction(0,1); //set both to active

        assertEquals(f.productionRequirements(), requirements);
        f.discardProductionPower(p2); //discard trough Production parameter
        assertEquals(f.productionRequirements(), input1); //Productions are equals, only one is discarded
        f.discardProductionPower(0); //discard trough index
        assertTrue(f.productionRequirements().isEmpty());
    }

    @Test
    public void testDeactivateProductions() {
        Factory f = new Factory();
        ResourcePack requirements = new ResourcePack();
        ResourcePack products = new ResourcePack();
        for(int i=0; i<5; i++) {
            ResourcePack input = new ResourcePack(1, 1); //1 coin, 1 stone
            ResourcePack output = new ResourcePack(0, 0, 3); //3 servants

            requirements.add(input);
            products.add(output);

            Production p = new Production(input,output);
            f.addProductionPower(p);
        }

        f.setActiveProduction(0,1,2,3,4);
        assertEquals(f.productionRequirements(), requirements);
        f.deactivateProductions();
        assertTrue(f.productionRequirements().isEmpty()); //all 5 productions set to inactive
    }

    @Test
    public void testProductionChain() {
        Factory f = new Factory();
        ResourcePack requirements = new ResourcePack();
        ResourcePack products = new ResourcePack();
        for(int i=0; i<10; i++) {
            ResourcePack input = new ResourcePack(1); //1 coin
            ResourcePack output = new ResourcePack(0, 1, 1); //1 stone, 1 servant

            requirements.add(input);
            products.add(output);

            Production p = new Production(input,output);
            f.addProductionPower(p);
        }

        f.setActiveProduction(0,1,2,3,4,5,6,7,8,9);
        assertEquals(f.productionRequirements(), requirements);
        assertEquals(f.productionChain(), products);
    }
}

