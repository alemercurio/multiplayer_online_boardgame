package it.polimi.ingsw.supply;

public class NonConsumablePackException extends Exception {
    private final ResourcePack missingResources;

    public NonConsumablePackException() {
        super("Pack has not enough resources...");
        this.missingResources = new ResourcePack();
    }

    public NonConsumablePackException(ResourcePack required,ResourcePack available) {
        super("Pack has not enough resources...");
        missingResources = required.getCopy();
        missingResources.discount(available);
    }

    public ResourcePack getMissing() {
        return this.missingResources;
    }
}
