package com.example.unidata.controller.StudentController;

import com.example.unidata.controller.StudentController.utils.Course;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import com.example.unidata.DatabaseConnection;
import javafx.stage.Stage;

public class CourseRegistrationController implements Initializable {

    @FXML
    private Button btnCourseRegistration;
    @FXML
    private Button btnSignOut;
    @FXML
    private Button btnStudyResults;
    @FXML
    private Button btnUserProfile;

    @FXML private TableView<Course> coursesTable;
    @FXML private TableColumn<Course, String> colCode1, colName1;
    @FXML private TableColumn<Course, Integer> colCredits1, colLectures, colLabs;
    @FXML private TableColumn<Course, Void> colRegister;

    @FXML private TableView<Course> coursesTable1;
    @FXML private TableColumn<Course, String> colCode11, colName11;
    @FXML private TableColumn<Course, Integer> colCredits11, colLectures1, colLabs1;
    @FXML private TableColumn<Course, Void> colRegister1;

    private String studentId = DatabaseConnection.getMasvFromLogin(); // giả định bạn có user đang đăng nhập


    @FXML
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
    public void onCourseRegistration(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/StudentView/CourseRegistrationView.fxml", "Courses Registration - Student", btnCourseRegistration);
    }

    @FXML
    public void onProfile(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/StudentView/StudentProfileView.fxml", "Profile - Student", btnUserProfile);
    }

    @FXML
    public void onStudyResults(ActionEvent event) {
        loadScene("/com/example/unidata/PhanHe2/StudentView/StudyResultsView.fxml", "Study Results - Student", btnStudyResults);
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

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setupTableColumns() {
        colCode1.setCellValueFactory(new PropertyValueFactory<>("code"));
        colName1.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCredits1.setCellValueFactory(new PropertyValueFactory<>("credits"));
        colLectures.setCellValueFactory(new PropertyValueFactory<>("lectures"));
        colLabs.setCellValueFactory(new PropertyValueFactory<>("labs"));

        colCode11.setCellValueFactory(new PropertyValueFactory<>("code"));
        colName11.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCredits11.setCellValueFactory(new PropertyValueFactory<>("credits"));
        colLectures1.setCellValueFactory(new PropertyValueFactory<>("lectures"));
        colLabs1.setCellValueFactory(new PropertyValueFactory<>("labs"));
    }

    private void loadRegisteredCourses() {
        coursesTable.getItems().clear();
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = """
                    SELECT UV_SV_MOMON.MAMM, HOCPHAN.MAHP, HOCPHAN.TENHP, HOCPHAN.SOTC, HOCPHAN.STLT, HOCPHAN.STTH
                    FROM DBA_MANAGER.DANGKY
                    JOIN DBA_MANAGER.UV_SV_MOMON ON DANGKY.MAMM = UV_SV_MOMON.MAMM
                    JOIN DBA_MANAGER.HOCPHAN ON UV_SV_MOMON.MAHP = HOCPHAN.MAHP
                    """;
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Course course = new Course(
                        rs.getString("MAHP"),
                        rs.getString("TENHP"),
                        rs.getInt("SOTC"),
                        rs.getInt("STLT"),
                        rs.getInt("STTH")
                );
                course.setOpenCourseId(rs.getString("MAMM"));
                coursesTable.getItems().add(course);
            }
            addDeleteButtonToTable();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi load môn đã đăng ký: " + e.getMessage());
        }
    }

    private void loadUnregisteredCourses() {
        coursesTable1.getItems().clear();
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = """
                SELECT U.MAMM, H.MAHP, H.TENHP, H.SOTC, H.STLT, H.STTH
                FROM DBA_MANAGER.HOCPHAN H
                JOIN DBA_MANAGER.UV_SV_MOMON U ON H.MAHP = U.MAHP
                WHERE H.MAHP NOT IN (
                    SELECT H2.MAHP
                    FROM DBA_MANAGER.DANGKY D
                    JOIN DBA_MANAGER.UV_SV_MOMON U2 ON D.MAMM = U2.MAMM
                    JOIN DBA_MANAGER.HOCPHAN H2 ON U2.MAHP = H2.MAHP
                )
            """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println("MAHP: " + rs.getString("MAHP"));  // kiểm tra có dòng không
                Course course = new Course(
                        rs.getString("MAHP"),
                        rs.getString("TENHP"),
                        rs.getInt("SOTC"),
                        rs.getInt("STLT"),
                        rs.getInt("STTH")
                );
                course.setOpenCourseId(rs.getString("MAMM"));

                coursesTable1.getItems().add(course);
            }
            addRegisterButtonToTable();
        } catch (SQLException e) {
            showAlert("Lỗi SQL khi tải khóa học chưa đăng ký: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addDeleteButtonToTable() {
        colRegister.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Xóa");

            {
                btn.setOnAction(event -> {
                    Course course = getTableView().getItems().get(getIndex());
                    unregisterCourse(course);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(btn);
            }
        });
    }

    private void addRegisterButtonToTable() {
        colRegister1.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Đăng ký");

            {
                btn.setOnAction(event -> {
                    Course course = getTableView().getItems().get(getIndex());
                    registerCourse(course);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(btn);
            }
        });
    }

    private void registerCourse(Course course) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO DBA_MANAGER.DANGKY(MASV, MAMM) VALUES(?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, studentId);
            ps.setString(2, course.getOpenCourseId()); // dùng MAMM
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Không có bản ghi nào được tạo ra. Có thể do chính sách VPD.");
            } else {
                System.out.println("Đã đăng kí thành công.");
                loadRegisteredCourses();
                loadUnregisteredCourses();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi khi đăng ký khóa học: " + e.getMessage());
        }
    }

    private void unregisterCourse(Course course) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM DBA_MANAGER.DANGKY WHERE MASV = ? AND MAMM = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, studentId);
            ps.setString(2, course.getOpenCourseId()); // dùng MAMM
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                showAlert("Không có bản ghi nào bị xóa. Có thể do chính sách VPD.");

            } else {
                showAlert("Đã xóa thành công.");
                loadRegisteredCourses();
                loadUnregisteredCourses();
            }
        } catch (SQLException e) {
            // Kiểm tra nếu có lỗi liên quan đến VPD
            showAlert("Lỗi khi hủy đăng ký khóa học" + e.getMessage());
            if (e.getMessage().contains("ORA-28115")) {
                System.out.println("Cảnh báo: Thao tác bị từ chối bởi chính sách VPD.");
            } else {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadRegisteredCourses();
        loadUnregisteredCourses();
    }
}
