package it.polimi.ingsw;

import java.io.Serializable;

/**
 * Abstract class to generalize Leader Cards and Development Cards.
 * @author Alessandro Mercurio
 */
public abstract class Card implements Serializable {
    private final int points;

    /**
     * Constructs a Card with the given Victory Points.
     * @param points the amount of Victory Points associated with the Card.
     */
    public Card(int points) {
        this.points = points;
    }

    /**
     * Returns the number of Victory Points given by the Card at the end of the game.
     * @return the number of Victory Points
     */
    public int getPoints() {
        return this.points;
    }
}
