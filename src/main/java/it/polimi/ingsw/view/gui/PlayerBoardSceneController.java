package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.model.resources.ResourcePack;
import it.polimi.ingsw.model.resources.Warehouse;
import it.polimi.ingsw.view.lightmodel.DevelopmentCardView;
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
import java.util.ResourceBundle;

public class PlayerBoardSceneController implements Initializable, InvalidationListener {

    private DevelopmentCardView cards;
    private WarehouseView warehouse;
    private ResourcePack strongbox;
    private ResourcePack pending;

    @FXML
    private ImageView board, card1, card2, card3, faithmarker,
                      first, secondA, secondB, thirdA, thirdB, thirdC,
                      coin, stone, shield, servant;

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
        board.setImage(new Image("/PNG/board/playerboard.png"));
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

    public void pendingScene() {
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

    public void discardResources() {

    }

    @Override
    public void invalidated(Observable observable) {
        cards = GuiView.getGuiView().devCardStack;
        Image card1Image = cards.getImageForCard(cards.getCard(1));
        Image card2Image = cards.getImageForCard(cards.getCard(2));
        Image card3Image = cards.getImageForCard(cards.getCard(3));

        if(card1Image!=null) card1.setImage(cards.getImageForCard(cards.getCard(1)));
        if(card2Image!=null) card2.setImage(cards.getImageForCard(cards.getCard(2)));
        if(card3Image!=null) card3.setImage(cards.getImageForCard(cards.getCard(3)));

        warehouse = GuiView.getGuiView().warehouse;
        if(warehouse.stock.get(0).getAvailable()==1) {
            first.setImage(new Image(warehouse.stock.get(0).getResource().getImage()));
        }
        if(warehouse.stock.get(1).getAvailable()>=1) {
            secondA.setImage(new Image(warehouse.stock.get(1).getResource().getImage()));
            if(warehouse.stock.get(1).getAvailable()==2) {
                secondB.setImage(new Image(warehouse.stock.get(1).getResource().getImage()));
            }
        }
        if(warehouse.stock.get(2).getAvailable()>=1) {
            thirdA.setImage(new Image(warehouse.stock.get(2).getResource().getImage()));
            if(warehouse.stock.get(2).getAvailable()>=2) {
                thirdB.setImage(new Image(warehouse.stock.get(2).getResource().getImage()));
                if(warehouse.stock.get(3).getAvailable()==3) {
                    thirdC.setImage(new Image(warehouse.stock.get(2).getResource().getImage()));
                }
            }
        }

        strongbox = GuiView.getGuiView().strongbox;
        storedCoins.setText(""+strongbox.get(Resource.COIN));
        storedServants.setText(""+strongbox.get(Resource.SERVANT));
        storedShields.setText(""+strongbox.get(Resource.SHIELD));
        storedStones.setText(""+strongbox.get(Resource.STONE));

        pending = GuiView.getGuiView().warehouse.pendingResources;
        if(!pending.isEmpty()) {
            pendingCoins.setText(""+pending.get(Resource.COIN));
            pendingServants.setText(""+pending.get(Resource.SERVANT));
            pendingShields.setText(""+pending.get(Resource.SHIELD));
            pendingStones.setText(""+pending.get(Resource.STONE));
            this.pendingScene();
        }
    }
}
