package model;

import java.time.LocalDateTime;
import java.util.Date;

public class CaLamViecNhanVien {
    private String maCaLamViec;
    private String tenCaLamViec;
    private LocalDateTime thoiGianBatDau;
    private float tienMoCa;
    private double tienKetCa;
    private String trangThai; // Changed from boolean to String
    private double tongChi;
    private double tongThu;
    private Ca ca;
    private NhanVien nhanVien;
    private Date ngay;
    
    public CaLamViecNhanVien(String maCaLamViec, String tenCaLamViec, LocalDateTime thoiGianBatDau, float tienMoCa,
            double tienKetCa, String trangThai, Ca ca, NhanVien nhanVien) {
        this.maCaLamViec = maCaLamViec;
        this.tenCaLamViec = tenCaLamViec;
        this.thoiGianBatDau = thoiGianBatDau;
        this.tienMoCa = tienMoCa;
        this.tienKetCa = tienKetCa;
        this.trangThai = trangThai;
        this.ca = ca;
        this.nhanVien = nhanVien;
    }

    

    
    public CaLamViecNhanVien(String maCaLamViec, String tenCaLamViec, LocalDateTime thoiGianBatDau, float tienMoCa,
            double tienKetCa, String trangThai, double tongChi, double tongThu, Ca ca, NhanVien nhanVien) {
        this.maCaLamViec = maCaLamViec;
        this.tenCaLamViec = tenCaLamViec;
        this.thoiGianBatDau = thoiGianBatDau;
        this.tienMoCa = tienMoCa;
        this.tienKetCa = tienKetCa;
        this.trangThai = trangThai;
        this.tongChi = tongChi;
        this.tongThu = tongThu;
        this.ca = ca;
        this.nhanVien = nhanVien;
    }


    

    public String getMaCaLamViec() {
        return maCaLamViec;
    }




    public double getTongChi() {
        return tongChi;
    }




    public void setTongChi(double tongChi) {
        this.tongChi = tongChi;
    }




    public double getTongThu() {
        return tongThu;
    }




    public void setTongThu(double tongThu) {
        this.tongThu = tongThu;
    }


    public String getTenCaLamViec() {
        return tenCaLamViec;
    }
    public void setTenCaLamViec(String tenCaLamViec) {
        this.tenCaLamViec = tenCaLamViec;
    }
    public LocalDateTime getThoiGianBatDau() {
        return thoiGianBatDau;
    }
    public void setThoiGianBatDau(LocalDateTime thoiGianBatDau) {
        this.thoiGianBatDau = thoiGianBatDau;
    }
    public float getTienMoCa() {
        return tienMoCa;
    }
    public void setTienMoCa(float tienMoCa) {
        this.tienMoCa = tienMoCa;
    }
    public double getTienKetCa() {
        return tienKetCa;
    }
    public void setTienKetCa(double tienKetCa) {
        this.tienKetCa = tienKetCa;
    }
    public String getTrangThai() {
        return trangThai;
    }
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
    public Date getNgay() {
        return ngay;
    }
    public void setNgay(Date ngay) {
        this.ngay = ngay;
    }
    public Ca getCa() {
        return ca;
    }
    public void setCa(Ca ca) {
        this.ca = ca;
    }
    public NhanVien getNhanVien() {
        return nhanVien;
    }
    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }


    public void setMaCaLamViec(String maCaLamViec) {
        this.maCaLamViec = maCaLamViec;
    }
    
    
    
}
