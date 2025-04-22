package com.example.unidata.controller.NVPDTController;

import com.example.unidata.DatabaseConnection;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddSubjectController {
    @FXML private Button btnSignOut;
    @FXML private Button btnUserProfile;
    @FXML private Button btnSubjectList;
    @FXML private Button btnAddSubject;
    @FXML private Button btnUpdateSubject;
    @FXML private Button btnDeleteSubject;
    @FXML private Button btnUpdateStudent;

    @FXML private ComboBox<String> cbMaHP;
    @FXML private ComboBox<String> cbMaGV;
    @FXML private ComboBox<String> cbHocKi;
    @FXML private TextField txtNam;
    @FXML private Button btnThem;

    private ObservableList<String> hocKiList = FXCollections.observableArrayList("1", "2", "3");

    @FXML
    private void initialize() {
        cbHocKi.setItems(hocKiList);
        query("SELECT MAHP FROM DBA_MANAGER.HOCPHAN", cbMaHP);
        query("SELECT MANLD FROM DBA_MANAGER.NHANVIEN WHERE VAITRO = 'GV'", cbMaGV);
    }

    private void query (String query, ComboBox<String> comboBox) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            ObservableList<String> list = FXCollections.observableArrayList();
            while (rs.next()) {
                list.add(rs.getString(1));
            }
            comboBox.setItems(list);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void addSubject(ActionEvent event) {
        String maHP = cbMaHP.getValue();
        String maGV = cbMaGV.getValue();
        String hocKi = cbHocKi.getValue();
        String nam = txtNam.getText();

        if (maHP == null || maGV == null || hocKi == null || nam.isEmpty()) {
            showAlert("Vui lòng điền đầy đủ thông tin!");
            return;
        }
        String selectSQL = "SELECT MAX(MAMM) FROM DBA_MANAGER.UV_NVPDT_MOMON";
        String insertSQL = "INSERT INTO DBA_MANAGER.UV_NVPDT_MOMON VALUES (?, ?, ?, ?, ?)";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement selectStmt = conn.prepareStatement(selectSQL);
            ResultSet rs = selectStmt.executeQuery();
            String newMaMM = "MM001"; // default if table is empty

            if (rs.next()) {
                String maxMaMM = rs.getString(1);
                if (maxMaMM != null) {
                    int num = Integer.parseInt(maxMaMM.substring(2));
                    num++; // increase to 13
                    newMaMM = String.format("MM%03d", num); // format to MM013
                }
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                insertStmt.setString(1, newMaMM);
                insertStmt.setString(2, maHP);
                insertStmt.setString(3, maGV);
                insertStmt.setString(4, hocKi);
                insertStmt.setString(5, nam);

                int rowsAffected = insertStmt.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert("Thêm môn học thành công!");
                } else {
                    showAlert("Lỗi khi thêm môn học!");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Có lỗi xảy ra khi thêm môn học!");
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



    @FXML
    public void onProfile(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/NV_PDTView/NV_PDT_ProfileView.fxml", "Courses Registration - Student", btnUserProfile);
    }
    @FXML
    public void onSubjectList(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/NV_PDTView/NV_PDT_SubjectList.fxml", "Courses Registration - Student", btnSubjectList);
    }
    @FXML
    public void onAddSubject(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/NV_PDTView/NV_PDT_AddSubject.fxml", "Courses Registration - Student", btnAddSubject);
    }
    @FXML
    public void onUpdateSubject(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/NV_PDTView/NV_PDT_UpdateSubject.fxml", "Courses Registration - Student", btnUpdateSubject);
    }
    @FXML
    public void onDeleteSubject(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/NV_PDTView/NV_PDT_DeleteSubject.fxml", "Courses Registration - Student", btnDeleteSubject);
    }
    @FXML
    public void onUpdateStudent(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/NV_PDTView/NV_PDT_CapNhatTinhTrang.fxml", "Courses Registration - Student", btnUpdateStudent);
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
