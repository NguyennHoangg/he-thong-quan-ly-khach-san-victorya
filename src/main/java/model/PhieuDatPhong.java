package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PhieuDatPhong {
    private String maPhieuDatPhong;
    private KhachHang khachHang;
    private LocalDate ngayTao;
    private List<ChiTietPhieuDatPhong> dsachPhieuDatPhong = new ArrayList<>();

    public PhieuDatPhong(String maPhieuDatPhong, KhachHang khachHang, LocalDate ngayTao,
            List<ChiTietPhieuDatPhong> dsachPhieuDatPhong) {
        this.maPhieuDatPhong = maPhieuDatPhong;
        this.khachHang = khachHang;
        this.ngayTao = ngayTao;
        this.dsachPhieuDatPhong = dsachPhieuDatPhong;
    }

    public PhieuDatPhong(String maPhieuDatPhong) {
        this.maPhieuDatPhong = maPhieuDatPhong;
    }

    public PhieuDatPhong() {
        //TODO Auto-generated constructor stub
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

    public long tinhTongTien(){
        long tongTien = 0;
        for(ChiTietPhieuDatPhong ct : dsachPhieuDatPhong){
            tongTien += ct.tinhThanhTien();
        }
        return tongTien;
    }

}
