package model;

import java.time.LocalDate;

public class LoaiPhong {
    private String maLoaiPhong;
    private String tenLoaiPhong;
    private double gia;
    private LocalDate ngayTao;

    public LoaiPhong(String maLoaiPhong, String tenLoaiPhong, double gia, LocalDate ngayTao) {
        this.maLoaiPhong = maLoaiPhong;
        this.tenLoaiPhong = tenLoaiPhong;
        this.gia = gia;
        this.ngayTao = ngayTao;
    }

    public LoaiPhong(String maLoaiPhong) {
        this.maLoaiPhong = maLoaiPhong;
    }

    public String getMaLoaiPhong() {
        return maLoaiPhong;
    }

    public String getTenLoaiPhong() {
        return tenLoaiPhong;
    }

    public double getGia() {
        return gia;
    }

    public LocalDate getNgayTao() {
        return ngayTao;
    }

    public void setTenLoaiPhong(String tenLoaiPhong) {
        this.tenLoaiPhong = tenLoaiPhong;
    }

    public void setGia(double gia) {
        this.gia = gia;
    }

    public void setNgayTao(LocalDate ngayTao) {
        this.ngayTao = ngayTao;
    }

}
