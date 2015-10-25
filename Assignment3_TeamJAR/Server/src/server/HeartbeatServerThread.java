/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.net.*;

/**
 *
 * @author dekarrin
 */
public class HeartbeatServerThread implements Runnable {
    
    private int port;
    
    private DatagramSocket socket;
    
    public static final int BUFFER_SIZE = 1024;
    
    private Server server;
    
    public HeartbeatServerThread(int port, Server server)
    {
        this.port = port;
        this.socket = socket;
        this.server = server;
    }
    
    @Override
    public void run() {
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.err.println("Fatal Error");
            e.printStackTrace();
            System.exit(1);
        }
        
        while (true) {
            receiveHeartbeats();
        }
    }
    
    private void receiveHeartbeats() {
        byte[] inBuffer = new byte[BUFFER_SIZE];
        DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);

        try {
            socket.receive(inPacket);
        } catch (IOException e) {
            System.err.println("Fatal Error");
            e.printStackTrace();
            System.exit(1);
        }

        String input = new String(inPacket.getData());

        if (input.equals("HELLO")) {
            InetAddress ip = inPacket.getAddress();
            server.refreshPeer(ip.getHostAddress());
        }
    }
}
