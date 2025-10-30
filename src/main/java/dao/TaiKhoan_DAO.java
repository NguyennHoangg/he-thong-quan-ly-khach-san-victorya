package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import config.ConnectDatabase;
import model.TaiKhoan;

/**
 * DAO cho bảng TaiKhoan: nạp và cập nhật thông tin tài khoản đăng nhập.
 */
public class TaiKhoan_DAO {

    /**
     * Lấy tài khoản theo tên đăng nhập.
     */
    public TaiKhoan findByUsername(String tenDangNhap) {
        String sql = "SELECT tenDangNhap, matKhau, vaiTro FROM TaiKhoan WHERE tenDangNhap = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new TaiKhoan(
                        rs.getString("tenDangNhap"),
                        rs.getString("matKhau"),
                        rs.getString("vaiTro")
                    );
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Cập nhật mật khẩu (đã băm) cho tài khoản.
     */
    public boolean updatePassword(String tenDangNhap, String matKhauHash) {
        String sql = "UPDATE TaiKhoan SET matKhau = ? WHERE tenDangNhap = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matKhauHash);
            ps.setString(2, tenDangNhap);
            return ps.executeUpdate() == 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật vai trò.
     */
    public boolean updateRole(String tenDangNhap, String vaiTro) {
        String sql = "UPDATE TaiKhoan SET vaiTro = ? WHERE tenDangNhap = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vaiTro);
            ps.setString(2, tenDangNhap);
            return ps.executeUpdate() == 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Thêm tài khoản mới
     */
    public boolean themTaiKhoan(TaiKhoan tk) {
        String sql = "INSERT INTO TaiKhoan (tenDangNhap, matKhau, vaiTro) VALUES (?, ?, ?)";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tk.getTenDangNhap());
            ps.setString(2, tk.getMatKhau());
            ps.setString(3, tk.getVaiTro());
            return ps.executeUpdate() == 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
