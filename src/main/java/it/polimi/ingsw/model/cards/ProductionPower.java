package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.controller.PlayerBoard;
import it.polimi.ingsw.model.resources.Production;

/**
 * Leader Card special ability giving the player an additional production power.
 * @author Alessandro Mercurio
 */
public class ProductionPower implements Power {
    private final Production production;

    /**
     * Constructs a ProductionPower with the given production.
     * @param production the production that the power is able to give.
     */
    public ProductionPower(Production production) {
        // Because Production objects are immutable, it is not necessary to make a copy.
        this.production = production;
    }

    /**
     * Returns the production granted by the current ProductionPower.
     * @return the Production given by the current Power.
     */
    public Production getProduction() {
        // Because Production objects are immutable, it is not necessary to make a copy.
        return this.production;
    }

    /**
     * The activation of this Power makes its Production available for the Player.
     * @param board the Player's PlayerBoard.
     */
    public void activate(PlayerBoard board) {
        board.addProduction(this.production);
    }
}
