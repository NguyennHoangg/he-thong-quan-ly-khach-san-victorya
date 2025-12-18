package model.thongke;

/**
 * Model cho dòng bảng thống kê phòng
 */
public class TableRowPhong {
    private String maPhong;
    private String soPhong;
    private String tenLoaiPhong;
    private int tang;
    private String trangThai;
    private String tinhTrang;
    private int luotDat;
    private double doanhThuPhong;
    private double congSuat; // %
    private int luotHuy;
    private double tiLeHuy; // %

    public TableRowPhong() {
    }

    public TableRowPhong(String maPhong, String soPhong, String tenLoaiPhong,
            int tang, String trangThai, String tinhTrang) {
        this.maPhong = maPhong;
        this.soPhong = soPhong;
        this.tenLoaiPhong = tenLoaiPhong;
        this.tang = tang;
        this.trangThai = trangThai;
        this.tinhTrang = tinhTrang;
    }

    public TableRowPhong(String maPhong, String soPhong, String tenLoaiPhong,
            int tang, String trangThai, String tinhTrang,
            int luotDat, double doanhThuPhong, double congSuat,
            int luotHuy, double tiLeHuy) {
        this.maPhong = maPhong;
        this.soPhong = soPhong;
        this.tenLoaiPhong = tenLoaiPhong;
        this.tang = tang;
        this.trangThai = trangThai;
        this.tinhTrang = tinhTrang;
        this.luotDat = luotDat;
        this.doanhThuPhong = doanhThuPhong;
        this.congSuat = congSuat;
        this.luotHuy = luotHuy;
        this.tiLeHuy = tiLeHuy;
    }

    // Getters and Setters
    public String getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(String maPhong) {
        this.maPhong = maPhong;
    }

    public String getSoPhong() {
        return soPhong;
    }

    public void setSoPhong(String soPhong) {
        this.soPhong = soPhong;
    }

    public String getTenLoaiPhong() {
        return tenLoaiPhong;
    }

    public void setTenLoaiPhong(String tenLoaiPhong) {
        this.tenLoaiPhong = tenLoaiPhong;
    }

    public int getTang() {
        return tang;
    }

    public void setTang(int tang) {
        this.tang = tang;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(String tinhTrang) {
        this.tinhTrang = tinhTrang;
    }

    public int getLuotDat() {
        return luotDat;
    }

    public void setLuotDat(int luotDat) {
        this.luotDat = luotDat;
    }

    public double getDoanhThuPhong() {
        return doanhThuPhong;
    }

    public void setDoanhThuPhong(double doanhThuPhong) {
        this.doanhThuPhong = doanhThuPhong;
    }

    public double getCongSuat() {
        return congSuat;
    }

    public void setCongSuat(double congSuat) {
        this.congSuat = congSuat;
    }

    public int getLuotHuy() {
        return luotHuy;
    }

    public void setLuotHuy(int luotHuy) {
        this.luotHuy = luotHuy;
    }

    public double getTiLeHuy() {
        return tiLeHuy;
    }

    public void setTiLeHuy(double tiLeHuy) {
        this.tiLeHuy = tiLeHuy;
    }
}
