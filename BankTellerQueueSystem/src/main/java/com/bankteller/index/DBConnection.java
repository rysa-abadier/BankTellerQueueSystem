package com.bankteller.index;

import java.sql.*;

public class DBConnection {
    private String server = "jdbc:mysql://localhost:3306/banktellersystem_db"; //localhost:3307/banktellersystem_db";
    private String user = "root";
    private String pass = "";

    public String getServer() {
        return server;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }
    
    public Connection connect() throws SQLException {
        return DriverManager.getConnection(getServer(), getUser(), getPass());
    }
}
