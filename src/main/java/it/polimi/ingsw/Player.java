package it.polimi.ingsw;
import it.polimi.ingsw.faith.FaithTrack;
import it.polimi.ingsw.supply.MarketBoard;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Player implements Runnable
{
    private final int ID;
    private String nickname;
    private final Socket socket;
    private final Scanner messageIn;
    private final PrintWriter messageOut;

    private Game game;
    private PlayerBoard playerBoard;
    private boolean isActive;
    private boolean hasEnded;

    public Player(int ID,Socket client) throws IOException
    {
        this.ID = ID;
        this.socket = client;
        this.messageIn = new Scanner(this.socket.getInputStream());
        this.messageOut = new PrintWriter(this.socket.getOutputStream());
    }

    public int getID() { return this.ID; }

    public String getNickname() { return this.nickname; }

    public void setForGame(FaithTrack faithTrack,MarketBoard market) {
        this.playerBoard = new PlayerBoard(market,faithTrack);
    }

    // getPoints, endGame, setWinner

    @Override
    public void run()
    {
        String msg;
        this.send("welcome");

        do {
            msg = messageIn.nextLine();
            switch(msg)
            {
                case "JoinGame":
                    if(this.joinGame()) this.playGame();
                    break;
                case "NewGame":
                    this.newGame();
                    if(this.game.isSinglePlayer()) this.playSoloGame();
                    else this.playGame();
                    break;
                default:
                    this.send("UnknownCommand");
                    break;
            }

            this.isActive = false;
            this.hasEnded = false;

        } while(!msg.equals("esc"));
    }

    public void send(String message)
    {
        System.out.println(">> sent: " + message);
        this.messageOut.println(message);
        this.messageOut.flush();
    }

    private void newGame()
    {
        String msg;
        String nickname;
        MessageParser mp = new MessageParser();

        this.send("OK");

        // Set Player Name
        msg = this.messageIn.nextLine();
        mp.parse(msg);
        while(!mp.getOrder().equals("setNickname") || mp.getNumberOfParameters() != 1)
        {
            this.send("InvalidOption");
            msg = this.messageIn.nextLine();
            mp.parse(msg);
        }
        nickname = mp.getStringParameter(0);
        this.send("OK");

        // Set Number of Player
        msg = this.messageIn.nextLine();
        mp.parse(msg);
        while(!mp.getOrder().equals("setNumPlayer") || mp.getNumberOfParameters() != 1)
        {
            this.send("InvalidNumPlayer");
            msg = this.messageIn.nextLine();
            mp.parse(msg);
        }

        this.game = Game.newGame(this,nickname,mp.getIntParameter(0));
        this.send("WAIT");

        this.game.start();
    }

    private boolean joinGame()
    {
        if(Game.hasNewGame())
        {
            String msg;
            MessageParser mp = new MessageParser();

            this.game = Game.join(this);
            this.send("OK");

            msg = this.messageIn.nextLine();
            mp.parse(msg);
            while(!mp.getOrder().equals("setNickname") || mp.getNumberOfParameters() != 1 || !this.game.nameAvailable(mp.getStringParameter(0)))
            {
                this.send("InvalidNickname");
                msg = this.messageIn.nextLine();
                mp.parse(msg);
            }
            this.game.setNickname(this, mp.getStringParameter(0));
            this.send("WAIT");

            return true;
        }
        else
        {
            this.send("NoGameAvailable");
            return false;
        }
    }

    // Round Management

    public synchronized void setActive()
    {
        this.isActive = true;
        notifyAll();
    }

    public synchronized void waitForActive()
    {
        while(!this.isActive)
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void setHasEnded()
    {
        this.hasEnded = true;
    }

    public void playGame()
    {
        while(!this.hasEnded)
        {
            this.waitForActive();
            if(!this.hasEnded)
            {
                this.playRound();
                this.isActive = false;
                this.game.nextPlayer(this);
            }
        }
    }

    public void playSoloGame()
    {
        while(!this.hasEnded)
        {
            this.playRound();
            if(!this.hasEnded) this.game.nextPlayer(this);
        }
        this.send("GameEnd");
    }

    public void playRound()
    {
        String cmd;
        this.send("PLAY");
        cmd = this.messageIn.nextLine();

        switch(cmd)
        {
            case "buyDevCard":
                System.out.println(">> " + this.game.getNickname(this) + " wants to buy a DevCard");
                if(!this.game.isSinglePlayer())
                    this.game.broadCast(this.game.getNickname(this) + " bought a DevCard...");
                this.send("OK");
                break;
            case "getResources":
                System.out.println(">> " + this.game.getNickname(this) + " wants resources");
                if(!this.game.isSinglePlayer())
                    this.game.broadCast(this.game.getNickname(this) + " has gathered resources...");
                this.game.endGame();
                this.send("OK");
                break;
        }
    }
}
