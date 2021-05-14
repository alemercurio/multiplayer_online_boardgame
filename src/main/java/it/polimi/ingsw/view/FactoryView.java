package it.polimi.ingsw.view;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import it.polimi.ingsw.supply.Production;
import it.polimi.ingsw.supply.ResourcePack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FactoryView {

    List<Production> productions;
    List<Integer> active;

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

    /**
     * Returns the collective requirements for all the active productions;
     * inactive productions are ignored.
     * @return the ResourcePack with the required Resources for all active productions.
     */
    public ResourcePack productionRequirements() {
        ResourcePack requirements = new ResourcePack();
        for(Production prod : this.productions) {
            requirements.add(prod.getRequired());
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
        for(Production prod : this.productions) {
            product.add(prod.produce());
        }
        return product;
    }

    public void print()
    {
        for(int i = 0; i < this.productions.size(); i++)
        {
            if(this.active.contains(i))
                Screen.setColor(46);
            System.out.print("\t");
            Screen.printCircledNumber(i);
            i++;
            Screen.reset();
            Screen.print(this.productions.get(i));
            System.out.print("\n");
        }

        System.out.print("\n\tTotal Cost: ");
        Screen.print(this.productionRequirements());
        System.out.print("\n\tProduct: ");
        Screen.print(this.productionResult());
    }
}
