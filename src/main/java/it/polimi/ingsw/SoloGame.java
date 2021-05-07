package it.polimi.ingsw;

import it.polimi.ingsw.solo.LorenzoIlMagnifico;
import it.polimi.ingsw.solo.SoloAction;
import it.polimi.ingsw.solo.SoloActionDeck;

public class SoloGame extends Game
{
    private final Player player;
    private String name;
    private final LorenzoIlMagnifico lorenzo;
    private boolean endGame;

    public SoloGame(Player player,String name)
    {
        super();

        this.player = player;
        this.name = name;

        SoloActionDeck soloDeck = new SoloActionDeck(SoloAction.getSoloActionDeck("src/main/resources/JSON/SoloAction.json"));
        this.lorenzo = new LorenzoIlMagnifico(this.vatican.getFaithTrack(),this.market,soloDeck);
        this.endGame = false;
    }

    @Override
    public boolean isSinglePlayer() { return true; }

    @Override
    public void broadCast(String message) { this.player.send(message); }

    @Override
    public boolean nameAvailable(String name) {
        return !name.equals("Lorenzo");
    }

    @Override
    public boolean setNickname(Player player, String name) { this.name = name; return true; }

    @Override
    public String getNickname(Player player) { return this.name; }

    @Override
    public void start()
    {
        this.player.send("GameStart");
    }

    @Override
    public void nextPlayer(Player player)
    {
        if(!this.endGame) this.playSolo();
    }

    @Override
    public void endGame()
    {
        this.endGame = true;
        this.player.setHasEnded();
    }

    public void playSolo()
    {
        SoloAction action = this.lorenzo.playSoloAction();
        this.broadCast(action.toString());
    }
}
