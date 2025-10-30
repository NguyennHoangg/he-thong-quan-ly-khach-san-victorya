package model;

public class TaiKhoan {
    private String tenDangNhap;
    private String matKhau;
    private String vaiTro;

    public TaiKhoan(String tenDangNhap, String matKhau, String vaiTro) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
    }

    public TaiKhoan(String tenDangNhap, String vaiTro) {
        this.tenDangNhap = tenDangNhap;
        this.vaiTro = vaiTro;
    }

    public TaiKhoan(String email) {
        //TODO Auto-generated constructor stub
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void settenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getVaiTro() {
        if (vaiTro == null)
            return "Không xác định";

        if (vaiTro.equalsIgnoreCase("employee"))
            return "Nhân viên";
        else if (vaiTro.equalsIgnoreCase("admin"))
            return "Quản lý";
        else
            return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        if (vaiTro == null || vaiTro.isBlank()) {
            this.vaiTro = "employee";
        } else {
            this.vaiTro = vaiTro;
        }
    }

}
