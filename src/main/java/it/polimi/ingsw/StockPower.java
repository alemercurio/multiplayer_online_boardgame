package it.polimi.ingsw;

/**
 * Leader Card special ability giving the player an additional depot for Resources.
 * @author Alessandro Mercurio
 */
public class StockPower implements Power {
    private final int size;
    private final Resource type;
    private int used;

    // TODO: la dimensione potrebbe essere modificata a posteriori;
    //  ciò può consentire di utilizzare un solo StockPower per modellarne molti...
    //  puoi implementare un metodo merge()...

    /**
     * Constructs a StockPower with the ability to store at most the given amount
     * of the specified Resource.
     * @param size the size of the depot.
     * @param type the Resource to contain.
     */
    public StockPower(int size, Resource type) {
        this.size = size;
        this.type = type;
        this.used = 0;
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
     * Returns the pack of Resources stored in the depot.
     * @return the ResourcePack corresponding to the stocked Resources.
     */
    public ResourcePack getResources() {
        return (new ResourcePack()).add(type, used);
    }

    /**
     * Returns the amount of resources stored in the current depot.
     * The type of resources is left implicit.
     * @return the amount of stored resources.
     */
    public int getUsed() {
        return this.used;
    }

    /**
     * Stores the maximum amount of Resources of the right type,
     * from the given pack, into the depot;
     * the same amount of resources is consumed from the given pack.
     * @param pack the ResourcePack containing the resources to store into the depot.
     */
    public void add(ResourcePack pack) {
        if (this.used < this.size) {
            // Adds all the available resources of the right type.
            this.used = this.used + pack.flush(this.type);
            // Overflow is then collected
            if (this.used > this.size) {
                pack.add(this.type, this.used - this.size);
                this.used = this.size;
            }
        }
    }

    /**
     * If there are enough Resources, consume the amount specified in the input ResourcePack;
     * partial consuming is allowed.
     * @param pack the ResourcePack to consume from the depot.
     * @return the ResourcePack of non-consumable resources.
     */
    public ResourcePack consume(ResourcePack pack) {
        ResourcePack leftToConsume = pack.getCopy();
        if (this.used > 0) {
            // Consumes all the required resources of the right type.
            this.used = this.used - leftToConsume.flush(this.type);
            // Missing resources returns as an unfulfilled requirement
            if (this.used < 0) {
                leftToConsume.add(this.type, (-this.used));
                this.used = 0;
            }
        }
        return leftToConsume;
    }
}
