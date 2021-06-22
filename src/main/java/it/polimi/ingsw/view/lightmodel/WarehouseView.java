package it.polimi.ingsw.view.lightmodel;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.cards.StockPower;
import it.polimi.ingsw.model.resources.NonConsumablePackException;
import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.model.resources.ResourcePack;
import it.polimi.ingsw.model.resources.Warehouse;
import it.polimi.ingsw.util.Screen;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class WarehouseView extends Warehouse implements Observable {

    public static class LimitedStock {

        protected Resource resource;
        protected final int size;
        protected int available;
        protected final boolean free;

        public LimitedStock(int size) {
            this.resource = Resource.VOID;
            this.size = size;
            this.available = 0;
            this.free = true;
        }

        public LimitedStock(Resource resource, int size) {
            this.resource = resource;
            this.size = size;
            this.available = 0;
            this.free = false;
        }

        public boolean isFree() {
            return this.free;
        }

        public Resource getResource() {
            return this.resource;
        }

        public void setResource(Resource resource) throws UnsupportedOperationException {
            if (this.free) this.resource = resource;
            else throw new UnsupportedOperationException();
        }

        public int size() {
            return this.size;
        }

        public int getAvailable() {
            return this.available;
        }

        public int store(int amount) {
            if (amount <= 0) return 0;

            this.available = this.available + amount;
            if (this.available > this.size) {
                int unableToStore = this.available - this.size;
                this.available = this.size;
                return unableToStore;
            } else return 0;
        }

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

        public ResourcePack consume(ResourcePack pack) {
            int toConsume = pack.flush(this.resource);
            pack.add(this.resource, this.consume(toConsume));
            return pack;
        }

        public int flush() {
            int available = this.available;

            if(this.free) this.resource = Resource.VOID;
            this.available = 0;

            return available;
        }

        public void print() {
            for (int i = 0; i < this.available; i++) {
                System.out.print("[");
                Screen.print(this.resource);
                System.out.print("] ");
            }

            for (int i = this.available; i < this.size; i++)
                System.out.print("[ ] ");
        }
    }

    public final List<LimitedStock> stock;
    public final ResourcePack pendingResources;
    private final List<InvalidationListener> observers = new ArrayList<>();

    public WarehouseView() {
        this.stock = new ArrayList<>();

        this.stock.add(new LimitedStock(1));
        this.stock.add(new LimitedStock(2));
        this.stock.add(new LimitedStock(3));

        this.pendingResources = new ResourcePack();
    }

    public void add(ResourcePack loot) {
        ResourcePack resources = loot.getCopy();
        resources.flush(Resource.VOID);
        resources.flush(Resource.FAITHPOINT);
        this.pendingResources.add(resources);
    }

    public void addStockPower(StockPower power) {
        this.stock.add(new LimitedStock(power.getType(),power.getLimit()));

        synchronized(this.observers) {
            for(InvalidationListener observer : this.observers)
                observer.invalidated(this);
        }
    }

    public List<StockPower> getStockPower() {

        List<StockPower> stockPowers = new ArrayList<>();

        for(int i = 3; i < this.stock.size(); i++) {

            LimitedStock shelf = this.stock.get(i);
            stockPowers.add(new StockPower(shelf.size,shelf.resource));
        }

        return stockPowers;
    }

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
            for(LimitedStock stock : this.stock)
                if(stock.isFree() && stock.getResource().equals(resource))
                    this.pendingResources.add(resource, stock.flush());

            this.pendingResources.add(dest.getResource(), dest.flush());
            dest.setResource(resource);
            amount = amount - dest.store(amount);
            return amount;
        }
    }

    public void move(int destination, Resource resource, int amount) {
        if(this.pendingResources.get(resource) < amount) amount = this.pendingResources.get(resource);
        try {
            this.pendingResources.consume(resource, this.stock(destination, resource, amount));
        } catch (NonConsumablePackException ignored) { /* this should not happen */ }
    }

    public boolean move(int destination, int source, int amount) {

        LimitedStock src;
        LimitedStock dest;

        try {
            src = this.stock.get(source - 1);
            dest = this.stock.get(destination - 1);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }

        if(amount > src.getAvailable()) amount = src.getAvailable();

        if(src.isFree() && dest.isFree()) {
            Resource toMove = src.getResource();
            this.pendingResources.add(toMove, src.flush());
            this.move(destination, toMove, amount);
        } else src.consume(this.stock(destination, src.getResource(), amount));

        return true;
    }

    public boolean isConsumable(ResourcePack pack) {
        ResourcePack required = pack.getCopy();
        required.flush(Resource.VOID);
        required.flush(Resource.FAITHPOINT);
        return this.getResources().isConsumable(required);
    }

    public ResourcePack consume(ResourcePack pack) {

        ResourcePack leftToConsume = pack.getCopy();

        for(LimitedStock shelf : this.stock)
            shelf.consume(leftToConsume);

        return leftToConsume;
    }

    public ResourcePack getPendingResources() {
        return this.pendingResources.getCopy();
    }

    public int done() {
        return this.pendingResources.flush();
    }

    public ResourcePack getResources() {

        ResourcePack resources = new ResourcePack();

        for(LimitedStock shelf : this.stock) {
            if(!shelf.getResource().isSpecial())
                resources.add(shelf.getResource(),shelf.getAvailable());
        }

        return resources;
    }

    public String getConfig() {

        ArrayList<Resource> resources = new ArrayList<>();
        ArrayList<Integer> amounts = new ArrayList<>();

        for(LimitedStock stock : this.stock)
        {
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

    public boolean update(String state) {

        Gson gson = new Gson();
        JsonElement element = gson.fromJson(state, JsonElement.class);
        JsonObject jsonObj = element.getAsJsonObject();

        Type listOfResources = new TypeToken<LinkedList<Resource>>() {}.getType();
        LinkedList<Resource> resources = gson.fromJson(jsonObj.get("resources"),listOfResources);

        int[] amounts = gson.fromJson(jsonObj.get("amounts"), int[].class);

        ResourcePack pending = gson.fromJson(jsonObj.get("pending"), ResourcePack.class);

        // The given configuration is applied

        for(int i = 0; i < amounts.length; i++) {
            LimitedStock shelf = this.stock.get(i);
            shelf.flush();

            if(shelf.isFree())
                shelf.setResource(resources.pollFirst());

            shelf.store(amounts[i]);
        }

        this.pendingResources.flush();
        this.pendingResources.add(pending);

        synchronized(this.observers) {
            for(InvalidationListener observer : this.observers)
                observer.invalidated(this);
        }

        return true;
    }

    public void print() {

        System.out.print("\n1:     ");
        this.stock.get(0).print();
        System.out.print("\n2:   ");
        this.stock.get(1).print();
        System.out.print("\n3: ");
        this.stock.get(2).print();
        System.out.print("\n");

        for (int i = 3; i < this.stock.size(); i++) {
            System.out.print((i + 1) + ": " + this.stock.get(i).resource + " ");
            this.stock.get(i).print();
            System.out.print("\n");
        }

        System.out.print("Pending: ");
        Screen.print(this.pendingResources);
        System.out.print("\n");
    }

    public void printWithoutPending() {

        System.out.print("\n1:     ");
        this.stock.get(0).print();
        System.out.print("\n2:   ");
        this.stock.get(1).print();
        System.out.print("\n3: ");
        this.stock.get(2).print();
        System.out.print("\n");

        for (int i = 3; i < this.stock.size(); i++) {
            System.out.print((i + 1) + ": " + this.stock.get(i).resource + " ");
            this.stock.get(i).print();
            System.out.print("\n");
        }

        System.out.print("\n");
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        synchronized(this.observers) {
            this.observers.add(invalidationListener);
        }
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        synchronized(this.observers) {
            this.observers.remove(invalidationListener);
        }
    }
}