package model;

import java.time.LocalDateTime;

public class ChiTietHoaDon {
    private HoaDon hoaDon;
    private NhanVien nhanVien;
    private java.util.List<DichVu> dsachDichVu = new java.util.ArrayList<>();
    private LocalDateTime ngayTao;
    private PhieuDatPhong phieuDatPhong;

    public ChiTietHoaDon(HoaDon hoaDon, NhanVien nhanVien, java.util.List<DichVu> dsachDichVu, LocalDateTime ngayTao,
            PhieuDatPhong phieuDatPhong) {
        this.hoaDon = hoaDon;
        this.nhanVien = nhanVien;
        this.dsachDichVu = dsachDichVu;
        this.ngayTao = ngayTao;
        this.phieuDatPhong = phieuDatPhong;
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public java.util.List<DichVu> getDsachDichVu() {
        return dsachDichVu;
    }

    public void setDsachDichVu(java.util.List<DichVu> dsachDichVu) {
        this.dsachDichVu = dsachDichVu;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public PhieuDatPhong getPhieuDatPhong() {
        return phieuDatPhong;
    }

    public void setPhieuDatPhong(PhieuDatPhong phieuDatPhong) {
        this.phieuDatPhong = phieuDatPhong;
    }

    
    
    
}
