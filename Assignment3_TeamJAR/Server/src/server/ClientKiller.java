/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;
import java.util.*;

/**
 *
 * @author dekarrin
 */
public class ClientKiller implements Runnable {
    
    private Server server;
    
    private Map<String, Long> heartbeatTimes;
    
    public ClientKiller(Server server) {
        this.server = new Server;
    }
}
