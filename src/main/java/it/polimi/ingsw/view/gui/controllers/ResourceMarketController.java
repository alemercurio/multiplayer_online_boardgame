package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.view.ViewEvent;
import it.polimi.ingsw.view.gui.GuiView;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import java.net.URL;
import java.util.*;

public class ResourceMarketController implements Initializable, InvalidationListener {

    private Resource[][] tray;
    private Resource remaining;

    @FXML
    private Circle a;

    @FXML
    private Circle b;

    @FXML
    private Circle c;

    @FXML
    private Circle d;

    @FXML
    private Circle e;

    @FXML
    private Circle f;

    @FXML
    private Circle g;

    @FXML
    private Circle h;

    @FXML
    private Circle i;

    @FXML
    private Circle l;

    @FXML
    private Circle m;

    @FXML
    private Circle n;

    @FXML
    private Circle x;

    @FXML
    private Button buttonColumn1;

    @FXML
    private Button buttonColumn2;

    @FXML
    private Button buttonColumn3;

    @FXML
    private Button buttonColumn4;

    @FXML
    private Button buttonRow1;

    @FXML
    private Button buttonRow3;

    @FXML
    private Button buttonRow2;

    @FXML
    void moveColumn1(ActionEvent event) {
        //animation
        TranslateTransition transitionPending1  = new TranslateTransition();
        transitionPending1.setNode(pendingMarble);
        transitionPending1.setByY(220);
        transitionPending1.setDuration(Duration.millis(1000));

        TranslateTransition transitionPending2  = new TranslateTransition();
        transitionPending2.setNode(pendingMarble);
        transitionPending2.setByX(-220);
        transitionPending2.setDuration(Duration.millis(1000));

        SequentialTransition transitionPending = new SequentialTransition(transitionPending1, transitionPending2);

        TranslateTransition t00 = new TranslateTransition();
        t00.setNode(marketTray[0][0]);
        t00.setByY(-55);
        t00.setDuration(Duration.millis(1000));

        TranslateTransition t10 = new TranslateTransition();
        t10.setNode(marketTray[1][0]);
        t10.setByY(-55);
        t10.setDuration(Duration.millis(1000));

        TranslateTransition t20 = new TranslateTransition();
        t20.setNode(marketTray[2][0]);
        t20.setByY(-55);
        t20.setDuration(Duration.millis(1000));

        TranslateTransition tp = new TranslateTransition();
        tp.setNode(pendingMarble);
        tp.setByY(-55);
        tp.setDuration(Duration.millis(1000));

        ParallelTransition row1Transition = new ParallelTransition(t00,t10,t20,tp);
        SequentialTransition firstStep = new SequentialTransition(transitionPending, row1Transition);

        TranslateTransition secondStep = new TranslateTransition();
        secondStep.setNode(marketTray[2][0]);
        secondStep.setByX(220);
        secondStep.setDuration(Duration.millis(1000));

        SequentialTransition finalTransition = new SequentialTransition(firstStep, secondStep);
        finalTransition.setOnFinished(e -> Platform.runLater(() -> {
            GuiView.getGuiView().showScene("/FXML/playerboard.fxml");
            GuiView.getGuiView().event(ViewEvent.MARBLES, "column 1");
        }));
        finalTransition.play();
    }

    @FXML
    void moveColumn2(ActionEvent event) {
        //animation
        // pending: from initial position to column
        TranslateTransition transitionPending1  = new TranslateTransition();
        transitionPending1.setNode(pendingMarble);
        transitionPending1.setByY(220);
        transitionPending1.setDuration(Duration.millis(1000));

        TranslateTransition transitionPending2  = new TranslateTransition();
        transitionPending2.setNode(pendingMarble);
        transitionPending2.setByX(-165);
        transitionPending2.setDuration(Duration.millis(1000));

        SequentialTransition transitionPending = new SequentialTransition(transitionPending1, transitionPending2);
        //move the column
        TranslateTransition t01 = new TranslateTransition();
        t01.setNode(marketTray[0][1]);
        t01.setByY(-55);
        t01.setDuration(Duration.millis(1000));

        TranslateTransition t11 = new TranslateTransition();
        t11.setNode(marketTray[1][1]);
        t11.setByY(-55);
        t11.setDuration(Duration.millis(1000));

        TranslateTransition t21 = new TranslateTransition();
        t21.setNode(marketTray[2][1]);
        t21.setByY(-55);
        t21.setDuration(Duration.millis(1000));

        TranslateTransition tp = new TranslateTransition();
        tp.setNode(pendingMarble);
        tp.setByY(-55);
        tp.setDuration(Duration.millis(1000));

        ParallelTransition row1Transition = new ParallelTransition(t01,t11,t21,tp);
        SequentialTransition firstStep = new SequentialTransition(transitionPending, row1Transition);

        TranslateTransition secondStep = new TranslateTransition();
        secondStep.setNode(marketTray[2][1]);
        secondStep.setByX(165);
        secondStep.setDuration(Duration.millis(1000));

        SequentialTransition finalTransition = new SequentialTransition(firstStep, secondStep);
        finalTransition.setOnFinished(e -> Platform.runLater(() -> {
            GuiView.getGuiView().showScene("/FXML/playerboard.fxml");
            GuiView.getGuiView().event(ViewEvent.MARBLES, "column 2");
        }));
        finalTransition.play();
    }


    @FXML
    void moveColumn3(ActionEvent event) {
        //animation
        // pending: from initial position to column
        TranslateTransition transitionPending1  = new TranslateTransition();
        transitionPending1.setNode(pendingMarble);
        transitionPending1.setByY(220);
        transitionPending1.setDuration(Duration.millis(1000));

        TranslateTransition transitionPending2  = new TranslateTransition();
        transitionPending2.setNode(pendingMarble);
        transitionPending2.setByX(-110);
        transitionPending2.setDuration(Duration.millis(1000));

        SequentialTransition transitionPending = new SequentialTransition(transitionPending1, transitionPending2);
        //move the column
        TranslateTransition t02 = new TranslateTransition();
        t02.setNode(marketTray[0][2]);
        t02.setByY(-55);
        t02.setDuration(Duration.millis(1000));

        TranslateTransition t12 = new TranslateTransition();
        t12.setNode(marketTray[1][2]);
        t12.setByY(-55);
        t12.setDuration(Duration.millis(1000));

        TranslateTransition t22 = new TranslateTransition();
        t22.setNode(marketTray[2][2]);
        t22.setByY(-55);
        t22.setDuration(Duration.millis(1000));

        TranslateTransition tp = new TranslateTransition();
        tp.setNode(pendingMarble);
        tp.setByY(-55);
        tp.setDuration(Duration.millis(1000));

        ParallelTransition row1Transition = new ParallelTransition(t02,t12,t22,tp);
        SequentialTransition firstStep = new SequentialTransition(transitionPending, row1Transition);

        TranslateTransition secondStep = new TranslateTransition();
        secondStep.setNode(marketTray[2][2]);
        secondStep.setByX(110);
        secondStep.setDuration(Duration.millis(1000));

        SequentialTransition finalTransition = new SequentialTransition(firstStep, secondStep);
        finalTransition.setOnFinished(e -> Platform.runLater(() -> {
            GuiView.getGuiView().showScene("/FXML/playerboard.fxml");
            GuiView.getGuiView().event(ViewEvent.MARBLES, "column 3");
        }));
        finalTransition.play();
    }

    @FXML
    void moveColumn4(ActionEvent event) {
        //animation
        // pending: from initial position to column
        TranslateTransition transitionPending1  = new TranslateTransition();
        transitionPending1.setNode(pendingMarble);
        transitionPending1.setByY(220);
        transitionPending1.setDuration(Duration.millis(1000));

        TranslateTransition transitionPending2  = new TranslateTransition();
        transitionPending2.setNode(pendingMarble);
        transitionPending2.setByX(-55);
        transitionPending2.setDuration(Duration.millis(1000));

        SequentialTransition transitionPending = new SequentialTransition(transitionPending1, transitionPending2);
        //move the column
        TranslateTransition t03 = new TranslateTransition();
        t03.setNode(marketTray[0][3]);
        t03.setByY(-55);
        t03.setDuration(Duration.millis(1000));

        TranslateTransition t13 = new TranslateTransition();
        t13.setNode(marketTray[1][3]);
        t13.setByY(-55);
        t13.setDuration(Duration.millis(1000));

        TranslateTransition t23 = new TranslateTransition();
        t23.setNode(marketTray[2][3]);
        t23.setByY(-55);
        t23.setDuration(Duration.millis(1000));

        TranslateTransition tp = new TranslateTransition();
        tp.setNode(pendingMarble);
        tp.setByY(-55);
        tp.setDuration(Duration.millis(1000));

        ParallelTransition row1Transition = new ParallelTransition(t03,t13,t23,tp);
        SequentialTransition firstStep = new SequentialTransition(transitionPending, row1Transition);

        TranslateTransition secondStep = new TranslateTransition();
        secondStep.setNode(marketTray[2][3]);
        secondStep.setByX(55);
        secondStep.setDuration(Duration.millis(1000));

        SequentialTransition finalTransition = new SequentialTransition(firstStep, secondStep);
        finalTransition.setOnFinished(e -> Platform.runLater(() -> {
            GuiView.getGuiView().showScene("/FXML/playerboard.fxml");
            GuiView.getGuiView().event(ViewEvent.MARBLES, "column 4");
        }));
        finalTransition.play();
    }

    @FXML
    void moveRow1(ActionEvent event) {
        //animation
        TranslateTransition transitionPending1  = new TranslateTransition();
        transitionPending1.setNode(pendingMarble);
        transitionPending1.setByY(166);
        transitionPending1.setDuration(Duration.millis(1000));

        TranslateTransition t00 = new TranslateTransition();
        t00.setNode(marketTray[0][0]);
        t00.setByX(-55);
        t00.setDuration(Duration.millis(1000));

        TranslateTransition t01 = new TranslateTransition();
        t01.setNode(marketTray[0][1]);
        t01.setByX(-55);
        t01.setDuration(Duration.millis(1000));

        TranslateTransition t02 = new TranslateTransition();
        t02.setNode(marketTray[0][2]);
        t02.setByX(-55);
        t02.setDuration(Duration.millis(1000));

        TranslateTransition t03 = new TranslateTransition();
        t03.setNode(marketTray[0][3]);
        t03.setByX(-55);
        t03.setDuration(Duration.millis(1000));

        TranslateTransition tp = new TranslateTransition();
        tp.setNode(pendingMarble);
        tp.setByX(-55);
        tp.setDuration(Duration.millis(1000));

        ParallelTransition t = new ParallelTransition(t00,t01,t02,t03,tp);
        SequentialTransition firstStep = new SequentialTransition(transitionPending1,t);

        TranslateTransition lastTransition1 = new TranslateTransition();
        lastTransition1.setNode(marketTray[0][0]);
        lastTransition1.setByY(-166);
        lastTransition1.setDuration(Duration.millis(1000));

        TranslateTransition lastTransition2 = new TranslateTransition();
        lastTransition2.setNode(marketTray[0][0]);
        lastTransition2.setByX(275);
        lastTransition2.setDuration(Duration.millis(1000));

        SequentialTransition secondStep = new SequentialTransition(lastTransition1,lastTransition2);
        SequentialTransition last = new SequentialTransition(firstStep, secondStep);
        last.setOnFinished(e -> Platform.runLater(() -> {
            GuiView.getGuiView().showScene("/FXML/playerboard.fxml");
            GuiView.getGuiView().event(ViewEvent.MARBLES, "row 3");
        }));
        last.play();
    }

    @FXML
    void moveRow2(ActionEvent event) {

        //animation
        TranslateTransition transitionPending1  = new TranslateTransition();
        transitionPending1.setNode(pendingMarble);
        transitionPending1.setByY(111);
        transitionPending1.setDuration(Duration.millis(1000));

        TranslateTransition t10 = new TranslateTransition();
        t10.setNode(marketTray[1][0]);
        t10.setByX(-55);
        t10.setDuration(Duration.millis(1000));

        TranslateTransition t11 = new TranslateTransition();
        t11.setNode(marketTray[1][1]);
        t11.setByX(-55);
        t11.setDuration(Duration.millis(1000));

        TranslateTransition t12 = new TranslateTransition();
        t12.setNode(marketTray[1][2]);
        t12.setByX(-55);
        t12.setDuration(Duration.millis(1000));

        TranslateTransition t13 = new TranslateTransition();
        t13.setNode(marketTray[1][3]);
        t13.setByX(-55);
        t13.setDuration(Duration.millis(1000));

        TranslateTransition tp = new TranslateTransition();
        tp.setNode(pendingMarble);
        tp.setByX(-55);
        tp.setDuration(Duration.millis(1000));

        ParallelTransition t = new ParallelTransition(t10,t11,t12,t13,tp);
        SequentialTransition firstStep = new SequentialTransition(transitionPending1,t);

        TranslateTransition lastTransition1 = new TranslateTransition();
        lastTransition1.setNode(marketTray[1][0]);
        lastTransition1.setByY(-111);
        lastTransition1.setDuration(Duration.millis(1000));

        TranslateTransition lastTransition2 = new TranslateTransition();
        lastTransition2.setNode(marketTray[1][0]);
        lastTransition2.setByX(275);
        lastTransition2.setDuration(Duration.millis(1000));

        SequentialTransition secondStep = new SequentialTransition(lastTransition1,lastTransition2);
        SequentialTransition last = new SequentialTransition(firstStep, secondStep);
        last.setOnFinished(e -> Platform.runLater(() -> {
            GuiView.getGuiView().showScene("/FXML/playerboard.fxml");
            GuiView.getGuiView().event(ViewEvent.MARBLES, "row 2");
        }));
        last.play();
    }

    @FXML
    void moveRow3(ActionEvent event) {
        //animation
        TranslateTransition transitionPending1  = new TranslateTransition();
        transitionPending1.setNode(pendingMarble);
        transitionPending1.setByY(56);
        transitionPending1.setDuration(Duration.millis(1000));

        TranslateTransition t20 = new TranslateTransition();
        t20.setNode(marketTray[2][0]);
        t20.setByX(-55);
        t20.setDuration(Duration.millis(1000));

        TranslateTransition t21 = new TranslateTransition();
        t21.setNode(marketTray[2][1]);
        t21.setByX(-55);
        t21.setDuration(Duration.millis(1000));

        TranslateTransition t22 = new TranslateTransition();
        t22.setNode(marketTray[2][2]);
        t22.setByX(-55);
        t22.setDuration(Duration.millis(1000));

        TranslateTransition t23 = new TranslateTransition();
        t23.setNode(marketTray[2][3]);
        t23.setByX(-55);
        t23.setDuration(Duration.millis(1000));

        TranslateTransition tp = new TranslateTransition();
        tp.setNode(pendingMarble);
        tp.setByX(-55);
        tp.setDuration(Duration.millis(1000));

        ParallelTransition t = new ParallelTransition(t20,t21,t22,t23,tp);
        SequentialTransition firstStep = new SequentialTransition(transitionPending1,t);

        TranslateTransition lastTransition1 = new TranslateTransition();
        lastTransition1.setNode(marketTray[2][0]);
        lastTransition1.setByY(-56);
        lastTransition1.setDuration(Duration.millis(1000));

        TranslateTransition lastTransition2 = new TranslateTransition();
        lastTransition2.setNode(marketTray[2][0]);
        lastTransition2.setByX(275);
        lastTransition2.setDuration(Duration.millis(1000));

        SequentialTransition secondStep = new SequentialTransition(lastTransition1,lastTransition2);
        SequentialTransition last = new SequentialTransition(firstStep, secondStep);
        last.setOnFinished(e -> Platform.runLater(() -> {
            GuiView.getGuiView().showScene("/FXML/playerboard.fxml");
            GuiView.getGuiView().event(ViewEvent.MARBLES, "row 1");
        }));
        last.play();
    }

    private Circle pendingMarble;

    @FXML
    private final Circle[][] marketTray = new Circle[3][4];


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        marketTray[0][0] = a;
        marketTray[0][1] = b;
        marketTray[0][2] = c;
        marketTray[0][3] = d;
        marketTray[1][0] = e;
        marketTray[1][1] = f;
        marketTray[1][2] = g;
        marketTray[1][3] = h;
        marketTray[2][0] = i;
        marketTray[2][1] = l;
        marketTray[2][2] = m;
        marketTray[2][3] = n;
        pendingMarble = x;

        GuiView.getGuiView().market.addListener(this);
        this.invalidated(GuiView.getGuiView().market);
    }

    public void cancel(ActionEvent event) {
        GuiView.getGuiView().event(ViewEvent.MARBLES, "back");
        GuiView.getGuiView().showScene("/FXML/playerboard.fxml");
        Platform.runLater(() -> GuiView.getGuiView().playerboard.showMenu());
    }

    @Override
    public void invalidated(Observable observable) {
        tray = GuiView.getGuiView().market.marketTray;
        remaining = GuiView.getGuiView().market.remaining;

        for (int i = 0; i < 3; i++) {
            int k=i;
            if(i==0) k=2;
            if(i==2) k=0;

            for (int j = 0; j < 4; j++) {
                if (tray[i][j].compareTo(Resource.COIN) == 0) {
                    marketTray[k][j].setFill(Color.YELLOW);
                }
                if (tray[i][j].compareTo(Resource.SHIELD) == 0) {
                    marketTray[k][j].setFill(Color.BLUE);
                }
                if (tray[i][j].compareTo(Resource.STONE) == 0) {
                    marketTray[k][j].setFill(Color.GRAY);
                }
                if (tray[i][j].compareTo(Resource.SERVANT) == 0) {
                    marketTray[k][j].setFill(Color.PURPLE);
                }
                if (tray[i][j].compareTo(Resource.VOID) == 0) {
                    marketTray[k][j].setFill(Color.WHITESMOKE);
                }
                if (tray[i][j].compareTo(Resource.FAITHPOINT) == 0) {
                    marketTray[k][j].setFill(Color.RED);
                }
            }
        }
        if (remaining.compareTo(Resource.COIN) == 0) {
            pendingMarble.setFill(Color.YELLOW);
        }
        if (remaining.compareTo(Resource.SHIELD) == 0) {
            pendingMarble.setFill(Color.BLUE);
        }
        if (remaining.compareTo(Resource.STONE) == 0) {
            pendingMarble.setFill(Color.GRAY);
        }
        if (remaining.compareTo(Resource.SERVANT) == 0) {
            pendingMarble.setFill(Color.PURPLE);
        }
        if (remaining.compareTo(Resource.VOID) == 0) {
            pendingMarble.setFill(Color.WHITESMOKE);
        }
        if (remaining.compareTo(Resource.FAITHPOINT) == 0) {
            pendingMarble.setFill(Color.RED);
        }
    }
}
