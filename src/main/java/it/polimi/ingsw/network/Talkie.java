package it.polimi.ingsw.network;

public interface Talkie {
    void send(String message);
    String receive();
    void close();
}
