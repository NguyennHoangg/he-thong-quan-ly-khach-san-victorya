package model.thongke;

import java.time.LocalDate;

/**
 * Model cho dòng bảng thống kê doanh thu
 */
public class TableRowDoanhThu {
    private LocalDate ngay;
    private int soHoaDon;
    private double doanhThuPhong;
    private double doanhThuDichVu;
    private double doanhThuTong;

    public TableRowDoanhThu() {
    }

    public TableRowDoanhThu(LocalDate ngay, int soHoaDon, double doanhThuPhong, double doanhThuDichVu,
            double doanhThuTong) {
        this.ngay = ngay;
        this.soHoaDon = soHoaDon;
        this.doanhThuPhong = doanhThuPhong;
        this.doanhThuDichVu = doanhThuDichVu;
        this.doanhThuTong = doanhThuTong;
    }

    // Getters and Setters
    public LocalDate getNgay() {
        return ngay;
    }

    public void setNgay(LocalDate ngay) {
        this.ngay = ngay;
    }

    public int getSoHoaDon() {
        return soHoaDon;
    }

    public void setSoHoaDon(int soHoaDon) {
        this.soHoaDon = soHoaDon;
    }

    public double getDoanhThuPhong() {
        return doanhThuPhong;
    }

    public void setDoanhThuPhong(double doanhThuPhong) {
        this.doanhThuPhong = doanhThuPhong;
    }

    public double getDoanhThuDichVu() {
        return doanhThuDichVu;
    }

    public void setDoanhThuDichVu(double doanhThuDichVu) {
        this.doanhThuDichVu = doanhThuDichVu;
    }

    public double getDoanhThuTong() {
        return doanhThuTong;
    }

    public void setDoanhThuTong(double doanhThuTong) {
        this.doanhThuTong = doanhThuTong;
    }
}
