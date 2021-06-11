package it.polimi.ingsw.view;

import it.polimi.ingsw.controller.Action;
import it.polimi.ingsw.controller.Error;
import it.polimi.ingsw.controller.GameEvent;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.resources.ResourcePack;

import java.util.List;

public interface View {

    void throwEvent(GameEvent event,String eventData);

    void disableGameEvent();

    void enableGameEvent();

    void flushGameEvent();

    void tell(String message);

    String selectConnection();

    void setID(int playerID);

    String selectGame();

    void showError(Error error);

    String selectNickname();

    boolean selectResume();

    int selectNumberOfPlayer();

    void gameStart();

    int[] selectLeader(List<LeaderCard> leaders);

    void showInitialAdvantage(ResourcePack advantage);

    void showAction(String...actionData);

    String selectAction();

    String selectLeaderAction();

    String selectDevCard();

    String selectDevCardPosition();

    String selectMarbles();

    void showGatheredResources(ResourcePack gathered);

    ResourcePack selectWhite(int amount);

    String selectWarehouse();

    String selectProduction();

    void clearFactory();

    String getActiveProductions();

    ResourcePack selectResources(int amount);

    ResourcePack selectFreeRequirement(int amount);

    boolean playLeaderAction();

    void gameEnd();

    void update(String target,String state);
}
