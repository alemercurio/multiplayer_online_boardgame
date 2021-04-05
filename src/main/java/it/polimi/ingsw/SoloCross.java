package it.polimi.ingsw;

/**
 * @author Patrick Niantcho
 * @author patrick.niantcho@gmail.com
 */
public class SoloCross implements SoloAction {

    private int fatihPoint;
    private boolean Shuffle;

    /**
     * constructor
     * @param fatihPoint represent the faith point to apply for Lorenzo il magnifico when the action is apply
     * @param shuffle reveals whether or not the action deck should be shuffled
     */
    public SoloCross(int fatihPoint, boolean shuffle) {
        this.fatihPoint = fatihPoint;
        Shuffle = shuffle;
    }

    /** apply the effect of the action
     * @param player represents Lorenzo il Magnifico
     * @return a boolean that specify if the action deck should be shuffled
     */
    @Override
    public boolean Apply(LorenzoIlMagnifico player) {
        player.advancedFaithTrack(fatihPoint);
        return  Shuffle;
    }
    public int getFatihPoint() {
        return fatihPoint;
    }


}
