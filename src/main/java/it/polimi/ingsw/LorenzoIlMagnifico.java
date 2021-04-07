package it.polimi.ingsw;

/**
 * Singleton for the unique instance of Lorenzo il Magnifico, when playing a game in solo mode.
 * @author Patrick Niantcho
 */
public class LorenzoIlMagnifico {
    private final FaithTrack faithTrack;
    private final MarketBoard market;
    private final SoloActionDeck deck;

    /**
     * Constructs the Singleton instance.
     * @param faithTrack the FaithTrack of LorenzoIlMagnifico.
     * @param market the reference to the MarketBoard.
     * @param deck the full stack of SoloAction tokens available for LorenzoIlMagnifico.
     */
    public LorenzoIlMagnifico(FaithTrack faithTrack, MarketBoard market, SoloActionDeck deck) {
        this.faithTrack = faithTrack;
        this.market = market;
        this.deck = deck;
    }

    /**
     * Extracts a random SoloAction and applies its effect.
     */
    public void playSoloAction() {
        SoloAction playedAction = deck.getSoloAction();
        playedAction.apply(this);
        if (playedAction.toShuffle()) {
            deck.shuffle();
        }
    }

    /**
     * Calls the discarding of DevelopmentCards over the MarketBoard instance, which contains the full grid.
     * @param color the color of the Cards.
     * @param amount the number of Cards of the specified color to discard.
     */
    public void discard (Color color, int amount){
        market.discard(color, amount);
    }

    /**
     * Calls the method to make the Faith Marker of LorenzoIlMagnifico (the Black Cross) advance.
     * @param step the number of spaces the Black Cross must advance in the FaithTrack.
     */
    public void advance(int step){
        faithTrack.advance(step);
    }

    /**
     * Calls the shuffling of the full stack of SoloAction in the SoloActionDeck.
     */
    public void shuffleDeck()
    {
        deck.shuffle();
    }
}