package it.polimi.ingsw.supply;

import it.polimi.ingsw.cards.StockPower;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Represents the storage section of a player board;
 * it consists of a warehouse, a strongbox with unlimited capacity
 * and optional leaders' stocks.
 * @author Francesco Tosini
 */
public class Storage {
    private final ResourcePack strongbox;
    private final Warehouse warehouse;
    private final LeaderStock leaderStock;

    /**
     * The LeaderStock class represents a group of additional
     * depots gained by playing LeaderCards.
     */
    static class LeaderStock
    {
        List<StockPower> powers;
        Map<Resource,Integer> maxAmount;
        ResourcePack resources;

        /**
         * Constructs an empty LeaderStock.
         */
        public LeaderStock()
        {
            this.powers = new LinkedList<StockPower>();
            this.maxAmount = new HashMap<Resource,Integer>();
            this.resources = new ResourcePack();
        }

        /**
         * Adds to the current stock the additional depot granted
         * by the given StockPower.
         * @param stock the StockPower to activate
         */
        public void addStockPower(StockPower stock)
        {
            // Because StockPower objects are immutable it is not necessary to make a copy.
            this.powers.add(stock);
            // Update max amount of containable resources.
            this.maxAmount.put(stock.getType(),
                    this.maxAmount.getOrDefault(stock.getType(),0) + stock.getLimit());
        }

        /**
         * Returns a List of all the StockPowers active on the current LeaderStock.
         * @return a list of active StockPowers.
         */
        public List<StockPower> getStockPower()
        {
            return new LinkedList<StockPower> (this.powers);
        }

        /**
         * Returns a ResourcePack representing the collective capacity of
         * the current LeaderStack; every additional depot is considered.
         * @return the capacity of the current LeaderStock as a ResourcePack.
         */
        public ResourcePack getLimit()
        {
            ResourcePack limits = new ResourcePack();
            for(Map.Entry<Resource,Integer> e : this.maxAmount.entrySet())
                limits.add(e.getKey(),e.getValue());
            return limits;
        }

        /**
         * Returns a ResourcePack representing all available resources in the
         * current LeaderStock.
         * @return the pack of stored resources.
         */
        public ResourcePack getResources()
        {
            return this.resources.getCopy();
        }

        /**
         * Stores the maximum amount of Resources, according to limits
         * on both type and amount, from the given pack.
         * Non containable resources are collected and returned as a ResourcePack.
         * @param loot the pack of resources to store.
         * @return the pack of non containable resources.
         */
        public ResourcePack add(ResourcePack loot)
        {
            ResourcePack toStore = loot.getCopy();
            // TODO: un iteratore in ResourcePack può tornare utile, come un metodo contains.
            for(Resource res : this.maxAmount.keySet())
            {
                if(toStore.get(res) != 0)
                {
                    // Adds all the available resources of the right type.
                    int tmp_amount = this.resources.flush(res) + toStore.flush(res);
                    if(tmp_amount > this.maxAmount.get(res))
                    {
                        // Overflow is collected
                        toStore.add(res,tmp_amount - this.maxAmount.get(res));
                        tmp_amount = this.maxAmount.get(res);
                    }
                    this.resources.add(res,tmp_amount);
                }
            }
            return toStore;
        }

        /**
         * If there are enough Resources, consume the amounts specified in the input ResourcePack;
         * partial consuming is allowed and returns the pack of non satisfiable requirements.
         * @param pack the pack of resources to consume.
         * @return the pack of non-consumable resources.
         */
        public ResourcePack consume(ResourcePack pack)
        {
            // TODO: refactor necessario!
            ResourcePack toConsume = pack.getCopy();
            for(Resource res : this.maxAmount.keySet())
            {
                int required = toConsume.flush(res);
                if(required != 0)
                {
                    required = required - this.resources.flush(res);
                    if(required > 0) this.resources.add(res,required); // Not used resource are collected.
                    else if(required < 0) toConsume.add(res,(- required)); // Not satisfiable requirement are returned.
                }
            }
            return toConsume;
        }
    }

    /**
     * Constructs an empty storage.
     */
    public Storage()
    {
        this.strongbox = new ResourcePack();
        this.warehouse = new Warehouse();
        this.leaderStock = new LeaderStock();
    }

    /**
     * Adds the additional depot granted from the given StockPower.
     * @param stock the StockPower to activate on the current Storage.
     */
    public void addStockPower(StockPower stock)
    {
        this.leaderStock.addStockPower(stock);
    }

    /**
     * Returns the pack of all the resources stored across LeaderStock, Warehouse and Strongbox.
     * @return the pack of all stored resources.
     */
    public ResourcePack getAllResource()
    {
        ResourcePack resources = this.strongbox.getCopy();
        resources.add(this.warehouse.getResources());
        resources.add(this.leaderStock.getResources());
        return resources;
    }

    /**
     * Adds the given ResourcePack as pending resources in the current Storage's Warehouse.
     * @param loot the ResourcePack to store in the Warehouse.
     */
    public void stockWarehouse(ResourcePack loot)
    {
        this.warehouse.add(loot);
    }

    /**
     * Stores the maximum amount of Resources from the given pack across all the available
     * additional depots. Non containable resources are collected and returned as a ResourcePack.
     * @param loot the pack of resources to store across additional depots.
     * @return the pack of non containable resources.
     */
    public ResourcePack stockLeaderStock(ResourcePack loot)
    {
        return this.leaderStock.consume(loot);
    }

    /**
     * Stores the given pack of resources in the current Storage's Strongbox.
     * @param pack the ResourcePack to store in the Strongbox.
     */
    public void stockStrongbox(ResourcePack pack)
    {
        this.strongbox.add(pack);
    }

    /**
     * Test if the given pack of resources is consumable from the current Storage,
     * i.e. across the different kinds of depots.
     * The special resource VOID, if contained in the requirement, is treated as a generic resource.
     * @param pack the ResourcePack representing the requirement to test.
     * @return true if the given pack is consumable from the current Storage, false otherwise
     */
    public boolean isConsumable(ResourcePack pack)
    {
        // TODO: potrebbe essere utile un refactor. (uso di VOID)
        ResourcePack available = this.getAllResource();
        ResourcePack toTest = pack.getCopy();
        int voidResources = toTest.flush(Resource.VOID);

        if(voidResources != 0)
            return available.isConsumable(toTest) && ((available.size() - toTest.size()) >= voidResources);
        else return available.isConsumable(toTest);
    }

    /**
     * Consumes the given pack of resources from the current Storage.
     * The depots are consumed in the following order: Warehouse, LeaderStock, Strongbox.
     * If the requirement is not satisfiable nothing happens.
     * @param pack the ResourcePack to consume from the Storage.
     */
    public void autoConsume(ResourcePack pack) throws NonConsumablePackException
    {
        // TODO: potrebbe sollevare un'eccezione nel caso non sia possibile
        if(this.getAllResource().isConsumable(pack))
        {
            ResourcePack leftToConsume = pack.getCopy();

            // Consume resources from the warehouse
            leftToConsume = this.consumeWarehouse(leftToConsume);

            // Consume resources from leaders' stocks.
            leftToConsume = this.consumeLeaderStock(leftToConsume);

            // Consume resources from the strongbox
            this.strongbox.consume(leftToConsume);
        }
    }

    /**
     * Consumes the given pack of resources from the Storage's Warehouse;
     * returns the portion of required resources that are not available.
     * @param pack the ResourcePack to consume from the Warehouse.
     * @return the ResourcePack of non satisfiable requirement.
     */
    public ResourcePack consumeWarehouse(ResourcePack pack) throws NonConsumablePackException
    {
        return this.warehouse.consume(pack);
    }

    /**
     * Consumes the given pack of resources from the Storage's LeaderStock;
     * returns the portion of required resources that are not available.
     * @param pack the ResourcePack to consume from the LeaderStock.
     * @return the ResourcePack of non satisfiable requirement.
     */
    public ResourcePack consumeLeaderStock(ResourcePack pack)
    {
        return this.leaderStock.consume(pack);
    }

    /**
     * Consumes the given pack of resources from the Storage's Strongbox;
     * if the requirement is not satisfiable, nothing happens.
     * @param pack the ResourcePack to consume from the Strongbox.
     */
    public void consumeStrongbox(ResourcePack pack) throws NonConsumablePackException
    {
        this.strongbox.consume(pack);
    }
}