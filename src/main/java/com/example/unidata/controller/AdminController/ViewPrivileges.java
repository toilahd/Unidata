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
import java.util.ResourceBundle;

public class ViewPrivileges implements Initializable {
    @FXML
    private Button btnGrantPrivileges;

    @FXML
    private Button btnRevokePrivileges;

    @FXML
    private Button btnSignOut;

    @FXML
    private Button btnUserRoleList;

    @FXML
    private Button btnUserRoleManagement;


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

    public void onUserRoleManagement(ActionEvent event) {
        loadScene("/com/example/unidata/user_role_management.fxml", "User/Role Management - ADMIN");
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


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
