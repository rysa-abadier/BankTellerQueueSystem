package com.bankteller.admin.teller;

import com.bankteller.admin.service.Service;
import java.util.ArrayList;

public class TellerManager {
    private ArrayList<Teller> tellers;

    public TellerManager() {
        tellers = new ArrayList<>();
    }

    public void addTeller(int id, String name) {
        tellers.add(new Teller(id, name));
    }

    public void removeTeller(int id) {
        tellers.removeIf(teller -> teller.getId() == id);
    }

    public void editTellerName(int id, String newName) {
        for (Teller t : tellers) {
            if (t.getId() == id) {
                t.setName(newName);
                break;
            }
        }
    }

    public void assignService(int id, Service serviceType) {
        for (Teller t : tellers) {
            if (t.getId() == id) {
                t.setServiceType(serviceType);
                break;
            }
        }
    }

    public ArrayList<Teller> getTellers() {
        return tellers;
    }
}

