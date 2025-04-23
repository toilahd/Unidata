package com.example.unidata.controller.NVPDTController.utils;

import com.example.unidata.controller.NVPDTController.UpdateSubjectWindowController;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import com.example.unidata.controller.NVPDTController.UpdateSubjectController;
import javafx.stage.Stage;

import java.io.IOException;

public class EditSubject {
    private final SimpleStringProperty maMoMon;
    private final SimpleStringProperty maHocPhan;
    private final SimpleStringProperty maGiangVien;
    private final SimpleIntegerProperty nam;
    private final SimpleIntegerProperty hocKi;
    private Button editButton;

    UpdateSubjectController controller = new UpdateSubjectController();
    public EditSubject(String maMoMon, String maHocPhan, String maGiangVien, int nam, int hocKi, UpdateSubjectController controller) {
        this.maMoMon = new SimpleStringProperty(maMoMon);
        this.maHocPhan = new SimpleStringProperty(maHocPhan);
        this.maGiangVien = new SimpleStringProperty(maGiangVien);
        this.nam = new SimpleIntegerProperty(nam);
        this.hocKi = new SimpleIntegerProperty(hocKi);

        this.editButton = new Button("Sửa");
        this.controller = controller;
        if (controller != null) {
            this.editButton.setOnAction(event -> onUpdateSubject());
        } else {
            this.editButton.setOnAction(event -> System.out.println("No controller for editing"));
        }
    }

    private void onUpdateSubject() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/unidata/PhanHe2/NV_PDTView/UpdateSubjectWindow.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Cập nhật môn học");


            System.out.println("Controller loaded: " + loader.getController().getClass().getName());


            UpdateSubjectWindowController updateController = loader.getController();
            updateController.initialize(this, controller);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi khi mở cửa sổ cập nhật môn học.");
        }
    }

    public Button getEditButton() {
        return editButton;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public String getMaMoMon() {
        return maMoMon.get();
    }

    public StringProperty maMoMonProperty() {
        return maMoMon;
    }

    public String getMaHocPhan() {
        return maHocPhan.get();
    }

    public StringProperty maHocPhanProperty() {
        return maHocPhan;
    }

    public String getMaGiangVien() {
        return maGiangVien.get();
    }

    public StringProperty maGiangVienProperty() {
        return maGiangVien;
    }

    public int getNam() {
        return nam.get();
    }

    public IntegerProperty namProperty() {
        return nam;
    }

    public int getHocKi() {
        return hocKi.get();
    }

    public IntegerProperty hocKiProperty() {
        return hocKi;
    }
}
