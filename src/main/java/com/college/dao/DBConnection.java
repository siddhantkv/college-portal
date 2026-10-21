package com.college.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class to provide JDBC Connection to MySQL database.
 * Uses Singleton connection factory method pattern.
 */
public class DBConnection {

    // Dynamic Configuration: Supports both Cloud (Render/Aiven) and Local (Laptop)
    private static String getJdbcUrl() {
        String host = System.getenv("DB_HOST"); // e.g. mysql-xxxx.aivencloud.com:25060
        String dbName = System.getenv("DB_NAME"); // e.g. defaultdb

        if (host != null && !host.trim().isEmpty()) {
            if (dbName == null || dbName.trim().isEmpty()) {
                dbName = "defaultdb";
            }
            // Cloud Database URL (SSL enabled for Aiven)
            return "jdbc:mysql://" + host.trim() + "/" + dbName.trim() + "?sslMode=REQUIRED&serverTimezone=UTC";
        }
        // Local Fallback URL for your laptop
        return "jdbc:mysql://localhost:3306/college_portal?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    private static String getUser() {
        String user = System.getenv("DB_USER");
        return (user != null && !user.trim().isEmpty()) ? user.trim() : "root";
    }

    private static String getPassword() {
        String pass = System.getenv("DB_PASS");
        return (pass != null) ? pass : "Siddhant@2002";
    }

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
        return DriverManager.getConnection(getJdbcUrl(), getUser(), getPassword());
    }
}
