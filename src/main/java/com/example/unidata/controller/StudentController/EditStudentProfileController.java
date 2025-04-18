package com.example.unidata.controller.StudentController;

import com.example.unidata.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditStudentProfileController {

    @FXML private Label tfMaSV;
    @FXML private Label tfHoTen;
    @FXML private Label tfPhai;
    @FXML private Label tfNgaySinh;
    @FXML private TextField tfDiaChi;
    @FXML private TextField tfDienThoai;
    @FXML private Label tfKhoa;
    @FXML private Label tfTinhTrang;

    private String originalMaSV;

    private Runnable onProfileUpdated;
    public void setOnProfileUpdated(Runnable callback) {
        this.onProfileUpdated = callback;
    }

    public void setStudentData(String maSV, String hoten, String phai, String ngsinh,
                               String diachi, String dt, String khoa, String tinhtrang) {
        originalMaSV = maSV;
        tfMaSV.setText(maSV);
        tfHoTen.setText(hoten);
        tfPhai.setText(phai);
        tfNgaySinh.setText(ngsinh);
        tfDiaChi.setText(diachi);
        tfDienThoai.setText(dt);
        tfKhoa.setText(khoa);
        tfTinhTrang.setText(tinhtrang);
    }

    @FXML
    public void onSave() {
        String updateQuery = """
                UPDATE DBA_MANAGER.SINHVIEN
                SET DCHI=?, DT=?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setString(1, tfDiaChi.getText());
            stmt.setString(2, tfDienThoai.getText());


            int updated = stmt.executeUpdate();
            if (updated > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setContentText("update Successfully!");
                alert.showAndWait();

                // Notify main window to refresh data
                if (onProfileUpdated != null) {
                    onProfileUpdated.run();
                }

                ((Stage) tfMaSV.getScene().getWindow()).close(); // Close popup
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setContentText("Cannot update user profile!");
            alert.showAndWait();
        }
    }
}
