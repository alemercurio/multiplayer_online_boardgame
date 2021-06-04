package it.polimi.ingsw.model.singleplayer;

import it.polimi.ingsw.model.cards.Color;
import it.polimi.ingsw.model.vatican.FaithTrack;
import it.polimi.ingsw.model.resources.MarketBoard;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class LorenzoIlMagnificoTest {

    private final String SANoShuffleTest = "src/test/resources/JSON/SoloActionNoShuffleTest.json";

    private class FaithStub extends FaithTrack
    {
        int steps;

        public FaithStub()
        {
            super(0,null,new ArrayList<>(), new ArrayList<>());
        }

        @Override
        public void advance(int steps) {
            this.steps = this.steps + steps;
        }
    }

    private class MarketStub extends MarketBoard
    {
        int callAmount;

        @Override
        public boolean discard(Color color, int amount) {
            callAmount++;
            return true;
        }
    }

    FaithStub ft = new FaithStub();
    MarketStub m = new MarketStub();
    SoloActionDeck soloDeck = new SoloActionDeck(SoloAction.getSoloActionDeck(SANoShuffleTest));
    LorenzoIlMagnifico lorenzo = new LorenzoIlMagnifico(null,ft,m,soloDeck);

    @Test
    public void testRunTime()
    {
        for(int i = 0; i < 6; i++) lorenzo.playSoloAction();

        assertEquals(ft.steps,6);
        assertEquals(m.callAmount, 2);
    }
}