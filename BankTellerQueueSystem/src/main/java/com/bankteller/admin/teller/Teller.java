/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bankteller.admin.teller;

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

