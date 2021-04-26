package it.polimi.ingsw.faith;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.Game;
import it.polimi.ingsw.MessageParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Class to represent the game feature of the Vatican, meaning the set of Faith Tracks,
 * Report Sections, Pope Spaces, Pope's Favours.
 * It is shared by all the Players in a game.
 * Vatican can execute the following commands:
 * <pre>
 * <ul>
 *  <li>wasted(IDFaithTrack,numberOfWastedResources</li>
 *  <li>pope(index)</li>
 *  <li>restart</li>
 *  <li>stop</li>
 *  <li>endGame</li>
 * </ul>
 * </pre>
 * @author Patrick Niantcho
 */
public class Vatican extends Thread{

    /**
     * Represents a single space in the FaithTrack.
     * Each space can assign victory points or activate a vatican report.
     */
    protected class Space
    {
        private final int points;
        private final int popeSpace;

        /**
         * Constructs a space that assigns the given victory points.
         * @param points the victory points associated with the space.
         */
        protected Space(int points)
        {
            this.points = points;
            this.popeSpace = -1;
        }

        /**
         * Constructs a space that assigns the given victory points
         * and activates the vatican report with the specified index.
         * @param points the victory points associated with the space.
         * @param popeSpace the index of the vatican report section that the space is able to activate.
         */
        protected Space(int points,int popeSpace)
        {
            this.points = points;
            this.popeSpace = popeSpace;
        }

        /**
         * Returns the amounts of victory points assignable by the current Space.
         * @return the victory points granted by the current Space.
         */
        protected int getPoints() { return this.points; }

        /**
         * Test if the current space activates a vatican report.
         * @return true if the current space should activate a vatican report, false otherwise.
         */
        protected boolean needReport() { return (this.popeSpace >= 0); }

        /**
         * Returns the index of the vatican report section that the current space
         * is able to activate; if no report is assigned to the current space returns a
         * negative index.
         * @return the index of the vatican report section to activate or a negative value.
         */
        protected int popeSpace() { return this.popeSpace; }
    }

    /**
     * Represents a vatican report section.
     * Each PopeSpace can assign a specific amount of victory points
     * and corresponds to a sequence of Spaces in the FaithTrack.
     */
    protected class PopeSpace
    {
        private final int firstSpace;
        private final int lastSpace;
        private final int points;

        private transient boolean toReport;

        /**
         * Constructs a PopeSpace that assigns the given amount of victory points
         * and corresponds to the sequence of Spaces within the specified indexes.
         * @param firstSpace the first Space in the sequence.
         * @param lastSpace the last Space in the sequence.
         * @param points the amount of victory points that the report section can assign.
         */
        protected PopeSpace(int firstSpace, int lastSpace, int points)
        {
            this.firstSpace = firstSpace;
            this.lastSpace = lastSpace;
            this.points = points;

            this.toReport = true;
        }

        /**
         * Tests if the current PopeSpace has already been activated through a vatican report.
         * @return false if the current PopeSpace has been activated, true otherwise.
         */
        protected synchronized boolean toReport() { return this.toReport; }

        /**
         * Sets the current PopeSpace as reported (so it will not be activated again).
         */
        private synchronized void setReported() { this.toReport = false; }

        /**
         * Returns the amount of victory points that the current PopeSpace can assign.
         * @return the victory points assignable by the PopeSpace.
         */
        protected int getPoints() { return this.points; }

        /**
         * Returns the index of the first Space associated with the current PopeSpace.
         * @return the index of the first Space of the sequence.
         */
        protected int getFirstSpace() { return this.firstSpace; }

        /**
         * Returns the index of the last Space associated with the current PopeSpace.
         * @return the index of the last Space of the sequence.
         */
        protected int getLastSpace() { return this.lastSpace; }
    }

    private transient final Game game;
    private final Space[] track;
    private final PopeSpace[] popeSpaces;
    private transient final List<FaithTrack> faithTracks;

    private final Queue<String> notification;

    /**
     * Constructs the Vatican with all of its attributes.
     * @param game the Game that the Vatican is associated with.
     * @param filePath the path of the Json file representing the structure of the FaithTrack
     */
    public Vatican(Game game,String filePath)
    {
        this.game = game;
        this.faithTracks = new ArrayList<FaithTrack>();
        this.notification = new LinkedList<String>();

        File file = new File(filePath);
        PopeSpace[] gotPopeSpaces;
        Space[] gotTrack;

        try {
            FileReader fr = new FileReader(file);
            JsonObject vaticanData = JsonParser.parseReader(fr).getAsJsonObject();

            Gson parser = new Gson();
            gotTrack = parser.fromJson(vaticanData.get("track"), Space[].class);
            gotPopeSpaces = parser.fromJson(vaticanData.get("popeSpaces"), PopeSpace[].class);

            fr.close();
        } catch (IOException e) {
            gotTrack = null;
            gotPopeSpaces = null;
        }

        popeSpaces = gotPopeSpaces;
        this.track = gotTrack;
    }

    /**
     * Constructs a FaithTrack associated with the current Vatican.
     * @return a FaithTrack.
     */
    public FaithTrack getFaithTrack()
    {
        int ID = this.faithTracks.size();
        FaithTrack ft = new FaithTrack(ID,this,List.of(this.track),List.of(this.popeSpaces));
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
        // makes other Players advance and update the furthestSpaceReached by any Player's Faith Marker
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
            faithTrack.PopeFavour(this.popeSpaces[popeSpace]);
        }
    }

    // Runtime of Vatican management

    /**
     * Enqueues the given instruction for execution in the current Vatican.
     * @param str the String representing the instruction to execute.
     */
    public synchronized void report(String str)
    {
        this.notification.add(str);
        notifyAll();
    }

    /**
     * Dequeues an instruction from the current Vatican.
     * @return the String representing the instruction to execute.
     */
    private synchronized String getReport()
    {
        while(this.notification.isEmpty())
        {
            try {
                wait();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        return this.notification.poll();
    }

    @Override
    public void run()
    {
        MessageParser cmd = new MessageParser();
        while(!cmd.getOrder().equals("stop"))
        {
            cmd.parse(this.getReport());
            switch(cmd.getOrder())
            {
                case "wasted":
                    if(cmd.getNumberOfParameters() == 2)
                    {
                        this.wastedResources(cmd.getIntParameter(0),cmd.getIntParameter(1));
                    }
                    break;

                case "pope":
                    if(cmd.getNumberOfParameters() == 1)
                    {
                        this.vaticanReport(cmd.getIntParameter(0));
                    }
                    break;

                case "restart":
                    this.faithTracks.clear();
                    for(PopeSpace ps : this.popeSpaces) ps.toReport = true;
                    this.notification.clear();
                    break;

                case "endGame":
                    this.game.endGame();
                    break;
            }
        }
    }
}
