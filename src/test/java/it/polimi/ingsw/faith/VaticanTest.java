package it.polimi.ingsw.faith;

import it.polimi.ingsw.Game;
import org.junit.Test;

import static org.junit.Assert.*;

public class VaticanTest {

    private class GameStub extends Game
    {
        boolean endSet = false;

        public GameStub() {
            super(2);
        }

        @Override
        public void endGame() {
            this.endSet = true;
        }
    }

    private final String vaticanData = "src/main/resources/JSON/Vatican.json";
    private final GameStub game = new GameStub();
    private Vatican vatican = new Vatican(game,vaticanData);

    @Test
    public void testEndGameReport() {
        vatican.endGame();
        assertTrue(game.endSet);
    }
}