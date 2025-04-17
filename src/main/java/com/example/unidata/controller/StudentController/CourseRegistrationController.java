package com.example.unidata.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class CourseRegistrationController {
    @FXML private ComboBox<String> semesterComboBox;
    @FXML private ComboBox<String> yearComboBox;
    @FXML private TableView<Course> coursesTable;
    @FXML private TableColumn<Course, String> colCode;
    @FXML private TableColumn<Course, String> colName;
    @FXML private TableColumn<Course, Integer> colCredits;
    @FXML private TableColumn<Course, Integer> colLectures;
    @FXML private TableColumn<Course, Integer> colLabs;
    @FXML private TableColumn<Course, Boolean> colRegister;

    @FXML
    private void initialize() {
        // Initialize comboboxes
        semesterComboBox.getItems().addAll("Học kỳ 1", "Học kỳ 2", "Học kỳ 3");
        yearComboBox.getItems().addAll("2024-2025", "2025-2026", "2026-2027");
        
        // Set up table columns
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCredits.setCellValueFactory(new PropertyValueFactory<>("credits"));
        colLectures.setCellValueFactory(new PropertyValueFactory<>("lectures"));
        colLabs.setCellValueFactory(new PropertyValueFactory<>("labs"));
        colRegister.setCellValueFactory(new PropertyValueFactory<>("registered"));
        
        // Add checkbox column
        colRegister.setCellFactory(tc -> new CheckBoxTableCell<>());
    }
    
    @FXML
    private void onSearch() {
        System.out.println("Search clicked");
        // Here you would query courses based on selected semester and year
    }
    
    @FXML
    private void onRegister() {
        System.out.println("Register clicked");
        // Register selected courses logic
    }
    
    @FXML
    private void onUnregister() {
        System.out.println("Unregister clicked");
        // Unregister selected courses logic
    }
    
    // Course model class
    public static class Course {
        private String code;
        private String name;
        private int credits;
        private int lectures;
        private int labs;
        private boolean registered;
        
        // Constructor, getters, setters
        // ...
    }
}