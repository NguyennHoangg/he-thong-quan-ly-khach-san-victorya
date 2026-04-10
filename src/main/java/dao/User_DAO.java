package dao;

import model.NhanVien;
import model.TaiKhoan;

public class User_DAO {
    public TaiKhoan timKiemTheoTenDangNhap(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.isEmpty()) return null;
        String vaiTro = "admin".equalsIgnoreCase(tenDangNhap) ? "admin" : "employee";
        return new TaiKhoan(tenDangNhap, "mock_hash", vaiTro);
    }

    public NhanVien timKiemTheoCCCD(String CCCD) {
        TaiKhoan tk = new TaiKhoan("admin", "mock_hash", "admin");
        NhanVien nv = new NhanVien("mock@victorya.com");
        nv.setTaiKhoan(tk);
        return nv;
    }
}
