package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PhieuDatPhong {
    private String maPhieuDatPhong;
    private KhachHang khachHang;
    private LocalDate ngayTao;
    private List<ChiTietPhieuDatPhong> dsachPhieuDatPhong = new ArrayList<>();
    private String trangThai;
    private long tienDatCoc;

    public PhieuDatPhong(String maPhieuDatPhong, KhachHang khachHang, LocalDate ngayTao,
            List<ChiTietPhieuDatPhong> dsachPhieuDatPhong) {
        this.maPhieuDatPhong = maPhieuDatPhong;
        this.khachHang = khachHang;
        this.ngayTao = ngayTao;
        this.dsachPhieuDatPhong = dsachPhieuDatPhong;
    }

    public PhieuDatPhong(String maPhieuDatPhong, KhachHang khachHang, LocalDate ngayTao,
            List<ChiTietPhieuDatPhong> dsachPhieuDatPhong, String trangThai, long tienDatCoc) {
        this.maPhieuDatPhong = maPhieuDatPhong;
        this.khachHang = khachHang;
        this.ngayTao = ngayTao;
        this.dsachPhieuDatPhong = dsachPhieuDatPhong;
        this.trangThai = trangThai;
        this.tienDatCoc = tienDatCoc;
    }

    public PhieuDatPhong(String maPhieuDatPhong) {
        this.maPhieuDatPhong = maPhieuDatPhong;
    }

    public PhieuDatPhong() {
        // TODO Auto-generated constructor stub
    }

    public PhieuDatPhong(String maPhieuDatPhong2, KhachHang kh) {
        // Khởi tạo đầy đủ mã phiếu và thông tin khách hàng
        this.maPhieuDatPhong = maPhieuDatPhong2;
        this.khachHang = kh;
    }

    public long getTienDatCoc() {
        return tienDatCoc;
    }

    public void setTienDatCoc(long tienDatCoc) {
        this.tienDatCoc = tienDatCoc;
    }

    public String getMaPhieuDatPhong() {
        return maPhieuDatPhong;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public LocalDate getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDate ngayTao) {
        this.ngayTao = ngayTao;
    }

    public List<ChiTietPhieuDatPhong> getDsachPhieuDatPhong() {
        return dsachPhieuDatPhong;
    }

    public void setDsachPhieuDatPhong(List<ChiTietPhieuDatPhong> dsachPhieuDatPhong) {
        this.dsachPhieuDatPhong = dsachPhieuDatPhong;
    }

    public long tinhTongTien() {
        long tongTien = 0;
        for (ChiTietPhieuDatPhong ct : dsachPhieuDatPhong) {
            tongTien += ct.tinhThanhTien();
        }
        return tongTien;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public long setTienDatCoc() {
        long tienDatCoc = 0;
        for (ChiTietPhieuDatPhong ct : dsachPhieuDatPhong) {
            tienDatCoc += ct.getSoGioLuuTru() * ct.getPhong().getLoaiPhong().getGia();
        }
        return tienDatCoc;
    }

}
