package it.polimi.ingsw;

import java.util.Arrays;

/**
 * Represents a warehouse with three shelves, each one with limited capacity.
 * Each shelf can store up to its index + 1 resources of the same kind.
 * Only non-special resources can be contained; to store a resource it is necessary
 * first to add it as pending and then move it internally.
 * @author Francesco Tosini
 */
public class Warehouse
{
    private final Resource[] type;
    private final int[] amount;
    private final ResourcePack pendingResources;

    /**
     * Constructs an empty warehouse.
     */
    public Warehouse()
    {
        this.type = new Resource[]{Resource.VOID, Resource.VOID, Resource.VOID};
        this.amount = new int[]{0,0,0};
        this.pendingResources = new ResourcePack();
    }


    /**
     * Makes the resources contained in the given pack able to be stored in the current warehouse;
     * actually, new resources are considered pending until they are placed in the shelves.
     * pending ones are discarded when done() is called;
     * @param loot the pack of resources to store in the warehouse.
     */
    public void add(ResourcePack loot)
    {
        this.pendingResources.add(loot);
    }


    /**
     * Switch the resources contained in two different shelves;
     * Exceeding resources are automatically collected as pending ones.
     * @param shelfA the first shelf.
     * @param shelfB the second shelf.
     */
    public void switchShelves(int shelfA, int shelfB)
    {
        Resource tmp_resource;
        int tmp_amount;

        // Saves the resources available in the first shelf.
        tmp_resource = type[shelfA];
        tmp_amount = amount[shelfA];

        // Update the content of the first shelf and collects any exceeding resource.
        type[shelfA] = type[shelfB];
        if(amount[shelfB] > (shelfA + 1))
        {
            amount[shelfA] = shelfA + 1;
            pendingResources.add(type[shelfA],amount[shelfB] - shelfA - 1);
        }
        else amount[shelfA] = amount[shelfB];

        // Update the content of the second shelf and collects any exceeding resource.
        type[shelfB] = tmp_resource;
        if(tmp_amount > (shelfB + 1))
        {
            amount[shelfB] = shelfB + 1;
            pendingResources.add(tmp_resource,tmp_amount - shelfB - 1);
        }
        else amount[shelfB] = tmp_amount;
    }

    /**
     * Moves resources within the warehouse to get exactly num resources of the specified kind
     * in the specified shelf; if num exceeds the capacity of the shelf only the maximum
     * amount is effectively stored.
     * if another shelf is holding the given kind of resource it is emptied automatically.
     * Exceeding resources are collected as pending ones.
     * @param shelf the shelf where the resources are going to be stored.
     * @param type the specific kind of resource to store.
     * @param num the amount of resource to store.
     */
    public void stock(int shelf, Resource type, int num)
    {
        int tmp_amount = 0;

        // note: only non-special resources can be stored.
        if(type.isSpecial()) return;

        // check if the resource is already present in the warehouse.
        int previous_shelf = Arrays.asList(this.type).indexOf(type);

        // num is capped since shelf can contain its index + 1 resources at most
        if(num > (shelf + 1)) num = shelf + 1;

        // If the resource was already in the warehouse
        if(previous_shelf != -1)
        {
            tmp_amount = amount[previous_shelf];

            // Empty previous shelf occupied with this kind of resource
            this.amount[previous_shelf] = 0;
            this.type[previous_shelf] = Resource.VOID;
        }

        if(tmp_amount > num) this.pendingResources.add(type, tmp_amount - num); // Drop exceeding resources
        else if(tmp_amount < num) this.pendingResources.consume(type,num - tmp_amount); // Collect required resources

        // Empty the shelf if it is already occupied by another kind of resource.
        if(this.type[shelf] != Resource.VOID)
        {
            this.pendingResources.add(this.type[shelf],this.amount[shelf]);
        }

        // Apply the desired configuration.
        this.type[shelf] = type;
        this.amount[shelf] = num;
    }

    /**
     * Returns the amount of resources stored in the warehouse; no side-effect are produced;
     * pending resources are ignored completely.
     * @return a ResourcePack of the resources contained in the warehouse.
     */
    public ResourcePack getResources()
    {
        ResourcePack resources = new ResourcePack();
        for(int i = 0; i < this.type.length; i++)
            resources.add(this.type[i], this.amount[i]);
        return resources;
    }

    /**
     * Consumes the resources contained in the warehouse; returns the portion of required
     * resources that are not available.
     * @param pack the pack of resources that are to consume if available.
     * @return the pack of resources yet to be consumed.
     */
    public ResourcePack consume(ResourcePack pack)
    {
        int required;
        ResourcePack toConsume = pack.getCopy();
        for(int i = 0; i < this.type.length; i++)
        {
            required = toConsume.get(type[i]);
            if(required >= this.amount[i])
            {
                // Available resources of this kind are insufficient or exactly enough.
                toConsume.consume(this.type[i],this.amount[i]);
                this.amount[i] = 0;
                this.type[i] = Resource.VOID;
            }
            else
            {
                // Available resources of this kind are more than required.
                toConsume.consume(this.type[i],required);
                this.amount[i] -= required;
            }
        }
        return toConsume;
    }

    /**
     * Test if the resources stored in the warehouse are enough to fully
     * satisfy the required non-special ones; pending resources are completely ignored.
     * No side-effect are produced.
     * @param pack  the pack of required resources.
     * @return true if warehouse has enough resources to fully satisfy the requirement.
     */
    public boolean isConsumable(ResourcePack pack)
    {
        ResourcePack required = pack.getCopy();

        // Eliminates requirements on special resources.
        for(Resource res : Resource.values()) if(res.isSpecial()) required.flush(res);

        return this.getResources().isConsumable(pack);
    }

    /**
     * Flush pending resources; returns the amount of discarded non-special resources.
     * @return the amount of non-special resources that have been flushed.
     */
    public int done()
    {
        return this.pendingResources.flush();
    }

    @Override
    public String toString()
    {
        String res = "{\n";
        for(int i = 0;i < this.type.length; i++)
        {
            res = res + "\t" + (i + 1) + " {" + this.type[i] + ":" + this.amount[i] + "}\n";
        }
        res = res + "}";
        return res;
    }

    /**
     * @return a textual representation of pending resources.
     */
    public String getPendingView()
    {
        return this.pendingResources.toString();
    }
}