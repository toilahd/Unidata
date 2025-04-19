package com.example.unidata.controller.StudentController.utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class StudyResults {
    private final SimpleStringProperty mahp;
    private final SimpleFloatProperty diemth;
    private final SimpleFloatProperty diemqt;
    private final SimpleFloatProperty diemck;
    private final SimpleFloatProperty diemtk;

    public StudyResults(String mahp, float diemth, float diemqt, float diemck, float diemtk) {
        this.mahp = new SimpleStringProperty(mahp);

        this.diemth = new SimpleFloatProperty(diemth);
        this.diemqt = new SimpleFloatProperty(diemqt);
        this.diemck = new SimpleFloatProperty(diemck);
        this.diemtk = new SimpleFloatProperty(diemtk);
    }

    public String getMahp() { return mahp.get(); }
    public float getDiemth() { return diemth.get(); }
    public float getDiemqt() { return diemqt.get(); }
    public float getDiemck() { return diemck.get(); }
    public float getDiemtk() { return diemtk.get(); }
}
