/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.tellermanagement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author Mariah
 */
public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/teller_temp";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // change this if your XAMPP has a MySQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
}
