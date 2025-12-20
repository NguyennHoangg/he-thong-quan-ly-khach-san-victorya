package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChiTietHoaDon {

    private PhieuDatPhong phieuDatPhong;
    private Phong phong;

    private LocalDateTime ngayTao;
    private double tongTien;

    // Dịch vụ nên đi qua bảng trung gian; tạm thời để List<ChiTietHoaDonDichVu>
    private List<ChiTietHoaDonDichVu> dichVus = new ArrayList<>();

    public ChiTietHoaDon() {}

    public ChiTietHoaDon( PhieuDatPhong phieuDatPhong, LocalDateTime ngayTao, double tongTien) {
      
        this.phieuDatPhong = phieuDatPhong;
        this.ngayTao = ngayTao;
        this.tongTien = tongTien;
    }

  

    public PhieuDatPhong getPhieuDatPhong() { return phieuDatPhong; }
    public void setPhieuDatPhong(PhieuDatPhong phieuDatPhong) { this.phieuDatPhong = phieuDatPhong; }

    public Phong getPhong() { return phong; }
    public void setPhong(Phong phong) { this.phong = phong; }

    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }

    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; } // <-- gán đúng field

    public List<ChiTietHoaDonDichVu> getDichVus() { return dichVus; }
    public void setDichVus(List<ChiTietHoaDonDichVu> dichVus) { this.dichVus = dichVus; }


    @Override
    public String toString() {
        return "ChiTietHoaDon{" +
                ", maPhieuDatPhong=" + (phieuDatPhong != null ? phieuDatPhong.getMaPhieuDatPhong() : "null") +
                ", ngayTao=" + ngayTao +
                ", tongTien=" + tongTien +
                ", soDichVu=" + (dichVus != null ? dichVus.size() : 0) +
                '}';
    }
}
