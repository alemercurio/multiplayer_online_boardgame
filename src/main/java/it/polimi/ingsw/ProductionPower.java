package it.polimi.ingsw;

/**
 * Leader Card special ability giving the player an additional production power.
 * @author Alessandro Mercurio
 */

public class ProductionPower implements Power {
    private Production production;

    /**
     * The activation of this Power adds the additional Production to the list of all productions available for the Player.
     * @param board the Player's PlayerBoard, to include with the others the Production that the Leader permits.
     */
    public void activate(PlayerBoard board){
        board.addProduction(production);
    }
}
