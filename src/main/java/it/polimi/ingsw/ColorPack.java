package it.polimi.ingsw;

import java.util.HashMap;


/**
 * Class to represent a group of Development Cards, with their colors and levels.
 * @author Alessandro Mercurio
 */

public class ColorPack {

    /**
     * Inner class to provide a two-values key for a Map.
     */
    private class Pair {
        private final Color color;
        private final int level;

        public Pair(Color color, int level) {
            this.color = color;
            this.level = level;
        }
    }

    private HashMap<Pair, Integer> cardSet;

    /**
     * Add a DevelopmentCard with a certain Color and level to the ColorPack.
     * @param color the Color of the card to add to the pack.
     * @param level the level of the card to add to the pack.
     */
    public void addColor(Color color, int level) {
        Pair cardToAdd = new Pair(color, level);
        int number = cardSet.get(cardToAdd);
        number = number + 1;
        cardSet.put(cardToAdd, number);
    }

    /**
     * Return True if the ColorPack passed as input is contained in this ColorPack.
     * @param required the ColorPack to test.
     * @return a boolean that answer the question.
     */
    public boolean testRequirements(ColorPack required) {
        boolean available = True;
        for(Pair pair : required.cardSet.keySet()) {
            Integer num = this.cardSet.get(pair);
            if(num==null) {
                available = False;
            }
            else if(required.cardSet.get(pair) > num) {
                available = False;
            }
        }
        return available;
    }
}
