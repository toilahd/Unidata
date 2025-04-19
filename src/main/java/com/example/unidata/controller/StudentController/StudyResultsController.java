package com.example.unidata.controller.StudentController;

import com.example.unidata.DatabaseConnection;
import com.example.unidata.controller.StudentController.utils.StudyResults;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.unidata.controller.StudentController.utils.StudyResults;

public class StudyResultsController {

    @FXML private Button btnCourseRegistration;
    @FXML private Button btnSignOut;
    @FXML private Button btnStudyResults;
    @FXML private Button btnUserProfile;

    @FXML private TableColumn<StudyResults, String> maHP;
    @FXML private TableColumn<StudyResults, Integer> tenHP;
    @FXML private TableColumn<StudyResults, Integer> soTC;
    @FXML private TableColumn<StudyResults, Float> diemTH;
    @FXML private TableColumn<StudyResults, Float> diemQT;
    @FXML private TableColumn<StudyResults, Float> diemCK;
    @FXML private TableColumn<StudyResults, Float> diemTK;

    @FXML private TableView<StudyResults> gradesTable;


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
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
    public void onCourseRegistration(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/StudentView/CourseRegistrationView.fxml", "Courses Registration - Student", btnCourseRegistration);
    }

    @FXML
    public void onProfile(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/StudentView/StudentProfileView.fxml", "Profile - Student", btnUserProfile);
    }

    @FXML
    public void onStudyResults(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/StudentView/StudyResultsView.fxml", "Study Results - Student", btnStudyResults);
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

    @FXML
    public void initialize() {
        // Set up table columns
        maHP.setCellValueFactory(new PropertyValueFactory<>("mahp"));
        diemTH.setCellValueFactory(new PropertyValueFactory<>("diemth"));
        diemQT.setCellValueFactory(new PropertyValueFactory<>("diemqt"));
        diemCK.setCellValueFactory(new PropertyValueFactory<>("diemck"));
        diemTK.setCellValueFactory(new PropertyValueFactory<>("diemtk"));

        loadGrades();
    }

    private void loadGrades() {
        ObservableList<StudyResults> results = FXCollections.observableArrayList();

        try{
            Connection conn = DatabaseConnection.getConnection();
            String sql = """
                    SELECT MM.MAHP AS MAHP, DK.DIEMTH, DK.DIEMQT, DK.DIEMCK, DK.DIEMTK
                    FROM DBA_MANAGER.DANGKY DK
                    JOIN DBA_MANAGER.MOMON MM
                    ON DK.MAMM = MM.MAMM
                    """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String mahp = rs.getString("MAHP");
                float diemth = rs.getFloat("DIEMTH");
                float diemqt = rs.getFloat("DIEMQT");
                float diemck = rs.getFloat("DIEMCK");
                float diemtk = rs.getFloat("DIEMTK");

                results.add(new StudyResults(
                        mahp,
                        diemth,
                        diemqt,
                        diemck,
                        diemtk
                ));

            }

            gradesTable.setItems(results);


        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi khi tải dữ liệu kết quả học tập.");
        }
    }
}
