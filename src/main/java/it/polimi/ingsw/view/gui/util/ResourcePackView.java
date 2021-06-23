package it.polimi.ingsw.view.gui.util;

import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.model.resources.ResourcePack;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class ResourcePackView {

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
