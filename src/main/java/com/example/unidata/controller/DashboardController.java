package com.example.unidata.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {
    @FXML
    private Button btnSignOut;

    @FXML
    private Text txtRole;

    @FXML
    private Button btnUserRoleManagement;

    @FXML
    private Button btnUserRoleList;

    @FXML
    private Button btnGrantPrivileges;

    @FXML
    private Button btnRevokePrivileges;

    @FXML
    private Button btnViewPrivileges;

    private String userRole;

    public void initialize() {
        // default role nếu chưa gán
        if (userRole != null) {
            txtRole.setText(userRole);
        } else {
            txtRole.setText("Unknown");
        }
    }

    // Hàm để nhận Role từ LoginController
    public void setUserRole(String role) {
        this.userRole = role;
        txtRole.setText(role);
    }

    @FXML
    void onSignOut(ActionEvent event) {
        try {
            Stage stage = (Stage) btnSignOut.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onUserRoleManagement(ActionEvent event) {
        loadScene("user_role_management.fxml", "User/Role Management");
    }

    @FXML
    void onUserRoleList(ActionEvent event) {
        loadScene("user_role_list.fxml", "User/Role List");
    }

    @FXML
    void onGrantPrivileges(ActionEvent event) {
        loadScene("grant_privileges.fxml", "Grant Privileges");
    }

    @FXML
    void onRevokePrivileges(ActionEvent event) {
        loadScene("revoke_privileges.fxml", "Revoke Privileges");
    }

    @FXML
    void onViewPrivileges(ActionEvent event) {
        loadScene("view_privileges.fxml", "View Privileges");
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
}
