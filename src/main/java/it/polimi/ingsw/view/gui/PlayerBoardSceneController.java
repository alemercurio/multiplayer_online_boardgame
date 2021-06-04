package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.view.lightmodel.DevelopmentCardView;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class PlayerBoardSceneController implements Initializable {

    private DevelopmentCardView cards;

    @FXML
    private ImageView board;

    @FXML
    private ImageView card1;

    @FXML
    private ImageView card2;

    @FXML
    private ImageView card3;

    @FXML
    private ImageView faithmarker;

    @FXML
    private VBox actionMenu;

    @FXML
    private Button menuToggler;

    @FXML
    private MenuBar show;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        board.setImage(new Image("/PNG/board/playerboard.png"));
        menuToggler.setVisible(false);
        show.setVisible(false);
    }

    public void update() {
        cards = GuiView.getGuiView().devCardStack;
        card1.setImage(cards.getImageForCard(cards.getCard(1)));
        card2.setImage(cards.getImageForCard(cards.getCard(2)));
        card3.setImage(cards.getImageForCard(cards.getCard(3)));
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
}
