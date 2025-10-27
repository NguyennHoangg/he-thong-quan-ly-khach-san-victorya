/*
 * @ (#) HoaDon_DAO.java     1.0    10/27/2025
 */
package dao;

import config.ConnectDatabase;
import model.HoaDon;
import model.KhachHang;
import model.KhuyenMai;
import model.NhanVien;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDon_DAO {

    public List<HoaDon> getAll() {
        List<HoaDon> ds = new ArrayList<>();


        final String sql = "SELECT maHoaDon, ngayDat, maKhachHang, maNhanVien, maKhuyenMai, ngayTao, trangThai, tongTien FROM HoaDon";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String maHoaDon = rs.getString("maHoaDon");

                Timestamp tsNgayDat = rs.getTimestamp("ngayDat");
                LocalDateTime ngayDat = tsNgayDat == null ? null : tsNgayDat.toLocalDateTime();

                String maKH = rs.getString("maKhachHang");
                KhachHang kh = new KhachHang(maKH);

                String maNV = rs.getString("maNhanVien");
                NhanVien nv = new NhanVien(maNV);

                String maKM = rs.getString("maKhuyenMai");
                KhuyenMai km = (maKM == null ? null : new KhuyenMai(maKM));

                Timestamp tsNgayTao = rs.getTimestamp("ngayTao");
                LocalDateTime ngayTao = tsNgayTao == null ? null : tsNgayTao.toLocalDateTime();

                String trangThai = rs.getString("trangThai");
                double tongTien = rs.getDouble("tongTien");

                HoaDon hd = new HoaDon(maHoaDon, ngayDat, kh, nv, km, ngayTao, trangThai, tongTien);
                ds.add(hd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean insertHoaDon(HoaDon hoaDon) {
        boolean result = false;
        final String query = "INSERT INTO HoaDon(maHoaDon, ngayDat, maKhachHang, maNhanVien, maKhuyenMai, ngayTao, trangThai, tongTien) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, hoaDon.getMaHoaDon());
            ps.setTimestamp(2, hoaDon.getNgayDat() == null ? null : Timestamp.valueOf(hoaDon.getNgayDat()));
            ps.setString(3, hoaDon.getKhachHang().getMaKhachHang());
            ps.setString(4, hoaDon.getNhanVien().getMaNhanVien());
            ps.setString(5, hoaDon.getKhuyenMai() == null ? null : hoaDon.getKhuyenMai().getMaKhuyenMai());
            ps.setTimestamp(6, hoaDon.getNgayTao() == null ? null : Timestamp.valueOf(hoaDon.getNgayTao()));
            ps.setString(7, hoaDon.getTrangThai());
            ps.setDouble(8, hoaDon.getTongTien());

            result = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

}
