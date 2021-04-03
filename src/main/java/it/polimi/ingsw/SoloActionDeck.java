package it.polimi.ingsw;

import java.util.List;

public class SoloActionDeck {


    private List<SoloAction> action;
    private List<SoloAction> usedAction;

    public SoloActionDeck(List<SoloAction> action, List<SoloAction> usedAction) {
        this.action = action;
        this.usedAction = usedAction;
    }



    public SoloAction getSoloAction() {return action.get(0);}
    public void Shuffle (){}
}
