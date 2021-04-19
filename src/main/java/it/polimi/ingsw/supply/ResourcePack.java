package it.polimi.ingsw.supply;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Represents a pack of resources with both special and non-special ones.
 * @see Resource
 * @author Francesco Tosini
 */
public class ResourcePack {

    final private Map<Resource,Integer> resources;

    /**
     * Constructs an empty pack of resources.
     */
    public ResourcePack()
    {
        this.resources = new HashMap<>();
    }

    /**
     * Constructs a pack with the given amounts of resources.
     * The order is the same as in Resource; non specified amounts
     * are treated as zeros, while exceeding parameters are ignored.
     * @param resourceAmounts the quantity of each resource.
     * @see Resource
     */
    public ResourcePack(int...resourceAmounts)
    {
        this();

        Resource[] res = Resource.values();
        for(int i = 0; i < resourceAmounts.length && i < 6; i++)
        {
            // convention: If the a amount of a resource is zero its record is omitted or removed from the map.
            if(resourceAmounts[i] != 0) this.resources.put(res[i], resourceAmounts[i]);
        }
    }

    /**
     * Returns the amount of non-special resources stored in the ResourcePack;
     * @return the quantity of non-special resources
     * @see Resource
     */
    public int size()
    {
        // Selects non-special resources and then evaluate their quantity by using a reduce function
        return this.resources.entrySet().stream().filter(e -> !(e.getKey().isSpecial()))
                .map(Map.Entry::getValue).reduce(0,Integer::sum);
    }

    /**
     * Returns the amount of a particular resource stored in the ResourcePack;
     * Unlike "consume" or "flush" it does not actually decrease its quantity.
     * @param resource the kind of resource whose amount is desired
     * @return the amount of the specified resource
     * @see Resource
     */
    public int get(Resource resource)
    {
        return resources.getOrDefault(resource,0);
    }

    /**
     * Adds the content of the given pack to the current one;
     * no resources are consumed in the process (even the ones from the given pack).
     * @param pack the resources to add to the current pack.
     * @return the current pack.
     */
    public ResourcePack add(ResourcePack pack)
    {
        pack.resources.forEach((res,amt) -> resources.merge(res,amt, Integer::sum));
        return this;
    }

    /**
     * Add the specified amount of the given resource to the current pack;
     * negative amounts are ignored.
     * @param resource the specific kind of resource to add.
     * @param amount the quantity of resource to add.
     * @return the current pack.
     */
    public ResourcePack add(Resource resource, int amount)
    {
        if(amount > 0)
        {
            int newAmount = this.get(resource) + amount;
            if (newAmount != 0) this.resources.put(resource, newAmount);
        }
        return this;
    }

    /**
     * Compares two ResourcePacks; Return true if the first has enough resources to satisfy the given one.
     * No side-effect are produced on the ResourcePacks. Resources are treated equally.
     * @param required the resources whose availability is to be tested.
     * @return true if it is possible to consume the required pack from the current one
     */
    public boolean isConsumable(ResourcePack required)
    {
        if(required != null)
        {
            for (Map.Entry<Resource, Integer> req : required.resources.entrySet())
                if (this.get(req.getKey()) < req.getValue()) return false;
        }
        // null is considered as an empty pack so the result is true in that particular case.
        return true;
    }

    /**
     * Consumes the resources in the current pack according to the given one.
     * If the specified pack is not consumable nothing happens. Resources are treated equally.
     * @param pack the amount of resources to consume.
     */
    public void consume(ResourcePack pack)
    {
        int reg;
        if(this.isConsumable(pack))
        {
            for(Map.Entry<Resource,Integer> e : pack.resources.entrySet())
            {
                reg = this.get(e.getKey()) - e.getValue();
                if(reg == 0) this.resources.remove(e.getKey());
                else this.resources.put(e.getKey(),reg);
            }
        }
    }

    /**
     * Consumes the specified amount of resources of the given type.
     * If the current pack has not enough resources nothing happens.
     * Negative amounts are ignored.
     * @param resource the kind of resource to consume.
     * @param amount the amount of resources to consume.
     */
    public void consume(Resource resource, int amount)
    {
        if(amount > 0)
        {
            // the non-consumeability is implicitly handled.
            int tmp_amount = this.get(resource) - amount;
            if (tmp_amount == 0) this.resources.remove(resource);
            else if (tmp_amount > 0) this.resources.put(resource, tmp_amount);
        }
    }


    /**
     * Apply a discount based on the resources contained in the given pack.
     * Special and non-special resources are equally treated.
     * The amount of each kind of resource is decreased at most to zero;
     * further discount is ignored.
     * @param pack the ResourcePack representing the discount to apply
     */
    public void discountPack(ResourcePack pack)
    {
        int reg;
        // null is considered as an empty pack.
        if(pack != null)
        {
            for (Map.Entry<Resource, Integer> e : pack.resources.entrySet())
            {
                reg = this.get(e.getKey()) - e.getValue();
                if (reg <= 0) this.resources.remove(e.getKey());
                else this.resources.put(e.getKey(), reg);
            }
        }
    }

    /**
     * Equals to flushing the special resource faithpoint.
     * @return the amount of faithpoints that have been consumed.
     */
    public int consumeFaithPoints()
    {
        return this.flush(Resource.FAITHPOINT);
    }

    /**
     * Consume a random resource from the pack and returns its type;
     * if the given pack is empty returns VOID.
     * @return a random kind of resource previously contained in the pack.
     */
    public Resource getRandom()
    {
        if(this.isEmpty()) return Resource.VOID;
        else {
            Random random = new Random();
            Resource[] res = this.resources.keySet().toArray(new Resource[0]);
            int rnd = random.nextInt(res.length);
            this.consume(res[rnd], 1);
            return res[rnd];
        }
    }

    /**
     * Empty the pack from every resource.
     * The return value does not count special-resources even though they are also discarded.
     * @return the amount of non-special resources that have been flushed.
     */
    public int flush()
    {
        int flushedResources = this.size();
        this.resources.clear();
        return flushedResources;
    }

    /**
     * Flushes every resource of the specified type and returns the amount of them;
     * @param resource the kind of resource to fully consume.
     * @return the amount of resources of the given kind that have been consumed.
     */
    public int flush(Resource resource)
    {
        int tmp_amount = this.get(resource);
        // note: if the amount of resources is zero its record does not exists.
        if(tmp_amount != 0) this.resources.remove(resource);
        return tmp_amount;
    }

    /**
     * Test if the current pack does not contain any resource, including special ones.
     * @return true if the ResourcePack is empty.
     */
    public boolean isEmpty()
    {
        // note: void is considered as a generic resource whose type is unknown.
        return resources.isEmpty();
    }

    /**
     * Create a copy of the current pack with the same amount of resources, including special ones.
     * @return a copy of the current ResourcePack
     */
    public ResourcePack getCopy()
    {
        ResourcePack clone = new ResourcePack();
        clone.add(this);
        return clone;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == null) return false;
        else if(o == this) return true;
        else if(!(o instanceof ResourcePack)) return false;
        else
        {
            ResourcePack rp = (ResourcePack) o;
            return this.resources.equals(rp.resources);
        }
    }

    @Override
    public String toString()
    {
        String res;
        boolean first = true;

        res = "{";
        for(Map.Entry<Resource,Integer> e : this.resources.entrySet())
        {
            if(!first)  res = res + ", ";
            else first = false;
            res = res + e.getKey() + ":" + e.getValue();
        }
        res = res + "}";
        return res;
    }
}
