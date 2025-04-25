package com.example.unidata.controller.NVCTSVController.utils;
import java.sql.Date;

public class SinhVien {
    private String maSV;
    private String hoTen;
    private String phai;
    private Date ngaySinh;
    private String diaChi;
    private String dienThoai;
    private String khoa;
    private String tinhTrang;

    public SinhVien(String maSV, String hoTen, String phai, Date ngaySinh, String diaChi, String dienThoai, String khoa, String tinhTrang) {
        this.maSV = maSV;
        this.hoTen = hoTen;
        this.phai = phai;
        this.ngaySinh = ngaySinh;
        this.diaChi = diaChi;
        this.dienThoai = dienThoai;
        this.khoa = khoa;
        this.tinhTrang = tinhTrang;
    }

    // Getters
    public String getMaSV() { return maSV; }
    public String getHoTen() { return hoTen; }
    public String getPhai() { return phai; }
    public Date getNgaySinh() { return ngaySinh; }
    public String getDiaChi() { return diaChi; }
    public String getDienThoai() { return dienThoai; }
    public String getKhoa() { return khoa; }
    public String getTinhTrang() { return tinhTrang; }
}