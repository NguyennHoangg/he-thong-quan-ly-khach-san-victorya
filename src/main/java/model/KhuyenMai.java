package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class KhuyenMai {
    private String maKhuyenMai;
    private String tenKhuyenMai;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private boolean trangThai;
    private float heSo;
    private float tongTienToiThieu;
    private float tongKhuyenMaiToiDa;

    public KhuyenMai(String maKhuyenMai, String tenKhuyenMai, LocalDateTime ngayBatDau, LocalDateTime ngayKetThuc,
            boolean trangThai, float heSo, float tongTienToiThieu, float tongKhuyenMaiToiDa) {
        this.maKhuyenMai = maKhuyenMai;
        this.tenKhuyenMai = tenKhuyenMai;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.trangThai = trangThai;
        this.heSo = heSo;
        this.tongTienToiThieu = tongTienToiThieu;
        this.tongKhuyenMaiToiDa = tongKhuyenMaiToiDa;
    }

    public KhuyenMai() {
        //TODO Auto-generated constructor stub
    }

    public void setMaKhuyenMai(String maKhuyenMai) {
        this.maKhuyenMai = maKhuyenMai;
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

    public float getTongTienToiThieu() {
        return tongTienToiThieu;
    }

    public float getTongKhuyenMaiToiDa() {
        return tongKhuyenMaiToiDa;
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

    public void settongTienToiThieu(float tongTienToiThieu) {
        this.tongTienToiThieu = tongTienToiThieu;
    }

    public void settongKhuyenMaiToiDa(float tongKhuyenMaiToiDa) {
        this.tongKhuyenMaiToiDa = tongKhuyenMaiToiDa;
    }


}
