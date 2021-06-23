package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.model.resources.ResourcePack;
import it.polimi.ingsw.view.ViewEvent;
import it.polimi.ingsw.view.gui.GuiView;
import it.polimi.ingsw.view.gui.util.ResourcePackView;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class LootSceneController implements Initializable {

    @FXML
    Label message;

    @FXML
    HBox white;

    private int amount;
    private ResourcePackView.ResourcePicker resourcePicker;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GuiView.getGuiView().lootScene = this;
    }

    public void selectWhite(int amount) {
        this.message.setText("Use your power to get " + amount + " more resources!");
        this.resourcePicker = new ResourcePackView.ResourcePicker(GuiView.getGuiView().playerBoard.whitePower,amount);
        this.white.getChildren().setAll(resourcePicker.get(70,20));
    }

    public void done() {
        GuiView.getGuiView().event(ViewEvent.CONVERT_WHITE,this.resourcePicker.getSelected());
        GuiView.getGuiView().showScene("/FXML/playerboard.fxml");
    }
}
