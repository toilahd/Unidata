package com.example.unidata.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class StudyResultsController {
    @FXML private ComboBox<String> semesterComboBox;
    @FXML private ComboBox<String> yearComboBox;
    @FXML private TableView<CourseGrade> gradesTable;
    @FXML private TableColumn<CourseGrade, String> colCode;
    @FXML private TableColumn<CourseGrade, String> colName;
    @FXML private TableColumn<CourseGrade, Integer> colCredits;
    @FXML private TableColumn<CourseGrade, Double> colPractice;
    @FXML private TableColumn<CourseGrade, Double> colMidterm;
    @FXML private TableColumn<CourseGrade, Double> colFinal;
    @FXML private TableColumn<CourseGrade, Double> colAverage;
    
    @FXML private Label lblTotalCredits;
    @FXML private Label lblSemesterGPA;
    @FXML private Label lblAccumulatedCredits;
    @FXML private Label lblCumulativeGPA;

    @FXML
    private void initialize() {
        // Initialize comboboxes
        semesterComboBox.getItems().addAll("Học kỳ 1", "Học kỳ 2", "Học kỳ 3");
        yearComboBox.getItems().addAll("2024-2025", "2025-2026", "2026-2027");
        
        // Set up table columns
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCredits.setCellValueFactory(new PropertyValueFactory<>("credits"));
        colPractice.setCellValueFactory(new PropertyValueFactory<>("practiceGrade"));
        colMidterm.setCellValueFactory(new PropertyValueFactory<>("midtermGrade"));
        colFinal.setCellValueFactory(new PropertyValueFactory<>("finalGrade"));
        colAverage.setCellValueFactory(new PropertyValueFactory<>("averageGrade"));
    }
    
    @FXML
    private void onViewGrades() {
        System.out.println("View grades clicked");
        // Here you would query grades based on selected semester and year
        // and update the summary statistics
    }
    
    // CourseGrade model class
    public static class CourseGrade {
        private String code;
        private String name;
        private int credits;
        private double practiceGrade;
        private double midtermGrade;
        private double finalGrade;
        private double averageGrade;
        
        // Constructor, getters, setters
        // ...
    }
}