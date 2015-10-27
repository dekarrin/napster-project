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
     private Map<String, Boolean> downloadedFiles;
     private BufferedReader serverIn;
     
     private OutputStreamWriter serverOut;

     public Client_Peer(String ip, int port, String path){
	 serverIP = ip;
	 serverPort = port;
	 folderPath = path;
	 downloadedFiles = Collections.synchronizedMap(new HashMap<String, Boolean>());
	 try {
		serverOut = new OutputStreamWriter(socket.getOutputStream());
		serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	 } catch (IOException e) {
	     System.err.println("FLAGRANT SYSTEM ERROR. Computer over. Virus = very yes.");
	 }
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
	  
	 int heartPort = -1;
	 
	 try{
	     
	     while (!serverIn.ready()){
		 Thread.sleep(10);
	     }
	     String tempStr = serverIn.readLine();
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
	 
	     // send list of files
	     serverOut.write(fList.length + "");
	     serverOut.write("\n");
	     serverOut.flush();
	     for(int i = 0; i<fList.length; i++){
	         serverOut.write(fList[i].getName());
		 serverOut.write("\n");
		 serverOut.flush();
	     }
	 
	 
	     while(socket.isConnected()){
		 
		 System.out.println("Download, Status, Exit");
		 
		 String userInput = scan.nextLine();
		 
		 this.checkConnection();
		 
		 if(userInput.equalsIgnoreCase("download")){
		     downloadFromServer();
		     
		 }else if (userInput.equalsIgnoreCase("status")){
		     Map<String, Boolean> downloadStatus = pd.getDownloadedFiles();
		     System.out.println("File_Name\n\tStatus\n");
		     for(Map.Entry<String, Boolean> entry : downloadStatus.entrySet()){
			 System.out.println(entry.getKey());
			 System.out.println("\t" + boolToStatus(entry.getValue()) + "\n");
		     }
		 }else if (userInput.equalsIgnoreCase("exit")){
		     System.exit(1);
		 }
		 
	    }
	     
	     cht.halt();
	     pd.halt();
	     ps.end();
	     
	     serverIn.close();
	     serverOut.close();
	     socket.close();
	 
	 
	 }catch (Exception e){
	     e.printStackTrace();
	     System.exit(1);
	 }
	
     }
     
     private synchronized void downloadFromServer() throws IOException, InterruptedException {
	System.out.println("What is the name of the file you are searching for?");
	String searchFile = scan.nextLine();
	this.checkConnection();
	serverOut.write("Search\n");
	serverOut.flush();
	this.checkConnection();
	serverOut.write(searchFile);
	serverOut.write("\n");
	serverOut.flush();
	while(!serverIn.ready()){
	    Thread.sleep(10);
	    this.checkConnection();
	}
	String ips = serverIn.readLine();
	if(ips.equals("")){
	    System.out.println("These are not the droids you are looking for.");
	}else{
	   String[] ipList = ips.split(",");
	   for (int i = 0; i < ipList.length; i++) {
	       if (ipList[i].startsWith("127.")) {
		   ipList[i] = serverIP;
	       }
	   }
	   this.checkConnection();
	   System.out.println("Select the number of the IP you wish to download from.");
	   for(int i = 0; i<ipList.length; i++){
	       System.out.println(i + " => " + ipList[i]);
	   }
	   boolean validInput = false;
	   int userInt = -1;
	   while (!validInput){
	       String input = scan.nextLine();
	       this.checkConnection();

	       try{
		   userInt = Integer.parseInt(input);
		   if (userInt >= -1 && userInt < ipList.length){
		       validInput = true;
		   }else{
		       System.out.println("Please only type the number next to the IP address you wish to download from or -1 to exit.");
		   }
	       }catch(Exception e){
		   System.out.println("Please only type the number next to the IP address you wish to download from or -1 to exit.");
	       }
	   }

	   if(userInt != -1){
	       pd = new PeerDownloader(ipList[userInt],peerPort,folderPath,searchFile,downloadedFiles,this);
	       pd.start();
	   }

	}
     }
     
     public synchronized void updateServer(String file) {
	 try {
		serverOut.write("Update\n");
		serverOut.write(file + "\n");
	 } catch (IOException e) {
	     System.err.println("Could not send update");
	 }
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
     
     private String boolToStatus(Boolean status){
	 if(status){
	     return "Complete";
	 }else{
	     return "Downloading";
	 }
	 
     }
     
}
