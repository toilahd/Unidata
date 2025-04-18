package com.example.unidata.controller.TeacherController;

import com.example.unidata.DatabaseConnection;
import com.example.unidata.controller.AdminController.util.PrivilegeData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.scene.control.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TeacherProfileController implements Initializable {

    @FXML
    private Button btnSignOut;
    @FXML
    private Button btnUserProfile;
    @FXML
    private Button btnClasses;
    @FXML
    private Button btnStudents;

    @FXML private Label MANLD;
    @FXML private Label HOTEN;
    @FXML private Label PHAI;
    @FXML private Label NGSINH;
    @FXML private Label LUONG;
    @FXML private Label PHUCAP;
    @FXML private Label MAKHOA;

    @FXML private TextField DT;
    @FXML private Button editProfile;


    private void loadTeacherData() {
        try {
            var conn = DatabaseConnection.getConnection();
            var stmt = conn.prepareStatement("SELECT * FROM GIAOVIEN WHERE MANLD = USER");
            var rs = stmt.executeQuery();

            if (rs.next()) {
                MANLD.setText(rs.getString("MANLD"));
                HOTEN.setText(rs.getString("HOTEN"));
                PHAI.setText(rs.getString("PHAI"));
                NGSINH.setText(rs.getDate("NGSINH").toString());
                LUONG.setText(rs.getString("LUONG"));
                PHUCAP.setText(rs.getString("PHUCAP"));
                DT.setText(rs.getString("DT"));
                MAKHOA.setText(rs.getString("MAKHOA"));
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEditProfile(ActionEvent event) {
        String newPhone = DT.getText().trim();

        if (!newPhone.matches("\\d{10,11}")) {
            showAlert("Số điện thoại không hợp lệ.");
            return;
        }

        try {
            var conn = DatabaseConnection.getConnection();
            var stmt = conn.prepareStatement("UPDATE GIAOVIEN SET DT = ? WHERE MANLD = USER");
            stmt.setString(1, newPhone);
            int rows = stmt.executeUpdate();

            stmt.close();
            conn.close();

            if (rows > 0) {
                showAlert("Cập nhật thành công.");
                loadTeacherData(); // Refresh view
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

    public void onProfile(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/TeacherView/TeacherProfileView.fxml",
                "Hồ sơ - Giáo Viên", btnUserProfile);
    }
    public void onClassAssigned(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/TeacherView/TeacherClass.fxml",
                "Danh sách lớp được phân công - Giáo Viên", btnClasses);
    }
    public void onStudentList(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/TeacherView/TeacherStudentList.fxml",
                "Danh sách sinh viên - Giáo Viên", btnStudents);
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
        loadTeacherData();
    }
}
