package it.polimi.ingsw.faith;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.view.GameView;
import it.polimi.ingsw.view.PlayerView;
import it.polimi.ingsw.view.Screen;

public class FaithView {

    private Vatican.Space[] spaces = new Vatican.Space[0];
    private Vatican.ReportSection[] popeSpaces = new Vatican.ReportSection[0];
    private final GameView players;

    public FaithView(GameView players) {
        this.players = players;
    }

    public void setTrack(String trackData) {
        Gson parser = new Gson();
        JsonObject vaticanData = parser.fromJson(trackData, JsonElement.class).getAsJsonObject();
        this.spaces = parser.fromJson(vaticanData.get("track"),Vatican.Space[].class);
        this.popeSpaces = parser.fromJson(vaticanData.get("reportSections"),Vatican.ReportSection[].class);
    }

    public void setConfig(JsonObject configData) {
        Gson parser = new Gson();
        Vatican.State[] states = parser.fromJson(configData.get("popeFavours"),Vatican.State[].class);
        for(int i = 0; i < states.length; i++) this.popeSpaces[i].setState(states[i]);
    }

    public void update(String state) {

        JsonObject faithData = new Gson().fromJson(state,JsonElement.class).getAsJsonObject();
        int configID = faithData.get("ID").getAsInt();

        if(configID == this.players.getCurrentPlayerID())
            this.setConfig(faithData);
        this.players.setFaithMarker(configID,faithData.get("faithMarker").getAsInt());
    }

    public void print() {

        int[] popeSection = new int[this.spaces.length];

        for(int i = 0; i < this.popeSpaces.length; i++) {
            Vatican.ReportSection ps = this.popeSpaces[i];
            popeSection[ps.getFirstSpace()] = i + 1;
            popeSection[ps.getLastSpace()] = - (i + 1);
        }

        for (int i = 0; i < spaces.length; i++) {
            Vatican.Space space = spaces[i];

            if(popeSection[i] > 0){

                Vatican.ReportSection ps = this.popeSpaces[popeSection[i] - 1];

                Screen.setColor(196);
                System.out.print("[");

                switch (ps.getState()) {

                    case AVAILABLE:
                        Screen.printExponentNumber(ps.getPoints());
                        System.out.print(" ");
                        break;

                    case GOT:
                        System.out.print("\u02DA ");
                        break;

                    case LOST:
                        System.out.print("\u02E3 ");
                        break;
                }

                Screen.reset();
            }

            if (space.getPoints() != 0) {
                Screen.setColor(220);
                Screen.printCircledNumber(space.getPoints());
            } else {
                System.out.print(i);
            }
            Screen.reset();

            for(PlayerView player : this.players.getPlayers()) {
                if(player.getFaithMarker() == i) {
                    Screen.setColor(this.players.getPlayerColor(player.getID()));
                    System.out.print('\u2020');
                    Screen.reset();
                }
            }

            if(popeSection[i] < 0){
                Screen.setColor(196);
                System.out.print(" ] ");
                Screen.reset();
            }
            else System.out.print(" ");
        }

        System.out.print("\n\t");

        for(PlayerView player : this.players.getPlayers()) {
            Screen.setColor(this.players.getPlayerColor(player.getID()));
            System.out.print("\u2020 " + player.getNickname());
            Screen.reset();
            System.out.print(" ~ ");
        }

        System.out.print("\b\b");
        Screen.reset();
    }
}
