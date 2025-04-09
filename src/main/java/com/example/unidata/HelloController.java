package com.example.unidata;

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

public class HelloController implements Initializable {
    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button button_login;

    //DATABASE TOOLS
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    //CREATE DATABASE
    public void loginAdmin() {
        String sql = "SELECT * FROM ACCOUNT WHERE username = ? and password = ?";
        DatabaseConnection database;
        connect = DatabaseConnection.connectDb();

        try {
            Alert alert;

            prepare = connect.prepareStatement(sql);
            prepare.setString(1, username.getText());
            prepare.setString(2, password.getText());

            result = prepare.executeQuery();

            if(username.getText().isEmpty() || password.getText().isEmpty()) {
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
                    alert.setContentText("Successfully Login!");
                    alert.showAndWait();

                    button_login.getScene().getWindow().hide();
                    //LINK TO NEXT SCENE
                    String fxmlFile;
                    switch (role.toLowerCase()) {
                        case "admin":
                            fxmlFile = "admin.fxml";
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

                    Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
                    Stage stage = new Stage();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
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