package com.bankteller.admin.service;

import com.bankteller.admin.queue.DBConnection;
import java.sql.*;
import java.util.ArrayList;

public class ServiceManager {
    public DBConnection db = new DBConnection();
    private ArrayList<Service> services;

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
                service.setAvgServiceTime(rs.getInt("averageTime"));
                
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
        String sql = "INSERT INTO services(name, priority, averageTime) VALUES (?, ?, ?)";
        
        try {
            Connection conn = db.connect();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, s.getName());
            stmt.setString(2, s.getPriority());
            stmt.setInt(3, s.getAvgServiceTime());
            
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
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
            
            services.removeIf(s -> s.getId() == id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void updateService(Service updated) {
        String sql = "UPDATE services SET name = ?, priority = ?, averageTime = ? WHERE id = ?";

        try {
        Connection conn = db.connect();
        PreparedStatement stmt = conn.prepareStatement(sql);

        stmt.setString(1, updated.getName());
        stmt.setString(2, updated.getPriority());
        stmt.setInt(3, updated.getAvgServiceTime());
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
