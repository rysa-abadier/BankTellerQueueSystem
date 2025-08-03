package com.bankteller.admin.service;

import com.bankteller.index.DBConnection;
import java.sql.*;
import java.util.ArrayList;

public class ServiceManager {
    public DBConnection db = new DBConnection();
    private ArrayList<Service> services;
    
    private String minutesToTimeString(int minutes) {
        int mins = minutes % 60;
        
        return String.format("00:%02d:00", mins);
    }
    
    public ServiceManager() {
        services = new ArrayList<>();
    }

    public ArrayList<Service> getAllServices() {
        services.clear();
        String sql = "SELECT * FROM services";
        
        try {
            Connection conn = db.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Service service = new Service(
                        rs.getInt("id"),
                        rs.getString("name")
                );
                
                service.setPriority(rs.getString("priority"));
                
                Time time = rs.getTime("average_time");
                int minutes = time.toLocalTime().getHour() * 60 + time.toLocalTime().getMinute();
                service.setAvgServiceTime(minutes);
                
                services.add(service);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return services;
    }
    
    public Service getById(int id) {
        for (Service s : services) {
            if (s.getId() == id) return s;
        }
        return null;
    }

    public void addService(Service s) {
        String sql = "INSERT INTO services(name, priority, average_time) VALUES (?, ?, ?)";
        
        try {
            Connection conn = db.connect();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, s.getName());
            stmt.setString(2, s.getPriority());
            stmt.setString(3, minutesToTimeString(s.getAvgServiceTime()));
            
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                s.setId(rs.getInt(1));
            }
            services.add(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeService(int id) {
        String sql = "DELETE FROM services WHERE id = ?";
        try {
            Connection conn = db.connect();
            
            PreparedStatement clearTellers = conn.prepareStatement(
                "UPDATE tellers SET service_id = NULL WHERE service_id = ?"
            );
            clearTellers.setInt(1, id);
            clearTellers.executeUpdate();

            PreparedStatement clearTransactions = conn.prepareStatement(
                "UPDATE transactions SET service_id = NULL WHERE service_id = ?"
            );
            clearTransactions.setInt(1, id);
            clearTransactions.executeUpdate();
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
            
            services.removeIf(s -> s.getId() == id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void updateService(Service updated) {
        String sql = "UPDATE services SET name = ?, priority = ?, average_time = ? WHERE id = ?";

        try {
        Connection conn = db.connect();
        PreparedStatement stmt = conn.prepareStatement(sql);

        stmt.setString(1, updated.getName());
        stmt.setString(2, updated.getPriority());
        stmt.setString(3, minutesToTimeString(updated.getAvgServiceTime()));
        stmt.setInt(4, updated.getId());
        stmt.executeUpdate();

            for (int i = 0; i < services.size(); i++) {
                if (services.get(i).getId() == updated.getId()) {
                    services.set(i, updated);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
}
