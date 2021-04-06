package it.polimi.ingsw;

/**
 * Represents a production power with a specific input and a specific output;
 * The special resource VOID stands for a generic non-special one.
 * The special resource FAITHPOINT is ignored as a production requirement, but can
 * be a product.
 * @author Francesco Tosini
 */
public class Production
{
    private final ResourcePack input;
    private final ResourcePack output;

    /**
     * Constructs a Production with the given input and output.
     * @param input the pack of resources required to activate the production.
     * @param output the resources produced.
     */
    public Production(ResourcePack input, ResourcePack output)
    {
        ResourcePack tmp_requirement = input.getCopy();
        // Flush the special resource faithpoint from the requirements.
        tmp_requirement.flush(Resource.FAITHPOINT);
        this.input = tmp_requirement;
        this.output = output.getCopy();
    }

    /**
     * Returns the pack of resources required to activate the production.
     * @return the required input for the production.
     */
    public ResourcePack getRequired()
    {
        return this.input.getCopy();
    }

    /**
     * Returns a pack of resources representing the result of the production.
     * @return the pack of resources that have been produced.
     */
    public ResourcePack produce()
    {
        return this.output.getCopy();
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == null) return false;
        else if(o == this) return true;
        else if(!(o instanceof Production)) return false;
        else
        {
            Production prod = (Production) o;
            return (this.input.equals(prod.input) && this.output.equals(prod.output));
        }
    }
}