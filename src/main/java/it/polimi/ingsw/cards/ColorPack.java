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
        public boolean equals(Object o) {
            if(o == null) return false;
            else if(o == this) return true;
            else if(!(o instanceof CardKey)) return false;
            else {
                CardKey ck = (CardKey) o;
                return (this.color == ck.color) && (this.level == ck.level);
            }
        }

        @Override
        public int hashCode() {
            return (this.color.ordinal() << 16) + this.level;
        }

        @Override
        public String toString() {
            return "{" + color + "," + level + "}";
        }
    }

    private final HashMap<CardKey,Integer> cardSet;

    public ColorPack() {
        cardSet = new HashMap<>();
    }

    /**
     * Adds a DevelopmentCard with the specified color and level to the ColorPack.
     * @param color the Color of the card to add to the pack.
     * @param level the level of the card to add to the pack.
     */
    public void addColor(Color color, int level) {
        CardKey cardToAdd = new CardKey(color, level);
        int amount = cardSet.getOrDefault(cardToAdd, 0) + 1;
        cardSet.put(cardToAdd,amount);
    }

    /**
     * Returns how many DevelopmentCards with the specified Color and level
     * are stored in the current ColorPack.
     * @param color the Color of the desired cards.
     * @param level the level of the desired cards.
     * @return the amount of cards with the given color and level within the current pack.
     */
    public int get(Color color,int level) {
        return this.cardSet.getOrDefault(new CardKey(color,level),0);
    }

    /**
     * Returns True if the given ColorPack is fully contained in the current one.
     * @param required the ColorPack to test.
     * @return a boolean that answer the question.
     */
    public boolean testRequirements(ColorPack required) {
        for(Map.Entry<CardKey,Integer> e : required.cardSet.entrySet()) {
            int available = 0;
            for(int level = e.getKey().level; level <= 3; level++) {
                available += this.cardSet.getOrDefault(new CardKey(e.getKey().color,level),0);
            }

            if(available < e.getValue()) return false;
        }
        return true;
    }

    /**
     * Tests if the current ColorPack is empty.
     * @return true if the current pack is empty, false otherwise.
     */
    public boolean isEmpty() {
        return this.cardSet.isEmpty();
    }

    /**
     * Returns a new ColorPack with the same development cards as the current one.
     * @return a copy of the current ColorPack.
     */
    public ColorPack getCopy() {
        ColorPack copy = new ColorPack();
        copy.cardSet.putAll(this.cardSet);
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        else if(o == this) return true;
        else if(!(o instanceof ColorPack)) return false;
        else {
            ColorPack cp = (ColorPack) o;
            return this.cardSet.equals(cp.cardSet);
        }
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("{");
        for(Map.Entry<CardKey,Integer> e : this.cardSet.entrySet()) {
            res.append("\n\t").append(e.getKey()).append(": ").append(e.getValue());
        }
        res.append("\n}");
        return res.toString();
    }
}
