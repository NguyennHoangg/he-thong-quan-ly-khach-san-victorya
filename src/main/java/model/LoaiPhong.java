package model;

import java.time.LocalDate;

public class LoaiPhong {
    private String maLoaiPhong;
    private String tenLoaiPhong;
    private float gia;
    private LocalDate ngayTao;

    //Contructor
    public LoaiPhong(String maLoaiPhong, String tenLoaiPhong, float gia, LocalDate ngayTao) {
        this.maLoaiPhong = maLoaiPhong;
        this.tenLoaiPhong = tenLoaiPhong;
        this.gia = gia;
        this.ngayTao = ngayTao;
    }

    public String getMaLoaiPhong() {
        return maLoaiPhong;
    }

    public String getTenLoaiPhong() {
        return tenLoaiPhong;
    }

    public float getGia() {
        return gia;
    }

    public LocalDate getNgayTao() {
        return ngayTao;
    }

    public void setTenLoaiPhong(String tenLoaiPhong) {
        this.tenLoaiPhong = tenLoaiPhong;
    }

    public void setGia(float gia) {
        this.gia = gia;
    }

    public void setNgayTao(LocalDate ngayTao) {
        this.ngayTao = ngayTao;
    }

    
}
