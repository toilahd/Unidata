package com.example.unidata.controller.NVTCHCController;

import com.example.unidata.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileViewController {
    @FXML private Button btnSignOut;
    @FXML private Button editProfile;
    @FXML private Button btnUserProfile;
    @FXML private Button btnStaffList;
    @FXML private Button btnAddStaff;

    @FXML private Label MANLD;
    @FXML private Label HOTEN;
    @FXML private Label PHAI;
    @FXML private Label NGSINH;
    @FXML private Label LUONG;
    @FXML private Label PHUCAP;
    @FXML private Label MAKHOA;
    @FXML private TextField DT;

    public void initialize() {
        loadProfileData();
    }
    public void loadProfileData() {
        String sql = "SELECT * " +
                "FROM DBA_MANAGER.UV_NVCB_CANHAN";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                MANLD.setText(rs.getString("MANLD"));
                HOTEN.setText(rs.getString("HOTEN"));
                PHAI.setText(rs.getString("PHAI"));
                NGSINH.setText(rs.getString("NGSINH"));
                LUONG.setText(rs.getString("LUONG"));
                PHUCAP.setText(rs.getString("PHUCAP"));
                DT.setText(rs.getString("DT"));
                MAKHOA.setText(rs.getString("MADV"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Không thể tải dữ liệu cá nhân: " + e.getMessage());
        }
    }

    @FXML
    public void onEditProfile(ActionEvent event) {
        String newPhone = DT.getText();

        String sql = "UPDATE DBA_MANAGER.UV_NVCB_CANHAN SET DT = ?";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newPhone);

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                showAlert("Cập nhật số điện thoại thành công!");
                loadProfileData();
            } else {
                showAlert("Không thể cập nhật thông tin.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi: không thể cập nhật dữ liệu: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onSignOut(ActionEvent event) {
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

    @FXML public void onProfile() {
        loadScene("/com/example/unidata/PhanHe2/NV_TCHCView/NV_TCHC_ProfileView.fxml", "NVTCHC - Profile", btnUserProfile);
    }
    @FXML public void onStaffList() {
        loadScene("/com/example/unidata/PhanHe2/NV_TCHCView/NV_TCHC_StaffList.fxml", "NVTCHC - Danh sách nhân viên", btnStaffList);
    }
    @FXML public void onAddStaff() {
        loadScene("/com/example/unidata/PhanHe2/NV_TCHCView/NV_TCHC_AddStaff.fxml", "NVTCHC - Thêm nhân viên mới", btnAddStaff);
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
}
