package it.polimi.ingsw.model.vatican;

import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.network.Player;
import it.polimi.ingsw.model.cards.LeaderCard;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class VaticanTest {

    private static class GameStub extends Game {
        boolean endSet = false;

        public GameStub() {
            super();
        }

        @Override
        public boolean isSinglePlayer() {
            return false;
        }

        @Override
        public void broadCast(String message) {
        }

        @Override
        public void nextPlayer() {
        }

        @Override
        public void endGame() {
            this.endSet = true;
        }

        @Override
        public List<LeaderCard> getLeaders() {
            return null;
        }
    }

    private final String vaticanData = "src/main/resources/JSON/Vatican.json";
    private final GameStub game = new GameStub();
    private final Vatican vatican = new Vatican(game,vaticanData);

    @Test
    public void testEndGameReport() {
        vatican.endGame();
        assertTrue(game.endSet);
    }
}