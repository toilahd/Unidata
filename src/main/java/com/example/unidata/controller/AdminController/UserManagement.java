package com.example.unidata.controller.AdminController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class UserManagement implements Initializable {
    @FXML
    private Button btnGrantPrivileges;

    @FXML
    private Button btnRevokePrivileges;

    @FXML
    private Button btnSignOut;

    @FXML
    private Button btnUserRoleList;

    @FXML
    private Button btnViewPrivileges;


    private Connection connect;
    private PreparedStatement prepare;
    private Statement statement;
    private ResultSet result;


    public void onSignOut(ActionEvent event) {
        try {
            Stage stage = (Stage) btnSignOut.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/unidata/login.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onUserRoleList(ActionEvent event) {
        loadScene("/com/example/unidata/user_role_list.fxml", "User/Role List - ADMIN");
    }

    public void onGrantPrivileges(ActionEvent event) {
        loadScene("/com/example/unidata/grant_privileges.fxml", "Grant Privileges - ADMIN");
    }

    public void onRevokePrivileges(ActionEvent event) {
        loadScene("/com/example/unidata/revoke_privileges.fxml", "Revoke Privileges - ADMIN");
    }

    public void onViewPrivileges(ActionEvent event) {
        loadScene("/com/example/unidata/view_privileges.fxml", "View Privileges - ADMIN");
    }

    public void onRoleManagement(ActionEvent event) {
        loadScene("/com/example/unidata/role_management.fxml", "User/Role Management - ADMIN");
    }


    private void loadScene(String fxmlFile, String title) {
        try {
            Stage stage = (Stage) btnSignOut.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


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


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
