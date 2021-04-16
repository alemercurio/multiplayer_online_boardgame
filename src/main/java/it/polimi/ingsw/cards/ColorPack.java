package it.polimi.ingsw.cards;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a group of Development Cards, with their colors and levels.
 * @see Color
 * @author Alessandro Mercurio
 */
public class ColorPack {

    /**
     * Inner class to represent both color and level of a single development card.
     */
    private static class CardKey {
        private final Color color;
        private final int level;

        public CardKey(Color color, int level) {
            this.color = color;
            this.level = level;
        }

        @Override
        public boolean equals(Object o)
        {
            if(o == null) return false;
            else if(o == this) return true;
            else if(!(o instanceof CardKey)) return false;
            else
            {
                CardKey ck = (CardKey) o;
                return (this.color == ck.color) && (this.level == ck.level);
            }
        }

        @Override
        public int hashCode()
        {
            return (this.color.ordinal() << 16) + this.level;
        }

        @Override
        public String toString()
        {
            return "{" + color + "," + level + "}";
        }
    }

    private final HashMap<CardKey,Integer> cardSet;

    public ColorPack() {
        cardSet = new HashMap<CardKey,Integer>();
    }

    /**
     * Adds a DevelopmentCard with the specified color and level to the ColorPack.
     * @param color the Color of the card to add to the pack.
     * @param level the level of the card to add to the pack.
     */
    public void addColor(Color color, int level)
    {
        CardKey cardToAdd = new CardKey(color, level);
        int amount = cardSet.getOrDefault(cardToAdd, 0) + 1;
        cardSet.put(cardToAdd,amount);
    }

    /**
     * Returns True if the given ColorPack is fully contained in the current one.
     * @param required the ColorPack to test.
     * @return a boolean that answer the question.
     */
    public boolean testRequirements(ColorPack required)
    {
        for(Map.Entry<CardKey,Integer> e : required.cardSet.entrySet())
            if(this.cardSet.getOrDefault(e.getKey(),0) < e.getValue())
                return false;
        return true;
    }

    /**
     * Returns a new ColorPack with the same development cards as the current one.
     * @return a copy of the current ColorPack.
     */
    public ColorPack getCopy()
    {
        ColorPack copy = new ColorPack();
        copy.cardSet.putAll(this.cardSet);
        return copy;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == null) return false;
        else if(o == this) return true;
        else if(!(o instanceof ColorPack)) return false;
        else
        {
            ColorPack cp = (ColorPack) o;
            return this.cardSet.equals(cp.cardSet);
        }
    }

    @Override
    public String toString()
    {
        String res = "{";
        for(Map.Entry<CardKey,Integer> e : this.cardSet.entrySet())
        {
            res = res + "\n\t" + e.getKey() + ": " + e.getValue();
        }
        res = res + "\n}";
        return res;
    }
}
