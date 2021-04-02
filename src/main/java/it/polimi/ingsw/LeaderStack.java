package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent the set of Leader Cards available for a given Player in a game.
 * @author Alessandro Mercurio
 */

public class LeaderStack {
    private ArrayList<LeaderCard> activeLeaders;
    private ArrayList<LeaderCard> inactiveLeaders;

    /**
     * Play LeaderCard at specified index, causing its Power to activate.
     * @param index the index of the LeaderCard to play in the list of non-played Leaders.
     * @param board the Player's PlayerBoard.
     */
    public void activate(int index, PlayerBoard board) {
        LeaderCard toActivate = inactiveLeaders.get(index);
        activeLeaders.add(toActivate);
        inactiveLeaders.remove(toActivate);
        toActivate.activate(board);
    }

    /**
     * Discard LeaderCard at specified index.
     * @param index the index of the LeaderCard to discard in the list of non-played Leaders.
     */
    public void discard(int index) {
        inactiveLeaders.remove(index);
    }

    /**
     * Get the played LeaderCard at specified index.
     * @param index the index of the LeaderCard in the list of played Leaders.
     * @return the LeaderCard requested.
     */
    public LeaderCard getActiveLeader(int index) {
        return activeLeaders.get(index);
    }

    /**
     * Get the non-played LeaderCard at specified index.
     * @param index the index of the LeaderCard in the list of non-played Leaders.
     * @return the LeaderCard requested.
     */
    public LeaderCard getInactiveLeader(int index) {
        return inactiveLeaders.get(index);
    }

    /**
     * Get a List with all the available Leaders for the Player, except the already discarded ones.
     * @return a List with both played and non-played Leaders.
     */
    public List<LeaderCard> getAllLeaders() {
        ArrayList<LeaderCard> allLeaders = new ArrayList<LeaderCard>();
        allLeaders.addAll(activeLeaders);
        allLeaders.addAll(inactiveLeaders);
        return allLeaders;
    }

    /**
     * Calculate the amount of Victory Points that the Player get from the played Leaders at the end of the game.
     * @return the number of Victory Points.
     */
    public int getPoints() {
        int totalPoints = 0;
        for(LeaderCard leader : activeLeaders) {
            totalPoints = totalPoints + leader.getPoints();
        }
        return totalPoints;
    }
}
