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
            e.printStackTrace();
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

    public boolean themNhanVien(NhanVien nv) {
        TaiKhoan_DAO tkDAO = new TaiKhoan_DAO();
        String sql = "INSERT INTO NhanVien (maNhanVien, tenNhanVien, tenDangNhap, gioiTinh, ngaySinh, email, soDienThoai, trangThai, ngayBatDau, CCCD, diaChi) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?)";

        try (Connection con = ConnectDatabase.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql)) {

            // 1. Sinh mã mới
            String maNV = phatSinhMaNhanVien();

            // 2. Thêm tài khoản
            if (!tkDAO.themTaiKhoan(nv.getTaiKhoan())) {
                System.out.println("Không thể thêm tài khoản cho nhân viên " + nv.getTenNhanVien());
                return false;
            }

            // 3. Set dữ liệu
            stmt.setString(1, maNV);
            stmt.setString(2, nv.getTenNhanVien());
            stmt.setString(3, nv.getTaiKhoan().getTenDangNhap());
            stmt.setBoolean(4, nv.isGioiTinh());
            stmt.setDate(5, Date.valueOf(nv.getNgaySinh()));
            stmt.setString(6, nv.getEmail());
            stmt.setString(7, nv.getSoDienThoai());
            stmt.setString(8, "Đang làm việc");
            stmt.setDate(9, Date.valueOf(LocalDate.now()));
            stmt.setString(10, nv.getCCCD());
            stmt.setString(11, nv.getDiaChi());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String phatSinhMaNhanVien() {
        String sql = "SELECT TOP 1 maNhanVien FROM NhanVien ORDER BY maNhanVien DESC"; // Giá trị đầu tiên trong bảng từ
                                                                                       // cao xuống

        try (Connection con = ConnectDatabase.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String lastMa = rs.getString("maNhanVien"); // Mã nhân viên đã thêm gần nhất
                int number = Integer.parseInt(lastMa.substring(2)); // bỏ 2 phần từ NV
                number++;
                return String.format("NV%03d", number);
            } else {
                return "NV001";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean capNhatNhanVien(NhanVien nv) {
        TaiKhoan_DAO tkDAO = new TaiKhoan_DAO();
        String vaiTroValue = nv.getTaiKhoan().getVaiTro().equalsIgnoreCase("Quản lý") ? "admin" : "employee";
        tkDAO.updateRole(nv.getTaiKhoan().getTenDangNhap(), vaiTroValue);
        String sql = "UPDATE NhanVien SET tenNhanVien=?, gioiTinh=?, ngaySinh=?, email=?, soDienThoai=?, diaChi=? WHERE CCCD=?";
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getTenNhanVien());
            ps.setBoolean(2, nv.isGioiTinh());
            ps.setDate(3, nv.getNgaySinh() != null ? Date.valueOf(nv.getNgaySinh()) : null);
            ps.setString(4, nv.getEmail());
            ps.setString(5, nv.getSoDienThoai());
            ps.setString(6, nv.getDiaChi());
            ps.setString(7, nv.getCCCD());
            return ps.executeUpdate() == 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public NhanVien timNhanVienTheoCCCD(String cccd) {
        String sql = "SELECT nv.maNhanVien, nv.tenNhanVien, nv.gioiTinh, nv.ngaySinh, nv.email, nv.soDienThoai, nv.ngayBatDau, nv.CCCD, nv.ngayBatDau, nv.trangThai, nv.diaChi, "
                +
                "tk.tenDangNhap, tk.matKhau, tk.vaiTro " +
                "FROM NhanVien nv JOIN TaiKhoan tk ON nv.tenDangNhap = tk.tenDangNhap " +
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
            ex.printStackTrace();
        }
        return null;
    }

    public boolean xoaNhanVienTheoCCCD(NhanVien nv) {
        TaiKhoan_DAO tkDAO = new TaiKhoan_DAO();
        boolean daXoaTaiKhoa = tkDAO.xoaTaiKhoanTheoTenDN(nv.getTaiKhoan().getTenDangNhap());
        if (daXoaTaiKhoa) {
            String sql = "DELETE FROM NhanVien WHERE CCCD = ?";
            try (Connection conn = ConnectDatabase.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nv.getCCCD());
                boolean deleted = ps.executeUpdate() > 0;
                if (deleted)
                    tkDAO.xoaTaiKhoanTheoTenDN(nv.getTaiKhoan().getTenDangNhap());
                return deleted;
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return false;
    }
}
