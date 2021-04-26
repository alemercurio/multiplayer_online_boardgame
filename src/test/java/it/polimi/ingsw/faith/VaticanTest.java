package it.polimi.ingsw.faith;

import it.polimi.ingsw.Game;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class VaticanTest {

    private class GameStub extends Game
    {
        boolean endSet = false;

        public GameStub() {
            super(false);
        }

        @Override
        public void endGame() {
            this.endSet = true;
        }
    }

    private final String vaticanData = "src/main/resources/JSON/Vatican.json";
    private Vatican vatican;
    private final GameStub game = new GameStub();

    @Before
    public void setUp()
    {
        vatican = new Vatican(game,vaticanData);
        vatican.start();
        game.endSet = false;
    }

    @Test
    public void testEndGameReport() throws InterruptedException {
        vatican.report("endGame");
        Thread.sleep(10);
        assertTrue(game.endSet);
    }

    @Test
    public void testStopReport() throws InterruptedException {
        vatican.report("stop");
        Thread.sleep(10);
        assertFalse(vatican.isAlive());
    }

    @After
    public void stop()
    {
        vatican.report("stop");
    }
}