package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import config.ConnectDatabase;
import model.NhanVien;
import model.TaiKhoan;

/**
 * DAO cho bảng NhanVien: nạp và cập nhật thông tin hồ sơ nhân viên.
 */
public class NhanVien_DAO {

    public List<NhanVien> getDsNhanVien() {
        List<NhanVien> dsKetQua = new ArrayList<>();
        String sql = "Select * from NhanVien nv join TaiKhoan tk on tk.tenDangNhap = nv.tenDangNhap";
        try {
            Connection connect = ConnectDatabase.getConnection();
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String maNV = rs.getString("maNhanVien");
                String ten = rs.getString("tenNhanVien");
                String tenDangNhap = rs.getString("tenDangNhap");
                String vaiTro = rs.getString("vaiTro");
                Boolean gioiTinh = rs.getBoolean("gioiTinh");
                LocalDate ngaySinh = rs.getDate("ngaySinh").toLocalDate();
                String email = rs.getString("email");
                String soDienThoai = rs.getString("soDienThoai");
                LocalDate ngayBatDau = rs.getDate("ngayBatDau").toLocalDate();

                TaiKhoan tk = new TaiKhoan(tenDangNhap, vaiTro);
                NhanVien nv = new NhanVien(maNV, ten, tk, gioiTinh, ngaySinh, email, soDienThoai, ngayBatDau);
                dsKetQua.add(nv);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return dsKetQua;
    }

    /**
     * Lấy nhân viên theo tên đăng nhập (join với bảng TaiKhoan).
     */
    public NhanVien findByUsername(String tenDangNhap) {
        String sql = "SELECT nv.maNhanVien, nv.tenNhanVien, nv.gioiTinh, nv.ngaySinh, nv.email, nv.soDienThoai, nv.ngayBatDau, "
                +
                "tk.tenDangNhap, tk.matKhau, tk.vaiTro " +
                "FROM NhanVien nv JOIN TaiKhoan tk ON nv.tenDangNhap = tk.tenDangNhap " +
                "WHERE tk.tenDangNhap = ?";
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TaiKhoan tk = new TaiKhoan(
                            rs.getString("tenDangNhap"),
                            rs.getString("matKhau"),
                            rs.getString("vaiTro"));
                    LocalDate ngaySinh = rs.getDate("ngaySinh") != null ? rs.getDate("ngaySinh").toLocalDate() : null;
                    LocalDate ngayBatDau = rs.getDate("ngayBatDau") != null ? rs.getDate("ngayBatDau").toLocalDate()
                            : null;
                    return new NhanVien(
                            rs.getString("maNhanVien"),
                            rs.getString("tenNhanVien"),
                            tk,
                            rs.getBoolean("gioiTinh"),
                            ngaySinh,
                            rs.getString("email"),
                            rs.getString("soDienThoai"),
                            ngayBatDau);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Cập nhật thông tin nhân viên cơ bản (không đổi username).
     */
    public boolean updateProfile(NhanVien nv) {
        String sql = "UPDATE NhanVien SET tenNhanVien=?, gioiTinh=?, ngaySinh=?, email=?, soDienThoai=? WHERE maNhanVien=?";
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getTenNhanVien());
            ps.setBoolean(2, nv.isGioiTinh());
            ps.setDate(3, nv.getNgaySinh() != null ? Date.valueOf(nv.getNgaySinh()) : null);
            ps.setString(4, nv.getEmail());
            ps.setString(5, nv.getSoDienThoai());
            ps.setString(6, nv.getMaNhanVien());
            return ps.executeUpdate() == 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
