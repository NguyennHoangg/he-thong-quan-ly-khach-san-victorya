package dao;

import config.ConnectDatabase;
import model.KhuyenMai;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMai_DAO {

    private static LocalDateTime dateColToLdt(Date d) {
        return d == null ? null : d.toLocalDate().atStartOfDay();
    } // chuyển Date SQL sang LocalDateTime

    // 3 trạng thái tính theo ngày hiện tại (dùng cho SELECT)
    private static final String STATUS_SQL = "CASE " +
            "WHEN CAST(GETDATE() AS date) < ngayBatDau THEN N'Sắp diễn ra' " +
            "WHEN CAST(GETDATE() AS date) > ngayKetThuc THEN N'Kết thúc' " +
            "ELSE N'Đang hoạt động' END AS trangThaiTinhToan";

    private KhuyenMai mapRow(ResultSet rs, boolean readComputedStatus) throws SQLException {
        java.math.BigDecimal bdHeSo = rs.getBigDecimal("heSo");
        java.math.BigDecimal bdToiThieu = rs.getBigDecimal("tongTienToiThieu");
        java.math.BigDecimal bdToiDa = rs.getBigDecimal("tongKhuyenMaiToiDa");

        String stStr = readComputedStatus ? rs.getString("trangThaiTinhToan")
                : rs.getString("trangThai");

        KhuyenMai.TrangThai st = KhuyenMai.TrangThai.fromDb(stStr);

        return new KhuyenMai(
                rs.getString("maKhuyenMai"),
                rs.getString("tenKhuyenMai"),
                dateColToLdt(rs.getDate("ngayBatDau")),
                dateColToLdt(rs.getDate("ngayKetThuc")),
                st,
                bdHeSo == null ? 0f : bdHeSo.floatValue(),
                bdToiThieu == null ? 0f : bdToiThieu.floatValue(),
                bdToiDa == null ? 0f : bdToiDa.floatValue());
    }

    public List<KhuyenMai> getAll() {
        List<KhuyenMai> ds = new ArrayList<>();
        String sql = "SELECT maKhuyenMai, tenKhuyenMai, ngayBatDau, ngayKetThuc, " +
                STATUS_SQL + ", trangThai, heSo, tongTienToiThieu, tongKhuyenMaiToiDa " +
                "FROM KhuyenMai ORDER BY ngayBatDau DESC";

        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next())
                ds.add(mapRow(rs, true));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public KhuyenMai findById(String maKM) {
        String sql = "SELECT maKhuyenMai, tenKhuyenMai, ngayBatDau, ngayKetThuc, " +
                STATUS_SQL + ", trangThai, heSo, tongTienToiThieu, tongKhuyenMaiToiDa " +
                "FROM KhuyenMai WHERE maKhuyenMai = ?";

        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maKM);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return mapRow(rs, true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getNextMaKM() {
        final String sql = "SELECT ISNULL(MAX(CAST(SUBSTRING(maKhuyenMai, 4, 10) AS INT)), 0) FROM KhuyenMai";
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            int next = 1;
            if (rs.next())
                next = rs.getInt(1) + 1;
            return String.format("KM-%03d", next);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean insert(KhuyenMai km) {
        final String sql = "INSERT INTO KhuyenMai " +
                "(maKhuyenMai, tenKhuyenMai, ngayBatDau, ngayKetThuc, trangThai, heSo, tongTienToiThieu, tongKhuyenMaiToiDa) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        if (km.getMaKhuyenMai() == null || km.getMaKhuyenMai().isBlank())
            throw new IllegalArgumentException("maKhuyenMai is required for insert()");

        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            Date bd = km.getNgayBatDau() == null ? null : Date.valueOf(km.getNgayBatDau().toLocalDate());
            Date kt = km.getNgayKetThuc() == null ? null : Date.valueOf(km.getNgayKetThuc().toLocalDate());

            ps.setString(1, km.getMaKhuyenMai());
            ps.setNString(2, km.getTenKhuyenMai());
            ps.setDate(3, bd);
            ps.setDate(4, kt);

            // DAO KHÔNG tự tính nữa -> lưu đúng trạng thái Controller đã set
            ps.setNString(5, KhuyenMai.TrangThai.toDb(km.getTrangThai()));

            ps.setBigDecimal(6, java.math.BigDecimal.valueOf(km.getHeSo()));
            ps.setBigDecimal(7, java.math.BigDecimal.valueOf(km.getTongTienToiThieu()));
            ps.setBigDecimal(8, java.math.BigDecimal.valueOf(km.getTongKhuyenMaiToiDa()));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(KhuyenMai km) {
        String sql = "UPDATE KhuyenMai SET tenKhuyenMai=?, ngayBatDau=?, ngayKetThuc=?, trangThai=?, " +
                "heSo=?, tongTienToiThieu=?, tongKhuyenMaiToiDa=? WHERE maKhuyenMai=?";

        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            Date bd = km.getNgayBatDau() == null ? null : Date.valueOf(km.getNgayBatDau().toLocalDate());
            Date kt = km.getNgayKetThuc() == null ? null : Date.valueOf(km.getNgayKetThuc().toLocalDate());

            ps.setNString(1, km.getTenKhuyenMai());
            ps.setDate(2, bd);
            ps.setDate(3, kt);

            // DAO KHÔNG tự tính nữa -> lưu đúng trạng thái Controller đã set
            ps.setNString(4, KhuyenMai.TrangThai.toDb(km.getTrangThai()));

            ps.setBigDecimal(5, java.math.BigDecimal.valueOf(km.getHeSo()));
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
        if (maKM == null || maKM.isBlank())
            return false;

        try (Connection conn = ConnectDatabase.getConnection()) {
            boolean old = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE HoaDon SET maKhuyenMai = NULL WHERE maKhuyenMai = ?")) {
                    ps.setString(1, maKM);
                    ps.executeUpdate();
                }

                int deleted;
                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM KhuyenMai WHERE maKhuyenMai = ?")) {
                    ps.setString(1, maKM);
                    deleted = ps.executeUpdate();
                }

                conn.commit();
                conn.setAutoCommit(old);
                return deleted > 0;
            } catch (SQLException ex) {
                conn.rollback();
                conn.setAutoCommit(old);
                ex.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int deleteMany(List<String> ids) {
        if (ids == null || ids.isEmpty())
            return 0;

        final int SAFE_CHUNK = 900;
        int totalDeleted = 0;

        try (Connection conn = ConnectDatabase.getConnection()) {
            boolean oldAuto = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try {
                for (int i = 0; i < ids.size(); i += SAFE_CHUNK) {
                    List<String> sub = ids.subList(i, Math.min(i + SAFE_CHUNK, ids.size()));

                    StringBuilder ph = new StringBuilder();
                    for (int j = 0; j < sub.size(); j++) {
                        if (j > 0)
                            ph.append(',');
                        ph.append('?');
                    }

                    String sqlClear = "UPDATE HoaDon SET maKhuyenMai = NULL WHERE maKhuyenMai IN (" + ph + ")";
                    try (PreparedStatement ps = conn.prepareStatement(sqlClear)) {
                        int idx = 1;
                        for (String id : sub)
                            ps.setString(idx++, id);
                        ps.executeUpdate();
                    }

                    String sqlDel = "DELETE FROM KhuyenMai WHERE maKhuyenMai IN (" + ph + ")";
                    try (PreparedStatement ps = conn.prepareStatement(sqlDel)) {
                        int idx = 1;
                        for (String id : sub)
                            ps.setString(idx++, id);
                        totalDeleted += ps.executeUpdate();
                    }
                }

                conn.commit();
                conn.setAutoCommit(oldAuto);
            } catch (SQLException ex) {
                conn.rollback();
                conn.setAutoCommit(oldAuto);
                ex.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalDeleted;
    }
}
