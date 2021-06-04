package it.polimi.ingsw.model.resources;

public class NoSuchDevelopmentCardException extends Exception {
    public NoSuchDevelopmentCardException() {
        super("Unable to retrieve card...");
    }
}
