package it.polimi.ingsw.view.gui;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class CardController {
    @FXML
    private ImageView img;

    @FXML
    private void click(MouseEvent mouseEvent) {
        listener.onClickListener(cardView);
    }

    private CardView cardView;
    private Listener listener;

    public void setData(CardView cardView, Listener listener) {
        this.cardView = cardView;
        this.listener = listener;
        Image image = new Image(getClass().getResourceAsStream(cardView.getImgSrc()));
        img.setImage(image);
    }
}
