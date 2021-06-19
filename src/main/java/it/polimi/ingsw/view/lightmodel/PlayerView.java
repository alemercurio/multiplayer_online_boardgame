package it.polimi.ingsw.view.lightmodel;

import it.polimi.ingsw.model.resources.ResourcePack;
import it.polimi.ingsw.util.Screen;

public class PlayerView {

    private final int id;
    private final String nickname;
    private final ResourcePack resources;
    private int faithMarker;
    private int previousMarker;
    private final int victoryPoints;

    public PlayerView(int id,String nickname,ResourcePack res,int faithMarker,int victoryPoints)
    {
        this.id = id;
        this.nickname = nickname;
        this.resources = res.getCopy();
        this.faithMarker = faithMarker;
        this.victoryPoints = victoryPoints;
    }

    public int getID()
    {
        return this.id;
    }

    public ResourcePack getResources() {
        return this.resources.getCopy();
    }

    public String getNickname() {
        if(this.nickname == null) return "...";
        else return this.nickname;
    }

    public int getFaithMarker()
    {
        return this.faithMarker;
    }

    public int getPreviousMarker() {
        return this.previousMarker;
    }

    public void setFaithMarker(int faithMarker) {
        if(this.faithMarker>0) {
            this.previousMarker = this.faithMarker;
        }
        else this.previousMarker = 0;
        this.faithMarker = faithMarker;
    }

    public void setPreviousMarker(int faithMarker) {
        this.previousMarker=faithMarker;
    }

    public int getVictoryPoints() {
        return this.victoryPoints;
    }

    public void print() {
        if(this.id < 0) System.out.println(this.nickname);
        else {
            System.out.print(this.nickname + ": resources: ");
            Screen.print(this.resources);
            Screen.setColor(214);
            System.out.println(" PV: " + this.victoryPoints + " ");
            Screen.reset();
        }
    }

    public void print(int color) {

        Screen.setColor(color);
        System.out.print(this.nickname);
        Screen.reset();

        if(this.id < 0) System.out.print("\n");
        else {
            System.out.print(": resources: ");
            Screen.print(this.resources);

            Screen.setColor(214);
            System.out.println(" PV: " + this.victoryPoints + " ");
            Screen.reset();
        }
    }
}
