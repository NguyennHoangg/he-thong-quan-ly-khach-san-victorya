package model;

import java.time.LocalDateTime;

public class CaLamViecNhanVien {
    private String maCaLamViec;
    private String tenCaLamViec;
    private LocalDateTime thoiGianBatDau;
    private float tienMoCa;
    private double tienKetCa;
    private boolean trangThai;
    private Ca ca;
    private NhanVien nhanVien;
    
    public CaLamViecNhanVien(String maCaLamViec, String tenCaLamViec, LocalDateTime thoiGianBatDau, float tienMoCa,
            double tienKetCa, boolean trangThai, Ca ca, NhanVien nhanVien) {
        this.maCaLamViec = maCaLamViec;
        this.tenCaLamViec = tenCaLamViec;
        this.thoiGianBatDau = thoiGianBatDau;
        this.tienMoCa = tienMoCa;
        this.tienKetCa = tienKetCa;
        this.trangThai = trangThai;
        this.ca = ca;
        this.nhanVien = nhanVien;
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
    public boolean isTrangThai() {
        return trangThai;
    }
    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
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
