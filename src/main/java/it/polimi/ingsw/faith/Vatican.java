package it.polimi.ingsw.faith;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.Game;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent the game feature of the Vatican, meaning the set of Faith Tracks,
 * Report Sections, Pope Spaces, Pope's Favours.
 * It is shared by all the Players in a game.
 * @author Patrick Niantcho
 */
public class Vatican {

    /**
     * Represents a single space in the FaithTrack.
     * Each space can assign victory points or activate a vatican report.
     */
    protected static class Space {
        private final int points;
        private final int popeSpace;

        /**
         * Constructs a space that assigns the given victory points.
         * @param points the victory points associated with the space.
         */
        protected Space(int points) {
            this.points = points;
            this.popeSpace = -1;
        }

        /**
         * Constructs a space that assigns the given victory points
         * and activates the vatican report with the specified index.
         * @param points the victory points associated with the space.
         * @param popeSpace the index of the vatican report section that the space is able to activate.
         */
        protected Space(int points,int popeSpace) {
            this.points = points;
            this.popeSpace = popeSpace;
        }

        /**
         * Returns the amounts of victory points assignable by the current Space.
         * @return the victory points granted by the current Space.
         */
        protected int getPoints() {
            return this.points;
        }

        /**
         * Tests if the current space activates a vatican report.
         * @return true if the current space should activate a vatican report, false otherwise.
         */
        protected boolean needReport() {
            return (this.popeSpace >= 0);
        }

        /**
         * Returns the index of the vatican report section that the current space
         * is able to activate; if no report is assigned to the current space returns a
         * negative index.
         * @return the index of the vatican report section to activate or a negative value.
         */
        protected int popeSpace() {
            return this.popeSpace;
        }
    }

    enum State {
        AVAILABLE,GOT,LOST
    }

    /**
     * Represents a vatican report section.
     * Each PopeSpace can assign a specific amount of victory points
     * and corresponds to a sequence of Spaces in the FaithTrack.
     */
    protected static class ReportSection {
        private final int firstSpace;
        private final int lastSpace;
        private final int points;

        private transient State state;

        /**
         * Constructs a PopeSpace that assigns the given amount of victory points
         * and corresponds to the sequence of Spaces within the specified indexes.
         * @param firstSpace the first Space in the sequence.
         * @param lastSpace the last Space in the sequence.
         * @param points the amount of victory points that the report section can assign.
         */
        protected ReportSection(int firstSpace, int lastSpace, int points) {
            this.firstSpace = firstSpace;
            this.lastSpace = lastSpace;
            this.points = points;

            this.state = State.AVAILABLE;
        }

        /**
         * Tests if the current PopeSpace has already been activated through a vatican report.
         * @return false if the current PopeSpace has been activated, true otherwise.
         */
        protected synchronized boolean toReport() {
            return (this.state == State.AVAILABLE);
        }

        /**
         * Sets the current PopeSpace as reported (so it will not be activated again).
         */
        private synchronized void setReported() {
            this.state = State.GOT;
        }

        /**
         * Returns the amount of victory points that the current PopeSpace can assign.
         * @return the victory points assignable by the PopeSpace.
         */
        protected int getPoints() {
            return this.points;
        }

        /**
         * Returns the index of the first Space associated with the current PopeSpace.
         * @return the index of the first Space of the sequence.
         */
        protected int getFirstSpace() {
            return this.firstSpace;
        }

        /**
         * Returns the index of the last Space associated with the current PopeSpace.
         * @return the index of the last Space of the sequence.
         */
        protected int getLastSpace() {
            return this.lastSpace;
        }


        /**
         * Sets the state of the current ReportSection to the one given.
         * @param state the new state for the current ReportSection.
         */
        protected void setState(State state) {
            this.state = state;
        }

        /**
         * Returns the state of the current ReportSection.
         * @return the Vatican.State of the current ReportSection.
         */
        protected State getState() {
            return this.state;
        }

        /**
         * Returns a copy of the current ReportSection initialized as AVAILABLE.
         * @return a copy of the current ReportSection.
         */
        protected ReportSection getCopy() {
            return new ReportSection(this.firstSpace,this.lastSpace,this.points);
        }
    }

    private transient final Game game;
    private final Space[] track;
    private final ReportSection[] reportSections;
    private transient final List<FaithTrack> faithTracks;

    /**
     * Constructs the Vatican with all of its attributes.
     * @param game the Game that the Vatican is associated with.
     * @param filePath the path of the Json file representing the structure of the FaithTrack
     */
    public Vatican(Game game,String filePath) {
        this.game = game;
        this.faithTracks = new ArrayList<>();

        File file = new File(filePath);
        ReportSection[] gotReportSections;
        Space[] gotTrack;

        try {
            FileReader fr = new FileReader(file);
            JsonObject vaticanData = JsonParser.parseReader(fr).getAsJsonObject();

            Gson parser = new Gson();
            gotTrack = parser.fromJson(vaticanData.get("track"), Space[].class);
            gotReportSections = parser.fromJson(vaticanData.get("popeSpaces"), ReportSection[].class);

            fr.close();
        } catch (IOException e) {
            gotTrack = null;
            gotReportSections = null;
        }

        reportSections = gotReportSections;
        this.track = gotTrack;
    }

    /**
     * Constructs a FaithTrack associated with the current Vatican.
     * @return a FaithTrack.
     */
    public FaithTrack getFaithTrack(int playerID) {
        FaithTrack ft = new FaithTrack(playerID,this,List.of(this.track),List.of(this.reportSections));
        this.faithTracks.add(ft);
        return ft;
    }

    /**
     * Makes all the FaitTracks advance of the specified amount of steps
     * except the one with the given identifier; this method should be called after a
     * Player has wasted some resources.
     * @param track the identifier of the FaithTrack of the Player who discarded Resources.
     * @param num the number of Resources discarded by the Player.
     */
    public void wastedResources(int track, int num) {
        // Makes other Players advance and update the furthestSpaceReached by any Player's Faith Marker.
        for(FaithTrack faithTrack : faithTracks) {
            if (faithTrack.getID() != track) {
                faithTrack.advance(num);
            }
        }
    }

    /**
     * Activates a Vatican Report and gives the Pope's Favour to Players
     * whose FaithMarker is in the reported section.
     * @param popeSpace the PopeSpace that has been activated.
     */
    public void vaticanReport(int popeSpace) {
        for(FaithTrack faithTrack : faithTracks) {
            faithTrack.PopeFavour(popeSpace);
        }
    }

    /**
     * Signals to the corresponding Game that a FaithMarker has reached the end of the
     * FaithTrack, meaning the Game has ended.
     */
    public void endGame() {
        this.game.endGame();
    }
}
