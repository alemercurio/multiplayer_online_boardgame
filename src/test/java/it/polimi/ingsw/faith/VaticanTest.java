package it.polimi.ingsw.faith;

import it.polimi.ingsw.Game;
import it.polimi.ingsw.Player;
import it.polimi.ingsw.cards.LeaderCard;
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
        public boolean nameAvailable(String name) {
            return false;
        }

        @Override
        public boolean setNickname(Player player, String name) {
            return false;
        }

        @Override
        public String getNickname(Player player) {
            return null;
        }

        @Override
        public void start() {
        }

        @Override
        public void nextPlayer(Player player) {
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