package it.polimi.ingsw.solo;

/**
 * Solo Action token whose revelation makes the Black Cross advance on the Faith Track.
 * @author Patrick Niantcho
 */
public class SoloCross extends SoloAction {
    private final int fatihPoints;

    /**
     * Constructs a SoloAction that makes the Black Cross advance of a number of spaces and eventually shuffles the full deck.
     * @param fatihPoints the number of steps the Black Cross will advance.
     * @param shuffle the boolean representing whether or not the action deck will be shuffled.
     */
    public SoloCross(int fatihPoints, boolean shuffle) {
        this.fatihPoints = fatihPoints;
        this.setShuffle(shuffle);
    }

    /**
     * Reveals the SoloAction and applies its effect, making the Black Cross advance.
     * @param lorenzo the unique instance of LorenzoIlMagnifico in the solo mode game.
     */
    @Override
    public void apply(LorenzoIlMagnifico lorenzo) {
        lorenzo.advance(fatihPoints);
        if(toShuffle()) {
            lorenzo.shuffleDeck();
        }
    }

    /**
     * Returns the number of spaces that the Black Cross will advance when the SoloAction gets activated.
     * @return the number of faithPoints earned by LorenzoIlMagnifico
     */
    public int getFatihPoints() {
        return fatihPoints;
    }
}
