package com.example.unidata;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    private static Connection connection;  // Add static connection instance
    private static String currentUsername;
    private static String currentRole;

    public static Connection connectDb(String userName, String password) {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection; // Reuse if already connected
            }

            Class.forName("oracle.jdbc.driver.OracleDriver");
            String url = "jdbc:oracle:thin:@localhost:1521/xepdb1";
            connection = DriverManager.getConnection(url, userName, password);

            currentUsername = userName; // Optional
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Optional session helpers
    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static void setCurrentRole(String role) {
        currentRole = role;
    }

    public static String getCurrentRole() {
        return currentRole;
    }
    public static void logout() {
        // Clear session details
        currentUsername = null;
        currentRole = null;

        // Close the database connection
        closeConnection();
    }
}
