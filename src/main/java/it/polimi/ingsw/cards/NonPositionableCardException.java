package it.polimi.ingsw.cards;

public class NonPositionableCardException extends Exception
{
    public NonPositionableCardException()
    {
        super("Unable to position that card here...");
    }
}
