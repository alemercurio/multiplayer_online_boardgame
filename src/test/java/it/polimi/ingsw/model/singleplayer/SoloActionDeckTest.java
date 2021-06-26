package it.polimi.ingsw.model.singleplayer;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SoloActionDeckTest {

    private final String SAShuffleTest = "JSON/SoloActionTest.json";
    private final String SANoShuffleTest = "JSON/SoloActionNoShuffleTest.json";
    private SoloActionDeck soloDeck;

    @Test
    public void testGetSoloActionWithShuffle_ShouldNotThrowExceptions() {
        soloDeck = new SoloActionDeck(SoloAction.getSoloActionDeck(SAShuffleTest));
        for(int i = 0; i < 6; i++) soloDeck.getSoloAction();
    }

    @Test
    public void testGetSoloActionNoShuffle_ShouldNotThrowExceptions() {
        soloDeck = new SoloActionDeck(SoloAction.getSoloActionDeck(SANoShuffleTest));
        List<SoloAction> gotBack = new ArrayList<>();
        int[] repetition = new int[3];

        for(int i = 0; i < 6; i++)
        {
            SoloAction sa = soloDeck.getSoloAction();
            if(!gotBack.contains(sa)) gotBack.add(sa);
            repetition[gotBack.indexOf(sa)]++;
        }

        for (int j : repetition) assertEquals(j, 2);
    }
}