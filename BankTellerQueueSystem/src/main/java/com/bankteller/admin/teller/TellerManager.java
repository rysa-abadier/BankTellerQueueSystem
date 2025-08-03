/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bankteller.admin.teller;
import com.bankteller.index.DBConnection;
import java.sql.*;
import java.util.ArrayList;
/**
 *
 * @author Mariah
 */
public class TellerManager {
    public DBConnection db = new DBConnection();
    
    public ArrayList<Teller> getAllTellers() {
    ArrayList<Teller> list = new ArrayList<>();
    try (Connection conn = db.connect();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM tellers")) {

        while (rs.next()) {
            int id = rs.getInt("id");
            String firstName = rs.getString("first_name");
            String lastName = rs.getString("last_name");
            int serviceTypeId = rs.getInt("service_id");

            list.add(new Teller(id, firstName, lastName, serviceTypeId));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return list;
}

public void addTeller(String firstName, String lastName, int serviceTypeId) {
    try (Connection conn = db.connect();
         PreparedStatement stmt = conn.prepareStatement(
             "INSERT INTO tellers (first_name, last_name, service_id) VALUES (?, ?, ?)")) {
        stmt.setString(1, firstName);
        stmt.setString(2, lastName);
        stmt.setInt(3, serviceTypeId);
        stmt.executeUpdate();
        System.out.println("Teller added to DB!");
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    public void updateTeller(int id, String fname, String lname, int serviceTypeId) {
        try (Connection conn = db.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE tellers SET first_name=?, last_name=?, service_id=? WHERE id=?")) {
            stmt.setString(1, fname);
            stmt.setString(2, lname);
            stmt.setInt(3, serviceTypeId); // now use setInt
            stmt.setInt(4, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTeller(int id) {
        try {
            Connection conn = db.connect();

            PreparedStatement clearTransactions = conn.prepareStatement(
                "UPDATE transactions SET teller_id = NULL WHERE teller_id = ?"
            );
            clearTransactions.setInt(1, id);
            clearTransactions.executeUpdate();
                
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM tellers WHERE id=?");
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
