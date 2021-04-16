package it.polimi.ingsw.supply;

/**
 * Represents a resource. Resources can be either
 * special or non-special and may be treated differently according to that.
 * @author Francesco Tosini
 */
public enum Resource {
    COIN(false,"Y"),
    STONE(false,"G"),
    SERVANT(false,"P"),
    SHIELD(false,"B"),
    FAITHPOINT(true,"R"),
    VOID(true,"W");

    private final boolean special;
    private final String alias;

    Resource(boolean special, String abbreviation)
    {
        this.special = special;
        this.alias = abbreviation;
    }

    /**
     * Test if the current resource is special or not.
     * @return true if the current resource is special.
     */
    public boolean isSpecial()
    {
        return special;
    }

    /**
     * Returns a short textual alias for the current resource.
     * @return an alias for the current resource.
     */
    public String getAlias()
    {
        return this.alias;
    }

    /**
     * Returns the resource corresponding to the given alias.
     * If there is no match for it, VOID is returned since the type is unknown.
     * @param alias the alternative textual representation of a resource.
     * @return the corresponding resource to the given alias.
     */
    public static Resource toResource(String alias)
    {
        for(Resource res : Resource.values())
        {
            if(res.alias.equals(alias)) return res;
        }
        return Resource.VOID;
    }
}