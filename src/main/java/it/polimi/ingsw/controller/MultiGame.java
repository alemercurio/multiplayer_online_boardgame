package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.network.DisconnectedPlayerException;
import it.polimi.ingsw.network.Player;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.resources.ResourcePack;
import it.polimi.ingsw.util.MessageParser;
import it.polimi.ingsw.view.lightmodel.PlayerView;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of a multiplayer Game.
 * @see Game
 * @author Francesco Tosini
 */
public class MultiGame extends Game implements Runnable{

    private final int numPlayer;
    private final Map<Player,String> nameTable;
    private final LinkedList<Player> round;
    private final AtomicInteger readyPlayers;
    private Player playerWithInkwell;
    private List<LeaderCard> leaders;
    private boolean endGame;

    private static final ResourcePack[] initialAdvantages = { new ResourcePack(),
            new ResourcePack(0,0,0,0,0,1),
            new ResourcePack(0,0,0,0,1,1),
            new ResourcePack(0,0,0,0,1,2) };

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
            try { player.send(message); }
            catch(DisconnectedPlayerException ignored) { }
        }
    }

    public void broadCastFull(String message) {
        for(Player player : this.nameTable.keySet()) {
            try { player.send(message); }
            catch(DisconnectedPlayerException ignored) { }
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
                this.broadCastFull(MessageParser.message("event",GameEvent.PLAYER_JOIN,name));
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
        player.send(MessageParser.message("update","player",this.getPlayerInfo()));
        player.send(MessageParser.message("event",GameEvent.JOINED_GAME));

        this.round.add(player);
        player.setForGame(this.vatican.getFaithTrack(player.getID()),this.market);
        if(this.round.size() == this.numPlayer) Game.newGames.remove(this);
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

    /*public void waitForOtherPlayer()
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

                this.playerWithInkwell = this.round.poll();
                if (this.playerWithInkwell != null) this.playerWithInkwell.setActive();
            }
        }
    }*/

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

        Collections.shuffle(this.round);
        this.broadCast(MessageParser.message("update","player",this.getPlayerInfo()));

        this.market.update();

        this.broadCast("GameStart");

        this.readyPlayers.set(0);
        for(Player player : this.round) player.setActive();

        new Thread(this).start();
    }

    public ResourcePack getAdvantage(Player player) {
        int index = this.round.indexOf(player)%4;
        return MultiGame.initialAdvantages[index];
    }

    /*@Override
    public void nextPlayer(Player player) {
        this.round.add(player);

        this.broadCast(MessageParser.message("update","player",this.getPlayerInfo()));

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
    }*/

    public String getPlayerInfo()
    {
        Gson parser = new Gson();
        Type listOfPlayerInfo = new TypeToken<List<PlayerView>>() {}.getType();

        List<PlayerView> players = new ArrayList<>();
        for(Player player : this.round) players.add(player.getPlayerStat());

        return parser.toJson(players,listOfPlayerInfo);
    }

    public synchronized void endGame() {
        this.endGame = true;
    }

    // NEW

    private final List<Player> disconnected = new ArrayList<>();
    private final AtomicBoolean nextRound = new AtomicBoolean();

    public void isAlive(Player player) {
        synchronized(this.disconnected) {
            disconnected.remove(player);
        }
    }

    public void run() {

        this.disconnected.addAll(this.nameTable.keySet());
        this.broadCastFull("alive?");

        TimerTask controlConnnection = new TimerTask() {
            @Override
            public void run() {
                synchronized(disconnected) {
                    for(Player player : disconnected) {
                        player.reduceDisconnectCounter();
                    }
                    disconnected.addAll(nameTable.keySet());
                }
                broadCastFull("alive?");
            }
        };

        Timer t = new Timer(true);
        t.scheduleAtFixedRate(controlConnnection,1000,1000);

        // Game management

        synchronized(this.readyPlayers) {
            while(this.readyPlayers.get() < this.numPlayer) {
                try {
                    this.readyPlayers.wait();
                } catch(InterruptedException ignored) { }
            }
        }

        this.playerWithInkwell = this.round.pollFirst();
        Player next = this.playerWithInkwell;

        do {

            if(next != null) {

                next.setActive();
                this.broadCast(MessageParser.message("event",GameEvent.ROUND,next.getNickname()));

                this.nextRound.set(false);

                synchronized(this.nextRound) {
                    while(!this.nextRound.get()) {
                        try { this.nextRound.wait(); }
                        catch (InterruptedException ignored) { }
                    }
                }

                if(!next.isDisconnected()) this.round.add(next);
            }

            this.broadCast(MessageParser.message("update","player",this.getPlayerInfo()));

            next = this.round.pollFirst();

        } while(!endGame || next != playerWithInkwell);

        this.round.add(next);

        for(Player play : this.round) {
            play.setHasEnded();
            play.setActive();
        }
        this.broadCast("GameEnd");
    }

    public void waitForOtherPlayer()
    {
        synchronized(this.readyPlayers) {
            this.readyPlayers.incrementAndGet();
            this.readyPlayers.notifyAll();
        }
    }

    @Override
    public void nextPlayer() {
        synchronized(this.nextRound) {
            this.nextRound.set(true);
            this.nextRound.notifyAll();
        }
    }

    @Override
    public void hasDisconnected(Player player) {
        this.round.remove(player);
        this.broadCastFull(MessageParser.message("event",GameEvent.PLAYER_DISCONNECT,player.getNickname()));
        this.vatican.removeFaithTrack(player.getID());
    }
}
