package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.model.cards.Color;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.view.ViewEvent;
import it.polimi.ingsw.view.gui.util.ResourcePackView;
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
import javafx.scene.layout.*;
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
    private AnchorPane marketPane;

    @FXML
    private GridPane grid;

    @FXML
    private Button cancelButton, buyButton;

    @FXML
    private HBox discount;

    Stage stage;

    private Map<Color, List<DevelopmentCard>> cardsMatrix;
    private List<DevelopmentCardView> cards = new ArrayList<>();
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
                if(card!=null) {
                    cardView.setImgSrc(card.getPathForCard());
                    cardView.setLevel(card.getLevel());
                }
                else {
                    cardView.setImgSrc("/PNG/cardback/LeaderCardBack.png");
                    cardView.setLevel(0);
                }
                cardView.setColor(color.getValue());
                cards.add(cardView);
            }
        }

        return cards;
    }

    private void setChosenCard(DevelopmentCardView cardView) {
        Image image = new Image(getClass().getResourceAsStream(cardView.getImgSrc()));
        cardImg.setImage(image);
        chosenCard.setStyle("-fx-background-color: #" + cardView.getColor() + ";\n" +
                "    -fx-background-radius: 30;");
        chosenCard.setVisible(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GuiView.getGuiView().cardMarket = this;
        GuiView.getGuiView().market.addListener(this);
        this.invalidated(GuiView.getGuiView().market);

        if(GuiView.getGuiView().playerBoard.hasDiscount()) {
            this.discount.setPrefHeight(30);
            this.discount.setSpacing(7);
            this.discount.getChildren().addAll(
                    new Label("Your discount: "),
                    ResourcePackView.get(GuiView.getGuiView().playerBoard.getDiscount(), 30,10)
            );
        }

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
        chosenCard.setVisible(false);
    }

    public void buyCard() {
        GuiView.getGuiView().bought = cardToBuy;
        GuiView.getGuiView().event(ViewEvent.DEVCARD, ""+Color.toColorFromValue(cardToBuy.getColor()).getAlias()+cardToBuy.getLevel());
    }

    public void disableActions() {
        buyButton.setVisible(false);
    }

    @Override
    public void invalidated(Observable observable) {
        this.cardsMatrix = GuiView.getGuiView().market.getDecksMap();
        getData();
    }
}
