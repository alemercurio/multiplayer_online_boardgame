package it.polimi.ingsw;

/**
 * Leader Card special ability;
 * gives the player a discount of resources
 * when buying Development Cards from the Market.
 * @author Alessandro Mercurio
 */
public class DiscountPower implements Power {
    private final ResourcePack discount;

    /**
     * Constructs a DiscountPower that applies a discount
     * equal to the given ResourcePack.
     * @param discount the discount that the power is able to apply.
     */
    public DiscountPower(ResourcePack discount) {
        this.discount = discount.getCopy();
    }

    /**
     * The activation of this Power add the discount granted by the Leader
     * to the set of discounts of the Player.
     * @param board the Player's PlayerBoard, where all the discounts are registered.
     */
    public void activate(PlayerBoard board) {
        board.addDiscount(this.discount);
    }
}
