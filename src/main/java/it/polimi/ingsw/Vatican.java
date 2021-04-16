package it.polimi.ingsw;

import java.util.ArrayList;

/**
 * Class to represent the game feature of the Vatican, meaning the set of Faith Tracks, Report Sections, Pope Spaces, Pope's Favours.
 * It is shared by all the Players in a game.
 * @author Patrick Niantcho
 */
public class Vatican {
    private final ArrayList<FaithTrack> faithTracks;
    private final int[] track;
    private final int[][] reportSection;
    private final Game game;
    private int lastPopeSpace = 0;
    private int furthestSpaceReached = 0;

    /**
     * Constructs the Vatican with all of its attributes.
     * @param faithTracks the list of FaithTracks of all the players.
     * @param track the Victory Points associated with each space in the FaithTrack.
     * @param reportSection a matrix with as many rows as there are report sections. For each row, three columns:
     *                      first column: the first space of the report section;
     *                      second column: the last space of the report section;
     *                      third column: the amount of Victory Points given by the Pope's Favor of the report section.
     * @param game the Game that the Vatican is associated with.
     */
    public Vatican(ArrayList<FaithTrack> faithTracks, int[] track, int[][] reportSection, Game game) {
        this.faithTracks = faithTracks;
        this.track = track;
        this.reportSection = reportSection;
        this.game = game;
    }

    /**
     * When a Player is forced to discard some Resources, he calls this method and makes all other Players advance on their FaithTrack.
     * @param track the identifier of the FaithTrack of the Player who discarded Resources.
     * @param num the number of Resources discarded by the Player who calls the method.
     */
    public void wastedResources(int track, int num) {
        // makes other Players advance and update the furthestSpaceReached by any Player's Faith Marker
        for(FaithTrack faithTrack : faithTracks) {
            if (faithTrack.getID() != track) {
                int spaceReached = faithTrack.advance(num);
                if (spaceReached > furthestSpaceReached)
                    furthestSpaceReached = spaceReached;
            }
        }
    }

    /**
     * Activates a Vatican Report and gives the Pope's Favor to Players whose FaithMarker is in the reported section.
     * @param popeSpace the position of the FaithMarker activating the report.
     */
    public void vaticanReport(int popeSpace) {
        for(FaithTrack faithTrack : faithTracks) {
            int faithMaker = faithTrack.getFaithMarker();
            // for each faithTrack, activates or not Pope's Favours, from the last reported Section to the further Report Section reached
            for (int j = lastPopeSpace + 1; j <= popeSpace; j++) {
                for (int[] section : reportSection) {
                    if (j >= section[0] && j <= section[1]) {
                        if (faithMaker >= reportSection[j][0])
                            faithTrack.givePopeFavour(j, reportSection[j][2]);
                    }
                }
            }
        }
        lastPopeSpace = popeSpace;
    }

    /**
     * Evaluates the total of Victory Points obtained by a Player based on the position reached by its FaithMarker.
     * @param faithID the identifier of the FaithTrack of the Player to whom counting points.
     * @return the total Victory Points obtained by a Player from the Faith Track.
     */
    public int getPoints(int faithID) {
        FaithTrack faithTrack = faithTracks.get(faithID);
        int faithMarker = faithTrack.getFaithMarker();
        int points = 0;
        for(int index=0; index<=faithMarker; index++) {
            points += track[index];
        }
        points += faithTrack.countFavors();
        return points;
    }

    /**
     * Returns a specified FaithTrack, given its index.
     * @param index the index of the FaithTrack in the list of FaithTracks
     * @return the specified FaithTrack
     */
    public FaithTrack getFaithTrack (int index) {
        return faithTracks.get(index);
    }

    /**
     * Signals the end of the Game to all Players' FaithTracks.
     */
    public void endGame() {
        for(FaithTrack track : faithTracks) {
            track.endGame();
        }
    }
}
