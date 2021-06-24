package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.view.ViewEvent;
import it.polimi.ingsw.view.gui.GuiView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LeaderActionController implements Initializable {

    @FXML
    private Rectangle leader1,leader2;

    @FXML
    private Label text;

    @FXML
    private Button play1,play2,discard1,discard2;

    @FXML
    private void play1() {
        GuiView.getGuiView().event(ViewEvent.LEADER_ACTION, "play 1");
        GuiView.getGuiView().showScene("/FXML/playerboard.fxml");
        Platform.runLater(() -> GuiView.getGuiView().playerboard.showMenu());
    }

    @FXML
    private void play2() {
        GuiView.getGuiView().event(ViewEvent.LEADER_ACTION, "play 2");
        GuiView.getGuiView().showScene("/FXML/playerboard.fxml");
        Platform.runLater(() -> GuiView.getGuiView().playerboard.showMenu());
    }

    @FXML
    private void discard1() {
        GuiView.getGuiView().event(ViewEvent.LEADER_ACTION, "discard 1");
        GuiView.getGuiView().showScene("/FXML/playerboard.fxml");
        Platform.runLater(() -> GuiView.getGuiView().playerboard.showMenu());
    }

    @FXML
    private void discard2() {
        GuiView.getGuiView().event(ViewEvent.LEADER_ACTION, "discard 2");
        GuiView.getGuiView().showScene("/FXML/playerboard.fxml");
        Platform.runLater(() -> GuiView.getGuiView().playerboard.showMenu());
    }

    @FXML
    private void back() {
        GuiView.getGuiView().event(ViewEvent.LEADER_ACTION, "back");
        GuiView.getGuiView().showScene("/FXML/playerboard.fxml");
        Platform.runLater(() -> GuiView.getGuiView().playerboard.showMenu());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GuiView.getGuiView().leaderScene = this;

        List<LeaderCard> inactiveLeaders = GuiView.getGuiView().leaderStack.getInactive();
        List<LeaderCard> activeLeaders = GuiView.getGuiView().leaderStack.getActive();

        LeaderCard leader;
        SepiaTone sepiaTone = new SepiaTone();
        sepiaTone.setLevel(0.8);

        if(inactiveLeaders.isEmpty() && activeLeaders.isEmpty()) {
            this.text.setText("You do not have any Leader left to play!");
            this.leader1.setVisible(false);
            this.leader2.setVisible(false);
        }
        else {
            if(!inactiveLeaders.isEmpty()) {
                leader = inactiveLeaders.remove(0);
                ImageView img = new ImageView(GuiView.getGuiView().leaderStack.getImageForCard(leader));
                img.setEffect(sepiaTone);
                Image image = img.snapshot(null,null);
                this.leader1.setFill(new ImagePattern(image));
                this.play1.setVisible(true);
                this.discard1.setVisible(true);
            } else {
                leader = activeLeaders.remove(0);
                Image image = GuiView.getGuiView().leaderStack.getImageForCard(leader);
                this.leader1.setFill(new ImagePattern(image));
            }

            if(!inactiveLeaders.isEmpty()) {
                leader = inactiveLeaders.remove(0);
                ImageView img = new ImageView(GuiView.getGuiView().leaderStack.getImageForCard(leader));
                img.setEffect(sepiaTone);
                Image image = img.snapshot(null,null);
                this.leader2.setFill(new ImagePattern(image));
                this.play2.setVisible(true);
                this.discard2.setVisible(true);
            } else if(!activeLeaders.isEmpty()) {
                leader = activeLeaders.remove(0);
                Image image = GuiView.getGuiView().leaderStack.getImageForCard(leader);
                this.leader2.setFill(new ImagePattern(image));
            } else {
                this.leader2.setVisible(false);
            }
        }
    }

    public void disableActions() {
        this.play1.setVisible(false);
        this.discard1.setVisible(false);
        this.play2.setVisible(false);
        this.discard2.setVisible(false);
    }
}
