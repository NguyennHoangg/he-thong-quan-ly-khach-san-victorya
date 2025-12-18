package model.thongke;

/**
 * Model cho dòng bảng thống kê dịch vụ
 */
public class TableRowDichVu {
    private String maDichVu;
    private String tenDichVu;
    private int tongSoLuong;
    private double tongDoanhThu;

    public TableRowDichVu() {
    }

    public TableRowDichVu(String maDichVu, String tenDichVu, int tongSoLuong, double tongDoanhThu) {
        this.maDichVu = maDichVu;
        this.tenDichVu = tenDichVu;
        this.tongSoLuong = tongSoLuong;
        this.tongDoanhThu = tongDoanhThu;
    }

    // Getters and Setters
    public String getMaDichVu() {
        return maDichVu;
    }

    public void setMaDichVu(String maDichVu) {
        this.maDichVu = maDichVu;
    }

    public String getTenDichVu() {
        return tenDichVu;
    }

    public void setTenDichVu(String tenDichVu) {
        this.tenDichVu = tenDichVu;
    }

    public int getTongSoLuong() {
        return tongSoLuong;
    }

    public void setTongSoLuong(int tongSoLuong) {
        this.tongSoLuong = tongSoLuong;
    }

    public double getTongDoanhThu() {
        return tongDoanhThu;
    }

    public void setTongDoanhThu(double tongDoanhThu) {
        this.tongDoanhThu = tongDoanhThu;
    }
}
