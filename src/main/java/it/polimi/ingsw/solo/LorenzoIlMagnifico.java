package it.polimi.ingsw.solo;

import it.polimi.ingsw.Game;
import it.polimi.ingsw.SoloGame;
import it.polimi.ingsw.cards.Color;
import it.polimi.ingsw.faith.FaithTrack;
import it.polimi.ingsw.supply.MarketBoard;

/**
 * Represents LorenzoIlMagnifico, the opponent player when playing a game in solo mode.
 * @author Patrick Niantcho
 */
public class LorenzoIlMagnifico {
    private final FaithTrack faithTrack;
    private final MarketBoard market;
    private final SoloActionDeck deck;
    private final Game game;

    /**
     * Constructs the single player opponent LorenzoIlMagnifico.
     * @param faithTrack the FaithTrack of LorenzoIlMagnifico.
     * @param market the reference to the MarketBoard.
     * @param deck the full stack of SoloAction tokens available for LorenzoIlMagnifico.
     */
    public LorenzoIlMagnifico(SoloGame game, FaithTrack faithTrack, MarketBoard market, SoloActionDeck deck) {
        this.faithTrack = faithTrack;
        this.market = market;
        this.deck = deck;
        this.game = game;
    }

    /**
     * Extracts a random SoloAction and applies its effect.
     * @return the SoloAction that has been played.
     */
    public SoloAction playSoloAction() {
        SoloAction playedAction = deck.getSoloAction();
        playedAction.apply(this);
        if (playedAction.toShuffle()) {
            deck.shuffle();
        }
        return playedAction;
    }

    /**
     * Calls the discarding of DevelopmentCards over the MarketBoard instance,
     * which contains the full grid.
     * @param color the color of the Cards.
     * @param amount the number of Cards of the specified color to discard.
     */
    public void discard (Color color, int amount){
        boolean ableToDiscard = market.discard(color, amount);
        if(!ableToDiscard) {
            game.endGame();
        }
    }

    /**
     * Makes the Faith Marker of LorenzoIlMagnifico (the Black Cross)
     * advance of the specified number of steps.
     * @param step the number of spaces the Black Cross must advance in the FaithTrack.
     */
    public void advance(int step) {
        faithTrack.advance(step);
    }

    /**
     * Calls the shuffling of the full stack of SoloAction in the SoloActionDeck.
     */
    public void shuffleDeck() {
        deck.shuffle();
    }
}
