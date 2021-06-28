package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.model.cards.Color;
import it.polimi.ingsw.model.singleplayer.SoloAction;
import it.polimi.ingsw.model.singleplayer.SoloCross;
import it.polimi.ingsw.model.singleplayer.SoloDiscard;
import it.polimi.ingsw.view.gui.GuiView;
import it.polimi.ingsw.view.lightmodel.GameView;
import it.polimi.ingsw.view.lightmodel.PlayerView;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class LorenzoController implements Initializable {

    @FXML
    ImageView marker, action, info;

    @FXML
    AnchorPane display;

    int previousMarkerPosition, markerPosition;

    private GameView game;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GuiView.getGuiView().lorenzo = this;
        game = GuiView.getGuiView().players;
        updateFaith();
        info.setVisible(false);
    }

    public void updateFaith() {
        this.game = GuiView.getGuiView().players;
        for(PlayerView player : game.players) {
            if(player.getNickname().equals("Lorenzo il Magnifico")) {
                previousMarkerPosition = 0;
                markerPosition = player.getFaithMarker();
            }
        }
        updatePosition();
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
            for(PlayerView player : game.players) {
                if(player.getNickname().equals(GuiView.getGuiView().nickname)) {
                    player.setPreviousMarker(position);
                }
            }
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

    public void showInfo() {
        info.setVisible(!info.isVisible());
    }

    public void proceed() {
        GuiView.getGuiView().showScene("/FXML/playerboard.fxml");
        Platform.runLater(() -> {
            GuiView.getGuiView().playerboard.setActive();
            GuiView.getGuiView().playerboard.showMenu();
        });
    }

    public void setAction(SoloAction soloAction) {
        if(soloAction instanceof SoloCross) {
            if(soloAction.toShuffle()) action.setImage(new Image("/PNG/punchboard/cerchio7.png"));
            else action.setImage(new Image("/PNG/punchboard/cerchio5.png"));
        }
        else if(soloAction instanceof SoloDiscard) {
            if(((SoloDiscard) soloAction).getToDiscard().containsKey(Color.BLUE)) action.setImage(new Image("/PNG/punchboard/cerchio1.png"));
            else if(((SoloDiscard) soloAction).getToDiscard().containsKey(Color.GREEN)) action.setImage(new Image("/PNG/punchboard/cerchio2.png"));
            else if(((SoloDiscard) soloAction).getToDiscard().containsKey(Color.PURPLE)) action.setImage(new Image("/PNG/punchboard/cerchio3.png"));
            else if(((SoloDiscard) soloAction).getToDiscard().containsKey(Color.YELLOW)) action.setImage(new Image("/PNG/punchboard/cerchio4.png"));
        }
    }
}
