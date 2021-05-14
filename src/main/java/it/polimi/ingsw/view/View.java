package it.polimi.ingsw.view;

public class View {

    public static FactoryView factory = new FactoryView();

    public static void update(String target,String state)
    {
        if(target == null || state == null) return;

        switch(target)
        {
            case "fact":
                View.factory.update(state);
                break;
        }
    }
}