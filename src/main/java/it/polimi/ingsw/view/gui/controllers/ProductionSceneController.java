package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.model.resources.NonConsumablePackException;
import it.polimi.ingsw.model.resources.Production;
import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.model.resources.ResourcePack;

import it.polimi.ingsw.view.ViewEvent;
import it.polimi.ingsw.view.gui.GuiView;
import it.polimi.ingsw.view.lightmodel.FactoryView;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.*;

public class ProductionSceneController implements Initializable, InvalidationListener {

    boolean events=true;

    public static class ResourcePackView {
        public static HBox getView(ResourcePack rp) {
            HBox pack = new HBox();
            pack.setAlignment(Pos.CENTER);
            pack.setSpacing(5);
            for(Resource res : Resource.values()) {
                if(rp.get(res) != 0) {
                    HBox hBox = new HBox();
                    hBox.setAlignment(Pos.CENTER);
                    hBox.setSpacing(5);

                    ImageView resImage = new ImageView(res.getImage());
                    resImage.setFitHeight(50);
                    resImage.setFitWidth(50);

                    Label amount = new Label(String.valueOf(rp.get(res)));
                    amount.setStyle("-fx-font-size: 35px;");

                    hBox.getChildren().addAll(
                            amount,
                            resImage);
                    pack.getChildren().add(hBox);
                }
            }
            return pack;
        }
    }

    public static class ProductionView {
        public static HBox getView(Production p) {
            HBox production = new HBox();
            production.setAlignment(Pos.CENTER);
            production.setSpacing(7);

            production.getChildren().add(ResourcePackView.getView(p.getRequired()));

            Label arrow = new Label(" -> ");
            arrow.setStyle("-fx-font-size: 35px;");

            production.getChildren().add(arrow);
            production.getChildren().add(ResourcePackView.getView(p.produce()));

            return production;
        }
    }

    public static class ResourcePackSelect {

        private final int amount;
        private final ResourcePack selected;

        public ResourcePackSelect(int amount) {
            this.amount = amount;
            this.selected = new ResourcePack();
        }

        public List<Node> get() {
            List<Node> nodes = new LinkedList<>();
            for(Resource res : Resource.values()) {
                if(!res.isSpecial()) {

                    Button button = new Button();
                    HBox hBox = new HBox();
                    hBox.setAlignment(Pos.CENTER);
                    hBox.setSpacing(2);

                    ImageView resImage = new ImageView(res.getImage());
                    resImage.setFitHeight(75);
                    resImage.setFitWidth(75);

                    Label label = new Label("x 0");
                    label.setFont(new Font(50));

                    hBox.getChildren().addAll(
                            resImage,
                            label);

                    button.setGraphic(hBox);
                    button.setStyle("-fx-background-color: transparent;");

                    button.setOnMouseClicked(event ->
                    {
                        if(event.getButton() == MouseButton.PRIMARY) {
                            if(this.selected.size() < this.amount) {
                                selected.add(res,1);
                                label.setText("x " + selected.get(res));
                            }
                        } else {
                            try { selected.consume(res,1);
                            } catch (NonConsumablePackException ignored) { }

                            label.setText("x " + selected.get(res));
                            label.setStyle("-fx-text-fill: black;");
                        }
                    });
                    nodes.add(button);
                }
            }
            return nodes;
        }

        public ResourcePack getSelected() {
            return this.selected.getCopy();
        }
    }

    public static class ResourcePackPicker {
        private final int amount;
        private final ResourcePack source;
        private final ResourcePack selected;

        public ResourcePackPicker(ResourcePack source,int amount) {
            this.amount = amount;
            this.source = source;
            this.selected = new ResourcePack();
        }

        public List<Node> get() {
            List<Node> nodes = new LinkedList<>();

            for(Resource res : Resource.values()) {

                if(this.source.get(res) != 0) {

                    Button button = new Button();
                    HBox hBox = new HBox();
                    hBox.setAlignment(Pos.CENTER);
                    hBox.setSpacing(2);

                    ImageView resImage = new ImageView(res.getImage());
                    resImage.setFitHeight(75);
                    resImage.setFitWidth(75);

                    Label label = new Label("x 0");
                    label.setFont(new Font(50));

                    hBox.getChildren().addAll(
                            resImage,
                            label);

                    button.setGraphic(hBox);
                    button.setStyle("-fx-background-color: transparent;");

                    button.setOnMouseClicked(event ->
                    {
                        if(event.getButton() == MouseButton.PRIMARY) {
                            if(selected.get(res) < source.get(res) && this.selected.size() < this.amount) {
                                selected.add(res,1);
                                label.setText("x " + selected.get(res));

                                if(selected.get(res) == source.get(res))
                                    label.setStyle("-fx-text-fill: gray;");
                            }
                        } else {

                            try { selected.consume(res,1);
                            } catch (NonConsumablePackException ignored) { }

                            label.setText("x " + selected.get(res));
                            label.setStyle("-fx-text-fill: black;");
                        }
                    });

                    nodes.add(button);
                }
            }
            return nodes;
        }

        public ResourcePack getSelected() {
            return this.selected.getCopy();
        }
    }


    @FXML
    private ListView<Production> productions;

    @FXML
    private HBox resButton;

    @FXML
    private Pane reqResources;

    @FXML
    private Button activate,done;

    @FXML
    private Label first_row,second_row;

    private final List<Production> fact = new LinkedList<>();
    private final ObservableList<Production> factory = FXCollections.observableList(this.fact);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GuiView.getGuiView().productionScene = this;
        GuiView.getGuiView().factory.addListener(this);

        this.productions.setItems(this.factory);
        this.productions.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.productions.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Production production, boolean empty) {
                super.updateItem(production, empty);
                if(empty) setGraphic(null);
                else setGraphic(ProductionView.getView(production));
            }
        });
    }

    @FXML
    private void activate() {
        if(!this.productions.getSelectionModel().getSelectedIndices().isEmpty()) {
            GuiView.getGuiView().factory.setActive(this.productions.getSelectionModel().getSelectedIndices());
            GuiView.getGuiView().event(ViewEvent.PRODUCTION, "active");
        }
        else GuiView.getGuiView().tell("You must select at least one production to activate!");
    }

    public void selectFreeRequirement(int amount) {

        ResourcePack resources = GuiView.getGuiView().warehouse.getResources();
        resources.add(GuiView.getGuiView().strongbox);

        ResourcePackPicker picker = new ResourcePackPicker(resources,amount);

        this.resButton.getChildren().setAll(picker.get());
        this.first_row.setText("To activate these production");

        if(amount == 1)
            this.second_row.setText("you have to choose 1 resource");
        else
            this.second_row.setText("you have to choose " + amount + " resources");

        this.done.setOnMouseClicked(event -> {
            GuiView.getGuiView().event(ViewEvent.FREE_REQUIREMENT,picker.getSelected());
            reqResources.setVisible(false);
        });

        this.reqResources.setVisible(true);
    }

    public void selectResources(int amount) {

        ResourcePackSelect select = new ResourcePackSelect(amount);

        this.resButton.getChildren().setAll(select.get());
        this.first_row.setText("");

        if(amount == 1)
            this.second_row.setText("You can choose 1 resource!");
        else
            this.second_row.setText("You can choose " + amount + " resources!");

        this.done.setOnMouseClicked(event -> {
            GuiView.getGuiView().event(ViewEvent.CHOOSE_PRODUCT,select.getSelected());
            reqResources.setVisible(false);
        });

        this.reqResources.setVisible(true);
    }

    @FXML
    private void back() {
        if(events) GuiView.getGuiView().event(ViewEvent.PRODUCTION, "back");
        GuiView.getGuiView().showScene("/FXML/playerboard.fxml");
        Platform.runLater(() -> GuiView.getGuiView().playerboard.showMenu());
    }

    public void disableActions() {
        this.activate.setDisable(true);
        this.productions.setMouseTransparent(true);
        events=false;
    }

    @Override
    public void invalidated(Observable observable) {
        if(observable instanceof FactoryView)
            this.factory.setAll(((FactoryView) observable).getProductions());
        this.productions.refresh();
    }
}
