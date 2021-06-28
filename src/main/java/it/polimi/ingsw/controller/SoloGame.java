package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.resources.ResourcePack;
import it.polimi.ingsw.network.DisconnectedPlayerException;
import it.polimi.ingsw.network.Player;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.singleplayer.LorenzoIlMagnifico;
import it.polimi.ingsw.model.singleplayer.SoloAction;
import it.polimi.ingsw.model.singleplayer.SoloActionDeck;
import it.polimi.ingsw.util.MessageParser;
import it.polimi.ingsw.view.lightmodel.PlayerView;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of a singleplayer Game.
 * @see Game
 * @author Francesco Tosini
 */
public class SoloGame extends Game {
    private Player player;
    private final LorenzoIlMagnifico lorenzo;
    private final AtomicBoolean connected;
    private final AtomicBoolean init;
    private Timer timer;
    private boolean endGame;

    public SoloGame(Player player,String name) {
        super();
        this.player = player;
        this.player.setForGame(this.vatican.getFaithTrack(this.player.getID()),this.market);
        SoloActionDeck soloDeck = new SoloActionDeck(SoloAction.getSoloActionDeck("JSON/SoloAction.json"));
        this.lorenzo = new LorenzoIlMagnifico(this,this.vatican.getFaithTrack(-1),this.market,soloDeck);
        this.connected = new AtomicBoolean(true);
        this.init = new AtomicBoolean(false);
        this.endGame = false;
    }

    @Override
    public boolean isSinglePlayer() {
        return true;
    }

    @Override
    public void broadCast(String message) {
        try {
            this.player.send(message);
        } catch(DisconnectedPlayerException ignored) { }
    }

    @Override
    public void broadCastFull(String message) {
        this.broadCast(message);
    }

    public void start() {

        this.broadCast("alive?");
        this.connected.set(false);

        TimerTask controlConnnection = new TimerTask() {
            @Override
            public void run() {
                if(!connected.get())
                    player.reduceDisconnectCounter();
                connected.set(false);
                broadCastFull("alive?");
            }
        };

        this.timer = new Timer(true);
        this.timer.scheduleAtFixedRate(controlConnnection,1000,1000);

        this.player.send(MessageParser.message("update","player",this.getPlayerInfo()));
        this.market.update();
        this.player.send("GameStart");
    }

    public void setInitialised() {
        this.init.set(true);
    }

    @Override
    public void nextPlayer() {
        this.broadCast(MessageParser.message("update","player",this.getPlayerInfo()));
        if(!this.endGame) this.playSolo();
    }

    @Override
    public void endGame() {
        this.endGame = true;
        this.player.setHasEnded();
        this.timer.cancel();
    }

    public void playSolo() {
        SoloAction action = this.lorenzo.playSoloAction();
        this.broadCast(MessageParser.message("action",Action.SOLO_ACTION,action));
        this.broadCast(MessageParser.message("update","player",this.getPlayerInfo()));
        this.broadCastFull(MessageParser.message("event",GameEvent.ROUND,player.getNickname()));
    }

    @Override
    public List<LeaderCard> getLeaders() {

        List<LeaderCard> leaders = Game.getLeaderDeck();
        Collections.shuffle(leaders);

        List<LeaderCard> drawn = new ArrayList<>();

        for(int i = 0; i < 4; i++) {
            if(leaders.isEmpty()) {
                leaders = Game.getLeaderDeck();
                Collections.shuffle(leaders);
            }
            drawn.add(leaders.remove(0));
        }

        return drawn;
    }

    public String getPlayerInfo() {
        Gson parser = new Gson();
        Type listOfPlayerInfo = new TypeToken<List<PlayerView>>() {}.getType();

        List<PlayerView> players = new ArrayList<>();
        players.add(this.player.getPlayerStat());
        players.add(new PlayerView(-1,"Lorenzo il Magnifico",
                new ResourcePack(),
                this.lorenzo.getFaithMarker(),
                -1));

        return parser.toJson(players,listOfPlayerInfo);
    }

    // NEW

    public void isAlive(Player player) {
        this.connected.set(true);
    }

    public void hasDisconnected(Player player) {
        if(timer != null) this.timer.cancel();
        if(this.init.get())
            PlayerController.getPlayerController().postPlayerDisconnected(this.player.getNickname(),this);
    }

    public boolean resumePlayer(Player player) {

        player.resume(this.player);
        this.player = player;
        this.broadCast(MessageParser.message("update","player",this.getPlayerInfo()));
        this.market.update();
        this.player.updateAll();

        this.broadCast("alive?");
        this.connected.set(false);

        TimerTask controlConnnection = new TimerTask() {
            @Override
            public void run() {
                if(!connected.get())
                    player.reduceDisconnectCounter();
                connected.set(false);
                broadCastFull("alive?");
            }
        };

        this.timer = new Timer(true);
        this.timer.scheduleAtFixedRate(controlConnnection,1000,1000);

        return true;
    }
}
