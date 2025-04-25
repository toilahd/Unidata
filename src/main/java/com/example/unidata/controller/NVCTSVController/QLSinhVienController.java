package com.example.unidata.controller.NVCTSVController;

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
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.stage.Modality;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import com.example.unidata.controller.NVCTSVController.utils.SinhVien;

public class QLSinhVienController implements Initializable {

    @FXML private TableView<SinhVien> studentTable;
    @FXML private TableColumn<SinhVien, String> colStudentId;
    @FXML private TableColumn<SinhVien, String> colFullName;
    @FXML private TableColumn<SinhVien, String> colGender;
    @FXML private TableColumn<SinhVien, Date> colDOB;
    @FXML private TableColumn<SinhVien, String> colAddress;
    @FXML private TableColumn<SinhVien, String> colPhone;
    @FXML private TableColumn<SinhVien, String> colFaculty;
    @FXML private TableColumn<SinhVien, String> colStatus;
    @FXML private TableColumn<SinhVien, Void> colAction;

    @FXML private Button btnSignOut;
    @FXML private Button btnUserProfile1;
    @FXML private Button btnQuanLySinhVien;
    @FXML private Button btnThemSinhVien;

    private ObservableList<SinhVien> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupColumns();
        loadStudentData();
        addActionButtonsToTable();
    }

    private void setupColumns() {
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("maSV"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("phai"));
        colDOB.setCellValueFactory(new PropertyValueFactory<>("ngaySinh"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("diaChi"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("dienThoai"));
        colFaculty.setCellValueFactory(new PropertyValueFactory<>("khoa"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("tinhTrang"));
    }

    private void loadStudentData() {
        data.clear();
        String sql = "SELECT MASV, HOTEN, PHAI, NGSINH, DCHI, DT, KHOA, TINHTRANG FROM DBA_MANAGER.SINHVIEN";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                SinhVien sv = new SinhVien(
                        rs.getString("MASV"),
                        rs.getString("HOTEN"),
                        rs.getString("PHAI"),
                        rs.getDate("NGSINH"),
                        rs.getString("DCHI"),
                        rs.getString("DT"),
                        rs.getString("KHOA"),
                        rs.getString("TINHTRANG")
                );

                data.add(sv);
            }
            studentTable.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    private void addActionButtonsToTable() {
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("Sửa");
            private final Button btnDelete = new Button("Xóa");

            {
                btnEdit.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                btnEdit.setOnAction(event -> {
                    SinhVien sv = getTableView().getItems().get(getIndex());
                    openEditPopup(sv);
                });

                btnDelete.setOnAction(event -> {
                    SinhVien sv = getTableView().getItems().get(getIndex());
                    deleteSinhVien(sv);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hBox = new HBox(5, btnEdit, btnDelete);
                    setGraphic(hBox);
                }
            }
        });
    }

    private void openEditPopup(SinhVien sv) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/unidata/PhanHe2/NVCTSV_View/NVCTSV_EditSinhVien.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the student data
            EditSinhVienController controller = loader.getController();
            controller.initData(sv, this);

            Stage stage = new Stage();
            stage.setTitle("Cập nhật Sinh Viên");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh table after editing
            loadStudentData();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Không thể mở form cập nhật sinh viên: " + e.getMessage());
        }
    }

    private void deleteSinhVien(SinhVien sv) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc chắn muốn xóa sinh viên này không?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                Connection conn = DatabaseConnection.getConnection();
                String sql = "DELETE FROM DBA_MANAGER.SINHVIEN WHERE MASV = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, sv.getMaSV());

                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    showAlert("Xóa sinh viên thành công!");
                    studentTable.getItems().remove(sv);
                } else {
                    showAlert("Không thể xóa sinh viên.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Lỗi khi xóa sinh viên: " + e.getMessage());
            }
        }
    }

    public void refreshData() {
        loadStudentData();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    public void onSignOut(ActionEvent event) {
        try {
            DatabaseConnection.logout();
            Stage stage = (Stage) btnSignOut.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/unidata/login.fxml"));
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Đăng nhập");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onProfile() {
        loadScene("/com/example/unidata/PhanHe2/NVCTSVView/NVCTSV_ProfileView.fxml", "Thông tin cá nhân", btnUserProfile1);
    }

    @FXML
    public void onQuanLySinhVien() {
        loadScene("/com/example/unidata/PhanHe2/NVCTSV_View/NVCTSV_QLSV.fxml", "Quản lý sinh viên", btnQuanLySinhVien);
    }

    @FXML
    public void onThemSinhVien() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/unidata/PhanHe2/NVCTSV_View/NVCTSV_AddSinhVien.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Thêm Sinh Viên");
            stage.setScene(new Scene(root));
            // Make it a modal dialog (blocks interaction with parent window)
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait(); // Using showAndWait() to wait for dialog to close

            // Refresh table after adding
            loadStudentData();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Không thể mở form thêm sinh viên: " + e.getMessage());
        }
    }

    private void loadScene(String fxmlPath, String title, Button sourceButton) {
        try {
            Stage stage = (Stage) sourceButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}