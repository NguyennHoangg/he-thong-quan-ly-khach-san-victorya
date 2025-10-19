package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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
                String tenPhong = resultSet.getString("tenPhong");
                String trangThai = resultSet.getString("trangThai");
                int tang = resultSet.getInt("tang");
                String tenLoaiPhong = resultSet.getString("tenLoaiPhong");
                String maLoaiPhong = resultSet.getString("maLoaiPhong");
                double gia = resultSet.getDouble("gia");

                if (!phongMap.containsKey(maPhong)) {
                    List<DichVu> dsDichVu = new ArrayList<>();
                    LoaiPhong loaiPhong = new LoaiPhong(maLoaiPhong, tenLoaiPhong, gia, dsDichVu);
                    Phong phong = new Phong(maPhong, tenPhong, loaiPhong, trangThai, tang);
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dsachPhong;
    }

    public List<Phong> getPhongTheoTrangThai(String trangThai) {
        LoaiPhong_DAO lp_dao = new LoaiPhong_DAO();
        List<Phong> dsKetQua = new ArrayList<>();
        String sql = "select * from Phong \r\n" +
                "where trangThai = N'" + trangThai + "';";
        try (Connection connection = ConnectDatabase.getConnection();
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                String maPHong = rs.getString("maPhong");
                String soPhong = rs.getString("tenPhong");
                String maLoaiPhong = rs.getString("maLoaiPhong");
                int soTang = rs.getInt("tang");

                LoaiPhong lp = lp_dao.getLoaiPhongTheoMa(maLoaiPhong);
                Phong p = new Phong(maPHong, soPhong, lp, trangThai, soTang);
                dsKetQua.add(p);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return dsKetQua;
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
                String tenPhong = rs.getString("tenPhong");
                String trangThai = rs.getString("trangThai");
                int tang = rs.getInt("tang");

                String maLoaiPhong = rs.getString("maLoaiPhong");
                String tenLoaiPhong = rs.getString("tenLoaiPhong");
                double gia = rs.getDouble("gia");

                if (!phongMap.containsKey(maPhong)) {
                    List<DichVu> dsDichVu = new ArrayList<>();
                    LoaiPhong loaiPhongObj = new LoaiPhong(maLoaiPhong, tenLoaiPhong, gia, dsDichVu);
                    Phong phong = new Phong(maPhong, tenPhong, loaiPhongObj, trangThai, tang);

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
