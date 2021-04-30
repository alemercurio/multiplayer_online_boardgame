package it.polimi.ingsw;

import java.util.*;

public class Game
{
    private static final List<Game> newGames = new LinkedList<Game>();

    private boolean endGame;
    private final int numPlayer;
    private Map<Integer,String> nameTable;
    private final Queue<Player> round;

    public Game(int numPlayer)
    {
        this.endGame = false;
        this.numPlayer = numPlayer;
        this.nameTable = new HashMap<Integer,String>();
        this.round = new LinkedList<Player>();
    }

    // Static Methods

    public synchronized static boolean hasNewGame()
    {
        return !Game.newGames.isEmpty();
    }

    public synchronized static Game newGame(int numPlayer)
    {
        Game game = new Game(numPlayer);
        Game.newGames.add(game);
        return game;
    }

    public synchronized static Game join(Player player)
    {
        if(Game.hasNewGame())
        {
            Game game = Game.newGames.get(0);
            game.addPlayer(player);
            return game;
        }
        else return null;
    }

    // Non-Static Methods

    public synchronized boolean nameAvailable(String name)
    {
        for(String nameTaken : this.nameTable.values())
            if(name.equals(nameTaken)) return false;
        return true;
    }

    public synchronized boolean setNickname(int playerID, String name)
    {
        if(this.nameAvailable(name))
        {
            this.nameTable.put(playerID,name);
            return true;
        }
        else return false;
    }

    public synchronized String getNickname(int playerID)
    {
        return this.nameTable.getOrDefault(playerID,"unknown");
    }

    public synchronized void addPlayer(Player player)
    {
        // Because Player objects are immutable no copy is needed.
        this.round.add(player);
        System.out.println("(GAME) >> add Player (" + this.round.size() + "/" + this.numPlayer + ")");

        if(this.round.size() == this.numPlayer)
        {
            Game.newGames.remove(this);
            Player first = this.round.poll();
            System.out.println("(GAME) >> Turno di " + first.getNickname());
            first.setActive();
        }
    }

    // Round Management

    public void nextPlayer(Player player)
    {
        this.round.add(player);
        //this.round.poll().setActive();

        Player next = this.round.poll();
        System.out.println("(GAME) >> Turno di " + next.getNickname());
        next.setActive();
    }

    public synchronized void endGame()
    {
        this.endGame = true;
    }
}

