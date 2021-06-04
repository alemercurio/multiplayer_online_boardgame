package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.resources.Production;
import it.polimi.ingsw.model.resources.ResourcePack;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DevelopmentCardStackTest {

    @Test
    public void testStoreDevCard()  {

        ResourcePack rp1_input = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_input = new ResourcePack(1,0,2,0,0);
        ResourcePack rp3_input = new ResourcePack(1,4,3,5,3);

        ResourcePack rp1_output = new ResourcePack(1,1,0,4,0);
        ResourcePack rp2_output = new ResourcePack(0,2,0,3,0);
        ResourcePack rp3_output = new ResourcePack(0,0,0,1,1);

        Production p1 = new Production(rp1_input, rp1_output);
        Production p2 = new Production(rp2_input, rp2_output);
        Production p3 = new Production(rp3_input, rp3_output);
        Production p4 = new Production(rp2_input, rp1_output);

        ResourcePack rp1_cost = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_cost = new ResourcePack(1,1,1,0,0);
        ResourcePack rp3_cost = new ResourcePack(2,2,0,0,0);

        DevelopmentCard devCard1 = new DevelopmentCard(3, rp1_cost, Color.BLUE,1, p1);
        DevelopmentCard devCard2 = new DevelopmentCard(2, rp2_cost, Color.GREEN,2, p2);
        DevelopmentCard devCard3 = new DevelopmentCard(3, rp3_cost, Color.PURPLE,3, p3);
        DevelopmentCard devCard4 = new DevelopmentCard(3, rp3_cost, Color.BLUE,4, p4);

        DevelopmentCardStack stack = new DevelopmentCardStack();

        // check that the stack is empty
        for (int pos = 1; pos <= 3; pos++)
            assertNull(stack.getDevCard(pos));

        //check that the exception NonPositionableCardException is correctly thrown when the position index is not valid
        assertTrue(testStoreDevCardThrowsException(stack, devCard1, 4));

        //check that no exception is thrown when the index position is valid, and the level 1 card is correctly append in the stack at an empty position
        assertFalse(testStoreDevCardThrowsException(stack, devCard1, 1));
        assertEquals(stack.getDevCard(1), devCard1);

        // check that the exception NonPositionableCardException is correctly thrown because it's not possible to append in the stack a not level 1 card at an empty position
        assertTrue(testStoreDevCardThrowsException(stack, devCard2, 2));
        assertTrue(testStoreDevCardThrowsException(stack, devCard3, 2));
        assertTrue(testStoreDevCardThrowsException(stack, devCard2, 3));
        assertTrue(testStoreDevCardThrowsException(stack, devCard3, 3));


        // check that the exception NonPositionableCardException is correctly thrown because it's not possible to append in the stack a not level 2 card on a level 1 card
        assertTrue(testStoreDevCardThrowsException(stack, devCard3, 1));
        assertTrue(testStoreDevCardThrowsException(stack, devCard4, 1));

        //check that no exception is thrown when the index position is valid, and a level 2 card is correctly append  in the stack on a level 1 card
        assertFalse(testStoreDevCardThrowsException(stack, devCard2, 1));
        assertEquals(stack.getDevCard(1), devCard2);

        // check that the exception NonPositionableCardException is correctly thrown because it's not possible to append in the stack a not level 3 card on a level 2 card
        assertTrue(testStoreDevCardThrowsException(stack, devCard4, 1));

        //check that no exception is thrown when the index position is valid, and a level 3 card is correctly append  in the stack on a level 2 card
        assertFalse(testStoreDevCardThrowsException(stack, devCard3, 1));
        assertEquals(stack.getDevCard(1), devCard3);

        //check that no exception is thrown when the index position is valid, and a level 4 card is correctly append  in the stack on a level 3 card
        assertFalse(testStoreDevCardThrowsException(stack, devCard4, 1));
        assertEquals(stack.getDevCard(1), devCard4);
    }

    @Test
    public void testGetDevCard()  {
        ResourcePack rp1_input = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_input = new ResourcePack(1,0,2,0,0);
        ResourcePack rp3_input = new ResourcePack(1,4,3,5,3);

        ResourcePack rp1_output = new ResourcePack(1,1,0,4,0);
        ResourcePack rp2_output = new ResourcePack(0,2,0,3,0);
        ResourcePack rp3_output = new ResourcePack(0,0,0,1,1);

        Production p1 = new Production(rp1_input, rp1_output);
        Production p2 = new Production(rp2_input, rp2_output);
        Production p3 = new Production(rp3_input, rp3_output);

        ResourcePack rp1_cost = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_cost = new ResourcePack(1,1,1,0,0);
        ResourcePack rp3_cost = new ResourcePack(2,2,0,0,0);

        DevelopmentCard devCard1 = new DevelopmentCard(3, rp1_cost, Color.BLUE,1, p1);
        DevelopmentCard devCard2 = new DevelopmentCard(2, rp2_cost, Color.GREEN,2, p2);
        DevelopmentCard devCard3 = new DevelopmentCard(3, rp3_cost, Color.PURPLE,3, p3);

        DevelopmentCardStack stack = new DevelopmentCardStack();

        // check if the stack is empty
        for (int pos = 1; pos <= 3; pos++)
            assertNull(stack.getDevCard(pos));

        // check if the stack remains unchanged if a card is appended with an invalid index position
        testStoreDevCardThrowsException(stack, devCard1, 4);
        assertNull(stack.getDevCard(1));


        testStoreDevCardThrowsException(stack, devCard1, 1);
        assertEquals(stack.getDevCard(1), devCard1);

        testStoreDevCardThrowsException(stack, devCard2, 1);
        assertEquals(stack.getDevCard(1), devCard2);

        testStoreDevCardThrowsException(stack, devCard3, 1);
        assertEquals(stack.getDevCard(1), devCard3);

        assertNull(stack.getDevCard(2));
    }

    @Test
    public void testGetDevCardOverload() {

        ResourcePack rp1_input = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_input = new ResourcePack(1,0,2,0,0);
        ResourcePack rp3_input = new ResourcePack(1,4,3,0,0);
        ResourcePack rp4_input = new ResourcePack(1,0,3,0,0);
        ResourcePack rp5_input = new ResourcePack(1,4,3,0,0);
        ResourcePack rp6_input = new ResourcePack(1,0,3,0,0);
        ResourcePack rp7_input = new ResourcePack(0,0,3,0,0);

        ResourcePack rp1_output = new ResourcePack(1,1,0,4,0);
        ResourcePack rp2_output = new ResourcePack(0,2,0,3,0);
        ResourcePack rp3_output = new ResourcePack(0,0,0,1,1);
        ResourcePack rp4_output = new ResourcePack(0,7,0,1,0);
        ResourcePack rp5_output = new ResourcePack(0,0,0,3,0);
        ResourcePack rp6_output = new ResourcePack(0,0,0,0,1);
        ResourcePack rp7_output = new ResourcePack(2,1,0,0,0);

        Production p1 = new Production(rp1_input, rp1_output);
        Production p2 = new Production(rp2_input, rp2_output);
        Production p3 = new Production(rp3_input, rp3_output);
        Production p4 = new Production(rp4_input, rp4_output);
        Production p5 = new Production(rp5_input, rp5_output);
        Production p6 = new Production(rp6_input, rp6_output);
        Production p7 = new Production(rp7_input, rp7_output);

        ResourcePack rp1_cost = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_cost = new ResourcePack(1,1,1,0,0);
        ResourcePack rp3_cost = new ResourcePack(2,2,0,0,0);
        ResourcePack rp4_cost = new ResourcePack(2,2,0,0,0);
        ResourcePack rp5_cost = new ResourcePack(2,0,1,1,0);
        ResourcePack rp6_cost = new ResourcePack(0,0,0,4,0);
        ResourcePack rp7_cost = new ResourcePack(0,2,1,1,1);

        DevelopmentCard devCard1 = new DevelopmentCard(3, rp1_cost, Color.BLUE,1, p1);
        DevelopmentCard devCard2 = new DevelopmentCard(2, rp2_cost, Color.GREEN,1, p2);
        DevelopmentCard devCard3 = new DevelopmentCard(1, rp3_cost, Color.YELLOW,1, p3);
        DevelopmentCard devCard4 = new DevelopmentCard(3, rp4_cost, Color.PURPLE,2, p4);
        DevelopmentCard devCard5 = new DevelopmentCard(4, rp5_cost, Color.BLUE,2, p5);
        DevelopmentCard devCard6 = new DevelopmentCard(1, rp6_cost, Color.GREEN,3, p6);
        DevelopmentCard devCard7 = new DevelopmentCard(2, rp7_cost, Color.PURPLE,3, p7);

        DevelopmentCardStack stack = new DevelopmentCardStack();

        List<DevelopmentCard> activeCards = new ArrayList<>();

        for(int i = 0; i < 3; i++)
            activeCards.add(null);
        assertEquals(stack.getDevCard(), activeCards);


        testStoreDevCardThrowsException(stack, devCard1, 2);
        activeCards.remove(1);
        activeCards.add(1, devCard1);
        assertEquals(stack.getDevCard(), activeCards);

        testStoreDevCardThrowsException(stack, devCard2, 1);
        activeCards.remove(0);
        activeCards.add(0, devCard2);
        assertEquals(stack.getDevCard(), activeCards);

        testStoreDevCardThrowsException(stack, devCard3, 3);
        activeCards.remove(2);
        activeCards.add(2, devCard3);
        assertEquals(stack.getDevCard(), activeCards);

        testStoreDevCardThrowsException(stack, devCard4, 1);
        activeCards.remove(0);
        activeCards.add(0, devCard4);
        assertEquals(stack.getDevCard(), activeCards);

        testStoreDevCardThrowsException(stack, devCard5, 2);
        activeCards.remove(1);
        activeCards.add(1, devCard5);
        assertEquals(stack.getDevCard(), activeCards);

        testStoreDevCardThrowsException(stack, devCard6, 1);
        activeCards.remove(0);
        activeCards.add(0, devCard6);
        assertEquals(stack.getDevCard(), activeCards);

        testStoreDevCardThrowsException(stack, devCard7, 2);
        activeCards.remove(1);
        activeCards.add(1, devCard7);
        assertEquals(stack.getDevCard(), activeCards);
    }

    @Test
    public void testGetColorPack() {
        ResourcePack rp1_input = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_input = new ResourcePack(1,0,2,0,0);
        ResourcePack rp3_input = new ResourcePack(1,4,3,0,0);
        ResourcePack rp4_input = new ResourcePack(1,0,3,0,0);
        ResourcePack rp5_input = new ResourcePack(1,4,3,0,0);
        ResourcePack rp6_input = new ResourcePack(1,0,3,0,0);
        ResourcePack rp7_input = new ResourcePack(0,0,3,0,0);

        ResourcePack rp1_output = new ResourcePack(1,1,0,4,0);
        ResourcePack rp2_output = new ResourcePack(0,2,0,3,0);
        ResourcePack rp3_output = new ResourcePack(0,0,0,1,1);
        ResourcePack rp4_output = new ResourcePack(0,7,0,1,0);
        ResourcePack rp5_output = new ResourcePack(0,0,0,3,0);
        ResourcePack rp6_output = new ResourcePack(0,0,0,0,1);
        ResourcePack rp7_output = new ResourcePack(2,1,0,0,0);

        Production p1 = new Production(rp1_input, rp1_output);
        Production p2 = new Production(rp2_input, rp2_output);
        Production p3 = new Production(rp3_input, rp3_output);
        Production p4 = new Production(rp4_input, rp4_output);
        Production p5 = new Production(rp5_input, rp5_output);
        Production p6 = new Production(rp6_input, rp6_output);
        Production p7 = new Production(rp7_input, rp7_output);

        ResourcePack rp1_cost = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_cost = new ResourcePack(1,1,1,0,0);
        ResourcePack rp3_cost = new ResourcePack(2,2,0,0,0);
        ResourcePack rp4_cost = new ResourcePack(2,2,0,0,0);
        ResourcePack rp5_cost = new ResourcePack(2,0,1,1,0);
        ResourcePack rp6_cost = new ResourcePack(0,0,0,4,0);
        ResourcePack rp7_cost = new ResourcePack(0,2,1,1,1);

        DevelopmentCard devCard1 = new DevelopmentCard(3, rp1_cost, Color.BLUE,1, p1);
        DevelopmentCard devCard2 = new DevelopmentCard(2, rp2_cost, Color.GREEN,1, p2);
        DevelopmentCard devCard3 = new DevelopmentCard(1, rp3_cost, Color.YELLOW,1, p3);
        DevelopmentCard devCard4 = new DevelopmentCard(3, rp4_cost, Color.PURPLE,2, p4);
        DevelopmentCard devCard5 = new DevelopmentCard(4, rp5_cost, Color.BLUE,2, p5);
        DevelopmentCard devCard6 = new DevelopmentCard(1, rp6_cost, Color.GREEN,3, p6);
        DevelopmentCard devCard7 = new DevelopmentCard(2, rp7_cost, Color.PURPLE,3, p7);

        DevelopmentCardStack stack = new DevelopmentCardStack();
        ColorPack colorPack1 = new ColorPack();

        testStoreDevCardThrowsException(stack, devCard1, 1);
        colorPack1.addColor(devCard1.getColor(), devCard1.getLevel());
        assertEquals(stack.getColorPack(), colorPack1);

        testStoreDevCardThrowsException(stack, devCard2, 2);
        colorPack1.addColor(devCard2.getColor(), devCard2.getLevel());
        assertEquals(stack.getColorPack(), colorPack1);

        testStoreDevCardThrowsException(stack, devCard3, 3);
        colorPack1.addColor(devCard3.getColor(), devCard3.getLevel());
        assertEquals(stack.getColorPack(), colorPack1);

        ColorPack colorPack2 = new ColorPack();
        testStoreDevCardThrowsException(stack, devCard4, 1);
        colorPack2.addColor(devCard4.getColor(), devCard4.getLevel());
        colorPack2.addColor(devCard1.getColor(), devCard1.getLevel());
        colorPack2.addColor(devCard2.getColor(), devCard2.getLevel());
        colorPack2.addColor(devCard3.getColor(), devCard3.getLevel());
        assertEquals(stack.getColorPack(), colorPack2);

        ColorPack colorPack3 = new ColorPack();
        testStoreDevCardThrowsException(stack, devCard5, 2);
        colorPack3.addColor(devCard4.getColor(), devCard4.getLevel());
        colorPack3.addColor(devCard1.getColor(), devCard1.getLevel());
        colorPack3.addColor(devCard5.getColor(), devCard5.getLevel());
        colorPack3.addColor(devCard2.getColor(), devCard2.getLevel());
        colorPack3.addColor(devCard3.getColor(), devCard3.getLevel());
        assertEquals(stack.getColorPack(), colorPack3);

        ColorPack colorPack4 = new ColorPack();
        testStoreDevCardThrowsException(stack, devCard6, 1);
        testStoreDevCardThrowsException(stack, devCard7, 2);
        colorPack4.addColor(devCard6.getColor(), devCard6.getLevel());
        colorPack4.addColor(devCard4.getColor(), devCard4.getLevel());
        colorPack4.addColor(devCard1.getColor(), devCard1.getLevel());
        colorPack4.addColor(devCard7.getColor(), devCard7.getLevel());
        colorPack4.addColor(devCard5.getColor(), devCard5.getLevel());
        colorPack4.addColor(devCard2.getColor(), devCard2.getLevel());
        colorPack4.addColor(devCard3.getColor(), devCard3.getLevel());
        assertEquals(stack.getColorPack(), colorPack4);
    }

    public boolean testStoreDevCardThrowsException(DevelopmentCardStack stack, DevelopmentCard card, int position){

        boolean thrown = false;

        try {
            stack.storeDevCard(card, position);
        }
        catch (NonPositionableCardException e){
            thrown = true;
        }
        return thrown;
    }

    @Test
    public void canBeStored() {

        ResourcePack rp1_input = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_input = new ResourcePack(1,0,2,0,0);
        ResourcePack rp3_input = new ResourcePack(1,4,3,0,0);
        ResourcePack rp4_input = new ResourcePack(1,0,3,0,0);
        ResourcePack rp5_input = new ResourcePack(1,4,3,0,0);
        ResourcePack rp6_input = new ResourcePack(1,0,3,0,0);
        ResourcePack rp7_input = new ResourcePack(0,0,3,0,0);

        ResourcePack rp1_output = new ResourcePack(1,1,0,4,0);
        ResourcePack rp2_output = new ResourcePack(0,2,0,3,0);
        ResourcePack rp3_output = new ResourcePack(0,0,0,1,1);
        ResourcePack rp4_output = new ResourcePack(0,7,0,1,0);
        ResourcePack rp5_output = new ResourcePack(0,0,0,3,0);
        ResourcePack rp6_output = new ResourcePack(0,0,0,0,1);
        ResourcePack rp7_output = new ResourcePack(2,1,0,0,0);

        Production p1 = new Production(rp1_input, rp1_output);
        Production p2 = new Production(rp2_input, rp2_output);
        Production p3 = new Production(rp3_input, rp3_output);
        Production p4 = new Production(rp4_input, rp4_output);
        Production p5 = new Production(rp5_input, rp5_output);
        Production p6 = new Production(rp6_input, rp6_output);
        Production p7 = new Production(rp7_input, rp7_output);

        ResourcePack rp1_cost = new ResourcePack(0,0,3,0,0);
        ResourcePack rp2_cost = new ResourcePack(1,1,1,0,0);
        ResourcePack rp3_cost = new ResourcePack(2,2,0,0,0);
        ResourcePack rp4_cost = new ResourcePack(2,2,0,0,0);
        ResourcePack rp5_cost = new ResourcePack(2,0,1,1,0);
        ResourcePack rp6_cost = new ResourcePack(0,0,0,4,0);
        ResourcePack rp7_cost = new ResourcePack(0,2,1,1,1);

        DevelopmentCard devCard1 = new DevelopmentCard(3, rp1_cost, Color.BLUE,1, p1);
        DevelopmentCard devCard2 = new DevelopmentCard(2, rp2_cost, Color.GREEN,1, p2);
        DevelopmentCard devCard3 = new DevelopmentCard(1, rp3_cost, Color.YELLOW,1, p3);
        DevelopmentCard devCard4 = new DevelopmentCard(3, rp4_cost, Color.PURPLE,2, p4);
        DevelopmentCard devCard5 = new DevelopmentCard(4, rp5_cost, Color.BLUE,2, p5);
        DevelopmentCard devCard6 = new DevelopmentCard(1, rp6_cost, Color.GREEN,3, p6);
        DevelopmentCard devCard7 = new DevelopmentCard(2, rp7_cost, Color.PURPLE,3, p7);

        DevelopmentCardStack stack = new DevelopmentCardStack();
        assertTrue(stack.canBeStored(devCard1,1));
        assertTrue(stack.canBeStored(devCard1,2));
        assertTrue(stack.canBeStored(devCard1,3));
        assertTrue(stack.canBeStored(devCard2,1));
        assertTrue(stack.canBeStored(devCard2,2));
        assertTrue(stack.canBeStored(devCard2,3));
        assertTrue(stack.canBeStored(devCard3,1));
        assertTrue(stack.canBeStored(devCard3,2));
        assertTrue(stack.canBeStored(devCard3,3));
        assertFalse(stack.canBeStored(devCard4,1));
        assertFalse(stack.canBeStored(devCard5,2));

        testStoreDevCardThrowsException(stack, devCard1, 1);
        testStoreDevCardThrowsException(stack, devCard2, 2);
        assertTrue(stack.canBeStored(devCard4,1));
        assertTrue(stack.canBeStored(devCard5,2));
        assertFalse(stack.canBeStored(devCard6,1));
        assertFalse(stack.canBeStored(devCard7,2));


        testStoreDevCardThrowsException(stack, devCard4, 1);
        testStoreDevCardThrowsException(stack, devCard5, 2);
        assertTrue(stack.canBeStored(devCard6,1));
        assertTrue(stack.canBeStored(devCard7,2));
    }
}