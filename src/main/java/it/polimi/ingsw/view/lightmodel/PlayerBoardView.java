package it.polimi.ingsw.view.lightmodel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.model.resources.ResourcePack;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PlayerBoardView implements Observable {

    public List<Resource> whitePower;
    public ResourcePack discount;

    private final List<InvalidationListener> observers = new ArrayList<>();

    public void updateWhite(String whitePower) {
        Gson parser = new Gson();
        Type listOfResources = new TypeToken<List<Resource>>() {}.getType();
        this.whitePower = parser.fromJson(whitePower,listOfResources);

        synchronized(this.observers) {
            for(InvalidationListener observer : this.observers)
                observer.invalidated(this);
        }
    }

    public void updateDiscount(String discount) {
        this.discount = ResourcePack.fromString(discount);

        synchronized(this.observers) {
            for(InvalidationListener observer : this.observers)
                observer.invalidated(this);
        }
    }

    public boolean hasWhitePower() {
        return (this.whitePower != null) && (!this.whitePower.isEmpty());
    }

    public boolean hasDiscount() {
        return (this.discount != null) && (!this.discount.isEmpty());
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
