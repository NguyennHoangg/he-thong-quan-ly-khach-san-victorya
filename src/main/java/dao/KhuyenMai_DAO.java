package dao;

import config.ConnectDatabase;
import model.KhuyenMai;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMai_DAO {

    /* ----- Helpers ----- */

    private static boolean statusToBool(String s) {
        if (s == null) return false;
        String x = s.trim().toLowerCase();
        return x.equals("đang áp dụng") || x.equals("dang ap dung")
                || x.equals("active") || x.equals("true") || x.equals("1");
    }

    private static String boolToStatus(boolean b) {
        return b ? "Đang áp dụng" : "Hết hạn";
    }

    private static LocalDateTime dateColToLdt(Date d) {
        return d == null ? null : d.toLocalDate().atStartOfDay();
    }

    // Trạng thái tính theo ngày hiện tại (để ghi xuống DB khi insert/update)
    private static String statusByDates(java.sql.Date bd, java.sql.Date kt) {
        java.sql.Date today = java.sql.Date.valueOf(java.time.LocalDate.now());
        if (bd != null && bd.after(today))  return "Chưa bắt đầu";
        if (kt != null && kt.before(today)) return "Hết hạn";
        return "Đang áp dụng";
    }

    // Chuỗi SQL tính trạng thái theo ngày khi SELECT
    private static final String STATUS_SQL =
            "CASE " +
                    "WHEN CAST(GETDATE() AS date) < ngayBatDau THEN N'Chưa bắt đầu' " +
                    "WHEN CAST(GETDATE() AS date) > ngayKetThuc THEN N'Hết hạn' " +
                    "ELSE N'Đang áp dụng' END AS trangThaiTinhToan";

    /* ----- Row mapping ----- */

    private KhuyenMai mapRow(ResultSet rs, boolean readComputedStatus) throws SQLException {
        java.math.BigDecimal bdHeSo     = rs.getBigDecimal("heSo");
        java.math.BigDecimal bdToiThieu = rs.getBigDecimal("tongTienToiThieu");
        java.math.BigDecimal bdToiDa    = rs.getBigDecimal("tongKhuyenMaiToiDa");

        String st = readComputedStatus ? rs.getString("trangThaiTinhToan")
                : rs.getString("trangThai");

        return new KhuyenMai(
                rs.getString("maKhuyenMai"),
                rs.getString("tenKhuyenMai"),
                dateColToLdt(rs.getDate("ngayBatDau")),
                dateColToLdt(rs.getDate("ngayKetThuc")),
                statusToBool(st),
                bdHeSo == null ? 0f : bdHeSo.floatValue(),
                bdToiThieu == null ? 0f : bdToiThieu.floatValue(),
                bdToiDa == null ? 0f : bdToiDa.floatValue()
        );
    }

    /* ----- Queries ----- */

    /** Lấy tất cả (trạng thái tự tính theo ngày) */
    public List<KhuyenMai> getAll() {
        List<KhuyenMai> ds = new ArrayList<>();
        String sql = "SELECT maKhuyenMai, tenKhuyenMai, ngayBatDau, ngayKetThuc, " +
                STATUS_SQL + ", heSo, tongTienToiThieu, tongKhuyenMaiToiDa " +
                "FROM KhuyenMai ORDER BY ngayBatDau DESC";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) ds.add(mapRow(rs, true));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** Tìm theo mã (trạng thái tự tính theo ngày) */
    public KhuyenMai findById(String maKM) {
        String sql = "SELECT maKhuyenMai, tenKhuyenMai, ngayBatDau, ngayKetThuc, " +
                STATUS_SQL + ", heSo, tongTienToiThieu, tongKhuyenMaiToiDa " +
                "FROM KhuyenMai WHERE maKhuyenMai = ?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKM);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs, true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Sinh mã tiếp theo: KM-xxx (lưu ý: nếu hệ thống concurrent cao, cân nhắc SEQUENCE) */
    public String getNextMaKM() {
        final String sql = "SELECT ISNULL(MAX(CAST(SUBSTRING(maKhuyenMai, 4, 10) AS INT)), 0) FROM KhuyenMai";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int next = 1;
            if (rs.next()) next = rs.getInt(1) + 1;
            return String.format("KM-%03d", next);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* ----- Commands ----- */

    /** Thêm mới: yêu cầu km.getMaKhuyenMai() đã có (được Controller gán trước). */
    public boolean insert(KhuyenMai km) {
        final String sql = "INSERT INTO KhuyenMai " +
                "(maKhuyenMai, tenKhuyenMai, ngayBatDau, ngayKetThuc, trangThai, heSo, " +
                " tongTienToiThieu, tongKhuyenMaiToiDa) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        if (km.getMaKhuyenMai() == null || km.getMaKhuyenMai().isBlank())
            throw new IllegalArgumentException("maKhuyenMai is required for insert()");

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            Date bd = km.getNgayBatDau() == null ? null : Date.valueOf(km.getNgayBatDau().toLocalDate());
            Date kt = km.getNgayKetThuc() == null ? null : Date.valueOf(km.getNgayKetThuc().toLocalDate());

            ps.setString(1, km.getMaKhuyenMai());
            ps.setString(2, km.getTenKhuyenMai());
            ps.setDate(3, bd);
            ps.setDate(4, kt);
            ps.setString(5, statusByDates(bd, kt)); // auto theo ngày
            ps.setBigDecimal(6, java.math.BigDecimal.valueOf(km.getHeSo()));
            ps.setBigDecimal(7, java.math.BigDecimal.valueOf(km.getTongTienToiThieu()));
            ps.setBigDecimal(8, java.math.BigDecimal.valueOf(km.getTongKhuyenMaiToiDa()));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Cập nhật theo mã (trạng thái được set lại theo ngày) */
    public boolean update(KhuyenMai km) {
        String sql = "UPDATE KhuyenMai SET tenKhuyenMai=?, ngayBatDau=?, ngayKetThuc=?, trangThai=?, " +
                "heSo=?, tongTienToiThieu=?, tongKhuyenMaiToiDa=? WHERE maKhuyenMai=?";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            Date bd = km.getNgayBatDau() == null ? null : Date.valueOf(km.getNgayBatDau().toLocalDate());
            Date kt = km.getNgayKetThuc() == null ? null : Date.valueOf(km.getNgayKetThuc().toLocalDate());

            ps.setString(1, km.getTenKhuyenMai());
            ps.setDate(2, bd);
            ps.setDate(3, kt);
            ps.setString(4, statusByDates(bd, kt)); // auto theo ngày
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

    /** Xoá 1 mã */
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

    /** Xoá nhiều mã (transaction + chia lô) */
    public int deleteMany(List<String> ids) {
        if (ids == null || ids.isEmpty()) return 0;

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
                        if (j > 0) ph.append(',');
                        ph.append('?');
                    }

                    String sql = "DELETE FROM KhuyenMai WHERE maKhuyenMai IN (" + ph + ")";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        int idx = 1;
                        for (String id : sub) ps.setString(idx++, id);
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
}
