package com.example.unidata.controller.NVPKTController;

import com.example.unidata.DatabaseConnection;
import com.example.unidata.controller.NVPKTController.utils.RegisteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javax.imageio.plugins.tiff.BaselineTIFFTagSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateGradesController {
    @FXML private Label masv;
    @FXML private Label mamm;
    @FXML private TextField diemth;
    @FXML private TextField diemquatrinh;
    @FXML private TextField diemcuoiki;
    @FXML private TextField diemtongket;
    @FXML private Button btnCapNhat;

    private RegisteredList currentItem;
    private RegisteredListController controller;

    @FXML
    void ininitialize(RegisteredList item, RegisteredListController controller) {
        btnCapNhat.setOnAction(this::onUpdateGrades);
        setUpdateGrade(item);
        this.controller = controller;
    }

    public void setUpdateGrade(RegisteredList item) {
        this.currentItem = item;

        this.controller = controller;

        masv.setText(item.getMasv());
        mamm.setText(item.getMamm());
        diemth.setText(String.valueOf(item.getDiemth()));
        diemquatrinh.setText(String.valueOf(item.getDiemquatrinh()));
        diemcuoiki.setText(String.valueOf(item.getDiemck()));
        diemtongket.setText(String.valueOf(item.getDiemtongket()));
    }

    @FXML
    private void onUpdateGrades(ActionEvent event) {
        try {
            // Lấy dữ liệu từ các ô nhập
            double th = Double.parseDouble(diemth.getText());
            double qt = Double.parseDouble(diemquatrinh.getText());
            double ck = Double.parseDouble(diemcuoiki.getText());
            double tk = Double.parseDouble(diemtongket.getText());

            // update data
            currentItem.setDiemth(th);
            currentItem.setDiemquatrinh(qt);
            currentItem.setDiemck(ck);
            currentItem.setDiemtongket(tk);

            String sql = """
                    UPDATE DBA_MANAGER.DANGKY
                    SET DIEMQT = ?, DIEMCK = ?, DIEMTH = ?, DIEMTK = ?
                    WHERE MASV = ? AND MAMM = ?
                    """;
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setDouble(1, qt);
            stmt.setDouble(2, ck);
            stmt.setDouble(3, th);
            stmt.setDouble(4, tk);
            stmt.setString(5, currentItem.getMasv());
            stmt.setString(6, currentItem.getMamm());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Cập nhật điểm thành công.");
                controller.loadData();
                showAlert("Cập nhật điểm thành công");
            } else {
                System.out.println("Không tìm thấy nhân viên để cập nhật.");
                showAlert("Không cập nhật được");
            }
            System.out.println("Điểm đã được cập nhật");
            System.out.println(currentItem);

//            diemth.getScene().getWindow().hide(); // close window

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
