package model;

public class Phong {
    private String maPhong;
    private String soPhong;
    private LoaiPhong loaiPhong;
    private String trangThai;

    public Phong(String maPhong, String soPhong, LoaiPhong loaiPhong, String trangThai) {
        this.maPhong = maPhong;
        this.soPhong = soPhong;
        this.loaiPhong = loaiPhong;
        this.trangThai = trangThai;
    }

    public void setSoPhong(String soPhong) {
        this.soPhong = soPhong;
    }

    public void setLoaiPhong(LoaiPhong loaiPhong) {
        this.loaiPhong = loaiPhong;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getMaPhong() {
        return maPhong;
    }

    public String getSoPhong() {
        return soPhong;
    }

    public LoaiPhong getLoaiPhong() {
        return loaiPhong;
    }

    public String getTrangThai() {
        return trangThai;
    }

}
