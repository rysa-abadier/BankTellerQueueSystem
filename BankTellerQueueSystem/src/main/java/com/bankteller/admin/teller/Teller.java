package com.example.tellermanagement;

public class Teller {
    private int id;
    private String name;
    private String serviceType;

    public Teller(int id, String name, String serviceType) {
        this.id = id;
        this.name = name;
        this.serviceType = serviceType;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getServiceType() { return serviceType; }
}

