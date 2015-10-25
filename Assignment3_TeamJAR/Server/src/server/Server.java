/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

/**
 *
 * @author joschutz
 */
public class Server {

    public int port;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // accepts a port from the command line
        if (args.length < 1) {
            System.err.println("Must give port");
            System.exit(1);
        }
        try {
            int port = Integer.parseInt(args[0]);
            Server s = new Server(port);
            s.run();
        } catch (NumberFormatException e) {
            System.err.println("Port must be an integer");
        }
    }
    
    public Server(int port) {
        this.port = port;
    }
    
    public void run() {
        // open TCP socket on port, listen for connection from client
        // when client connects, spawn new thread to handle it and do (new_client_thread)


        // (new_client_thread):
        // spawn thread for heartbeat (heartbeat_thread)
        // add client to whatever list/map/structure is keeping track of clients
        // accept list of files from client
        // while client is not dropped:
                // wait for input from client
                // if input is search:
                        // return a list of peers that have file
                // elif input is update:
                        // now peer has that file
        // close socket


        // (heartbeat_thread):
        // create new UDP socket for heartbeat
        // establish connection with client
        // while client is not dropped:
                // check for message from client
                // if there is a message:
                        // reset time, loop
                // if not check time, if greater than 200 seconds:
                        // drop client
        // close socket
    }
    
}
