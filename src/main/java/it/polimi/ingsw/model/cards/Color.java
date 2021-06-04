package it.polimi.ingsw.model.cards;

/**
 * Enumeration for all possible Development Cards colors.
 * @author Alessandro Mercurio
 */
public enum Color {
    GREEN("G"),
    BLUE("B"),
    YELLOW("Y"),
    PURPLE("P");

    private final String alias;

    private Color(String alias) {
        this.alias = alias;
    }

    /**
     * Returns a short textual alias for the current Color.
     * @return an alias for the current Color.
     */
    public String getAlias() {
        return this.alias;
    }

    /**
     * Returns the Color corresponding to the given alias.
     * If there is no match for it, null is returned since the type is unknown.
     * @param alias the alternative textual representation of the Color.
     * @return the corresponding Color with the given alias.
     */
    public static Color toColor(String alias) {
        for(Color color : Color.values()) {
            if(color.alias.equals(alias)) return color;
        }
        return null;
    }
}

