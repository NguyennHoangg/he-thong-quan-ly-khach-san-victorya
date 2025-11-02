package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

        try (Connection connect = ConnectDatabase.getConnection();
                Statement stmt = connect.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

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
                String diaChi = rs.getString("diaChi");
                String cccd = rs.getString("CCCD");
                String trangThai = rs.getString("trangThai");

                TaiKhoan tk = new TaiKhoan(tenDangNhap, vaiTro);
                NhanVien nv = new NhanVien(maNV, cccd, ten, tk, gioiTinh, ngaySinh, email, soDienThoai, ngayBatDau,
                        trangThai,
                        diaChi);
                dsKetQua.add(nv);
            }
        } catch (Exception e) {
            return dsKetQua;
        }
        return dsKetQua;
    }

    /**
     * Lấy nhân viên theo tên đăng nhập (join với bảng TaiKhoan).
     */
    public NhanVien findByUsername(String tenDangNhap) {
        String sql = "SELECT nv.maNhanVien, nv.CCCD, nv.tenNhanVien, nv.gioiTinh, nv.ngaySinh, nv.email, nv.soDienThoai, nv.ngayBatDau, nv.trangThai, nv.diaChi, "
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
                            rs.getString("CCCD"),
                            rs.getString("tenNhanVien"),
                            tk,
                            rs.getBoolean("gioiTinh"),
                            ngaySinh,
                            rs.getString("email"),
                            rs.getString("soDienThoai"),
                            ngayBatDau,
                            rs.getString("trangThai"),
                            rs.getString("diaChi"));
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

    public boolean themNhanVien(NhanVien nv) {
        TaiKhoan_DAO tkDAO = new TaiKhoan_DAO();
        String sqlNV = "INSERT INTO NhanVien (maNhanVien, tenNhanVien, tenDangNhap, gioiTinh, ngaySinh, email, soDienThoai, trangThai, ngayBatDau, CCCD, diaChi) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection con = null;
        PreparedStatement stmtNV = null;

        try {
            con = ConnectDatabase.getConnection();
            con.setAutoCommit(false);

            if (!tkDAO.themTaiKhoan(nv.getTaiKhoan())) {
                con.rollback(); // rollback nếu thêm tài khoản thất bại
                return false;
            }

            stmtNV = con.prepareStatement(sqlNV);
            stmtNV.setString(1, nv.getMaNhanVien());
            stmtNV.setString(2, nv.getTenNhanVien());
            stmtNV.setString(3, nv.getTaiKhoan().getTenDangNhap());
            stmtNV.setBoolean(4, nv.isGioiTinh());
            stmtNV.setDate(5, Date.valueOf(nv.getNgaySinh()));
            stmtNV.setString(6, nv.getEmail());
            stmtNV.setString(7, nv.getSoDienThoai());
            stmtNV.setString(8, "Đang làm việc");
            stmtNV.setDate(9, Date.valueOf(LocalDate.now()));
            stmtNV.setString(10, nv.getCCCD());
            stmtNV.setString(11, nv.getDiaChi());

            int rowsNV = stmtNV.executeUpdate();

            if (rowsNV > 0) {
                con.commit(); // Thành công cả 2, commit
                return true;
            } else {
                con.rollback(); // Nếu nhân viên không thêm được
                return false;
            }

        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback(); // ✅ Rollback nếu có lỗi SQL
                } catch (SQLException ex) {
                    return false;
                }
            }
            return false;
        } finally {
            try {
                if (stmtNV != null)
                    stmtNV.close();
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException e) {
                return false;
            }
        }
    }

    public String getMaxMaNhanVien() {
        String sql = "SELECT MAX(maNhanVien) AS maxMa FROM NhanVien";

        try (Connection con = ConnectDatabase.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getString("maxMa"); // có thể null nếu chưa có bản ghi
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public boolean capNhatNhanVien(NhanVien nv) {
        TaiKhoan_DAO tkDAO = new TaiKhoan_DAO();
        String vaiTroValue = nv.getTaiKhoan().getVaiTro().equalsIgnoreCase("Quản lý") ? "admin" : "employee";
        tkDAO.updateRole(nv.getTaiKhoan().getTenDangNhap(), vaiTroValue);
        String sql = "UPDATE NhanVien SET tenNhanVien=?, gioiTinh=?, ngaySinh=?, email=?, soDienThoai=?, diaChi=? WHERE maNhanVien=?";
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nv.getTenNhanVien());
            ps.setBoolean(2, nv.isGioiTinh());
            ps.setDate(3, nv.getNgaySinh() != null ? Date.valueOf(nv.getNgaySinh()) : null);
            ps.setString(4, nv.getEmail());
            ps.setString(5, nv.getSoDienThoai());
            ps.setString(6, nv.getDiaChi());
            ps.setString(7, nv.getMaNhanVien());

            return ps.executeUpdate() == 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public NhanVien timNhanVienTheoCCCD(String cccd) {
        String sql = "SELECT nv.maNhanVien, nv.tenNhanVien, nv.gioiTinh, nv.ngaySinh, nv.email, " +
                "nv.soDienThoai, nv.ngayBatDau, nv.CCCD, nv.trangThai, nv.diaChi, " +
                "tk.tenDangNhap, tk.matKhau, tk.vaiTro " +
                "FROM NhanVien nv " +
                "JOIN TaiKhoan tk ON nv.tenDangNhap = tk.tenDangNhap " +
                "WHERE nv.CCCD = ?";

        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cccd);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TaiKhoan tk = new TaiKhoan(
                            rs.getString("tenDangNhap"),
                            rs.getString("matKhau"),
                            rs.getString("vaiTro"));
                    LocalDate ngaySinh = rs.getDate("ngaySinh") != null ? rs.getDate("ngaySinh").toLocalDate() : null;
                    return new NhanVien(
                            rs.getString("maNhanVien"),
                            rs.getString("CCCD"),
                            rs.getString("tenNhanVien"),
                            tk,
                            rs.getBoolean("gioiTinh"),
                            ngaySinh,
                            rs.getString("email"),
                            rs.getString("soDienThoai"),
                            rs.getDate("ngayBatDau").toLocalDate(),
                            rs.getString("trangThai"),
                            rs.getString("diaChi"));
                }
            }
        } catch (Exception ex) {
            return null;
        }
        return null;
    }

    public boolean xoaNhanVienTheoCCCD(NhanVien nv) {
        TaiKhoan_DAO tkDAO = new TaiKhoan_DAO();
        boolean daXoaTaiKhoan = tkDAO.xoaTaiKhoanTheoTenDN(nv.getTaiKhoan().getTenDangNhap());

        if (!daXoaTaiKhoan) {
            System.out.println("");
        }

        String sql = "DELETE FROM NhanVien WHERE CCCD = ?";

        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nv.getCCCD());
            int deleted = ps.executeUpdate();

            return deleted > 0;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Cập nhật số điện thoại cho nhân viên
     * 
     * @param maNhanVien     Mã nhân viên
     * @param soDienThoaiMoi Số điện thoại mới
     * @return true nếu cập nhật thành công
     */
    public boolean updatePhone(String maNhanVien, String soDienThoaiMoi) {
        String sql = "UPDATE NhanVien SET soDienThoai = ? WHERE maNhanVien = ?";
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, soDienThoaiMoi);
            ps.setString(2, maNhanVien);
            return ps.executeUpdate() == 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật email cho nhân viên
     * 
     * @param maNhanVien Mã nhân viên
     * @param emailMoi   Email mới
     * @return true nếu cập nhật thành công
     */
    public boolean updateEmail(String maNhanVien, String emailMoi) {
        String sql = "UPDATE NhanVien SET email = ? WHERE maNhanVien = ?";
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, emailMoi);
            ps.setString(2, maNhanVien);
            return ps.executeUpdate() == 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
