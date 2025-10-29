package dao;

import config.ConnectDatabase;
import model.LoaiPhong;
import model.Phong;

import java.sql.*;
import java.time.LocalDate; // <- thêm dòng này
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class QuanLiPhong_DAO {
// tìm kiếm theo loại phòng
    public List<LoaiPhong> findAllRoomTypes() {
        final String sql = "SELECT maLoaiPhong, tenLoaiPhong, gia, ngayTao FROM LoaiPhong ORDER BY tenLoaiPhong";
        List<LoaiPhong> list = new ArrayList<>();

        try (Connection con = ConnectDatabase.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                double gia = rs.getBigDecimal("gia") != null ? rs.getBigDecimal("gia").doubleValue() : 0d;
                list.add(new LoaiPhong(
                        rs.getString("maLoaiPhong"),
                        rs.getString("tenLoaiPhong"),
                        gia,
                        rs.getDate("ngayTao") != null ? rs.getDate("ngayTao").toLocalDate() : LocalDate.now() // sửa:
                                                                                                              // không
                                                                                                              // để null
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }



    public List<Phong> findAll() {
        final String sql = "SELECT p.maPhong, p.soPhong AS soPhong, p.tang, p.trangThai, " +
                "       lp.maLoaiPhong, lp.tenLoaiPhong, lp.gia " +
                "FROM Phong p JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong " +
                "ORDER BY TRY_CAST(p.soPhong AS INT), p.soPhong";

        List<Phong> list = new ArrayList<>();

        try (Connection con = ConnectDatabase.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                double gia = rs.getBigDecimal("gia") != null ? rs.getBigDecimal("gia").doubleValue() : 0d;
                LoaiPhong lp = new LoaiPhong(
                        rs.getString("maLoaiPhong"),
                        rs.getString("tenLoaiPhong"),
                        gia,
                        LocalDate.now() // sửa: không để null
                );
                Phong p = new Phong(
                        rs.getString("maPhong"),
                        rs.getString("soPhong"),
                        lp,
                        rs.getString("trangThai"),
                        rs.getInt("tang"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Phong findById(String maPhong) {
        final String sql = "SELECT p.maPhong, p.soPhong AS soPhong, p.tang, p.trangThai, " +
                "       lp.maLoaiPhong, lp.tenLoaiPhong, lp.gia " +
                "FROM Phong p JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong " +
                "WHERE p.maPhong = ?";

        try (Connection con = ConnectDatabase.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maPhong);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double gia = rs.getBigDecimal("gia") != null ? rs.getBigDecimal("gia").doubleValue() : 0d;
                    LoaiPhong lp = new LoaiPhong(
                            rs.getString("maLoaiPhong"),
                            rs.getString("tenLoaiPhong"),
                            gia,
                            LocalDate.now() // sửa: không để null
                    );
                    return new Phong(
                            rs.getString("maPhong"),
                            rs.getString("soPhong"),
                            lp,
                            rs.getString("trangThai"),
                            rs.getInt("tang"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean existsBySoPhong(String soPhong) {
        final String sql = "SELECT 1 FROM Phong WHERE soPhong = ?";
        try (Connection con = ConnectDatabase.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, soPhong);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsBySoPhongExcludingId(String soPhong, String maPhong) {
        final String sql = "SELECT 1 FROM Phong WHERE soPhong = ? AND maPhong <> ?";
        try (Connection con = ConnectDatabase.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, soPhong);
            ps.setString(2, maPhong);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

// tự sinh mã trong khi lưu entity
    private String generateMaPhong() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        return "P" + LocalDateTime.now().format(fmt);
    }


    public String insert(Phong p) {
        final String sql = "INSERT INTO Phong (maPhong, soPhong, maLoaiPhong, tang, trangThai) VALUES (?, ?, ?, ?, ?)";
        String id = generateMaPhong();

        try (Connection con = ConnectDatabase.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.setString(2, p.getSoPhong()); // map soPhong -> soPhong
            ps.setString(3, p.getLoaiPhong().getMaLoaiPhong());
            ps.setInt(4, p.getTang());
            ps.setString(5, p.getTrangThai());

            int rows = ps.executeUpdate();
            if (rows > 0)
                return id;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(Phong p) {
        final String sql = "UPDATE Phong SET soPhong = ?, maLoaiPhong = ?, tang = ?, trangThai = ? WHERE maPhong = ?";

        try (Connection con = ConnectDatabase.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getSoPhong());
            ps.setString(2, p.getLoaiPhong().getMaLoaiPhong());
            ps.setInt(3, p.getTang());
            ps.setString(4, p.getTrangThai());
            ps.setString(5, p.getMaPhong());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteById(String maPhong) {
        final String sql = "DELETE FROM Phong WHERE maPhong = ?";
        try (Connection con = ConnectDatabase.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maPhong);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int deleteMany(List<String> ids) {
        if (ids == null || ids.isEmpty())
            return 0;
        final String sql = "DELETE FROM Phong WHERE maPhong = ?";
        int affected = 0;

        try (Connection con = ConnectDatabase.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            for (String id : ids) {
                ps.setString(1, id);
                affected += ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return affected;
    }

    /* ========== PHÒNG: TÌM KIẾM ========== */
    public List<Phong> search(String keyword, String maLoaiPhong, String trangThai, Integer tang) {
        StringBuilder sb = new StringBuilder(
                "SELECT p.maPhong, p.soPhong AS soPhong, p.tang, p.trangThai, " +
                        "       lp.maLoaiPhong, lp.tenLoaiPhong, lp.gia " +
                        "FROM Phong p JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong WHERE 1=1");
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
                while (rs.next()) {
                    double gia = rs.getBigDecimal("gia") != null ? rs.getBigDecimal("gia").doubleValue() : 0d;
                    LoaiPhong lp = new LoaiPhong(
                            rs.getString("maLoaiPhong"),
                            rs.getString("tenLoaiPhong"),
                            gia,
                            LocalDate.now() // sửa: không để null
                    );
                    Phong p = new Phong(
                            rs.getString("maPhong"),
                            rs.getString("soPhong"),
                            lp,
                            rs.getString("trangThai"),
                            rs.getInt("tang"));
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // QuanLiPhong_DAO.java
    public Phong findBySoPhong(String soPhong) {
        final String sql = "SELECT p.maPhong, p.soPhong AS soPhong, p.tang, p.trangThai, " +
                "       lp.maLoaiPhong, lp.tenLoaiPhong, lp.gia " +
                "FROM Phong p JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong " +
                "WHERE p.soPhong = ?";
        try (Connection con = ConnectDatabase.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, soPhong);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double gia = rs.getBigDecimal("gia") != null ? rs.getBigDecimal("gia").doubleValue() : 0d;
                    LoaiPhong lp = new LoaiPhong(
                            rs.getString("maLoaiPhong"),
                            rs.getString("tenLoaiPhong"),
                            gia,
                            java.time.LocalDate.now());
                    return new Phong(
                            rs.getString("maPhong"),
                            rs.getString("soPhong"),
                            lp,
                            rs.getString("trangThai"),
                            rs.getInt("tang"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
