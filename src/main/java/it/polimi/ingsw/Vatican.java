package it.polimi.ingsw;

import java.util.ArrayList;

/** represent the vatican
 * @author Patrick Niantcho
 * @author patrick.niantcho@gmail.com
 */
public class Vatican {
    private final ArrayList<FaithTrack> faithTracks;
    /**
     *  victory points associated to each square of the faithTrack
     */
    private int[] track;
    private final int [][] reportSection;
    private  boolean [] reportActive;
    private  final Game game;
    private boolean finalRound = false;
    private int lastFavourActivated = -1;
    /**
     * represents the furthest case reached by a player
     */
    private int furthestReachedSquare = 0;


    /** constructor
     * @param faithTracks represents the list of faithTrack of all the players;
     * @param track represents the victory point associated with each square;
     * @param reportSection it has as many rows as there are report sections. for each row, we have:
     *                      first column: which represents the first square of the current report section;
     *                      second column: which represents the last square of the current report section;
     *                      third column: which represents the victory points associated to the current report section;
     * @param reportActive represents the report section that have been activated;
     * @param game represent the game where the current Vatican is used;
     */
    public Vatican(ArrayList<FaithTrack> faithTracks, int[] track, int[][] reportSection, boolean[] reportActive, Game game) {
        this.faithTracks = faithTracks;
        this.track = track;
        this.reportSection = reportSection;
        this.reportActive = reportActive;
        this.game = game;
    }


    /**
     * this method is invoked whenever a player decides to discard a resource
     * @param track represents the trackID of the faithTrack's player. Also represents the index of the faithTrack in the arrayList of faithTrack
     * @param num represents the number of resources discarded by the player who invoked the method
     */
    public void wastedResources(int track, int num){
        int reachedSquare = 0;

        // after this statement, I have the furthest square reached by any player's marker
        for ( int i = 0; i < faithTracks.size(); i++){
            if ( i != track){
                   reachedSquare =  faithTracks.get(i).advance(num);
                   if (reachedSquare > furthestReachedSquare)
                       furthestReachedSquare = reachedSquare;
            }
        }

        //after this statement, I have the report section representing the furthest square reached
        int i = lastFavourActivated;
        while ( furthestReachedSquare >= reportSection[i + 1][1])
            i++;

        // report only if the section to report if after the last section reported
        if (i > lastFavourActivated)
            this.vaticanReport(i);

    }

    /** this method give or discard the specified popeFavour for each payer's faithTrack, according the square reached by the faithMarker
     *
     * @param sectionToReport represents the last section to report, from the last section reported
     */
    public void vaticanReport (int sectionToReport){

        FaithTrack current_faithTrack;
        int current_faithMaker = 0;

        for (int i = 0; i < faithTracks.size(); i++) {
            current_faithTrack = faithTracks.get(i);
            current_faithMaker = current_faithTrack.getFaithMarker();

            // for each faithTrack, active or discard popeFavours, from the last reported section to the further report section reached
            for (int j = lastFavourActivated + 1; j <= sectionToReport; j++) {
                if (current_faithMaker >= reportSection[j][0])
                    current_faithTrack.givePopeFavour(j);
                else
                    current_faithTrack.disCardPopeFavour(j);
            }
        }
        lastFavourActivated = sectionToReport;
    }

    /**when any faithMarker goes one square forward, this method is invoked to check whether there is any section to report
     * @param trackID represents the ID of the faithTrack (and player) who invoked the method
     */
    public void isToReport(int trackID){
        //only the section after the last one reported is checked because advancing of only one square, the maker can't reach more than one pope space
        if (faithTracks.get(trackID).getFaithMarker() == reportSection[lastFavourActivated + 1][1])
            this.vaticanReport(lastFavourActivated + 1);
    }

    /**  PATTERN OBSERVER : notify Game to end the game is it's the final round
     *
     */
    public void endGame()
    {
        if (finalRound = true){game.endGame();}
    }

    /** specified that the current round is the final round
     *
     */
    public void setFinalRound () {finalRound = true; endGame();}


    /**
     * @param faithMarker specified the square form with we want to point associated to
     * @return the point associated to the square specified
     */
    public int getPoints( int faithMarker){return track [faithMarker];}

    /**
     *
     * @param index represente the index in the list of the specified faithTrack
     * @return the specified faithTrack
     */
    public FaithTrack getFaithTrack (int index){
        return faithTracks.get(index);
    }

}
