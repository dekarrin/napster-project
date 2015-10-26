package client_peer;

import java.net.*;
import java.util.*;
import java.io.*;

public class Client_Peer {

     public static void main(String[] args) {
	 
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
     
     private Client_Heartbeat_Thread cht;
     private PeerServer ps;
     private PeerDownloader pd;
     
     Socket socket = null;
     
     Scanner scan = new Scanner(System.in);
     
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
	 
	 // connect to server at server-ip on server-port
	 try{
	    socket = new Socket(InetAddress.getByName(serverIP), serverPort);
	 }catch(Exception e){
	     e.printStackTrace();
	     System.exit(1);
	 } 
	 
	 // create empty map for downloaded files
	 Map<String, Boolean> downloadedFiles = Collections.synchronizedMap(new HashMap<String, Boolean>());
	 
	 BufferedReader in = null;
	 int heartPort = -1;
	 
	 try{
	     in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	     while (!in.ready()){
		 Thread.sleep(10);
	     }
	     String tempStr = in.readLine();
	     heartPort = Integer.parseInt(tempStr);
	 }catch (Exception e){
	     e.printStackTrace();
	     System.exit(1);
	 }
	 
	 // spawn (client_heartbeat_thread)
	 try{
	    cht = new Client_Heartbeat_Thread(InetAddress.getByName(serverIP), heartPort);
	    cht.start();
	 }catch (Exception e){
	     e.printStackTrace();
	     System.exit(1);
	 }	 
	 
        ps = new PeerServer(peerPort,folderPath);
	ps.start();
        
	 
	 try{
	     File directory = new File(folderPath);
	     File[] fList = directory.listFiles();
	 
	     OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
	 
	     // send list of files
	     out.write(fList.length + "");
	     out.write("\n");
	     out.flush();
	     for(int i = 0; i<fList.length; i++){
	         out.write(fList[i].getName());
		 out.write("\n");
		 out.flush();
	     }
	 
	 
	     while(socket.isConnected()){
		 
		 System.out.println("Download, Status, Exit");
		 
		 String userInput = scan.nextLine();
		 
		 this.checkConnection();
		 
		 if(userInput.equalsIgnoreCase("download")){
		     System.out.println("What is the name of the file you are searching for?");
		     String searchFile = scan.nextLine();
		     this.checkConnection();
		     out.write("Search\n");
		     out.flush();
		     this.checkConnection();
		     out.write(searchFile);
		     out.write("\n");
		     out.flush();
		     while(!in.ready()){
			 Thread.sleep(10);
			 this.checkConnection();
		     }
		     String ips = in.readLine();
		     if(ips.equals("")){
			 System.out.println("These are not the droids you are looking for.");
		     }else{
			String[] ipList = ips.split(",");
			this.checkConnection();
			System.out.println("Select the number of the IP you wish to download from.");
			for(int i = 0; i<ipList.length; i++){
			    System.out.println(i + " => " + ipList[i]);
			}
			userInput = scan.nextLine();
			this.checkConnection();
			int userInt = -1;
			try{
			    userInt = Integer.parseInt(userInput);
			}catch(Exception e){
			    System.out.println("Please only type the number next to the IP address you wish to download from.");
			}
			
			pd = new PeerDownloader(ipList[userInt],peerPort,folderPath,searchFile,downloadedFiles);
		     }
		     
		 }else if (userInput.equalsIgnoreCase("status")){
		     
		 }else if (userInput.equalsIgnoreCase("exit")){
		     break;
		 }
		 
	    }
	     
	     cht.halt();
	     pd.halt();
	     ps.end();
	     
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
     
     public void checkConnection(){
	 if (!socket.isConnected()){
	     System.out.println("Connection to server lost. Closing program.");
	     cht.halt();
	     pd.halt();
	     ps.end();
	     System.exit(1);
	 }
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


           

    
}
