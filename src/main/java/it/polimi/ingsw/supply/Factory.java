package it.polimi.ingsw.supply;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Factory class collects different productions and manages to get collective
 * requirements and products; only active (available) productions are considered,
 * while others are temporarily ignored.
 * @author Francesco Tosini
 */
public class Factory {
    private final List<ProductionEntry> productions;

    /**
     * The ProductionEntry inner class represent a single production in a Factory.
     * It handles the state of the production (active/inactive).
     * @author Francesco Tosini
     */
    private static class ProductionEntry {
        private final Production production;
        private transient boolean active;

        /**
         * Constructs a ProductionEntry with the given Production;
         * its state is initially set to inactive.
         * @param p the corresponding production.
         */
        ProductionEntry(Production p) {
            this.production = p;
            this.active = false;
        }

        /**
         * Sets the current ProductionEntry to active.
         */
        void activate() {
            this.active = true;
        }

        /**
         * Sets the current ProductionEntry to inactive.
         */
        void deactivate() {
            this.active = false;
        }

        /**
         * Tests if the current ProductionEntry is active.
         * @return true if active, false otherwise.
         */
        boolean isActive() {
            return this.active;
        }
    }

    /**
     * Constructs an empty Factory.
     */
    public Factory() {
        this.productions = new ArrayList<>();
    }

    /**
     * Adds the given production and set it as inactive.
     * @param p the production to add.
     */
    public void addProductionPower(Production p) {
        this.productions.add(new ProductionEntry(p));
    }

    /**
     * Removes the given production if present;
     * if multiple instances of the same production exist, only one is removed.
     * @param p the production to remove.
     */
    public void discardProductionPower(Production p) {
        for(ProductionEntry prod : this.productions) {
            if(prod.production.equals(p)) {
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
    public void discardProductionPower(Integer...index) {
        // Indexes are sorted in descending order
        // to avoid the re-indexing of the first productions.
        Arrays.sort(index, Collections.reverseOrder());
        for(int i : index)
            if(i >= 0 && i < this.productions.size()) this.productions.remove(i);
    }

    /**
     * Sets the productions with the given indexes to active;
     * all other productions are set to inactive.
     * Invalid indexes are ignored.
     * @param index the indexes of the productions to activate.
     */
    public void setActiveProduction(int...index) {
        deactivateProductions();
        for(int i : index)
            if(i >= 0 && i < this.productions.size())
                this.productions.get(i).activate();
    }

    /**
     * Sets all the productions to inactive.
     */
    public void deactivateProductions() {
        this.productions.forEach(ProductionEntry::deactivate);
    }

    /**
     * Returns the collective requirements for all the active productions;
     * inactive productions are ignored.
     * @return the ResourcePack with the required Resources for all active productions.
     */
    public ResourcePack productionRequirements() {
        ResourcePack requirements = new ResourcePack();
        for(ProductionEntry prod : this.productions) {
            if (prod.isActive()) {
                requirements.add(prod.production.getRequired());
            }
        }
        return requirements;
    }

    /**
     * Calls .produce() on every active production and sets it to inactive.
     * @return the ResourcePack with the collective products of previously active productions.
     * @see Production
     */
    public ResourcePack productionChain() {
        ResourcePack product = new ResourcePack();
        for (ProductionEntry prod : this.productions) {
            if (prod.isActive()) {
                product.add(prod.production.produce());
                prod.deactivate();
            }
        }
        return product;
    }

    @Override
    public String toString() {
        Gson parser = new Gson();
        return parser.toJson(this.productions.stream()
                .map(prod -> prod.production).collect(Collectors.toList()));
    }
}