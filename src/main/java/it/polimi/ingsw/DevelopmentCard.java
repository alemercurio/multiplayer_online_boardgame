package it.polimi.ingsw;

/**
 * Immutable class to represent Development Cards.
 * @author Alessandro Mercurio
 */

public class DevelopmentCard extends Card {
    final private ResourcePack cost;
    final private Color color;
    final private int level;
    final private Production production;

    /**
     * Getter for the cost, in terms of Resources, to buy the Card.
     * @return the ResourcePack representing the cost.
     */
    public ResourcePack getCost() {
        ResourcePack needed;
        needed = reqResources.getCopy();
        return needed;
    }

    /**
     * Getter for the Production characterizing the Development Card.
     * @return the Production that the Card makes available for the player.
     */
    public Production getProduction() {
        return production;
    }

    /**
     * Getter for the color of the Card.
     * @return the Color of the Card.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Getter for the level of the Card.
     * @return the Color of the Card.
     */
    public int getLevel() {
        return level;
    }
}
