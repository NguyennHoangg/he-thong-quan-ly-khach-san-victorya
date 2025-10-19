package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

}