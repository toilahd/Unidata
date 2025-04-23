package com.example.unidata;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Unidata");
        stage.setScene(scene);
        stage.show();
    }
    // NVPDT_NVPDT001
    // NVTCHC_NVTCHC001
    public static void main(String[] args) {
        launch();
    }
}