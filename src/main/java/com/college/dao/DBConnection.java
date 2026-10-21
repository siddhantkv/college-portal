package com.college.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class to provide JDBC Connection to MySQL database.
 * Uses Singleton connection factory method pattern.
 */
public class DBConnection {

    // Database Configuration Constants
    // NOTE: Agar aapke MySQL ka root password alag hai, toh yahan change karein.
    private static final String URL = "jdbc:mysql://localhost:3306/college_portal?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Siddhant@2002"; // <-- Apna MySQL root password yahan daalein

    // Static block to register MySQL JDBC driver once when class loads
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found! Ensure mysql-connector-j is in classpath.");
            e.printStackTrace();
        }
    }

    /**
     * Returns a new database connection.
     * @return Connection object
     * @throws SQLException if database connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
