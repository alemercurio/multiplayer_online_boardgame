package it.polimi.ingsw.solo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class to represent full stack of Solo Action tokens.
 * @author Patrick Niantcho
 */
public class SoloActionDeck {
    private final List<SoloAction> tokens;
    private final List<SoloAction> tokensUsed;

    /**
     * Constructs a shuffled stack of SoloAction tokens.
     * @param soloActions the list of SoloActions that compose the stack.
     */
    public SoloActionDeck(List<SoloAction> soloActions) {
        this.tokens = new ArrayList<>(soloActions);
        this.tokensUsed = new ArrayList<>();
        Collections.shuffle(tokens);
    }

    /**
     * Returns the first SoloAction in the stack.
     * If no more SoloActions are available forces a shuffle.
     * @return the first SoloAction of the SoloActionDeck.
     */
    public SoloAction getSoloAction() {
        if(tokens.isEmpty()) this.shuffle();
        tokensUsed.add(tokens.get(0));
        return tokens.remove(0);
    }

    /**
     * Shuffles the full stack of SoloActions, used tokens included.
     */
    public void shuffle() {
        tokens.addAll(tokensUsed);
        Collections.shuffle(tokens);
        tokensUsed.clear();
    }
}
