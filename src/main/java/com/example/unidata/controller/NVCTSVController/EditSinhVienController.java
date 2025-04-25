package com.example.unidata.controller.NVCTSVController;

import com.example.unidata.DatabaseConnection;
import com.example.unidata.controller.NVCTSVController.utils.SinhVien;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class EditSinhVienController {

    @FXML private TextField masv;
    @FXML private TextField hoten;
    @FXML private TextField phai;
    @FXML private TextField ngsinh;
    @FXML private TextField diachi;
    @FXML private TextField dienthoai;
    @FXML private TextField khoa;
    @FXML private TextField tinhtrang;
    @FXML private Button btnCapNhat;

    private SinhVien sinhVien;
    private QLSinhVienController parentController;

    public void initData(SinhVien sinhVien, QLSinhVienController parentController) {
        this.sinhVien = sinhVien;
        this.parentController = parentController;

        // Populate fields with student data
        masv.setText(sinhVien.getMaSV());
        hoten.setText(sinhVien.getHoTen());
        phai.setText(sinhVien.getPhai());
        
        // Format date for display
        if (sinhVien.getNgaySinh() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            ngsinh.setText(dateFormat.format(sinhVien.getNgaySinh()));
        }
        
        diachi.setText(sinhVien.getDiaChi());
        dienthoai.setText(sinhVien.getDienThoai());
        khoa.setText(sinhVien.getKhoa());
        tinhtrang.setText(sinhVien.getTinhTrang());
    }

    @FXML
    public void capNhatSinhVien(ActionEvent event) {
        if (validateInputs()) {
            try {
                // Parse date from text
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                java.util.Date parsedDate = dateFormat.parse(ngsinh.getText());
                Date sqlDate = new Date(parsedDate.getTime());

                // SQL query to update student
                String sql = "UPDATE DBA_MANAGER.SINHVIEN SET HOTEN=?, PHAI=?, NGSINH=?, DCHI=?, DT=?, KHOA=? WHERE MASV=?";

                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    
                    stmt.setString(1, hoten.getText().trim());
                    stmt.setString(2, phai.getText().trim());
                    stmt.setDate(3, sqlDate);
                    stmt.setString(4, diachi.getText().trim());
                    stmt.setString(5, dienthoai.getText().trim());
                    stmt.setString(6, khoa.getText().trim());
                    stmt.setString(7, tinhtrang.getText().trim());
                    stmt.setString(8, masv.getText().trim());
                    
                    int result = stmt.executeUpdate();
                    
                    if (result > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Sinh viên đã được cập nhật thành công!");
                        
                        // Close the window
                        Stage stage = (Stage) btnCapNhat.getScene().getWindow();
                        stage.close();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật sinh viên.");
                    }
                }
                
            } catch (ParseException e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Định dạng ngày sinh không hợp lệ. Sử dụng định dạng dd/MM/yyyy");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi SQL", "Lỗi khi cập nhật sinh viên: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();
        
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