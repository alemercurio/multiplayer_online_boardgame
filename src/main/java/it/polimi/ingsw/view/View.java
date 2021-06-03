package it.polimi.ingsw.view;

import it.polimi.ingsw.cards.LeaderCard;
import it.polimi.ingsw.supply.ResourcePack;

import java.util.List;

public interface View {

    void tell(String message);

    void fancyTell(String message);

    String selectConnection();

    int getID();

    void setID(int playerID);

    String selectGame();

    void showError(Error error);

    String selectNickname();

    int selectNumberOfPlayer();

    void gameStart();

    int[] selectLeader(List<LeaderCard> leaders);

    void showInitialAdvantage(ResourcePack advantage);

    void showAction(String action);

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

    void gameEnd();

    void update(String target,String state);
}
