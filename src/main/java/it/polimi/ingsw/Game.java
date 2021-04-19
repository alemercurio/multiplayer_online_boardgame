package it.polimi.ingsw;

import java.util.LinkedList;
import java.util.Queue;

public class Game
{
    private int numPlayer;
    private final boolean isSinglePlayer;
    private final Queue<Player> turn;
    private boolean finalRound;

    public Game(boolean isSinglePlayer)
    {
        this.numPlayer = 0;
        this.isSinglePlayer = isSinglePlayer;
        this.turn = new LinkedList<Player>();
        this.finalRound = false;
    }

    public boolean join(Player p)
    {
        if(this.numPlayer >= 4) return false;
        else
        {
            this.numPlayer = this.numPlayer + 1;
            this.turn.add(p);
            return true;
        }
    }
}
