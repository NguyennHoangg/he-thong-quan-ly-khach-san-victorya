package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import model.ChiTietPhieuDatPhong;
import model.DichVu;
import model.LoaiPhong;
import model.Phong;
import model.PhieuDatPhong;
import model.LoaiDatPhong;
import config.ConnectDatabase;


public class Phong_DAO {

    public Phong_DAO() {

    }


    public List<Phong> getTatCaPhong(){
        List<Phong> dsachPhong = new ArrayList<>();
        Map<String, Phong> phongMap = new HashMap<>();

    try (Connection connection = ConnectDatabase.getConnection()) {
        String query = "SELECT p.*, lp.*, dv.* " +
            "FROM Phong p " +
            "JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong " +
            "LEFT JOIN DichVu_LoaiPhong dvp ON lp.maLoaiPhong = dvp.maLoaiPhong " +
            "LEFT JOIN DichVu dv ON dvp.maDichVu = dv.maDichVu";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
            
            while(resultSet.next()){
                String maPhong = resultSet.getString("maPhong");
                String tenPhong = resultSet.getString("tenPhong");
                String trangThai = resultSet.getString("trangThai");
                int tang = resultSet.getInt("tang");
                String tenLoaiPhong = resultSet.getString("tenLoaiPhong");
                String maLoaiPhong = resultSet.getString("maLoaiPhong");
                double gia = resultSet.getDouble("gia");
                
                if(!phongMap.containsKey(maPhong)) {
                    List<DichVu> dsDichVu = new ArrayList<>();
                    LoaiPhong loaiPhong = new LoaiPhong(maLoaiPhong, tenLoaiPhong, gia, dsDichVu);
                    Phong phong = new Phong(maPhong, tenPhong, loaiPhong, trangThai, tang);
                    phongMap.put(maPhong, phong);
                }
                
                String maDichVu = resultSet.getString("maDichVu");
                if(maDichVu != null) {
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


    public List<ChiTietPhieuDatPhong> getPhongTheoTrangThai(String trangThai) {
        List<ChiTietPhieuDatPhong> dsPhongTheoTrangThai = new ArrayList<>();
        String query = "SELECT p.*, lp.*, ctdp.*, ldp.* " +
                "FROM Phong p " +
                "JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong " +
                "JOIN ChiTietPhieuDatPhong ctdp ON p.maPhong = ctdp.maPhong " +
                "JOIN LoaiDatPhong ldp ON ldp.maLoaiDatPhong = ctdp.maLoaiDatPhong " +
                "WHERE p.trangThai = N'" + trangThai + "'";
    try ( Connection connection = ConnectDatabase.getConnection();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query)) {

            while (rs.next()) {
                String maPhieuDatPhong = rs.getString("maPhieuDatPhong");
                String maLoaiDatPhong = rs.getString("maLoaiDatPhong");
                String maDichVu = rs.getString("maDichVu");
                java.sql.Timestamp gioBatDau = rs.getTimestamp("gioBatDau");
                java.sql.Timestamp gioKetThuc = rs.getTimestamp("gioKetThuc");
                int soNguoi = rs.getInt("soNguoi");
                
                // Get Phong details
                String maPhong = rs.getString("maPhong");
                String tenPhong = rs.getString("tenPhong");
                int tang = rs.getInt("tang");
                
                // Get LoaiPhong details
                String maLoaiPhong = rs.getString("maLoaiPhong");
                String tenLoaiPhong = rs.getString("tenLoaiPhong");
                double gia = rs.getDouble("gia");
                
                // Create LoaiPhong with full details
                List<DichVu> dsachDichVu = new ArrayList<>();
                LoaiPhong loaiPhong = new LoaiPhong(maLoaiPhong, tenLoaiPhong, gia, dsachDichVu);
                
                // Create Phong with full details including LoaiPhong
                Phong p = new Phong(maPhong, tenPhong, loaiPhong, trangThai, tang);

                PhieuDatPhong pdp = new PhieuDatPhong(maPhieuDatPhong);
                LoaiDatPhong ldp = new LoaiDatPhong(maLoaiDatPhong);
                DichVu dv = new DichVu(maDichVu);
                List<DichVu> dsDV = new ArrayList<>();
                dsDV.add(dv);

                // Convert SQL Timestamp to LocalDateTime
                java.time.LocalDateTime gioBatDauLDT = gioBatDau != null ? gioBatDau.toLocalDateTime() : null;
                java.time.LocalDateTime gioKetThucLDT = gioKetThuc != null ? gioKetThuc.toLocalDateTime() : null;

                int soGioLuuTru = 0;
                if (gioBatDauLDT != null && gioKetThucLDT != null) {
                    java.time.Duration thoiGianThue = java.time.Duration.between(gioBatDauLDT, gioKetThucLDT);
                    soGioLuuTru = (int) Math.ceil(thoiGianThue.toMinutes() / 60.0);
                }

                ChiTietPhieuDatPhong ctpdp = new ChiTietPhieuDatPhong(
                        pdp, ldp, dsDV, soGioLuuTru, gioBatDauLDT, gioKetThucLDT, p, soNguoi);
                dsPhongTheoTrangThai.add(ctpdp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dsPhongTheoTrangThai;
    }

}
