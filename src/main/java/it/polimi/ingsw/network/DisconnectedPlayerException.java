package it.polimi.ingsw.network;

public class DisconnectedPlayerException extends RuntimeException{

    private final Player player;

    public DisconnectedPlayerException(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }
}
