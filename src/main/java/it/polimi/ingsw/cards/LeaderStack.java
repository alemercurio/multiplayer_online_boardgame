package it.polimi.ingsw.cards;

import it.polimi.ingsw.PlayerBoard;

import java.util.LinkedList;
import java.util.List;

/**
 * The LeaderStack class represents the set of Leader Cards available for a specific Player in a game;
 * it also manages the status (played/playable) of each Leader Card.
 * @author Alessandro Mercurio
 */
public class LeaderStack {
    private final List<LeaderCard> activeLeaders;
    private final List<LeaderCard> inactiveLeaders;

    /**
     * Constructs an empty LeaderStack.
     */
    public LeaderStack() {
        this.activeLeaders = new LinkedList<>();
        this.inactiveLeaders = new LinkedList<>();
    }

    /**
     * Adds all the LeaderCards in the given list to the current LeaderStack;
     * new LeaderCards are set to inactive.
     * @param leaders the list of LeaderCard to add.
     */
    public void addLeaders(List<LeaderCard> leaders)
    {
        // Because LeaderCard objects are immutable no copy is needed.
        this.inactiveLeaders.addAll(leaders);
    }

    /**
     * Plays the LeaderCard with the specified index, causing its Power to activate.
     * If the given index has not a corresponding LeaderCard nothing happens.
     * @param index the index of the LeaderCard to play in the list of non-played Leaders.
     * @param board the Player's PlayerBoard.
     */
    public void activate(int index, PlayerBoard board) {
        if (index >= 0 && index < inactiveLeaders.size()) {
            LeaderCard toActivate = inactiveLeaders.remove(index);
            activeLeaders.add(toActivate);
            toActivate.activate(board);
        }
    }

    /**
     * Discards the non-played LeaderCard at specified index.
     * If the given index has not a corresponding LeaderCard nothing happens.
     * @param index the index of the LeaderCard to discard in the list of non-played Leaders.
     */
    public void discard(int index)
    {
        if (index >= 0 && index < inactiveLeaders.size())
        {
            inactiveLeaders.remove(index);
        }
    }

    /**
     * Gets the played LeaderCard at specified index.
     * If the given index has not a corresponding LeaderCard returns null.
     * @param index the index of the LeaderCard in the list of played Leaders.
     * @return the LeaderCard requested or null.
     */
    public LeaderCard getActiveLeader(int index) {
        if (index >= 0 && index < activeLeaders.size()) {
            // Because LeaderCard objects are immutable they can be shared.
            return activeLeaders.get(index);
        } else return null;
    }

    /**
     * Returns a list of all the active Leaders in the LeaderStack.
     * @return a list of all active Leaders.
     */
    public List<LeaderCard> getActiveLeader() {
        // Because LeaderCard objects are immutable they can be shared.
        return new LinkedList<LeaderCard>(this.activeLeaders);
    }

    /**
     * Gets the non-played LeaderCard at specified index.
     * If the given index has not a corresponding LeaderCard returns null.
     * @param index the index of the LeaderCard in the list of non-played Leaders.
     * @return the LeaderCard requested or null.
     */
    public LeaderCard getInactiveLeader(int index) {
        if (index >= 0 && index < inactiveLeaders.size()) {
            // Because LeaderCard objects are immutable they can be shared.
            return inactiveLeaders.get(index);
        } else return null;
    }

    /**
     * Returns a list of playable Leaders in the current LeaderStack.
     * @return a list of non-played Leaders.
     */
    public List<LeaderCard> getInactiveLeader() {
        // Because LeaderCard objects are immutable they can be shared.
        return new LinkedList<LeaderCard>(this.inactiveLeaders);
    }

    /**
     * Calculates the amount of Victory Points
     * gained by playing leader cards during the game.
     * @return the number of Victory Points.
     */
    public int getPoints()
    {
        int totalPoints = 0;
        for (LeaderCard leader : activeLeaders)
            totalPoints = totalPoints + leader.getPoints();
        return totalPoints;
    }
}
