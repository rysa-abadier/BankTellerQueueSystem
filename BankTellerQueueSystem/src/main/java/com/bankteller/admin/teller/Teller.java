/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tellermanagement;

/**
 *
 * @author Mariah
 */
public class Teller {
    private int id;
    private String firstName;
    private String lastName;
    private int serviceTypeId;

    public Teller(int id, String firstName, String lastName, int serviceTypeId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.serviceTypeId = serviceTypeId;
    }

    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public int getServiceTypeId() { return serviceTypeId; }

    public String getServiceTypeName() {
        switch (serviceTypeId) {
            case 1: return "Deposit";
            case 2: return "Bills Payment";
            case 3: return "Withdrawal";
            case 4: return "Loan Inquiry";
            case 5: return "Account Opening";
            default: return "Unknown";
        }
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Service: " + getServiceTypeName() +
               " | First Name: " + firstName + " | Last Name: " + lastName;
    }
}

