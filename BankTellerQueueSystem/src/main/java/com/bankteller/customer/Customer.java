package com.bankteller.customer;

import java.time.*;

public class Customer {
    private int id;
    private int tellerId;
    private int serviceId;
    private String queueNo;
    private String name;
    private String status;
    private boolean emergency;
    private LocalDateTime transactionDate;
    private LocalTime startTime;
    private LocalTime endTime;

    public Customer(int id, int tellerId, int serviceId, String queueNo, String name, String status, boolean emergency, LocalDateTime transactionDate, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.tellerId = tellerId;
        this.serviceId = serviceId;
        this.queueNo = queueNo;
        this.name = name;
        this.status = status;
        this.emergency = emergency;
        this.transactionDate = transactionDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTellerId() {
        return tellerId;
    }

    public void setTellerId(int tellerId) {
        this.tellerId = tellerId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getQueueNo() {
        return queueNo;
    }

    public void setQueueNo(String queueNo) {
        this.queueNo = queueNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isEmergency() {
        return emergency;
    }

    public void setEmergency(boolean emergency) {
        this.emergency = emergency;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
    
    public Duration getServiceDuration() {
        if (startTime != null && endTime != null) {
            return Duration.between(startTime, endTime);
        }
        return Duration.ZERO;
    }
    
}
