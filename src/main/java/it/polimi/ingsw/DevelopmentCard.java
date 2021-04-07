package it.polimi.ingsw;

import java.util.LinkedList;
import java.util.List;

/**
 * Immutable class to represent Development Cards.
 *
 * @author Alessandro Mercurio
 */
public class DevelopmentCard extends Card {
    final private ResourcePack cost;
    final private Color color;
    final private int level;
    final private Production production;

    /**
     * Constructs a DevelopmentCard with the given parameters.
     * @param cost the ResourcePack representing the cost of the card.
     * @param color the Color of the card.
     * @param level the level of the card.
     * @param production the Production associated with the card.
     */
    public DevelopmentCard(ResourcePack cost, Color color, int level, Production production) {
        // TODO: il costruttore è opportuno che sia private.
        this.cost = cost.getCopy();
        this.color = color;
        this.level = level;
        this.production = production;
    }

    /**
     * Returns a List of all the available DevelopmentCards.
     * @return a list of all the DevelopmentCards.
     */
    public static List<DevelopmentCard> getDevelopmentCardDeck() {
        // TODO: aggiungere il codice relativo al caricamento delle carte.
        ResourcePack cost = new ResourcePack(1);
        ResourcePack res = new ResourcePack(0, 1);
        DevelopmentCard dc = new DevelopmentCard(cost, Color.GREEN, 1, new Production(cost, res));

        List<DevelopmentCard> devCardDeck = new LinkedList<>();
        devCardDeck.add(dc);
        return devCardDeck;
    }

    /**
     * Getter for the cost, in terms of Resources, to buy the Card.
     * @return the ResourcePack representing the cost.
     */
    public ResourcePack getCost() {
        return this.cost.getCopy();
    }

    /**
     * Getter for the Production characterizing the Development Card.
     * @return the Production that the Card makes available for the player.
     */
    public Production getProduction() {
        // Because Production objects are immutable they can be shared.
        return this.production;
    }

    /**
     * Getter for the color of the Card.
     * @return the Color of the Card.
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Getter for the level of the Card.
     * @return the level of the Card.
     */
    public int getLevel() {
        return this.level;
    }
}
