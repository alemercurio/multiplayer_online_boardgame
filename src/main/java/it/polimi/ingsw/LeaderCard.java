package it.polimi.ingsw;

/**
 * Class to represent Leader Cards.
 * @author Alessandro Mercurio
 */

public class LeaderCard extends Card {
    private ResourcePack reqResources;
    private ColorPack reqDevCards;
    private boolean isActive;
    private Power power;

    /**
     * Play the Leader and set its status as active, meaning its special ability can be used for the rest of the game.
     */
    public void activate() {
        isActive = true;
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

    /**
     * Return True if the LeaderCard has been played, False if not.
     * @return a boolean representing the current status.
     */
    public boolean getStatus() {
        return isActive;
    }

}
