/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client_peer;

import java.io.*;
import java.util.*;
import java.net.*;

/**
 *
 * @author dekarrin
 */
public class PeerDownloader extends Thread {
    
    private int port;
    
    private String sharedDir;
    
    private String ip;
    
    private Map<String, Boolean> downloadedFiles;
    
    private String filename;
    
    private Socket socket;
    
    private int BUFFER_SIZE = 64;
    
    private volatile boolean running = true;
    
    public PeerDownloader(String peerIp, int peerPort, String sharedDir, String filename, Map<String, Boolean> downloadedFiles) {
        port = peerPort;
        ip = peerIp;
        this.downloadedFiles = downloadedFiles;
        this.filename = filename;
	this.sharedDir = sharedDir;
    }
    
    @Override
    public void run() {
	downloadedFiles.put(filename, false);
	createSocket();
	BufferedWriter output = createSocketWriter();
	InputStream input = createSocketReader();
	try {
	    long numBytes = getFileSize(input, output);
	    if (numBytes > 0) {
		downloadFileData(input, output, numBytes);
		downloadedFiles.put(filename, true);
	    }
	} catch (IOException e) {
	    System.err.println("Error while downloading '" + filename + "' from " + ip + ":");
	    e.printStackTrace();
	}
	try {
	    socket.close();
	} catch (IOException e) {
	    e.printStackTrace();
	    // but otherwise, this should be okay
	}
    }
    
    public void halt() {
        running = false;
    }
    
    private long getFileSize(InputStream socketIn, BufferedWriter socketOut) throws IOException {
	socketOut.write(filename + "\n");
	socketOut.flush();
	waitForInput(socketIn);
	long size = Long.parseLong(socketIn.readLine());
	return size;
    }
    
    private void downloadFileData(BufferedReader socketIn, BufferedWriter socketOut, long fileSize) throws IOException {
	File dir = new File(sharedDir);
	File downloadFile = new File(dir, filename);
	BufferedWriter fileOut = new BufferedWriter(new FileWriter(downloadFile));
	long readBytes = 0L;
	
	while (readBytes < fileSize) {
	    socketIn.read 
	}
    }
    
    private void createSocket() {
	try {
	    socket = new Socket(ip, port);
	} catch (UnknownHostException e) {
	    System.err.println("Error connecting to peer '" + ip + "': unknown host");
	    running = false;
	} catch (IOException e) {
	    System.err.println("Error connecting to peer '" + ip + "': could not create socket");
	    running = false;
	}
    }
    
    private BufferedWriter createSocketWriter() {
	BufferedWriter out = null;
	try {
	    out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	} catch (IOException e) {
	    System.err.println("Error connecting to peer '" + ip + "': could not get output stream");
	    running = false;
	}
	return out;
    }
    
    private InputStream createSocketReader() {
	InputStream in = null;
	try {
	    in = socket.getInputStream();
	} catch (IOException e) {
	    System.err.println("Error connecting to peer '" + ip + "': could not get input stream");
	    running = false;
	}
	return in;
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
    
    public Map<String, Boolean> getDownloadedFiles(){
	return downloadedFiles;
    }
}
