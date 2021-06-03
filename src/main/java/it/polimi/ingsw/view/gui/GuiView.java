package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.cards.LeaderCard;
import it.polimi.ingsw.supply.ResourcePack;
import it.polimi.ingsw.view.DevelopmentCardView;
import it.polimi.ingsw.view.Error;
import it.polimi.ingsw.view.View;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiView implements View {

    private static GuiView guiView;
    public DevelopmentCardView devCardStack = new DevelopmentCardView();

    private Map<String,String> eventHandler = new HashMap<>();

    public static GuiView getGuiView() {
        if(guiView == null) guiView = new GuiView();
        return guiView;
    }

    public synchronized void event(String eventType, String parameter) {
        eventHandler.put(eventType, parameter);
        notifyAll();
    }

    @Override
    public void tell(String message) {

    }

    @Override
    public void fancyTell(String message) {

    }

    @Override
    public String selectConnection() {
        return "127.0.0.1 2703";
    }

    @Override
    public synchronized String selectGame() {
        try {
            while (!eventHandler.containsKey("gameMode")) {
                wait();
            }
        } catch (InterruptedException ignored) {}
        return eventHandler.remove("gameMode");
    }

    @Override
    public void showError(Error error) {

    }

    @Override
    public String selectNickname() {
        return null;
    }

    @Override
    public int selectNumberOfPlayer() {
        return 0;
    }

    @Override
    public void gameStart() {

    }

    @Override
    public int[] selectLeader(List<LeaderCard> leaders) {
        return new int[0];
    }

    @Override
    public void showInitialAdvantage(ResourcePack advantage) {

    }

    @Override
    public void showAction(String action) {

    }

    @Override
    public String selectAction() {
        return null;
    }

    @Override
    public String selectLeaderAction() {
        return null;
    }

    @Override
    public String selectDevCard() {
        return null;
    }

    @Override
    public String selectDevCardPosition() {
        return null;
    }

    @Override
    public String selectMarbles() {
        return null;
    }

    @Override
    public void showGatheredResources(ResourcePack gathered) {

    }

    @Override
    public ResourcePack selectWhite(int amount) {
        return null;
    }

    @Override
    public String selectWarehouse() {
        return null;
    }

    @Override
    public String selectProduction() {
        return null;
    }

    @Override
    public void clearFactory() {

    }

    @Override
    public String getActiveProductions() {
        return null;
    }

    @Override
    public ResourcePack selectResources(int amount) {
        return null;
    }

    @Override
    public ResourcePack selectFreeRequirement(int amount) {
        return null;
    }

    @Override
    public void gameEnd() {

    }

    @Override
    public void update(String target, String state) {

    }
}
