package dao;

import java.sql.Connection;
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
import model.NhanVien;
import model.Phong;
import model.TaiKhoan;

public class Phong_DAO {
    public Phong_DAO() {

    }

    public List<Object> getPhongTheoTrangThai(String trangThai) {
        List<Object> dsPhongTheoTrangThai = new ArrayList<>();
        String query = "SELECT p.*, lp.*, ctdp.*, ldp.* " +
                "FROM Phong p " +
                "JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong " +
                "JOIN ChiTietPhieuDatPhong ctdp ON p.maPhong = ctdp.maPhong " +
                "JOIN LoaiDatPhong ldp ON ldp.maLoaiDatPhong = ctdp.maLoaiDatPhong " +
                "WHERE p.trangThai = '" + trangThai + "'";
        try (var connection = ConnectDatabase.getConnection();
                Statement statement = connection.createStatement();
                var resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                // Lấy dữ liệu
                String tenLoaiPhong = resultSet.getString("tenLoaiPhong");
                LocalDateTime nhanPhong = resultSet.getTimestamp("gioBatDau").toLocalDateTime();
                LocalDateTime traPhong = resultSet.getTimestamp("gioKetThuc").toLocalDateTime();
                String loaiDatPhong = resultSet.getString("tenLoaiDatPhong");
                double giaPhong = resultSet.getDouble("gia");
                Duration thoiGianThue = Duration.between(nhanPhong, traPhong);
                double thoiGianThueGio = thoiGianThue.toMinutes() / 60.0;

                // Tạo map để hứng dữ liệu nhiều loại
                Map<String, Object> record = new HashMap<>();
                record.put("tenLoaiPhong", tenLoaiPhong);
                record.put("gioBatDau", nhanPhong);
                record.put("gioKetThuc", traPhong);
                record.put("loaiDatPhong", loaiDatPhong);
                record.put("giaPhong", giaPhong);
                record.put("thoiGianThueGio", thoiGianThueGio);

                dsPhongTheoTrangThai.add(record);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsPhongTheoTrangThai;
    }

}
