package client_peer;

import java.net.*;
import java.io.*;
import java.util.*;

public class PeerHandler extends Thread {
    
    private Socket socket;
    
    private int udpPort;
    
    private String shareLocation;
    private String fileName;
    
    public PeerHandler(Socket socket, int port, String folder){
	this.socket = socket;
	this.udpPort = port;
	this.shareLocation = folder;
    }
    
    public void run(){
	System.out.println("Started upload");
	try {
	    //input stream
	    //output stream
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    
	    DataOutputStream dOutStream = new DataOutputStream(socket.getOutputStream());
	    
	    //OutputStream outStream = socket.getOutputStream();
	    
            // accept file being searched for from peer
            waitForInput(input);
	    fileName = input.readLine();
	    
	    //this should make the path to the file
	    File dirLocation = new File(shareLocation, fileName);
	    
	    FileInputStream fileInStream = new FileInputStream(dirLocation);
	    
	    dOutStream.writeLong(dirLocation.length());
	    dOutStream.flush();
	    
	    byte[] byteArray = new byte[64];
	    
	    int endOfFile = fileInStream.read(byteArray);
	    
	    while (endOfFile != -1){
		dOutStream.write(byteArray, 0, endOfFile);
		dOutStream.flush();
		endOfFile = fileInStream.read(byteArray);
	    }
	    
	    socket.close();
	    
	} catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //wait fo input so we don't close reader prematurely
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
    
    private BufferedWriter createSocketWriter() {
	BufferedWriter out = null;
	try {
	    out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return out;
    }
    
}
