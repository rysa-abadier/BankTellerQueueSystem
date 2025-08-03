/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tellermanagement;
import java.sql.*;
import java.util.ArrayList;
/**
 *
 * @author Mariah
 */
public class TellerManager {
    public ArrayList<Teller> getAllTellers() {
    ArrayList<Teller> list = new ArrayList<>();
    try (Connection conn = DBConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM tellers")) {

        while (rs.next()) {
            int id = rs.getInt("id");
            String firstName = rs.getString("first_name");
            String lastName = rs.getString("last_name");
            int serviceTypeId = rs.getInt("service_type");

            list.add(new Teller(id, firstName, lastName, serviceTypeId));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return list;
}

public void addTeller(String firstName, String lastName, int serviceTypeId) {
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(
             "INSERT INTO tellers (first_name, last_name, service_type) VALUES (?, ?, ?)")) {
        stmt.setString(1, firstName);
        stmt.setString(2, lastName);
        stmt.setInt(3, serviceTypeId);
        stmt.executeUpdate();
        System.out.println("Teller added to DB!");
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    public void updateTeller(int id, String name, int serviceTypeId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE tellers SET name=?, service_type=? WHERE id=?")) {
            stmt.setString(1, name);
            stmt.setInt(2, serviceTypeId); // now use setInt
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
