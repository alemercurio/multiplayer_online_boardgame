package it.polimi.ingsw.cards;

import it.polimi.ingsw.PlayerBoard;
import it.polimi.ingsw.supply.Resource;

/**
 * Leader Card special ability giving the player an additional depot for Resources.
 * @author Alessandro Mercurio
 */
public class StockPower implements Power {
    private final int size;
    private final Resource type;

    /**
     * Constructs a StockPower with the ability to store at most the given amount
     * of the specified Resource.
     * @param size the size of the depot.
     * @param type the Resource to contain.
     */
    public StockPower(int size, Resource type) {
        this.size = size;
        this.type = type;
    }

    /**
     * The activation of this Power grants an additional depot
     * to the ones available for the Player.
     * @param board the Player's PlayerBoard.
     */
    public void activate(PlayerBoard board) {
        board.addLeaderStock(this);
    }

    /**
     * Returns the type of Resource that the current Stock is able to store.
     * @return the resource containable into the current Stock.
     */
    public Resource getType() {
        return this.type;
    }

    /**
     * Returns the maximum amount of resources containable in the
     * additional depot granted by the activation of the current power.
     * @return the size of the stock given by the current power.
     */
    public int getLimit() { return this.size; }
}
