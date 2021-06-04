package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.cards.LeaderCard;
import it.polimi.ingsw.supply.ResourcePack;
import it.polimi.ingsw.view.Error;
import it.polimi.ingsw.view.GameEvent;
import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.ViewEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiView implements View {

    private int playerID;

    private static GuiView guiView;

    private final Map<ViewEvent,Object> eventHandler = new HashMap<>();

    public static GuiView getGuiView() {
        if(guiView == null) guiView = new GuiView();
        return guiView;
    }

    public synchronized void event(ViewEvent eventType, Object parameter) {
        eventHandler.put(eventType, parameter);
        notifyAll();
    }

    @Override
    public void throwEvent(GameEvent event) {

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

        /*try {
            while (!eventHandler.containsKey(ViewEvent.CONNECTION_INFO)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ /*}
        return (String) eventHandler.remove(ViewEvent.CONNECTION_INFO);*/
    }

    @Override
    public void setID(int playerID) {
        this.playerID = playerID;
    }

    @Override
    public synchronized String selectGame() {
        try {
            while (!eventHandler.containsKey(ViewEvent.GAMEMODE)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (String) eventHandler.remove(ViewEvent.GAMEMODE);
    }

    @Override
    public void showError(Error error) {

    }

    @Override
    public String selectNickname() {
        try {
            while (!eventHandler.containsKey(ViewEvent.NICKNAME)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (String) eventHandler.remove(ViewEvent.NICKNAME);
    }

    @Override
    public int selectNumberOfPlayer() {
        try {
            while (!eventHandler.containsKey(ViewEvent.NUMBER_OF_PLAYERS)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (int) eventHandler.remove(ViewEvent.NUMBER_OF_PLAYERS);
    }

    @Override
    public void gameStart() {

    }

    @Override
    public int[] selectLeader(List<LeaderCard> leaders) {
        try {
            while (!eventHandler.containsKey(ViewEvent.KEEP_LEADERS)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (int[]) eventHandler.remove(ViewEvent.KEEP_LEADERS);
    }

    @Override
    public void showInitialAdvantage(ResourcePack advantage) {

    }

    @Override
    public void showAction(String action) {

    }

    @Override
    public String selectAction() {
        try {
            while (!eventHandler.containsKey(ViewEvent.ACTION)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (String) eventHandler.remove(ViewEvent.ACTION);
    }

    @Override
    public String selectLeaderAction() {
        try {
            while (!eventHandler.containsKey(ViewEvent.LEADER_ACTION)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (String) eventHandler.remove(ViewEvent.LEADER_ACTION);
    }

    @Override
    public String selectDevCard() {
        try {
            while (!eventHandler.containsKey(ViewEvent.DEVCARD)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (String) eventHandler.remove(ViewEvent.DEVCARD);
    }

    @Override
    public String selectDevCardPosition() {
        try {
            while (!eventHandler.containsKey(ViewEvent.DEVCARD_POSITION)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (String) eventHandler.remove(ViewEvent.DEVCARD_POSITION);
    }

    @Override
    public String selectMarbles() {
        try {
            while (!eventHandler.containsKey(ViewEvent.MARBLES)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (String) eventHandler.remove(ViewEvent.MARBLES);
    }

    @Override
    public void showGatheredResources(ResourcePack gathered) {

    }

    @Override
    public ResourcePack selectWhite(int amount) {
        try {
            while (!eventHandler.containsKey(ViewEvent.CONVERT_WHITE)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (ResourcePack) eventHandler.remove(ViewEvent.CONVERT_WHITE);
    }

    @Override
    public String selectWarehouse() {
        try {
            while (!eventHandler.containsKey(ViewEvent.WAREHOUSE_CONFIG)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (String) eventHandler.remove(ViewEvent.WAREHOUSE_CONFIG);
    }

    @Override
    public String selectProduction() {
        try {
            while (!eventHandler.containsKey(ViewEvent.PRODUCTION)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (String) eventHandler.remove(ViewEvent.PRODUCTION);
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
        try {
            while (!eventHandler.containsKey(ViewEvent.CHOOSE_RESOURCES)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (ResourcePack) eventHandler.remove(ViewEvent.CHOOSE_RESOURCES);
    }

    @Override
    public ResourcePack selectFreeRequirement(int amount) {
        try {
            while (!eventHandler.containsKey(ViewEvent.FREE_REQUIREMENT)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (ResourcePack) eventHandler.remove(ViewEvent.FREE_REQUIREMENT);
    }

    @Override
    public void gameEnd() {

    }

    @Override
    public void update(String target, String state) {

    }
}
