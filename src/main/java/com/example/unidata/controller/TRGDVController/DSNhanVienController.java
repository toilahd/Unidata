package com.example.unidata.controller.TRGDVController;

import com.example.unidata.controller.TRGDVController.util.NhanVienData;

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

public class DSNhanVienController implements Initializable{
    @FXML
    private TableView<NhanVienData> addAccounts_tableView;
    @FXML
    private TableColumn<NhanVienData, String> addAccounts_col_id;
    @FXML
    private TableColumn<NhanVienData, String> addAccounts_col_username;
    @FXML
    private TableColumn<NhanVienData, String> addAccounts_col_role;
    @FXML
    private TableColumn<NhanVienData, Date> addAccounts_col_ngsinh;
    @FXML
    private TableColumn<NhanVienData, String> addAccounts_col_dt;
    @FXML
    private TableColumn<NhanVienData, String> addAccounts_col_vaitro;
    @FXML
    private TableColumn<NhanVienData, String> addAccounts_col_madv;

    private ObservableList<NhanVienData> nhanVienList = FXCollections.observableArrayList();

    @FXML
    private Button btnPhanCong;

    @FXML
    private Button btnSignOut;

    @FXML
    private Button btnUserProfile;

    @FXML
    void onPhanCong(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/TRGDV_View/TRGDV_PhanCong.fxml", "Phan Cong - TRGDV", btnUserProfile);
    }

    @FXML
    void onProfile(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/TRGDV_View/TRGDV_ProfileView.fxml", "Profile - TRGDV", btnUserProfile);
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
        addAccounts_col_id.setCellValueFactory(new PropertyValueFactory<>("manld"));
        addAccounts_col_username.setCellValueFactory(new PropertyValueFactory<>("hoten"));
        addAccounts_col_role.setCellValueFactory(new PropertyValueFactory<>("phai"));
        addAccounts_col_ngsinh.setCellValueFactory(new PropertyValueFactory<>("ngsinh"));
        addAccounts_col_dt.setCellValueFactory(new PropertyValueFactory<>("dt"));
        addAccounts_col_vaitro.setCellValueFactory(new PropertyValueFactory<>("vaitro"));
        addAccounts_col_madv.setCellValueFactory(new PropertyValueFactory<>("madv"));
    }


    private void loadEmployeeData() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();

            // Use the view that's already filtered for the current department head
            String query = "SELECT * FROM UV_TRGDV_NV";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            nhanVienList.clear();

            while (resultSet.next()) {
                NhanVienData nhanVien = new NhanVienData(
                        resultSet.getString("MANLD"),
                        resultSet.getString("HOTEN"),
                        resultSet.getString("PHAI"),
                        resultSet.getDate("NGSINH"),
                        resultSet.getString("DT"),
                        resultSet.getString("VAITRO"),
                        resultSet.getString("MADV")
                );
                nhanVienList.add(nhanVien);
            }

            addAccounts_tableView.setItems(nhanVienList);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error loading employee data: " + e.getMessage());
            e.printStackTrace();
        } finally {

        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadEmployeeData();
    }
}