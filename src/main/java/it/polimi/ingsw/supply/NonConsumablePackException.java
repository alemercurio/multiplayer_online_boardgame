package it.polimi.ingsw.supply;

public class NonConsumablePackException extends Exception {
    public NonConsumablePackException() {
        super("Pack has not enough resources...");
    }
}
