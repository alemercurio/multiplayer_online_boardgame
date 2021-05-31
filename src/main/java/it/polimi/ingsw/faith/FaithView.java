package it.polimi.ingsw.faith;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.view.Screen;

public class FaithView {

    private int faithMarker;
    private Vatican.Space[] spaces = new Vatican.Space[0];
    private Vatican.ReportSection[] popeSpaces = new Vatican.ReportSection[0];

    public void setTrack(String trackData) {
        Gson parser = new Gson();
        JsonObject vaticanData = parser.fromJson(trackData, JsonElement.class).getAsJsonObject();
        this.spaces = parser.fromJson(vaticanData.get("track"),Vatican.Space[].class);
        this.popeSpaces = parser.fromJson(vaticanData.get("reportSections"),Vatican.ReportSection[].class);
    }

    public void setConfig(String config) {
        Gson parser = new Gson();
        JsonObject configData = parser.fromJson(config,JsonElement.class).getAsJsonObject();
        this.faithMarker = parser.fromJson(configData.get("faithMarker"),int.class);

        Vatican.State[] states = parser.fromJson(configData.get("popeFavours"),Vatican.State[].class);
        for(int i = 0; i < states.length; i++) this.popeSpaces[i].setState(states[i]);
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

            if(this.faithMarker == i){
                Screen.setColor(40);
                System.out.print('\u2020');
                Screen.reset();
            }

            if(popeSection[i] < 0){
                Screen.setColor(196);
                System.out.print(" ] ");
                Screen.reset();
            }
            else System.out.print(" ");
        }
    }
}
