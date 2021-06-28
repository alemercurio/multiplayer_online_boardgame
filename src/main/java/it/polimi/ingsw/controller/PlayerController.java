package it.polimi.ingsw.controller;

import it.polimi.ingsw.network.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PlayerController {

    private final Map<String,Player> nameTable = new HashMap<>();
    private final Map<String,Game> disconnectedPlayer = new HashMap<>();

    private static PlayerController controller;

    public static PlayerController getPlayerController() {
        if(controller == null) controller = new PlayerController();
        return controller;
    }

    private synchronized boolean nameAvailable(String nickname) {
        for(Map.Entry<String,Player> name : this.nameTable.entrySet())
            if(nickname.equals(name.getKey())) {
                return !name.getValue().ping();
            }
        return true;
    }

    public boolean hasLeftGame(String nickname) {
        return this.disconnectedPlayer.containsKey(nickname);
    }

    public Game resumeGame(String nickname) {
        return this.disconnectedPlayer.remove(nickname);
    }

    public synchronized boolean registerNickname(String nickname,Player player) {
        if(this.nameAvailable(nickname)) {
            this.nameTable.put(nickname,player);
            return true;
        } else return false;
    }

    public synchronized void postPlayerDisconnected(String nickname,Game game) {
        this.nameTable.remove(nickname);
        this.disconnectedPlayer.put(nickname,game);
    }

    public synchronized void removePlayerDisconnected(String...nickname) {
        for(String name : nickname) {
            this.disconnectedPlayer.remove(name);
        }
    }
}
