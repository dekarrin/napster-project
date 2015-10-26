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
public class HeartbeatHandler implements Runnable {
    
    private int port;
    
    private DatagramSocket socket;
    
    public static final int BUFFER_SIZE = 1024;
    
    private volatile boolean running = true;
    
    private ClientKiller killer;
    
    public HeartbeatHandler(int port, ClientKiller killer)
    {
        this.port = port;
        this.socket = socket;
        this.killer = killer;
    }
    
    @Override
    public void run() {
        System.out.println("ready to get heartbeats");
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.err.println("Fatal Error");
            e.printStackTrace();
            System.exit(1);
        }
        
        while (running) {
            receiveHeartbeats();
        }
        
        System.out.println("heartbeats thread closed");
    }
    
    public void halt() {
        running = false;
    }
    
    private void receiveHeartbeats() {
        System.out.println("Getting Heartbeats...");
        byte[] inBuffer = new byte[BUFFER_SIZE];
        DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);

        try {
            socket.receive(inPacket);
        } catch (IOException e) {
            System.err.println("Fatal Error");
            e.printStackTrace();
            System.exit(1);
        }

        byte[] input = inPacket.getData();

        if (input[0] == 'H' && input[1] == 'E' && input[2] == 'L' && input[3] == 'L' && input[4] == 'O') {
            InetAddress ip = inPacket.getAddress();
            System.out.println("Received heartbeat from " + ip.getHostAddress());
            if (killer.hasClient(ip.getHostAddress())) {
                killer.updateClientHeartbeat(ip.getHostAddress());
            }
        }
    }
}
