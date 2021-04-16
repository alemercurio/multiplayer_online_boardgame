package it.polimi.ingsw;

/**
 * Class to represent the Faith Track status for a specific Player, meaning the position of its Faith Marker and the Pope's Favors he did and didn't get.
 * @author Patrick Niantcho
 */
public class FaithTrack {
    private int faithMarker;
    private final boolean[] popeSpaces;
    private final Vatican vatican;
    private final int faithTrackID;
    private final int[] popeFavors;
    private int totalPoints = 0;

    /**
     * Constructs a FaithTrack.
     * @param popeSpaces   the spaces that are Pope spaces in the track.
     * @param vatican      the unique Vatican.
     * @param faithTrackID the identifier of the FaithTrack, to distinguish it from the others.
     * @param popeFavors  for each Pope's Favour, whether the Player got it (true) or not (false).
     */
    public FaithTrack(boolean[] popeSpaces, Vatican vatican, int faithTrackID, int[] popeFavors) {
        this.faithMarker = 0;
        this.popeSpaces = popeSpaces;
        this.vatican = vatican;
        this.faithTrackID = faithTrackID;
        this.popeFavors = popeFavors;
    }

    /**
     * Returns the position of the Faith Marker in the Faith Track, meaning the index of the space it is currently in.
     * @return the current position of the Faith Marker.
     */
    public int getFaithMarker() {
        return faithMarker;
    }

    /**
     * Returns the identifier of the Faith Track within the list of all FaithTracks.
     * @return the identifier.
     */
    public int getID() {
        return faithTrackID;
    }

    /**
     * Makes the Faith Marker advance of one single space. If the new current space is a Pope Space, reports to Vatican.
     * @return the new current space occupied by the FaithMarker after the movement.
     */
    public int advance() {
        faithMarker++;
        if(popeSpaces[faithMarker]) {
            vatican.vaticanReport(faithMarker);
        }
        return faithMarker;
    }

    /**
     * Makes the FaithMarker advance of some spaces. If the new current space is a Pope Space, reports to Vatican.
     * @param steps the number of spaces of the movement.
     * @return the new current space occupied by the FaithMarker after the movement.
     */
    public int advance(int steps) {
        faithMarker += steps;
        if(popeSpaces[faithMarker]) {
            vatican.vaticanReport(faithMarker);
        }
        return faithMarker;
    }

    /**
     * Assigns a Pope Favor to the Player.
     * @param index the Pope Favour assigned to the Player.
     */
    public void givePopeFavour(int index, int points) {
        popeFavors[index] = points;
    }

    /**
     * Evaluates the number of Victory Points obtained by the Player through Pope Favors.
     * @return the total Pope Favors points obtained.
     */
    public int countFavors() {
        int points = 0;
        for(int i = 0; i <= popeFavors.length; i++) {
            points += popeFavors[i];
        }
        return points;
    }

    /**
     * Signals the end of the Game and calls the evaluation of Victory Points.
     */
    public void endGame() {
        totalPoints = vatican.getPoints(faithTrackID);
    }

    /**
     * Returns the total Victory Points obtained by the Player through the FaithTrack mechanic.
     * @return the total of points obtained.
     */
    public int getTotalPoints() {
        return totalPoints;
    }
}
