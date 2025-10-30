package model;

public class DichVu {
    private String maDichVu;
    private String tenDichVu;
    private double gia;
    private String moTa;
    private String donViTinh;

    public DichVu(String maDichVu, String tenDichVu) {
        this.maDichVu = maDichVu;
        this.tenDichVu = tenDichVu;
    }

    public DichVu(String maDichVu, String tenDichVu, double gia, String moTa, String donViTinh) {
        this.maDichVu = maDichVu;
        this.tenDichVu = tenDichVu;
        this.gia = gia;
        this.moTa = moTa;
        this.donViTinh = donViTinh;
    }

    public DichVu(String tenDichVu, double gia, String moTa, String donViTinh) {
        this.tenDichVu = tenDichVu;
        this.gia = gia;
        this.moTa = moTa;
        this.donViTinh = donViTinh;
    }

    public DichVu(String maDichVu) {
        this.maDichVu = maDichVu;
    }

    public String getMaDichVu() {
        return maDichVu;
    }

    public String getTenDichVu() {
        return tenDichVu;
    }

    public void setTenDichVu(String tenDichVu) {
        this.tenDichVu = tenDichVu;
    }

    public double getGia() {
        return gia;
    }

    public void setGia(double gia) {
        this.gia = gia;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

}
