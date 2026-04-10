package dao;

import model.TaiKhoan;

public class TaiKhoan_DAO {
    public TaiKhoan findByUsername(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.isEmpty()) return null;
        String vaiTro = "admin".equalsIgnoreCase(tenDangNhap) ? "admin" : "employee";
        return new TaiKhoan(tenDangNhap, "mock_hash", vaiTro);
    }
    public boolean updatePassword(String tenDangNhap, String matKhauHash) { return true; }
    public boolean updateRole(String tenDangNhap, String vaiTro) { return true; }
    public boolean themTaiKhoan(TaiKhoan tk) { return true; }
    public boolean xoaTaiKhoanTheoTenDN(String tenDN) { return true; }
}
