package it.polimi.ingsw;

/** represent the FaithTrack
 * @author Patrick Niantcho
 * @author patrick.niantcho@gmail.com
 */
public class FaithTrack {

    private int faithMarker = 0;
    private boolean [] reportSection;
    private final Vatican vatican;
    // represent the index of the current faithTrack in the arrayList of faithTrack in Vatican
    private final int faithTrackID;
    // has the same length as the field reportSection in vatican
    private boolean [] popeFavours;

    /** constructor
     * @param reportSection used to specified the squares that are part of a pope Space
     * @param vatican represents the (observer) vatican
     * @param faithTrackID represents the ID of the faithTrack
     * @param popeFavours ued to specified whether popeFavours are activated or not
     */
    public FaithTrack(boolean[] reportSection, Vatican vatican, int faithTrackID, boolean [] popeFavours) {
        this.reportSection = reportSection;
        this.vatican = vatican;
        this.faithTrackID = faithTrackID;
        // initialize with false
        this.popeFavours  = popeFavours;
    }

    /**
     * advance the faithMaker of one square
     * PATTERN OBSERVER: whenever this method is called, notify the Vatican (observer) to update the favour
     */
    public void advance (){
        faithMarker ++;

        if (faithMarker == 24)
            vatican.setFinalRound();

       vatican.isToReport(faithTrackID);
    }

    /**
     *advance the faithMaker of a specified number of squares
     * used ONLY by the method wastedResource in Vatican
     * @param step the number of squares the player must advance his marker
     * @return represents the position of the faithMarker the the faithTrack after advance of "step" squares
     */
    public int advance (int step){

        faithMarker += step;
        if (faithMarker > 24)
        {
            faithMarker = 24;
           vatican.setFinalRound();
        }
        return this.faithMarker;
    }

    /**
     *
     * @return this position of the faithMarker in the faithTrack
     */
    public int getFaithMarker() {return this.faithMarker;}

    /**
     *
     * @param index must have values from 0, and represent the favour to give from the current player's faithTrack
     */
    public void givePopeFavour(int index) {
        this.popeFavours [index] = true;
    }

    /**
     *
     * @param index must have values from 0, and represent the favour to discard from the current player's faithTrack
     */
    public void disCardPopeFavour (int index){
        this.popeFavours [index] = false;
    }

    /**
     *
     * @return this position of the faithMarker in the faithTrack
     */
    public int getFaithTrackID() {return this.faithTrackID;}

}
