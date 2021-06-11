package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.model.resources.ResourcePack;
import it.polimi.ingsw.view.ViewEvent;
import it.polimi.ingsw.view.gui.GuiView;
import it.polimi.ingsw.view.lightmodel.DevelopmentCardStackView;
import it.polimi.ingsw.view.lightmodel.FaithView;
import it.polimi.ingsw.view.lightmodel.GameView;
import it.polimi.ingsw.view.lightmodel.WarehouseView;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class PlayerBoardSceneController implements Initializable, InvalidationListener {

    boolean active = false;

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

    private int markerPosition = 0;

    @FXML
    private ImageView board, card1, card2, card3,
            first, secondA, secondB, thirdA, thirdB, thirdC,
            coin, stone, shield, servant;

    @FXML
    private ImageView marker;

    @FXML
    private VBox actionMenu;

    @FXML
    private MenuBar show;

    @FXML
    private Label pendingBar;

    @FXML
    private Rectangle pendingArea;

    @FXML
    private Button menuToggler, pendingDone;

    @FXML
    private TextField pendingCoins, pendingStones, pendingShields, pendingServants,
            storedCoins, storedStones, storedShields, storedServants;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GuiView.getGuiView().playerboard = this;
        GuiView.getGuiView().devCardStack.addListener(this);
        GuiView.getGuiView().warehouse.addListener(this);
        GuiView.getGuiView().players.addListener(this);
        board.setImage(new Image("/PNG/board/playerboard.png"));
        setBlank();
        updateWarehouse();
        pendingSceneOff();
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
    }

    public void setActive() {
        active = true;
        actionMenu.setVisible(true);

    }

    public void hideMenu() {
        actionMenu.setVisible(false);
        menuToggler.setVisible(true);
        show.setVisible(true);
    }

    public void showMenu() {
        menuToggler.setVisible(false);
        show.setVisible(false);
        actionMenu.setVisible(true);
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
        GuiView.getGuiView().showScene("/FXML/notyourturn.fxml");
    }

    public void showResourceMarket() {
        GuiView.getGuiView().showScene("/FXML/marbles.fxml");
    }

    public void showCardMarket() {
        GuiView.getGuiView().showScene("/FXML/market.fxml");
    }

    public void showProductions() {

    }

    public void showPlayers() {

    }

    public void takeResources() {
        GuiView.getGuiView().event(ViewEvent.ACTION, "takeResources");
        GuiView.getGuiView().showScene("/FXML/marbles.fxml");
    }

    public void buyDevCard() {
        GuiView.getGuiView().event(ViewEvent.ACTION, "buyDevCard");
        GuiView.getGuiView().showScene("/FXML/market.fxml");
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
        int id = game.getCurrentPlayerID();
        markerPosition = game.players[id].getFaithMarker();
        System.out.println(markerPosition);
    }

    @Override
    public void invalidated(Observable observable) {
        if (observable instanceof DevelopmentCardStackView) updateCards();
        if (observable instanceof WarehouseView) updateWarehouse();
        if (observable instanceof FaithView) updateFaith();
    }
}
