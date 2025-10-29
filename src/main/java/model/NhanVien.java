package model;

import java.time.LocalDate;

public class NhanVien {
    private String maNhanVien;
    private String tenNhanVien;
    private TaiKhoan taiKhoan;
    private boolean gioiTinh;
    private LocalDate ngaySinh;
    private String email;

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    private String soDienThoai;
    private LocalDate ngayBatDau;
    private String trangThai;

    public NhanVien(String maNhanVien, String tenNhanVien, TaiKhoan taiKhoan, boolean gioiTinh, LocalDate ngaySinh,
            String email, String soDienThoai, LocalDate ngayBatDau, String trangThai) {
        this.maNhanVien = maNhanVien;
        this.tenNhanVien = tenNhanVien;
        this.taiKhoan = taiKhoan;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.ngayBatDau = ngayBatDau;
        this.trangThai = trangThai;
    }

    public NhanVien(String maNV, String ten, TaiKhoan tk, Boolean gioiTinh2, LocalDate ngaySinh2, String email2,
            String soDienThoai2, LocalDate ngayBatDau2) {
        this.maNhanVien = maNV;
        this.tenNhanVien = ten;
        this.taiKhoan = tk;
        this.gioiTinh = gioiTinh2;
        this.ngaySinh = ngaySinh2;
        this.email = email2;
        this.soDienThoai = soDienThoai2;
        this.ngayBatDau = ngayBatDau2;
        this.trangThai = "Đang làm việc";
    }
    public NhanVien (String maNV){
        this.maNhanVien=maNV;
    }

    public NhanVien(String tenNhanVien, TaiKhoan taiKhoan, boolean gioiTinh, LocalDate ngaySinh,
            String email, String soDienThoai) {
        this.tenNhanVien = tenNhanVien;
        this.taiKhoan = taiKhoan;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.email = email;
        this.soDienThoai = soDienThoai;
    }

    public NhanVien() {

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

    public boolean getGioiTinh() {
        return gioiTinh;
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

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

}
