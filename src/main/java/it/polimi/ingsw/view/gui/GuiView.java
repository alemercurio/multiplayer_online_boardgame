package it.polimi.ingsw.view.gui;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.controller.Action;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.StockPower;
import it.polimi.ingsw.model.resources.ResourcePack;
import it.polimi.ingsw.model.singleplayer.SoloCross;
import it.polimi.ingsw.model.singleplayer.SoloDiscard;
import it.polimi.ingsw.model.vatican.Vatican;
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
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class GuiView implements View {

    private GuiApp guiApp;

    private static GuiView guiView;

    public boolean online = false;
    public boolean solo = false;
    private boolean firstRound = true;

    public final GameView players = new GameView();
    public MarketView market = new MarketView();
    public LeaderView leaderStack = new LeaderView();
    public DevelopmentCardStackView devCardStack = new DevelopmentCardStackView();
    public DevelopmentCardView bought = new DevelopmentCardView();
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
    public LorenzoController lorenzo;
    public ProductionSceneController productionScene;
    public LeaderActionController leaderScene;

    public String nickname;
    public String currentPlayer;

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

                    //automatic resizing
                    join.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));

                    // alert styling
                    DialogPane dialogPane = join.getDialogPane();
                    dialogPane.getStylesheets().add(
                            getClass().getResource("/CSS/style.css").toExternalForm());
                    dialogPane.getStyleClass().add("alertStyle");

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
                    //automatic resizing
                    disconnect.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
                    // alert styling
                    DialogPane dialogPane = disconnect.getDialogPane();
                    dialogPane.getStylesheets().add(
                            getClass().getResource("/CSS/style.css").toExternalForm());
                    dialogPane.getStyleClass().add("alertStyle");

                    if (disconnect.showAndWait().get() == ButtonType.OK) {
                        disconnect.close();
                    }
                });
                break;

            case PLAYER_RECONNECT:
                Platform.runLater(() -> {
                    Alert reconnect = new Alert(Alert.AlertType.WARNING);
                    reconnect.setTitle("Reconnect");
                    reconnect.setHeaderText(eventData + " has reconnected to the game.");
                    //automatic resizing
                    reconnect.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
                    // alert styling
                    DialogPane dialogPane = reconnect.getDialogPane();
                    dialogPane.getStylesheets().add(
                            getClass().getResource("/CSS/style.css").toExternalForm());
                    dialogPane.getStyleClass().add("alertStyle");

                    if (reconnect.showAndWait().get() == ButtonType.OK) {
                        reconnect.close();
                    }
                });
                break;

            case POPE_FAVOUR:
                Platform.runLater(() -> {
                    Alert favour = new Alert(Alert.AlertType.INFORMATION);
                    favour.setTitle("Pope's Favour");
                    favour.setHeaderText("Pope's Favour activated!");

                    //automatic resizing
                    favour.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
                    // alert styling
                    DialogPane dialogPane = favour.getDialogPane();
                    dialogPane.getStylesheets().add(
                            getClass().getResource("/CSS/style.css").toExternalForm());
                    dialogPane.getStyleClass().add("alertStyle");

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
        if(message.equals("Done!")) {
            if(!solo) {
                GuiView.getGuiView().showScene("/FXML/playerboard.fxml");
                Platform.runLater(() -> GuiView.getGuiView().playerboard.showMenu());
            }
        }
        else {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Hey!");
                alert.setHeaderText(message);

                //automatic resizing
                alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
                // alert styling
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add(
                        getClass().getResource("/CSS/style.css").toExternalForm());
                dialogPane.getStyleClass().add("alertStyle");

                try {
                    ButtonType response = alert.showAndWait().get();
                    if (response == ButtonType.OK) {
                        if (message.equals("No game available!")) {
                            Platform.runLater(() -> guiApp.resetToMainMenu());
                        } else if (message.equals("Server unavailable!")) {
                            Platform.exit();
                        }
                    }
                } catch (NoSuchElementException e) {
                    if (message.equals("No game available!")) {
                        Platform.runLater(() -> guiApp.resetToMainMenu());
                    } else if (message.equals("Server unavailable!")) {
                        Platform.exit();
                    }
                    alert.close();
                }
            });
        }
    }

    @Override
    public synchronized String selectConnection() {
        try {
            while (!eventHandler.containsKey(ViewEvent.CONNECTION_INFO)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (String) eventHandler.remove(ViewEvent.CONNECTION_INFO);
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

                    //automatic resizing
                    alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
                    // alert styling
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(
                            getClass().getResource("/CSS/style.css").toExternalForm());
                    dialogPane.getStyleClass().add("alertStyle");

                    try {
                        ButtonType response = alert.showAndWait().get();
                        if (response == ButtonType.OK) {
                            Platform.runLater(() -> {
                                guiApp.setMenu(guiApp.newJoinBox, guiApp.nicknameChoice);
                                guiApp.showNicknameField();
                            });
                            alert.close();
                        }
                    } catch (NoSuchElementException e) {
                        Platform.runLater(() -> {
                            guiApp.setMenu(guiApp.newJoinBox, guiApp.nicknameChoice);
                            guiApp.showNicknameField();
                        });
                        alert.close();
                    }
                });
                break;

            case INVALID_CARD_SELECTION:
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Oops!");
                    alert.setHeaderText("You cannot buy this Card!");
                    alert.setContentText("Choose another one or change action.");

                    //automatic resizing
                    alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
                    // alert styling
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(
                            getClass().getResource("/CSS/style.css").toExternalForm());
                    dialogPane.getStyleClass().add("alertStyle");

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

                    //automatic resizing
                    alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
                    // alert styling
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(
                            getClass().getResource("/CSS/style.css").toExternalForm());
                    dialogPane.getStyleClass().add("alertStyle");

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

                    //automatic resizing
                    alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
                    // alert styling
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(
                            getClass().getResource("/CSS/style.css").toExternalForm());
                    dialogPane.getStyleClass().add("alertStyle");

                    try {
                        ButtonType response = alert.showAndWait().get();
                        if (response == ButtonType.OK) {
                            GuiView.getGuiView().factory.clear();
                            alert.close();
                        }
                    } catch (NoSuchElementException e) {
                        GuiView.getGuiView().factory.clear();
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

                    //automatic resizing
                    alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
                    // alert styling
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(
                            getClass().getResource("/CSS/style.css").toExternalForm());
                    dialogPane.getStyleClass().add("alertStyle");

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

            /*default:
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Oops!");
                    alert.setContentText(error.toString());

                    //automatic resizing
                    alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
                    // alert styling
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(
                            getClass().getResource("/CSS/style.css").toExternalForm());
                    dialogPane.getStyleClass().add("alertStyle");

                    try {
                        ButtonType response = alert.showAndWait().get();
                        if (response == ButtonType.OK) {
                            alert.close();
                        }
                    } catch (NoSuchElementException e) {
                        alert.close();
                    }
                });
                break;*/
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
    public String getNickname() {
        return this.players.getNickname(this.players.getCurrentPlayerID());
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
        this.enableLeaderUpdate();
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
                JsonObject data = parser.fromJson(actionData[1], JsonElement.class).getAsJsonObject();
                if(data.get("type").getAsString().equals("SoloCross"))
                   Platform.runLater(() -> lorenzo.setAction(parser.fromJson(data.get("description"), SoloCross.class)));
                else Platform.runLater(() -> lorenzo.setAction(parser.fromJson(data.get("description"), SoloDiscard.class)));
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

                    //automatic resizing
                    alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
                    // alert styling
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(
                            getClass().getResource("/CSS/style.css").toExternalForm());
                    dialogPane.getStyleClass().add("alertStyle");

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

                    //automatic resizing
                    alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
                    // alert styling
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(
                            getClass().getResource("/CSS/style.css").toExternalForm());
                    dialogPane.getStyleClass().add("alertStyle");

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
                    alert.setContentText("Action: buys a Development Card");

                    //automatic resizing
                    alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
                    // alert styling
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(
                            getClass().getResource("/CSS/style.css").toExternalForm());
                    dialogPane.getStyleClass().add("alertStyle");

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
                    alert.setContentText("Action: takes Resources");

                    //automatic resizing
                    alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
                    // alert styling
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(
                            getClass().getResource("/CSS/style.css").toExternalForm());
                    dialogPane.getStyleClass().add("alertStyle");

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

                    //automatic resizing
                    alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
                    // alert styling
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(
                            getClass().getResource("/CSS/style.css").toExternalForm());
                    dialogPane.getStyleClass().add("alertStyle");

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
        // TODO: FAR FUNZIONARE
        if(!solo && this.online) {
            Platform.runLater(() -> {
                guiApp.showScene("/FXML/playerboard.fxml");
                this.playerboard.setActive();
                this.playerboard.showMenu();
            });
        }
        else if(firstRound) {
            firstRound = false;
            this.currentPlayer = players.players[0].getNickname();
            Platform.runLater(() -> {
                guiApp.showScene("/FXML/playerboard.fxml");
                this.playerboard.setActive();
                this.playerboard.showMenu();
            });
        }
        try {
            while (!eventHandler.containsKey(ViewEvent.ACTION)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (String) eventHandler.remove(ViewEvent.ACTION);
    }

    @Override
    public synchronized String selectLeaderAction() {
        this.showScene("/FXML/leaderaction.fxml");
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
        Platform.runLater(() -> GuiView.getGuiView().playerboard.positionCard(bought));
        try {
            while (!eventHandler.containsKey(ViewEvent.DEVCARD_POSITION)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
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
    public synchronized void showGatheredResources(ResourcePack gathered) { }

    @Override
    public synchronized ResourcePack selectWhite(int amount) {
        this.showScene("/FXML/loot.fxml");
        Platform.runLater(() -> this.lootScene.selectWhite(amount));
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
            playerboard.pendingSceneOn();
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
    public synchronized boolean playLeaderAction() {
        Platform.runLater(() -> {
            Alert playLeader = new Alert(Alert.AlertType.NONE);
            playLeader.setTitle("Leader Action");
            playLeader.setContentText("Do you want to play or discard a Leader?");

            ButtonType yes = new ButtonType("Yes");
            ButtonType no = new ButtonType("No");

            playLeader.getButtonTypes().setAll(yes,no);

            // automatic resizing
            playLeader.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
            // alert styling
            DialogPane dialogPane = playLeader.getDialogPane();
            dialogPane.getStylesheets().add(
                    getClass().getResource("/CSS/style.css").toExternalForm());
            dialogPane.getStyleClass().add("alertStyle");

            Optional<ButtonType> result = playLeader.showAndWait();
            if(result.isPresent()) {
                GuiView.getGuiView().event(ViewEvent.PLAY_LEADER,result.get() == yes);
            } else GuiView.getGuiView().event(ViewEvent.PLAY_LEADER,false);

            playerboard.notYourTurn();
        });
        try {
            while (!eventHandler.containsKey(ViewEvent.PLAY_LEADER)) wait();
        } catch (InterruptedException ignored) { /* Should not happen */ }
        return (Boolean) eventHandler.remove(ViewEvent.PLAY_LEADER);
    }

    @Override
    public void gameEnd() {
        showScene("/FXML/leaderboard.fxml");
        Platform.runLater(() -> leaderboard.endGame());
    }

    // ------- NEW ----------
    boolean leaderUpdate = false;

    public void enableLeaderUpdate() {
        this.leaderUpdate = true;
    }
    // ------------------------

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

            case "leaders":
                if(this.leaderUpdate) this.leaderStack.update(state);
                break;

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
