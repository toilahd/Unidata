package com.example.unidata.controller.TRGDVController;

import com.example.unidata.controller.TRGDVController.util.PhanCongData;

import com.example.unidata.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;

public class PhanCongController implements Initializable{
    @FXML
    private TableView<PhanCongData> addAccounts_tableView;

    @FXML
    private TableColumn<PhanCongData, String> addAccounts_col_MaMM;

    @FXML
    private TableColumn<PhanCongData, String> addAccounts_col_MaHP;

    @FXML
    private TableColumn<PhanCongData, String> addAccounts_col_MaGV;

    @FXML
    private TableColumn<PhanCongData, String> addAccounts_col_HK;

    @FXML
    private TableColumn<PhanCongData, Integer> addAccounts_col_Nam;

    private ObservableList<PhanCongData> dataList = FXCollections.observableArrayList();

    @FXML
    private Button btnCourseRegistration1;

    @FXML
    private Button btnSignOut;

    @FXML
    private Button btnUserProfile1;

    @FXML
    void onCourseRegistration(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/TRGDV_View/TRGDV_StaffList.fxml", "Staff List - TRGDV", btnCourseRegistration1);
    }

    @FXML
    void onProfile(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/TRGDV_View/TRGDV_ProfileView.fxml", "Profile - TRGDV", btnUserProfile1);
    }

    @FXML
    private void onSignOut(ActionEvent event) {
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

    private void loadScene(String fxmlFile, String title, Button button) {
        try {
            Stage stage = (Stage) button.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void setupTableColumns() {
        addAccounts_col_MaMM.setCellValueFactory(new PropertyValueFactory<>("maMM"));
        addAccounts_col_MaHP.setCellValueFactory(new PropertyValueFactory<>("maHP"));
        addAccounts_col_MaGV.setCellValueFactory(new PropertyValueFactory<>("maGV"));
        addAccounts_col_HK.setCellValueFactory(new PropertyValueFactory<>("hk"));
        addAccounts_col_Nam.setCellValueFactory(new PropertyValueFactory<>("nam"));
    }


    private void loadPhanCongData() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();

            // Use the view that's already filtered for the current department head
            String query = "SELECT * FROM DBA_MANAGER.UV_TRGDV_MOMON";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            dataList.clear();

            while (resultSet.next()) {
                PhanCongData nhanVien = new PhanCongData(
                        resultSet.getString("MAMM"),
                        resultSet.getString("MAHP"),
                        resultSet.getString("MAGV"),
                        resultSet.getInt("HK"),
                        resultSet.getInt("NAM")
                );
                dataList.add(nhanVien);
            }

            addAccounts_tableView.setItems(dataList);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error loading employee data: " + e.getMessage());
            e.printStackTrace();
        } finally {

        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadPhanCongData();
    }
}