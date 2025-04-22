package com.example.unidata.controller.StudentController;

import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.sql.*;
import com.example.unidata.DatabaseConnection;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class StudentProfileController implements Initializable {
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
    @FXML
    private Button editProfile;


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
    public void onCourseRegistration(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/StudentView/CourseRegistrationView.fxml", "Courses Registration - Student");
    }

    @FXML
    public void onProfile(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/StudentView/StudentProfileView.fxml", "Profile - Student");
    }

    @FXML
    public void onStudyResults(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/StudentView/StudyResultsView.fxml", "Study Results - Student");
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
                SELECT * FROM DBA_MANAGER.SINHVIEN
                """;

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("DEBUG: Found student data for " + rs.getString("MASV"));

                MaSV.setText(rs.getString("MASV"));
                HOTEN.setText(rs.getString("HOTEN"));
                PHAI.setText(rs.getString("PHAI"));
                NGSINH.setText(rs.getString("NGSINH"));
                DIACHI.setText(rs.getString("DCHI"));
                DT.setText(rs.getString("DT"));
                KHOA.setText(rs.getString("KHOA"));
                TINHTRANG.setText(rs.getString("TINHTRANG"));
            } else {
                System.out.println("DEBUG: No student data returned for current user.");
            }

        } catch (SQLException e) {
            Alert alert;
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Cannot fetch student data!");
            alert.showAndWait();
            e.printStackTrace();
            Platform.exit();
        }
    }
    @FXML
    public void onEditProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/unidata/PhanHe2/StudentView/EditStudentProfileView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Updating user profile");
            stage.setScene(new Scene(loader.load()));

            // Get controller and set data
            EditStudentProfileController controller = loader.getController();
            controller.setStudentData(
                    MaSV.getText(), HOTEN.getText(), PHAI.getText(),
                    NGSINH.getText(), DIACHI.getText(), DT.getText(),
                    KHOA.getText(), TINHTRANG.getText()
            );
            // Set the callback so that after update, the main profile is refreshed.
            controller.setOnProfileUpdated(() -> {
                // This callback gets executed after the update is successful
                loadStudentInfo();  // Reload data from the DB into the main profile view.
            });
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void initialize(URL url, ResourceBundle resourceBundle) {
        setStudentUsername(DatabaseConnection.getCurrentUsername());
        if (studentUsername != null) {
            loadStudentInfo();
        }
    }
}
