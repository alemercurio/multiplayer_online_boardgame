package it.polimi.ingsw;

/**
 * Abstract class to generalize Leader Cards and Development Cards.
 * @author Alessandro Mercurio
 */

public abstract class Card {
    protected int points;

    /**
     * Return the number of Victory Points given by the Card at the end of the game.
     * @return the number of Victory Points
     */
    public int getPoints() {
        return points;
    }
}
