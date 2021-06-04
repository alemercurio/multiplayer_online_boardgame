package it.polimi.ingsw.model.vatican;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.*;

/**
 * Class to represent the Faith Track status for a specific Player,
 * meaning the position of its Faith Marker and the Pope's Favors he did and didn't get.
 * @author Patrick Niantcho
 */
public class FaithTrack {
    private transient final int faithTrackID;
    private transient int faithMarker;
    private transient final Vatican vatican;
    private final ArrayList<Vatican.Space> track;
    private final ArrayList<Vatican.ReportSection> reportSections;

    private transient boolean end;

    /**
     * Constructs a FaithTrack.
     * @param faithTrackID the unique identifier of the FaithTrack.
     * @param vatican the Vatican associated with the FaithTrack.
     * @param track the List of Spaces which the FaithTrack is composed.
     * @param reportSections the List of PopeSpaces in the FaithTrack.
     */
    protected FaithTrack(int faithTrackID,Vatican vatican,List<Vatican.Space> track,List<Vatican.ReportSection> reportSections) {
        this.faithTrackID = faithTrackID;
        this.faithMarker = 0;
        this.vatican = vatican;

        this.track = new ArrayList<>(track);
        this.reportSections = new ArrayList<>();
        for(Vatican.ReportSection ps : reportSections) {
            this.reportSections.add(ps.getCopy());
        }

        this.end = (this.track.size() == 1);
    }

    /**
     * Returns the position of the Faith Marker in the current FaithTrack,
     * meaning the index of the Space it is currently in.
     * @return the current position of the Faith Marker in the current FaithTrack.
     */
    public int getFaithMarker() {
        return this.faithMarker;
    }

    /**
     * Returns the identifier of the current FaithTrack within the list of all FaithTracks.
     * @return the identifier of the current FaithTrack.
     */
    public int getID() {
        return faithTrackID;
    }

    /**
     * Makes the Faith Marker advance of one single space.
     * If the new current space is a PopeSpace, reports to Vatican.
     */
    private void advance() {
        if(!end) {
            this.faithMarker++;
        }

        Vatican.Space space = this.track.get(this.faithMarker);
        if(space.needReport()) {
            // Activates the Vatican report.
            this.vatican.vaticanReport(space.popeSpace());
        }

        if(!end && this.faithMarker == this.track.size() - 1) {
            // Signals to Vatican that the current FaithTrack has reached the end.
            this.vatican.endGame();
            this.end = true;
        }
    }

    /**
     * Makes the FaithMarker advance of the specified amount of spaces.
     * Reports to Vatican if one or more PopeSpaces had been reached.
     * @param steps the number of spaces of the movement.
     */
    public void advance(int steps) {
        for(int i = 0; i < steps; i++)
            this.advance();
        this.vatican.update(this);
    }

    /**
     * Signals to Vatican that the Player associated with the current FaithTrack
     * has wasted the specified amount of resources.
     * @param amount the amount of wasted resources.
     */
    public void wastedResources(int amount) {
        this.vatican.wastedResources(this.faithTrackID,amount);
    }

    /**
     * Assigns a Pope Favor to the Player if its FaithMarker is within or beyond
     * the associated section in the FaithTrack. If not, the section is marked as lost.
     * @param index the index of the Pope Favour assignable to the Player.
     */
    public void PopeFavour(int index) {

        Vatican.ReportSection rs = this.reportSections.get(index);

        if(this.faithMarker >= rs.getFirstSpace())
            rs.setState(Vatican.State.GOT);
        else rs.setState(Vatican.State.LOST);

        this.vatican.update(this);
    }

    /**
     * Evaluates the number of Victory Points obtained by the Player through Pope Favours.
     * @return the total points gained from Pope Favours.
     */
    public int countFavors() {
        return this.reportSections.stream()
                .filter(e -> e.getState().equals(Vatican.State.GOT)).mapToInt(Vatican.ReportSection::getPoints)
                .reduce(Integer::sum).orElse(0);
    }

    /**
     * Returns the total Victory Points obtained by the Player through the FaithTrack mechanic.
     * @return the total of points obtained.
     */
    public int getTotalPoints() {
        int points = this.countFavors();
        for(int index = 0; index <= this.faithMarker; index++) {
            points = points + this.track.get(index).getPoints();
        }
        return points;
    }

    public String toString() {
        return new Gson().toJson(this);
    }

    public String getConfig() {

        JsonObject faithData = new JsonObject();
        faithData.addProperty("ID",this.faithTrackID);
        faithData.addProperty("faithMarker",this.faithMarker);
        faithData.add("popeFavours", new Gson().toJsonTree(
                this.reportSections.stream().map(Vatican.ReportSection::getState).toArray(),
                Vatican.State[].class));

        return faithData.toString();
    }
}
