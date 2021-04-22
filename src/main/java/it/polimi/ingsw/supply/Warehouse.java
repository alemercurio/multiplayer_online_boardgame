package it.polimi.ingsw.supply;

import java.util.Arrays;

/**
 * Represents the Warehouse with three shelves, each one with limited capacity.
 * Each shelf can store up to its index + 1 resources of the same type.
 * Only non-special Resources can be contained; to store a resource it is necessary
 * to add it as pending first and then move it internally.
 * @see Resource
 * @author Francesco Tosini
 */
public class Warehouse {
    private final Resource[] type;
    private final int[] amount;
    private final ResourcePack pendingResources;

    /**
     * Constructs an empty warehouse.
     */
    public Warehouse() {
        this.type = new Resource[]{Resource.VOID, Resource.VOID, Resource.VOID};
        this.amount = new int[]{0,0,0};
        this.pendingResources = new ResourcePack();
    }


    /**
     * Makes the resources contained in the given pack able to be stored in the Warehouse;
     * new acquired resources are considered pending until they are placed in the shelves.
     * @param loot the ResourcePack be to store in the warehouse.
     */
    public void add(ResourcePack loot) {
        this.pendingResources.add(loot);
    }


    /**
     * Switches the Resources contained in two different shelves.
     * Exceeding Resources are automatically moved to pending.
     * @param shelfA the first shelf.
     * @param shelfB the second shelf.
     */
    public void switchShelves(int shelfA, int shelfB) {
        Resource tmp_resource;
        int tmp_amount;

        //saves the resources available in the first shelf
        tmp_resource = type[shelfA];
        tmp_amount = amount[shelfA];

        //update the content of the first shelf and collects any exceeding resources
        type[shelfA] = type[shelfB];
        if(amount[shelfB] > (shelfA + 1)) {
            amount[shelfA] = shelfA + 1;
            pendingResources.add(type[shelfA],amount[shelfB] - shelfA - 1);
        }
        else amount[shelfA] = amount[shelfB];

        //update the content of the second shelf and collects any exceeding resources
        type[shelfB] = tmp_resource;
        if(tmp_amount > (shelfB + 1)) {
            amount[shelfB] = shelfB + 1;
            pendingResources.add(tmp_resource,tmp_amount - shelfB - 1);
        }
        else amount[shelfB] = tmp_amount;
    }

    /**
     * Moves Resources within the Warehouse to put a specified number of Resources
     * of the specified type in the specified shelf; only the maximum amount is effectively stored.
     * If another shelf is holding the given kind of resource it is emptied automatically.
     * Exceeding Resources are automatically moved to pending.
     * @param shelf the shelf where the Resources are going to be stored.
     * @param type the specific type of Resource to store.
     * @param num the amount of Resource to store.
     */
    public void stock(int shelf, Resource type, int num) throws NonConsumablePackException {
        int tmp_amount = 0;

        //note: only non-special resources can be stored
        if(type.isSpecial()) return;

        //check if the resource is already present in the warehouse
        int previous_shelf = Arrays.asList(this.type).indexOf(type);

        //num is capped since shelf can contain its index + 1 resources at most
        if(num > (shelf + 1)) num = shelf + 1;

        //if the resource was already in the warehouse
        if(previous_shelf != -1) {
            tmp_amount = amount[previous_shelf];

            //empty previous shelf occupied with this kind of resource
            this.amount[previous_shelf] = 0;
            this.type[previous_shelf] = Resource.VOID;
        }

        if(tmp_amount > num) this.pendingResources.add(type, tmp_amount - num); //exceeding resources to pending
        else if(tmp_amount < num) this.pendingResources.consume(type,num - tmp_amount); //collect required resources

        //empty the shelf if it is already occupied by another kind of resource
        if(this.type[shelf] != Resource.VOID) {
            this.pendingResources.add(this.type[shelf],this.amount[shelf]);
        }

        //apply the final configuration
        this.type[shelf] = type;
        this.amount[shelf] = num;
    }

    /**
     * Returns all the Resources stored in the Warehouse;
     * no side-effect are produced; pending Resources are ignored.
     * @return the ResourcePack with all the Resources contained in the Warehouse.
     */
    public ResourcePack getResources() {
        ResourcePack resources = new ResourcePack();
        for(int i = 0; i < this.type.length; i++)
            resources.add(this.type[i], this.amount[i]);
        return resources;
    }

    /**
     * Consumes the Resources contained in the Warehouse; returns the portion of required
     * resources that are not available.
     * @param pack the pack of resources that are to consume if available.
     * @return the pack of resources yet to be consumed.
     */
    public ResourcePack consume(ResourcePack pack) throws NonConsumablePackException {
        int required;
        ResourcePack toConsume = pack.getCopy();
        for(int i = 0; i < this.type.length; i++) {
            required = toConsume.get(type[i]);
            if(required >= this.amount[i]) {
                //available resources of this kind are insufficient or exactly enough.
                toConsume.consume(this.type[i],this.amount[i]);
                this.amount[i] = 0;
                this.type[i] = Resource.VOID;
            }
            else {
                //available resources of this kind are more than required.
                toConsume.consume(this.type[i],required);
                this.amount[i] -= required;
            }
        }
        return toConsume;
    }

    /**
     * Tests if the set of all Resources stored in the Warehouse is enough to fully
     * cover the required ones; pending Resources are completely ignored.
     * No side-effect are produced.
     * @param pack the ResourcePack required.
     * @return true if Warehouse has enough Resources in its shelves.
     */
    public boolean isConsumable(ResourcePack pack) {
        ResourcePack required = pack.getCopy();

        //eliminates requirements on special resources.
        for(Resource res : Resource.values()) if(res.isSpecial()) required.flush(res);

        return this.getResources().isConsumable(required);
    }

    /**
     * Discard pending Resources.
     * @return the amount of non-special Resources discarded.
     */
    public int done() {
        return this.pendingResources.flush();
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("{\n");
        for(int i = 0;i < this.type.length; i++) {
            res.append("\t").append(i + 1).append(" {").append(this.type[i]).append(":").append(this.amount[i]).append("}\n");
        }
        res.append("}");
        return res.toString();
    }

    /**
     * @return a textual representation of pending Resources.
     */
    public String getPendingView() {
        return this.pendingResources.toString();
    }
}