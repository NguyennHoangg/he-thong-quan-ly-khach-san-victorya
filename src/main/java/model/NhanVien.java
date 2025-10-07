package model;

import java.time.LocalDate;

public class NhanVien {
    private String maNhanVien;
    private String tenNhanVien;
    private TaiKhoan taiKhoan;
    private boolean gioiTinh;
    private LocalDate ngaySinh;
    private String email;
    private String soDienThoai;
    private LocalDate ngayBatDau;

    public NhanVien(String maNhanVien, String tenNhanVien, TaiKhoan taiKhoan, boolean gioiTinh, LocalDate ngaySinh,
            String email, String soDienThoai, LocalDate ngayBatDau) {
        this.maNhanVien = maNhanVien;
        this.tenNhanVien = tenNhanVien;
        this.taiKhoan = taiKhoan;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.ngayBatDau = ngayBatDau;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public boolean isGioiTinh() {
        return gioiTinh;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public String getEmail() {
        return email;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }

    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public void setGioiTinh(boolean gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

}
