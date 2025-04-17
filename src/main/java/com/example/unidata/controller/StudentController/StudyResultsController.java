package com.example.unidata.controller.StudentController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

public class StudyResultsController {

    @FXML
    private Button btnCourseRegistration;

    @FXML
    private Button btnSignOut;

    @FXML
    private Button btnStudyResults;

    @FXML
    private Button btnUserProfile;

    @FXML
    private TableColumn<?, ?> colAverage;

    @FXML
    private TableColumn<?, ?> colCode;

    @FXML
    private TableColumn<?, ?> colCredits;

    @FXML
    private TableColumn<?, ?> colFinal;

    @FXML
    private TableColumn<?, ?> colMidterm;

    @FXML
    private TableColumn<?, ?> colName;

    @FXML
    private TableColumn<?, ?> colPractice;

    @FXML
    private TableView<?> gradesTable;

    @FXML
    private Label lblAccumulatedCredits;

    @FXML
    private Label lblCumulativeGPA;

    @FXML
    private Label lblSemesterGPA;

    @FXML
    private Label lblTotalCredits;

    @FXML
    private AnchorPane mainContentPane;

    @FXML
    private ComboBox<?> semesterComboBox;

    @FXML
    private ComboBox<?> yearComboBox;

    @FXML
    void onCourseRegistration(ActionEvent event) {

    }

    @FXML
    void onProfile(ActionEvent event) {

    }

    @FXML
    void onSignOut(ActionEvent event) {

    }

    @FXML
    void onStudyResults(ActionEvent event) {

    }

    @FXML
    void onViewGrades(ActionEvent event) {

    }

}
