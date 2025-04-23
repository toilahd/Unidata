package com.example.unidata.controller.NVTCHCController;

import com.example.unidata.DatabaseConnection;
import com.example.unidata.controller.NVTCHCController.utils.NhanVien;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Savepoint;

public class UpdateStaffController {
    @FXML
    private TextField tfHoTen, tfLuong, tfPhucap, tfDienThoai;
    @FXML private Label lbGioiTinh;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> maKhoa;
    @FXML private Button btnCapNhat;

    StaffListController controller = new StaffListController();

    @FXML
    public void initialize() {
        loadDepartments();
        btnCapNhat.setOnAction(event -> updateNhanVien());
    }

    private String manld;
    String getManld() {
        return manld;
    }
    public void setMaNLD(String str) {
        manld = str;
    }
    public void setNhanVien(NhanVien nv, StaffListController controller) {
        tfHoTen.setText(nv.getHoten());
        tfLuong.setText(String.valueOf(nv.getLuong()));
        tfPhucap.setText(String.valueOf(nv.getPhucap()));
        tfDienThoai.setText(nv.getDienthoai());
        lbGioiTinh.setText(nv.getPhai());
        datePicker.setValue(nv.getNgsinh());
        maKhoa.setValue(nv.getMadv());
        setMaNLD(nv.getManld());
        this.controller = controller;
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
            showAlert("Lỗi khi tải danh sách mã khoa." + e.getMessage());
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
    private void updateNhanVien() {
        String hoten = tfHoTen.getText();
        double luong = Double.parseDouble(tfLuong.getText());
        double phucap = Double.parseDouble(tfPhucap.getText());
        String dienthoai = tfDienThoai.getText();
        String phai = lbGioiTinh.getText(); // hoặc ComboBox nếu bạn dùng ComboBox
        java.time.LocalDate ngsinh = datePicker.getValue();
        String madv = maKhoa.getValue();
        String manld = getManld();
        String sql = "UPDATE DBA_MANAGER.NHANVIEN SET HOTEN = ?, LUONG = ?, PHUCAP = ?, DT = ?, NGSINH = ?, MADV = ? WHERE MANLD = ?";

        try{
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, hoten);
            pstmt.setDouble(2, luong);
            pstmt.setDouble(3, phucap);
            pstmt.setString(4, dienthoai);
            pstmt.setDate(5, java.sql.Date.valueOf(ngsinh));
            pstmt.setString(6, madv);
            pstmt.setString(7, manld);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Cập nhật nhân viên thành công.");
                controller.loadStaffData();
                showAlert("Cập nhật thành công");
            } else {
                System.out.println("Không tìm thấy nhân viên để cập nhật.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi: " + e.getMessage());
        }
    }

}
