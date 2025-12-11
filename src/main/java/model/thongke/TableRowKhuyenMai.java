package model.thongke;

/**
 * Model cho dòng bảng thống kê khuyến mãi
 */
public class TableRowKhuyenMai {
    private String maKhuyenMai;
    private String tenKhuyenMai;
    private int soHoaDon;
    private double doanhThu;

    public TableRowKhuyenMai() {
    }

    public TableRowKhuyenMai(String maKhuyenMai, String tenKhuyenMai, int soHoaDon, double doanhThu) {
        this.maKhuyenMai = maKhuyenMai;
        this.tenKhuyenMai = tenKhuyenMai;
        this.soHoaDon = soHoaDon;
        this.doanhThu = doanhThu;
    }

    // Getters and Setters
    public String getMaKhuyenMai() {
        return maKhuyenMai;
    }

    public void setMaKhuyenMai(String maKhuyenMai) {
        this.maKhuyenMai = maKhuyenMai;
    }

    public String getTenKhuyenMai() {
        return tenKhuyenMai;
    }

    public void setTenKhuyenMai(String tenKhuyenMai) {
        this.tenKhuyenMai = tenKhuyenMai;
    }

    public int getSoHoaDon() {
        return soHoaDon;
    }

    public void setSoHoaDon(int soHoaDon) {
        this.soHoaDon = soHoaDon;
    }

    public double getDoanhThu() {
        return doanhThu;
    }

    public void setDoanhThu(double doanhThu) {
        this.doanhThu = doanhThu;
    }
}
