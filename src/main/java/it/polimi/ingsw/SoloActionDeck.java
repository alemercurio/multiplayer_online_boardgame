package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** represent the deck of available action
 * @author patrick Niantcho
 * @author patrick.niantcho@gmail.com
 */
public class SoloActionDeck {


    private List<SoloAction> action;
    private List<Integer> usedActionIndex;
    private List<Integer> index;

    /** constructor
     * @param action represents the list of action
     */
    public SoloActionDeck(ArrayList<SoloAction> action) {
        this.action = action;
        this.usedActionIndex = new ArrayList<Integer>(0);
        this.index = new ArrayList<Integer>(action.size());
        initializeIndex();
        Collections.shuffle(index);
    }

    /**
     * initialize the index list with number from 0 to the number of action available without duplicate
     */
    public void initializeIndex () {
        for (int i = 0; i < action.size(); i++){
            if (!index.contains(i))
                index.add(i);
        }

    }

    /**
     * @return return a random action, removing its index in the index list
     */
    public SoloAction getSoloAction() {
        usedActionIndex.add(index.get(0));
        return action.get(index.remove(0));
    }

    /**
     * shuffle the list of index of actions
     */
    public void Shuffle (){

        //reset index list
        initializeIndex();
        Collections.shuffle(index);

        //  empty the index list of used actions
        int length = usedActionIndex.size();
        for (int i = 0; i < length; i++) {
            usedActionIndex.remove(0);
        }

    }


}
