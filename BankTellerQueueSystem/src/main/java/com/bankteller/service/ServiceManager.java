package com.bankteller.service;

import java.util.ArrayList;

public class ServiceManager {
    private ArrayList<Service> services;

    public ServiceManager() {
        services = new ArrayList<>();
        
        // temp list since wla pang db
        services.add(new Service(1, "Deposit"));
        services.add(new Service(2, "Withdrawal"));
        services.add(new Service(3, "Loan Inquiry"));
        services.add(new Service(4, "Account Opening"));
        services.add(new Service(5, "Bills Payment"));
    }

    public ArrayList<Service> getAllServices() {
        return services;
    }
    
    public Service getById(int id) {
        for (Service s : services) {
            if (s.getId() == id) return s;
        }
        return null;
    }

    public void addService(Service s) {
        services.add(s);
    }

    public void removeService(int id) {
        services.removeIf(s -> s.getId() == id);
    }
    
    public void updateService(Service updated) {
        for (int i = 0; i < services.size(); i++) {
            if (services.get(i).getId() == updated.getId()) {
                services.set(i, updated);
                break;
            }
        }
    }
    
}
