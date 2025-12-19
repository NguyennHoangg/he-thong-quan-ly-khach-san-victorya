package model;

import java.time.LocalDateTime;


public class HuyPhong {

    private int maHuyPhong;
    private String maPhieuDatPhong;
    private String lyDo;
    private LocalDateTime ngayHuy;

    public HuyPhong() {
    }

    public HuyPhong(int maHuyPhong, String maPhieuDatPhong, String lyDo, LocalDateTime ngayHuy) {
        this.maHuyPhong = maHuyPhong;
        this.maPhieuDatPhong = maPhieuDatPhong;
        this.lyDo = lyDo;
        this.ngayHuy = ngayHuy;
    }
    public int getMaHuyPhong() {
        return maHuyPhong;
    }

    public void setMaHuyPhong(int maHuyPhong) {
        this.maHuyPhong = maHuyPhong;
    }

    public String getMaPhieuDatPhong() {
        return maPhieuDatPhong;
    }

    public void setMaPhieuDatPhong(String maPhieuDatPhong) {
        this.maPhieuDatPhong = maPhieuDatPhong;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public LocalDateTime getNgayHuy() {
        return ngayHuy;
    }

    public void setNgayHuy(LocalDateTime ngayHuy) {
        this.ngayHuy = ngayHuy;
    }

    @Override
    public String toString() {
        return "HuyPhong{" +
                "maHuyPhong=" + maHuyPhong +
                ", maPhieuDatPhong='" + maPhieuDatPhong + '\'' +
                ", lyDo='" + lyDo + '\'' +
                ", ngayHuy=" + ngayHuy +
                '}';
    }
}