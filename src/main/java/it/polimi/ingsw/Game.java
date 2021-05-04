package it.polimi.ingsw;

import it.polimi.ingsw.faith.Vatican;
import it.polimi.ingsw.solo.LorenzoIlMagnifico;
import it.polimi.ingsw.solo.SoloAction;
import it.polimi.ingsw.solo.SoloActionDeck;
import it.polimi.ingsw.supply.MarketBoard;

import java.util.*;

public class Game
{
    private static final List<Game> newGames = new LinkedList<Game>();

    private final int numPlayer;
    private final Map<Integer,String> nameTable;
    private final Queue<Player> round;
    private Player playerWithInkwell;
    private LorenzoIlMagnifico lorenzo;

    private boolean endGame;
    private final Vatican vatican;
    private final MarketBoard market;


    public Game(int numPlayer)
    {
        this.numPlayer = numPlayer;
        this.nameTable = new HashMap<Integer,String>();
        this.round = new LinkedList<Player>();

        this.endGame = false;
        this.vatican = new Vatican(this,"src/main/resources/JSON/Vatican.json");
        this.market = new MarketBoard();
    }

    public Game(Player creator, String nickname, int numPlayer)
    {
        this(numPlayer);

        this.addPlayer(creator);
        creator.setForGame(this.vatican.getFaithTrack(),this.market);
        this.nameTable.put(creator.getID(),nickname);
        this.playerWithInkwell = creator;
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

    public synchronized static Game newGame(Player creator, String nickname, int numPlayer)
    {
        Game game = new Game(creator, nickname, numPlayer);
        if(numPlayer > 1)
        {
            Game.newGames.add(game);
        }
        else
        {
            game.setSinglePlayer();
        }
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

    public boolean nameAvailable(String name)
    {
        synchronized(this.nameTable)
        {
            for(String nameTaken : this.nameTable.values())
                if(name.equals(nameTaken)) return false;
            return true;
        }
    }

    public boolean setNickname(int playerID, String name)
    {
        synchronized(this.nameTable)
        {
            if (this.nameAvailable(name))
            {
                this.nameTable.put(playerID, name);
                this.nameTable.notifyAll();
                return true;
            } else return false;
        }
    }

    public String getNickname(int playerID) {
        synchronized(this.nameTable)
        {
            return this.nameTable.getOrDefault(playerID,"unknown");
        }
    }

    public void broadCast(String message)
    {
        for(Player player : this.round) {
            player.send(message);
        }
    }

    public synchronized void addPlayer(Player player)
    {
        this.round.add(player);
        player.setForGame(this.vatican.getFaithTrack(),this.market);

        // TODO: remove
        System.out.println("(GAME) >> add Player (" + this.round.size() + "/" + this.numPlayer + ")");

        if(this.round.size() == this.numPlayer) Game.newGames.remove(this);
    }

    // Round Management

    public void mayStart()
    {
        // Ensures that every player has set its nickname

        synchronized(this.nameTable)
        {
            while(this.nameTable.size() != this.numPlayer)
            {
                try {
                    this.nameTable.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        this.broadCast("GameStart");
        Player first = this.round.poll();
        System.out.println("(GAME) >> Turno di " + this.nameTable.get(first.getID()));
        first.setActive();
    }

    public void nextPlayer(Player player)
    {
        this.round.add(player);
        Player next = this.round.peek();

        if(this.endGame && this.playerWithInkwell == next)
        {
            for(Player play : this.round)
            {
                play.setHasEnded();
                play.setActive();
            }
            System.out.println(">> game ends...");
            this.broadCast("GameEnd");
        }
        else
        {
            next = this.round.poll();
            System.out.println("(GAME) >> Turno di " + this.nameTable.get(next.getID()));
            next.setActive();
        }
    }

    // SinglePlayer

    public boolean isSinglePlayer()
    {
        return (this.numPlayer <= 1);
    }

    public void setSinglePlayer()
    {
        SoloActionDeck soloDeck = new SoloActionDeck(SoloAction.getSoloActionDeck("src/main/resources/JSON/SoloAction.json"));
        this.lorenzo = new LorenzoIlMagnifico(this.vatican.getFaithTrack(),this.market,soloDeck);
    }

    public void playSolo()
    {
        SoloAction action = this.lorenzo.playSoloAction();
        this.broadCast(action.toString());
    }

    public synchronized void endGame()
    {
        this.endGame = true;
        if(this.isSinglePlayer()) this.playerWithInkwell.setHasEnded();
    }
}