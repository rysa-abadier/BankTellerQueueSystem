package com.bankteller.admin.queue;

import com.bankteller.admin.teller.Teller;
import java.util.*;

public class QueueManager {
    private Queue<Client> normal = new LinkedList<>();
    private Queue<Client> emergency = new LinkedList<>();
    private Queue<Client> waiting = new LinkedList<>();
    private List<Client> active = new ArrayList<>();
    private Queue<Client> reassigned = new LinkedList<>();
    private List<Client> completed = new ArrayList<>();
    
    public void addClient(Teller t, Client c) {
        c.setTeller(t);
        
        if (c.isEmergency()) {
            emergency.add(c);
        } else {
            normal.add(c);
        }
        
        c.setStatus(Status.Queued);
    }
    
    public Client nextClient() {
        Client c = !emergency.isEmpty() ? emergency.poll() : normal.poll();
        
        if (c != null) {
            active.add(c);
            c.setStatus(Status.Active);
        }
        
        return c;
    }
    
    public void skipClient(Client c) {
        active.remove(c);
        normal.offer(c);
        c.setStatus(Status.Skipped);
    }
    
    public void reassignClient(Teller t, Client c) {
        active.remove(c);
        c.setTeller(t);
        c.setStatus(Status.Reassigned);
        reassigned.add(c);
    }
    
    public void completeService(Client c) {
        active.remove(c);
        completed.add(c);
        c.setStatus(Status.Completed);
    }

    public Queue<Client> getWaiting() {
        waiting.addAll(emergency);
        waiting.addAll(reassigned);
        waiting.addAll(normal);
        return waiting;
    }

    public List<Client> getActive() {
        return active;
    }

    public List<Client> getCompleted() {
        return completed;
    }
}
