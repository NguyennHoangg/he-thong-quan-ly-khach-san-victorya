package model.thongke;

/**
 * Model cho dòng bảng thống kê nhân viên
 */
public class TableRowNhanVien {
    private String maNhanVien;
    private String tenNhanVien;
    private int soCa;
    private int soHoaDon;
    private double tongDoanhThu;

    public TableRowNhanVien() {
    }

    public TableRowNhanVien(String maNhanVien, String tenNhanVien, int soCa,
            int soHoaDon, double tongDoanhThu) {
        this.maNhanVien = maNhanVien;
        this.tenNhanVien = tenNhanVien;
        this.soCa = soCa;
        this.soHoaDon = soHoaDon;
        this.tongDoanhThu = tongDoanhThu;
    }

    // Getters and Setters
    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }

    public int getSoCa() {
        return soCa;
    }

    public void setSoCa(int soCa) {
        this.soCa = soCa;
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
