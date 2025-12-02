package dao;

import config.ConnectDatabase;
import model.LoaiPhong;
import model.Phong;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class QuanLiPhong_DAO {


    private static double toDouble(ResultSet rs, String col) throws SQLException {
        var bd = rs.getBigDecimal(col);
        return bd == null ? 0d : bd.doubleValue();
    }

    private static LoaiPhong mapLoaiPhong(ResultSet rs) throws SQLException {
        LocalDate ngayTao = rs.getDate("ngayTao") != null
                ? rs.getDate("ngayTao").toLocalDate()
                : LocalDate.now();

        int soNguoiLonToiDa = 0;
        int soTreEmToiDa = 0;
        try {
            // Nếu DB đã có 2 cột này
            soNguoiLonToiDa = rs.getInt("soNguoiLonToiDa");
            soTreEmToiDa = rs.getInt("soTreEmToiDa");
        } catch (SQLException ex) {
            // Nếu DB cũ chưa có cột -> giữ mặc định trong LoaiPhong
        }

        // Dùng full constructor mới của LoaiPhong
        return new LoaiPhong(
                rs.getString("maLoaiPhong"),
                rs.getString("tenLoaiPhong"),
                toDouble(rs, "gia"),
                ngayTao,
                null,               // danh sách dịch vụ (chưa load ở đây)
                soNguoiLonToiDa,
                soTreEmToiDa
        );
    }

    private static Phong mapPhong(ResultSet rs) throws SQLException {
        LoaiPhong lp = mapLoaiPhong(rs);

        Phong p = new Phong(
                rs.getString("maPhong"),
                rs.getString("soPhong"),
                lp,
                rs.getString("trangThai"),
                rs.getInt("tang")
        );

        try { p.setTinhTrang(rs.getString("tinhTrang")); } catch (Exception ignore) {}
        try { p.setMoTa(rs.getString("moTa")); } catch (Exception ignore) {}

        return p;
    }



    public String getNextMaPhong() {
        final String sql = "SELECT ISNULL(MAX(CAST(SUBSTRING(maPhong, 3, 10) AS INT)), 0) FROM Phong";
        try (Connection con = ConnectDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int next = 1;
            if (rs.next()) next = rs.getInt(1) + 1;
            return String.format("P-%03d", next);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi sinh mã phòng: " + e.getMessage(), e);
        }
    }



    public List<LoaiPhong> findAllRoomTypes() {
        String sql = """
            SELECT maLoaiPhong, tenLoaiPhong, gia, ngayTao,
                   soNguoiLonToiDa, soTreEmToiDa
            FROM LoaiPhong
            ORDER BY tenLoaiPhong
        """;
        List<LoaiPhong> list = new ArrayList<>();
        try (Connection con = ConnectDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapLoaiPhong(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải loại phòng", e);
        }
        return list;
    }



    public List<Phong> findAll() {
        String sql = """
        SELECT p.maPhong, p.soPhong, p.tang, p.trangThai, p.tinhTrang, p.moTa,
               lp.maLoaiPhong, lp.tenLoaiPhong, lp.gia, lp.ngayTao,
               lp.soNguoiLonToiDa, lp.soTreEmToiDa
        FROM Phong p JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong
        ORDER BY TRY_CAST(p.soPhong AS INT), p.soPhong
""";

        List<Phong> list = new ArrayList<>();
        try (Connection con = ConnectDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapPhong(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải phòng", e);
        }
        return list;
    }

    public Phong findById(String id) {
        String sql = """
            SELECT p.maPhong, p.soPhong, p.tang, p.trangThai, p.tinhTrang,
                   lp.maLoaiPhong, lp.tenLoaiPhong, lp.gia, lp.ngayTao,
                   lp.soNguoiLonToiDa, lp.soTreEmToiDa
            FROM Phong p JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong
            WHERE p.maPhong = ?
        """;
        try (Connection con = ConnectDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapPhong(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm phòng", e);
        }
        return null;
    }

    public boolean existsBySoPhong(String soPhong) {
        String sql = "SELECT 1 FROM Phong WHERE soPhong = ?";
        try (Connection con = ConnectDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, soPhong);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi check số phòng", e);
        }
    }

    public boolean existsBySoPhongExcludingId(String soPhong, String id) {
        String sql = "SELECT 1 FROM Phong WHERE soPhong = ? AND maPhong <> ?";
        try (Connection con = ConnectDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, soPhong);
            ps.setString(2, id);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi check số phòng", e);
        }
    }

    public boolean insert(Phong p) {
        String sql = """
        INSERT INTO Phong (maPhong, soPhong, maLoaiPhong, tang, trangThai, tinhTrang, moTa)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection con = ConnectDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getMaPhong());
            ps.setString(2, p.getSoPhong());
            ps.setString(3, p.getLoaiPhong().getMaLoaiPhong());
            ps.setInt(4, p.getTang());
            ps.setString(5, p.getTrangThai());
            ps.setString(6, p.getTinhTrang());
            ps.setString(7, p.getMoTa());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi insert phòng", e);
        }
    }

    public boolean update(Phong p) {
        String sql = """
        UPDATE Phong SET soPhong=?, maLoaiPhong=?, tang=?, 
                        trangThai=?, tinhTrang=?, moTa=?
        WHERE maPhong=?
    """;

        try (Connection con = ConnectDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getSoPhong());
            ps.setString(2, p.getLoaiPhong().getMaLoaiPhong());
            ps.setInt(3, p.getTang());
            ps.setString(4, p.getTrangThai());
            ps.setString(5, p.getTinhTrang());
            ps.setString(6, p.getMoTa());
            ps.setString(7, p.getMaPhong());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi update phòng", e);
        }
    }


    public boolean deleteById(String id) {
        try (Connection con = ConnectDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM Phong WHERE maPhong = ?")) {

            ps.setString(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            if (e.getErrorCode() == 547) {
                throw new RuntimeException("Không thể xoá phòng vì đã có lịch sử sử dụng.");
            }
            throw new RuntimeException("Lỗi xóa phòng", e);
        }
    }

    public int deleteMany(List<String> ids) {
        if (ids.isEmpty()) return 0;

        int total = 0;
        try (Connection con = ConnectDatabase.getConnection()) {
            con.setAutoCommit(false);
            try {
                final int BATCH = 900;
                for (int i = 0; i < ids.size(); i += BATCH) {
                    List<String> sub = ids.subList(i, Math.min(i + BATCH, ids.size()));

                    StringBuilder ph = new StringBuilder();
                    for (int j = 0; j < sub.size(); j++) {
                        if (j > 0) ph.append(',');
                        ph.append('?');
                    }

                    String sql = "DELETE FROM Phong WHERE maPhong IN (" + ph + ")";
                    try (PreparedStatement ps = con.prepareStatement(sql)) {
                        int idx = 1;
                        for (String id : sub) ps.setString(idx++, id);
                        total += ps.executeUpdate();
                    }
                }
                con.commit();
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa nhiều phòng", e);
        }
        return total;
    }

    public List<Phong> search(String keyword, String maLoaiPhong, String trangThai, Integer tang) {
        StringBuilder sb = new StringBuilder("""
            SELECT p.maPhong, p.soPhong, p.tang, p.trangThai, p.tinhTrang,
                   lp.maLoaiPhong, lp.tenLoaiPhong, lp.gia, lp.ngayTao,
                   lp.soNguoiLonToiDa, lp.soTreEmToiDa
            FROM Phong p JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            sb.append(" AND (p.soPhong LIKE ? OR lp.tenLoaiPhong LIKE ?)");
            String like = "%" + keyword.trim() + "%";
            params.add(like);
            params.add(like);
        }
        if (maLoaiPhong != null && !maLoaiPhong.isBlank()) {
            sb.append(" AND lp.maLoaiPhong = ?");
            params.add(maLoaiPhong);
        }
        if (trangThai != null && !trangThai.isBlank()) {
            sb.append(" AND p.trangThai = ?");
            params.add(trangThai);
        }
        if (tang != null) {
            sb.append(" AND p.tang = ?");
            params.add(tang);
        }

        sb.append(" ORDER BY TRY_CAST(p.soPhong AS INT), p.soPhong");

        List<Phong> list = new ArrayList<>();
        try (Connection con = ConnectDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sb.toString())) {

            for (int i = 0; i < params.size(); i++)
                ps.setObject(i + 1, params.get(i));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapPhong(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi search phòng", e);
        }
        return list;
    }
}
