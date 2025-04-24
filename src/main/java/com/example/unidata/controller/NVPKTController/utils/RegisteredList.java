package com.example.unidata.controller.NVPKTController.utils;

public class RegisteredList {
    String masv;
    String mamm;
    Double diemth;
    Double diemquatrinh;
    Double diemck;
    Double diemtongket;

    public RegisteredList(String masv, String mamm, Double diemth, Double diemquatrinh, Double diemck, Double diemtongket) {
        this.masv = masv;
        this.mamm = mamm;
        this.diemth = diemth;
        this.diemquatrinh = diemquatrinh;
        this.diemck = diemck;
        this.diemtongket = diemtongket;
    }

    public String getMasv() {return masv;};
    public String getMamm() {return mamm;};
    public Double getDiemth() {return diemth;};
    public Double getDiemquatrinh() {return diemquatrinh;};
    public Double getDiemck() {return diemck;};
    public Double getDiemtongket() {return diemtongket;};

    public void setMasv(String masv) {this.masv = masv;};
    public void setMamm(String mamm) {this.mamm = mamm;};
    public void setDiemth(Double diemquatrinh) {this.diemquatrinh = diemquatrinh;};
    public void setDiemck(Double diemck) {this.diemck = diemck;};
    public void setDiemtongket(Double diemtongket) {this.diemtongket = diemtongket;};
    public void setDiemquatrinh(Double diemquatrinh) {this.diemquatrinh = diemquatrinh;};
}
