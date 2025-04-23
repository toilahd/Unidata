package com.example.unidata.controller.NVCBController;

import com.example.unidata.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class ProfileViewController implements Initializable {

    @FXML
    private Button btnSignOut;

    @FXML
    private Label MANLD;
    @FXML
    private Label HOTEN;
    @FXML
    private Label PHAI;
    @FXML
    private Label NGSINH;
    @FXML
    private Label LUONG;
    @FXML
    private Label PHUCAP;
    @FXML
    private Label MAKHOA;

    @FXML
    private TextField DT;
    @FXML
    private Button editProfile;

    private void loadNVCBData() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM DBA_MANAGER.UV_NVCB_CANHAN");
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                MANLD.setText(rs.getString("MANLD"));
                HOTEN.setText(rs.getString("HOTEN"));
                PHAI.setText(rs.getString("PHAI"));
                NGSINH.setText(rs.getDate("NGSINH").toString());
                LUONG.setText(rs.getString("LUONG"));
                PHUCAP.setText(rs.getString("PHUCAP"));
                DT.setText(rs.getString("DT"));
                MAKHOA.setText(rs.getString("MADV"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEditProfile(ActionEvent event) {
        String newPhone = DT.getText().trim();

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("UPDATE DBA_MANAGER.UV_NVCB_CANHAN SET DT = ?");
            stmt.setString(1, newPhone);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                showAlert("Cập nhật thành công.");
                loadNVCBData(); // Refresh view
            } else {
                showAlert("Không thể cập nhật thông tin.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Đã xảy ra lỗi khi cập nhật.");
        }
    }
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    @FXML
    private void onSignOut(ActionEvent event) {
        try {
            DatabaseConnection.logout();
            Stage stage = (Stage) btnSignOut.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/unidata/login.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadScene(String fxmlFile, String title, Button button) {
        try {
            Stage stage = (Stage) button.getScene().getWindow();
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
        loadNVCBData();
    }
}
