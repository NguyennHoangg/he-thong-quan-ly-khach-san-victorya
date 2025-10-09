package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CaLamViecNhanVien {
    private String maCaLamViecNhanVien;
    private String tenCaLamViec;
    private LocalDateTime thoiGianBatDau;
    private float tienCa;
    private boolean trangThai;
    private Ca ca;
    private NhanVien nhanVien;
    
    public CaLamViecNhanVien(String maCaLamViecNhanVien, String tenCaLamViec, LocalDateTime thoiGianBatDau,
            float tienCa, boolean trangThai, Ca ca, NhanVien nhanVien) {
        this.maCaLamViecNhanVien = maCaLamViecNhanVien;
        this.tenCaLamViec = tenCaLamViec;
        this.thoiGianBatDau = thoiGianBatDau;
        this.tienCa = tienCa;
        this.trangThai = trangThai;
        this.ca = ca;
        this.nhanVien = nhanVien;
    }

    public String getMaCaLamViecNhanVien() {
        return maCaLamViecNhanVien;
    }

    public String getTenCaLamViec() {
        return tenCaLamViec;
    }

    public LocalDateTime getThoiGianBatDau() {
        return thoiGianBatDau;
    }

    public float getTienCa() {
        return tienCa;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public Ca getCa() {
        return ca;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setMaCaLamViecNhanVien(String maCaLamViecNhanVien) {
        this.maCaLamViecNhanVien = maCaLamViecNhanVien;
    }

    
}
