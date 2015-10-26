
package server;


import java.net.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author joschutz
 */
public class Server {

    private int port;
    
    private int nextDatagramPort;
    
    private ServerSocket incomingSocket;
    
    private ClientKiller killer;
    
    private Map<String, ClientHandler> connections;
    
    private FileIndex index;
    
    public static final long CLIENT_TIMEOUT = 200 * 1000;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // accepts a port from the command line
        if (args.length < 1) {
            System.err.println("Must give port");
            System.exit(1);
        }
        try {
            int port = Integer.parseInt(args[0]);
            Server s = new Server(port);
            s.run();
        } catch (NumberFormatException e) {
            System.err.println("Port must be an integer");
        }
    }
    
    public Server(int port) {
        this.port = port;
        killer = new ClientKiller(this, CLIENT_TIMEOUT);
        connections = Collections.synchronizedMap(new HashMap<String, ClientHandler>());
        index = new FileIndex();
    }
    
    public void run() {
        try {
            (new Thread(killer)).start();
            (new Thread(new HeartbeatHandler(port, killer))).start();
            incomingSocket = new ServerSocket(port);
            listenForConnections();
        } catch (IOException e) {
            System.err.println("Error: could not open socket");
        }
    }
    
    public void drop(String clientIp) {
        connections.get(clientIp).halt();
        connections.remove(clientIp);
        index.removeClient(clientIp);
        killer.removeClient(clientIp);
    }
    
    private void listenForConnections() throws IOException {
        while (true) {
            Socket connection = incomingSocket.accept();
            System.out.println("Accepted connection");
            String clientIp = connection.getInetAddress().getHostAddress();
            killer.updateClientHeartbeat(clientIp);
            ClientHandler handler = new ClientHandler(connection, port, index);
            connections.put(clientIp, handler);
            (new Thread(handler)).start();
        }
    }
        // open TCP socket on port, listen for connection from client
        // when client connects, spawn new thread to handle it and do (new_client_thread)


        


    
}
