package it.polimi.ingsw;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Client
{
    private Socket socket;
    private Scanner messageIn;
    private PrintWriter messageOut;

    public void connect(String ipAddress) throws IOException
    {
        this.socket = new Socket(ipAddress,2703);
        this.messageIn = new Scanner(this.socket.getInputStream());
        this.messageOut = new PrintWriter(this.socket.getOutputStream());
    }

    public void send(String message)
    {
        this.messageOut.println(message);
        this.messageOut.flush();
    }

    /*private final List<String> answer = new LinkedList<String>();
    public synchronized void report(String message)
    {
        this.answer.add(message);
    }
    public synchronized String getAnswer()
    {
        try {
            while(this.answer.isEmpty()) wait();
            return this.answer.remove(0);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }*/

    public void close() throws IOException
    {
        this.messageOut.close();
        this.messageIn.close();
        this.socket.close();
    }

    public static void main(String[] args)
    {
        Client client = new Client();
        try {
            client.connect("127.0.0.1");
        } catch (IOException e) {
            System.out.println(">> Server unavailable...");
        }

        if(!client.messageIn.nextLine().equals("welcome"))
            System.out.println(">> Unable to be welcomed..");
        else System.out.println(">> Successfully connected..");

        Scanner input = new Scanner(System.in);
        String msg = "";

        while(!msg.equals("esc"))
        {
            System.out.print(">> ");
            msg = input.nextLine();

            switch(msg)
            {
                case "new":
                    client.newGame();
                    break;
                case "join":
                    client.joinGame();
                    break;
            }
        }

        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void newGame()
    {
        String answer;
        Scanner input = new Scanner(System.in);

        this.send("NewGame");
        if(!this.messageIn.nextLine().equals("OK"))
        {
            System.out.println(">> Something went wrong...");
            return;
        }

        System.out.print("\tChoose a nickname! << ");
        this.send("setNickname(" + input.nextLine() + ")");
        if(!this.messageIn.nextLine().equals("OK"))
        {
            System.out.println(">> Something went wrong...");
            return;
        }

        System.out.print("\tHow many players? << ");
        this.send("setNumPlayer(" + input.nextInt() + ")");

        answer = this.messageIn.nextLine();
        while(answer.equals("invalidNumPlayer"))
        {
            System.out.print("\tChoose another number of player << ");
            this.send("setNumPlayer(" + input.nextInt() + ")");
        }

        if(!answer.equals("WAIT")) System.out.println("Something went wrong... >> " + answer);

        this.playGame();
    }

    public void joinGame()
    {
        String answer;
        Scanner input = new Scanner(System.in);

        this.send("JoinGame");

        if(!this.messageIn.nextLine().equals("OK"))
        {
            System.out.println(">> No game available!");
            return;
        }

        System.out.print("\tChoose a nickname! << ");
        this.send("setNickname(" + input.nextLine() + ")");
        while(!this.messageIn.nextLine().equals("WAIT"))
        {
            System.out.print("\tChoose another nickname! << ");
            this.send("setNickname(" + input.nextLine() + ")");
        }

        this.playGame();
    }

    public void playGame()
    {
        String answer;
        Scanner input = new Scanner(System.in);

        do { answer = this.messageIn.nextLine(); } while(!answer.equals("GameStart"));

        boolean active = true;
        while(active)
        {
            do {
                answer = this.messageIn.nextLine();
                if(answer.equals("GameEnd")) active = false;
                else if(!answer.equals("PLAY")) System.out.println(">> " + answer);

            } while(!answer.equals("PLAY") && active);

            if(answer.equals("PLAY"))
            {
                String cmd;
                System.out.print("playing >> ");
                cmd = input.nextLine();

                switch(cmd)
                {
                    case "buyDevCard":
                        this.send("buyDevCard");
                        break;
                    case "getResources":
                        this.send("getResources");
                        break;
                }

                System.out.println(">> " + this.messageIn.nextLine());
            }
        }

        System.out.println(">> Game has ended...");
    }
}
