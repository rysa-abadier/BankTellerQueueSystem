package com.bankteller.customer;

public class Customer {
    private String name;
    private int accountNum;

    public Customer(String name, int accountNum) {
        this.name = name;
        this.accountNum = accountNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(int accountNum) {
        this.accountNum = accountNum;
    }
}
