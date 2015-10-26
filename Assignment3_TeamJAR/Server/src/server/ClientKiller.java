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
    
    private volatile boolean running = true;
    
    private Map<String, Long> heartbeatTimes;
    
    private long timeout;
    
    public ClientKiller(Server server, long timeout) {
        this.server = server;
        heartbeatTimes = Collections.synchronizedMap(new HashMap<String, Long>());
        this.timeout = timeout;
    }
    
    @Override
    public void run() {
        try {
            while (running) {
                long time = System.currentTimeMillis();
                for (String client : heartbeatTimes.keySet()) {
                    if (time - heartbeatTimes.get(client) > timeout) {
                        System.out.println("No heartbeat from client " + client + "; connection timed out");
                        server.drop(client);
                    }
                }
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            // do nothing; this is okay
        }
    }
    
    public void removeClient(String client) {
        heartbeatTimes.remove(client);
    }
    
    public void updateClientHeartbeat(String client) {
        heartbeatTimes.put(client, System.currentTimeMillis());
    }
    
    public void halt() {
        running = false;
    }

    boolean hasClient(String hostAddress) {
        return heartbeatTimes.containsKey(hostAddress);
    }
}
