package it.polimi.ingsw;

public class LorenzoIlMagnifico {
    private FaithTrack faith;
    private MarketBoard market;
    private SoloActionDeck action;


    public void playSoloAction (){
        boolean isShuffle = false;
        SoloAction currentAction = action.getSoloAction();
        isShuffle = currentAction.Apply(this);
        if(isShuffle){ // maybe if I directly do if (currentAction.Apply(this)) it's walk ?
            action.Shuffle();
        }
    }
    public void discard (Color color, int amount){
        market.discard(color, amount);
    }

    public void advancedFaithTrack(int step){
        faith.advance(step);
    }
}
