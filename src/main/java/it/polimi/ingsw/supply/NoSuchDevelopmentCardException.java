package it.polimi.ingsw.supply;

public class NoSuchDevelopmentCardException extends Exception {
    public NoSuchDevelopmentCardException() {
        super("Unable to retrieve card...");
    }
}
