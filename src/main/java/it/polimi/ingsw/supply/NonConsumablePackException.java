package it.polimi.ingsw.supply;

public class NonConsumablePackException extends Exception {
    ResourcePack missingResources;

    public NonConsumablePackException() {
        super("Pack has not enough resources...");
    }

    public ResourcePack missingResources(ResourcePack toConsume, ResourcePack available) {
        missingResources = toConsume.getCopy();
        missingResources.discount(available);
        return missingResources;
    }
}
