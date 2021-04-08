package it.polimi.ingsw;

import java.util.LinkedList;
import java.util.List;

/**
 * Immutable class to represent Leader Cards.
 * @author Alessandro Mercurio
 */
public class LeaderCard extends Card {
    final private ResourcePack reqResources;
    final private ColorPack reqDevCards;
    final private Power power;

    /**
     * Constructs a LeaderCard with the given parameters.
     * @param reqResources the requirement in terms of Resources.
     * @param reqDevCards the requirement in terms of DevelopmentCards.
     * @param power the Power granted by the LeaderCard.
     */
    public LeaderCard(ResourcePack reqResources, ColorPack reqDevCards, Power power) {
        // TODO: il costruttore è opportuno che sia private.
        this.reqResources = reqResources.getCopy();
        this.reqDevCards = reqDevCards.getCopy();

        // note: powers may be mutable objects.
        this.power = power;
    }

    /**
     * Returns a List of all the available LeaderCards.
     * @return a list of all the LeaderCards.
     */
    public static List<LeaderCard> getLeaderCardDeck() {
        // TODO: aggiungere il codice relativo al caricamento delle carte.
        ResourcePack rp = new ResourcePack(1);
        ColorPack cp = new ColorPack();
        cp.addColor(Color.GREEN, 1);
        Power pow = new DiscountPower(rp);

        List<LeaderCard> leaderCardDeck = new LinkedList<>();
        leaderCardDeck.add(new LeaderCard(rp, cp, pow));
        return leaderCardDeck;
    }

    /**
     * Activates the Leader on the given PlayerBoard;
     * its power is made available to the corresponding player for the rest of the game.
     * @param board the Player's PlayerBoard.
     */
    public void activate(PlayerBoard board) {
        this.power.activate(board);
    }

    /**
     * Returns the pack of Resources needed to be in Player's Storage to play the Leader.
     * @return a ResourcePack representing the required amount of each Resource.
     */
    public ResourcePack getReqResources() {
        return this.reqResources.getCopy();
    }

    /**
     * Returns the Development Cards needed to be on the Player's board to play the Leader.
     * @return a ColorPack representing the amount, color and level of the required Cards.
     */
    public ColorPack getReqDevCards() {
        return this.reqDevCards.getCopy();
    }
}