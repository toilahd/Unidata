package com.example.unidata;

import java.sql.*;

public class DatabaseConnection {
    private static Connection connection;  // Add static connection instance
    private static String currentUsername;
    private static String currentPassword;
    private static String currentRole;


    public static Connection connectDb(String userName, String password) {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection; // Reuse if already connected
            }

            Class.forName("oracle.jdbc.driver.OracleDriver");
            String url = "jdbc:oracle:thin:@localhost:1521/xepdb1";
            connection = DriverManager.getConnection(url, userName, password);
            currentPassword = password;
            currentUsername = userName;
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(5)) {
                if (currentUsername != null && currentPassword != null) {
                    System.out.println("Reconnecting to the database...");
                    connectDb(currentUsername, currentPassword);
                } else {
                    System.err.println("No stored credentials. Cannot reconnect.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public static String getMasvFromLogin() {
        String sql = """
            SELECT MASV
            FROM DBA_MANAGER.SINHVIEN
        """;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("MASV");
            } else {
                System.out.println("THERE IS NOT MASV FROM THIS USER FROM THE SINHVIEN TABLE");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
