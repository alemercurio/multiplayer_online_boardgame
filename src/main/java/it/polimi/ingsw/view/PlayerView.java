package it.polimi.ingsw.view;

import it.polimi.ingsw.supply.ResourcePack;

public class PlayerView {

    private final int id;
    private final String nickname;
    private final ResourcePack resources;
    private int faithMarker;
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

    public String getNickname()
    {
        return this.nickname;
    }

    public int getFaithMarker()
    {
        return this.faithMarker;
    }

    public void setFaithMarker(int faithMarker) {
        this.faithMarker = faithMarker;
    }

    public int getVictoryPoints() {
        return this.victoryPoints;
    }

    public void print() {
        System.out.print(this.nickname + ": resources: ");
        Screen.print(this.resources);

        Screen.setColor(214);
        System.out.println(" PV: " + this.victoryPoints + " ");
        Screen.reset();
    }

    public void print(int color) {

        Screen.setColor(color);
        System.out.print(this.nickname);
        Screen.reset();

        System.out.print(": resources: ");
        Screen.print(this.resources);

        Screen.setColor(214);
        System.out.println(" PV: " + this.victoryPoints + " ");
        Screen.reset();
    }
}
