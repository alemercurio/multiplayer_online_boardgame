package it.polimi.ingsw;

import it.polimi.ingsw.faith.Vatican;
import it.polimi.ingsw.supply.MarketBoard;

import java.util.*;

/**
 * Abstract class to represent a Game, which could be solo or multiplayer.
 * @see SoloGame
 * @see MultiGame
 * @author Francesco Tosini
 */
public abstract class Game {
    protected static final List<MultiGame> newGames = new LinkedList<>();

    protected final Vatican vatican;
    protected final MarketBoard market;

    public Game() {
        this.vatican = new Vatican(this,"src/main/resources/JSON/Vatican.json");
        this.market = new MarketBoard();
    }

    // Static methods

    public synchronized static boolean hasNewGame() {
        return !Game.newGames.isEmpty();
    }

    public synchronized static Game newGame(int numPlayer) {
        MultiGame game = new MultiGame(numPlayer);
        Game.newGames.add(game);
        return game;
    }

    public synchronized static Game newGame(Player creator, String nickname, int numPlayer) {
        if(numPlayer > 1) {
            MultiGame game = new MultiGame(creator, nickname, numPlayer);
            Game.newGames.add(game);
            return game;
        }
        else {
            return new SoloGame(creator,nickname);
        }
    }

    public synchronized static MultiGame join(Player player) {
        if(Game.hasNewGame()) {
            MultiGame game = Game.newGames.get(0);
            game.addPlayer(player);
            return game;
        }
        else return null;
    }

    // Non static methods

    public abstract boolean isSinglePlayer();

    public abstract void broadCast(String message);

    public abstract boolean nameAvailable(String name);

    public abstract boolean setNickname(Player player,String name);

    public abstract String getNickname(Player player);

    public abstract void start();

    public abstract void nextPlayer(Player player);

    public abstract void endGame();
}