package dao;

import config.ConnectDatabase;
import model.KhuyenMai;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMai_DAO {

    /* ========= Helpers ========= */
    // "Đang áp dụng" -> true, còn lại false
    private static boolean statusToBool(String s) {
        if (s == null) return false;
        String x = s.trim().toLowerCase();
        return x.equals("đang áp dụng") || x.equals("dang ap dung") || x.equals("active") || x.equals("true") || x.equals("1");
    }
    private static String boolToStatus(boolean b) { return b ? "Đang áp dụng" : "Hết hạn"; }
    private static LocalDateTime dateColToLdt(Date d) { return d == null ? null : d.toLocalDate().atStartOfDay(); }

    /* ========= READ ALL ========= */
    public List<KhuyenMai> getAll() {
        List<KhuyenMai> ds = new ArrayList<>();
        String sql = "SELECT maKhuyenMai, tenKhuyenMai, ngayBatDau, ngayKetThuc, trangThai, heSo, tongTienToiThieu, tongKhuyenMaiToiDa " +
                "FROM KhuyenMai ORDER BY ngayBatDau DESC";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ds.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /* ========= READ ONE ========= */
    public KhuyenMai findById(String maKM) {
        String sql = "SELECT maKhuyenMai, tenKhuyenMai, ngayBatDau, ngayKetThuc, trangThai, heSo, tongTienToiThieu, tongKhuyenMaiToiDa " +
                "FROM KhuyenMai WHERE maKhuyenMai = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maKM);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* ========= CREATE ========= */
    public boolean insert(KhuyenMai km) {
        String sql = "INSERT INTO KhuyenMai " +
                "(maKhuyenMai, tenKhuyenMai, ngayBatDau, ngayKetThuc, trangThai, heSo, tongTienToiThieu, tongKhuyenMaiToiDa) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, km.getMaKhuyenMai());
            ps.setString(2, km.getTenKhuyenMai());
            ps.setDate(3, km.getNgayBatDau() == null ? null : Date.valueOf(km.getNgayBatDau().toLocalDate()));
            ps.setDate(4, km.getNgayKetThuc() == null ? null : Date.valueOf(km.getNgayKetThuc().toLocalDate()));
            ps.setString(5, boolToStatus(km.isTrangThai())); // NVARCHAR
            ps.setFloat(6, km.getHeSo());
            ps.setBigDecimal(7, java.math.BigDecimal.valueOf(km.getSoTienToiThieuHuongKhuyenMai()));
            ps.setBigDecimal(8, java.math.BigDecimal.valueOf(km.getSoTienDuocGiamToiDa()));
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ========= UPDATE ========= */
    public boolean update(KhuyenMai km) {
        String sql = "UPDATE KhuyenMai SET tenKhuyenMai=?, ngayBatDau=?, ngayKetThuc=?, trangThai=?, heSo=?, " +
                "tongTienToiThieu=?, tongKhuyenMaiToiDa=? WHERE maKhuyenMai=?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, km.getTenKhuyenMai());
            ps.setDate(2, km.getNgayBatDau() == null ? null : Date.valueOf(km.getNgayBatDau().toLocalDate()));
            ps.setDate(3, km.getNgayKetThuc() == null ? null : Date.valueOf(km.getNgayKetThuc().toLocalDate()));
            ps.setString(4, boolToStatus(km.isTrangThai())); // NVARCHAR
            ps.setFloat(5, km.getHeSo());
            ps.setBigDecimal(6, java.math.BigDecimal.valueOf(km.getSoTienToiThieuHuongKhuyenMai()));
            ps.setBigDecimal(7, java.math.BigDecimal.valueOf(km.getSoTienDuocGiamToiDa()));
            ps.setString(8, km.getMaKhuyenMai());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ========= DELETE ========= */
    public boolean delete(String maKM) {
        String sql = "DELETE FROM KhuyenMai WHERE maKhuyenMai = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maKM);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ========= MAP RESULTSET -> MODEL ========= */
    private KhuyenMai mapRow(ResultSet rs) throws SQLException {
        return new KhuyenMai(
                rs.getString("maKhuyenMai"),
                rs.getString("tenKhuyenMai"),
                dateColToLdt(rs.getDate("ngayBatDau")),   // DATE -> LocalDateTime (00:00)
                dateColToLdt(rs.getDate("ngayKetThuc")),
                statusToBool(rs.getString("trangThai")),  // NVARCHAR -> boolean
                rs.getFloat("heSo"),
                rs.getBigDecimal("tongTienToiThieu").floatValue(),
                rs.getBigDecimal("tongKhuyenMaiToiDa").floatValue()
        );
    }
}
