package it.polimi.ingsw.model.resources;

/**
 * Represents a type of Resource. Resources can be either special (FAITHPOINT, VOID)
 * or non-special, and may be treated differently according to that.
 * @author Francesco Tosini
 */
public enum Resource {
    COIN(false,"Y","coin"),
    STONE(false,"G","stone"),
    SERVANT(false,"P","servant"),
    SHIELD(false,"B","shield"),
    FAITHPOINT(true,"R","faithmarker"),
    VOID(true,"W","question");

    private final boolean special;
    private final String alias;
    private final String image;

    Resource(boolean special, String abbreviation, String image) {
        this.special = special;
        this.alias = abbreviation;
        this.image = image;
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

    public String getImage() {
        return "/PNG/punchboard/"+this.image+".png";
    }
}