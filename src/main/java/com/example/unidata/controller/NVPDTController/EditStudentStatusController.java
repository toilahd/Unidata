package com.example.unidata.controller.NVPDTController;

import com.example.unidata.DatabaseConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import com.example.unidata.controller.NVPDTController.utils.Student;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditStudentStatusController {
    @FXML private Button btnSignOut;
    @FXML private Button btnUserProfile;
    @FXML private Button btnSubjectList;
    @FXML private Button btnAddSubject;
    @FXML private Button btnUpdateSubject;
    @FXML private Button btnDeleteSubject;
    @FXML private Button btnUpdateStudent;

    @FXML private TableView<Student> svTable;
    @FXML private TableColumn<Student, String> mssv;
    @FXML private TableColumn<Student, String> hoten;
    @FXML private TableColumn<Student, String> phai;
    @FXML private TableColumn<Student, String> ngaysinh;
    @FXML private TableColumn<Student, String> diachi;
    @FXML private TableColumn<Student, String> tinhtrang;
    @FXML private TableColumn<Student, String> khoa;
    @FXML private TableColumn<Student, String> dienthoai;

    ObservableList<Student> studentList = FXCollections.observableArrayList();

    // Load dữ liệu sinh viên vào TableView
    public ObservableList<Student> loadStudentData() {

        svTable.setItems(studentList);
        String sql = "SELECT * FROM DBA_MANAGER.SINHVIEN"; // SQL truy vấn để lấy dữ liệu sinh viên
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Student student = new Student(
                        rs.getString("MASV"),
                        rs.getString("HOTEN"),
                        rs.getString("PHAI"),
                        rs.getString("NGSINH"),
                        rs.getString("DCHI"),
                        rs.getString("TINHTRANG"),
                        rs.getString("KHOA"),
                        rs.getString("DT")
                );
                studentList.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studentList;
    }

    public void displayButtonUpdate() {
        tinhtrang.setCellFactory(param -> new TableCell<Student, String>() {
            private final ComboBox<String> comboBox = new ComboBox<>();

            {
                comboBox.getItems().addAll("Đang học", "Nghỉ học", "Bảo lưu");
                comboBox.setOnAction(event -> {
                    // Get the corresponding student for this row
                    Student student = getTableView().getItems().get(getIndex());
                    String newStatus = comboBox.getValue();

                    // Only call handleUpdate if the status has actually changed
                    if (!newStatus.equals(student.getTinhTrang())) {
                        handleUpdate(student.getMssv(), newStatus);
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    comboBox.setValue(item);

                    // Get the corresponding student for this row
                    Student student = getTableView().getItems().get(getIndex());

                    // Set the userData to store the student object
                    comboBox.setUserData(student);

                    setGraphic(comboBox);
                }
            }
        });
    }
    private void handleUpdate(String studentId, String newStatus) {
        // You can now directly use the studentId and newStatus
        String sql = "UPDATE DBA_MANAGER.SINHVIEN SET TINHTRANG = ? WHERE MASV = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newStatus);
            stmt.setString(2, studentId);  // Use the studentId directly here
            stmt.executeUpdate();

            // Show success message
            showAlert("Cập nhật thành công!");

            // Cập nhật trạng thái sinh viên trong ObservableList
            for (Student student : studentList) {
                if (student.getMssv().equals(studentId)) {
                    student.setTinhTrang(newStatus);  // Cập nhật trạng thái sinh viên
                    break;
                }
            }

            // Làm mới TableView để hiển thị dữ liệu đã thay đổi
            svTable.refresh();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Cập nhật thất bại!");
        }
    }

    @FXML
    private void initialize() {
        mssv.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMssv()));
        hoten.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getHoten()));
        phai.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGioitinh()));
        ngaysinh.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNgaysinh()));
        diachi.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDiachi()));
        tinhtrang.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTinhTrang()));
        khoa.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKhoa()));
        dienthoai.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDienthoai()));

        svTable.setItems(loadStudentData());
        displayButtonUpdate();
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
    public void onProfile(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/NV_PDTView/NV_PDT_ProfileView.fxml", "Courses Registration - Student", btnUserProfile);
    }
    @FXML
    public void onSubjectList(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/NV_PDTView/NV_PDT_SubjectList.fxml", "Courses Registration - Student", btnSubjectList);
    }
    @FXML
    public void onAddSubject(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/NV_PDTView/NV_PDT_AddSubject.fxml", "Courses Registration - Student", btnAddSubject);
    }
    @FXML
    public void onUpdateSubject(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/NV_PDTView/NV_PDT_UpdateSubject.fxml", "Courses Registration - Student", btnUpdateSubject);
    }
    @FXML
    public void onDeleteSubject(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/NV_PDTView/NV_PDT_DeleteSubject.fxml", "Courses Registration - Student", btnDeleteSubject);
    }
    @FXML
    public void onUpdateStudent(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/NV_PDTView/NV_PDT_CapNhatTinhTrang.fxml", "Courses Registration - Student", btnUpdateStudent);
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
