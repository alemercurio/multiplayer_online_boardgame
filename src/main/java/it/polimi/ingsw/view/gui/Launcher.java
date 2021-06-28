package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.util.MessageManager;
import it.polimi.ingsw.util.Screen;
import javafx.application.Platform;

import java.io.IOException;
import java.util.Scanner;

public class Launcher {
    public static void main(String[] args) {
        new Thread(() -> GuiApp.main(args)).start();

        Client client = new Client();
        client.setView(GuiView.getGuiView());

        if(args.length != 2) {

            String selection = GuiView.getGuiView().selectConnection();

            if(selection.equals("esc")) return;

            Scanner connectionInfo = new Scanner(selection);
            try {
                MessageManager manager = new MessageManager(connectionInfo.next(),connectionInfo.nextInt(),GuiView.getGuiView());
                client.setMessageManager(manager, true);
                GuiView.getGuiView().serverStatus=true;
            } catch (Exception e) {
                GuiView.getGuiView().tell("Server unavailable!");
                return;
            }
        }
        else {
            try {
                MessageManager manager = new MessageManager(args[0],Integer.parseInt(args[1]),GuiView.getGuiView());
                client.setMessageManager(manager, true);
            } catch (IOException e) {
                Screen.printError("Server unavailable...");
                return;
            }
        }

        Thread t1 = new Thread(client);
        t1.setDaemon(true);
        t1.start();
    }
}
