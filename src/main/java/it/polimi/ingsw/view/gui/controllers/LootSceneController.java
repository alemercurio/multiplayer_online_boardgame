package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.model.resources.ResourcePack;
import it.polimi.ingsw.view.gui.GuiView;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class LootSceneController implements Initializable {

    @FXML
    TextField numCoinText;

    @FXML
    TextField numShieldText;

    @FXML
    TextField numServantText;

    @FXML
    TextField numStoneText;

    @FXML
    TextField numVoidText;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GuiView.getGuiView().lootScene = this;
    }

    public void setPack(ResourcePack pack) {
        Platform.runLater(() -> {
            numCoinText.setText(""+pack.get(Resource.COIN));
            numShieldText.setText(""+pack.get(Resource.SHIELD));
            numServantText.setText(""+pack.get(Resource.SERVANT));
            numStoneText.setText(""+pack.get(Resource.STONE));
            numVoidText.setText(""+pack.get(Resource.VOID));
            new AnimationTimer() {

                @Override
                public void handle(long l) {

                }
            }.start();
        });
    }

    public void askWhite(int amount) {

    }
}
