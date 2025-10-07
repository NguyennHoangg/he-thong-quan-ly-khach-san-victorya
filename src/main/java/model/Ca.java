package model;

import java.time.LocalDate;

public class Ca {
    private String maCa;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    
    public Ca(String maCa, LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        this.maCa = maCa;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
    }

    public String getMaCa() {
        return maCa;
    }

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }

    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    
}
