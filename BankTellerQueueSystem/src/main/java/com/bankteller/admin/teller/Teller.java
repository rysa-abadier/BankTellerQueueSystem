package com.bankteller.admin.teller;

import com.bankteller.admin.service.Service;

public class Teller {
    private int id;
    private String name;
    private Service serviceType;
    private int transactionsHandled;
    
    public Teller() {
        id = 1;
        name = "Jane Doe";
    }

    public Teller(int id, String name) {
        this.id = id;
        this.name = name;
        this.serviceType = serviceType;
        this.transactionsHandled = transactionsHandled;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public Service getServiceType() { return serviceType; }
    public int getTransactionsHandled() { return transactionsHandled; }

    public void setName(String name) { this.name = name; }
    public void setServiceType(Service serviceType) { this.serviceType = serviceType; }
    public void incrementTransactions() { transactionsHandled++; }

    @Override
    public String toString() {
        return "ID: " + id +
               " | Name: " + name +
               " | Service: " + (serviceType != null ? serviceType.getName() : "Unassigned") +
               " | Handled: " + transactionsHandled;
    }

}
