package it.polimi.ingsw.cards;

import it.polimi.ingsw.supply.Resource;
import org.junit.Test;

import static org.junit.Assert.*;

public class StockPowerTest {
    @Test
    public void testGetType() {
        StockPower power1 = new StockPower(2, Resource.COIN);
        StockPower power2 = new StockPower(1, Resource.SERVANT);
        StockPower power3 = new StockPower(2, Resource.SHIELD);
        assertEquals(power1.getType(), Resource.COIN);
        assertEquals(power2.getType(), Resource.SERVANT);
        assertEquals(power3.getType(), Resource.SHIELD);
    }

    @Test
    public void testGetLimit() {
        StockPower power1 = new StockPower(2, Resource.COIN);
        StockPower power2 = new StockPower(1, Resource.SERVANT);
        StockPower power3 = new StockPower(2, Resource.SHIELD);
        assertEquals(power1.getLimit(), 2);
        assertEquals(power2.getLimit(), 1);
        assertEquals(power3.getLimit(), 2);
    }
}