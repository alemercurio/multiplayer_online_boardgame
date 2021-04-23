package it.polimi.ingsw.supply;

import it.polimi.ingsw.cards.StockPower;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Represents the Storage section of a Player Board; it consists of a Warehouse,
 * a Strongbox with unlimited capacity and additional Leaders' depots added during the Game.
 * @author Francesco Tosini
 */
public class Storage {
    private final ResourcePack strongbox;
    private final Warehouse warehouse;
    private final LeaderStock leaderStock;

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
         * @return the pack of Resources exceeding the capacity of the LeaderStock.
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
            // TODO: refactor
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
     * Constructs an empty Storage.
     */
    public Storage() {
        this.strongbox = new ResourcePack();
        this.warehouse = new Warehouse();
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
     * Returns the pack of all the Resources stored across LeaderStock, Warehouse and Strongbox.
     * @return the ResourcePack of all stored Resources.
     */
    public ResourcePack getAllResource() {
        ResourcePack resources = this.strongbox.getCopy();
        resources.add(this.warehouse.getResources());
        resources.add(this.leaderStock.getResources());
        return resources;
    }

    /**
     * Adds the given ResourcePack as pending Resources in the Warehouse.
     * @param loot the ResourcePack to store in the Warehouse.
     */
    public void stockWarehouse(ResourcePack loot) {
        this.warehouse.add(loot);
    }

    /**
     * Consumes the given pack of Resources from the Warehouse;
     * returns the exceeding, non-consumable Resources.
     * @param pack the ResourcePack to consume from the Warehouse.
     * @return the ResourcePack of non-consumable Resources.
     */
    public ResourcePack consumeWarehouse(ResourcePack pack) {
        return this.warehouse.consume(pack);
    }

    /**
     * Stores the maximum amount of Resources from the given pack across all the available
     * additional Leader depots. Exceeding Resources are collected and returned as a ResourcePack.
     * @param loot the pack of Resources to store across Leader depots.
     * @return the pack of non-containable Resources.
     */
    public ResourcePack stockLeaderStock(ResourcePack loot) {
        return this.leaderStock.add(loot);
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
     * Stores the given pack of Resources in the Strongbox.
     * @param pack the ResourcePack to store in the Strongbox.
     */
    public void stockStrongbox(ResourcePack pack) {
        ResourcePack toStock = pack.getCopy();
        toStock.flush(Resource.FAITHPOINT);
        toStock.flush(Resource.VOID); // TODO: la gestione del VOID è problematica
        this.strongbox.add(toStock);
    }

    /**
     * Consumes the given pack of resources from the Storage's Strongbox;
     * If the pack exceed the available Resources, an exception is raised.
     * @param pack the ResourcePack to consume from the Strongbox.
     */
    public void consumeStrongbox(ResourcePack pack) throws NonConsumablePackException {
        this.strongbox.consume(pack);
    }

    /**
     * Tests if the given pack of Resources is consumable from the Storage,
     * across all the different kinds of depots.
     * The special resource VOID, if contained in the input, is treated as a generic Resource.
     * @param pack the ResourcePack representing the requirement to test.
     * @return true if the given pack is consumable from the Storage, false otherwise.
     */
    public boolean isConsumable(ResourcePack pack) {
        // TODO: potrebbe essere utile un refactor (uso di VOID)
        ResourcePack available = this.getAllResource();
        ResourcePack toTest = pack.getCopy();
        int voidResources = toTest.flush(Resource.VOID);

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
        if(this.getAllResource().isConsumable(pack)) {
            ResourcePack leftToConsume = pack.getCopy();

            //consume resources from the warehouse
            leftToConsume = this.consumeWarehouse(leftToConsume);

            //consume resources from leaders' stocks
            leftToConsume = this.consumeLeaderStock(leftToConsume);

            //consume resources from the strongbox
            this.consumeStrongbox(leftToConsume);
        }
    }

    /**
     * Discard pending Resources in the Warehouse.
     * @return the amount of Resources discarded.
     */
    public int done() {
        // TODO: ragionare sulle pending resources dal punto di vista della PlayerBoard
        return this.warehouse.done();
    }
}