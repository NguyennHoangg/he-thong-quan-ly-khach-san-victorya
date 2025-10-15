package dao;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDate;
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


    public List<Phong> getTatCaPhong(){
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


    public List<Object> getPhongTheoTrangThai(String trangThai) {
        List<Object> dsPhongTheoTrangThai = new ArrayList<>();
        String query = "SELECT p.*, lp.*, ctdp.*, ldp.* " +
                "FROM Phong p " +
                "JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong " +
                "JOIN ChiTietPhieuDatPhong ctdp ON p.maPhong = ctdp.maPhong " +
                "JOIN LoaiDatPhong ldp ON ldp.maLoaiDatPhong = ctdp.maLoaiDatPhong " +
                "WHERE p.trangThai = N'" + trangThai + "'";
        try (var connection = ConnectDatabase.getConnection();
                Statement statement = connection.createStatement();
                var rs = statement.executeQuery(query)) {

            while (rs.next()) {
                // Lấy dữ liệu
                String tenLoaiPhong = rs.getString("tenLoaiPhong");
                LocalDateTime nhanPhong = rs.getTimestamp("gioBatDau").toLocalDateTime();
                LocalDateTime traPhong = rs.getTimestamp("gioKetThuc").toLocalDateTime();
                String loaiDatPhong = rs.getString("tenLoaiDatPhong");
                double giaPhong = rs.getDouble("gia");
                Duration thoiGianThue = Duration.between(nhanPhong, traPhong);
                double thoiGianThueGio = thoiGianThue.toMinutes() / 60.0;
                int soNguoi = rs.getInt("soNguoi");

                // Tạo map để hứng dữ liệu nhiều loại
                Map<String, Object> record = new HashMap<>();
                record.put("tenLoaiPhong", tenLoaiPhong);
                record.put("gioBatDau", nhanPhong);
                record.put("gioKetThuc", traPhong);
                record.put("loaiDatPhong", loaiDatPhong);
                record.put("giaPhong", giaPhong);
                record.put("thoiGianThueGio", thoiGianThueGio);
                record.put("soNguoi", soNguoi);

                dsPhongTheoTrangThai.add(record);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsPhongTheoTrangThai;
    }

}
