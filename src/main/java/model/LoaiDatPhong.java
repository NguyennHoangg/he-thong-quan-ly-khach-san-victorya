package model;

import java.time.LocalDate;

public class LoaiDatPhong {
    private String maLoaiDatPhong;
    private String tenLoaiDatPhong;
    private LocalDate ngayTao;

    public LoaiDatPhong(String maLoaiDatPhong, String tenLoaiDatPhong, LocalDate ngayTao) {
        this.maLoaiDatPhong = maLoaiDatPhong;
        this.tenLoaiDatPhong = tenLoaiDatPhong;
        this.ngayTao = ngayTao;
    }

    public LoaiDatPhong(String maLoaiDatPhong) {
        this.maLoaiDatPhong = maLoaiDatPhong;
    }

    public String getMaLoaiDatPhong() {
        return maLoaiDatPhong;
    }

    public String getTenLoaiDatPhong() {
        return tenLoaiDatPhong;
    }

    public void setTenLoaiDatPhong(String tenLoaiDatPhong) {
        this.tenLoaiDatPhong = tenLoaiDatPhong;
    }

    public LocalDate getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDate ngayTao) {
        this.ngayTao = ngayTao;
    }

}
