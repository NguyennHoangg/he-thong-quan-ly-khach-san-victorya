package model.thongke;

import java.time.LocalDate;

/**
 * Model cho dòng bảng thống kê đặt phòng
 */
public class TableRowDatPhong {
    private String maPhieuDatPhong;
    private LocalDate ngayTao;
    private String tenKhach;
    private String trangThai;
    private double tienDatCoc;

    public TableRowDatPhong() {
    }

    public TableRowDatPhong(String maPhieuDatPhong, LocalDate ngayTao, String tenKhach,
            String trangThai, double tienDatCoc) {
        this.maPhieuDatPhong = maPhieuDatPhong;
        this.ngayTao = ngayTao;
        this.tenKhach = tenKhach;
        this.trangThai = trangThai;
        this.tienDatCoc = tienDatCoc;
    }

    // Getters and Setters
    public String getMaPhieuDatPhong() {
        return maPhieuDatPhong;
    }

    public void setMaPhieuDatPhong(String maPhieuDatPhong) {
        this.maPhieuDatPhong = maPhieuDatPhong;
    }

    public LocalDate getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDate ngayTao) {
        this.ngayTao = ngayTao;
    }

    public String getTenKhach() {
        return tenKhach;
    }

    public void setTenKhach(String tenKhach) {
        this.tenKhach = tenKhach;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public double getTienDatCoc() {
        return tienDatCoc;
    }

    public void setTienDatCoc(double tienDatCoc) {
        this.tienDatCoc = tienDatCoc;
    }
}
