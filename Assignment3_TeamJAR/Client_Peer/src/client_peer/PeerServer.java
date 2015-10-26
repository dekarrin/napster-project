package client_peer;

import java.net.*;
import java.io.*;
import java.util.*;

public class PeerServer extends Thread {
    
    private int listenPort = -1;
    private volatile boolean running = true;
    
    private ServerSocket incomingSocket;
    
    private String shareLocation;
    
    public PeerServer(int port, String folderPath){
	listenPort = port;
	shareLocation = folderPath;
    }
    
    public void run(){
	try {
            incomingSocket = new ServerSocket(listenPort);
            listenForConnections();
        } catch (IOException e) {
            System.err.println("Error: could not open socket");
        }
    }
    
    public void end(){
	running = false;
    }
    
    private void listenForConnections() throws IOException {
        while (running) {
            Socket connection = incomingSocket.accept();
            System.out.println("Accepted connection");
            String clientIp = connection.getInetAddress().getHostAddress();
            PeerHandler handler = new PeerHandler(connection, listenPort, shareLocation);
            (new Thread(handler)).start();
        }
    }
    
}
