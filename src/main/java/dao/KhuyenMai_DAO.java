package dao;

import config.ConnectDatabase;
import model.KhuyenMai;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



public class KhuyenMai_DAO {


    private static boolean statusToBool(String s) {
        if (s == null)
            return false;
        String x = s.trim().toLowerCase();
        return x.equals("đang áp dụng") || x.equals("dang ap dung") || x.equals("active") || x.equals("true")
                || x.equals("1");
    }

    private static String boolToStatus(boolean b) {
        return b ? "Đang áp dụng" : "Hết hạn";
    }

    private static LocalDateTime dateColToLdt(Date d) {
        return d == null ? null : d.toLocalDate().atStartOfDay();
    }


    public List<KhuyenMai> getAll() {
        List<KhuyenMai> ds = new ArrayList<>();
        String sql = "SELECT maKhuyenMai, tenKhuyenMai, ngayBatDau, ngayKetThuc, trangThai, heSo, tongTienToiThieu, tongKhuyenMaiToiDa "
                +
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

    public KhuyenMai findById(String maKM) {
        String sql = "SELECT maKhuyenMai, tenKhuyenMai, ngayBatDau, ngayKetThuc, trangThai, heSo, tongTienToiThieu, tongKhuyenMaiToiDa "
                +
                "FROM KhuyenMai WHERE maKhuyenMai = ?";
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maKM);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String generateMaKM(Connection conn) throws SQLException {
        // Lấy số lớn nhất sau tiền tố "KM-"
        String sql = "SELECT ISNULL(MAX(CAST(SUBSTRING(maKhuyenMai, 4, 10) AS INT)), 0) FROM KhuyenMai";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int next = 1;
            if (rs.next()) next = rs.getInt(1) + 1;
            return String.format("KM-%03d", next);
        }
    }

public float giamGiaToiDa(float tongTienToiThieu, float heSo){
        return tongTienToiThieu*heSo;
}
    /** Giữ lại nếu nơi khác vẫn dùng: insert với mã tự truyền vào */
    public boolean insert(KhuyenMai km) {
        final String sql = "INSERT INTO KhuyenMai " +
                "(maKhuyenMai, tenKhuyenMai, ngayBatDau, ngayKetThuc, trangThai, heSo, tongTienToiThieu, tongKhuyenMaiToiDa) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // SINH MÃ từ DB để không trùng
            String maKM = generateMaKM(conn);
            km.setMaKhuyenMai(maKM);
            km.settongKhuyenMaiToiDa(giamGiaToiDa(km.getTongTienToiThieu(),km.getHeSo()));
            ps.setString(1, km.getMaKhuyenMai());
            ps.setString(2, km.getTenKhuyenMai());
            ps.setDate(3, km.getNgayBatDau() == null ? null : Date.valueOf(km.getNgayBatDau().toLocalDate()));
            ps.setDate(4, km.getNgayKetThuc() == null ? null : Date.valueOf(km.getNgayKetThuc().toLocalDate()));
            ps.setString(5, boolToStatus(km.isTrangThai()));

            // heSo / tong... là DECIMAL/NUMERIC trong SQL → dùng BigDecimal
            ps.setBigDecimal(6, java.math.BigDecimal.valueOf(km.getHeSo()));
            ps.setBigDecimal(7, java.math.BigDecimal.valueOf(km.getTongTienToiThieu()));
            ps.setBigDecimal(8, java.math.BigDecimal.valueOf(km.getTongKhuyenMaiToiDa()));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // tạm thời log ra console để thấy lỗi cụ thể
            return false;
        }
    }

    public boolean update(KhuyenMai km) {
        String sql = "UPDATE KhuyenMai SET tenKhuyenMai=?, ngayBatDau=?, ngayKetThuc=?, trangThai=?, heSo=?, " +
                "tongTienToiThieu=?, tongKhuyenMaiToiDa=? WHERE maKhuyenMai=?";
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, km.getTenKhuyenMai());
            ps.setDate(2, km.getNgayBatDau() == null ? null : Date.valueOf(km.getNgayBatDau().toLocalDate()));
            ps.setDate(3, km.getNgayKetThuc() == null ? null : Date.valueOf(km.getNgayKetThuc().toLocalDate()));
            ps.setString(4, boolToStatus(km.isTrangThai()));
            ps.setFloat(5, km.getHeSo());
            ps.setBigDecimal(6, java.math.BigDecimal.valueOf(km.getTongTienToiThieu()));
            ps.setBigDecimal(7, java.math.BigDecimal.valueOf(km.getTongKhuyenMaiToiDa()));
            ps.setString(8, km.getMaKhuyenMai());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

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

    /**
     * Xoá nhiều khuyến mãi theo danh sách mã.
     * - Dùng transaction đảm bảo toàn vẹn.
     * - Chia lô để không chạm giới hạn tham số (SQL Server ~2100).
     *
     * @return tổng số hàng xoá được
     */
    public int deleteMany(List<String> ids) {
        if (ids == null || ids.isEmpty())
            return 0;

        final int SAFE_CHUNK = 900; // dư dả
        int totalDeleted = 0;

        try (Connection conn = ConnectDatabase.getConnection()) {
            boolean oldAuto = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                for (int i = 0; i < ids.size(); i += SAFE_CHUNK) {
                    List<String> sub = ids.subList(i, Math.min(i + SAFE_CHUNK, ids.size()));

                    StringBuilder placeholders = new StringBuilder();
                    for (int j = 0; j < sub.size(); j++) {
                        if (j > 0)
                            placeholders.append(',');
                        placeholders.append('?');
                    }

                    String sql = "DELETE FROM KhuyenMai WHERE maKhuyenMai IN (" + placeholders + ")";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        int idx = 1;
                        for (String id : sub)
                            ps.setString(idx++, id);
                        totalDeleted += ps.executeUpdate();
                    }
                }
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(oldAuto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalDeleted;
    }

    private KhuyenMai mapRow(ResultSet rs) throws SQLException {
        return new KhuyenMai(
                rs.getString("maKhuyenMai"),
                rs.getString("tenKhuyenMai"),
                dateColToLdt(rs.getDate("ngayBatDau")),
                dateColToLdt(rs.getDate("ngayKetThuc")),
                statusToBool(rs.getString("trangThai")),
                rs.getFloat("heSo"),
                rs.getBigDecimal("tongTienToiThieu").floatValue(),
                rs.getBigDecimal("tongKhuyenMaiToiDa").floatValue());
    }
}
