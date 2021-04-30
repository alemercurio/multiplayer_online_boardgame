package it.polimi.ingsw;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Player implements Runnable
{
    private final int ID;
    private final Socket socket;
    private final Scanner messageIn;
    private final PrintWriter messageOut;

    private Game game;
    private boolean isActive;

    public Player(int ID,Socket client) throws IOException
    {
        this.ID = ID;
        this.socket = client;
        this.messageIn = new Scanner(this.socket.getInputStream());
        this.messageOut = new PrintWriter(this.socket.getOutputStream());
        this.isActive = false;
    }

    public String getNickname()
    {
        return this.game.getNickname(this.ID);
    }

    // getPoints, endGame, setWinner

    public void send(String message)
    {
        this.messageOut.println(message);
        this.messageOut.flush();
    }

    @Override
    public void run()
    {
        String msg;

        this.send("welcome");

        msg = "";

        while(!msg.equals("esc"))
        {
            msg = messageIn.nextLine();
            switch(msg)
            {
                case "JoinGame":
                    //if(this.joinGame()) this.playGame();
                    this.send("KO");
                    break;
                case "NewGame":
                    /*this.newGame();
                    this.playGame();*/
                    this.send("OK");
                    break;
                case "esc":
                    this.send("Goodbye!");
                    break;
                default:
                    this.send("UnknownCommand");
                    break;
            }
        }
    }

    private boolean joinGame()
    {
        if(Game.hasNewGame())
        {
            String msg;

            this.game = Game.join(this);
            this.send("OK");

            msg = this.messageIn.nextLine();
            while(!this.game.setNickname(this.ID,msg))
            {
                this.send("NickNameNotAvailable");
                msg = this.messageIn.nextLine();
            }
            this.send("WAIT");
            return true;
        }
        else
        {
            this.send("NoGameAvailable");
            return false;
        }
    }

    private void newGame()
    {
        String msg,name;
        MessageParser mp = new MessageParser();

        this.send("OK");

        // Set Player Name
        name = this.messageIn.nextLine();
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

        this.game = Game.newGame(mp.getIntParameter(0));
        this.game.addPlayer(this);
        this.game.setNickname(this.ID,name);
        this.send("WAIT");
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

    public void playRound()
    {
        this.waitForActive();
        this.send("PLAY");
        this.messageIn.nextLine();
        this.isActive = false;
        this.game.nextPlayer(this);
    }

    public void playGame()
    {
        // TODO: codice per lo svolgimento della partita
    }
}
