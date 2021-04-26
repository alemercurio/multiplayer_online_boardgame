package it.polimi.ingsw.solo;

import it.polimi.ingsw.cards.Color;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a SoloAction token whose revelation discard DevelopmentCards from the Market,
 * from the lowest level to the highest.
 * @author Patrick Niantcho
 */
public class SoloDiscard extends SoloAction {
    private final Map<Color,Integer> toDiscard;

    /**
     * Constructs a SoloAction that causes the discarding of the given amounts of DevelopmentCards.
     * @param toDiscard a Map representing the number of Cards to discard for each color.
     */
    public SoloDiscard(Map<Color,Integer> toDiscard) {
        super(false);
        this.toDiscard = new HashMap<>(toDiscard);
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
     * Returns an unmodifiable Map representing the effect of the SoloAction.
     * @return a Map representing the number of Cards that the SoloAction discards for each color.
     */
    public Map<Color,Integer> getToDiscard() {
        return Collections.unmodifiableMap(this.toDiscard);
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == null) return false;
        else if(o == this) return true;
        else if(!(o instanceof SoloDiscard)) return false;
        else
        {
            SoloDiscard sd = (SoloDiscard) o;
            return (this.toDiscard.equals(sd.toDiscard)) && (this.toShuffle() == sd.toShuffle());
        }
    }
}
