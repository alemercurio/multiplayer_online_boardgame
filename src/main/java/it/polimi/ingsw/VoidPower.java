package it.polimi.ingsw;

/**
 * Leader Card special ability transforming the white Marbles extracted from the Market into the specified Resource.
 * @author Alessandro Mercurio
 */

public class VoidPower implements Power {
    private Resource resource;

    /**
     * The activation of this Power adds the possibility to change a white Marble into a Resource to the list of possible exchanges.
     * @param board the Player's PlayerBoard.
     */
    public void activate(PlayerBoard board){
        board.addWhite(resource);
    }
}
