package model;

import java.time.LocalDateTime;

public class KhuyenMai {
    private String maKhuyenMai;
    private String tenKhuyenMai;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private boolean trangThai;
    private float heSo;
    private float soTienToiThieuHuongKhuyenMai;
    private float soTienDuocGiamToiDa;
    
    public KhuyenMai(String maKhuyenMai, String tenKhuyenMai, LocalDateTime ngayBatDau, LocalDateTime ngayKetThuc,
            boolean trangThai, float heSo, float soTienToiThieuHuongKhuyenMai, float soTienDuocGiamToiDa) {
        this.maKhuyenMai = maKhuyenMai;
        this.tenKhuyenMai = tenKhuyenMai;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.trangThai = trangThai;
        this.heSo = heSo;
        this.soTienToiThieuHuongKhuyenMai = soTienToiThieuHuongKhuyenMai;
        this.soTienDuocGiamToiDa = soTienDuocGiamToiDa;
    }

    public String getMaKhuyenMai() {
        return maKhuyenMai;
    }

    public String getTenKhuyenMai() {
        return tenKhuyenMai;
    }

    public LocalDateTime getNgayBatDau() {
        return ngayBatDau;
    }

    public LocalDateTime getNgayKetThuc() {
        return ngayKetThuc;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public float getHeSo() {
        return heSo;
    }

    public float getSoTienToiThieuHuongKhuyenMai() {
        return soTienToiThieuHuongKhuyenMai;
    }

    public float getSoTienDuocGiamToiDa() {
        return soTienDuocGiamToiDa;
    }

    public void setTenKhuyenMai(String tenKhuyenMai) {
        this.tenKhuyenMai = tenKhuyenMai;
    }

    public void setNgayBatDau(LocalDateTime ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public void setNgayKetThuc(LocalDateTime ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    public void setHeSo(float heSo) {
        this.heSo = heSo;
    }

    public void setSoTienToiThieuHuongKhuyenMai(float soTienToiThieuHuongKhuyenMai) {
        this.soTienToiThieuHuongKhuyenMai = soTienToiThieuHuongKhuyenMai;
    }

    public void setSoTienDuocGiamToiDa(float soTienDuocGiamToiDa) {
        this.soTienDuocGiamToiDa = soTienDuocGiamToiDa;
    }

    
}
