package it.polimi.ingsw;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server
{
    private final int port = 2703;

    public void startServer() throws IOException
    {
        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket server = new ServerSocket(this.port);
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
            server.startServer();
        } catch (IOException e) {
            System.out.println("(SERVER) >> Unable to start server...");
        }
    }
}

