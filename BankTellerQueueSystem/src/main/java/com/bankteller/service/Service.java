package com.bankteller.service;

import com.bankteller.queue.*;

public class Service {
    public DBConnetion db = new DBConnetion();
    
    private int id;
    private String name;
    private int priority;
    private int avgServiceTime;

    public Service(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getAvgServiceTime() {
        return avgServiceTime;
    }

    public void setAvgServiceTime(int avgServiceTime) {
        this.avgServiceTime = avgServiceTime;
    }
    
    @Override
    public String toString() {
        return "Service ID: " + id + " | Service: " + name + " | Priority: " + priority + " | Avg Service Time: " + avgServiceTime + " minutes";
    }
}
