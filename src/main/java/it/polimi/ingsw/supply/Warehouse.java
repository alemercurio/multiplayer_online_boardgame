package it.polimi.ingsw.supply;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.cards.StockPower;

import java.util.*;

/**
 * Represents the Warehouse with three shelves, each one with limited capacity,
 * and the additional depots obtained by playing Leader Cards during the Game.
 * All depots are equally treated, except for the Leader depots having a fixed type
 * of Resource (while the shelves can change type).
 * Only non-special Resources can be contained; to store a Resource it is necessary
 * to add it as pending first, and then move it internally.
 * @see Resource
 * @author Francesco Tosini
 */
public class Warehouse {

    /**
     * Inner class to represent a generic set of depots, meaning it could be standard Warehouse
     * shelves or additional Leader depots activated. The distinction between the two is obtained
     * with a special attribute, that indicates if the type of Resource is fixed (as in Leader depots)
     * or variable (as in Warehouse shelves).
     */
    protected static class LimitedStock {

        protected Resource resource;
        protected final int size;
        protected int available;
        protected final boolean free;

        /**
         * Creates a Warehouse shelf with the specified size (1, 2, or 3 for the standard rules).
         * @param size the size of the shelf.
         */
        public LimitedStock(int size) {
            this.resource = Resource.VOID;
            this.size = size;
            this.available = 0;
            this.free = true;
        }

        /**
         * Creates additional Leader depots with the specified size for the specified Resource.
         * @param resource the type of Resource of the depots.
         * @param size the number of additional depots.
         */
        public LimitedStock(Resource resource, int size) {
            this.resource = resource;
            this.size = size;
            this.available = 0;
            this.free = false;
        }

        /**
         * Returns the type of the LimitedStock.
         * @return true if it is a Warehouse shelf, false if it is a Leader depot.
         */
        public boolean isFree() {
            return this.free;
        }

        /**
         * Returns the type of Resource currently contained by the depot.
         * @return the type of Resource.
         */
        public Resource getResource() {
            return this.resource;
        }

        /**
         * Sets the type of Resource to the one specified, if possible.
         * @param resource the Resource to set.
         * @throws UnsupportedOperationException if the depot is a Leader depot, meaning the type
         * of Resource is fixed.
         */
        public void setResource(Resource resource) throws UnsupportedOperationException {
            if (this.free) this.resource = resource;
            else throw new UnsupportedOperationException();
        }

        /**
         * Returns the size of the LimitedStock.
         * @return the size as an integer.
         */
        public int size() {
            return this.size;
        }

        /**
         * Returns the Resources currently contained in the depots.
         * @return the number of Resources contained.
         */
        public int getAvailable() {
            return this.available;
        }

        /**
         * Stores the specified amount of the Resource currently set for the depots in the
         * depots themselves. If the free depots are not enough, stores the maximum amount
         * possible and returns the excess.
         * @param amount the amount of Resources to store.
         * @return the exceeding amount of Resources.
         */
        public int store(int amount) {
            if (amount <= 0) return 0;

            this.available = this.available + amount;
            if (this.available > this.size) {
                int unableToStore = this.available - this.size;
                this.available = this.size;
                return unableToStore;
            } else return 0;
        }

        /**
         * Consumes the specified amount of the Resource currently set for the depots from the
         * depots themselves. If the amount to consume exceed the available Resources, consume the
         * maximum amount and returns the excess.
         * @param amount the amount of Resources to consume.
         * @return the exceeding amount of Resources.
         */
        public int consume(int amount) {
            if (amount <= 0) return 0;

            this.available = this.available - amount;
            if (this.available <= 0) {
                int unableToConsume = -this.available;

                this.available = 0;
                if (this.free) this.resource = Resource.VOID;

                return unableToConsume;
            } else return 0;
        }

        /**
         * Consumes the specified ResourcePack from  the depots. If the amount to consume
         * exceed the available Resources, consume the maximum amount and returns the excess.
         * @param pack the ResourcePack to consume.
         * @return the ResourcePack with the exceeding Resources.
         */
        public ResourcePack consume(ResourcePack pack) {
            int toConsume = pack.flush(this.resource);
            pack.add(this.resource, this.consume(toConsume));
            return pack;
        }

        /**
         * Empties the depots and returns the amount of Resources deleted.
         * @return the amount of Resources deleted.
         */
        public int flush() {
            int available = this.available;

            if (this.free) this.resource = Resource.VOID;
            this.available = 0;

            return available;
        }
    }

    protected final List<LimitedStock> stock;
    protected final ResourcePack pendingResources;

    public Warehouse() {
        this.stock = new ArrayList<>();

        this.stock.add(new LimitedStock(1));
        this.stock.add(new LimitedStock(2));
        this.stock.add(new LimitedStock(3));

        this.pendingResources = new ResourcePack();
    }

    /**
     * Adds the specified ResourcePack as pending.
     * @param loot the ResourcePack to add in the Warehouse.
     */
    public void add(ResourcePack loot) {
        ResourcePack resources = loot.getCopy();
        resources.flush(Resource.VOID);
        resources.flush(Resource.FAITHPOINT);
        this.pendingResources.add(resources);
    }

    /**
     * Adds the specified Power to the Warehouse, making additional depots available.
     * @param power the StockPower containing the additional Leader depots to add.
     */
    public void addStockPower(StockPower power) {
        this.stock.add(new LimitedStock(power.getType(),power.getLimit()));
    }

    /**
     * Returns the List of all StockPowers activated.
     * @return the List of StockPowers.
     */
    public List<StockPower> getStockPower() {
        List<StockPower> stockPowers = new ArrayList<>();

        for(int i = 3; i < this.stock.size(); i++) {
            LimitedStock shelf = this.stock.get(i);
            stockPowers.add(new StockPower(shelf.size,shelf.resource));
        }

        return stockPowers;
    }

    /**
     * Puts the specified amount of the specified Resource to the specified destination,
     * without breaking the Warehouse shelves rules.
     * @param destination the Warehouse shelf or Leader additional depots to move the Resources to.
     * @param resource the type of Resource to move.
     * @param amount the amount of Resource to move.
     * @return the amount of Resources that was previously occupying the destination.
     */
    public int stock(int destination, Resource resource, int amount) {
        LimitedStock dest;

        try {
            dest = this.stock.get(destination - 1);
        } catch (IndexOutOfBoundsException e) {
            return 0;
        }

        if (dest.getResource().equals(resource)) {
            amount = amount - dest.store(amount);
            return amount;
        } else if (!dest.isFree()) return 0;

        else {
            for (LimitedStock stock : this.stock)
                if (stock.isFree() && stock.getResource().equals(resource))
                    this.pendingResources.add(resource, stock.flush());

            this.pendingResources.add(dest.getResource(), dest.flush());
            dest.setResource(resource);
            amount = amount - dest.store(amount);
            return amount;
        }
    }

    /**
     * Moves the specified amount of the specified Resource from pending to the specified
     * destination within the Warehouse, without breaking the Warehouse shelves rules.
     * @param destination the Warehouse shelf or Leader additional depots to move the Resources to.
     * @param resource the type of Resource to move.
     * @param amount the amount of Resource to move.
     */
    public void move(int destination, Resource resource, int amount) {
        if (this.pendingResources.get(resource) < amount) amount = this.pendingResources.get(resource);
        try {
            this.pendingResources.consume(resource, this.stock(destination, resource, amount));
        } catch (NonConsumablePackException ignored) { /* this should not happen */ }
    }

    /**
     * Moves the specified amount of Resource from the specified source to the specified
     * destination within the Warehouse, without breaking the Warehouse shelves rules.
     * @param destination the Warehouse shelf or Leader additional depots to move the Resources to.
     * @param source the origin of the Resource (the type is inferred by the actual content of source)
     * @param amount the amount of Resource to move.
     * @return true if the movement was consistent, false if it is not possible to do.
     */
    public boolean move(int destination, int source, int amount) {
        LimitedStock src;
        LimitedStock dest;

        try {
            src = this.stock.get(source - 1);
            dest = this.stock.get(destination - 1);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }

        if (amount > src.getAvailable()) amount = src.getAvailable();

        if (src.isFree() && dest.isFree()) {
            Resource toMove = src.getResource();
            this.pendingResources.add(toMove, src.flush());
            this.move(destination, toMove, amount);
        } else src.consume(this.stock(destination, src.getResource(), amount));

        return true;
    }

    /**
     * Returns whether the ResourcePack is contained in the Warehouse (and therefore consumable)
     * or not.
     * @param pack the input ResourcePack.
     * @return true if it is consumable, false otherwise.
     */
    public boolean isConsumable(ResourcePack pack) {
        ResourcePack required = pack.getCopy();
        required.flush(Resource.VOID);
        required.flush(Resource.FAITHPOINT);
        return this.getResources().isConsumable(required);
    }

    /**
     * Consumes the ResourcePack from the Warehouse, removing the Resources contained in the pack
     * from the Warehouse itself.
     * @param pack the ResourcePack with the Resources to remove.
     * @return the remaining Resources that it was not possible to consume.
     */
    public ResourcePack consume(ResourcePack pack) {

        ResourcePack leftToConsume = pack.getCopy();

        for(LimitedStock shelf : this.stock)
            shelf.consume(leftToConsume);

        return leftToConsume;
    }

    /**
     * Returns the ResourcePack with all the pending Resources, gathered but not stocked.
     * @return the pending ResourcePack.
     */
    public ResourcePack getPendingResources() {
        return this.pendingResources.getCopy();
    }

    /**
     * Confirms the Warehouse operations and discard the pending Resources.
     * @return the amount of Resources discarded.
     */
    public int done() {
        return this.pendingResources.flush();
    }

    /**
     * Constructs and returns the ResourcePack with the overall availability of Resources
     * contained in the Warehouse.
     * @return the ResourcePack with all the Resources stocked.
     */
    public ResourcePack getResources() {
        ResourcePack resources = new ResourcePack();

        for(LimitedStock shelf : this.stock) {
            if(!shelf.getResource().isSpecial())
                resources.add(shelf.getResource(),shelf.getAvailable());
        }

        return resources;
    }

    /**
     * Returns the status of the Warehouse in a JsonObject.
     * @return a String representing the JsonObject.
     */
    public String getConfig() {
        ArrayList<Resource> resources = new ArrayList<>();
        ArrayList<Integer> amounts = new ArrayList<>();

        for(LimitedStock stock : this.stock) {
            if(stock.isFree()) resources.add(stock.getResource());
            amounts.add(stock.getAvailable());
        }

        Gson parser = new Gson();
        JsonObject config = new JsonObject();

        config.add("resources",parser.toJsonTree(resources));
        config.add("amounts",parser.toJsonTree(amounts));
        config.add("pending",parser.toJsonTree(this.pendingResources));

        return config.toString();
    }

    /**
     * Tests if a Warehouse status passed as input is consistent and applies it if it is.
     * @param state a String representing the JsonObject configuration.
     * @return true if the configuration was applied, false if it is not applicable.
     */
    public boolean update(String state) {
        Gson gson = new Gson();
        JsonElement element = gson.fromJson(state, JsonElement.class);
        JsonObject jsonObj = element.getAsJsonObject();

        Resource[] resources = gson.fromJson(jsonObj.get("resources"), Resource[].class);
        int[] amounts = gson.fromJson(jsonObj.get("amounts"), int[].class);
        ResourcePack pending = gson.fromJson(jsonObj.get("pending"), ResourcePack.class);

        // Test if the new configuration can be applied.

        if (amounts.length != this.stock.size()) return false;

        ResourcePack toStore = pending.getCopy();
        LinkedList<Resource> freeResources = new LinkedList<>();

        for (int i = 0; i < amounts.length; i++) {
            LimitedStock shelf = this.stock.get(i);

            if (shelf.size() < amounts[i]) return false;

            if (shelf.isFree()) {
                Resource newResource = resources[freeResources.size()];

                if (!newResource.equals(Resource.VOID) && freeResources.contains(newResource)) return false;

                freeResources.add(newResource);
                toStore.add(newResource, amounts[i]);
            } else toStore.add(shelf.getResource(), amounts[i]);
        }

        ResourcePack available = this.getResources();
        available.add(this.pendingResources);
        if (!available.equals(toStore)) return false;

        // The given configuration can be applied.

        for (int i = 0; i < amounts.length; i++) {
            LimitedStock shelf = this.stock.get(i);
            shelf.flush();

            if (shelf.isFree())
                shelf.setResource(freeResources.pollFirst());

            shelf.store(amounts[i]);
        }

        this.pendingResources.flush();
        this.pendingResources.add(pending);

        return true;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}