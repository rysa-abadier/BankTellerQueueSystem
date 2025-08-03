package com.bankteller.admin.queue;

import java.time.*;

public class Transaction {
    private int id;
    private int customer_id;
    private int teller_id;
    private int service_id;
    private int queue_no;
    private String name;
    private String status;
    private boolean emergency;
    private LocalDateTime transaction_date;
    private String start_time;
    private String end_time;

    public Transaction(int id, int customer_id, int teller_id, int service_id, int queue_no, String name, String status, boolean emergency, LocalDateTime transaction_date, String start_time, String end_time) {
        this.id = id;
        this.customer_id = customer_id;
        this.teller_id = teller_id;
        this.service_id = service_id;
        this.queue_no = queue_no;
        this.name = name;
        this.status = status;
        this.emergency = emergency;
        this.transaction_date = transaction_date;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public int getTeller_id() {
        return teller_id;
    }

    public void setTeller_id(int teller_id) {
        this.teller_id = teller_id;
    }

    public int getService_id() {
        return service_id;
    }

    public void setService_id(int service_id) {
        this.service_id = service_id;
    }

    public int getQueue_no() {
        return queue_no;
    }

    public void setQueue_no(int queue_no) {
        this.queue_no = queue_no;
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

    public LocalDateTime getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(LocalDateTime transaction_date) {
        this.transaction_date = transaction_date;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }    
}
