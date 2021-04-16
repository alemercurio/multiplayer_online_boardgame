package it.polimi.ingsw.solo;

/**
 * Interface for the different types of Solo Action tokens.
 * @author Patrick Niantcho
 */

public abstract class SoloAction {
    private boolean shuffle;

    /**
     * Reveals the SoloAction and applies its effect.
     * @param lorenzo the unique instance of LorenzoilMagnifico in a solo mode game.
     */
    public abstract void apply(LorenzoIlMagnifico lorenzo);

    /**
     * Returns whether the SoloAction causes the shuffling of the SoloActionDeck or not.
     * @return the boolean representing the need to shuffle or not.
     */
    public boolean toShuffle() {
        return shuffle;
    }

    /**
     * Sets whether the SoloAction causes the shuffling of the SoloActionDeck or not, called when constructing the SoloAction.
     * @param shuffle the boolean representing the need to shuffle or not.
     */
    protected void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }
}
