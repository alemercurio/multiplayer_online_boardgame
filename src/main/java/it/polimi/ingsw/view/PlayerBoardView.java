package it.polimi.ingsw.view;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.supply.Resource;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PlayerBoardView implements Observable {

    protected List<Resource> whitePower;
    private final List<InvalidationListener> observers = new ArrayList<>();

    protected void updateWhite(String whitePower) {
        Gson parser = new Gson();
        Type listOfResources = new TypeToken<List<Resource>>() {}.getType();
        this.whitePower = parser.fromJson(whitePower,listOfResources);

        for(InvalidationListener observer : this.observers)
            observer.invalidated(this);
    }

    public boolean hasWhitePower() {
        return (whitePower != null) && (!whitePower.isEmpty());
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        this.observers.add(invalidationListener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        this.observers.remove(invalidationListener);
    }
}
