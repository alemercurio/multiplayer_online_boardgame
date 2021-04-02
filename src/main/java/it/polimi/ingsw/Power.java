package it.polimi.ingsw;

/**
 * Interface for all type of Leader Card special abilities, sharing the possibility to be activated.
 * @author Alessandro Mercurio
 */

public interface Power {
    /**
     * The Power gets activated, and operates its effect over the board of the Player.
     * @param board the Player's PlayerBoard.
     */
    public void activate(PlayerBoard board);
}
