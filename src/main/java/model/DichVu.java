package model;

public class DichVu {
    private String maDichVu;
    private String tenDichVu;
    private float gia;
    private String moTa;
    private String donViTinh;

    public DichVu(String maDichVu, String tenDichVu){
        this.maDichVu = maDichVu;
        this.tenDichVu = tenDichVu;
    }

    public DichVu(String maDichVu, String tenDichVu, float gia, String moTa, String donViTinh) {
        this.maDichVu = maDichVu;
        this.tenDichVu = tenDichVu;
        this.gia = gia;
        this.moTa = moTa;
        this.donViTinh = donViTinh;
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

    public float getGia() {
        return gia;
    }

    public void setGia(float gia) {
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
