package it.polimi.ingsw.util;

import it.polimi.ingsw.controller.Action;
import it.polimi.ingsw.controller.GameEvent;
import it.polimi.ingsw.view.View;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class MessageManager extends Thread {

    private final Socket socket;
    private final Scanner messageIn;
    private final PrintWriter messageOut;
    private final View view;
    private final LinkedList<String> answer = new LinkedList<>();

    public MessageManager(String ipAddress, int port, View view) throws IOException {
        this.socket = new Socket(ipAddress,port);
        this.messageIn = new Scanner(this.socket.getInputStream());
        this.messageOut = new PrintWriter(this.socket.getOutputStream());
        this.view = view;
    }

    public synchronized void report(String message) {
        this.answer.add(message);
        notifyAll();
    }

    public synchronized String receive() {
        try {
            while(this.answer.isEmpty()) wait();
            return this.answer.poll();
        }
        catch(Exception e) {
            return null;
        }
    }

    public void send(String message) {
        this.messageOut.println(message);
        this.messageOut.flush();
    }

    public void close() throws IOException {
        this.messageOut.close();
        this.messageIn.close();
        this.socket.close();
    }

    public void run() {

        String message;
        MessageParser mp = new MessageParser();

        try {
            while (true) {
                message = this.messageIn.nextLine();
                mp.parse(message);

                switch (mp.getOrder()) {
                    case "update":
                        this.view.update(mp.getStringParameter(0), mp.getStringParameter(1));
                        break;
                    case "event":
                        this.view.throwEvent(GameEvent.valueOf(mp.getStringParameter(0)),mp.getStringParameter(1));
                        break;
                    case "action":
                        this.view.showAction(mp.getStringParameter());
                        break;
                    case "alive?":
                        this.send("alive");
                        break;
                    default:
                        this.report(message);
                        break;
                }
            }
        }
        catch(NoSuchElementException ignored) { }
    }
}
