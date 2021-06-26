package it.polimi.ingsw.controller;

import it.polimi.ingsw.network.Player;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.vatican.Vatican;
import it.polimi.ingsw.model.resources.MarketBoard;
import it.polimi.ingsw.model.resources.ResourcePack;

import java.util.*;

/**
 * Abstract class to represent a Game, which could be singleplayer or multiplayer.
 * @see SoloGame
 * @see MultiGame
 * @author Francesco Tosini
 */
public abstract class Game {
    protected static final List<MultiGame> newGames = new LinkedList<>();
    protected static final List<Player> waitingRoom = new LinkedList<>();

    protected final Vatican vatican;
    public final MarketBoard market;
    private static final List<LeaderCard> leaders = LeaderCard.getLeaderCardDeck("JSON/LeaderCard.json");

    public Game() {
        this.vatican = new Vatican(this,"JSON/Vatican.json");
        this.market = new MarketBoard(this);
    }

    // Static methods

    public synchronized static boolean hasNewGame() {
        return !Game.newGames.isEmpty();
    }

    public synchronized static Game newGame(Player creator, String nickname, int numPlayer) {
        if(numPlayer > 1) {
            MultiGame game = new MultiGame(creator,numPlayer);
            int playerCount = 1;
            while(!Game.waitingRoom.isEmpty() && playerCount < numPlayer) {
                Player otherPlayer = Game.waitingRoom.remove(0);
                otherPlayer.hasJoined(game);
                game.addPlayer(otherPlayer);
                playerCount++;
            }
            if(playerCount < numPlayer) Game.newGames.add(game);
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
        } else {
            if(Game.waitingRoom.size() == 3) {
                MultiGame game = new MultiGame(4);
                for(int i = 0; i < 3; i++) {
                    Player otherPlayer = Game.waitingRoom.remove(0);
                    otherPlayer.hasJoined(game);
                    game.addPlayer(otherPlayer);
                }
                game.addPlayer(player);
                return game;
            } else {
                Game.waitingRoom.add(player);
                return null;
            }
        }
    }

    // Non static methods

    public abstract boolean isSinglePlayer();

    public abstract void broadCast(String message);

    public void broadCastFull(String message) { }

    public abstract void nextPlayer();

    public abstract void endGame();

    // NEW

    public void waitForOtherPlayer() { }
    public static List<LeaderCard> getLeaderDeck() { return new ArrayList<LeaderCard>(Game.leaders); }
    public abstract List<LeaderCard> getLeaders();
    public ResourcePack getAdvantage(Player player) { return new ResourcePack(); }

    public void isAlive(Player player) { };
    public void hasDisconnected(Player player) { };
    public boolean resumePlayer(Player player) { return false; }
}