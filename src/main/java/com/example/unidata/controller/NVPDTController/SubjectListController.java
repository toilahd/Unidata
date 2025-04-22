package com.example.unidata.controller.NVPDTController;

import com.example.unidata.DatabaseConnection;
import com.example.unidata.controller.NVPDTController.utils.Subject;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SubjectListController implements Initializable {
    @FXML private Button btnSignOut;
    @FXML private Button btnUserProfile;
    @FXML private Button btnSubjectList;
    @FXML private Button btnAddSubject;
    @FXML private Button btnUpdateSubject;
    @FXML private Button btnDeleteSubject;
    @FXML private Button btnUpdateStudent;

    @FXML private TableView<Subject> tableMon;
    @FXML private TableColumn<Subject, String> mamm;
    @FXML private TableColumn<Subject, String> mahp;
    @FXML private TableColumn<Subject, String> magv;
    @FXML private TableColumn<Subject, Integer> nam;
    @FXML private TableColumn<Subject, Integer> hocki;


    private ObservableList<Subject> subjectList = FXCollections.observableArrayList();
    DeleteSubjectController deleteController = new DeleteSubjectController(); // Create or retrieve the controller instance

    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Bind columns to the data properties
        mamm.setCellValueFactory(new PropertyValueFactory<>("maMoMon"));
        mahp.setCellValueFactory(new PropertyValueFactory<>("maHocPhan"));
        magv.setCellValueFactory(new PropertyValueFactory<>("maGiangVien"));
        nam.setCellValueFactory(new PropertyValueFactory<>("nam"));
        hocki.setCellValueFactory(new PropertyValueFactory<>("hocKi"));

        loadData();
    }

    public void loadData() {
        subjectList.clear(); // clear existing data in list before loading new data
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM DBA_MANAGER.UV_NVPDT_MOMON";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Subject subject = new Subject(
                        rs.getString("MAMM"),
                        rs.getString("MAHP"),
                        rs.getString("MAGV"),
                        rs.getInt("HK"),
                        rs.getInt("NAM"),
                        deleteController
                );
                subjectList.add(subject);
            }

            tableMon.setItems(subjectList);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi khi tải dữ liệu từ cơ sở dữ liệu!");
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
