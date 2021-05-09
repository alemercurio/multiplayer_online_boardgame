package it.polimi.ingsw.supply;

/**
 * Represents the Storage section of a Player Board; it consists of a Warehouse and
 * a Strongbox with unlimited capacity.
 * @author Francesco Tosini
 */
public class Storage {
    public final ResourcePack strongbox;
    public final Warehouse warehouse;

    /**
     * Constructs an empty Storage.
     */
    public Storage() {
        this.strongbox = new ResourcePack();
        this.warehouse = new Warehouse();
    }

    /**
     * Stores the given pack of Resources in the Strongbox.
     * @param pack the ResourcePack to store in the Strongbox.
     */
    public void stockStrongbox(ResourcePack pack) {
        ResourcePack toStock = pack.getCopy();
        toStock.flush(Resource.FAITHPOINT);
        toStock.flush(Resource.VOID);
        this.strongbox.add(toStock);
    }

    /**
     * Returns the pack of all the Resources stored across LeaderStock, Warehouse and Strongbox.
     * @return the ResourcePack of all stored Resources.
     */
    public ResourcePack getAllResource() {
        ResourcePack resources = this.strongbox.getCopy();
        resources.add(this.warehouse.getResources());
        return resources;
    }

    /**
     * Tests if the given pack of Resources is consumable from the Storage,
     * across all the different kinds of depots.
     * The special resource VOID, if contained in the input, is treated as a generic Resource.
     * The special resource FAITHPOINT is ignored.
     * @param pack the ResourcePack representing the requirement to test.
     * @return true if the given pack is consumable from the Storage, false otherwise.
     */
    public boolean isConsumable(ResourcePack pack) {
        ResourcePack available = this.getAllResource();
        ResourcePack toTest = pack.getCopy();

        int voidResources = toTest.flush(Resource.VOID);
        toTest.flush(Resource.FAITHPOINT);

        if(voidResources != 0)
            return available.isConsumable(toTest) && ((available.size() - toTest.size()) >= voidResources);
        else return available.isConsumable(toTest);
    }

    /**
     * Consumes the given pack of Resources from the Storage.
     * The depots are considered in the following order: Warehouse, LeaderStock, Strongbox.
     * If the pack exceed the Storage available Resources, an exception is raised.
     * @param pack the ResourcePack to consume from the Storage.
     */
    public void autoConsume(ResourcePack pack) throws NonConsumablePackException {
        ResourcePack available = this.getAllResource();

        if(available.isConsumable(pack)) {
            ResourcePack leftToConsume = pack.getCopy();

            // Consume Resources from the Warehouse.
            leftToConsume = this.warehouse.consume(leftToConsume);

            // Consume Resources from the Strongbox.
            this.strongbox.consume(leftToConsume);
        }
        else throw new NonConsumablePackException(pack,available);
    }

    /**
     * Discards pending Resources in the Warehouse.
     * @return the amount of Resources discarded.
     */
    public int done() {
        return this.warehouse.done();
    }
}