package it.polimi.ingsw.cards;

import it.polimi.ingsw.supply.Production;

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
        // Because Production objects are immutable it is not necessary to make a copy.
        this.production = production;
    }

    /**
     * The activation of this Power makes its Production available for the Player.
     * @param board the Player's PlayerBoard.
     */
    public void activate(PlayerBoard board) {
        board.addProduction(this.production);
    }
}
