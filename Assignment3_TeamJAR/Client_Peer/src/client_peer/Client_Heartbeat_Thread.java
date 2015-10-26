package client_peer;

import java.net.*;
import java.util.*;
import java.io.*;


public class Client_Heartbeat_Thread extends Thread{
    
    private volatile boolean running = true;
    
    private InetAddress serverIP;
    private int heartPort;
    
    public Client_Heartbeat_Thread(InetAddress ia, int port){
	serverIP = ia;
	heartPort = port;
    }
    
    public void run(){
	
	DatagramSocket socket = null;
        byte[] message = {'H','E','L','L','O'};
	
	// connect to UDP server
	try{
	    socket = new DatagramSocket();
	}catch(Exception e){
	    e.printStackTrace();
	    System.exit(1);
	}
	
	DatagramPacket sendPacket = new DatagramPacket(message,message.length,serverIP,heartPort);
	
	
	// while connected:
	while (running){
	    try{
		// send heartbeat
		socket.send(sendPacket);
		// block for 60 seconds
		Thread.sleep(60000);
	    }catch (Exception e){
		e.printStackTrace();
		System.exit(1);
	    }
	}
	
	// close heartbeat connection
	socket.close();
    }
    
    public void halt(){
	running = false;
    }
    
}
