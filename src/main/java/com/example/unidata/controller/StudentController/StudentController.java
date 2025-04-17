package com.example.unidata.controller.StudentController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;

public class StudentController {

    @FXML
    private AnchorPane mainContentPane;

    // Load a view into the main content area
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            
            // Clear current content and add new view
            mainContentPane.getChildren().clear();
            mainContentPane.getChildren().add(view);
            
            // Make view fill the entire pane
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            
        } catch (IOException e) {
            e.printStackTrace();
            // You might want to show an error message to the user
        }
    }

    @FXML
    private void onProfile() {
        loadView("/com/example/unidata/PhanHe2/StudentProfileView.fxml");
    }

    @FXML
    private void onCourseRegistration() {
        loadView("/com/example/unidata/PhanHe2/CourseRegistrationView.fxml");
    }

    @FXML
    private void onStudyResults() {
        loadView("/com/example/unidata/PhanHe2/StudyResultsView.fxml");
        // You'll need to create this FXML file
    }

    @FXML
    private void onSignOut() {
        // Handle sign out logic
        System.out.println("User signed out");
        // You might want to navigate back to login screen
    }

    // This is called when the FXML is loaded
    @FXML
    private void initialize() {
        // Optional: Load a default view when the application starts
        loadView("/com/example/unidata/PhanHe2/UserProfileView.fxml");
    }
}