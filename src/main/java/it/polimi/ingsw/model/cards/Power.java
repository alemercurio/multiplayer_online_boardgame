package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.controller.PlayerBoard;

/**
 * Interface for all type of Leader Card special abilities, sharing the possibility to be activated.
 * @author Alessandro Mercurio
 */
public interface Power {
    /**
     * The Power gets activated, and operates its effect over the Player's Board.
     * @param board the Player's PlayerBoard.
     */
    void activate(PlayerBoard board);
}
