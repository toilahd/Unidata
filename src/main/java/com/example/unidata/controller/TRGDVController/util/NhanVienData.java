package com.example.unidata.controller.TRGDVController.util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Date;

public class NhanVienData {
    private final StringProperty manld;
    private final StringProperty hoten;
    private final StringProperty phai;
    private final ObjectProperty<Date> ngsinh;
    private final StringProperty dt;
    private final StringProperty vaitro;
    private final StringProperty madv;

    public NhanVienData(String manld, String hoten, String phai, Date ngsinh, String dt, String vaitro, String madv) {
        this.manld = new SimpleStringProperty(manld);
        this.hoten = new SimpleStringProperty(hoten);
        this.phai = new SimpleStringProperty(phai);
        this.ngsinh = new SimpleObjectProperty<>(ngsinh);
        this.dt = new SimpleStringProperty(dt);
        this.vaitro = new SimpleStringProperty(vaitro);
        this.madv = new SimpleStringProperty(madv);
    }

    // Property getters
    public StringProperty manldProperty() {
        return manld;
    }

    public StringProperty hotenProperty() {
        return hoten;
    }

    public StringProperty phaiProperty() {
        return phai;
    }

    public ObjectProperty<Date> ngsinhProperty() {
        return ngsinh;
    }

    public StringProperty dtProperty() {
        return dt;
    }

    public StringProperty vaitroProperty() {
        return vaitro;
    }

    public StringProperty madvProperty() {
        return madv;
    }

    // Regular getters
    public String getManld() {
        return manld.get();
    }

    public String getHoten() {
        return hoten.get();
    }

    public String getPhai() {
        return phai.get();
    }

    public Date getNgsinh() {
        return ngsinh.get();
    }

    public String getDt() {
        return dt.get();
    }

    public String getVaitro() {
        return vaitro.get();
    }

    public String getMadv() {
        return madv.get();
    }

    // Setters
    public void setManld(String manld) {
        this.manld.set(manld);
    }

    public void setHoten(String hoten) {
        this.hoten.set(hoten);
    }

    public void setPhai(String phai) {
        this.phai.set(phai);
    }

    public void setNgsinh(Date ngsinh) {
        this.ngsinh.set(ngsinh);
    }

    public void setDt(String dt) {
        this.dt.set(dt);
    }

    public void setVaitro(String vaitro) {
        this.vaitro.set(vaitro);
    }

    public void setMadv(String madv) {
        this.madv.set(madv);
    }
}
