package model;

import javafx.beans.Observable;

public class    Phong {
    private String maPhong;
    private String soPhong;
    private LoaiPhong loaiPhong;
    private String trangThai;
    private int tang;
    private String tinhTrang;
    private String moTa;

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public Phong(String maPhong, String soPhong, LoaiPhong loaiPhong, String trangThai, int tang) {
        this.maPhong = maPhong;
        this.soPhong = soPhong;
        this.loaiPhong = loaiPhong;
        this.trangThai = trangThai;
        this.tang = tang;
    }


    public Phong(LoaiPhong loaiPhong, String maPhong, String moTa, String soPhong, int tang, String tinhTrang, String trangThai) {
        this.loaiPhong = loaiPhong;
        this.maPhong = maPhong;
        this.moTa = moTa;
        this.soPhong = soPhong;
        this.tang = tang;
        this.tinhTrang = tinhTrang;
        this.trangThai = trangThai;
    }

    public Phong(String maPhong) {
        this.maPhong = maPhong;
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

    public int getTang() {
        return tang;
    }

    public void setTang(int tang) {
        this.tang = tang;
    }




    public String getTinhTrang() {
        return tinhTrang;
    }



    public void setTinhTrang(String tinhTrang) {
        this.tinhTrang = tinhTrang;
    }

    
}
