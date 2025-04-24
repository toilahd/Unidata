package com.example.unidata.controller.NVPKTController;

import com.example.unidata.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.example.unidata.controller.NVPKTController.utils.RegisteredList;

public class RegisteredListController implements Initializable {
    @FXML private Button btnSignOut;
    @FXML private Button btnUserProfile;
    @FXML private Button viewRegistered;

    @FXML private TableView<RegisteredList> tableDangKy;
    @FXML private TableColumn<RegisteredList, String> masv;
    @FXML private TableColumn<RegisteredList, String> mamm;
    @FXML private TableColumn<RegisteredList, Double> diemth;
    @FXML private TableColumn<RegisteredList, Double> diemquatrinh;
    @FXML private TableColumn<RegisteredList, Double> diemck;
    @FXML private TableColumn<RegisteredList, Double> diemtk;
    @FXML private TableColumn<RegisteredList, Button> thaotac;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addEditButtonToTable();
        loadData();
    }

    public void loadData() {
        ObservableList<RegisteredList> data = FXCollections.observableArrayList();
        String sql = "SELECT MASV, MAMM, DIEMTH, DIEMQT, DIEMCK, DIEMTK FROM DBA_MANAGER.DANGKY";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String masv = rs.getString("MASV");
                String mamm = rs.getString("MAMM");
                double diemth = rs.getDouble("DIEMTH");
                double diemqt = rs.getDouble("DIEMQT");
                double diemck = rs.getDouble("DIEMCK");
                double diemtongket = rs.getDouble("DIEMTK");

                RegisteredList item = new RegisteredList(masv, mamm, diemth, diemqt, diemck, diemtongket);
                data.add(item);
            }

            masv.setCellValueFactory(new PropertyValueFactory<>("masv"));
            mamm.setCellValueFactory(new PropertyValueFactory<>("mamm"));
            diemth.setCellValueFactory(new PropertyValueFactory<>("diemth"));
            diemquatrinh.setCellValueFactory(new PropertyValueFactory<>("diemquatrinh"));
            diemck.setCellValueFactory(new PropertyValueFactory<>("diemck"));
            diemtk.setCellValueFactory(new PropertyValueFactory<>("diemtongket"));



            tableDangKy.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi khi tải danh sách: " + e.getMessage());
        }
    }
    private void addEditButtonToTable() {
        thaotac.setCellFactory(param -> new TableCell<>() {
            final Button btn = new Button("Sửa");
            {
                btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                btn.setOnAction(event -> {
                    RegisteredList registerList = getTableView().getItems().get(getIndex());
                    openEditPopup(registerList);
                });
            }

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }

    private void openEditPopup(RegisteredList registeredList) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/unidata/PhanHe2/NV_PKTView/NV_PKT_UpdateGrades.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Chỉnh sửa điểm");

            UpdateGradesController controller = loader.getController();
            controller.ininitialize(registeredList, this);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onSignOut(ActionEvent event) {
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

    @FXML
    private void onProfile() {
        loadScene("/com/example/unidata/PhanHe2/NV_PKTView/NV_PKT_ProfileView.fxml", "Nhân viên phòng khảo thí - Profile", btnUserProfile);
    }
    @FXML
    private void onViewRegistered() {
        loadScene("/com/example/unidata/PhanHe2/NV_PKTView/NV_PKT_DangKyList.fxml", "Nhân viên phòng khảo thí - Danh sách đăng kí", viewRegistered);
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
}
