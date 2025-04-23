package com.example.unidata.controller.NVPDTController;

import com.example.unidata.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import com.example.unidata.controller.NVPDTController.utils.EditSubject;
import com.example.unidata.controller.NVPDTController.UpdateSubjectController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateSubjectWindowController {
    @FXML private TextField tfNamHoc;
    @FXML private Text mamm;
    @FXML private ComboBox cbMaHP;
    @FXML private ComboBox cbMaGV;
    @FXML private ComboBox cbHocKi;
    @FXML private Button btnUpdate;

    private ObservableList<String> hocKiList = FXCollections.observableArrayList("1", "2", "3");


    public void initialize(EditSubject subject, UpdateSubjectController controller) {
        setSubjectData(subject);
        btnUpdate.setOnAction(event -> onUpdateButtonClick(controller));
        cbHocKi.setItems(hocKiList);
        query("SELECT MAHP FROM DBA_MANAGER.HOCPHAN", cbMaHP);
        query("SELECT MANLD FROM DBA_MANAGER.NHANVIEN WHERE VAITRO = 'GV'", cbMaGV);
    }

    public void setSubjectData(EditSubject subject) {
        mamm.setText(subject.getMaMoMon());
        cbMaHP.setValue(subject.getMaHocPhan());
        cbMaGV.setValue(subject.getMaGiangVien());
        cbHocKi.setValue(String.valueOf(subject.getHocKi()));
        tfNamHoc.setText(String.valueOf(subject.getNam()));
    }

    @FXML
    private void onUpdateButtonClick(UpdateSubjectController controller) {
        String maMoMon = mamm.getText();
        String maHP = cbMaHP.getValue().toString();
        String maGV = cbMaGV.getValue().toString();
        String hocKi = cbHocKi.getValue().toString();
        String nam = tfNamHoc.getText();

        String sql = "UPDATE DBA_MANAGER.UV_NVPDT_MOMON SET MAHP = ?, MAGV = ?, HK = ?, NAM = ? WHERE MAMM = ?";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, maHP);
            stmt.setString(2, maGV);
            stmt.setString(3, hocKi);
            stmt.setString(4, nam);
            stmt.setString(5, maMoMon);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Cập nhật môn học thành công!");
                controller.loadData();
            }
            else {
                showAlert("Không tìm thấy môn học để cập nhật!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi khi cập nhật môn học!");
        }
    }

    private void query (String query, ComboBox<String> comboBox) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            ObservableList<String> list = FXCollections.observableArrayList();
            while (rs.next()) {
                list.add(rs.getString(1));
            }
            comboBox.setItems(list);
        }
        catch (SQLException e) {
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
}
