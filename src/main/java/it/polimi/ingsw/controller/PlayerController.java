package it.polimi.ingsw.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PlayerController {

    private final List<String> nameList = new LinkedList<>();
    private final Map<String,Game> disconnectedPlayer = new HashMap<>();

    private static PlayerController controller;

    public static PlayerController getPlayerController() {
        if(controller == null) controller = new PlayerController();
        return controller;
    }

    private synchronized boolean nameAvailable(String nickname) {
        for(String name : this.nameList)
            if(nickname.equals(name)) return false;
        return true;
    }

    public boolean hasLeftGame(String nickname) {
        return this.disconnectedPlayer.containsKey(nickname);
    }

    public Game resumeGame(String nickname) {
        return this.disconnectedPlayer.remove(nickname);
    }

    public synchronized boolean registerNickname(String nickname) {
        if(this.nameAvailable(nickname)) {
            this.nameList.add(nickname);
            return true;
        } else return false;
    }

    public synchronized void postPlayerDisconnected(String nickname,Game game) {
        this.nameList.remove(nickname);
        this.disconnectedPlayer.put(nickname,game);
    }

    public synchronized void freeName(String...nickname) {
        for(String name : nickname) {
            this.disconnectedPlayer.remove(name);
            this.nameList.remove(name);
        }
    }
}
