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

    private Talkie talkie;
    private Talkie online;
    private Talkie offline;

    private final View view;
    private final LinkedList<String> answer = new LinkedList<>();

    public MessageManager(View view) {
        this.offline = MessageBridge.getBridge().get();
        new Thread(new LocalPlayer(1)).start();
        this.talkie = this.offline;
        this.view = view;
    }

    public MessageManager(String ipAddress, int port, View view) throws IOException {
        this.online = new Talkie() {
            private final Socket socket;
            private Scanner messageIn;
            private final PrintWriter messageOut;

            {
                this.socket = new Socket(ipAddress,port);
                this.messageIn = new Scanner(this.socket.getInputStream());
                this.messageOut = new PrintWriter(this.socket.getOutputStream());
            }

            @Override
            public void send(String message) {
                if(message.equals("stop")) {
                    this.messageIn.close();
                    try {
                        this.messageIn = new Scanner(this.socket.getInputStream());
                    } catch (IOException ignored) { }
                } else {
                    this.messageOut.println(message);
                    this.messageOut.flush();
                }
            }

            @Override
            public String receive() {
                try {
                    return this.messageIn.nextLine();
                } catch(Exception e) {
                    return "stop";
                }
            }

            @Override
            public void close() {
                this.messageOut.close();
                this.messageIn.close();
                try {
                    this.socket.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        };
        this.talkie = this.online;
        this.view = view;
    }

    public synchronized void setOffline() {
        if(this.online != null) {
            this.online.send("stop");
        }
        if(this.offline == null) {
            this.offline = MessageBridge.getBridge().get();
            new Thread(new LocalPlayer(1,this.view.getNickname())).start();
        }
        this.talkie = this.offline;
    }

    public synchronized void setOnline() {
        if(this.offline != null) this.offline.send("stop");
        if(this.online != null) this.talkie = this.online;
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

                synchronized(this) {
                    message = this.talkie.receive();
                }
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
                    case "stop":
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
