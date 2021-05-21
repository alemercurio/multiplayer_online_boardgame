package it.polimi.ingsw.view;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import it.polimi.ingsw.supply.Production;
import it.polimi.ingsw.supply.ResourcePack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FactoryView {

    private List<Production> productions;
    private final List<Integer> active;

    public FactoryView()
    {
        this.productions = new ArrayList<>();
        this.active = new ArrayList<>();
    }

    public void update(String productions)
    {
        Gson parser = new Gson();
        Type listOfProductions = new TypeToken<List<Production>>() {}.getType();
        this.productions = parser.fromJson(productions,listOfProductions);
    }

    public void setActive(Integer index)
    {
        if(index >= 0 && index < this.productions.size())
            this.active.add(index);
    }

    public void setInactive(Integer index)
    {
        this.active.remove(index);
    }

    public int numberOfActive()
    {
        return this.active.size();
    }

    public String getActive()
    {
        return new Gson().toJson(this.active);
    }

    public void clear()
    {
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

    public void print()
    {
        System.out.print("\n");

        for(int i = 0; i < this.productions.size(); i++)
        {
            if(this.active.contains(i))
                Screen.setColor(46);
            System.out.print("\t");
            Screen.printCircledNumber(i);
            Screen.reset();
            Screen.print(this.productions.get(i));
            System.out.print("\n");
        }

        System.out.print("\n\tTotal Cost: ");
        Screen.print(this.productionRequirements());
        System.out.print("\n\tAvailable: ");
        Screen.print(View.strongbox.getCopy().add(View.warehouse.getResources()));
        System.out.print("\n\tProduct: ");
        Screen.print(this.productionResult());

        System.out.print("\n");
    }
}
