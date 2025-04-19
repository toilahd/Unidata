package com.example.unidata.controller.TeacherController.util;

import javafx.beans.property.SimpleStringProperty;

public class DanhSachPhanCong {
    private final SimpleStringProperty stt;
    private final SimpleStringProperty maHocPhan;
    private final SimpleStringProperty hocKi;
    private final SimpleStringProperty namHoc;

    public DanhSachPhanCong(String stt, String maHocPhan, String hocKi, String namHoc) {
        this.stt = new SimpleStringProperty(stt);
        this.maHocPhan = new SimpleStringProperty(maHocPhan);
        this.hocKi = new SimpleStringProperty(hocKi);
        this.namHoc = new SimpleStringProperty(namHoc);
    }

    public String getStt() { return stt.get(); }
    public String getMaHocPhan() { return maHocPhan.get(); }
    public String getHocKi() { return hocKi.get(); }
    public String getNamHoc() { return namHoc.get(); }
}