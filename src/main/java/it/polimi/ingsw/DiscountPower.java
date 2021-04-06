package it.polimi.ingsw;

/**
 * Leader Card special ability giving the player discounts of resources when buying Development Cards from the Market.
 * @author Alessandro Mercurio
 */

public class DiscountPower implements Power {
    private ResourcePack discount;

    /**
     * The activation of this Power add the discount granted by the Leader to the set of discounts of the Player.
     * @param board the Player's PlayerBoard, where all the discounts are registered.
     */
    public void activate(PlayerBoard board) {
        board.addDiscount(discount);
    }
}
