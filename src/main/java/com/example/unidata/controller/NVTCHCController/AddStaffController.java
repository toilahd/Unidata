package com.example.unidata.controller.NVTCHCController;

import com.example.unidata.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AddStaffController {
    @FXML private Button btnSignOut;
    @FXML private Button btnUserProfile;
    @FXML private Button btnStaffList;
    @FXML private Button btnAddStaff;

    @FXML private Button btnThem;
    @FXML private TextField tfHoTen;
    @FXML private ComboBox cbGioiTinh;
    @FXML private DatePicker datePicker;
    @FXML private TextField tfLuong;
    @FXML private TextField tfDienThoai;
    @FXML private TextField tfPhuCap;
    @FXML private ComboBox<String> maKhoa;
    @FXML private ComboBox<String> cbVaiTro;

    @FXML
    void initialize() {
        loadGioiTinh();
        loadVaiTro();
        loadDepartments();
    }

    @FXML public void onAdd(ActionEvent event) {
        String hoTen = tfHoTen.getText();
        String gioiTinhStr = (cbGioiTinh.getValue() != null) ? cbGioiTinh.getValue().toString() : null;
        String ngaySinh = (datePicker.getValue() != null) ? datePicker.getValue().toString() : null;
        String luongStr = tfLuong.getText();
        String phuCapStr = tfPhuCap.getText();
        String dienThoai = tfDienThoai.getText();
        String maKhoaStr = (maKhoa.getValue() != null) ? maKhoa.getValue().toString() : null;
        String maVaiTroStr = (cbVaiTro.getValue() != null) ? cbVaiTro.getValue().toString() : null;

        if (hoTen.isEmpty() || gioiTinhStr.isEmpty() || ngaySinh == null || luongStr.isEmpty() || phuCapStr.isEmpty() || dienThoai.isEmpty() || maKhoaStr == null) {
            showAlert("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        try {
            var conn = DatabaseConnection.getConnection();
            double luong = Double.parseDouble(luongStr);
            double phuCap = Double.parseDouble(phuCapStr);

            // Câu lệnh SQL với dấu ? để thay thế các tham số
            String sql = """
            DECLARE
                v_MANLD   VARCHAR2(10);
                v_MAX_NUM NUMBER;
                v_VAITRO  VARCHAR2(20) := ?;
            BEGIN
                SELECT NVL(MAX(TO_NUMBER(SUBSTR(MANLD, LENGTH(v_VAITRO) + 1))), 0)
                INTO v_MAX_NUM
                FROM DBA_MANAGER.NHANVIEN
                WHERE VAITRO = v_VAITRO
                AND REGEXP_LIKE(MANLD, '^' || v_VAITRO || '\\d{3}$');
                 
                v_MANLD := v_VAITRO || LPAD(v_MAX_NUM + 1, 3, '0');
            
                INSERT INTO DBA_MANAGER.NHANVIEN (
                    MANLD, HOTEN, PHAI, NGSINH, LUONG, PHUCAP, DT, VAITRO, MADV
                ) VALUES (
                    v_MANLD, ?, ?, TO_DATE(?, 'YYYY-MM-DD'),
                    ?, ?, ?, v_VAITRO, ?
                );
            
                COMMIT;
            END;
        """;

            var pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, maVaiTroStr);
            pstmt.setString(2, hoTen);
            pstmt.setString(3, gioiTinhStr);
            pstmt.setString(4, ngaySinh);
            pstmt.setDouble(5, luong);
            pstmt.setDouble(6, phuCap);
            pstmt.setString(7, dienThoai);
            pstmt.setString(8, maKhoaStr);

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                showAlert("Thêm nhân viên thành công!");
                clearForm();
            } else {
                showAlert("Không thể thêm nhân viên.");
            }

        } catch (NumberFormatException e) {
            showAlert("Lương và phụ cấp phải là số!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi khi thêm nhân viên: " + e.getMessage());
        }
    }

    private void loadVaiTro() {
        cbVaiTro.getItems().addAll("GV", "TRGDV", "NVPDT", "NVTCHC", "NVCTSV", "NVCB");
    }
    private void loadGioiTinh() {
        cbGioiTinh.getItems().addAll("Nam", "Nữ");
    }
    private void loadDepartments() {
        String sql = "SELECT MADV FROM DBA_MANAGER.DONVI";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String ma = rs.getString("MADV");
                maKhoa.getItems().add(ma);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi khi tải danh sách mã khoa.");
        }
    }


    private void clearForm() {
        tfHoTen.clear();
        datePicker.setValue(null);
        tfLuong.clear();
        tfPhuCap.clear();
        tfDienThoai.clear();
        cbGioiTinh.setValue(null);
        maKhoa.setValue(null);
        cbVaiTro.setValue(null);
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
