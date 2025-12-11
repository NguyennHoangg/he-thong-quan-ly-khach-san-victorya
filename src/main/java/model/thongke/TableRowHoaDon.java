package model.thongke;

import java.time.LocalDate;

/**
 * Bảng hóa đơn (danh sách hóa đơn trong kỳ)
 */
public class TableRowHoaDon {
    private String maHoaDon;
    private LocalDate ngayTao;
    private String trangThai;
    private double tongTien;

    public TableRowHoaDon() {
    }

    public TableRowHoaDon(String maHoaDon, LocalDate ngayTao, String trangThai, double tongTien) {
        this.maHoaDon = maHoaDon;
        this.ngayTao = ngayTao;
        this.trangThai = trangThai;
        this.tongTien = tongTien;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public LocalDate getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDate ngayTao) {
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
}
