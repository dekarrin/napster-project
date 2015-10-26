package client_peer;

import java.net.*;
import java.util.*;
import java.io.*;

public class Client_Peer {

     static void main(String[] args) {
	 
        // accept ip and port of server, shared_directory from command line
	 if (args.length < 3){
	     System.err.println("Must have server IP, server port, and folder of shared files.");
	     System.exit(1);
	 }
	 try{
	     String serverIP = args[0];
	     int serverPort = Integer.parseInt(args[1]);
	     String filePath = args[2];
	     Client_Peer clpr = new Client_Peer(serverIP, serverPort, filePath);
	     clpr.run();	
	 } catch (Exception e){
	     System.err.println(e);
	 }
     }
     
     private String serverIP;
     private int serverPort;
     private String folderPath;
     final private static int peerPort = 4004;

     public Client_Peer(String ip, int port, String path){
	 serverIP = ip;
	 serverPort = port;
	 folderPath = path;
     }
	 
     public void run() {
	 
	 Socket socket = null;
	 
	 // connect to server at server-ip on server-port
	 try{
	    socket = new Socket(InetAddress.getByName(serverIP), serverPort);
	 }catch(Exception e){
	     e.printStackTrace();
	     System.exit(1);
	 } 
	 
	 // create empty map for downloaded files
	 Map<String, Boolean> downloadedFiles = new HashMap<>();
	 
	 BufferedReader in = null;
	 int heartPort = -1;
	 
	 try{
	     in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	     heartPort = Integer.parseInt(in.readLine());
	 }catch (Exception e){
	     e.printStackTrace();
	     System.exit(1);
	 }
	 
	 // spawn (client_heartbeat_thread)
	 try{
	    new Client_Heartbeat_Thread(InetAddress.getByName(serverIP), heartPort).start();
	 }catch (Exception e){
	     e.printStackTrace();
	     System.exit(1);
	 }	 
	 
        
        
        // spawn (peer_server)
        
	 
	 try{
	     File directory = new File(folderPath);
	     File[] fList = directory.listFiles();
	 
	     DataOutputStream out = new DataOutputStream(socket.getOutputStream());
	 
	     // send list of files
	     out.write(fList.length);
	     out.writeBytes("\n");
	     for(int i = 0; i<fList.length; i++){
	         out.writeBytes(fList[i].getName());
		 out.writeBytes("\n");
	     }
	 
	 
	     while(socket.isConnected()){
		 
		 System.out.println("Download, Status, Exit");
		 Scanner scan = new Scanner(System.in);
		 
		 String userInput = scan.nextLine();
		 
		 if(userInput.equalsIgnoreCase("download")){
		     System.out.println("What is the name of the file you are searching for?");
		     String searchFile = scan.nextLine();
		     out.writeBytes("Search\n");
		     out.writeBytes(searchFile);
		     out.writeBytes("\n");
		     while(!in.ready()){
			 Thread.sleep(10);
		     }
		     String ips = in.readLine();
		     if(ips.equals("")){
			 System.out.println("The file you are searching for doesn't exist.");
		     }else{
			String[] ipList = ips.split(",");
			System.out.println("Select the number of the IP you wish to download from.");
			for(int i = 0; i<ipList.length; i++){
			    System.out.println(i + " => " + ipList[i]);
			}
			userInput = scan.nextLine();
			try{
			    int userInt = Integer.parseInt(userInput);
			    new Download_Thread(InetAddress.getByName(ipList[userInt]), peerPort, searchFile).start();
			}catch(Exception e){
			    e.printStackTrace();
			}
		     }
		 }else if (userInput.equalsIgnoreCase("status")){
		     //something
		 }else if (userInput.equalsIgnoreCase("exit")){
		     break;
		 }
		 
	    }
	     
	     in.close();
	     out.close();
	     socket.close();
	 
	 
	 }catch (Exception e){
	     e.printStackTrace();
	     System.exit(1);
	 }
	
        // while connected:
                // get user input
                // if user input is download:
                        // prompt for file to download
                        // send search to server
                        // if search contains peers:
                                // prompt for peer
                                // spawn thread (download_from_peer)
                // if user input is status:
                        // display whether that file is downloaded
                // if user input is exit:
                        // connected is false
        // close server connection
     }

            // (peer_server)
            // create TCP socket
            // while true:
                    // accept connection
                    // spawn (upload_to_peer)
                    // close connection


            // (upload_to_peer)
            // open file
            // transmit file


            // (download_from_peer)
            // add file to downloaded files map, set to false
            // open tcp connection to peer
            // download file
            // close tcp connection to peer
            // tell server to update file list
            // set file to true in downloaded files


    }
    
}
