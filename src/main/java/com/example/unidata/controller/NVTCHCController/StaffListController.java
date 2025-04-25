package com.example.unidata.controller.NVTCHCController;

import com.example.unidata.DatabaseConnection;
import com.example.unidata.controller.NVTCHCController.utils.NhanVien;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class StaffListController {
    @FXML private Button btnSignOut;
    @FXML private Button btnUserProfile;
    @FXML private Button btnStaffList;
    @FXML private Button btnAddStaff;

    @FXML private TableView<NhanVien> addAccounts_tableView;
    @FXML private TableColumn<NhanVien, Void> thaotac;
    @FXML private TableColumn<NhanVien, String> manld;
    @FXML private TableColumn<NhanVien, String> hoten;
    @FXML private TableColumn<NhanVien, String> phai;
    @FXML private TableColumn<NhanVien, LocalDate> ngsinh;
    @FXML private TableColumn<NhanVien, Double> luong;
    @FXML private TableColumn<NhanVien, Double> phucap;
    @FXML private TableColumn<NhanVien, String> dienthoai;
    @FXML private TableColumn<NhanVien, String> vaitro;
    @FXML private TableColumn<NhanVien, String> madv;


    @FXML
    public void initialize() {
        loadStaffData();
        addActionButtonsToTable();
    }

    public void loadStaffData() {
        ObservableList<NhanVien> nhanVienList = FXCollections.observableArrayList();

        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM DBA_MANAGER.NHANVIEN");

            while (rs.next()) {
                NhanVien nv = new NhanVien(
                        rs.getString("MANLD"),
                        rs.getString("HOTEN"),
                        rs.getString("PHAI"),
                        rs.getDate("NGSINH").toLocalDate(),
                        rs.getDouble("LUONG"),
                        rs.getDouble("PHUCAP"),
                        rs.getString("DT"),
                        rs.getString("VAITRO"),
                        rs.getString("MADV")
                );
                nhanVienList.add(nv);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Set cell value factories
        manld.setCellValueFactory(new PropertyValueFactory<>("manld"));
        hoten.setCellValueFactory(new PropertyValueFactory<>("hoten"));
        phai.setCellValueFactory(new PropertyValueFactory<>("phai"));
        ngsinh.setCellValueFactory(new PropertyValueFactory<>("ngsinh"));
        luong.setCellValueFactory(new PropertyValueFactory<>("luong"));
        phucap.setCellValueFactory(new PropertyValueFactory<>("phucap"));
        dienthoai.setCellValueFactory(new PropertyValueFactory<>("dienthoai"));
        vaitro.setCellValueFactory(new PropertyValueFactory<>("vaitro"));
        madv.setCellValueFactory(new PropertyValueFactory<>("madv"));

        addAccounts_tableView.setItems(nhanVienList);
    }


    private void addActionButtonsToTable() {
        thaotac.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("Sửa");
            private final Button btnDelete = new Button("Xóa");

            {
                btnEdit.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                btnEdit.setOnAction(event -> {
                    NhanVien nv = getTableView().getItems().get(getIndex());
                    openUpdateForm(nv);
                });

                btnDelete.setOnAction(event -> {
                    NhanVien nv = getTableView().getItems().get(getIndex());
                    deleteNhanVien(nv);
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

    private void deleteNhanVien(NhanVien nv) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc chắn muốn xóa nhân viên này không?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                var conn = DatabaseConnection.getConnection();
                var sql = "DELETE FROM DBA_MANAGER.NHANVIEN WHERE MANLD = ?";
                var stmt = conn.prepareStatement(sql);
                stmt.setString(1, nv.getManld());
                
                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    showAlert("Xóa nhân viên thành công!");
                    addAccounts_tableView.getItems().remove(nv);
                } else {
                    showAlert("Không thể xóa nhân viên.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Lỗi khi xóa nhân viên." + e.getMessage());
            }
        }
    }


    private void openUpdateForm(NhanVien nv) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/unidata/PhanHe2/NV_TCHCView/NV_TCHC_UpdateStaff.fxml"));
            AnchorPane pane = loader.load();

            // Truyền dữ liệu sang controller cập nhật
            UpdateStaffController controller = loader.getController();
            controller.setNhanVien(nv, this);

            Stage stage = new Stage();
            stage.setScene(new Scene(pane));
            stage.setTitle("Cập nhật nhân viên");
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

    @FXML public void onProfile() {
        loadScene("/com/example/unidata/PhanHe2/NV_TCHCView/NV_TCHC_ProfileView.fxml", "NVTCHC - Profile", btnUserProfile);
    }
    @FXML public void onStaffList() {
        loadScene("/com/example/unidata/PhanHe2/NV_TCHCView/NV_TCHC_StaffList.fxml", "NVTCHC - Danh sách nhân viên", btnStaffList);
    }
    @FXML public void onAddStaff() {
        loadScene("/com/example/unidata/PhanHe2/NV_TCHCView/NV_TCHC_AddStaff.fxml", "NVTCHC - Thêm nhân viên mới", btnAddStaff);
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
