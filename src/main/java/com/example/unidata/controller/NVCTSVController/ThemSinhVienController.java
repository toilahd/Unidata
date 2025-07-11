package com.example.unidata.controller.NVCTSVController;
import com.example.unidata.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.stage.Stage;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;

public class ThemSinhVienController {

    @FXML private TextField masv;
    @FXML private TextField hoten;
    @FXML private TextField phai;
    @FXML private TextField ngsinh;
    @FXML private TextField diachi;
    @FXML private TextField dienthoai;
    @FXML private TextField khoa;
    @FXML private Button btnThem;

    @FXML
    public void themSinhVien(ActionEvent event) {
        if (validateInputs()) {
            try {
                // Parse date from text
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                java.util.Date parsedDate = dateFormat.parse(ngsinh.getText());
                Date sqlDate = new Date(parsedDate.getTime());

                // SQL query to insert student
                String sql = "INSERT INTO DBA_MANAGER.SINHVIEN(MASV, HOTEN, PHAI, NGSINH, DCHI, DT, KHOA) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?)";

                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    
                    stmt.setString(1, masv.getText().trim());
                    stmt.setString(2, hoten.getText().trim());
                    stmt.setString(3, phai.getText().trim());
                    stmt.setDate(4, sqlDate);
                    stmt.setString(5, diachi.getText().trim());
                    stmt.setString(6, dienthoai.getText().trim());
                    stmt.setString(7, khoa.getText().trim());
                    
                    int result = stmt.executeUpdate();
                    
                    if (result > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Sinh viên đã được thêm thành công!");
                        
                        // Close the window
                        Stage stage = (Stage) btnThem.getScene().getWindow();
                        stage.close();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm sinh viên.");
                    }
                }
                
            } catch (ParseException e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Định dạng ngày sinh không hợp lệ. Sử dụng định dạng dd/MM/yyyy");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi SQL", "Lỗi khi thêm sinh viên: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();
        
        if (masv.getText().trim().isEmpty()) {
            errors.append("Mã sinh viên không được để trống\n");
        }
        if (hoten.getText().trim().isEmpty()) {
            errors.append("Họ tên không được để trống\n");
        }
        if (phai.getText().trim().isEmpty()) {
            errors.append("Phái không được để trống\n");
        }
        if (ngsinh.getText().trim().isEmpty()) {
            errors.append("Ngày sinh không được để trống\n");
        }
        if (khoa.getText().trim().isEmpty()) {
            errors.append("Khoa không được để trống\n");
        }
        
        if (errors.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", errors.toString());
            return false;
        }
        
        return true;
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}