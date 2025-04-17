package com.example.unidata.controller.StudentController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.sql.*;
import com.example.unidata.DatabaseConnection;

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
    void onCourseRegistration(ActionEvent event) {

    }

    @FXML
    void onProfile(ActionEvent event) {

    }

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
    void onStudyResults(ActionEvent event) {

    }

    @FXML
    void onUnregister(ActionEvent event) {

    }

}
