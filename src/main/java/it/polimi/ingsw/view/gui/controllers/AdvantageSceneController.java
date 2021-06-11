package it.polimi.ingsw.view.gui.controllers;

import it.polimi.ingsw.model.resources.Resource;
import it.polimi.ingsw.model.resources.ResourcePack;
import it.polimi.ingsw.view.ViewEvent;
import it.polimi.ingsw.view.gui.GuiView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class AdvantageSceneController implements Initializable {

    @FXML
    private Label text;

    @FXML
    private ImageView inkwell;

    @FXML
    private ImageView coin;

    @FXML
    private ImageView shield;

    @FXML
    private ImageView servant;

    @FXML
    private ImageView stone;

    @FXML
    private TextField numCoinText;

    @FXML
    private TextField numShieldText;

    @FXML
    private TextField numServantText;

    @FXML
    private TextField numStoneText;

    private int numCoin = 0, numShield = 0, numServant = 0, numStone = 0;

    private int max;

    private ResourcePack chosen = new ResourcePack();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GuiView.getGuiView().advantageSetter = this;
        inkwell.setVisible(false);
        coin.setVisible(false);
        shield.setVisible(false);
        servant.setVisible(false);
        stone.setVisible(false);
        numCoinText.setVisible(false);
        numShieldText.setVisible(false);
        numServantText.setVisible(false);
        numStoneText.setVisible(false);
    }

    public void setAdvantage(ResourcePack advantage) {
        if(advantage.isEmpty()) {
            text.setText("First to play!");
            inkwell.setVisible(true);
        }
        else {
            coin.setVisible(true);
            shield.setVisible(true);
            servant.setVisible(true);
            stone.setVisible(true);
            max = advantage.get(Resource.VOID);
            text.setText("Choose "+max+"!");
        }
    }

    public void choose(Resource resource) {
        chosen.add(resource, 1);
        if(chosen.size()==max) {
            GuiView.getGuiView().event(ViewEvent.CHOOSE_RESOURCES, chosen);
        }
    }

    public void chooseCoin() {
        numCoin++;
        numCoinText.setText(""+numCoin);
        numCoinText.setVisible(true);
        choose(Resource.COIN);
    }

    public void chooseShield() {
        numShield++;
        numShieldText.setText(""+numShield);
        numShieldText.setVisible(true);
        choose(Resource.SHIELD);
    }

    public void chooseServant() {
        numServant++;
        numServantText.setText(""+numServant);
        numServantText.setVisible(true);
        choose(Resource.SERVANT);
    }

    public void chooseStone() {
        numStone++;
        numStoneText.setText(""+numStone);
        numStoneText.setVisible(true);
        choose(Resource.STONE);
    }
}
