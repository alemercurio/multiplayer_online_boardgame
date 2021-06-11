package it.polimi.ingsw.view.gui;

import com.google.gson.Gson;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.StockPower;
import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.model.resources.ResourcePack;
import it.polimi.ingsw.view.gui.controllers.AdvantageSceneController;
import it.polimi.ingsw.view.gui.controllers.LootSceneController;
import it.polimi.ingsw.view.gui.controllers.PlayerBoardSceneController;
import it.polimi.ingsw.view.lightmodel.*;
import it.polimi.ingsw.controller.Error;
import it.polimi.ingsw.controller.GameEvent;
import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.ViewEvent;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiView implements View {

    private int playerID;
    private GuiApp guiApp;

    private static GuiView guiView;

    public final GameView players = new GameView();
    public MarketView market = new MarketView();
    public LeaderView leaderStack = new LeaderView();
    public DevelopmentCardStackView devCardStack = new DevelopmentCardStackView();
    public DevelopmentCardView buyed = new DevelopmentCardView();
    public FactoryView factory = new FactoryView();
    public WarehouseView warehouse = new WarehouseView();
    public ResourcePack strongbox = new ResourcePack();
    private final FaithView faithTrack = new FaithView(this.players);
    public PlayerBoardView playerBoard = new PlayerBoardView();

    public AdvantageSceneController advantageSetter;
    public PlayerBoardSceneController playerboard;
    public LootSceneController lootScene;

    private final Map<ViewEvent,Object> eventHandler = new HashMap<>();

    public static GuiView getGuiView() {
        if(guiView == null) guiView = new GuiView();
        return guiView;
    }

    public void setGuiApp(GuiApp app) {
        guiApp = app;
    }

    public synchronized void event(ViewEvent eventType, Object parameter) {
        eventHandler.put(eventType, parameter);
        notifyAll();
    }

    @Override
    public void throwEvent(GameEvent event,String eventData) {

    }

    @Override
    public void disableGameEvent() {

    }

    @Override
    public void enableGameEvent() {

    }

    @Override
    public void flushGameEvent() {

    }

    @Override
    public void tell(String message) {

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
    public synchronized void showError(Error error) {
        Platform.runLater(() -> {
            if(error.compareTo(Error.INVALID_CARD_SELECTION)==0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Not enough Resources");
                alert.setHeaderText("You cannot buy this Card!");
                alert.setContentText("You do not have enough Resources.");

                if(alert.showAndWait().get() == ButtonType.OK){
                    alert.close();
                }
            }
            if(error.compareTo(Error.INVALID_POSITION)==0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Not valid position");
                alert.setHeaderText("You cannot put this Card here!");
                alert.setContentText("Its level is wrong for this positioning.");

                if(alert.showAndWait().get() == ButtonType.OK){
                    alert.close();
                }
            }
        });
    }

    @Override
    public synchronized String selectNickname() {
        try {
            while (!eventHandler.containsKey(ViewEvent.NICKNAME)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (String) eventHandler.remove(ViewEvent.NICKNAME);
    }

    @Override
    public boolean selectResume() {
        return false;
    }

    @Override
    public synchronized int selectNumberOfPlayer() {
        try {
            while (!eventHandler.containsKey(ViewEvent.NUMBER_OF_PLAYERS)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (int) eventHandler.remove(ViewEvent.NUMBER_OF_PLAYERS);
    }

    @Override
    public void gameStart() {
        Platform.runLater(() -> guiApp.showScene("/FXML/gamestart.fxml"));
    }

    @Override
    public synchronized int[] selectLeader(List<LeaderCard> leaders) {
        Platform.runLater(() -> leaderStack.showChoices(leaders));
        try {
            while (!eventHandler.containsKey(ViewEvent.KEEP_LEADERS)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (int[]) eventHandler.remove(ViewEvent.KEEP_LEADERS);
    }

    @Override
    public synchronized void showInitialAdvantage(ResourcePack advantage) {
        Platform.runLater(() -> {
            guiApp.showScene("/FXML/advantage.fxml");
            advantageSetter.setAdvantage(advantage);
        });
    }

    @Override
    public void showAction(String...actionData) {

    }

    @Override
    public synchronized String selectAction() {
        Platform.runLater(() -> {
            guiApp.showScene("/FXML/playerboard.fxml");
            this.playerboard.setActive();
        });
        try {
            while (!eventHandler.containsKey(ViewEvent.ACTION)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (String) eventHandler.remove(ViewEvent.ACTION);
    }

    @Override
    public synchronized String selectLeaderAction() {
        try {
            while (!eventHandler.containsKey(ViewEvent.LEADER_ACTION)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (String) eventHandler.remove(ViewEvent.LEADER_ACTION);
    }

    @Override
    public synchronized String selectDevCard() {
        try {
            while (!eventHandler.containsKey(ViewEvent.DEVCARD)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (String) eventHandler.remove(ViewEvent.DEVCARD);
    }

    @Override
    public synchronized String selectDevCardPosition() {
        GuiView.getGuiView().showScene("/FXML/playerboard.fxml");
        Platform.runLater(() -> GuiView.getGuiView().playerboard.positionCard(buyed));
        try {
            while (!eventHandler.containsKey(ViewEvent.DEVCARD_POSITION)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        Platform.runLater(() -> GuiView.getGuiView().playerboard.notYourTurn());
        return (String) eventHandler.remove(ViewEvent.DEVCARD_POSITION);
    }

    @Override
    public synchronized String selectMarbles() {
        try {
            while (!eventHandler.containsKey(ViewEvent.MARBLES)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (String) eventHandler.remove(ViewEvent.MARBLES);
    }

    @Override
    public synchronized void showGatheredResources(ResourcePack gathered) {
        if(gathered.get(Resource.VOID)>0 && playerBoard.hasWhitePower()) {
            Platform.runLater(() -> {
                guiApp.showScene("/FXML/loot.fxml");
                lootScene.setPack(gathered);
            });
        }
    }

    @Override
    public ResourcePack selectWhite(int amount) {
        Platform.runLater(() -> {
            lootScene.askWhite(amount);
        });
        try {
            while (!eventHandler.containsKey(ViewEvent.CONVERT_WHITE)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (ResourcePack) eventHandler.remove(ViewEvent.CONVERT_WHITE);
    }

    @Override
    public synchronized String selectWarehouse() {
        Platform.runLater(() -> {
            guiApp.showScene("/FXML/playerboard.fxml");
            playerboard.invalidated(this.warehouse);
        });
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
    public synchronized ResourcePack selectResources(int amount) {
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
    public boolean playLeaderAction() {
        return false;
    }

    @Override
    public void gameEnd() {

    }

    @Override
    public void update(String target, String state) {

        if(target == null || state == null) return;

        switch(target) {

            case "fact":
                this.factory.update(state);
                break;

            case "player":
                this.players.update(state);
                break;

            case "white":
                this.playerBoard.updateWhite(state);
                break;

            case "WHConfig":
                this.warehouse.update(state);
                break;

            case "WH":
                this.warehouse.addStockPower(new Gson().fromJson(state, StockPower.class));
                break;

            case "strongbox":
                this.strongbox = new Gson().fromJson(state,ResourcePack.class);
                break;

            /*case "leaders":
                this.leaderStack.update(state);
                break;*/

            case "market:res":
                this.market.updateResourceMarket(state);
                break;

            case "market:card":
                this.market.updateCardMarket(state);
                break;

            case "devCards":
                this.devCardStack.update(state);
                break;

            case "faith:track":
                this.faithTrack.setTrack(state);
                break;

            case "faith:config":
                this.faithTrack.update(state);
                break;
        }
    }

    public void showScene(String fxml) {
        Platform.runLater(() -> guiApp.showScene(fxml));
    }
}
