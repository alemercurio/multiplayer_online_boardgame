package it.polimi.ingsw.view.gui.util;

import it.polimi.ingsw.model.resources.NonConsumablePackException;
import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.model.resources.ResourcePack;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ResourcePackView {

    public static class ResourcePicker {

        private final int amount;
        private final List<Resource> resources;
        private final ResourcePack selected;

        public ResourcePicker(List<Resource> resources,int amount) {
            this.amount = amount;
            this.resources = new ArrayList<>(resources);
            this.selected = new ResourcePack();
        }

        public List<Node> get(int size,int spacing) {
            List<Node> nodes = new LinkedList<>();
            for(Resource res : this.resources) {

                Button button = new Button();
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER);
                hBox.setSpacing(spacing/5.0);

                ImageView resImage = new ImageView(res.getImage());
                resImage.setFitHeight(size);
                resImage.setFitWidth(size);

                Label label = new Label("x 0");
                label.setFont(new Font(size * 0.7));

                hBox.getChildren().addAll(resImage,label);

                button.setGraphic(hBox);
                button.setStyle("-fx-background-color: transparent;");

                button.setOnMouseClicked(event ->
                {
                    if(event.getButton() == MouseButton.PRIMARY) {
                        if(selected.size() < amount) {
                            this.selected.add(res,1);
                            label.setText("x " + selected.get(res));
                        }
                    } else {
                        try { selected.consume(res,1);
                        } catch (NonConsumablePackException ignored) { }
                        label.setText("x " + selected.get(res));
                    }
                });
                nodes.add(button);
            }
            return nodes;
        }

        public ResourcePack getSelected() {
            return this.selected.getCopy();
        }
    }

    public static HBox get(ResourcePack rp,int size,int spacing) {

        HBox pack = new HBox();
        pack.setAlignment(Pos.CENTER);
        pack.setSpacing(spacing);

        for(Resource res : Resource.values()) {
            if(rp.get(res) != 0) {

                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER);
                hBox.setSpacing(size/10.0);

                ImageView resImage = new ImageView(res.getImage());
                resImage.setFitHeight(size);
                resImage.setFitWidth(size);

                Label amount = new Label(String.valueOf(rp.get(res)));
                amount.setStyle(String.format("-fx-font-size: %dpx;",(int) (size * 0.7)));

                hBox.getChildren().addAll(
                        amount,
                        resImage);
                pack.getChildren().add(hBox);
            }
        }

        return pack;
    }

}
