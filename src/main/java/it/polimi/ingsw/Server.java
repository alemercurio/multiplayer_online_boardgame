package it.polimi.ingsw;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server
{
    public void startServer(int port) throws IOException
    {
        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket server = new ServerSocket(port);
        int playerCount = 0;

        System.out.println("(SERVER) >> Server Ready");
        while(true)
        {
            Socket client = server.accept();
            System.out.println("(SERVER) >> new Player with ID = " + playerCount);
            executor.submit(new Player(playerCount,client));
            playerCount = playerCount + 1;
        }
    }

    public static void main(String[] args)
    {
        Server server = new Server();
        try {
            server.startServer(2703);
        } catch (IOException e) {
            System.out.println("(SERVER) >> Unable to start server...");
        }
    }
}

