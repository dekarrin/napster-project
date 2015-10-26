/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.net.Socket;
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
    
    private volatile boolean running = false;
    
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
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            output.writeInt(udpPort);
            output.writeUTF("\n");
            // accept list of files from client
            waitForInput(input);
            int numFiles = Integer.parseInt(input.readLine());
            for (int i = 0; i < numFiles; i++) {
                waitForInput(input);
                index.addFileToClient(input.readLine(), clientIp);
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
    
    private void processClientCommand(BufferedReader input, DataOutputStream output) throws IOException
    {
        String command = input.readLine();
        switch (command) {
            case "Search":
                waitForInput(input);
                String filename = input.readLine();
                Collection<String> clients = index.getClientsForFile(filename);
                if (clients.size() > 0) {
                    StringBuilder builder = new StringBuilder();
                    for (String c : clients) {
                        builder.append(c);
                        builder.append(",");
                    }
                    builder.deleteCharAt(builder.length() - 1);
                    output.writeUTF(builder.toString() + "\n");
                } else {
                    // write empty string
                    output.writeUTF("\n");
                }
                break;

            case "Update":
                waitForInput(input);
                String updatedFilename = input.readLine();
                index.addFileToClient(updatedFilename, clientIp);
                break;

        }
    }
    
    private void waitForInput(BufferedReader input) {
        try {
            while (!input.ready()) {
                Thread.sleep(10);
            }
        } catch (Exception e) {
            return;
        }
    }
}
