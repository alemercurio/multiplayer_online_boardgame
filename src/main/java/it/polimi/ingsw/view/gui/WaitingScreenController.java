package it.polimi.ingsw.view.gui;

import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.util.Duration;


import java.net.URL;
import java.util.ResourceBundle;

public class WaitingScreenController implements Initializable {

    @FXML
    private ImageView board;

    @FXML
    private Circle wheel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        RotateTransition rt = new RotateTransition();
        rt.setNode(wheel);
        rt.setDuration(Duration.millis(2000));
        rt.setByAngle(360);
        rt.setCycleCount(RotateTransition.INDEFINITE);
        rt.play();
    }
}
