package it.polimi.ingsw.model.resources;

/**
 * Represents a type of Resource. Resources can be either special (FAITHPOINT, VOID)
 * or non-special, and may be treated differently according to that.
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

    Resource(boolean special, String abbreviation) {
        this.special = special;
        this.alias = abbreviation;
    }

    /**
     * Tests if the current Resource is special or not.
     * @return true if the current Resource is special.
     */
    public boolean isSpecial() {
        return special;
    }

    /**
     * Returns a short textual alias (representative of the Color) for the current Resource.
     * @return an alias for the current Resource.
     */
    public String getAlias() {
        return this.alias;
    }

    /**
     * Returns the Resource corresponding to the given alias.
     * If there is no match for it, VOID is returned since the type is unknown.
     * @param alias the alternative textual representation of the Resource.
     * @return the corresponding Resource with the given alias.
     */
    public static Resource toResource(String alias) {
        for(Resource res : Resource.values()) {
            if(res.alias.equals(alias)) return res;
        }
        return Resource.VOID;
    }
}