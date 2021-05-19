package it.polimi.ingsw.view;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.supply.Resource;

import java.lang.reflect.Type;
import java.util.List;

public class PlayerBoardView
{
    protected List<Resource> whitePower;

    protected void updateWhite(String whitePower)
    {
        Gson parser = new Gson();
        Type listOfResources = new TypeToken<List<Resource>>() {}.getType();
        this.whitePower = parser.fromJson(whitePower,listOfResources);
    }

    public boolean hasWhitePower()
    {
        return (whitePower != null) && (!whitePower.isEmpty());
    }
}
