package it.polimi.ingsw.view.gui;

import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import java.net.URL;
import java.util.ResourceBundle;

public class marblesController implements Initializable {

    @FXML
    private Circle G1;

    @FXML
    private Circle Y2;

    @FXML
    private Circle W3;

    @FXML
    private Circle R;

    @FXML
    private Circle W4;

    @FXML
    private Circle P2;

    @FXML
    private Circle P1;

    @FXML
    private Circle B1;

    @FXML
    private Circle B2;

    @FXML
    private Circle G2;

    @FXML
    private Circle Y1;

    @FXML
    private Circle W2;

    @FXML
    private Circle W1;

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
        finalTransition.play();


        // update values
        Circle tempPending = pendingMarble;
        pendingMarble = marketTray[2][0];
        marketTray[2][0] = marketTray[1][0];
        marketTray[1][0] = marketTray[0][0];
        marketTray[0][0] = tempPending;
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
        finalTransition.play();


        // update values
        Circle tempPending = pendingMarble;
        pendingMarble = marketTray[2][1];
        marketTray[2][1] = marketTray[1][1];
        marketTray[1][1] = marketTray[0][1];
        marketTray[0][1] = tempPending;
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
        finalTransition.play();


        // update values
        Circle tempPending = pendingMarble;
        pendingMarble = marketTray[2][2];
        marketTray[2][2] = marketTray[1][2];
        marketTray[1][2] = marketTray[0][2];
        marketTray[0][2] = tempPending;
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
        finalTransition.play();


        // update values
        Circle tempPending = pendingMarble;
        pendingMarble = marketTray[2][3];
        marketTray[2][3] = marketTray[1][3];
        marketTray[1][3] = marketTray[0][3];
        marketTray[0][3] = tempPending;
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
        last.play();

        //update data
        Circle tempPending = pendingMarble;
        pendingMarble = marketTray[0][0];
        marketTray[0][0] = marketTray[0][1];
        marketTray[0][1] = marketTray[0][2];
        marketTray[0][2] = marketTray[0][3];
        marketTray[0][3] = tempPending;
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
        last.play();

        //update data
        Circle tempPending = pendingMarble;
        pendingMarble = marketTray[1][0];
        marketTray[1][0] = marketTray[1][1];
        marketTray[1][1] = marketTray[1][2];
        marketTray[1][2] = marketTray[1][3];
        marketTray[1][3] = tempPending;
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
        last.play();

        //update data
        Circle tempPending = pendingMarble;
        pendingMarble = marketTray[2][0];
        marketTray[2][0] = marketTray[2][1];
        marketTray[2][1] = marketTray[2][2];
        marketTray[2][2] = marketTray[2][3];
        marketTray[2][3] = tempPending;
    }

    private Circle pendingMarble;

    @FXML
    private final Circle[][] marketTray = new Circle[3][4];

    public void initialMarketSetting() {
        marketTray[0][0] = G1;
        marketTray[0][1] = Y2;
        marketTray[0][2] = W3;
        marketTray[0][3] = R;
        marketTray[1][0] = B1;
        marketTray[1][1] = P1;
        marketTray[1][2] = P2;
        marketTray[1][3] = W4;
        marketTray[2][0] = W2;
        marketTray[2][1] = Y1;
        marketTray[2][2] = G2;
        marketTray[2][3] = B2;
        pendingMarble = W1;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {initialMarketSetting();}
}
