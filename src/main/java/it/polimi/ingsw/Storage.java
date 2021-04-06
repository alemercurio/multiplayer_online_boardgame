package it.polimi.ingsw;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Represents the storage section of a player board;
 * it consists of a warehouse, a strongbox with unlimited capacity
 * and optional leaders' stocks.
 * @author Francesco Tosini
 */
public class Storage {
    private final ResourcePack strongbox;
    private final Warehouse warehouse;
    private final Map<Resource,List<StockPower>> leaderStock;

    /**
     * Constructs an empty storage.
     */
    public Storage()
    {
        this.strongbox = new ResourcePack();
        this.warehouse = new Warehouse();
        this.leaderStock = new HashMap<>();
    }

    public void addStockPower(StockPower stock)
    {
        // Test if the list associated with this kind of resource
        // already exists and constructs it otherwise.
        if(!this.leaderStock.containsKey(stock.getType()))
        {
            this.leaderStock.put(stock.getType(),new LinkedList<>());
        }
        this.leaderStock.get(stock.getType()).add(stock);
    }

    public ResourcePack getAllResource()
    {
        ResourcePack resources = this.strongbox.getCopy();
        resources.add(this.warehouse.getResources());
        this.leaderStock.values().forEach(list -> list.forEach(stock -> resources.add(stock.getResources())));
        return resources;
    }

    public void stockWarehouse(ResourcePack loot)
    {
        this.warehouse.add(loot);
    }

    public void stockStrongbox(ResourcePack pack)
    {
        this.strongbox.add(pack);
    }

    public void autoConsume(ResourcePack pack)
    {
        if(this.getAllResource().isConsumable(pack))
        {
            // Consume resources from the warehouse
            pack = this.warehouse.consume(pack);

            // Consume resources from leaders' stocks.
            int tmp_amount, stock_index = 0;
            for(Resource res : this.leaderStock.keySet())
            {
                tmp_amount = pack.get(res);
                while(tmp_amount > 0 && (stock_index < this.leaderStock.get(res).size()))
                {
                    // TODO: FINIRE IL CODICE.
                }
            }

            // Consume resources from the strongbox
            this.strongbox.consume(pack);
        }
    }

    public ResourcePack consumeWarehouse(ResourcePack pack)
    {
        return this.warehouse.consume(pack);
    }

    public void consumeStrongbox(ResourcePack pack)
    {
        this.strongbox.consume(pack);
    }
}
