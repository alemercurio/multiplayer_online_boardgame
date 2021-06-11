package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.model.cards.Color;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.view.ViewEvent;
import it.polimi.ingsw.view.lightmodel.DevelopmentCardView;
import it.polimi.ingsw.view.gui.GuiView;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class CardMarketController implements Initializable, InvalidationListener {
    @FXML
    private VBox chosenCard;


    @FXML
    private ImageView cardImg;

    @FXML
    private ScrollPane scroll;

    @FXML
    private AnchorPane marketPane;

    @FXML
    private GridPane grid;

    @FXML
    private Button cancelButton;

    Stage stage;

    private Map<Color, List<DevelopmentCard>> cardsMatrix;
    private List<DevelopmentCardView> cards = new ArrayList<>();
    private Image image;
    private CardListener listener;

    private DevelopmentCardView cardToBuy;

    public void cancel(ActionEvent event) {
        GuiView.getGuiView().event(ViewEvent.DEVCARD, "back");
        GuiView.getGuiView().showScene("/FXML/playerboard.fxml");
        Platform.runLater(() -> GuiView.getGuiView().playerboard.showMenu());
    }

    private List<DevelopmentCardView> getData() {
        List<DevelopmentCardView> cards = new ArrayList<>();
        DevelopmentCardView cardView;

        for(int i=0; i<3; i++) {
            for(Color color : Color.values()) {
                DevelopmentCard card = cardsMatrix.get(color).get(i);
                cardView = new DevelopmentCardView();
                cardView.setImgSrc(card.getPathForCard());
                cardView.setColor(color.getValue());
                cardView.setLevel(card.getLevel());
                cards.add(cardView);
            }
        }

        return cards;
    }

    private void setChosenCard(DevelopmentCardView cardView) {
        image = new Image(getClass().getResourceAsStream(cardView.getImgSrc()));
        cardImg.setImage(image);
        chosenCard.setStyle("-fx-background-color: #" + cardView.getColor() + ";\n" +
                "    -fx-background-radius: 30;");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GuiView.getGuiView().market.addListener(this);
        this.invalidated(GuiView.getGuiView().market);
        cards.addAll(getData());
        if (cards.size() > 0) {
            setChosenCard(cards.get(0));
            listener = new CardListener() {
                @Override
                public void onClickListener(DevelopmentCardView cardView) {
                    setChosenCard(cardView);
                    //save the chosen card
                    cardToBuy = cardView;
                }
            };
        }
        int column = 0;
        int row = 1;
        try {
            for (int i = 0; i < cards.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/FXML/card.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                CardController cardController = fxmlLoader.getController();
                cardController.setData(cards.get(i),listener);

                if (column == 4) {
                    column = 0;
                    row++;
                }

                grid.add(anchorPane, column++, row); //(child,column,row)
                //set grid width
                grid.setMinWidth(Region.USE_COMPUTED_SIZE);
                grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
                grid.setMaxWidth(Region.USE_PREF_SIZE);

                //set grid height
                grid.setMinHeight(Region.USE_COMPUTED_SIZE);
                grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
                grid.setMaxHeight(Region.USE_PREF_SIZE);

                GridPane.setMargin(anchorPane, new Insets(10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        cardImg.imageProperty().set(null);
    }

    public void buyCard() {
        GuiView.getGuiView().buyed = cardToBuy;
        GuiView.getGuiView().event(ViewEvent.DEVCARD, ""+Color.toColorFromValue(cardToBuy.getColor()).getAlias()+cardToBuy.getLevel());
    }

    @Override
    public void invalidated(Observable observable) {
        this.cardsMatrix = GuiView.getGuiView().market.decksMap;
        getData();
    }
}
