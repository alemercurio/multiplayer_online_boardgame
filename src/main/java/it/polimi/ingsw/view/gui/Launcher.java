package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.controller.Error;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.util.MessageManager;
import javafx.application.Platform;

import java.util.Scanner;

public class Launcher {
    public static void main(String[] args) {
        new Thread(() -> GuiApp.main(args)).start();

        Client client = new Client();
        client.setView(GuiView.getGuiView());

        if(args.length != 2) {

            String selection = GuiView.getGuiView().selectConnection();
            switch(selection) {

                case "esc":
                    return;

                case "offline":
                    client.setMessageManager(new MessageManager(GuiView.getGuiView()),false);
                    GuiView.getGuiView().online = false;
                    break;

                default:
                    Scanner connectionInfo = new Scanner(selection);
                    try {
                        MessageManager manager = new MessageManager(connectionInfo.next(),connectionInfo.nextInt(),GuiView.getGuiView());
                        client.setMessageManager(manager,true);
                        GuiView.getGuiView().online = true;
                    } catch (Exception e) {
                        GuiView.getGuiView().showError(Error.SERVER_OFFLINE);
                        GuiView.getGuiView().online = false;
                        return;
                    }
                    break;
            }
        } else {
            try {
                MessageManager manager = new MessageManager(args[0],Integer.parseInt(args[1]),GuiView.getGuiView());
                client.setMessageManager(manager,true);
                GuiView.getGuiView().online = true;
            } catch (Exception e) {
                GuiView.getGuiView().showError(Error.SERVER_OFFLINE);
                GuiView.getGuiView().online = false;
                return;
            }
        }

        Thread t1 = new Thread(client);
        t1.setDaemon(true);
        t1.start();
    }
}
