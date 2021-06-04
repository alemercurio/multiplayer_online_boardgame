package it.polimi.ingsw.model.cards;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Class to represent the three stacks of Development Cards in a Player's board.
 * @author Alessandro Mercurio
 */
public class DevelopmentCardStack {
    private final List<LinkedList<DevelopmentCard>> devCards; // List of 3 Lists, one for each stack.

    /**
     * Constructs and empty DevelopmentCardStack.
     */
    public DevelopmentCardStack() {
        this.devCards = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            devCards.add(new LinkedList<>());
        }
    }

    /**
     * Tests if the given DevelopmentCard can be stored in the current DevelopmentCardStack.
     * @param devCard the DevelopmentCard to position.
     * @return true if the given Card can be positioned, false otherwise.
     */
    public boolean canBeStored(DevelopmentCard devCard) {
        for(LinkedList<DevelopmentCard> stack : this.devCards) {
            int current = (stack.isEmpty()) ? 0 : stack.getFirst().getLevel();
            if(devCard.getLevel() == current + 1) return true;
        }
        return false;
    }

    /**
     * Tests if the given DevelopmentCard can be stored in the current DevelopmentCardStack, at a specific position.
     * @param devCard the DevelopmentCard to position.
     * @param position the position where the Card should be stored.
     * @return true if the given Card can be positioned, false otherwise.
     */
    public boolean canBeStored(DevelopmentCard devCard, int position){
        LinkedList<DevelopmentCard> stack;
        stack = this.devCards.get(position - 1);
        int current = (stack.isEmpty()) ? 0 : stack.getFirst().getLevel();
        return devCard.getLevel() == current + 1;
    }

    /**
     * Tests if a card with the given level can be stored in the specified position of the
     * current DevelopmentCardStack.
     * @param level the level of DevelopmentCard to position.
     * @param position the position where the card should be placed.
     * @return true if the a Card with the given level can be positioned, false otherwise.
     */
    public boolean canBeStored(int level,int position) {

        LinkedList<DevelopmentCard> stack = this.devCards.get(position - 1);
        if(stack.isEmpty()) return (level == 1);
        else return (level == stack.getFirst().getLevel() + 1);
    }

    /**
     * Puts a DevelopmentCard on top of one of the three stacks.
     * Position are indexed from one and if the given index is invalid nothing happens.
     * @param card     the DevelopmentCard to add.
     * @param position the index of the chosen stack.
     */
    public void storeDevCard(DevelopmentCard card, int position) throws NonPositionableCardException {
        if (position >= 1 && position <= 3) {
            LinkedList<DevelopmentCard> stack = this.devCards.get(position - 1);
            int current = (stack.isEmpty()) ? 0 : stack.getFirst().getLevel();

            // Because DevelopmentCard objects are immutable, it is not necessary to make a copy.
            if(card.getLevel() == current + 1) stack.addFirst(card);
            else throw new NonPositionableCardException();
        }
        else throw new NonPositionableCardException();
    }

    /**
     * Gets the DevelopmentCard on top of the specified stack;
     * if the given position is invalid or empty returns null.
     * @param position the chosen stack (1,2 or 3).
     * @return the DevelopmentCard on top of the stack or null.
     */
    public DevelopmentCard getDevCard(int position) {
        if (position >= 1 && position <= 3) {
            if (this.devCards.get(position - 1).isEmpty()) return null;
            else return this.devCards.get(position - 1).getFirst();
        } else return null;
    }

    /**
     * Returns a list of the Cards that are on top of their stack;
     * if a stack is empty, its position in the list is occupied with null.
     * @return a list containing the higher card of each stack.
     */
    public List<DevelopmentCard> getDevCard() {
        List<DevelopmentCard> activeCards = new ArrayList<>();
        for (LinkedList<DevelopmentCard> stack : this.devCards) {
            // Because DevelopmentCard objects are immutable they can be shared.
            if (stack.isEmpty()) activeCards.add(null);
            else activeCards.add(stack.getFirst());
        }
        return activeCards;
    }

    /**
     * Returns a ColorPack representing all Cards in the current DevelopmentCardStack.
     * @return the ColorPack representation of all played DevelopmentCards.
     */
    public ColorPack getColorPack() {
        ColorPack pack = new ColorPack();
        for (int i = 0; i < 3; i++)
            for (DevelopmentCard card : this.devCards.get(i))
                pack.addColor(card.getColor(), card.getLevel());
        return pack;
    }

    /**
     * Returns the amount of victory points given by the DevelopmentCards
     * stored in the current DevelopmentCardStack.
     * @return the amount of victory points.
     */
    public int getPoints() {
        return this.devCards.stream()
                .mapToInt(stack -> stack.stream()
                .mapToInt(Card::getPoints).sum()).sum();
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
