package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.ConnectDatabase;
import model.DichVu;
import model.LoaiPhong;
import model.Phong;

public class Phong_DAO {
    public Phong_DAO() {

    }

    public List<Phong> getTatCaPhong() {
        List<Phong> dsachPhong = new ArrayList<>();
        Map<String, Phong> phongMap = new HashMap<>();

        try {
            var connection = ConnectDatabase.getConnection();
            String query = "SELECT p.*, lp.*, dv.* " +
                    "FROM Phong p " +
                    "JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong " +
                    "LEFT JOIN DichVu_LoaiPhong dvp ON lp.maLoaiPhong = dvp.maLoaiPhong " +
                    "LEFT JOIN DichVu dv ON dvp.maDichVu = dv.maDichVu";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String maPhong = resultSet.getString("maPhong");
                String soPhong = resultSet.getString("soPhong");
                String trangThai = resultSet.getString("trangThai");
                int tang = resultSet.getInt("tang");
                String tenLoaiPhong = resultSet.getString("tenLoaiPhong");
                String maLoaiPhong = resultSet.getString("maLoaiPhong");
                double gia = resultSet.getDouble("gia");

                if (!phongMap.containsKey(maPhong)) {
                    List<DichVu> dsDichVu = new ArrayList<>();
                    LoaiPhong loaiPhong = new LoaiPhong(maLoaiPhong, tenLoaiPhong, gia, dsDichVu);
                    Phong phong = new Phong(maPhong, soPhong, loaiPhong, trangThai, tang);
                    phongMap.put(maPhong, phong);
                }

                String maDichVu = resultSet.getString("maDichVu");
                if (maDichVu != null) {
                    String tenDichVu = resultSet.getString("tenDichVu");
                    DichVu dichVu = new DichVu(maDichVu, tenDichVu);
                    phongMap.get(maPhong).getLoaiPhong().getDsachDichVu().add(dichVu);
                }
            }

            dsachPhong.addAll(phongMap.values());
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dsachPhong;
    }

    public boolean capNhatTrangThaiPhong(String maPhong, String trangThaiMoi) {
        String sql = "UPDATE Phong SET trangThai = ? WHERE maPhong = ?";

        try (Connection connection = ConnectDatabase.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, trangThaiMoi);
            stmt.setString(2, maPhong);

            int n = stmt.executeUpdate();
            connection.close();
            return n > 0; // Trả về true nếu có ít nhất 1 dòng được cập nhật

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Phong> timKiemPhongTheoThoiGian(String loaiPhong, String gioBatDau, String gioKetThuc) {
        List<Phong> dsPhong = new ArrayList<>();
        Map<String, Phong> phongMap = new HashMap<>();

        String sql = "SELECT DISTINCT " +
                "p.*, " +
                "lp.*, " +
                "dv.* " +
                "FROM Phong p " +
                "JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong " +
                "LEFT JOIN DichVu_LoaiPhong dvp ON lp.maLoaiPhong = dvp.maLoaiPhong " +
                "LEFT JOIN DichVu dv ON dvp.maDichVu = dv.maDichVu " +
                "JOIN ChiTietPhieuDatPhong ct ON ct.maPhong = p.maPhong " +
                "AND ( " +
                "    (ct.gioBatDau BETWEEN ? AND ?) " +
                "    OR (ct.gioKetThuc BETWEEN ? AND ?) " +
                "    OR (? BETWEEN ct.gioBatDau AND ct.gioKetThuc) " +
                "    OR (? BETWEEN ct.gioBatDau AND ct.gioKetThuc) " +
                ") " +
                "WHERE (? IS NULL OR lp.tenLoaiPhong = ?) " +
                "ORDER BY p.tang, p.maPhong";

        try (Connection connection = ConnectDatabase.getConnection();
                var ps = connection.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(gioBatDau)); // ct.gioBatDau BETWEEN ? (start)
            ps.setTimestamp(2, Timestamp.valueOf(gioKetThuc)); // AND ? (end)
            ps.setTimestamp(3, Timestamp.valueOf(gioBatDau)); // ct.gioKetThuc BETWEEN ? (start)
            ps.setTimestamp(4, Timestamp.valueOf(gioKetThuc)); // AND ? (end)
            ps.setTimestamp(5, Timestamp.valueOf(gioBatDau)); // ? BETWEEN ct.gioBatDau AND ct.gioKetThuc
            ps.setTimestamp(6, Timestamp.valueOf(gioKetThuc)); // ? BETWEEN ct.gioBatDau AND ct.gioKetThuc
            ps.setString(7, loaiPhong); // ? IS NULL
            ps.setString(8, loaiPhong); // OR lp.tenLoaiPhong = ?

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String maPhong = rs.getString("maPhong");
                String soPhong = rs.getString("soPhong");
                String trangThai = rs.getString("trangThai");
                int tang = rs.getInt("tang");

                String maLoaiPhong = rs.getString("maLoaiPhong");
                String tenLoaiPhong = rs.getString("tenLoaiPhong");
                double gia = rs.getDouble("gia");

                if (!phongMap.containsKey(maPhong)) {
                    List<DichVu> dsDichVu = new ArrayList<>();
                    LoaiPhong loaiPhongObj = new LoaiPhong(maLoaiPhong, tenLoaiPhong, gia, dsDichVu);
                    Phong phong = new Phong(maPhong, soPhong, loaiPhongObj, trangThai, tang);

                    phongMap.put(maPhong, phong);
                }

                String maDichVu = rs.getString("maDichVu");
                if (maDichVu != null) {
                    String tenDichVu = rs.getString("tenDichVu");
                    DichVu dichVu = new DichVu(maDichVu, tenDichVu);
                    phongMap.get(maPhong).getLoaiPhong().getDsachDichVu().add(dichVu);
                }
            }

            dsPhong.addAll(phongMap.values());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dsPhong;
    }

}
