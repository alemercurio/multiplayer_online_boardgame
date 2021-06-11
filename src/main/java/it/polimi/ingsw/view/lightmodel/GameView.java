package it.polimi.ingsw.view.lightmodel;

import com.google.gson.Gson;
import it.polimi.ingsw.util.Screen;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameView implements Observable {

    private final static int[] color = {202,191,81,147};
    private int currentPlayerID;
    public PlayerView[] players;
    private final List<InvalidationListener> observers = new ArrayList<>();

    public GameView() {
        this.currentPlayerID = 0;
        this.players = new PlayerView[0];
    }

    public void setCurrentPlayerID(int currentPlayerID) {
       this.currentPlayerID = currentPlayerID;
    }

    public void update(String state) {
        this.players = new Gson().fromJson(state,PlayerView[].class);

        for(InvalidationListener observer : this.observers)
            observer.invalidated(this);
    }

    public int getCurrentPlayerID() {
        return this.currentPlayerID;
    }

    public List<PlayerView> getPlayers() {
        return new ArrayList<>(Arrays.asList(this.players));
    }

    public int getPlayerColor(int playerID) {
        // Lorenzo il Magnifico
        if(playerID < 0) return 237;

        // Other players.
        for(int i = 0; i < this.players.length; i++)
            if(this.players[i].getID() == playerID) return color[i%4];

        return 13;
    }

    public String getNickname(int playerID) {
        for(PlayerView player : this.players)
            if(player.getID() == playerID) return player.getNickname();
        return "unknown";
    }

    public void setFaithMarker(int playerID,int faithMarker) {
        for(PlayerView player : this.players)
            if(player.getID() == playerID) {
                player.setFaithMarker(faithMarker);
                return;
            }

        for(InvalidationListener observer : this.observers)
            observer.invalidated(this);
    }

    public void printPlayers() {
        if(this.players.length == 0) System.out.println("\n>> wait for other player!");
        else {
            System.out.print(">> The players are ~ ");
            for(PlayerView player : this.players) System.out.print(player.getNickname() + ", ");
            System.out.println("\b\b ~");
        }
    }

    public void print() {
        System.out.println("\n>> These are the players:");
        for(PlayerView player : this.players) {
            if(player.getID() == this.currentPlayerID) player.print(153);
            else player.print();
        }
        System.out.print("\n");
    }

    public void printRank() {
        System.out.println("\n>> These are the players:");

        List<PlayerView> sortedPlayers = Arrays.stream(this.players)
                .sorted((p1,p2) -> Integer.compare(p2.getVictoryPoints(),p1.getVictoryPoints()))
                .collect(Collectors.toList());

        // Print the leading player.
        PlayerView first = sortedPlayers.remove(0);
        Screen.setColor(220);
        System.out.print("\u2654 ");
        Screen.reset();
        if(first.getID() == this.currentPlayerID) first.print(153);
        else first.print();

        // Print other players.
        for(PlayerView player : sortedPlayers) {
            if(player.getID() < 0) System.out.print("\u269C ");
            else System.out.print("~ ");
            if(player.getID() == this.currentPlayerID) player.print(153);
            else player.print();
        }
        System.out.print("\n");
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        this.observers.add(invalidationListener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        this.observers.remove(invalidationListener);
    }
}
