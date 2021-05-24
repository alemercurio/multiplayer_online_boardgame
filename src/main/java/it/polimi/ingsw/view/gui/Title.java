package it.polimi.ingsw.view.gui;

import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Title extends Pane {
    private Text text;

    public Title(String title) {
        StringBuilder spread = new StringBuilder();
        for(char c : title.toCharArray()) {
            spread.append(c).append(" ");
        }
        text = new Text(spread.toString());
        //Font font = Font.loadFont(getClass().getResourceAsStream("/TTF/majanan.ttf"), 40);
        text.setFont(Font.font("Copperplate Gothic Bold", 40));
        text.setFill(Color.WHITE);
        text.setEffect(new DropShadow(30, Color.GOLD));

        getChildren().addAll(text);
    }

    public double getTitleWidth() {
        return text.getLayoutBounds().getWidth();
    }

    public double getTitleHeight() {
        return text.getLayoutBounds().getHeight();
    }
}
