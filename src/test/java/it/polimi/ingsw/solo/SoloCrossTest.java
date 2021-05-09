package it.polimi.ingsw.solo;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SoloCrossTest {

    private static class LorenzoTheStub extends LorenzoIlMagnifico
    {
        int faithMarker;
        boolean shuffled;

        public LorenzoTheStub()
        {
            super(null, null,null,null);
            faithMarker = 0;
            shuffled = false;
        }

        @Override
        public void advance(int steps)
        {
            this.faithMarker += steps;
        }

        @Override
        public void shuffleDeck()
        {
            this.shuffled = true;
        }
    }

    private final LorenzoTheStub lorenzo = new LorenzoTheStub();
    private final SoloCross soloCrossNonShuffle = new SoloCross(2,false);
    private final SoloCross soloCrossShuffle = new SoloCross(1,true);

    @Before
    public void reset()
    {
        lorenzo.faithMarker = 0;
        lorenzo.shuffled = false;
    }

    @Test
    public void testGetFaithPoints()
    {
        assertEquals(soloCrossNonShuffle.getFaithPoints(),2);
        assertEquals(soloCrossShuffle.getFaithPoints(),1);
    }

    @Test
    public void shuffleCrossTest()
    {
        soloCrossShuffle.apply(lorenzo);
        assertEquals(lorenzo.faithMarker, soloCrossShuffle.getFaithPoints());
        assertTrue(lorenzo.shuffled);
    }

    @Test
    public void nonShuffleCrossTest()
    {
        soloCrossNonShuffle.apply(lorenzo);
        assertEquals(lorenzo.faithMarker, soloCrossNonShuffle.getFaithPoints());
        assertFalse(lorenzo.shuffled);
    }
}