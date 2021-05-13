package it.polimi.ingsw;

import it.polimi.ingsw.cards.*;
import it.polimi.ingsw.supply.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import java.util.*;

public class Client
{
    private Socket socket;
    private Scanner messageIn;
    private PrintWriter messageOut;

    private final LinkedList<String> answer = new LinkedList<String>();

    public void connect(String ipAddress) throws IOException
    {
        this.socket = new Socket(ipAddress,2703);
        this.messageIn = new Scanner(this.socket.getInputStream());
        this.messageOut = new PrintWriter(this.socket.getOutputStream());
    }

    public static class MessageManager extends Thread
    {
        private final Client client;
        private final Scanner messageIn;

        public MessageManager(Client client,Scanner messageIn)
        {
            this.client = client;
            this.messageIn = messageIn;
        }

        public void run()
        {
            try {
                while (true)
                {
                    this.client.report(this.messageIn.nextLine());
                }
            }
            catch(NoSuchElementException ignored) { }
        }
    }

    public synchronized void report(String message)
    {
        this.answer.add(message);
        notifyAll();
    }

    public synchronized String receive()
    {
        //return this.messageIn.nextLine();
        try {
            while(this.answer.isEmpty()) wait();
            return this.answer.poll();
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public void send(String message)
    {
        this.messageOut.println(message);
        this.messageOut.flush();
    }

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

        Thread t = new Client.MessageManager(client,client.messageIn);
        t.start();

        if(!client.receive().equals("welcome"))
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
        if(!this.receive().equals("OK"))
        {
            System.out.println(">> Something went wrong...");
            return;
        }

        System.out.print("\tChoose a nickname! << ");
        this.send("setNickname(" + input.nextLine() + ")");
        if(!this.receive().equals("OK"))
        {
            System.out.println(">> Something went wrong...");
            return;
        }

        System.out.print("\tHow many players? << ");
        this.send("setNumPlayer(" + input.nextInt() + ")");

        answer = this.receive();
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
        Scanner input = new Scanner(System.in);

        this.send("JoinGame");

        if(!this.receive().equals("OK"))
        {
            System.out.println(">> No game available!");
            return;
        }

        System.out.print("\tChoose a nickname! << ");
        this.send("setNickname(" + input.nextLine() + ")");
        while(!this.receive().equals("WAIT"))
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

        do { answer = this.receive(); } while(!answer.equals("GameStart"));

        boolean active = true;
        while(active)
        {
            do {
                answer = this.receive();
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
                        this.buyDevelopmentCard();
                        break;
                    case "getResources":
                        this.send("getResources");
                        break;
                }

                System.out.println(">> " + this.receive());
            }
        }

        System.out.println(">> Game has ended...");
    }
    public void buyDevelopmentCard () {
        String answer = " ";
        boolean existsCard = false;
        Scanner input = new Scanner(System.in);
        String msg = "";
        MessageParser mp = new MessageParser();
        int cardLevel;
        int cardColorIndex;

        //TODO: create a market view that allows the player to see the card the he satisfies the requirements
        MarketBoard marketBoard = new MarketBoard();

        answer = this.receive();
        if (answer.equals("OK")) {
            do {
                // if the server doesn't allows the card to be bought
                if (answer.equals("KO")){
                    System.out.println("It's not possible to buy this card, please choose another one.");
                    existsCard = false;
                }
                System.out.println(">> This is the market board, please choose a card..\n" + marketBoard.toString());
                        //at this step, the command must be "buy" or "esc"
                while (!existsCard) {
                    msg = checkCommand("G1", "G2", "G3", "B1", "B2", "B3", "Y1", "Y2", "Y3", "P1", "P2", "P3", "esc");
                    if (msg.equals("esc")) {
                        System.out.println(">> Command canceled..");
                        // message 2
                        this.send(msg);
                        return;
                    }
                    existsCard = checkCardExists(marketBoard, msg);
                    if (!existsCard)
                        System.out.println(">> Please, choose a correct card.");
                    System.out.print(">> ");
                }
                cardLevel = Character.getNumericValue(msg.charAt(1));
                cardColorIndex = getColorIndex(msg.charAt(0));
                System.out.println("cardLevel :" + cardLevel +" color: " + getColor(msg.charAt(0)) );
                this.send("Buy(" + cardLevel + "," + cardColorIndex + ")");
                answer = this.receive();
            } while(answer.equals("KO"));

            //the card to buy exists
            System.out.println("Please, choose a position for the new card");
            msg = input.nextLine();
            if (msg.equals("esc")){
                System.out.println(">> Command canceled..");
                // message 2
                this.send(msg);
                return;
            }
            while (!(0 < Character.getNumericValue(msg.charAt(0)) && Character.getNumericValue(msg.charAt(0)) < 4)){
                System.out.println(">> Please choose a position between 1 and 3.");
                System.out.print(">> ");
                msg = input.nextLine();
            }
            this.send("position(" +  Character.getNumericValue(msg.charAt(0)) + ")");
            answer = this.receive();
            while (answer.equals("KO")) {

                System.out.println(">> It's not possible to append the new card at this position, please enter a new position..");
                System.out.print(">> ");
                msg = input.nextLine();
                if (msg.equals("esc")){
                    System.out.println(">> Command canceled..");
                    // message 2
                    this.send(msg);
                    return;
                }
                while (!(0 < Character.getNumericValue(msg.charAt(0)) && Character.getNumericValue(msg.charAt(0)) < 4)){
                    System.out.println(">> Please choose a position between 1 and 3.");
                    System.out.print(">> ");
                    msg = input.nextLine();
                }
                this.send("position(" +  Character.getNumericValue(msg.charAt(0)) + ")");
                answer = receive();
            }
        }
        else {
            System.out.println("Something went wrong..");
            return;
        }
        System.out.println("the card have been successfully bought!!");
        // TODO: update the development card stack
        DevelopmentCardStack stack = new DevelopmentCardStack();
        System.out.println("this is the development card stack on your board:" + stack);
    }
    public String checkCommand(String... acceptedCommands ) {

        Scanner input = new Scanner(System.in);
        System.out.print(">>");
        String command = input.nextLine();
        MessageParser mp = new MessageParser();
        mp.parse(command);
        while (!Arrays.asList(acceptedCommands).contains(mp.getOrder())) {
            System.out.println(">> unacceptable command, please try again..");
            System.out.print(">> ");
            command = input.nextLine();
            mp.parse(command);
        }
        return command;
    }

    public boolean checkCardExists (MarketBoard marketBoard, String command){
        // if the level is not correct
        if (!(0 < Character.getNumericValue(command.charAt(1)) && Character.getNumericValue(command.charAt(1)) < 4))
            return false;
        if (getColor(command.charAt(0)) == null)
            return false;
        else {

                //TODO: nella marketView per il cliente questo metodo deve considerare le carte di cui il giocatore non soddisfa i requisiti
            try {
                marketBoard.getDevelopmentCard(Character.getNumericValue(command.charAt(1)),getColor(command.charAt(0)));
            } catch (NoSuchDevelopmentCardException e) {
                return false;
            }

        }
        return true;
    }

    public Color getColor (char c){
        switch (c) {
            case 'G':
                return Color.GREEN;
            case 'B':
                return Color.BLUE;
            case 'Y':
                return Color.YELLOW;
            case 'P':
                return Color.PURPLE;
            default:
                return null;
        }
    }

    public int getColorIndex (char c){
        switch (c) {
            case 'G':
                return 0;
            case 'B':
                return 1;
            case 'Y':
                return 2;
            case 'P':
                return 3;
            default:
                return -1;
        }
    }
}
