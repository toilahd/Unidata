package com.example.unidata.controller.TeacherController;

import com.example.unidata.DatabaseConnection;
import com.example.unidata.controller.TeacherController.util.DanhSachDiem;
import com.example.unidata.controller.TeacherController.util.DanhSachSinhVien;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class TeacherViewStudyResults implements Initializable {
    @FXML private Button btnSignOut;
    @FXML private Button btnUserProfile;
    @FXML private Button btnClasses;
    @FXML private Button btnStudentsList;
    @FXML private Button btnResults;

    @FXML private TableView<DanhSachDiem> phanCong;
    @FXML private TableColumn<DanhSachDiem, Integer> stt;
    @FXML private TableColumn<DanhSachDiem, String> maSV;
    @FXML private TableColumn<DanhSachDiem, Float> diemThucHanh;
    @FXML private TableColumn<DanhSachDiem, Float> diemQuaTrinh;
    @FXML private TableColumn<DanhSachDiem, Float> diemCuoiKi;
    @FXML private TableColumn<DanhSachDiem, Float> diemTongKet;

    private void loadResults() {
        ObservableList<DanhSachDiem> data = FXCollections.observableArrayList();
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM DBA_MANAGER.DANGKY");
            ResultSet rs = stmt.executeQuery();
            int i = 1;
            while (rs.next()) {
                data.add(new DanhSachDiem(
                        i++,
                        rs.getString("MASV"),
                        rs.getFloat("DIEMTH"),
                        rs.getFloat("DIEMQT"),
                        rs.getFloat("DIEMCK"),
                        rs.getFloat("DIEMTK")
                ));
            }
            phanCong.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        stt.setCellValueFactory(new PropertyValueFactory<>("stt"));
        maSV.setCellValueFactory(new PropertyValueFactory<>("masv"));
        diemThucHanh.setCellValueFactory(new PropertyValueFactory<>("diemthuchanh"));
        diemQuaTrinh.setCellValueFactory(new PropertyValueFactory<>("diemquatrinh"));
        diemCuoiKi.setCellValueFactory(new PropertyValueFactory<>("diemcuoiki"));
        diemTongKet.setCellValueFactory(new PropertyValueFactory<>("diemtongket"));
        loadResults();
    }

}
