package it.polimi.ingsw;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
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
            client.messageOut.println(msg);
            client.messageOut.flush();

            String answer = client.messageIn.nextLine();
            System.out.println(answer);
        }

        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
