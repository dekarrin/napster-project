/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client_peer;

/**
 *
 * @author joschutz
 */
public class Client_Peer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // accept ip and port of server, shared_directory from command line


            // connect to server at server-ip on server-port
            // create empty map for downloaded files
            // spawn (client_heartbeat_thread)
            // spawn (peer_server)
            // send list of files
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


            // (client_heartbeat_thread)
            // connect to UDP server
            // while connected:
                    // block for 60 seconds
                    // send heartbeat
            // close heartbeat connection
    }
    
}
