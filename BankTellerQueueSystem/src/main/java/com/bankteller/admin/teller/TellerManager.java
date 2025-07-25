package com.example.tellermanagement;
import java.sql.*;
import java.util.ArrayList;

public class TellerManager {
     public ArrayList<Teller> getAllTellers() {
        ArrayList<Teller> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM tellers")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String serviceType = rs.getString("service_type");
                list.add(new Teller(id, name, serviceType));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addTeller(String name, String serviceType) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO tellers (name, service_type) VALUES (?, ?)")) {
            stmt.setString(1, name);
            stmt.setString(2, serviceType);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTeller(int id, String name, String serviceType) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE tellers SET name=?, service_type=? WHERE id=?")) {
            stmt.setString(1, name);
            stmt.setString(2, serviceType);
            stmt.setInt(3, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTeller(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM tellers WHERE id=?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
