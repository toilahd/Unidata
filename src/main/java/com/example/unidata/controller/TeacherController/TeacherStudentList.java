package com.example.unidata.controller.TeacherController;

import com.example.unidata.DatabaseConnection;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import com.example.unidata.controller.TeacherController.util.DanhSachSinhVien;

public class TeacherStudentList implements Initializable {
    @FXML private Button btnSignOut;
    @FXML private Button btnUserProfile;
    @FXML private Button btnClasses;
    @FXML private Button btnStudentsList;
    @FXML private Button btnResults;

    @FXML private TableView<DanhSachSinhVien> tableView;
    @FXML private TableColumn<DanhSachSinhVien, Integer> stt;
    @FXML private TableColumn<DanhSachSinhVien, String> hoTen;
    @FXML private TableColumn<DanhSachSinhVien, String> gioiTinh;
    @FXML private TableColumn<DanhSachSinhVien, String> tinhtrang;

    private void loadStudentData() {
        ObservableList<DanhSachSinhVien> data = FXCollections.observableArrayList();
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT HOTEN, GIOITINH, TINHTRANG FROM DBA_MANAGER.SINHVIEN");
            ResultSet rs = stmt.executeQuery();
            int i = 1;
            while (rs.next()) {
                data.add(new DanhSachSinhVien(
                        i++,
                        rs.getString("HOTEN"),
                        rs.getString("GIOITINH"),
                        rs.getString("TINHTRANG")
                ));
            }
            tableView.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
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
    public void onStudentsList(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/TeacherView/TeacherStudentList.fxml",
                "Danh sách sinh viên - Giáo Viên", btnStudentsList);
    }
    public void onStudyResults(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/TeacherView/TeacherScoreView.fxml",
                "Danh sách sinh viên - Giáo Viên", btnResults);
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
        // Gán cột với dữ liệu
        stt.setCellValueFactory(new PropertyValueFactory<>("stt"));
        hoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        gioiTinh.setCellValueFactory(new PropertyValueFactory<>("gioiTinh"));
        tinhtrang.setCellValueFactory(new PropertyValueFactory<>("tinhTrang"));

        loadStudentData(); // hàm tự viết để load dữ liệu từ CSDL
    }

}
