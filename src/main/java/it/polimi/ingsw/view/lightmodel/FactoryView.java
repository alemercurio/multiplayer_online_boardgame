package it.polimi.ingsw.view.lightmodel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import it.polimi.ingsw.model.resources.Production;
import it.polimi.ingsw.model.resources.ResourcePack;
import it.polimi.ingsw.util.Screen;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FactoryView implements Observable {

    private List<Production> productions;
    private final List<Integer> active;
    private final List<InvalidationListener> observers = new ArrayList<>();

    public FactoryView() {
        this.productions = new ArrayList<>();
        this.active = new ArrayList<>();
    }

    public void update(String productions) {
        Gson parser = new Gson();
        Type listOfProductions = new TypeToken<List<Production>>() {}.getType();
        this.productions = parser.fromJson(productions,listOfProductions);

        synchronized(this.observers) {
            for(InvalidationListener observer : this.observers)
                observer.invalidated(this);
        }
    }

    public void setActive(Integer index) {
        if(index >= 0 && index < this.productions.size())
            this.active.add(index);
    }

    public void setActive(List<Integer> indexes) {
        for(int index : indexes)
            this.setActive(index);
    }

    public void setInactive(Integer index) {
        this.active.remove(index);
    }

    public int numberOfActive() {
        return this.active.size();
    }

    public String getActive() {
        return new Gson().toJson(this.active);
    }

    public void clear() {
        this.active.clear();
    }

    /**
     * Returns the collective requirements for all the active productions;
     * inactive productions are ignored.
     * @return the ResourcePack with the required Resources for all active productions.
     */
    public ResourcePack productionRequirements() {
        ResourcePack requirements = new ResourcePack();
        for(int i = 0; i < this.productions.size(); i++) {
            if(this.active.contains(i)) requirements.add(this.productions.get(i).getRequired());
        }
        return requirements;
    }

    /**
     * Returns the collective products for all the active productions;
     * inactive productions are ignored.
     * @return the ResourcePack representing the result of all the active productions.
     */
    public ResourcePack productionResult() {
        ResourcePack product = new ResourcePack();
        for(int i = 0; i < this.productions.size(); i++) {
            if(this.active.contains(i)) product.add(this.productions.get(i).produce());
        }
        return product;
    }

    public List<Production> getProductions() {
        return Collections.unmodifiableList(this.productions);
    }

    public void print() {

        System.out.print("\n");

        for(int i = 0; i < this.productions.size(); i++) {
            if(this.active.contains(i))
                Screen.setColor(46);
            System.out.print("\t");
            Screen.printCircledNumber(i);
            Screen.reset();
            Screen.print(this.productions.get(i));
            System.out.print("\n");
        }
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        synchronized(this.observers) {
            this.observers.add(invalidationListener);
            invalidationListener.invalidated(this);
        }
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        synchronized(this.observers) {
            this.observers.remove(invalidationListener);
        }
    }
}
