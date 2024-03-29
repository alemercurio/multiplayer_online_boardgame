package it.polimi.ingsw.model.resources;

/**
 * Represents a Production Power with a specific input and a specific output;
 * The special Resource VOID stands for a generic non-special one.
 * The special Resource FAITHPOINT is ignored as a production requirement (input),
 * but can be a product (output).
 * @see Resource
 * @author Francesco Tosini
 */
public class Production {
    private final ResourcePack input;
    private final ResourcePack output;

    /**
     * Constructs a Production with the given input and output.
     * @param input the ResourcePack required to activate the Production.
     * @param output the ResourcePack produced.
     */
    public Production(ResourcePack input, ResourcePack output) {
        ResourcePack tmp_requirement = input.getCopy();
        // Flush the special Resource FAITHPOINT from the requirements.
        tmp_requirement.flush(Resource.FAITHPOINT);
        this.input = tmp_requirement;
        this.output = output.getCopy();
    }

    /**
     * Returns the pack of resources required to activate the Production.
     * @return the ResourcePack representing the input of the Production.
     */
    public ResourcePack getRequired() {
        return this.input.getCopy();
    }

    /**
     * Returns the pack of resources generated by the Production.
     * @return the ResourcePack representing the output of the Production.
     */
    public ResourcePack produce() {
        return this.output.getCopy();
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        else if(o == this) return true;
        else if(!(o instanceof Production)) return false;
        else {
            Production prod = (Production) o;
            return (this.input.equals(prod.input) && this.output.equals(prod.output));
        }
    }
}