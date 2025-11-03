package model;

import java.time.LocalDate;

public class NhanVien {
    private String maNhanVien;
    private String CCCD;
    private String tenNhanVien;
    private TaiKhoan taiKhoan;
    private boolean gioiTinh;
    private LocalDate ngaySinh;
    private String email;
    private String soDienThoai;
    private LocalDate ngayBatDau;
    private String trangThai;
    private String diaChi;

    public NhanVien(String maNhanVien, String cCCD, String tenNhanVien, TaiKhoan taiKhoan, boolean gioiTinh,
            LocalDate ngaySinh, String email, String soDienThoai, LocalDate ngayBatDau, String trangThai,
            String diaChi) {
        this.maNhanVien = maNhanVien;
        CCCD = cCCD;
        this.tenNhanVien = tenNhanVien;
        this.taiKhoan = taiKhoan;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.ngayBatDau = ngayBatDau;
        this.trangThai = trangThai;
        this.diaChi = diaChi;
    }

    public NhanVien() {
        // TODO Auto-generated constructor stub
    }

    public NhanVien(String maNV, String ten, TaiKhoan tk, Boolean gioiTinh2, LocalDate ngaySinh2, String email2,
            String soDienThoai2, LocalDate ngayBatDau2) {
        this.maNhanVien = maNV;
        this.tenNhanVien = ten;
        this.taiKhoan = tk;
        this.gioiTinh = gioiTinh2 != null ? gioiTinh2 : false;
        this.ngaySinh = ngaySinh2;
        this.email = email2;
        this.soDienThoai = soDienThoai2;
        this.ngayBatDau = ngayBatDau2;
    }

    public NhanVien(String tenNV, TaiKhoan tk, boolean gioiTinhValue, LocalDate ngaySinh2, String email2,
            String soDienThoai2, String cccd2, String diaChi2) {
        this.tenNhanVien = tenNV;
        this.taiKhoan = tk;
        this.gioiTinh = gioiTinhValue;
        this.ngaySinh = ngaySinh2;
        this.email = email2;
        this.soDienThoai = soDienThoai2;
        this.CCCD = cccd2;
        this.diaChi = diaChi2;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public NhanVien(String email) {
        this.email = email;
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

    public String getCCCD() {
        return CCCD;
    }

    public void setCCCD(String cCCD) {
        CCCD = cCCD;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }
}
