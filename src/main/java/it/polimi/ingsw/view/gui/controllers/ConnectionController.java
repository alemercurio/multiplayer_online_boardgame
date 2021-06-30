package it.polimi.ingsw.view.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class ConnectionController implements Initializable {

    @FXML
    TextField ipSelect,portSelect;

    private Dialog<String> dialog;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ipSelect.setOnMouseClicked(event -> ipSelect.requestFocus());
        portSelect.setOnMouseClicked(event -> portSelect.requestFocus());
    }

    public void setDialog(Dialog<String> dialog) {
        this.dialog = dialog;
    }

    public void online() {
        String ip = ipSelect.getText();
        String port = portSelect.getText();

        if(ip.matches("[ ]*(?:[0-9]{1,3}.){3}(?:[0-9]{1,3})[ ]*")) {
            if(port.matches("[ ]*[0-9]{1,5}[ ]*")) {
                this.dialog.setResult(ipSelect.getText() + " " + portSelect.getText());
            } else {
                portSelect.clear();
                portSelect.setPromptText("Please, use a valid IP address");
            }
        } else {
            ipSelect.clear();
            ipSelect.setPromptText("Please, select a port");
        }
    }

    public void offline() {
        this.dialog.setResult("offline");
    }
}
