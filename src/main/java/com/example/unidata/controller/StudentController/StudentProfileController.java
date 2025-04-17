package com.example.unidata.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StudentProfileController {
    @FXML private Label lblStudentId;
    @FXML private Label lblFullName;
    @FXML private Label lblGender;
    @FXML private Label lblDOB;
    @FXML private Label lblAddress;
    @FXML private Label lblPhone;
    @FXML private Label lblFaculty;
    @FXML private Label lblStatus;

    @FXML
    private void initialize() {
        // Here you would load data from your database
        // For now, we use the placeholder data in the FXML
        
        // Example: If you had a StudentService to get the current student
        // Student student = studentService.getCurrentStudent();
        // lblStudentId.setText(student.getId());
        // lblFullName.setText(student.getFullName());
        // etc...
    }
    
    @FXML
    private void onEditProfile() {
        System.out.println("Edit profile clicked");
        // Implement edit functionality
    }
}