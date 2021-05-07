package it.polimi.ingsw.supply;

import it.polimi.ingsw.cards.StockPower;

import java.util.*;

/**
 * Represents the Warehouse with three shelves, each one with limited capacity,
 * and the additional depots obtained by playing Leader Cards during the Game.
 * Each Warehouse shelf can store up to its index + 1 resources of the same type.
 * Only non-special Resources can be contained; to store a resource it is necessary
 * to add it as pending first and then move it internally.
 * @see Resource
 * @author Francesco Tosini
 */
public class Warehouse {
    private final Resource[] type;
    private final int[] amount;
    private final LeaderStock leaderStock;
    private final ResourcePack pendingResources;

    /**
     * The LeaderStock inner class represents a group of additional
     * depots gained during the Game by playing LeaderCards.
     */
    static class LeaderStock {
        List<StockPower> powers;
        Map<Resource,Integer> maxAmount;
        ResourcePack resources;

        /**
         * Constructs an empty LeaderStock.
         */
        public LeaderStock() {
            this.powers = new LinkedList<>();
            this.maxAmount = new HashMap<>();
            this.resources = new ResourcePack();
        }

        /**
         * Adds to the set of additional depots the one granted by the new StockPower.
         * @param stock the StockPower to activate.
         */
        public void addStockPower(StockPower stock) {
            //because StockPower objects are immutable, it is not necessary to make a copy
            this.powers.add(stock);
            //update max amount of containable resources
            this.maxAmount.put(stock.getType(),
                    this.maxAmount.getOrDefault(stock.getType(),0) + stock.getLimit());
        }

        /**
         * Returns all the StockPowers currently active.
         * @return the list of active StockPowers.
         */
        public List<StockPower> getStockPower() {
            return new LinkedList<>(this.powers);
        }

        /**
         * Returns a ResourcePack representing the collective capacity of
         * the current LeaderStock, considering all additional depots.
         * @return the capacity of the current LeaderStock as a ResourcePack.
         */
        public ResourcePack getLimit() {
            ResourcePack limits = new ResourcePack();
            for(Map.Entry<Resource,Integer> e : this.maxAmount.entrySet())
                limits.add(e.getKey(),e.getValue());
            return limits;
        }

        /**
         * Returns a ResourcePack representing all available Resources
         * contained in the current LeaderStock.
         * @return the ResourcePack with all the stored Resources.
         */
        public ResourcePack getResources() {
            return this.resources.getCopy();
        }

        /**
         * Stores the maximum amount of Resources, according to limits
         * on both type and amount, from the given pack.
         * Exceeding Resources are collected and returned as a new ResourcePack.
         * @param loot the pack of Resources to store.
         */
        public ResourcePack add(ResourcePack loot) {
            ResourcePack toStore = loot.getCopy();
            // TODO: un iteratore in ResourcePack può tornare utile, come un metodo contains.
            for(Resource res : this.maxAmount.keySet()) {
                if(toStore.get(res) != 0) {
                    //adds all the available resources of the right type
                    int tmp_amount = this.resources.flush(res) + toStore.flush(res);
                    if(tmp_amount > this.maxAmount.get(res)) {
                        //overflow is collected
                        toStore.add(res,tmp_amount - this.maxAmount.get(res));
                        tmp_amount = this.maxAmount.get(res);
                    }
                    this.resources.add(res,tmp_amount);
                }
            }
            return toStore;
        }

        /**
         * If there are enough Resources, consumes the amounts specified in the input ResourcePack;
         * partial consuming is allowed and returns the pack of exceeding Resources.
         * @param pack the pack of Resources to consume.
         * @return the pack of non-consumable Resources.
         */
        public ResourcePack consume(ResourcePack pack) {
            ResourcePack toConsume = pack.getCopy();
            for(Resource res : this.maxAmount.keySet()) {
                int required = toConsume.flush(res);
                if(required != 0) {
                    required = required - this.resources.flush(res);
                    if(required < 0) this.resources.add(res, ( - required)); //non used resources are collected
                    else if(required > 0) toConsume.add(res,required); //non satisfiable requirements are returned
                }
            }
            return toConsume;
        }
    }

    /**
     * Constructs an empty warehouse.
     */
    public Warehouse() {
        this.type = new Resource[]{Resource.VOID, Resource.VOID, Resource.VOID};
        this.amount = new int[]{0,0,0};
        this.pendingResources = new ResourcePack();
        this.leaderStock = new LeaderStock();
    }

    /**
     * Adds the additional depots granted from the given StockPower.
     * @param stock the StockPower to activate.
     */
    public void addStockPower(StockPower stock) {
        this.leaderStock.addStockPower(stock);
    }

    /**
     * Returns a ResourcePack representing the collective capacity of
     * the current LeaderStock, considering all additional depots.
     * @return the capacity of the current LeaderStock as a ResourcePack.
     */
    public ResourcePack getLeaderStockLimit() {
        return leaderStock.getLimit();
    }

    /**
     * Returns the maximum amount of a given Resource that can be stored in
     * the additional depots of the LeaderStock.
     */
    public int getLeaderStockLimit(Resource type) {
        return leaderStock.getLimit().get(type);
    }

    /**
     * Returns all the StockPowers currently active in the LeaderStock.
     * @return the list of active StockPowers.
     */
    public List<StockPower> getStockPower() {
        return new LinkedList<>(leaderStock.getStockPower());
    }

    /**
     * Stores the maximum amount of Resources from the given pack across all the available
     * additional Leader depots. Exceeding Resources are moved to pending.
     * @param loot the pack of Resources to store across Leader depots.
     */
    public void stockLeaderStock(ResourcePack loot) {
        ResourcePack remaining = this.leaderStock.add(loot);
        this.add(remaining);
    }

    /**
     * Consumes the given pack of Resources from the LeaderStock;
     * returns the exceeding, non-consumable Resources.
     * @param pack the ResourcePack to consume from the LeaderStock.
     * @return the ResourcePack of non-consumable Resources.
     */
    public ResourcePack consumeLeaderStock(ResourcePack pack) {
        return this.leaderStock.consume(pack);
    }

    /**
     * Makes the resources contained in the given pack able to be stored in the Warehouse;
     * new acquired resources are considered pending until they are placed in the shelves.
     * @param loot the ResourcePack be to store in the warehouse.
     */
    public void add(ResourcePack loot) {
        ResourcePack toStock = loot.getCopy();
        toStock.flush(Resource.FAITHPOINT);
        toStock.flush(Resource.VOID);
        this.pendingResources.add(loot);
    }

    /**
     * Returns a pack with the pending Resources to manage, which will be discarded if not
     * stocked properly before calling the done() method.
     * @return the ResourcePack with pending Resources.
     */
    public ResourcePack getPendingResources() {
        return this.pendingResources.getCopy();
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
    public void stock(int shelf, Resource type, int num) {
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
        else if(tmp_amount < num)
            try {
                this.pendingResources.consume(type,num - tmp_amount); //collect required resources
            } catch (NonConsumablePackException e) {
                // TODO: this can happen if pending resources are not enough...
                e.printStackTrace();
                return;
            }

        //empty the shelf if it is already occupied by another kind of resource
        if(this.type[shelf] != Resource.VOID) {
            this.pendingResources.add(this.type[shelf],this.amount[shelf]);
        }

        //apply the final configuration
        this.type[shelf] = type;
        this.amount[shelf] = num;
    }

    /**
     * Returns all the Resources stored in the Warehouse and additional depots;
     * no side-effect are produced; pending Resources are ignored.
     * @return the ResourcePack with all the Resources contained in the Warehouse.
     */
    public ResourcePack getResources() {
        ResourcePack resources = new ResourcePack();
        for(int i = 0; i < this.type.length; i++)
            resources.add(this.type[i], this.amount[i]);
        resources.add(leaderStock.getResources());
        return resources;
    }

    /**
     * Consumes the Resources contained in the Warehouse; returns the portion of required
     * resources that are not available.
     * @param pack the pack of resources that are to consume if available.
     * @return the pack of resources yet to be consumed.
     */
    public ResourcePack consume(ResourcePack pack) {
        int required;
        ResourcePack toConsume = pack.getCopy();
        for(int i = 0; i < this.type.length; i++) {
            required = toConsume.get(type[i]);
            if(required >= this.amount[i]) {
                //available resources of this kind are insufficient or exactly enough.
                try {
                    toConsume.consume(this.type[i], this.amount[i]);
                } catch (NonConsumablePackException e)
                {
                    //this is something that cannot happen
                    e.printStackTrace();
                }
                this.amount[i] = 0;
                this.type[i] = Resource.VOID;
            }
            else {
                //available resources of this kind are more than required.
                try {
                    toConsume.consume(this.type[i], required);
                } catch (NonConsumablePackException e) {
                    //this is something that cannot happen
                    e.printStackTrace();
                }
                this.amount[i] -= required;
            }
        }
        toConsume = this.consumeLeaderStock(toConsume);
        return toConsume;
    }

    /**
     * Tests if the set of all Resources stored in the Warehouse is enough to fully
     * cover the required ones; pending Resources are completely ignored.
     * Special resources are ignored. No side-effects are produced.
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
     * Returns the amount of resources that are still pending and are going
     * to be wasted if .done() is called.
     * @return the amount of pending resources.
     */
    public int wastedIfDone() {
        return this.pendingResources.size();
    }

    /**
     * Discards pending Resources.
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