package com.example.unidata.controller;

import com.example.unidata.DatabaseConnection;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.io.IOException;

public class LoginController implements Initializable {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    //DATABASE TOOLS
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    // CREATE DATABASE
    public void onLogin() {
        Alert alert;
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Check if fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all blank fields!");
            alert.showAndWait();
            return;
        }

        // Try to establish a connection
        connect = DatabaseConnection.connectDb(username, password);

        if (connect == null) {
            // If the connection fails, show an error message
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Failed to connect to the database. Check username or password (may be you dont have an account)");
            alert.showAndWait();
            return;
        }

        // Proceed with fetching roles from the database
        try {
            String sql = "SELECT GRANTED_ROLE FROM USER_ROLE_PRIVS";
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            ArrayList<String> grantedRoles = new ArrayList<String>();

            while (result.next()) {
                grantedRoles.add(result.getString("GRANTED_ROLE"));
            }

            if (!grantedRoles.isEmpty()) {
                String role = determineUserRole(grantedRoles);

                // Set the current role in DatabaseConnection
                DatabaseConnection.setCurrentRole(role);

                // Show information about successful login
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Message");
                alert.setHeaderText(null);
                alert.setContentText("Successfully Logged In as " + username + " with role: " + role + "!");
                alert.showAndWait();

                // Hide the current login window and proceed to the next scene
                loginButton.getScene().getWindow().hide();
                loadDashboardForRole(role);

            } else {
                // If no role found, show error message
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("There is something sus here (no role), fix it my friend");
                alert.showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while fetching user roles. Please try again.");
            alert.showAndWait();
        }
    }

    // Helper method to determine the user role
    private String determineUserRole(ArrayList<String> grantedRoles) {
        if (grantedRoles.contains("DBA")) {
            return "dba";
        }
        else if (grantedRoles.contains("RL_SV")) {
            return "student";
        }
        // add next logic here
        else if (grantedRoles.contains("RL_GV")) {
            return "teacher";
        }
        else if (grantedRoles.contains("RL_NVCTSV")) {
            return "nvctsv";
        }
        else if (grantedRoles.contains("RL_NVPDT")) {
            return "nvpdt";
        }
        else {
            return "null"; // or another default role if applicable
        }
    }
    private Parent loadFXML(String path) throws IOException {
        URL fxmlUrl = getClass().getResource(path);
        if (fxmlUrl == null) {
            throw new IOException("FXML file not found: " + path);
        }
        return FXMLLoader.load(fxmlUrl);
    }

    // Helper method to load the appropriate dashboard based on the role
    private void loadDashboardForRole(String role) {
        String fxmlFile;
        switch (role.toLowerCase()) {
            case "dba":
                fxmlFile = "/com/example/unidata/PhanHe1/dashboard.fxml";
                break;
            case "teacher":
                fxmlFile = "/com/example/unidata/PhanHe2/TeacherView/TeacherProfileView.fxml";
                break;
            case "student":
                fxmlFile = "/com/example/unidata/PhanHe2/StudentView/StudentProfileView.fxml";
                break;
            case "nvctsv":
                fxmlFile = "/com/example/unidata/PhanHe2/NVCTSV_View/NVCTSV_ProfileView.fxml";
                break;
            case "nvpdt":
                fxmlFile = "/com/example/unidata/PhanHe2/NV_PDTView/NV_PDT_ProfileView.fxml";
                break;
            default:
                fxmlFile = "/com/example/unidata/default.fxml"; // optional fallback
                break;
        }

        try {
            Parent root = loadFXML(fxmlFile);
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Dashboard - " + role);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load the scene. Please try again.");
            alert.showAndWait();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
}
