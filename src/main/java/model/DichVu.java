package model;

public class DichVu {
    private String maDichVu;
    private String tenDichVu;
    private double gia;
    private String moTa;
    private String donViTinh;

    public DichVu(String maDichVu, String tenDichVu) {
        this.maDichVu = maDichVu;
        setTenDichVu(tenDichVu);
    }

    public DichVu(String maDichVu, String tenDichVu, double gia, String moTa, String donViTinh) {
        this.maDichVu = maDichVu;
        setTenDichVu(tenDichVu);
        setGia(gia);
        this.moTa = moTa;
        this.donViTinh = donViTinh;
    }

    public DichVu(String tenDichVu, double gia, String moTa, String donViTinh) {
        setTenDichVu(tenDichVu);
        setGia(gia);
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
        if (tenDichVu == null || tenDichVu.trim().isEmpty()) {
            this.tenDichVu = "Unnamed Service";
        } else {
            this.tenDichVu = tenDichVu.trim();
        }
    }

    public double getGia() {
        return gia;
    }

    public void setGia(double gia) {
        if (gia < 0) this.gia = 0;
        else this.gia = gia;
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

    @Override
    public String toString(){
        return tenDichVu;
    }

}
