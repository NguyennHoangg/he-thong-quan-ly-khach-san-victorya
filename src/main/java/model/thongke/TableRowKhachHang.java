package model.thongke;

/**
 * Model cho dòng bảng top khách hàng
 */
public class TableRowKhachHang {
    private String maKhachHang;
    private String hoTen;
    private int soHoaDon;
    private double tongDoanhThu;

    public TableRowKhachHang() {
    }

    public TableRowKhachHang(String maKhachHang, String hoTen, int soHoaDon, double tongDoanhThu) {
        this.maKhachHang = maKhachHang;
        this.hoTen = hoTen;
        this.soHoaDon = soHoaDon;
        this.tongDoanhThu = tongDoanhThu;
    }

    // Getters and Setters
    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public int getSoHoaDon() {
        return soHoaDon;
    }

    public void setSoHoaDon(int soHoaDon) {
        this.soHoaDon = soHoaDon;
    }

    public double getTongDoanhThu() {
        return tongDoanhThu;
    }

    public void setTongDoanhThu(double tongDoanhThu) {
        this.tongDoanhThu = tongDoanhThu;
    }
}
