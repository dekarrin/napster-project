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
public class FileIndex {
    
    private Map<String, Set<String>> clients;
    
    private Map<String, Set<String>> files;
    
    public FileIndex() {
        clients = Collections.synchronizedMap(new HashMap<String, Set<String>>());
        files = Collections.synchronizedMap(new HashMap<String, Set<String>>());
    }
    
    public void addClient(String name) {
        clients.put(name, new HashSet<String>());
    }
    
    public void addFile(String name) {
        files.put(name, new HashSet<String>());
    }
    
    public void addFileToClient(String file, String client) {
        if (!clients.containsKey(client)) {
            addClient(client);
        }
        clients.get(client).add(file);
        if (!files.containsKey(file)) {
            addFile(file);
        }
        if (!files.get(file).contains(client)) {        
            files.get(file).add(client);
        }
    }
    
    public void addClientToFile(String file, String client) {
        if (!files.containsKey(file)) {
            addFile(file);
        }
        files.get(file).add(client);
        if (!clients.containsKey(client)) {
            addClient(client);
        }
        if (!clients.get(client).contains(file)) {
            clients.get(client).add(file);
        }
    }
    
    public void removeClient(String client) {
        if (clients.containsKey(client)) {
            Set<String> clientFiles = clients.get(client);
            Set<String> delFiles = new HashSet<>();
            for (String f : clientFiles) {
                Set<String> fSet = files.get(f);
                fSet.remove(client);
                if (fSet.isEmpty()) {
                    files.remove(f);
                }
            }
        }
    }
}
