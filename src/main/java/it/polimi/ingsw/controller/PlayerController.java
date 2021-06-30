package it.polimi.ingsw.controller;

import it.polimi.ingsw.network.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PlayerController {

    private final List<String> nicknames = new LinkedList<>();
    private final Map<String,Game> disconnectedPlayer = new HashMap<>();

    private static PlayerController controller;

    public static PlayerController getPlayerController() {
        if(controller == null) controller = new PlayerController();
        return controller;
    }

    private synchronized boolean nameAvailable(String nickname) {
        if(nickname.equals("Lorenzo il Magnifico")) return false;
        for(String name : this.nicknames)
            if(nickname.equals(name)) {
                return false;
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
            this.nicknames.add(nickname);
            return true;
        } else return false;
    }

    public synchronized void postPlayerDisconnected(String nickname,Game game) {
        this.nicknames.remove(nickname);
        this.disconnectedPlayer.put(nickname,game);
    }

    public synchronized void removePlayerDisconnected(String...nickname) {
        for(String name : nickname) {
            this.disconnectedPlayer.remove(name);
        }
    }
}
