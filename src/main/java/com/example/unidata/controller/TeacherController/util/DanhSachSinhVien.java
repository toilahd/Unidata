package com.example.unidata.controller.TeacherController.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class DanhSachSinhVien {
    private final SimpleIntegerProperty stt;
    private final SimpleStringProperty hoTen;
    private final SimpleStringProperty gioiTinh;
    private final SimpleStringProperty tinhTrang;

    public DanhSachSinhVien(int stt, String hoTen, String gioiTinh, String tinhTrang) {
        this.stt = new SimpleIntegerProperty(stt);
        this.hoTen = new SimpleStringProperty(hoTen);
        this.gioiTinh = new SimpleStringProperty(gioiTinh);
        this.tinhTrang = new SimpleStringProperty(tinhTrang);
    }

    public int getStt() { return stt.get(); }
    public String getHoTen() { return hoTen.get(); }
    public String getGioiTinh() { return gioiTinh.get(); }
    public String getTinhTrang() { return tinhTrang.get(); }

}
