package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.view.gui.GuiApp;
import javafx.application.Platform;

public class CreditsController {

    public void back() {
        Platform.runLater(() -> GuiApp.window.setScene(GuiApp.mainMenu));
    }
}
