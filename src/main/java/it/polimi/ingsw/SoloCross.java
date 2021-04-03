package it.polimi.ingsw;

public class SoloCross implements SoloAction {



    private int fatihPoint;
    private boolean Shuffle;

    public SoloCross(int fatihPoint, boolean shuffle) {
        this.fatihPoint = fatihPoint;
        Shuffle = shuffle;
    }

    @Override
    public boolean Apply(LorenzoIlMagnifico player) {
        player.advancedFaithTrack(fatihPoint);
        return  Shuffle;
    }
    public int getFatihPoint() {
        return fatihPoint;
    }


}
