package it.polimi.ingsw.view.gui;

import javafx.beans.binding.Bindings;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class StartMenuItem extends Pane {
    private Text text;
    private Polygon back;

    private Effect shadow = new DropShadow(5, Color.GOLD);
    private Effect blur = new BoxBlur(1, 1, 1);

    public StartMenuItem(String item) {
        back = new Polygon(
                0, 0,
                200, 0,
                215, 15,
                200, 30,
                0, 30
        );
        back.setStroke(Color.color(1, 1, 1, 0.75));
        back.setEffect(new GaussianBlur());

        back.fillProperty().bind(
                Bindings.when(pressedProperty())
                .then(Color.color(0, 0, 0, 0.75))
                .otherwise(Color.color(0, 0, 0, 0.25))
        );

        text = new Text(item);
        text.setTranslateX(5);
        text.setTranslateY(20);
        text.setFont(Font.font("Copperplate Gothic Bold", 14));
        text.setFill(Color.WHITE);

        text.effectProperty().bind(
                Bindings.when(hoverProperty())
                .then(shadow)
                .otherwise(blur)
        );

        getChildren().addAll(back, text);
    }

    public void setOnAction(Runnable action) {
        setOnMouseClicked(e -> action.run());
        setOnMouseEntered(e -> setTranslateX(10));
        setOnMouseExited(e -> setTranslateX(0));
    }
}
