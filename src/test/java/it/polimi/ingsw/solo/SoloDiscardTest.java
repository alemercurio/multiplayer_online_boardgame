package it.polimi.ingsw.solo;

import it.polimi.ingsw.cards.Color;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class SoloDiscardTest {

    private static class LorenzoTheStub extends LorenzoIlMagnifico
    {
        Map<Color,Integer> discarded;

        public LorenzoTheStub()
        {
            super(null,null,null,null);
            this.discarded = new HashMap<>();
        }

        @Override
        public void discard(Color color,int amount)
        {
            this.discarded.put(color,amount);
        }
    }

    private final LorenzoTheStub lorenzo = new LorenzoTheStub();
    private final Map<Color,Integer> toDiscard = new HashMap<>();
    private SoloDiscard soloDiscard;

    @Before
    public void reset() {
        lorenzo.discarded.clear();
        toDiscard.clear();
        toDiscard.put(Color.GREEN,0);
        toDiscard.put(Color.BLUE,1);
        toDiscard.put(Color.PURPLE,2);
        soloDiscard = new SoloDiscard(toDiscard);
    }

    @Test
    public void testGetDiscarded()
    {
        assertEquals(toDiscard,soloDiscard.getToDiscard());
    }

    @Test
    public void testApply()
    {
        soloDiscard.apply(lorenzo);
        assertEquals(lorenzo.discarded,soloDiscard.getToDiscard());
    }
}