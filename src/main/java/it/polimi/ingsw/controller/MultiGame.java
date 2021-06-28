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
public class MultiGame extends Game implements Runnable {

    private final int numPlayer;
    private final LinkedList<Player> round;
    private final AtomicInteger readyPlayers;
    private List<LeaderCard> leaders;
    private boolean endGame;

    private static final ResourcePack[] initialAdvantages = { new ResourcePack(),
            new ResourcePack(0,0,0,0,0,1),
            new ResourcePack(0,0,0,0,1,1),
            new ResourcePack(0,0,0,0,1,2) };

    public MultiGame(int numPlayer) {
        super();
        this.numPlayer = numPlayer;
        this.readyPlayers = new AtomicInteger(0);
        this.round = new LinkedList<>();

        this.leaders = Game.getLeaderDeck();
        Collections.shuffle(this.leaders);
        this.endGame = false;
        new Thread(this).start();
    }

    public MultiGame(Player creator,int numPlayer) {
        this(numPlayer);
        this.addPlayer(creator);
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
        synchronized(this.players) {
            for(Player player : this.players) {
                try { player.send(message); }
                catch(DisconnectedPlayerException ignored) { }
            }
        }
    }

    public synchronized void addPlayer(Player player) {
        this.broadCastFull(MessageParser.message("event",GameEvent.PLAYER_JOIN,player.getNickname()));

        synchronized(this.players) {
            this.players.add(player);
            player.setForGame(this.vatican.getFaithTrack(player.getID()),this.market);
            this.players.notifyAll();
        }

        player.send(MessageParser.message("update","player",this.getPlayerInfo()));
        player.send(MessageParser.message("event",GameEvent.JOINED_GAME));

        if(this.players.size() == this.numPlayer) {
            Game.newGames.remove(this);
        }
    }

    public List<LeaderCard> getLeaders() {
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

    public ResourcePack getAdvantage(Player player) {
        int index = this.round.indexOf(player)%4;
        return MultiGame.initialAdvantages[index];
    }

    public String getPlayerInfo() {
        Gson parser = new Gson();
        Type listOfPlayerInfo = new TypeToken<List<PlayerView>>() {}.getType();

        List<PlayerView> players = new ArrayList<>();
        for(Player player : this.players) players.add(player.getPlayerStat());

        return parser.toJson(players,listOfPlayerInfo);
    }

    public synchronized void endGame() {
        this.endGame = true;
    }

    // NEW

    private final List<Player> players = new ArrayList<>();
    private Player currentPlayer;
    private final List<Player> disconnected = new LinkedList<>();
    private final Map<String,Player> disconnectedPlayer = new HashMap<>();
    private final AtomicBoolean nextRound = new AtomicBoolean();

    private final AtomicBoolean gameStarted = new AtomicBoolean(false);

    public void isAlive(Player player) {
        synchronized(this.disconnected) {
            disconnected.remove(player);
        }
    }

    public void run() {

        // Disconnection control

        synchronized(this.players) {
            this.disconnected.addAll(this.players);
            this.broadCastFull("alive?");
        }

        TimerTask controlConnnection = new TimerTask() {
            @Override
            public void run() {
                synchronized(disconnected) {
                    for (Player player : disconnected) {
                        player.reduceDisconnectCounter();
                    }

                    synchronized(players) {
                        disconnected.addAll(players);
                        broadCastFull("alive?");
                    }
                }
            }
        };

        Timer t = new Timer(true);
        t.scheduleAtFixedRate(controlConnnection,1000,1000);

        // Game Management

        synchronized(this.players) {
            while(this.players.size() < this.numPlayer) {
                try {
                    this.players.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // Every player has joined

        this.round.addAll(this.players);
        Collections.shuffle(this.round);

        this.broadCast(MessageParser.message("update","player",this.getPlayerInfo()));
        this.market.update();

        this.gameStarted.set(true);
        this.broadCast("GameStart");

        this.readyPlayers.set(0);
        for(Player player : this.round) player.setActive();

        // Game management

        synchronized(this.readyPlayers) {
            while(this.readyPlayers.get() < this.numPlayer) {
                try {
                    this.readyPlayers.wait();
                } catch(InterruptedException ignored) { }
            }
        }

        Player playerWithInkwell = this.round.pollFirst();
        Player next = playerWithInkwell;

        do {

            if(next != null) {
                if(!next.isDisconnected()) {
                    synchronized(this) {
                        this.currentPlayer = next;
                    }
                    next.setActive();
                    this.broadCastFull(MessageParser.message("event",GameEvent.ROUND,next.getNickname()));

                    this.nextRound.set(false);

                    synchronized(this.nextRound) {
                        while(!this.nextRound.get()) {
                            try { this.nextRound.wait(); }
                            catch (InterruptedException ignored) { }
                        }
                    }
                }
                this.round.add(next);
            } else this.endGame();
            this.broadCast(MessageParser.message("update","player",this.getPlayerInfo()));
            next = this.round.pollFirst();
        } while((!endGame || next != playerWithInkwell) && !this.players.isEmpty());

        this.round.add(next);

        for(Player play : this.round) {
            play.setHasEnded();
            play.setActive();
        }

        this.broadCast("GameEnd");
        t.cancel();
        for(String name : this.disconnectedPlayer.keySet())
            PlayerController.getPlayerController().removePlayerDisconnected(name);
    }

    public void waitForOtherPlayer() {
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
    public synchronized void hasDisconnected(Player player) {
        synchronized(this.players) {
            this.players.remove(player);
        }
        if(this.gameStarted.get()) {
            this.vatican.pauseFaithTrack(player.getID());
            this.disconnectedPlayer.put(player.getNickname(),player);
            PlayerController.getPlayerController().postPlayerDisconnected(player.getNickname(),this);
        } else {
            this.vatican.removeFaithTrack(player.getID());
        }
        this.broadCastFull(MessageParser.message("event",GameEvent.PLAYER_DISCONNECT,player.getNickname()));
        if(player == this.currentPlayer) this.nextPlayer();
    }

    public boolean resumePlayer(Player newPlayer) {
        Player oldPlayer = this.disconnectedPlayer.get(newPlayer.getNickname());
        if(oldPlayer != null) {
            newPlayer.resume(oldPlayer);
            newPlayer.updateAll();
            synchronized(this.players) {
                this.players.add(newPlayer);
            }
            if(this.round.contains(oldPlayer)) {
                this.round.set(this.round.indexOf(oldPlayer),newPlayer);
            } else this.currentPlayer = newPlayer;
            this.broadCast(MessageParser.message("update","player",this.getPlayerInfo()));
            this.market.update();
            this.vatican.retrieveFaithTrack(oldPlayer.getID());
            return true;
        } else return false;
    }
}
