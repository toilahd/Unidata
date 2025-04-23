package com.example.unidata.controller.TRGDVController.util;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PhanCongData {
    private final StringProperty maMM;
    private final StringProperty maHP;
    private final StringProperty maGV;
    private final IntegerProperty hk;
    private final IntegerProperty nam;

    public PhanCongData(String maMM, String maHP, String maGV, int hk, int nam) {
        this.maMM = new SimpleStringProperty(maMM);
        this.maHP = new SimpleStringProperty(maHP);
        this.maGV = new SimpleStringProperty(maGV);
        this.hk = new SimpleIntegerProperty(hk);
        this.nam = new SimpleIntegerProperty(nam);
    }

    // Property getters
    public StringProperty maMMProperty() {
        return maMM;
    }

    public StringProperty maHPProperty() {
        return maHP;
    }

    public StringProperty maGVProperty() {
        return maGV;
    }

    public IntegerProperty hkProperty() {
        return hk;
    }

    public IntegerProperty namProperty() {
        return nam;
    }

    // Regular getters
    public String getMaMM() {
        return maMM.get();
    }

    public String getMaHP() {
        return maHP.get();
    }

    public String getMaGV() {
        return maGV.get();
    }

    public int getHk() {
        return hk.get();
    }

    public int getNam() {
        return nam.get();
    }

    // Setters
    public void setMaMM(String maMM) {
        this.maMM.set(maMM);
    }

    public void setMaHP(String maHP) {
        this.maHP.set(maHP);
    }

    public void setMaGV(String maGV) {
        this.maGV.set(maGV);
    }

    public void setHk(int hk) {
        this.hk.set(hk);
    }

    public void setNam(int nam) {
        this.nam.set(nam);
    }
}
