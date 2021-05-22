package it.polimi.ingsw;

import com.google.gson.Gson;
import it.polimi.ingsw.cards.LeaderCard;
import it.polimi.ingsw.util.MessageParser;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of a multiplayer Game.
 * @see Game
 * @author Francesco Tosini
 */
public class MultiGame extends Game {

    private final int numPlayer;
    private final Map<Player,String> nameTable;
    private final LinkedList<Player> round;
    private final AtomicInteger readyPlayers;
    private Player playerWithInkwell;
    private List<LeaderCard> leaders;
    private boolean endGame;

    public MultiGame(int numPlayer) {
        super();
        this.numPlayer = numPlayer;
        this.nameTable = new HashMap<>();
        this.readyPlayers = new AtomicInteger(0);
        this.round = new LinkedList<>();

        this.leaders = Game.getLeaderDeck();
        Collections.shuffle(this.leaders);
        this.endGame = false;
    }

    public MultiGame(Player creator, String nickname, int numPlayer) {
        this(numPlayer);

        this.addPlayer(creator);
        this.nameTable.put(creator,nickname);
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
        if(this.round.size() == this.numPlayer) Game.newGames.remove(this);

        String otherPlayer = new Gson().toJson(this.nameTable.values().toArray());
        player.send(MessageParser.message("update","player",otherPlayer));
    }

    public List<LeaderCard> getLeaders()
    {
        List<LeaderCard> drawn = new ArrayList<>();

        for(int i = 0; i < 4; i++) {
            if(this.leaders.isEmpty()) {
                this.leaders = Game.getLeaderDeck();
                Collections.shuffle(this.leaders);
            }
            drawn.add(this.leaders.remove(0));
        }

        return drawn;
    }

    public void waitForOtherPlayer()
    {
        synchronized(this.readyPlayers)
        {
            this.readyPlayers.incrementAndGet();
            this.readyPlayers.notifyAll();

            if(this.readyPlayers.get() != this.numPlayer)
            {
                // The player is not the last one.
                while(this.readyPlayers.get() != this.numPlayer) {
                    try {
                        this.readyPlayers.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            else
            {
                // The player is the last one who got ready

                Collections.shuffle(this.round);

                String otherPlayer = this.round.stream().map(Player::getNickname).collect(Collectors.toList()).toString();
                System.out.println(otherPlayer);
                this.broadCast(MessageParser.message("update","player",otherPlayer));

                this.playerWithInkwell = this.round.poll();
                if (this.playerWithInkwell != null) this.playerWithInkwell.setActive();
            }
        }
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

        Gson parser = new Gson();
        String otherPlayer = parser.toJson(this.nameTable.values().toArray());
        this.broadCast(MessageParser.message("update","player",otherPlayer));

        this.broadCast("GameStart");

        this.readyPlayers.set(0);
        for(Player player : this.round) player.setActive();
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
