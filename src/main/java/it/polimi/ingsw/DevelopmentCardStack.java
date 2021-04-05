package it.polimi.ingsw;

import java.util.ArrayList;

/**
 * Class to represent the three stacks of Development Cards in a Player's board.
 * @author Alessandro Mercurio
 */

public class DevelopmentCardStack {
    private ArrayList<DevelopmentCard>[] devCards; //array of 3 ArrayList, one for each stack

    /**
     * Standard constructor to create the array of three ArrayList.
     */
    public DevelopmentCardStack() {
        devCards = new ArrayList[3];
        devCards[0] = new ArrayList<DevelopmentCard>();
        devCards[1] = new ArrayList<DevelopmentCard>();
        devCards[2] = new ArrayList<DevelopmentCard>();
    }

    /**
     * Put a DevelopmentCard on top of one of the three stacks.
     * @param card the card to add.
     * @param position the stack chosen.
     */
    public void storeDevCard(DevelopmentCard card, int position) {
        if (position >=1 & position <=3) {
            devCards[position].add(card);
        }
    }

    /**
     * Get the DevelopmentCard on top of the specified stack.
     * @param position the stack chosen (1,2 or 3).
     * @return the DevelopmentCard on top of the stack.
     */
    public DevelopmentCard getDevCard(int position) {
        if (position >=1 & position <=3) {
            ArrayList<DevelopmentCard> stack = devCards[position];
            return stack.get(stack.size() - 1);
        }
        else {
            return null;
        }
    }

    /**
     * Get one of the three full Cards stacks.
     * @param position the stack chosen.
     * @return the full DevelopmentCards stack in the form of ArrayList.
     */
    public ArrayList<DevelopmentCard> getStack(int position) {
        if (position >=1 & position <=3) {
            return devCards[position];
        }
        else {
            return null;
        }
    }

    /**
     * Creates and return the full ColorPack representing all Cards in the three stacks.
     * @return the ColorPack representation of all played DevelopmentCards.
     */
    public ColorPack getColorPack() {
        int index = 0;
        ColorPack pack = new ColorPack();
        while(index < 3) {
            for(DevelopmentCard card : devCards[index])
            {
                pack.addColor(card.getColor(), card.getLevel());
            }
            index++;
        }
        return pack;
    }
}
