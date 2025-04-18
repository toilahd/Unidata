package com.example.unidata.controller.StudentController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.io.IOException;
import java.sql.*;
import com.example.unidata.DatabaseConnection;
import javafx.stage.Stage;

public class CourseRegistrationController {

    @FXML
    private Button btnCourseRegistration;
    @FXML
    private Button btnSignOut;
    @FXML
    private Button btnStudyResults;

    @FXML
    private Button btnUserProfile;

    @FXML
    private TableColumn<?, ?> colCode1;

    @FXML
    private TableColumn<?, ?> colCredits1;

    @FXML
    private TableColumn<?, ?> colLabs;

    @FXML
    private TableColumn<?, ?> colLectures;

    @FXML
    private TableColumn<?, ?> colName1;

    @FXML
    private TableColumn<?, ?> colRegister;

    @FXML
    private TableView<?> coursesTable;

    @FXML
    private ComboBox<?> semesterComboBox1;

    @FXML
    private ComboBox<?> yearComboBox1;


    @FXML
    void onRegister(ActionEvent event) {

    }

    @FXML
    void onSearch(ActionEvent event) {

    }

    @FXML
    void onSignOut(ActionEvent event) {

    }

    @FXML
    void onUnregister(ActionEvent event) {

    }


    @FXML
    public void onCourseRegistration(ActionEvent event) {
        loadScene("com/example/unidata/PhanHe2/StudentView/CourseRegistrationView.fxml", "Courses Registration - Student");
    }

    @FXML
    public void onProfile(ActionEvent event) {
        loadScene("com/example/unidata/PhanHe2/StudentView/StudentProfileView.fxml", "Profile - Student");
    }

    @FXML
    public void onStudyResults(ActionEvent event) {
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


}
