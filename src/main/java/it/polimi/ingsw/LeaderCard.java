package it.polimi.ingsw;

/**
 * Immutable class to represent Leader Cards.
 * @author Alessandro Mercurio
 */

public class LeaderCard extends Card {
    final private ResourcePack reqResources;
    final private ColorPack reqDevCards;
    final private Power power;

    /**
     * Play the Leader and set its status as active, meaning its special ability can be used for the rest of the game.
     * @param board the Player's PlayerBoard.
     */
    public void activate(PlayerBoard board) {
        power.activate(board);
    }

    /**
     * Return the amount of Resources needed to be in Player's depots to play the Leader.
     * @return a ResourcePack representing the required amount of each Resource.
     */
    public ResourcePack getReqResources() {
        ResourcePack needed;
        needed = reqResources.getCopy();
        return needed;
    }

    /**
     * Return the Cards needed to be on the Player's board to play the Leader.
     * @return a ColorPack representing the required amount, color and level of the needed Cards.
     */
    public ColorPack getReqDevCards() {
        ColorPack needed;
        needed = reqDevCards.getCopy();
        return needed;
    }
}
