package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.view.gui.GuiApp;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class RulesController {

    @FXML
    ImageView page;

    int current=1;

    public void next() {
        if(current<9) {
            page.imageProperty().set(null);
            page.setImage(new Image("/PNG/rules/"+(current+1)+".png"));
            current++;
        }
    }

    public void prev() {
        if(current>1) {
            page.imageProperty().set(null);
            page.setImage(new Image("/PNG/rules/"+(current-1)+".png"));
            current--;
        }
    }

    public void back() {
        Platform.runLater(() -> GuiApp.window.setScene(GuiApp.mainMenu));
    }

}
