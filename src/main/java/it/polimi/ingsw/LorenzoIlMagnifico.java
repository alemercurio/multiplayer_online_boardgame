package it.polimi.ingsw;

/**
 * @author Patrick Niantcho
 * @author patrick.niantcho@gmail.com
 */
public class LorenzoIlMagnifico {
    private FaithTrack faith;
    private MarketBoard market;
    private SoloActionDeck action;
    private final Vatican vatican;

    /**
     * constructor
     * @param faith represents the FaithTrack of LorenzoIlMagnifico
     * @param market represents the marketBoard
     * @param action represents the list of actions available for LorenzoIlMagnifico
     * @param vatican represents the vatican
     */
    public LorenzoIlMagnifico(FaithTrack faith, MarketBoard market, SoloActionDeck action, Vatican vatican) {
        this.faith = faith;
        this.market = market;
        this.action = action;
        this.vatican = vatican;
    }

    /**
     * apply the effect of the any action which is ramdomly picked in the action deck
     */
    public void playSoloAction (){
        boolean isShuffle = false;
        SoloAction currentAction = action.getSoloAction();
        isShuffle = currentAction.Apply(this);
        if(isShuffle){
            action.Shuffle();
        }
    }

    /**
     *
     * @param color the color of the card to discard from the market
     * @param amount the number of the spicified card to discard from the market
     */
    public void discard (Color color, int amount){
        market.discard(color, amount);
    }

    /**
     *
     * @param step the number of squares the player must advance his marker
     */
    public void advancedFaithTrack(int step){

        FaithTrack opponentFaithTrack = vatican.getFaithTrack(0);
       if ( faith == opponentFaithTrack)
           opponentFaithTrack = vatican.getFaithTrack(1);

       vatican.wastedResources(opponentFaithTrack.getFaithTrackID(), step);


    }
}
