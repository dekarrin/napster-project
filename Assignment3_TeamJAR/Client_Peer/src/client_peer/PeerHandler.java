package client_peer;

import java.net.*;
import java.io.*;
import java.util.*;

public class PeerHandler extends Thread {
    
    private Socket socket;
    private int udpPort;
    
    public PeerHandler(Socket socket, int port){
	this.socket = socket;
	this.udpPort = port;
    }
    
    public void run(){
	try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            OutputStreamWriter output = new OutputStreamWriter(socket.getOutputStream());
	    
            // accept file being searched for from peer and location of file
            waitForInput(input);
            
	    
            while (running) {
                if (input.ready()) {
                    processClientCommand(input, output);
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    
                }
            }
	} catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void waitForInput(BufferedReader input) {
        try {
            while (!input.ready()) {
                Thread.sleep(10);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
    
}
