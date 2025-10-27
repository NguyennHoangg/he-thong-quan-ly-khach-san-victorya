/*
 * @ (#) HoaDon_DAO.java     1.0    10/27/2025
 */
package dao;

import config.ConnectDatabase;
import model.*;

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
    public List<ChiTietHoaDon> getChiTietHoaDonTheoMa(String maHD) {
        List<ChiTietHoaDon> ds = new ArrayList<>();

        final String sql = """
        SELECT maHoaDon, maPhieuDatPhong, ngayTao, tongTien
        FROM ChiTietHoaDon
        WHERE maHoaDon = ?
        ORDER BY ngayTao
    """;

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHD);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maHoaDon = rs.getString("maHoaDon");
                    String maPhieuDatPhong = rs.getString("maPhieuDatPhong");

                    Timestamp ts = rs.getTimestamp("ngayTao");
                    LocalDateTime ngayTao = ts == null ? null : ts.toLocalDateTime();

                    double tongTien = rs.getDouble("tongTien");

                    // Gán các đối tượng liên kết (nếu bạn có model tương ứng)
                    HoaDon hd = new HoaDon(maHoaDon);
                    PhieuDatPhong pdp = new PhieuDatPhong(maPhieuDatPhong);

                    // Constructor phổ biến nhất trong project của bạn:
                    ChiTietHoaDon cthd = new ChiTietHoaDon(hd, pdp, ngayTao, tongTien);
                    ds.add(cthd);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ds;
    }

}
