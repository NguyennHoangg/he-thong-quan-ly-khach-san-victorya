package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoaiPhong {
    private String maLoaiPhong;
    private String tenLoaiPhong;
    private double gia;
    private LocalDate ngayTao;
    private List<DichVu> dsachDichVu = new ArrayList<>();

    

    public LoaiPhong(String maLoaiPhong, String tenLoaiPhong, float gia, LocalDate ngayTao, List<DichVu> dsachDichVu) {
        this.maLoaiPhong = maLoaiPhong;
        this.tenLoaiPhong = tenLoaiPhong;
        this.gia = gia;
        this.ngayTao = ngayTao;
        this.dsachDichVu = dsachDichVu;
    }

    public LoaiPhong(String maLoaiPhong2, String tenLoaiPhong2, double gia2, List<DichVu> dsDichVu) {
        this.maLoaiPhong = maLoaiPhong2;
        this.tenLoaiPhong = tenLoaiPhong2;
        this.gia = (float) gia2;
        this.dsachDichVu = dsDichVu != null ? dsDichVu : new ArrayList<>();
    }

    public LoaiPhong(String string, String string2, int i, LocalDate now) {
        this.maLoaiPhong = string;
        this.tenLoaiPhong = string2;
        this.gia = i;
        this.ngayTao = now;
        this.dsachDichVu = new ArrayList<>();
    }

    public void setMaLoaiPhong(String maLoaiPhong) {
        this.maLoaiPhong = maLoaiPhong;
    }

    public List<DichVu> getDsachDichVu() {
        return dsachDichVu;
    }

    public void setDsachDichVu(List<DichVu> dsachDichVu) {
        this.dsachDichVu = dsachDichVu;
    }

    public LoaiPhong(String maLoaiPhong) {
        this.maLoaiPhong = maLoaiPhong;
    }

    public LoaiPhong(String maLP, String tenLP, double gia2, LocalDate ngayTao2) {
        //TODO Auto-generated constructor stub
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
