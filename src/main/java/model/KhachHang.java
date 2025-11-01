package model;

import java.time.LocalDate;

public class KhachHang {
    private String maKhachHang;
    private String CCCD;
    private String tenKhachHang;
    private String soDienThoai;
    private String email;
    private LocalDate ngayTao;

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }
    public KhachHang(){}
    public KhachHang(String maKhachHang, String CCCD, String tenKhachHang, String soDienThoai, String email,
                     LocalDate ngayTao) {
        this.maKhachHang = maKhachHang;
        this.CCCD = CCCD;
        this.tenKhachHang = tenKhachHang;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.ngayTao = ngayTao;
    }
public KhachHang(String maKH){
        this.maKhachHang= maKH;
}
    

    public KhachHang(String maKhachHang, String cCCD, String tenKhachHang, String soDienThoai, String email) {
        this.maKhachHang = maKhachHang;
        CCCD = cCCD;
        this.tenKhachHang = tenKhachHang;
        this.soDienThoai = soDienThoai;
        this.email = email;
    }



    public String getMaKhachHang() {
        return maKhachHang;
    }

    public String getCCCD() {
        return CCCD;
    }

    public void setCCCD(String cCCD) {
        CCCD = cCCD;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDate ngayTao) {
        this.ngayTao = ngayTao;
    }

        
}
