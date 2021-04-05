package it.polimi.ingsw;

import java.util.HashMap;
import java.util.Map;

/**
 * @author patrick Niantcho
 * @author patrick.niantcho@gmail.com
 */
public class SoloDiscard implements SoloAction {

    private Map<Color, Integer> toDiscard;

    /**
     *
     * @param toDiscard represent the number of a specified card to discard
     */
    public SoloDiscard(HashMap<Color, Integer> toDiscard) {
        this.toDiscard = toDiscard;
    }

    /**
     *
     * @param player represent LorenzoIlMagnifico
     * @return specify that after apply the action, to action desk should not be shuffled
     */
    @Override
    public boolean Apply(LorenzoIlMagnifico player) {
       for ( Map.Entry<Color, Integer> pair : toDiscard.entrySet() )
           player.discard( pair.getKey(), pair.getValue());
       return false;
    }

    /**
     *
     * @return the effect of this action on the MarketBoard
     */
    public Map<Color, Integer> getToDiscard() {
        return toDiscard;
    }
}
