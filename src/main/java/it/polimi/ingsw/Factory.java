package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The Factory class collects different productions and manages to get collective
 * requirement and products; only active productions are considered, while others are
 * temporarily ignored.
 * @author Francesco Tosini
 */
public class Factory
{
    private final List<ProductionEntry> productions;

    /**
     * The ProductionEntry class represent a single production in a Factory.
     * It handles the state of the production (active/inactive).
     * @author Francesco Tosini
     */
    private static class ProductionEntry
    {
        private final Production production;
        private boolean active;

        /**
         * Constructs a ProductionEntry with the given Production;
         * its state is initially set to inactive.
         * @param p the corresponding production.
         */
        ProductionEntry(Production p)
        {
            this.production = p;
            this.active = false;
        }

        /**
         * Set the current ProductionEntry to active.
         */
        void activate()
        {
            this.active = true;
        }

        /**
         * Set the current ProductionEntry to inactive.
         */
        void deactivate()
        {
            this.active = false;
        }

        /**
         * Test if the current ProductionEntry is active.
         * @return true if active, false otherwise.
         */
        boolean isActive()
        {
            return this.active;
        }
    }

    /**
     * Constructs an empty Factory.
     */
    public Factory()
    {
        this.productions = new ArrayList<ProductionEntry>();
    }

    /**
     * Add the given production and set it as inactive.
     * @param p the production to add.
     */
    public void addProductionPower(Production p)
    {
        this.productions.add(new ProductionEntry(p));
    }

    /**
     * Removes the given production if present;
     * if exists multiple instances of the same production only one is removed.
     * @param p the production to remove.
     */
    public void discardProductionPower(Production p)
    {
        for(ProductionEntry prod : this.productions)
        {
            if(prod.production.equals(p))
            {
                this.productions.remove(prod);
                return;
            }
        }
    }

    /**
     * Removes the existing productions corresponding to the given indexes;
     * indexes without a corresponding production are ignored.
     * @param index the indexes of the productions to discard.
     */
    public void discardProductionPower(Integer...index)
    {
        // Indexes are sorted in descending order
        // to avoid the re-indexing of the first productions.
        Arrays.sort(index, Collections.reverseOrder());
        for(int i : index)
            if(i >= 0 && i < this.productions.size()) this.productions.remove(i);
    }

    /**
     * Set the productions with the given indexes to active;
     * other productions are deactivated.
     * Invalid indexes are ignored.
     * @param index the indexes of the productions to activate.
     */
    public void setActiveProduction(int...index)
    {
        deactivateProduction();
        for(int i : index)
            if(i >= 0 && i < this.productions.size())
                this.productions.get(i).activate();
    }

    /**
     * Set all the productions to inactive.
     */
    public void deactivateProduction()
    {
        this.productions.forEach(ProductionEntry::deactivate);
    }

    /**
     * Returns the collective requirement for all the active productions;
     * inactive productions are ignored.
     * @return the required resources from all active productions.
     */
    public ResourcePack productionRequirements()
    {
        ResourcePack requirement = new ResourcePack();
        for (ProductionEntry prod : this.productions)
        {
            if (prod.isActive())
            {
                requirement.add(prod.production.getRequired());
            }
        }
        return requirement;
    }

    /**
     * Calls .produce() on every active production and set it to inactive.
     * @return the collective products of previously active productions.
     * @see Production
     */
    public ResourcePack productionChain()
    {
        ResourcePack product= new ResourcePack();
        for (ProductionEntry prod : this.productions)
        {
            if (prod.isActive())
            {
                product.add(prod.production.getRequired());
                prod.deactivate();
            }
        }
        return product;
    }
}