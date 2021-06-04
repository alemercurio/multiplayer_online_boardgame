package it.polimi.ingsw.model.singleplayer;

/**
 * Represents a SoloAction token whose revelation makes the Black Cross advance on the Faith Track.
 * @author Patrick Niantcho
 */
public class SoloCross extends SoloAction {
    private final int faithPoints;

    /**
     * Constructs a SoloAction that makes the Black Cross advance of the specified number of spaces
     * and eventually shuffles the full deck.
     * @param fatihPoints the number of steps the Black Cross will advance.
     * @param shuffle the boolean representing whether or not the action deck will be shuffled.
     */
    public SoloCross(int fatihPoints, boolean shuffle) {
        super(shuffle);
        this.faithPoints = fatihPoints;
    }

    /**
     * Reveals the SoloAction and applies its effect, making the Black Cross advance.
     * @param lorenzo the unique instance of LorenzoIlMagnifico in the singleplayer mode game.
     */
    @Override
    public void apply(LorenzoIlMagnifico lorenzo) {
        lorenzo.advance(this.faithPoints);
        if(toShuffle()) {
            lorenzo.shuffleDeck();
        }
    }

    /**
     * Returns the number of spaces that the Black Cross will advance when the SoloAction gets activated.
     * @return the number of faithPoints earned by LorenzoIlMagnifico
     */
    public int getFaithPoints() {
        return faithPoints;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        else if(o == this) return true;
        else if(!(o instanceof SoloCross)) return false;
        else {
            SoloCross sc = (SoloCross) o;
            return (this.faithPoints == sc.faithPoints) && (this.toShuffle() == sc.toShuffle());
        }
    }
}
