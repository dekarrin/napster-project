/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.net.Socket;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author dekarrin
 */
public class ServerThread implements Runnable {
    
    private Socket socket;
    
    private int udpPort;
    
    public ServerThread(Socket socket, int udpPort)
    {
        this.socket = socket;
        this.udpPort = udpPort;
    }
    
    @Override
    public void run() {
        try {
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            output.writeInt(udpPort);
            output.writeUTF("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
