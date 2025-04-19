package com.example.unidata.controller.TeacherController.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class DanhSachDiem {
    private final SimpleIntegerProperty stt;
    private final SimpleStringProperty masv;
    private final SimpleFloatProperty diemthuchanh;
    private final SimpleFloatProperty diemquatrinh;
    private final SimpleFloatProperty diemcuoiki;
    private final SimpleFloatProperty diemtongket;


    public DanhSachDiem(int stt, String masv, float diemthuchanh, float diemquatrinh, float diemcuoiki, float diemtongket) {
        this.stt = new SimpleIntegerProperty(stt);
        this.masv = new SimpleStringProperty(masv);
        this.diemthuchanh = new SimpleFloatProperty(diemthuchanh);
        this.diemquatrinh = new SimpleFloatProperty(diemquatrinh);
        this.diemcuoiki = new SimpleFloatProperty(diemcuoiki);
        this.diemtongket = new SimpleFloatProperty(diemtongket);
    }

    public int getStt() {
        return stt.get();
    }

    public String getMasv() {
        return masv.get();
    }

    public float getDiemthuchanh() {
        return diemthuchanh.get();
    }


    public float getDiemquatrinh() {
        return diemquatrinh.get();
    }


    public float getDiemcuoiki() {
        return diemcuoiki.get();
    }

    public float getDiemtongket() {
        return diemtongket.get();
    }

}
