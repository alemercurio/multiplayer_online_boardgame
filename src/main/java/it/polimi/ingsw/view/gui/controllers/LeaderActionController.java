package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.view.ViewEvent;
import it.polimi.ingsw.view.gui.GuiView;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    }

    @FXML
    private void play2() {
        GuiView.getGuiView().event(ViewEvent.LEADER_ACTION, "play 2");
        GuiView.getGuiView().showScene("/FXML/playerboard.fxml");
    }

    @FXML
    private void discard1() {
        GuiView.getGuiView().event(ViewEvent.LEADER_ACTION, "discard 1");
        GuiView.getGuiView().showScene("/FXML/playerboard.fxml");
    }

    @FXML
    private void discard2() {
        GuiView.getGuiView().event(ViewEvent.LEADER_ACTION, "discard 2");
        GuiView.getGuiView().showScene("/FXML/playerboard.fxml");
    }

    @FXML
    private void back() {
        GuiView.getGuiView().event(ViewEvent.LEADER_ACTION, "back");
        GuiView.getGuiView().showScene("/FXML/playerboard.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GuiView.getGuiView().leaderScene = this;
        Map<LeaderCard,Boolean> leaders = GuiView.getGuiView().leaderStack.getLeader();

        List<Image> images = new ArrayList<>();
        int inactive = 0;

        for(Map.Entry<LeaderCard,Boolean> leader : leaders.entrySet()) {
            Image image = GuiView.getGuiView().leaderStack.getImageForCard(leader.getKey());
            if(leader.getValue()) {
                ImageView img = new ImageView(image);
                SepiaTone sepiaTone = new SepiaTone();
                sepiaTone.setLevel(0.8);
                img.setEffect(sepiaTone);
                image = img.snapshot(null,null);
                inactive++;
            }
            images.add(image);
        }

        if(inactive == 0) this.text.setText("You do not have any Leader left to play!");
        if(images.size() >= 1) {
            this.leader1.setFill(new ImagePattern(images.get(0)));
            this.play1.setVisible(true);
            this.discard1.setVisible(true);
        }
        else this.leader1.setVisible(false);
        if(images.size() >= 2) {
            this.leader2.setFill(new ImagePattern(images.get(1)));
            this.play2.setVisible(true);
            this.discard2.setVisible(true);
        }
        else this.leader2.setVisible(false);
    }
}
