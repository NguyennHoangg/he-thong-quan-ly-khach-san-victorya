package model;

import java.util.Objects;

public class ChiTietHoaDonDichVu {
    private HoaDon hoaDon;                 // tham chiếu hóa đơn
    private PhieuDatPhong phieuDatPhong;   // tham chiếu phiếu
    private DichVu dichVu;                 // dịch vụ



    public ChiTietHoaDonDichVu() {}

    public ChiTietHoaDonDichVu(HoaDon hoaDon, PhieuDatPhong phieuDatPhong, DichVu dichVu) {
        this.hoaDon = hoaDon;
        this.phieuDatPhong = phieuDatPhong;
        this.dichVu = dichVu;
    }

    public HoaDon getHoaDon() { return hoaDon; }
    public void setHoaDon(HoaDon hoaDon) { this.hoaDon = hoaDon; }

    public PhieuDatPhong getPhieuDatPhong() { return phieuDatPhong; }
    public void setPhieuDatPhong(PhieuDatPhong phieuDatPhong) { this.phieuDatPhong = phieuDatPhong; }

    public DichVu getDichVu() { return dichVu; }
    public void setDichVu(DichVu dichVu) { this.dichVu = dichVu; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietHoaDonDichVu)) return false;
        ChiTietHoaDonDichVu that = (ChiTietHoaDonDichVu) o;
        String maHD = hoaDon != null ? hoaDon.getMaHoaDon() : null;
        String maPDP = phieuDatPhong != null ? phieuDatPhong.getMaPhieuDatPhong() : null;
        String maDV = dichVu != null ? dichVu.getMaDichVu() : null;
        String thatMaHD = that.hoaDon != null ? that.hoaDon.getMaHoaDon() : null;
        String thatMaPDP = that.phieuDatPhong != null ? that.phieuDatPhong.getMaPhieuDatPhong() : null;
        String thatMaDV = that.dichVu != null ? that.dichVu.getMaDichVu() : null;
        return Objects.equals(maHD, thatMaHD) &&
                Objects.equals(maPDP, thatMaPDP) &&
                Objects.equals(maDV, thatMaDV);
    }

    @Override
    public int hashCode() {
        String maHD = hoaDon != null ? hoaDon.getMaHoaDon() : null;
        String maPDP = phieuDatPhong != null ? phieuDatPhong.getMaPhieuDatPhong() : null;
        String maDV = dichVu != null ? dichVu.getMaDichVu() : null;
        return Objects.hash(maHD, maPDP, maDV);
    }
}
