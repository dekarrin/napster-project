/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author dekarrin
 */
public class ClientHandler implements Runnable {
    
    private Socket socket;
    
    private int udpPort;
    
    private FileIndex index;
    
    private String clientIp;
    
    private volatile boolean running = true;
    
    public ClientHandler(Socket socket, int udpPort, FileIndex index)
    {
        this.index = index;
        this.socket = socket;
        this.udpPort = udpPort;
    }
    
    @Override
    public void run() {
        // add client to whatever list/map/structure is keeping track of clients
        clientIp = socket.getInetAddress().getHostAddress();
        index.addClient(clientIp);
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            OutputStreamWriter output = new OutputStreamWriter(socket.getOutputStream());
            output.write(udpPort + "\n");
            output.flush();
            // accept list of files from client
            waitForInput(input);
            int numFiles = Integer.parseInt(input.readLine());
            for (int i = 0; i < numFiles; i++) {
                waitForInput(input);
                String file = input.readLine();
                index.addFileToClient(file, clientIp);
                System.out.println("New client has file " + file);
            }
            while (running) {
                if (input.ready()) {
                    processClientCommand(input, output);
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    
                }
            }
        // while client is not dropped:
                // wait for input from client
                // if input is search:
                        // return a list of peers that have file
                // elif input is update:
                        // now peer has that file
        // close socket
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void halt() {
        running = false;
    }
    
    private void processClientCommand(BufferedReader input, OutputStreamWriter output) throws IOException
    {
        String command = input.readLine();
        System.out.println("Recieved command " + command + " from client");
        if (command.equals("Search")) {
	    waitForInput(input);
	    String filename = input.readLine();
	    Collection<String> clients = index.getClientsForFile(filename);
	    if (clients != null && clients.size() > 0) {
		StringBuilder builder = new StringBuilder();
		for (String c : clients) {
		    if (c.equals("127.0.0.1")) {
			c = InetAddress.getLocalHost().getHostAddress();
		    }
		    builder.append(c);
		    builder.append(",");
		}
		builder.deleteCharAt(builder.length() - 1);
		output.write(builder.toString() + "\n");
	    } else {
		output.write("\n");
	    }
	    output.flush();
	} else if (command.equals("Update")) {
	    waitForInput(input);
	    String updatedFilename = input.readLine();
	    index.addFileToClient(updatedFilename, clientIp);
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
