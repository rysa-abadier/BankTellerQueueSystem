package com.bankteller.admin.service;

public class Service {
    private int id;
    private String name;
    private String priority;
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public int getAvgServiceTime() {
        return avgServiceTime;
    }

    public void setAvgServiceTime(int avgServiceTime) {
        this.avgServiceTime = avgServiceTime;
    }
}
