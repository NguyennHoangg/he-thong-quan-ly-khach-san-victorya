package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDon {
    private String maHoaDon;
    private LocalDateTime ngayDat;
    private KhachHang khachHang;
    private NhanVien nhanVien;
    private KhuyenMai khuyenMai;
    private LocalDateTime ngayTao;
    private String trangThai;
    private double tongTien;

    private List<ChiTietHoaDon> chiTietHoaDon = new ArrayList<>();



    public HoaDon() {
    }

    public HoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public HoaDon(String maHoaDon, LocalDateTime ngayDat, KhachHang khachHang,
                  NhanVien nhanVien, KhuyenMai khuyenMai, LocalDateTime ngayTao) {
        this.maHoaDon = maHoaDon;
        this.ngayDat = ngayDat;
        this.khachHang = khachHang;
        this.nhanVien = nhanVien;
        this.khuyenMai = khuyenMai;
        this.ngayTao = ngayTao;
    }

    public HoaDon(String maHoaDon, LocalDateTime ngayDat, KhachHang khachHang,
                  NhanVien nhanVien, KhuyenMai khuyenMai, LocalDateTime ngayTao,
                  String trangThai, double tongTien) {
        this.maHoaDon = maHoaDon;
        this.ngayDat = ngayDat;
        this.khachHang = khachHang;
        this.nhanVien = nhanVien;
        this.khuyenMai = khuyenMai;
        this.ngayTao = ngayTao;
        this.trangThai = trangThai;
        this.tongTien = tongTien;
    }

    public HoaDon(String maHoaDon, LocalDateTime ngayDat, KhachHang khachHang,
                  NhanVien nhanVien, KhuyenMai khuyenMai, LocalDateTime ngayTao,
                  String trangThai, double tongTien, List<ChiTietHoaDon> chiTietHoaDon) {
        this.maHoaDon = maHoaDon;
        this.ngayDat = ngayDat;
        this.khachHang = khachHang;
        this.nhanVien = nhanVien;
        this.khuyenMai = khuyenMai;
        this.ngayTao = ngayTao;
        this.trangThai = trangThai;
        this.tongTien = tongTien;
        this.chiTietHoaDon = chiTietHoaDon;
    }

    // ====== GETTER & SETTER ======

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public LocalDateTime getNgayDat() {
        return ngayDat;
    }

    public void setNgayDat(LocalDateTime ngayDat) {
        this.ngayDat = ngayDat;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public KhuyenMai getKhuyenMai() {
        return khuyenMai;
    }

    public void setKhuyenMai(KhuyenMai khuyenMai) {
        this.khuyenMai = khuyenMai;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public List<ChiTietHoaDon> getChiTietHoaDon() {
        return chiTietHoaDon;
    }

    public void setChiTietHoaDon(List<ChiTietHoaDon> chiTietHoaDon) {
        this.chiTietHoaDon = chiTietHoaDon;
    }

    // ====== TO STRING (hữu ích khi debug) ======
    @Override
    public String toString() {
        return "HoaDon{" +
                "maHoaDon='" + maHoaDon + '\'' +
                ", ngayDat=" + ngayDat +
                ", khachHang=" + (khachHang != null ? khachHang.getMaKhachHang() : "null") +
                ", nhanVien=" + (nhanVien != null ? nhanVien.getMaNhanVien() : "null") +
                ", khuyenMai=" + (khuyenMai != null ? khuyenMai.getMaKhuyenMai() : "null") +
                ", ngayTao=" + ngayTao +
                ", trangThai='" + trangThai + '\'' +
                ", tongTien=" + tongTien +
                '}';
    }
}
