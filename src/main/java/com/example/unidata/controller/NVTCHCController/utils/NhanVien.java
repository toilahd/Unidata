package com.example.unidata.controller.NVTCHCController.utils;

import java.time.LocalDate;

public class NhanVien {
    private String manld;
    private String hoten;
    private String phai;
    private LocalDate ngsinh;
    private double luong;
    private double phucap;
    private String dienthoai;
    private String vaitro;
    private String madv;

    // Getters, Setters, Constructors
    public NhanVien() {

    }
    public NhanVien(String manld, String hoten, String phai, LocalDate ngsinh, double luong, double phucap, String dienthoai, String vaitro, String madv) {
        this.manld = manld;
        this.hoten = hoten;
        this.phai = phai;
        this.ngsinh = ngsinh;
        this.luong = luong;
        this.phucap = phucap;
        this.dienthoai = dienthoai;
        this.vaitro = vaitro;
        this.madv = madv;
    }

    public String getManld() {
        return manld;
    }

    public void setManld(String manld) {
        this.manld = manld;
    }

    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public String getPhai() {
        return phai;
    }

    public void setPhai(String phai) {
        this.phai = phai;
    }

    public LocalDate getNgsinh() {
        return ngsinh;
    }

    public void setNgsinh(LocalDate ngsinh) {
        this.ngsinh = ngsinh;
    }

    public double getLuong() {
        return luong;
    }

    public void setLuong(double luong) {
        this.luong = luong;
    }

    public double getPhucap() {
        return phucap;
    }

    public void setPhucap(double phucap) {
        this.phucap = phucap;
    }

    public String getDienthoai() {
        return dienthoai;
    }

    public void setDienthoai(String dienthoai) {
        this.dienthoai = dienthoai;
    }

    public String getVaitro() {
        return vaitro;
    }

    public void setVaitro(String vaitro) {
        this.vaitro = vaitro;
    }

    public String getMadv() {
        return madv;
    }

    public void setMadv(String madv) {
        this.madv = madv;
    }
}

