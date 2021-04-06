package it.polimi.ingsw;

/**
 * Leader Card special ability giving the player additional depots for Resources.
 * @author Alessandro Mercurio
 */

public class StockPower implements Power {
    private int size;
    private Resource type;
    private int used;

    /**
     * The activation of this Power starts the process to add the Resource depots given by the Leader to the full set of depots available for the Player
     * @param board the Player's PlayerBoard.
     */
    public void activate(PlayerBoard board) {
        board.addLeaderStock(this);
    }

    /**
     * Return the available Resources stocked in the Leader additional depots.
     * @return the ResourcePack corresponding to the stocked Resources.
     */
    public ResourcePack getResources() {
        ResourcePack output = new ResourcePack();
        output.add(type, used);
        return output;
    }

    /**
     * If the pack is correct, add the amount of Resources of the right type into the depots of the Leader.
     * @param pack the ResourcePack to add in the depots.
     */
    public void addResources(ResourcePack pack) {
        boolean correct = true;
        int toAdd = pack.get(type);

        for(Resource resource : Resource.values()) {
            if(resource!=type && pack.get(resource)!=0) {
                correct = false;
            }
        }

        if(correct) {
            if(toAdd < (size - used)) {
                used = used + toAdd;
            }
        }
    }

    /**
     * If there are enough Resources, consume the amount specified in the input ResourcePack.
     * @param pack the ResourcePack to consume from the depots.
     */
    public void consume(ResourcePack pack) {
        ResourcePack available = new ResourcePack();
        available.add(type, used);
        if (available.isConsumable(pack)) {
            used = used - pack.get(type);
        }

    }
}
