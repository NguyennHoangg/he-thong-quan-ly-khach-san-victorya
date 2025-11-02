/*
 * @ (#) HoaDon_DAO.java     1.1    10/31/2025
 */
package dao;

import config.ConnectDatabase;
import model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDon_DAO {

    /** Lấy tất cả hóa đơn + JOIN thông tin KH, NV, KM để UI hiển thị chi tiết ngay */
    public List<HoaDon> getAll() {
        List<HoaDon> ds = new ArrayList<>();

        final String sql =
                "SELECT hd.maHoaDon, hd.ngayDat, hd.maKhachHang, hd.maNhanVien, hd.maKhuyenMai, " +
                        "       hd.ngayTao, hd.trangThai, hd.tongTien, " +
                        "       kh.hoTen AS tenKH, kh.soDienThoai, kh.email, " +
                        "       nv.tenNhanVien, " +
                        "       km.tenKhuyenMai, km.heSo " +
                        "FROM HoaDon hd " +
                        "LEFT JOIN KhachHang kh ON kh.maKhachHang = hd.maKhachHang " +
                        "LEFT JOIN NhanVien  nv ON nv.maNhanVien  = hd.maNhanVien " +
                        "LEFT JOIN KhuyenMai km ON km.maKhuyenMai = hd.maKhuyenMai " +
                        "ORDER BY hd.ngayDat DESC, hd.maHoaDon DESC";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String maHD = rs.getString("maHoaDon");

                Timestamp tsNgayDat = rs.getTimestamp("ngayDat");
                LocalDateTime ngayDat = tsNgayDat == null ? null : tsNgayDat.toLocalDateTime();

                Timestamp tsNgayTao = rs.getTimestamp("ngayTao");
                LocalDateTime ngayTao = tsNgayTao == null ? null : tsNgayTao.toLocalDateTime();

                double tongTien = 0d;
                try {
                    // tống tiền có thể DECIMAL -> dùng BigDecimal nếu cần
                    tongTien = rs.getBigDecimal("tongTien") != null ? rs.getBigDecimal("tongTien").doubleValue() : 0d;
                } catch (Exception ignore) {
                    tongTien = rs.getDouble("tongTien");
                }

                // Map KhachHang
                String maKH = rs.getString("maKhachHang");
                KhachHang kh = null;
                if (maKH != null) {
                    kh = new KhachHang();
                    kh.setMaKhachHang(maKH);
                    kh.setTenKhachHang(rs.getString("tenKH"));
                    kh.setSoDienThoai(rs.getString("soDienThoai"));
                    kh.setEmail(rs.getString("email"));
                }

                // Map NhanVien
                String maNV = rs.getString("maNhanVien");
                NhanVien nv = null;
                if (maNV != null) {
                    nv = new NhanVien();
                    nv.setMaNhanVien(maNV);
                    nv.setTenNhanVien(rs.getString("tenNhanVien"));
                }

                // Map KhuyenMai
                String maKM = rs.getString("maKhuyenMai");
                KhuyenMai km = null;
                if (maKM != null) {
                    km = new KhuyenMai();
                    km.setMaKhuyenMai(maKM);
                    km.setTenKhuyenMai(rs.getString("tenKhuyenMai"));
                    try {
                        km.setHeSo(rs.getBigDecimal("heSo") != null ? rs.getBigDecimal("heSo").floatValue() : 0f);
                    } catch (Exception e) {
                        km.setHeSo(rs.getFloat("heSo"));
                    }
                }

                // Map HoaDon
                HoaDon hd = new HoaDon();
                hd.setMaHoaDon(maHD);
                hd.setNgayDat(ngayDat);
                hd.setNgayTao(ngayTao);
                hd.setTrangThai(rs.getString("trangThai"));
                hd.setTongTien(tongTien);
                hd.setKhachHang(kh);
                hd.setNhanVien(nv);
                hd.setKhuyenMai(km);

                ds.add(hd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** (tuỳ chọn) Lấy 1 hóa đơn theo mã, có JOIN đầy đủ – dùng khi bạn muốn refetch trước khi mở dialog */
    public HoaDon findById(String maHoaDon) {
        final String sql =
                "SELECT hd.maHoaDon, hd.ngayDat, hd.maKhachHang, hd.maNhanVien, hd.maKhuyenMai, " +
                        "       hd.ngayTao, hd.trangThai, hd.tongTien, " +
                        "       kh.hoTen AS tenKH, kh.soDienThoai, kh.email, " +
                        "       nv.tenNhanVien, " +
                        "       km.tenKhuyenMai, km.heSo " +
                        "FROM HoaDon hd " +
                        "LEFT JOIN KhachHang kh ON kh.maKhachHang = hd.maKhachHang " +
                        "LEFT JOIN NhanVien  nv ON nv.maNhanVien  = hd.maNhanVien " +
                        "LEFT JOIN KhuyenMai km ON km.maKhuyenMai = hd.maKhuyenMai " +
                        "WHERE hd.maHoaDon = ?";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // (Map giống như ở trên)
                    String maHD = rs.getString("maHoaDon");

                    Timestamp tsNgayDat = rs.getTimestamp("ngayDat");
                    LocalDateTime ngayDat = tsNgayDat == null ? null : tsNgayDat.toLocalDateTime();

                    Timestamp tsNgayTao = rs.getTimestamp("ngayTao");
                    LocalDateTime ngayTao = tsNgayTao == null ? null : tsNgayTao.toLocalDateTime();

                    double tongTien = 0d;
                    try {
                        tongTien = rs.getBigDecimal("tongTien") != null ? rs.getBigDecimal("tongTien").doubleValue() : 0d;
                    } catch (Exception ignore) {
                        tongTien = rs.getDouble("tongTien");
                    }

                    KhachHang kh = null;
                    String maKH = rs.getString("maKhachHang");
                    if (maKH != null) {
                        kh = new KhachHang();
                        kh.setMaKhachHang(maKH);
                        kh.setTenKhachHang(rs.getString("tenKH"));
                        kh.setSoDienThoai(rs.getString("soDienThoai"));
                        kh.setEmail(rs.getString("email"));

                    }

                    NhanVien nv = null;
                    String maNV = rs.getString("maNhanVien");
                    if (maNV != null) {
                        nv = new NhanVien();
                        nv.setMaNhanVien(maNV);
                        nv.setTenNhanVien(rs.getString("tenNhanVien"));
                    }

                    KhuyenMai km = null;
                    String maKM = rs.getString("maKhuyenMai");
                    if (maKM != null) {
                        km = new KhuyenMai();
                        km.setMaKhuyenMai(maKM);
                        km.setTenKhuyenMai(rs.getString("tenKhuyenMai"));
                        try {
                            km.setHeSo(rs.getBigDecimal("heSo") != null ? rs.getBigDecimal("heSo").floatValue() : 0f);
                        } catch (Exception e) {
                            km.setHeSo(rs.getFloat("heSo"));
                        }
                    }

                    HoaDon hd = new HoaDon();
                    hd.setMaHoaDon(maHD);
                    hd.setNgayDat(ngayDat);
                    hd.setNgayTao(ngayTao);
                    hd.setTrangThai(rs.getString("trangThai"));
                    hd.setTongTien(tongTien);
                    hd.setKhachHang(kh);
                    hd.setNhanVien(nv);
                    hd.setKhuyenMai(km);
                    return hd;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
