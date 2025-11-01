package model;

import java.time.LocalDateTime;

public class ChiTietHoaDon {
    private HoaDon hoaDon;
    private java.util.List<DichVu> dsachDichVu = new java.util.ArrayList<>();
    private LocalDateTime ngayTao;
    private PhieuDatPhong phieuDatPhong;
    private long tongTien;
    

    public ChiTietHoaDon(HoaDon hoaDon, java.util.List<DichVu> dsachDichVu, LocalDateTime ngayTao,
            PhieuDatPhong phieuDatPhong) {
        this.hoaDon = hoaDon;

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

    private double setTongTien(double tongTien){
        return tongTien;
    }
    
    
}
