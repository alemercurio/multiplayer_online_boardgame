package it.polimi.ingsw.util;

import it.polimi.ingsw.network.Talkie;

import java.io.Closeable;
import java.util.*;

public class MessageBridge {

    public static class Port implements Talkie,Closeable {

        private final List<String> messageIn;

        protected Port() {
            this.messageIn = new LinkedList<>();
        }

        public String receive() {
            synchronized(this.messageIn) {
                while(this.messageIn.isEmpty()) {
                    try {
                        this.messageIn.wait();
                    } catch(InterruptedException ignored) { }
                }
                String msg = this.messageIn.remove(0);
                System.out.println(">>> < " + msg);
                return msg;
                //return this.messageIn.remove(0);
            }
        }

        public void send(String message) {
            MessageBridge.bridge.multicast(message,this);
        }

        public void close() {
            MessageBridge.bridge.remove(this);
        }
    }

    private final List<Port> net;
    private static MessageBridge bridge;

    private MessageBridge() {
        this.net = new ArrayList<>();
    }

    public static MessageBridge getBridge() {
        if(bridge == null) bridge = new MessageBridge();
        return bridge;
    }

    public Port get() {
        Port gateway = new Port();
        this.net.add(gateway);
        return gateway;
    }

    protected void multicast(String message,Port sender) {
        for(Port port : this.net) {
            if(!port.equals(sender)) {
                synchronized (port.messageIn) {
                    port.messageIn.add(message);
                    port.messageIn.notifyAll();
                }
            }
        }
    }

    protected void remove(Port port) {
        this.net.remove(port);
    }
}
