package it.polimi.ingsw;

import java.util.HashMap;
import java.util.Map;

/**
 * Solo Action token whose revelation discard Development Cards from the grid, from the lowest level to the highest.
 * @author Patrick Niantcho
 */
public class SoloDiscard extends SoloAction {
    private final Map<Color, Integer> toDiscard;

    /**
     * Constructs a SoloAction that cause the discarding of a number of DevelopmentCards.
     * @param toDiscard the number of Cards to discard for each color.
     */
    public SoloDiscard(HashMap<Color, Integer> toDiscard) {
        this.toDiscard = toDiscard;
        this.setShuffle(false);
    }

    /**
     * Reveals the SoloAction and applies its effect, the discarding of DevelopmentCards.
     * @param lorenzo the unique instance of LorenzoilMagnifico in the solo mode game.
     */
    @Override
    public void apply(LorenzoIlMagnifico lorenzo) {
       for(Map.Entry<Color, Integer> pair : toDiscard.entrySet())
           lorenzo.discard(pair.getKey(), pair.getValue());
    }

    /**
     * Returns the Map representing the effect of the SoloAction.
     * @return the number of Cards that the SoloAction discards for each color.
     */
    public Map<Color, Integer> getToDiscard() {
        return toDiscard;
    }
}
