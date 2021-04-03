package it.polimi.ingsw;

public class FaithTrack {

    private int faithMarker;
    private boolean [] reportSection;
    private final Vatican vatican;
    private final int faithTrackID;
    private byte [] popeFavours;

    public FaithTrack(int faithMarker, boolean[] reportSection, Vatican vatican, int faithTrackID, byte [] popeFavours) {
        this.faithMarker = faithMarker;
        this.reportSection = reportSection;
        this.vatican = vatican;
        this.faithTrackID = faithTrackID;
        this.popeFavours  = popeFavours; // initialize with false
    }

    public void advance (){
        faithMarker ++;
        for(int i = 0; i < vatican.getNumberSection(); i++){
            if (faithMarker == vatican.getReportSpace(i,2)){ // convention: faithMarker must have values from 0
                if (!vatican.getFavourStatus(i)) { // no player has active this favour yet
                    vatican.vaticanReport(this.faithMarker, i);
                }
            }
        }
    }
    public void advance (int num){

        faithMarker += num;
        if (faithMarker > 24)
        {
            faithMarker = 24;
           vatican.setFinalRound();
        }

    }

    public void report (){ // used to check the possibility of report for the faith marker advanced due to waste of resources
        boolean last = false;
        int sectionToReport = vatican.getLastFavourActivated();
        int i = vatican.getLastFavourActivated() + 1;

        while ( (i < popeFavours.length) && (last == false)){
            if ( (!vatican.getFavourStatus(i)) &&(faithMarker >= vatican.getReportSpace(i,1)))
                sectionToReport = i;
            if (faithMarker < vatican.getReportSpace(i,1) )
                last = true;
            i++;
        }
        if (sectionToReport != vatican.getLastFavourActivated())
            vatican.vaticanReport(faithMarker, sectionToReport);

    }

    public int getFaithMarker() {return this.faithMarker;}

    public void givePopeFavour(int index) { // convention : index must have values from 0
        this.popeFavours [index] = 1;
    }

    public void disCardPopeFavour (int index){
        this.popeFavours [index] = -1;
    }

    public byte getPopeFavour (int index){
        return popeFavours [index];
    }
}
