package it.polimi.ingsw.view.gui.marketGui;

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
import java.util.ResourceBundle;

public class MarketController implements Initializable {
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

    private List<CardView> cards = new ArrayList<>();
    private Image image;
    private Listener listener;

    private CardView cardToBuy;

    public void cancel(ActionEvent event) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("cancel");
        alert.setHeaderText("You're about to cancel!");
        alert.setContentText("are you sure you want to cancel ?: ");

        if(alert.showAndWait().get() == ButtonType.OK){
            stage = (Stage) marketPane.getScene().getWindow();
            System.out.println("You successfully cancel!");
            stage.close();
        }

    }

    private List<CardView> getData() {
        List<CardView> cards = new ArrayList<>();
        CardView cardView;

        cardView = new CardView();
        cardView.setImgSrc("/PNG/cardfront/DevCardFrontG3_3.png");
        cardView.setColor("137035");
        cards.add(cardView);

        cardView = new CardView();
        cardView.setImgSrc("/PNG/cardfront/DevCardFrontB3_3.png");
        cardView.setColor("19719F");
        cards.add(cardView);

        cardView = new CardView();
        cardView.setImgSrc("/PNG/cardfront/DevCardFrontY3_3.png");
        cardView.setColor("A88E1F");
        cards.add(cardView);

        cardView = new CardView();
        cardView.setImgSrc("/PNG/cardfront/DevCardFrontP3_3.png");
        cardView.setColor("574776");
        cards.add(cardView);

        cardView = new CardView();
        cardView.setImgSrc("/PNG/cardfront/DevCardFrontG2_1.png");
        cardView.setColor("137035");
        cards.add(cardView);

        cardView = new CardView();
        cardView.setImgSrc("/PNG/cardfront/DevCardFrontB2_1.png");
        cardView.setColor("19719F");
        cards.add(cardView);

        cardView = new CardView();
        cardView.setImgSrc("/PNG/cardfront/DevCardFrontY2_1.png");
        cardView.setColor("A88E1F");
        cards.add(cardView);

        cardView = new CardView();
        cardView.setImgSrc("/PNG/cardfront/DevCardFrontP2_1.png");
        cardView.setColor("574776");
        cards.add(cardView);

        cardView = new CardView();
        cardView.setImgSrc("/PNG/cardfront/DevCardFrontG1_1.png");
        cardView.setColor("137035");
        cards.add(cardView);

        cardView = new CardView();
        cardView.setImgSrc("/PNG/cardfront/DevCardFrontB1_1.png");
        cardView.setColor("19719F");
        cards.add(cardView);

        cardView = new CardView();
        cardView.setImgSrc("/PNG/cardfront/DevCardFrontY1_1.png");
        cardView.setColor("A88E1F");
        cards.add(cardView);

        cardView = new CardView();
        cardView.setImgSrc("/PNG/cardfront/DevCardFrontP1_1.png");
        cardView.setColor("574776");
        cards.add(cardView);

        return cards;
    }

    private void setChosenCard(CardView cardView) {
        image = new Image(getClass().getResourceAsStream(cardView.getImgSrc()));
        cardImg.setImage(image);
        chosenCard.setStyle("-fx-background-color: #" + cardView.getColor() + ";\n" +
                "    -fx-background-radius: 30;");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cards.addAll(getData());
        if (cards.size() > 0) {
            setChosenCard(cards.get(0));
            listener = new Listener() {
                @Override
                public void onClickListener(CardView cardView) {

                    setChosenCard(cardView);
                    //save the chosen card
                    cardToBuy = cardView;
                    System.out.println(cardToBuy.getImgSrc());
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
    }

}
