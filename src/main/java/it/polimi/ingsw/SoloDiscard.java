package it.polimi.ingsw;

import java.util.Map;

public class SoloDiscard implements SoloAction {

    public SoloDiscard(Map<Color, Integer> toDiscard) {
        this.toDiscard = toDiscard;
    }

    private Map<Color, Integer> toDiscard;

    @Override
    public boolean Apply(LorenzoIlMagnifico player) {
       for ( Map.Entry<Color, Integer> pair : toDiscard.entrySet() )
           player.discard( pair.getKey(), pair.getValue());
       return false;
    }

    public Map<Color, Integer> getToDiscard() {
        return toDiscard;
    }
}
