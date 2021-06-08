package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.resources.ResourcePack;
import it.polimi.ingsw.network.Player;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.singleplayer.LorenzoIlMagnifico;
import it.polimi.ingsw.model.singleplayer.SoloAction;
import it.polimi.ingsw.model.singleplayer.SoloActionDeck;
import it.polimi.ingsw.util.MessageParser;
import it.polimi.ingsw.view.lightmodel.PlayerView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of a singleplayer Game.
 * @see Game
 * @author Francesco Tosini
 */
public class SoloGame extends Game {
    private final Player player;
    private final LorenzoIlMagnifico lorenzo;
    private boolean endGame;

    public SoloGame(Player player,String name) {
        super();

        this.player = player;
        this.player.setForGame(this.vatican.getFaithTrack(this.player.getID()),this.market);

        SoloActionDeck soloDeck = new SoloActionDeck(SoloAction.getSoloActionDeck("src/main/resources/JSON/SoloAction.json"));
        this.lorenzo = new LorenzoIlMagnifico(this,this.vatican.getFaithTrack(-1),this.market,soloDeck);
        this.endGame = false;
    }

    @Override
    public boolean isSinglePlayer() {
        return true;
    }

    @Override
    public void broadCast(String message) {
        this.player.send(message);
    }

    @Override
    public void broadCastFull(String message) {
        this.player.send(message);
    }

    public void start() {
        this.player.send(MessageParser.message("update","player",this.getPlayerInfo()));
        this.market.update();
        this.player.send("GameStart");
    }

    @Override
    public void nextPlayer() {
        if(!this.endGame) this.playSolo();
    }

    @Override
    public void endGame() {
        this.endGame = true;
        this.player.setHasEnded();
    }

    public void playSolo() {
        SoloAction action = this.lorenzo.playSoloAction();
        this.broadCast(MessageParser.message("action",Action.SOLO_ACTION,action));
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

    public String getPlayerInfo()
    {
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
}
