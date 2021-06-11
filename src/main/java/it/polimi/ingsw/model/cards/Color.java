package it.polimi.ingsw.model.cards;

/**
 * Enumeration for all possible Development Cards colors.
 * @author Alessandro Mercurio
 */
public enum Color {
    GREEN("G","137035"),
    BLUE("B","19719F"),
    YELLOW("Y","A88E1F"),
    PURPLE("P","574776");

    private final String alias;
    private final String value;

    Color(String alias, String value) {
        this.alias = alias;
        this.value = value;
    }

    /**
     * Returns a short textual alias for the current Color.
     * @return an alias for the current Color.
     */
    public String getAlias() {
        return this.alias;
    }

    /**
     * Returns the value of the Color for JavaFX usage.
     * @return the String representing the Color.
     */
    public String getValue() {
        return this.value;
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

    /**
     * Returns the Color corresponding to the given value.
     * If there is no match for it, null is returned since the type is unknown.
     * @param value the value representation of the Color.
     * @return the corresponding Color with the given value.
     */
    public static Color toColorFromValue(String value) {
        for(Color color : Color.values()) {
            if(color.value.equals(value)) return color;
        }
        return null;
    }
}

