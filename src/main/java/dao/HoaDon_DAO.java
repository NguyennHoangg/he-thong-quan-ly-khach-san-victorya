// ...existing code...
/*
 * @ (#) HoaDon_DAO.java     1.0    10/27/2025
 */
package dao;

import config.ConnectDatabase;
import model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDon_DAO {
    // ...existing code...
    public NhanVien findByUsername(String tenDangNhap) {
        String sql = "SELECT nv.maNhanVien, nv.tenNhanVien, nv.gioiTinh, nv.ngaySinh, nv.email, nv.soDienThoai, nv.ngayBatDau, "
                + "tk.tenDangNhap, tk.matKhau, tk.vaiTro "
                + "FROM NhanVien nv JOIN TaiKhoan tk ON nv.tenDangNhap = tk.tenDangNhap "
                + "WHERE tk.tenDangNhap = ?";

        Connection conn = null;
        try {
            conn = ConnectDatabase.getConnection();
            if (conn == null) {
                System.err.println("DB connection is null. Kiểm tra ConnectDatabase.JDBC_URL và SQL Server đang chạy.");
                return null;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, tenDangNhap);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        TaiKhoan tk = new TaiKhoan(
                                rs.getString("tenDangNhap"),
                                rs.getString("matKhau"),
                                rs.getString("vaiTro"));
                        LocalDate ngaySinh = rs.getDate("ngaySinh") != null ? rs.getDate("ngaySinh").toLocalDate()
                                : null;
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
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    // ...existing code...

    public List<HoaDon> getAll() {
        List<HoaDon> ds = new ArrayList<>();

        final String sql = "SELECT maHoaDon, ngayDat, maKhachHang, maNhanVien, maKhuyenMai, ngayTao, trangThai, tongTien FROM HoaDon";

        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String maHoaDon = rs.getString("maHoaDon");

                Timestamp tsNgayDat = rs.getTimestamp("ngayDat");
                LocalDateTime ngayDat = tsNgayDat == null ? null : tsNgayDat.toLocalDateTime();

                String maKH = rs.getString("maKhachHang");
                KhachHang kh = new KhachHang(maKH);

                String maNV = rs.getString("maNhanVien");
                NhanVien nv = new NhanVien(maNV);

                String maKM = rs.getString("maKhuyenMai");
                KhuyenMai km = (maKM == null ? null : new KhuyenMai(maKM));

                Timestamp tsNgayTao = rs.getTimestamp("ngayTao");
                LocalDateTime ngayTao = tsNgayTao == null ? null : tsNgayTao.toLocalDateTime();

                String trangThai = rs.getString("trangThai");
                double tongTien = rs.getDouble("tongTien");

                HoaDon hd = new HoaDon(maHoaDon, ngayDat, kh, nv, km, ngayTao, trangThai, tongTien);
                ds.add(hd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
}
