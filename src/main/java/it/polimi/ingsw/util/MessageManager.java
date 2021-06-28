package it.polimi.ingsw.util;

import it.polimi.ingsw.controller.GameEvent;
import it.polimi.ingsw.controller.LocalPlayer;
import it.polimi.ingsw.network.Talkie;
import it.polimi.ingsw.view.View;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class MessageManager extends Thread {

    private final Talkie talkie;
    private final View view;
    private final LinkedList<String> answer = new LinkedList<>();

    public MessageManager(View view) {
        new Thread(new LocalPlayer(1)).start();
        this.talkie = MessageBridge.getBridge().get();;
        this.view = view;
    }

    public MessageManager(String ipAddress, int port, View view) throws IOException {
        this.talkie = new Talkie() {
            private final Socket socket;
            private final Scanner messageIn;
            private final PrintWriter messageOut;

            {
                this.socket = new Socket(ipAddress,port);
                this.messageIn = new Scanner(this.socket.getInputStream());
                this.messageOut = new PrintWriter(this.socket.getOutputStream());
            }

            @Override
            public void send(String message) {
                this.messageOut.println(message);
                this.messageOut.flush();
            }

            @Override
            public String receive() {
                return this.messageIn.nextLine();
            }

            @Override
            public void close() {
                try {
                    this.messageOut.close();
                    this.messageIn.close();
                    this.socket.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        };
        this.view = view;
    }

    public void report(String message) {
        synchronized(this.answer) {
            this.answer.add(message);
            this.answer.notifyAll();
        }
    }

    public String receive() {
        synchronized(this.answer) {
            try {
                while(this.answer.isEmpty()) this.answer.wait();
                return answer.poll();
            } catch(Exception e) { return null; }
        }
    }

    public void send(String message) {
        this.talkie.send(message);
    }

    public void close()  {
        this.talkie.close();
    }

    public void run() {

        String message;
        MessageParser mp = new MessageParser();

        try {
            while (true) {

                message = this.talkie.receive();
                mp.parse(message);

                switch(mp.getOrder()) {
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
