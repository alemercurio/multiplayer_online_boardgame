package it.polimi.ingsw.supply;

import org.junit.Test;

import static org.junit.Assert.*;

public class FactoryTest
{
    @Test
    public void addProductionPower() {
        ResourcePack rp1 = new ResourcePack(1);
        ResourcePack rp2 = new ResourcePack(0,1);
        ResourcePack res = new ResourcePack(1,1);

        Production p1 = new Production(rp1,rp2);
        Production p2 = new Production(rp2,rp1);

        Factory f = new Factory();
        f.addProductionPower(p1);
        f.addProductionPower(p2);
        f.setActiveProduction(0,1);

        assertTrue(f.productionRequirements().equals(res));
        assertTrue(f.productionChain().equals(res));
    }

    @Test
    public void discardProductionPower() {
        ResourcePack rp1 = new ResourcePack(1,2,3);
        ResourcePack rp2 = new ResourcePack(0,1,2);
        ResourcePack res = new ResourcePack().add(rp1).add(rp2);

        Production p1 = new Production(rp1,rp2);
        Production p2 = new Production(rp2,rp1);

        Factory f = new Factory();
        f.addProductionPower(p1);
        f.addProductionPower(p2);
        f.setActiveProduction(0,1);

        assertEquals(f.productionRequirements(),res);
        f.discardProductionPower(p1);
        assertEquals(f.productionRequirements(),rp2);
        f.discardProductionPower(0);
        assertTrue(f.productionRequirements().isEmpty());
    }

    @Test
    public void deactivateProduction() {
        ResourcePack rp = new ResourcePack(1,2,3);

        Production p = new Production(rp,rp);

        Factory f = new Factory();
        f.addProductionPower(p);

        f.setActiveProduction(0);
        assertEquals(f.productionRequirements(),rp);
        f.deactivateProduction();
        assertTrue(f.productionRequirements().isEmpty());
    }
}
