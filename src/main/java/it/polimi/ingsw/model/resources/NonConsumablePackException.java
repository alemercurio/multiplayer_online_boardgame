package it.polimi.ingsw.model.resources;

/**
 * Exception thrown when a ResourcePack has not enough Resources in respect to another, and
 * therefore cannot be used to consume the other pack (because the amount of Resources remaining
 * would be negative for some of the Resource types).
 * @see ResourcePack
 * @author Alessandro Mercurio
 */
public class NonConsumablePackException extends Exception {
    private final ResourcePack missingResources;

    public NonConsumablePackException() {
        super("Pack has not enough resources...");
        this.missingResources = new ResourcePack();
    }

    public NonConsumablePackException(ResourcePack required, ResourcePack available) {
        super("Pack has not enough resources...");
        missingResources = required.getCopy();
        missingResources.discount(available);
    }

    public ResourcePack getMissing() {
        return this.missingResources;
    }
}
