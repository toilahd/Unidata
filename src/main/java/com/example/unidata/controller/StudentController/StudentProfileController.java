package com.example.unidata.controller.StudentController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.sql.*;
import com.example.unidata.DatabaseConnection;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;


public class StudentProfileController {

    @FXML
    private Label DIACHI;
    @FXML
    private Label DT;
    @FXML
    private Label HOTEN;
    @FXML
    private Label KHOA;
    @FXML
    private Label MaSV;
    @FXML
    private Label NGSINH;
    @FXML
    private Label PHAI;
    @FXML
    private Label TINHTRANG;

    @FXML
    private Button btnCourseRegistration;
    @FXML
    private Button btnSignOut;
    @FXML
    private Button btnStudyResults;
    @FXML
    private Button btnUserProfile;

    @FXML
    private AnchorPane mainContentPane;

    // variables
    private String studentUsername;
    public void setStudentUsername(String studentUsername) {
        this.studentUsername = studentUsername;
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
    void onEditProfile(ActionEvent event) {
    }

    @FXML
    void onCourseRegistration(ActionEvent event) {
        loadScene("com/example/unidata/PhanHe2/StudentView/CourseRegistrationView.fxml", "Courses Registration - Student");
    }

    @FXML
    void onProfile(ActionEvent event) {
        loadScene("com/example/unidata/PhanHe2/StudentView/StudentProfileView.fxml", "Profile - Student");
    }

    @FXML
    void onStudyResults(ActionEvent event) {
        loadScene("com/example/unidata/controller/StudentController/StudyResultsController.java", "Study Results - Student");
    }
    private void loadScene(String fxmlFile, String title) {
        try {
            Stage stage = (Stage) btnSignOut.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // loading profile data
    public void loadStudentInfo() {
        String query = """
                SELECT SV.MASV, SV.HOTEN, SV.PHAI, TO_CHAR(SV.NGSINH, 'DD/MM/YYYY') AS NGSINH,
                       SV.DIACHI, SV.DT, DV.TENDV AS KHOA, SV.TINHTRANG
                FROM SINHVIEN SV
                JOIN DONVI DV ON SV.MADV = DV.MADV
                WHERE SV.MASV = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, studentUsername);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                MaSV.setText(rs.getString("MASV"));
                HOTEN.setText(rs.getString("HOTEN"));
                PHAI.setText(rs.getString("PHAI"));
                NGSINH.setText(rs.getString("NGSINH"));
                DIACHI.setText(rs.getString("DIACHI"));
                DT.setText(rs.getString("DT"));
                KHOA.setText(rs.getString("KHOA"));
                TINHTRANG.setText(rs.getString("TINHTRANG"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // optionally display error in UI
        }
    }


    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (studentUsername != null) {
            loadStudentInfo();
        }
    }
}
