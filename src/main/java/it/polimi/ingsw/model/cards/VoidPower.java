package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.controller.PlayerBoard;
import it.polimi.ingsw.model.resources.Resource;

/**
 * Leader Card special ability to transform VOID resources gained
 * from the Market into a specific Resource.
 * @author Alessandro Mercurio
 */
public class VoidPower implements Power {
    private final Resource resource;

    /**
     * Constructs a VoidPower that grants the ability to convert
     * VOID resources into the given one.
     * @param resource the Resource obtainable from converting VOID ones.
     */
    public VoidPower(Resource resource) {
        this.resource = resource;
    }

    /**
     * Returns the resource which white marbles from the market can be converted into.
     * @return the resource associated with the current power.
     */
    public Resource getResource() {
       return this.resource;
    }

    /**
     * The activation of this Power grants the ability to exchange
     * VOID resources gained from the Market to another non-special one.
     * @param board the Player's PlayerBoard.
     */
    public void activate(PlayerBoard board) {
        board.addWhite(this.resource);
    }
}
