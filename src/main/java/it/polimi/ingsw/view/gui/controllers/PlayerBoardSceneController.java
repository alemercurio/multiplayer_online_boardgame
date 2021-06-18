package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.model.resources.ResourcePack;
import it.polimi.ingsw.model.vatican.Vatican;
import it.polimi.ingsw.view.ViewEvent;
import it.polimi.ingsw.view.gui.GuiView;
import it.polimi.ingsw.view.lightmodel.*;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class PlayerBoardSceneController implements Initializable, InvalidationListener {

    boolean active;

    private DevelopmentCardStackView cards;
    private WarehouseView warehouse;
    private ResourcePack strongbox;
    private ResourcePack pending;
    private GameView game;

    private boolean moveStarted = false;
    private int source;
    private Resource moving;
    private Map<Resource, Integer> numPending = new HashMap<>() {{
        for(Resource resource : Resource.values()) put(resource, 0);
    }};

    private int tempPosition = 0;
    private int markerPosition = 0;
    private int previousMarkerPosition = 0;

    @FXML
    private ImageView board, card1, card2, card3, buyed,
            first, secondA, secondB, thirdA, thirdB, thirdC,
            coin, stone, shield, servant;

    @FXML
    private ImageView marker, pope2, pope3, pope4;

    @FXML
    private VBox actionMenu;

    @FXML
    private MenuBar show;

    @FXML
    private Label pendingBar, notYourTurnBar;

    @FXML
    private Rectangle pendingArea;

    @FXML
    private Button menuToggler, pendingDone;

    @FXML
    private TextField pendingCoins, pendingStones, pendingShields, pendingServants,
            storedCoins, storedStones, storedShields, storedServants;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        game = GuiView.getGuiView().players;
        GuiView.getGuiView().playerboard = this;
        GuiView.getGuiView().devCardStack.addListener(this);
        GuiView.getGuiView().warehouse.addListener(this);
        GuiView.getGuiView().players.addListener(this);
        board.setImage(new Image("/PNG/board/playerboard.png"));
        setBlank();
        updateWarehouse();
        updateCards();
        previousMarkerPosition = -1;
        updateFaith();
        pendingSceneOff();
        for(int i = 0; i < 3; i++) {
            Vatican.ReportSection ps = GuiView.getGuiView().faithTrack.popeSpaces[i];
            if (ps.getState() == Vatican.State.GOT) {
                turnPope(i + 1);
            }
            else if(ps.getState() == Vatican.State.LOST) {
                discardPope(i+1);
            }
        }
        if(GuiView.getGuiView().nickname.equals(GuiView.getGuiView().currentPlayer)) {
            setActive();
        }
    }

    public void setBlank() {
        actionMenu.setVisible(false);
        menuToggler.setVisible(false);
        show.setVisible(false);
        pendingBar.setVisible(false);
        pendingArea.setVisible(false);
        pendingDone.setVisible(false);
        coin.setVisible(false);
        stone.setVisible(false);
        shield.setVisible(false);
        servant.setVisible(false);
        pendingCoins.setVisible(false);
        pendingStones.setVisible(false);
        pendingShields.setVisible(false);
        pendingServants.setVisible(false);
        moveStarted = false;
        buyed.setVisible(false);
        notYourTurnBar.setVisible(false);
    }

    public void setActive() {
        active = true;
        card1.setVisible(true);
        card2.setVisible(true);
        card3.setVisible(true);
    }

    public void notYourTurn() {
        active = false;
        setBlank();
        show.setVisible(true);
        notYourTurnBar.setVisible(true);
    }

    public void hideMenu() {
        actionMenu.setVisible(false);
        menuToggler.setVisible(true);
        show.setVisible(true);
    }

    public void showMenu() {
        if(active) {
            menuToggler.setVisible(false);
            show.setVisible(false);
            actionMenu.setVisible(true);
        }
        else {
            notYourTurn();
        }
    }

    public void pendingSceneOn() {
        if(!warehouse.pendingResources.isEmpty()) {
            actionMenu.setVisible(false);
            menuToggler.setVisible(false);
            show.setVisible(false);
            pendingBar.setVisible(true);
            pendingArea.setVisible(true);
            pendingDone.setVisible(true);
            coin.setVisible(true);
            stone.setVisible(true);
            shield.setVisible(true);
            servant.setVisible(true);
            pendingCoins.setVisible(true);
            pendingStones.setVisible(true);
            pendingShields.setVisible(true);
            pendingServants.setVisible(true);
        }
    }

    public void pendingSceneOff() {
        pendingBar.setVisible(false);
        pendingArea.setVisible(false);
        pendingDone.setVisible(false);
        coin.setVisible(false);
        stone.setVisible(false);
        shield.setVisible(false);
        servant.setVisible(false);
        pendingCoins.setVisible(false);
        pendingStones.setVisible(false);
        pendingShields.setVisible(false);
        pendingServants.setVisible(false);
    }

    public void moveCoin() {
        if(!moveStarted && pending.get(Resource.COIN)>0) {
            moveStarted=true;
            source=0;
            moving=Resource.COIN;
        }
    }

    public void moveShield() {
        if(!moveStarted && pending.get(Resource.SHIELD)>0) {
            moveStarted=true;
            source=0;
            moving=Resource.SHIELD;
        }
    }

    public void moveServant() {
        if(!moveStarted && pending.get(Resource.SERVANT)>0) {
            moveStarted=true;
            source=0;
            moving=Resource.SERVANT;
        }
    }

    public void moveStone() {
        if(!moveStarted && pending.get(Resource.STONE)>0) {
            moveStarted=true;
            source=0;
            moving=Resource.STONE;
        }
    }

    public void move(int shelf) {
        if(!moveStarted) {
            if(warehouse.stock.get(shelf-1).getAvailable()>0) {
                moveStarted = true;
                source = shelf;
            }
        }
        else {
            if(source==0) {
                warehouse.move(shelf, moving, warehouse.pendingResources.get(moving));
            }
            else warehouse.move(shelf, source, warehouse.stock.get(source-1).getAvailable());
            warehouseLocalUpdate();
            moveStarted = false;
        }
    }

    public void moveFirst() {
        move(1);
    }

    public void moveSecondA() {
        move(2);
    }

    public void moveSecondB() {
        move(2);
    }

    public void moveThirdA() {
        move(3);
    }

    public void moveThirdB() {
        move(3);
    }

    public void moveThirdC() {
        move(3);
    }


    public void done() {
        GuiView.getGuiView().event(ViewEvent.WAREHOUSE_CONFIG, warehouse.getConfig());
        pendingSceneOff();
        Platform.runLater(() -> GuiView.getGuiView().playerboard.notYourTurn());
    }

    public void showResourceMarket() {
        GuiView.getGuiView().showScene("/FXML/marbles.fxml");
        Platform.runLater(() -> GuiView.getGuiView().resourceMarket.disableActions());
    }

    public void showCardMarket() {
        GuiView.getGuiView().showScene("/FXML/market.fxml");
        Platform.runLater(() -> GuiView.getGuiView().cardMarket.disableActions());
    }

    public void showProductions() {

    }

    public void showPlayers() {
        GuiView.getGuiView().showScene("/FXML/leaderboard.fxml");
    }

    public void takeResources() {
        GuiView.getGuiView().event(ViewEvent.ACTION, "takeResources");
        GuiView.getGuiView().showScene("/FXML/marbles.fxml");
    }

    public void buyDevCard() {
        GuiView.getGuiView().event(ViewEvent.ACTION, "buyDevCard");
        GuiView.getGuiView().showScene("/FXML/market.fxml");
    }

    public void positionCard(DevelopmentCardView card) {
        buyed.setVisible(true);
        buyed.setImage(new Image(card.getImgSrc()));
    }

    public void put1() {
        GuiView.getGuiView().event(ViewEvent.DEVCARD_POSITION, "1");
        buyed.setVisible(false);
    }

    public void put2() {
        GuiView.getGuiView().event(ViewEvent.DEVCARD_POSITION, "2");
        buyed.setVisible(false);
    }

    public void put3() {
        GuiView.getGuiView().event(ViewEvent.DEVCARD_POSITION, "3");
        buyed.setVisible(false);
    }

    public void activateProduction() {

    }

    public void leaderAction() {

    }

    public void updateCards() {
        cards = GuiView.getGuiView().devCardStack;
        card1.setImage(cards.getImageForCard(cards.getCard(1)));
        card2.setImage(cards.getImageForCard(cards.getCard(2)));
        card3.setImage(cards.getImageForCard(cards.getCard(3)));
    }

    public void updateWarehouse() {
        warehouse = GuiView.getGuiView().warehouse;
        warehouseLocalUpdate();
        this.pendingSceneOn();

        strongbox = GuiView.getGuiView().strongbox;
        storedCoins.setText("" + strongbox.get(Resource.COIN));
        storedServants.setText("" + strongbox.get(Resource.SERVANT));
        storedShields.setText("" + strongbox.get(Resource.SHIELD));
        storedStones.setText("" + strongbox.get(Resource.STONE));
    }

    public void warehouseLocalUpdate() {
        if (warehouse.stock.get(0).getAvailable() == 1) {
            first.setImage(new Image(warehouse.stock.get(0).getResource().getImage()));
        }
        else first.imageProperty().set(null);

        if (warehouse.stock.get(1).getAvailable() >= 1) {
            secondA.setImage(new Image(warehouse.stock.get(1).getResource().getImage()));
            if (warehouse.stock.get(1).getAvailable() == 2) {
                secondB.setImage(new Image(warehouse.stock.get(1).getResource().getImage()));
            }
            else secondB.imageProperty().set(null);
        }
        else {
            secondA.imageProperty().set(null);
            secondB.imageProperty().set(null);
        }

        if (warehouse.stock.get(2).getAvailable() >= 1) {
            thirdA.setImage(new Image(warehouse.stock.get(2).getResource().getImage()));
            if (warehouse.stock.get(2).getAvailable() >= 2) {
                thirdB.setImage(new Image(warehouse.stock.get(2).getResource().getImage()));
                if (warehouse.stock.get(2).getAvailable() == 3) {
                    thirdC.setImage(new Image(warehouse.stock.get(2).getResource().getImage()));
                }
                else thirdC.imageProperty().set(null);
            }
            else {
                thirdB.imageProperty().set(null);
                thirdC.imageProperty().set(null);
            }
        }
        else {
            thirdA.imageProperty().set(null);
            thirdB.imageProperty().set(null);
            thirdC.imageProperty().set(null);
        }

        pending = warehouse.pendingResources;
        pendingCoins.setText("" + pending.get(Resource.COIN));
        pendingServants.setText("" + pending.get(Resource.SERVANT));
        pendingShields.setText("" + pending.get(Resource.SHIELD));
        pendingStones.setText("" + pending.get(Resource.STONE));
    }

    public void updateFaith() {
        this.game = GuiView.getGuiView().players;
        int id = 0;
        for(PlayerView player : game.players) {
            if(player.getNickname().equals(GuiView.getGuiView().nickname)) {
                id = player.getID();
            }
        }
        if(previousMarkerPosition == -1) previousMarkerPosition = 0;
        else previousMarkerPosition = game.players[id].getPreviousMarker();
        markerPosition = game.players[id].getFaithMarker();
        updatePosition();
    }

    @Override
    public void invalidated(Observable observable) {
        if (observable instanceof DevelopmentCardStackView) updateCards();
        if (observable instanceof WarehouseView) updateWarehouse();
    }

    void updatePosition() {
        int position = previousMarkerPosition;
        if(position != markerPosition) {
            if ((0 <= position && position <= 1) || (4 <= position && position <= 6) || (11 <= position && position <= 14) || (18 <= position && position <= 22) || (position == 8)) {
                previousMarkerPosition++;
                horizontalTranslation();
            }
            else if ((position == 7) || (position == 15) || (position == 23)) {
                previousMarkerPosition++;
                horizontalTranslationToPopeSpace();
            }
            else if ((2 <= position && position <= 3) || (16 <= position && position <= 17)) {
                previousMarkerPosition++;
                upVerticalTranslation();
            }
            else {
                previousMarkerPosition++;
                downVerticalTranslation();
            }
        }
        else {
            int id = 0;
            for(PlayerView player : game.players) {
                if(player.getNickname().equals(GuiView.getGuiView().nickname)) {
                    id = player.getID();
                }
            }
            game.players[id].setPreviousMarker(position);
        }
    }
    public void horizontalTranslation() {
        TranslateTransition transition = new TranslateTransition();
        transition.setNode(marker);
        transition.setByX(63);
        transition.setDuration(Duration.millis(10));
        transition.setOnFinished(e -> updatePosition());
        transition.play();
    }

    public void horizontalTranslationToPopeSpace() {
        TranslateTransition transition = new TranslateTransition();
        transition.setNode(marker);
        transition.setByX(61.75);
        transition.setDuration(Duration.millis(10));
        transition.setOnFinished(e -> updatePosition());
        transition.play();
    }

    public void downVerticalTranslation() {
        TranslateTransition transition = new TranslateTransition();
        transition.setNode(marker);
        transition.setByY(45);
        transition.setDuration(Duration.millis(10));
        transition.setOnFinished(e -> updatePosition());
        transition.play();
    }

    public void upVerticalTranslation() {
        TranslateTransition transition = new TranslateTransition();
        transition.setNode(marker);
        transition.setByY(-45);
        transition.setDuration(Duration.millis(10));
        transition.setOnFinished(e -> updatePosition());
        transition.play();
    }

    public void turnPope(int i) {
        if(i==1) {
            turnPope1();
        }
        else if(i==2) {
            turnPope2();
        }
        else if(i==3) {
            turnPope3();
        }
    }

    public void turnPope1() {
        pope2.setImage(new Image("/PNG/punchboard/pope_favor1_front.png"));
    }

    public void turnPope2() {
        pope3.setImage(new Image("/PNG/punchboard/pope_favor2_front.png"));
    }
    public void turnPope3() {
        pope4.setImage(new Image("/PNG/punchboard/pope_favor3_front.png"));
    }

    public void discardPope(int i) {
        if(i==1) {
            discardPope1();
        }
        else if(i==2) {
            discardPope2();
        }
        else if(i==3) {
            discardPope3();
        }
    }

    public void discardPope1() {
        pope2.imageProperty().set(null);
    }

    public void discardPope2() {
        pope3.imageProperty().set(null);
    }
    public void discardPope3() {
        pope4.imageProperty().set(null);
    }
}
