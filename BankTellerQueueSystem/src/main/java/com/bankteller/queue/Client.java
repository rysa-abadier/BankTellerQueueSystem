package com.bankteller.queue;

import com.bankteller.service.*;
import com.bankteller.teller.*;

public class Client {
    private int accNum;
    private String name;
//    private Service service;
    private boolean emergency;
    private Status status;
    private Teller teller;

    public Client() {
        accNum = 1;
        name = "John Doe";
        emergency = false;
    }
    
    public Client(int accNum, String name, boolean emergency) {
        this.accNum = accNum;
        this.name = name;
        this.emergency = emergency;
    }

    public int getAccNum() {
        return accNum;
    }

    public void setAccNum(int accNum) {
        this.accNum = accNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEmergency() {
        return emergency;
    }

    public void setEmergency(boolean emergency) {
        this.emergency = emergency;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Teller getTeller() {
        return teller;
    }

    public void setTeller(Teller teller) {
        this.teller = teller;
    }
    
    public String printDetails() {
        StringBuilder sb = new StringBuilder("Client Details:\n");
        sb.append("\tAccount Number: ").append(accNum).append("\n");
        sb.append("\tName: ").append(name).append("\n");
        
        return sb.toString();
    }
}
