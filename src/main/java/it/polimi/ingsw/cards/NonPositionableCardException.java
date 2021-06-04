package it.polimi.ingsw.cards;

/**
 * Exception thrown when a DevelopmentCard is not positionable in a space of the PlayerBoard,
 * likely because of its level being too high or too low in respect to the already positioned cards.
 * @see DevelopmentCard
 * @author Francesco Tosini
 */
public class NonPositionableCardException extends Exception {
    public NonPositionableCardException() {
        super("Unable to position that card here...");
    }
}
