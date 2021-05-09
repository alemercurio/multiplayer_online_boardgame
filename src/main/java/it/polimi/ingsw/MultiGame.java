package it.polimi.ingsw;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Implementation of a multiplayer Game.
 * @see Game
 * @author Francesco Tosini
 */
public class MultiGame extends Game {
    private final int numPlayer;
    private final Map<Player,String> nameTable;
    private final Queue<Player> round;
    private Player playerWithInkwell;
    private boolean endGame;

    public MultiGame(int numPlayer) {
        super();
        this.numPlayer = numPlayer;
        this.nameTable = new HashMap<>();
        this.round = new LinkedList<>();
        this.endGame = false;
    }

    public MultiGame(Player creator, String nickname, int numPlayer) {
        this(numPlayer);

        this.addPlayer(creator);
        creator.setForGame(this.vatican.getFaithTrack(),this.market);
        this.nameTable.put(creator,nickname);
        this.playerWithInkwell = creator;
    }

    @Override
    public boolean isSinglePlayer() { return false; }

    @Override
    public void broadCast(String message) {
        for(Player player : this.round) {
            player.send(message);
        }
    }

    public void broadCastFull(String message) {
        for(Player player : this.nameTable.keySet()) {
            player.send(message);
        }
    }

    public boolean nameAvailable(String name) {
        synchronized(this.nameTable) {
            for(String nameTaken : this.nameTable.values())
                if(name.equals(nameTaken)) return false;
            return true;
        }
    }

    public boolean setNickname(Player player, String name) {
        synchronized(this.nameTable) {
            if (this.nameAvailable(name)) {
                this.nameTable.put(player, name);
                this.nameTable.notifyAll();
                return true;
            } else return false;
        }
    }

    public String getNickname(Player player) {
        synchronized(this.nameTable) {
            return this.nameTable.getOrDefault(player,"unknown");
        }
    }

    public synchronized void addPlayer(Player player) {
        this.round.add(player);
        player.setForGame(this.vatican.getFaithTrack(),this.market);

        // TODO: remove
        System.out.println("(GAME) >> add Player (" + this.round.size() + "/" + this.numPlayer + ")");

        if(this.round.size() == this.numPlayer) Game.newGames.remove(this);
    }

    public void start() {
        // Ensures that every player has set its nickname.
        synchronized(this.nameTable) {
            while(this.nameTable.size() != this.numPlayer) {
                try {
                    this.nameTable.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        this.broadCast("GameStart");
        Player first = this.round.poll();
        System.out.println("(GAME) >> Turno di " + this.nameTable.get(first));
        first.setActive();
    }

    @Override
    public void nextPlayer(Player player) {
        this.round.add(player);
        Player next = this.round.peek();

        if(this.endGame && this.playerWithInkwell == next) {
            for(Player play : this.round) {
                play.setHasEnded();
                play.setActive();
            }
            System.out.println(">> game ends...");
            this.broadCast("GameEnd");
        }
        else {
            next = this.round.poll();
            System.out.println("(GAME) >> Turno di " + this.nameTable.get(next));
            next.setActive();
        }
    }

    public synchronized void endGame() {
        this.endGame = true;
    }

}
