package it.polimi.ingsw.view.gui;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.controller.Action;
import it.polimi.ingsw.model.cards.Color;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.StockPower;
import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.model.resources.ResourcePack;
import it.polimi.ingsw.model.singleplayer.SoloCross;
import it.polimi.ingsw.model.singleplayer.SoloDiscard;
import it.polimi.ingsw.model.vatican.Vatican;
import it.polimi.ingsw.util.Screen;
import it.polimi.ingsw.view.gui.controllers.*;
import it.polimi.ingsw.view.gui.controllers.AdvantageSceneController;
import it.polimi.ingsw.view.gui.controllers.LootSceneController;
import it.polimi.ingsw.view.gui.controllers.PlayerBoardSceneController;
import it.polimi.ingsw.view.gui.controllers.ProductionSceneController;
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
import java.util.NoSuchElementException;

public class GuiView implements View {

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
    public final FaithView faithTrack = new FaithView(this.players);
    public PlayerBoardView playerBoard = new PlayerBoardView();

    public AdvantageSceneController advantageSetter;
    public PlayerBoardSceneController playerboard;
    public LootSceneController lootScene;
    public CardMarketController cardMarket;
    public ResourceMarketController resourceMarket;
    public LeaderboardController leaderboard;

    public String nickname;
    public String currentPlayer;
    public ProductionSceneController productionScene;

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
        switch(event) {
            case PLAYER_JOIN:
                Platform.runLater(() -> {
                    Alert join = new Alert(Alert.AlertType.INFORMATION);
                    join.setTitle("Join");
                    join.setHeaderText("A player is joining!");
                    join.setContentText(eventData);

                    if (join.showAndWait().get() == ButtonType.OK) {
                        join.close();
                    }
                });
                break;

            case PLAYER_DISCONNECT:
                Platform.runLater(() -> {
                    Alert disconnect = new Alert(Alert.AlertType.WARNING);
                    disconnect.setTitle("Disconnect");
                    disconnect.setHeaderText(eventData + " has disconnected from the game.");

                    if (disconnect.showAndWait().get() == ButtonType.OK) {
                        disconnect.close();
                    }
                });
                break;

            case POPE_FAVOUR:
                Platform.runLater(() -> {
                    Alert favour = new Alert(Alert.AlertType.INFORMATION);
                    favour.setTitle("Pope's Favour");
                    favour.setHeaderText("Pope's Favour activated!");

                    if (favour.showAndWait().get() == ButtonType.OK) {
                        favour.close();
                    }
                });

                playerboard.updateFaith();
                for(int i = 0; i < 3; i++) {
                    Vatican.ReportSection ps = faithTrack.popeSpaces[i];
                    if (ps.getState() == Vatican.State.GOT) {
                        playerboard.turnPope(i + 1);
                    }
                    else if(ps.getState() == Vatican.State.LOST) {
                        playerboard.discardPope(i+1);
                    }
                }
                break;

            case ROUND:
                currentPlayer = eventData;
                break;
        }
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
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Oops!");
            alert.setHeaderText(message);

            try {
                ButtonType response = alert.showAndWait().get();
                if (response == ButtonType.OK) {
                    if(message.equals("No game available!")) {
                        Platform.runLater(() -> guiApp.resetToMainMenu());
                    }
                    alert.close();
                }
            } catch (NoSuchElementException e) {
                if(message.equals("No game available!")) {
                    Platform.runLater(() -> guiApp.resetToMainMenu());
                }
                alert.close();
            }
        });
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
        this.players.setCurrentPlayerID(playerID);
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
        switch(error) {
            case NICKNAME_TAKEN:
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Non valid nickname!");
                    alert.setHeaderText("This nickname is already taken on the server.");
                    alert.setContentText("Choose another.");

                    try {
                        ButtonType response = alert.showAndWait().get();
                        if (response == ButtonType.OK) {
                            Platform.runLater(() -> guiApp.showNicknameField());
                            alert.close();
                        }
                    } catch (NoSuchElementException e) {
                        Platform.runLater(() -> guiApp.showNicknameField());
                        alert.close();
                    }
                });
                break;

            case INVALID_CARD_SELECTION:
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Not enough Resources");
                    alert.setHeaderText("You cannot buy this Card!");
                    alert.setContentText("Choose another one or change action.");

                    try {
                        ButtonType response = alert.showAndWait().get();
                        if (response == ButtonType.OK) {
                            alert.close();
                        }
                    } catch (NoSuchElementException e) {
                        alert.close();
                    }
                });
                break;

            case INVALID_POSITION:
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Not valid position");
                    alert.setHeaderText("You cannot put this Card here!");
                    alert.setContentText("Its level is wrong for this positioning.");

                    try {
                        ButtonType response = alert.showAndWait().get();
                        if (response == ButtonType.OK) {
                            alert.close();
                        }
                    } catch (NoSuchElementException e) {
                        alert.close();
                    }
                });
                break;

            case NOT_ENOUGH_RESOURCES:
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Not valid selection");
                    alert.setHeaderText("You cannot perform this set of productions.");
                    alert.setContentText("You do not have enough Resources.");

                    try {
                        ButtonType response = alert.showAndWait().get();
                        if (response == ButtonType.OK) {
                            alert.close();
                        }
                    } catch (NoSuchElementException e) {
                        alert.close();
                    }
                });
                break;

            case UNKNOWN_ERROR:
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Something strange happened!");
                    alert.setHeaderText("An unknown error has occurred...");
                    alert.setContentText("Let's hope it doesn't break anything!");

                    try {
                        ButtonType response = alert.showAndWait().get();
                        if (response == ButtonType.OK) {
                            alert.close();
                        }
                    } catch (NoSuchElementException e) {
                        alert.close();
                    }
                });
                break;
        }
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
        Platform.runLater(() -> guiApp.setMenu(guiApp.nicknameChoice, guiApp.newGameMenuBox));
        try {
            while (!eventHandler.containsKey(ViewEvent.NUMBER_OF_PLAYERS)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (int) eventHandler.remove(ViewEvent.NUMBER_OF_PLAYERS);
    }

    @Override
    public void gameStart() {
        showScene("/FXML/gamestart.fxml");
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
        Gson parser = new Gson();
        Action action;

        try { action = Action.valueOf(actionData[0]); }
        catch(Exception e) {
            this.showError(Error.UNKNOWN_ERROR);
            return;
        }

        switch(action) {
            case SOLO_ACTION:
                if(actionData.length < 2) {
                    this.showError(Error.UNKNOWN_ERROR);
                    return;
                }
                showScene("/FXML/lorenzo.fxml");
                System.out.print("\n>> Lorenzo il Magnifico: ");
                JsonObject data = parser.fromJson(actionData[1], JsonElement.class).getAsJsonObject();
                if(data.get("type").getAsString().equals("SoloCross"))
                    Screen.print(parser.fromJson(data.get("description"), SoloCross.class));
                else Screen.print(parser.fromJson(data.get("description"), SoloDiscard.class));
                System.out.print("\n");
                break;

            case PLAY_LEADER:
                if(actionData.length < 2) {
                    this.showError(Error.UNKNOWN_ERROR);
                    return;
                }
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Enemy action!");
                    alert.setHeaderText("Player: "+actionData[1]);
                    alert.setContentText("Action: plays a Leader");

                    try {
                        ButtonType response = alert.showAndWait().get();
                        if (response == ButtonType.OK) {
                            alert.close();
                        }
                    } catch (NoSuchElementException e) {
                        alert.close();
                    }
                });
                break;

            case DISCARD_LEADER:
                if(actionData.length < 2) {
                    this.showError(Error.UNKNOWN_ERROR);
                    return;
                }
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Enemy action!");
                    alert.setHeaderText("Player: "+actionData[1]);
                    alert.setContentText("Action: discards a Leader");

                    try {
                        ButtonType response = alert.showAndWait().get();
                        if (response == ButtonType.OK) {
                            alert.close();
                        }
                    } catch (NoSuchElementException e) {
                        alert.close();
                    }
                });
                break;

            case BUY_DEVELOPMENT_CARD:
                if(actionData.length < 4) {
                    this.showError(Error.UNKNOWN_ERROR);
                    return;
                }
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Enemy action!");
                    alert.setHeaderText("Player: "+actionData[1]);
                    alert.setContentText("Action: buys a "+Color.valueOf(actionData[2])+actionData[3]+" Development Card");

                    try {
                        ButtonType response = alert.showAndWait().get();
                        if (response == ButtonType.OK) {
                            alert.close();
                        }
                    } catch (NoSuchElementException e) {
                        alert.close();
                    }
                });
                break;

            case TAKE_RESOURCES:
                if(actionData.length < 3) {
                    this.showError(Error.UNKNOWN_ERROR);
                    return;
                }
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Enemy action!");
                    alert.setHeaderText("Player: "+actionData[1]);
                    alert.setContentText("Action: takes Resources -> "+ResourcePack.fromString(actionData[2]));

                    try {
                        ButtonType response = alert.showAndWait().get();
                        if (response == ButtonType.OK) {
                            alert.close();
                        }
                    } catch (NoSuchElementException e) {
                        alert.close();
                    }
                });
                break;

            case WASTED_RESOURCES:
                if(actionData.length < 3) {
                    this.showError(Error.UNKNOWN_ERROR);
                    return;
                }
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Enemy action!");
                    alert.setHeaderText("Player: "+actionData[1]);
                    alert.setContentText("Action: wastes Resources -> "+ResourcePack.fromString(actionData[2]));

                    try {
                        ButtonType response = alert.showAndWait().get();
                        if (response == ButtonType.OK) {
                            alert.close();
                        }
                    } catch (NoSuchElementException e) {
                        alert.close();
                    }
                });
                break;

            case ACTIVATE_PRODUCTION:
                if(actionData.length < 2) {
                    this.showError(Error.UNKNOWN_ERROR);
                    return;
                }
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Enemy action!");
                    alert.setHeaderText("Player: "+actionData[1]);
                    alert.setContentText("Action: activates Production");

                    try {
                        ButtonType response = alert.showAndWait().get();
                        if (response == ButtonType.OK) {
                            alert.close();
                        }
                    } catch (NoSuchElementException e) {
                        alert.close();
                    }
                });
                break;
        }
    }

    @Override
    public synchronized String selectAction() {
        Platform.runLater(() -> {
            guiApp.showScene("/FXML/playerboard.fxml");
            this.playerboard.setActive();
            this.playerboard.showMenu();
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
        showScene("/FXML/playerboard.fxml");
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
    public synchronized String selectProduction() {
        try {
            while (!eventHandler.containsKey(ViewEvent.PRODUCTION)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (String) eventHandler.remove(ViewEvent.PRODUCTION);
    }

    @Override
    public void clearFactory() {
        this.factory.clear();
    }

    @Override
    public String getActiveProductions() {
        return this.factory.getActive();
    }

    @Override
    public synchronized ResourcePack selectResources(int amount) {
        try {
            while (!eventHandler.containsKey(ViewEvent.CHOOSE_RESOURCES)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (ResourcePack) eventHandler.remove(ViewEvent.CHOOSE_RESOURCES);
    }

    @Override
    public synchronized ResourcePack selectFreeRequirement(int amount) {
        Platform.runLater(() -> this.productionScene.selectFreeRequirement(amount));
        try {
            while (!eventHandler.containsKey(ViewEvent.FREE_REQUIREMENT)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (ResourcePack) eventHandler.remove(ViewEvent.FREE_REQUIREMENT);
    }

    @Override
    public synchronized ResourcePack selectProduct(int amount) {
        Platform.runLater(() -> this.productionScene.selectResources(amount));
        try {
            while (!eventHandler.containsKey(ViewEvent.CHOOSE_PRODUCT)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (ResourcePack) eventHandler.remove(ViewEvent.CHOOSE_PRODUCT);
    }

    @Override
    public boolean playLeaderAction() {
        return false;
    }

    @Override
    public void gameEnd() {
        showScene("/FXML/leaderboard.fxml");
        Platform.runLater(() -> leaderboard.endGame());
    }

    @Override
    public void update(String target, String state) {

        if(target == null || state == null) return;

        switch(target) {

            case "fact":
                this.factory.update(state);
                break;

            case "playerID":
                this.players.setCurrentPlayerID(Integer.parseInt(state));
                break;

            case "player":
                this.players.update(state);
                break;

            case "white":
                this.playerBoard.updateWhite(state);
                break;

            case "discount":
                this.playerBoard.updateDiscount(state);
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
