package com.example.unidata.controller.NVPDTController.utils;

public class Student {
    private String mssv;
    private String hoten;
    private String gioitinh;
    private String ngaysinh;
    private String diachi;
    private String tinhTrang;
    private String khoa;
    private String dienthoai;

    // Constructor
    public Student(String mssv, String hoten, String gioitinh, String ngaysinh,
                   String diachi, String tinhTrang, String khoa, String dienthoai) {
        this.mssv = mssv;
        this.hoten = hoten;
        this.gioitinh = gioitinh;
        this.ngaysinh = ngaysinh;
        this.diachi = diachi;
        this.tinhTrang = tinhTrang;
        this.khoa = khoa;
        this.dienthoai = dienthoai;
    }

    // Getters and Setters
    public String getMssv() { return mssv; }
    public void setMssv(String mssv) { this.mssv = mssv; }

    public String getHoten() { return hoten; }
    public void setHoten(String hoten) { this.hoten = hoten; }

    public String getGioitinh() { return gioitinh; }
    public void setGioitinh(String gioitinh) { this.gioitinh = gioitinh; }

    public String getNgaysinh() { return ngaysinh; }
    public void setNgaysinh(String ngaysinh) { this.ngaysinh = ngaysinh; }

    public String getDiachi() { return diachi; }
    public void setDiachi(String diachi) { this.diachi = diachi; }

    public String getTinhTrang() { return tinhTrang; }
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }

    public String getKhoa() { return khoa; }
    public void setKhoa(String khoa) { this.khoa = khoa; }

    public String getDienthoai() { return dienthoai; }
    public void setDienthoai(String dienthoai) { this.dienthoai = dienthoai; }
}
