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
	try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter output = createSocketWriter();
	    
            // accept file being searched for from peer
            waitForInput(input);
	    fileName = input.readLine();
	    
	    //this should make the path to the file
	    File dirLocation = new File(shareLocation, fileName);
	    
	    output.write(dirLocation.length()+"\n");
	    output.flush();
	    
	    //read in the file the is do be downloaded
	    BufferedReader fileBufRead = new BufferedReader(new FileReader(dirLocation));
	    
	    while (fileBufRead.ready()){
		//send out file one line at a time
		output.write(fileBufRead.readLine()+"\n");
		output.flush();
	    }
	    
	    fileBufRead.close();
	    socket.close();
	    
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
