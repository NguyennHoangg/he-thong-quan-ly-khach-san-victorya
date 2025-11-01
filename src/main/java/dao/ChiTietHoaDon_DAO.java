package dao;

import config.ConnectDatabase;
import model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDon_DAO {

    public List<ChiTietHoaDon> findByMaHoaDon(String maHoaDon) {
        final String sql = "SELECT maHoaDon, maPhieuDatPhong, ngayTao, tongTien " +
                "FROM ChiTietHoaDon WHERE maHoaDon = ?";

        List<ChiTietHoaDon> ds = new ArrayList<>();

        try (Connection con = ConnectDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maPDP = rs.getString("maPhieuDatPhong");
                    Timestamp ts = rs.getTimestamp("ngayTao");
                    LocalDateTime ngayTao = ts == null ? null : ts.toLocalDateTime();
                    double tongTien = rs.getDouble("tongTien");

                    HoaDon hd = new HoaDon(maHoaDon);
                    PhieuDatPhong pdp = new PhieuDatPhong(maPDP);

                    ChiTietHoaDon cthd = new ChiTietHoaDon(hd, pdp, ngayTao, tongTien);
                    // nạp danh sách dịch vụ cho từng dòng
                    cthd.setDichVus(findDichVuByMaHDAndMaPDP(maHoaDon, maPDP));
                    ds.add(cthd);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public List<ChiTietHoaDonDichVu> findDichVuByMaHDAndMaPDP(String maHoaDon, String maPhieuDatPhong) {
        final String sql = "SELECT dv.maDichVu, dv.tenDichVu, dv.gia, dv.moTa, dv.donViTinh " +
                "FROM ChiTietHoaDon_DichVu x " +
                "JOIN DichVu dv ON dv.maDichVu = x.maDichVu " +
                "WHERE x.maHoaDon = ? AND x.maPhieuDatPhong = ?";

        List<ChiTietHoaDonDichVu> ds = new ArrayList<>();

        try (Connection con = ConnectDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maHoaDon);
            ps.setString(2, maPhieuDatPhong);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DichVu dv = new DichVu(
                            rs.getString("maDichVu"),
                            rs.getString("tenDichVu"),
                            rs.getBigDecimal("gia") != null ? rs.getBigDecimal("gia").doubleValue() : 0d,
                            rs.getString("moTa"),
                            rs.getString("donViTinh")
                    );
                    ChiTietHoaDonDichVu row = new ChiTietHoaDonDichVu(
                            new HoaDon(maHoaDon),
                            new PhieuDatPhong(maPhieuDatPhong),
                            dv
                    );
                    ds.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
}
