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

                    PhieuDatPhong pdp = new PhieuDatPhong(maPDP);

                    ChiTietHoaDon cthd = new ChiTietHoaDon( pdp, ngayTao, tongTien);
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

    /**
     * Thêm chi tiết hóa đơn vào database
     * @param chiTietHoaDon Chi tiết hóa đơn cần thêm
     * @return true nếu thêm thành công, false nếu thất bại
     */
    public boolean themChiTietHoaDon(ChiTietHoaDon chiTietHoaDon) {
        String sql = "INSERT INTO ChiTietHoaDon (maHoaDon, maPhieuDatPhong, ngayTao, tongTien) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(2, chiTietHoaDon.getPhieuDatPhong().getMaPhieuDatPhong());
            ps.setTimestamp(3, chiTietHoaDon.getNgayTao() != null ? 
                             Timestamp.valueOf(chiTietHoaDon.getNgayTao()) : 
                             Timestamp.valueOf(LocalDateTime.now()));
            ps.setDouble(4, chiTietHoaDon.getTongTien());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Thêm chi tiết hóa đơn dịch vụ vào database
     * @param maHoaDon Mã hóa đơn
     * @param maPhieuDatPhong Mã phiếu đặt phòng
     * @param maDichVu Mã dịch vụ
     * @return true nếu thêm thành công, false nếu thất bại
     */
    public boolean themChiTietHoaDonDichVu(String maHoaDon, String maPhieuDatPhong, String maDichVu) {
        String sql = "INSERT INTO ChiTietHoaDon_DichVu (maHoaDon, maPhieuDatPhong, maDichVu) " +
                     "VALUES (?, ?, ?)";
        
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maHoaDon);
            ps.setString(2, maPhieuDatPhong);
            ps.setString(3, maDichVu);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lưu toàn bộ hóa đơn (bao gồm chi tiết và dịch vụ) vào database
     * @param hoaDon Hóa đơn cần lưu
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean luuHoaDonDayDu(HoaDon hoaDon) {
        Connection conn = null;
        PreparedStatement psHoaDon = null;
        PreparedStatement psChiTiet = null;
        PreparedStatement psDichVu = null;
        
        try {
            conn = ConnectDatabase.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction
            
            // 1. Thêm hóa đơn
            String sqlHoaDon = "INSERT INTO HoaDon (maHoaDon, ngayDat, maKhachHang, maNhanVien, maKhuyenMai, ngayTao, trangThai, tongTien) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            psHoaDon = conn.prepareStatement(sqlHoaDon);
            psHoaDon.setString(1, hoaDon.getMaHoaDon());
            psHoaDon.setTimestamp(2, hoaDon.getNgayDat() != null ? Timestamp.valueOf(hoaDon.getNgayDat()) : null);
            psHoaDon.setString(3, hoaDon.getKhachHang() != null ? hoaDon.getKhachHang().getMaKhachHang() : null);
            psHoaDon.setString(4, hoaDon.getNhanVien() != null ? hoaDon.getNhanVien().getMaNhanVien() : null);
            psHoaDon.setString(5, hoaDon.getKhuyenMai() != null ? hoaDon.getKhuyenMai().getMaKhuyenMai() : null);
            psHoaDon.setTimestamp(6, hoaDon.getNgayTao() != null ? Timestamp.valueOf(hoaDon.getNgayTao()) : Timestamp.valueOf(LocalDateTime.now()));
            psHoaDon.setString(7, hoaDon.getTrangThai());
            psHoaDon.setDouble(8, hoaDon.getTongTien());
            
            if (psHoaDon.executeUpdate() <= 0) {
                conn.rollback();
                return false;
            }
            
            // 2. Thêm chi tiết hóa đơn và dịch vụ
            if (hoaDon.getChiTietHoaDon() != null && !hoaDon.getChiTietHoaDon().isEmpty()) {
                String sqlChiTiet = "INSERT INTO ChiTietHoaDon (maHoaDon, maPhieuDatPhong, maPhong, ngayTao, tongTien) VALUES (?, ?, ?, ?, ?)";
                psChiTiet = conn.prepareStatement(sqlChiTiet);
                
                String sqlDichVu = "INSERT INTO ChiTietHoaDon_DichVu (maHoaDon, maPhieuDatPhong, maPhong, maDichVu) VALUES (?, ?, ?, ?)";
                psDichVu = conn.prepareStatement(sqlDichVu);
                
                for (ChiTietHoaDon chiTiet : hoaDon.getChiTietHoaDon()) {
                    // Thêm chi tiết hóa đơn
                    psChiTiet.setString(1, hoaDon.getMaHoaDon());
                    psChiTiet.setString(2, chiTiet.getPhieuDatPhong().getMaPhieuDatPhong());
                    psChiTiet.setString(3, chiTiet.getPhong() != null ? chiTiet.getPhong().getMaPhong() : null);
                    psChiTiet.setTimestamp(4, chiTiet.getNgayTao() != null ? 
                                         Timestamp.valueOf(chiTiet.getNgayTao()) : 
                                         Timestamp.valueOf(LocalDateTime.now()));
                    psChiTiet.setDouble(5, chiTiet.getTongTien());
                    
                    if (psChiTiet.executeUpdate() <= 0) {
                        conn.rollback();
                        return false;
                    }
                    
                    // 3. Thêm các dịch vụ trong chi tiết
                    if (chiTiet.getDichVus() != null && !chiTiet.getDichVus().isEmpty()) {
                        for (ChiTietHoaDonDichVu dichVu : chiTiet.getDichVus()) {
                            psDichVu.setString(1, hoaDon.getMaHoaDon());
                            psDichVu.setString(2, chiTiet.getPhieuDatPhong().getMaPhieuDatPhong());
                            psDichVu.setString(3, chiTiet.getPhong() != null ? chiTiet.getPhong().getMaPhong() : null);
                            psDichVu.setString(4, dichVu.getDichVu().getMaDichVu());
                            
                            if (psDichVu.executeUpdate() <= 0) {
                                conn.rollback();
                                return false;
                            }
                        }
                    }
                }
            }
            
            conn.commit(); // Hoàn thành transaction
            System.out.println("Đã lưu hóa đơn thành công vào database: " + hoaDon.getMaHoaDon());
            return true;
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi lưu hóa đơn: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (psHoaDon != null) psHoaDon.close();
                if (psChiTiet != null) psChiTiet.close();
                if (psDichVu != null) psDichVu.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
