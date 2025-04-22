package com.example.unidata.controller.NVPDTController.utils;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import com.example.unidata.DatabaseConnection;
import java.sql.*;
import com.example.unidata.controller.NVPDTController.DeleteSubjectController;

public class Subject {
    private final SimpleStringProperty maMoMon;
    private final SimpleStringProperty maHocPhan;
    private final SimpleStringProperty maGiangVien;
    private final SimpleIntegerProperty nam;
    private final SimpleIntegerProperty hocKi;
    private Button deleteButton;
    DeleteSubjectController controller = new DeleteSubjectController();

    public Subject(String maMoMon, String maHocPhan, String maGiangVien, int nam, int hocKi, DeleteSubjectController controller) {
        this.maMoMon = new SimpleStringProperty(maMoMon);
        this.maHocPhan = new SimpleStringProperty(maHocPhan);
        this.maGiangVien = new SimpleStringProperty(maGiangVien);
        this.nam = new SimpleIntegerProperty(nam);
        this.hocKi = new SimpleIntegerProperty(hocKi);

        this.deleteButton = new Button("Xóa");
        this.controller = controller;
        if (controller != null) {
            this.deleteButton.setOnAction(event -> deleteSubject());
        } else {
            this.deleteButton.setOnAction(event -> System.out.println("No controller for deletion"));
        }
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    private void deleteSubject() {
        String sql = "DELETE FROM DBA_MANAGER.UV_NVPDT_MOMON WHERE MAMM = ?";

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maMoMon.get());
            int rowsAffected = stmt.executeUpdate();

            // Kiểm tra nếu xóa thành công
            if (rowsAffected > 0) {
                System.out.println("Subject deleted with MAMM: " + maMoMon.get()); // In ra console để kiểm tra
                // Hiển thị thông báo trong UI thread
                    showAlert("Subject with MAMM " + maMoMon.get() + " deleted successfully.");
                if (controller != null) {
                    controller.loadData();
                    System.out.println("Load data in controller");
                }
            } else {
                showAlert("No subject found with MAMM: " + maMoMon.get());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Xử lý lỗi trong UI thread
            Platform.runLater(() -> showAlert("There is something wrong with the delete operation."));
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public String getMaMoMon() {
        return maMoMon.get();
    }

    public StringProperty maMoMonProperty() {
        return maMoMon;
    }

    public String getMaHocPhan() {
        return maHocPhan.get();
    }

    public StringProperty maHocPhanProperty() {
        return maHocPhan;
    }

    public String getMaGiangVien() {
        return maGiangVien.get();
    }

    public StringProperty maGiangVienProperty() {
        return maGiangVien;
    }

    public int getNam() {
        return nam.get();
    }

    public IntegerProperty namProperty() {
        return nam;
    }

    public int getHocKi() {
        return hocKi.get();
    }

    public IntegerProperty hocKiProperty() {
        return hocKi;
    }
}
