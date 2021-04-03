package it.polimi.ingsw;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;


public class Vatican {
    private ArrayList<FaithTrack> faithTracks;
    private int[] track; // victory points that you get when you pass the square
    private int [][] reportSection; // first row: for the  report section set by the players; second row: first column for the initial square of the section, second for the last one, third for the victory point  
    private  boolean [] reportActive;
    private  final Game game;
    private boolean finalRound = false;
    private int lastFavourActivated = 0;


    public Vatican(ArrayList<FaithTrack> faithTracks, int[] track, int[][] reportSection, boolean[] reportActive, Game game, boolean finalRound) {
        this.faithTracks = faithTracks;
        this.track = track;
        this.reportSection = reportSection;
        this.reportActive = reportActive;
        this.game = game;
    }

    /**public static void main(String[] args) {

        JsonParser jsonParser = new JsonParser();
        try {
            FileReader reader = new FileReader("C:\\Users\\patri\\ing-sw-2021-mercurio-niantcho-tosini\\src\\main\\java\\it\\polimi\\ingsw\\parameters.json");
            Object obj = jsonParser.parse(reader);
            JsonObject trackJson = (JsonObject) obj;

            JsonElement track = trackJson.get("track");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }**/

    public void vaticanReport(int faithMarker, int index){
        FaithTrack current_faithTrack;
        int current_faithMaker = 0;

        for (int i = 0; i < faithTracks.size(); i++) {
            current_faithTrack = faithTracks.get(i);
            current_faithMaker = current_faithTrack.getFaithMarker();

            for (int j = lastFavourActivated + 1; j <= index; j++)
            {
                if (!reportActive[j])
                {
                    if (current_faithMaker < reportSection[j][0])
                    {
                        if (current_faithTrack.getPopeFavour(j) == 0){
                            current_faithTrack.disCardPopeFavour(j);
                        }
                    }
                    else
                    {
                        if (current_faithTrack.getPopeFavour(j) == 0) {
                            current_faithTrack.givePopeFavour(j);
                        }
                    }
                }

            }
        }
        for (int i = lastFavourActivated + 1; i <= index; i++)
            reportActive[i] = true;
        lastFavourActivated = index;

    }
    public void wastedResources(int track, int num){ // track is the trackID which also corresponds to the index of the faith track in the arrayList of faithTrack
        for ( int i = 0; i < faithTracks.size(); i++){
            if ( i != track){
                    faithTracks.get(i).advance(num);
            }
        }

        for ( int i = 0; i < faithTracks.size(); i++)
            faithTracks.get(i).report();

    }

    public void endGame()
    {
        if (finalRound = true){game.endGame();}
    }

    public int getReportSpace(int row, int colomn){
        return reportSection[row][colomn];
    }

    public int getNumberSection (){
        return reportSection.length;
    }

    public boolean getFavourStatus (int index){
        return reportActive[index];
    }


    public int getLastFavourActivated(){return this.lastFavourActivated;}
    public void setFinalRound () {finalRound = true;}

}
