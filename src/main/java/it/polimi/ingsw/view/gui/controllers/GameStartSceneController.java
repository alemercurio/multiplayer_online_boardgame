package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.view.ViewEvent;
import it.polimi.ingsw.view.gui.GuiView;
import it.polimi.ingsw.view.lightmodel.LeaderView;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class GameStartSceneController implements Initializable, InvalidationListener {
    private LeaderView leaders;

    @FXML
    private ImageView board;

    @FXML
    private ImageView leader1;

    @FXML
    private ImageView leader2;

    @FXML
    private ImageView leader3;

    @FXML
    private ImageView leader4;

    List<Integer> chosen = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GuiView.getGuiView().leaderStack.addListener(this);
    }

    @Override
    public void invalidated(Observable observable) {
        leaders = GuiView.getGuiView().leaderStack;
        leader1.setImage(leaders.getImageForCard(leaders.getCard(0)));
        leader2.setImage(leaders.getImageForCard(leaders.getCard(1)));
        leader3.setImage(leaders.getImageForCard(leaders.getCard(2)));
        leader4.setImage(leaders.getImageForCard(leaders.getCard(3)));
    }

    public void choose1() {
        leader1.setVisible(false);
        choose(1);
    }

    public void choose2() {
        leader2.setVisible(false);
        choose(2);
    }

    public void choose3() {
        leader3.setVisible(false);
        choose(3);
    }

    public void choose4() {
        leader4.setVisible(false);
        choose(4);
    }

    public void choose(int index) {
        chosen.add(index);
        if(chosen.size()==2) {
            int[] array = chosen.stream().mapToInt(i->i).toArray();
            GuiView.getGuiView().leaderStack.removeListener(this);
            GuiView.getGuiView().event(ViewEvent.KEEP_LEADERS, array);
        }
    }
}
