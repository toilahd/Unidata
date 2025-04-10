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

    //CREATE DATABASE
    public void onLogin() {
        String sql = "SELECT * FROM ACCOUNT WHERE username = ? and password = ?";
        DatabaseConnection database;
        connect = DatabaseConnection.connectDb();

        try {
            Alert alert;

            prepare = connect.prepareStatement(sql);
            prepare.setString(1, usernameField.getText());
            prepare.setString(2, passwordField.getText());

            result = prepare.executeQuery();

            if(usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please fill all blank fields!");
                alert.showAndWait();
            } else {
                if(result.next()) {
                    String role = result.getString("VAITRO");

                    //Proceed To Next Scene
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully Logged In as " + role + "!");
                    alert.showAndWait();

                    loginButton.getScene().getWindow().hide();
                    //LINK TO NEXT SCENE
                    String fxmlFile;
                    switch (role.toLowerCase()) {
                        case "admin":
                            fxmlFile = "/com/example/unidata/dashboard.fxml";
                            break;
                        case "teacher":
                            fxmlFile = "teacher.fxml";
                            break;
                        case "student":
                            fxmlFile = "student.fxml";
                            break;
                        default:
                            fxmlFile = "default.fxml"; // optional fallback
                            break;
                    }

                    Parent root = FXMLLoader.load(getClass().getResource("/com/example/unidata/dashboard.fxml"));
                    Stage stage = new Stage();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.setTitle("Dashboard - " + role);
                    stage.show();

                }else{
                    //Send Error Message
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Wrong Username/Password");
                    alert.showAndWait();
                }
            }

        }catch (Exception e) {e.printStackTrace();}
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
}
