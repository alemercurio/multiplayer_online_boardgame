package it.polimi.ingsw.supply;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Represents a pack of Resources with both special and non-special ones.
 * @see Resource
 * @author Francesco Tosini
 */
public class ResourcePack {
    final private Map<Resource,Integer> resources;

    /**
     * Constructs an empty pack of Resources.
     */
    public ResourcePack() {
        this.resources = new HashMap<>();
    }

    /**
     * Constructs a pack with the given amounts of resources.
     * The order is the same as the one in the Resource enumeration;
     * non specified amounts are treated as zeros, while exceeding parameters are ignored.
     * @param resourceAmounts the quantity of each Resource.
     * @see Resource
     */
    public ResourcePack(int...resourceAmounts) {
        this();
        Resource[] res = Resource.values();
        for(int i = 0; i < resourceAmounts.length && i < 6; i++) {
            //if the amount of a Resource is zero, its record is omitted or removed from the map
            if(resourceAmounts[i] != 0) {
                this.resources.put(res[i], resourceAmounts[i]);
            }
        }
    }

    /**
     * Returns the amount of non-special Resources stored in the ResourcePack.
     * @return the sum of all quantities of non-special Resources.
     * @see Resource
     */
    public int size() {
        //selects non-special resources and then evaluate their quantity by using a reduce function
        return this.resources.entrySet().stream().filter(e -> !(e.getKey().isSpecial()))
                .map(Map.Entry::getValue).reduce(0,Integer::sum);
    }

    /**
     * Returns the amount of a particular Resource stored in the ResourcePack;
     * Unlike "consume" or "flush" it does not actually decrease its quantity.
     * @param resource the type of Resource whose amount is desired.
     * @return the quantity of the specified Resource contained.
     * @see Resource
     */
    public int get(Resource resource) {
        return resources.getOrDefault(resource,0);
    }

    /**
     * Adds the content of the given ResourcePack to the current one;
     * no resources are consumed in the process (even the ones from the given pack).
     * @param pack the Resources to add to the current pack.
     * @return the current pack with the added Resources.
     */
    public ResourcePack add(ResourcePack pack) {
        pack.resources.forEach((res,amt) -> resources.merge(res,amt, Integer::sum));
        return this;
    }

    /**
     * Adds the specified amount of the given Resource to the current pack;
     * negative amounts are ignored.
     * @param resource the specific type of Resource to add.
     * @param amount the quantity of Resource to add.
     * @return the current pack with the added Resources.
     */
    public ResourcePack add(Resource resource, int amount) {
        if(amount > 0) {
            int newAmount = this.get(resource) + amount;
            this.resources.put(resource, newAmount);
        }
        return this;
    }

    /**
     * Compares two ResourcePacks; returns true if the first "contains" the second.
     * No side-effects are produced on the ResourcePacks. Resources are treated equally.
     * @param required the ResourcePack with Resources whose availability is to be tested.
     * @return true if it is possible to consume the required pack from the current one.
     */
    public boolean isConsumable(ResourcePack required) {
        if(required != null) {
            for (Map.Entry<Resource, Integer> req : required.resources.entrySet())
                if (this.get(req.getKey()) < req.getValue()) return false;
        }
        //null is considered as an empty pack so the result is true in that particular case
        return true;
    }

    /**
     * Consumes the Resources in the current pack, removing the contents of the given one from it.
     * If the specified pack is not consumable an exception is raised.
     * Resources are treated equally.
     * @param pack the ResourcePack with the amount of Resources to consume.
     * @throws NonConsumablePackException if the are not enough Resources in the current pack.
     */
    public void consume(ResourcePack pack) throws NonConsumablePackException {
        int remaining;
        if(this.isConsumable(pack)) {
            for(Map.Entry<Resource,Integer> e : pack.resources.entrySet()) {
                remaining = this.get(e.getKey()) - e.getValue();
                if(remaining == 0) this.resources.remove(e.getKey());
                else this.resources.put(e.getKey(),remaining);
            }
        }
        else throw new NonConsumablePackException();
    }

    /**
     * Consumes the specified amount of Resources of the given type.
     * If the current pack has not enough Resources an exception is raised.
     * Negative amounts are ignored.
     * @param resource the kind of resource to consume.
     * @param amount the amount of resources to consume.
     * @throws NonConsumablePackException if the are not enough Resources in the current pack.
     */
    public void consume(Resource resource, int amount) throws NonConsumablePackException {
        if(amount > 0) {
            //the possibility of non-consumable is implicitly handled (do nothing)
            int remaining = this.get(resource) - amount;
            if (remaining == 0) this.resources.remove(resource);
            else if (remaining > 0) this.resources.put(resource, remaining);
            else throw new NonConsumablePackException();
        }
    }

    /**
     * Applies a "discount" based on the Resources contained in a given pack.
     * Differently from .consume(), the maximum possible amount of each Resource is removed.
     * Special and non-special resources are equally treated.
     * @param pack the ResourcePack representing the total maximum Resources to remove.
     */
    public void discount(ResourcePack pack) {
        int remaining;
        // null is considered as an empty pack
        if(pack != null) {
            for (Map.Entry<Resource, Integer> e : pack.resources.entrySet()) {
                remaining = this.get(e.getKey()) - e.getValue();
                if (remaining <= 0) this.resources.remove(e.getKey());
                else this.resources.put(e.getKey(), remaining);
            }
        }
    }

    /**
     * Empties the pack from every Resource it contains.
     * The return value does not count special Resources even though they are also discarded.
     * @return the amount of non-special resources that have been flushed.
     */
    public int flush() {
        int flushedResources = this.size();
        this.resources.clear();
        return flushedResources;
    }

    /**
     * Flushes every Resource of the specified type and returns the amount of them;
     * @param resource the type of Resource to fully consume.
     * @return the amount of Resource of the specified type previously contained.
     */
    public int flush(Resource resource) {
        int amount = this.get(resource);
        // note: if the amount of resources is zero its record does not exists
        if(amount != 0) this.resources.remove(resource);
        return amount;
    }

    /**
     * Consumes a random Resource from the pack and returns its type;
     * if the given pack is empty returns VOID.
     * @return a random type of Resource previously contained in the pack.
     */
    public Resource getRandom() {
        if(this.isEmpty()) return Resource.VOID;
        else {
            Random random = new Random();
            Resource[] resourceTypes = this.resources.keySet().toArray(new Resource[0]);
            int randomIndex = random.nextInt(resourceTypes.length);
            try {
                this.consume(resourceTypes[randomIndex], 1);
            }
            catch (NonConsumablePackException e) {
                // this is something that cannot happen
                return Resource.VOID;
            }
            return resourceTypes[randomIndex];
        }
    }

    /**
     * Tests if the current pack does not contain any Resource, including special ones.
     * @return true if the ResourcePack is empty.
     */
    public boolean isEmpty() {
        // note: void is considered as a generic resource whose type is unknown.
        return resources.isEmpty();
    }

    /**
     * Creates a copy of the current pack with the same amount of Resources, including special ones.
     * @return a copy of the current ResourcePack.
     */
    public ResourcePack getCopy() {
        ResourcePack clone = new ResourcePack();
        clone.add(this);
        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        else if(o == this) return true;
        else if(!(o instanceof ResourcePack)) return false;
        else {
            ResourcePack rp = (ResourcePack) o;
            return this.resources.equals(rp.resources);
        }
    }

    @Override
    public String toString() {
        Gson parser = new Gson();
        return parser.toJson(this);
    }

    public static ResourcePack fromString(String resourcePack) {
        Gson parser = new Gson();
        return parser.fromJson(resourcePack,ResourcePack.class);
    }
}
