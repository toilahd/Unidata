package com.example.unidata.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdminController {
    private Connection connect;


    // 1. Creating, Deleting, and Modifying Users and Roles
    // Create a new user
    public void createUser(String username, String password) throws SQLException {
        String sql = "CREATE USER " + username + " IDENTIFIED BY " + password;
        executeStatement(sql);
        System.out.println("User " + username + " created successfully");
    }

    // Delete a user
    public void dropUser(String username) throws SQLException {
        String sql = "DROP USER " + username + " CASCADE";
        executeStatement(sql);
        System.out.println("User " + username + " dropped successfully");
    }

    // Alter user password
    public void alterUserPassword(String username, String newPassword) throws SQLException {
        String sql = "ALTER USER " + username + " IDENTIFIED BY " + newPassword;
        executeStatement(sql);
        System.out.println("Password for user " + username + " changed successfully");
    }

    // Create a new role
    public void createRole(String roleName) throws SQLException {
        String sql = "CREATE ROLE " + roleName;
        executeStatement(sql);
        System.out.println("Role " + roleName + " created successfully");
    }

    // Delete a role
    public void dropRole(String roleName) throws SQLException {
        String sql = "DROP ROLE " + roleName;
        executeStatement(sql);
        System.out.println("Role " + roleName + " dropped successfully");
    }

    // Helper method to execute SQL statements
    private void executeStatement(String sql) throws SQLException {
        try (Statement stmt = connect.createStatement()) {
            stmt.execute(sql);
        }
    }


    // 2. Listing Users and Roles
    // List all users in the database
    public void listUsers() throws SQLException {
        String sql = "SELECT username, account_status, default_tablespace FROM dba_users ORDER BY username";
        try (Statement stmt = connect.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("=== DATABASE USERS ===");
            System.out.println("USERNAME\t\tSTATUS\t\tDEFAULT TABLESPACE");
            System.out.println("---------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-20s\t%-10s\t%s\n",
                        rs.getString("username"),
                        rs.getString("account_status"),
                        rs.getString("default_tablespace"));
            }
        }
    }

    // List all roles in the database
    public void listRoles() throws SQLException {
        String sql = "SELECT role, authentication_type FROM dba_roles ORDER BY role";
        try (Statement stmt = connect.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("=== DATABASE ROLES ===");
            System.out.println("ROLE\t\t\tAUTHENTICATION TYPE");
            System.out.println("---------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-20s\t%s\n",
                        rs.getString("role"),
                        rs.getString("authentication_type"));
            }
        }
    }


    // 3. Granting Privileges
    // Grant system privilege to a user
    public void grantSystemPrivilegeToUser(String privilege, String username, boolean withGrantOption)
            throws SQLException {
        String sql = "GRANT " + privilege + " TO " + username;
        if (withGrantOption) {
            sql += " WITH ADMIN OPTION";
        }
        executeStatement(sql);
        System.out.println("System privilege " + privilege + " granted to user " + username);
    }

    // Grant system privilege to a role
    public void grantSystemPrivilegeToRole(String privilege, String roleName) throws SQLException {
        String sql = "GRANT " + privilege + " TO " + roleName;
        executeStatement(sql);
        System.out.println("System privilege " + privilege + " granted to role " + roleName);
    }

    // Grant role to user
    public void grantRoleToUser(String roleName, String username) throws SQLException {
        String sql = "GRANT " + roleName + " TO " + username;
        executeStatement(sql);
        System.out.println("Role " + roleName + " granted to user " + username);
    }

    // Grant object privilege on table
    public void grantTablePrivilegeToUser(String privilege, String tableName, String username,
                                          boolean withGrantOption, String... columns) throws SQLException {

        StringBuilder sql = new StringBuilder("GRANT ");

        if (privilege.equalsIgnoreCase("SELECT") || privilege.equalsIgnoreCase("UPDATE")) {
            // For SELECT and UPDATE, we can specify columns
            if (columns != null && columns.length > 0) {
                sql.append(privilege).append("(");
                for (int i = 0; i < columns.length; i++) {
                    sql.append(columns[i]);
                    if (i < columns.length - 1) {
                        sql.append(", ");
                    }
                }
                sql.append(")");
            } else {
                sql.append(privilege);
            }
        } else {
            // For other privileges like INSERT, DELETE
            sql.append(privilege);
        }

        sql.append(" ON ").append(tableName).append(" TO ").append(username);

        if (withGrantOption) {
            sql.append(" WITH GRANT OPTION");
        }

        executeStatement(sql.toString());
        System.out.println("Privilege " + privilege + " on table " + tableName + " granted to user " + username);
    }

    // Grant object privilege on view
    public void grantViewPrivilegeToUser(String privilege, String viewName, String username,
                                         boolean withGrantOption, String... columns) throws SQLException {

        // Similar to granting table privilege
        StringBuilder sql = new StringBuilder("GRANT ");

        if (privilege.equalsIgnoreCase("SELECT") || privilege.equalsIgnoreCase("UPDATE")) {
            if (columns != null && columns.length > 0) {
                sql.append(privilege).append("(");
                for (int i = 0; i < columns.length; i++) {
                    sql.append(columns[i]);
                    if (i < columns.length - 1) {
                        sql.append(", ");
                    }
                }
                sql.append(")");
            } else {
                sql.append(privilege);
            }
        } else {
            sql.append(privilege);
        }

        sql.append(" ON ").append(viewName).append(" TO ").append(username);

        if (withGrantOption) {
            sql.append(" WITH GRANT OPTION");
        }

        executeStatement(sql.toString());
        System.out.println("Privilege " + privilege + " on view " + viewName + " granted to user " + username);
    }

    // Grant execute privilege on procedure/function
    public void grantExecutePrivilege(String objectType, String objectName, String username,
                                      boolean withGrantOption) throws SQLException {

        String sql = "GRANT EXECUTE ON " + objectType + " " + objectName + " TO " + username;

        if (withGrantOption) {
            sql += " WITH GRANT OPTION";
        }

        executeStatement(sql);
        System.out.println("Execute privilege on " + objectType + " " + objectName + " granted to user " + username);
    }
}
