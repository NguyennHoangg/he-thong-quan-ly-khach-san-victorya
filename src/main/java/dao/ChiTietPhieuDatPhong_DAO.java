package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import config.ConnectDatabase;
import model.ChiTietPhieuDatPhong;
import model.DichVu;
import model.LoaiDatPhong;
import model.LoaiPhong;
import model.PhieuDatPhong;
import model.Phong;

public class ChiTietPhieuDatPhong_DAO {
    public ChiTietPhieuDatPhong_DAO() {
    }

    public List<ChiTietPhieuDatPhong> getDsChiTietPhieuDatPhong() {
        List<ChiTietPhieuDatPhong> dsKetQua = new ArrayList<>();
        String sql = "select * from ChiTietPhieuDatPhong";
        try (Connection connect = ConnectDatabase.getConnection();
             Statement stmt = connect.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String maPhieuDatPhong = rs.getString("maPhieuDatPhong");
                String maLoaiDatPhong = rs.getString("maLoaiDatPhong");
                java.sql.Timestamp tsNhan = rs.getTimestamp("thoiGianNhanPhong");
                java.sql.Timestamp tsTra = rs.getTimestamp("thoiGianTraPhong");
                LocalDateTime gioBatDau = tsNhan != null ? tsNhan.toLocalDateTime() : null;
                LocalDateTime gioKetThuc = tsTra != null ? tsTra.toLocalDateTime() : null;
                Duration thoiGianThue = (gioBatDau != null && gioKetThuc != null) ? Duration.between(gioBatDau, gioKetThuc) : Duration.ZERO;
                int soGioLuuTru = (gioBatDau != null && gioKetThuc != null) ? (int) Math.ceil(thoiGianThue.toMinutes() / 60.0) : 0;
                String maPhong = rs.getString("maPhong");
                int soNguoi = rs.getInt("soNguoi");
                PhieuDatPhong pdp = new PhieuDatPhong(maPhieuDatPhong);
                LoaiDatPhong ldp = new LoaiDatPhong(maLoaiDatPhong);
                List<DichVu> dsDV = new ArrayList<>(); // No maDichVu in this table
                Phong p = new Phong(maPhong);
                ChiTietPhieuDatPhong ctpdp = new ChiTietPhieuDatPhong(pdp, ldp, dsDV, soGioLuuTru, gioBatDau, gioKetThuc, p, soNguoi);
                dsKetQua.add(ctpdp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsKetQua;
    }

    /**
     * Tìm danh sách phòng đang thuê theo CCCD khách hàng
     * @param cccd CCCD của khách hàng
     * @return Danh sách ChiTietPhieuDatPhong đang hoạt động
     */
    public List<ChiTietPhieuDatPhong> getDatPhongHienTaiTheoCCCD(String cccd) {
        List<ChiTietPhieuDatPhong> danhSachPhong = new ArrayList<>();
        
        String sql = "SELECT kh.maKhachHang, kh.CCCD, kh.hoTen, kh.soDienThoai, kh.email, kh.ngayTao AS ngayTaoKH, " +
                     "       pdp.maPhieuDatPhong, pdp.ngayTao AS ngayTaoPDP, " +
                     "       ctpdp.gioBatDau, ctpdp.gioKetThuc, ctpdp.maLoaiDatPhong, ctpdp.maDichVu, ctpdp.maPhong, ctpdp.soNguoi, " +
                     "       p.soPhong, p.trangThai, p.tang, lp.maLoaiPhong, lp.tenLoaiPhong, lp.gia " +
                     "FROM KhachHang kh " +
                     "JOIN PhieuDatPhong pdp ON pdp.maKhachHang = kh.maKhachHang " +
                     "JOIN ChiTietPhieuDatPhong ctpdp ON ctpdp.maPhieuDatPhong = pdp.maPhieuDatPhong " +
                     "JOIN Phong p ON p.maPhong = ctpdp.maPhong " +
                     "JOIN LoaiPhong lp ON lp.maLoaiPhong = p.maLoaiPhong " +
                     "WHERE kh.CCCD = ? " +
                     "  AND GETDATE() BETWEEN ctpdp.gioBatDau AND ctpdp.gioKetThuc " +
                     "ORDER BY p.tang, p.maPhong";
        
        try (Connection connect = ConnectDatabase.getConnection();
             PreparedStatement ps = connect.prepareStatement(sql)) {
            
            System.out.println("Kết nối database: " + (connect != null ? "thành công" : "thất bại"));
            System.out.println("Tìm kiếm với CCCD: " + cccd);
            
            ps.setString(1, cccd);
            System.out.println("Đang thực thi SQL query...");
            
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("SQL query đã thực thi");
                
                while (rs.next()) {
                    System.out.println("Đang xử lý 1 dòng dữ liệu...");
                    try {
                        // Tạo PhieuDatPhong
                        PhieuDatPhong pdp = taoPhieuDatPhong(rs);
                        System.out.println("Tạo PhieuDatPhong: " + pdp.getMaPhieuDatPhong());
                    
                        // Tạo LoaiPhong
                        LoaiPhong lp = taoLoaiPhong(rs);
                        System.out.println("Tạo LoaiPhong: " + lp.getMaLoaiPhong());
                        
                        // Tạo Phong
                        Phong p = taoPhong(rs, lp);
                        System.out.println("Tạo Phong: " + p.getMaPhong());
                        
                        // Tạo ChiTietPhieuDatPhong
                        ChiTietPhieuDatPhong ctpdp = taoChiTietPhieuDatPhong(rs, pdp, p);
                        System.out.println("Tạo ChiTietPhieuDatPhong thành công");
                        
                        danhSachPhong.add(ctpdp);
                    } catch (Exception ex) {
                        System.out.println("Lỗi khi xử lý dòng dữ liệu: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
            
            System.out.println("Tìm thấy " + danhSachPhong.size() + " phòng đang thuê cho CCCD: " + cccd);
            
        } catch (Exception e) {
            System.out.println("Lỗi khi tìm phòng theo CCCD: " + e.getMessage());
            e.printStackTrace();
        }
        
        return danhSachPhong;
    }

    /**
     * Gia hạn phòng đến thời gian mới
     * @param maPhieuDatPhong Mã phiếu đặt phòng
     * @param maPhong Mã phòng
     * @param gioKetThucMoi Thời gian kết thúc mới
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean giaHanDen(String maPhieuDatPhong, String maPhong, LocalDateTime gioKetThucMoi) {
        try (Connection connect = ConnectDatabase.getConnection()) {
            System.out.println("Bắt đầu gia hạn phòng " + maPhong + " đến " + gioKetThucMoi);
            
            // Bước 1: Lấy thông tin hiện tại
            LocalDateTime gioBatDau = layThoiGianBatDau(connect, maPhieuDatPhong, maPhong);
            LocalDateTime gioKetThucCu = layThoiGianKetThuc(connect, maPhieuDatPhong, maPhong);
            
            if (gioBatDau == null || gioKetThucCu == null) {
                System.out.println("Không tìm thấy phòng " + maPhong + " trong phiếu " + maPhieuDatPhong);
                return false;
            }
            
            // Bước 2: Kiểm tra tính hợp lệ
            if (!kiemTraThoiGianHopLe(gioBatDau, gioKetThucCu, gioKetThucMoi)) {
                System.out.println("Thời gian gia hạn không hợp lệ");
                return false;
            }
            
            // Bước 3: Kiểm tra xung đột lịch
            if (coXungDotLich(connect, maPhong, gioKetThucCu, gioKetThucMoi)) {
                System.out.println("Có xung đột lịch với phòng khác");
                return false;
            }
            
            // Bước 4: Cập nhật thời gian kết thúc
            boolean thanhCong = capNhatThoiGianKetThuc(connect, maPhieuDatPhong, maPhong, gioKetThucMoi);
            
            if (thanhCong) {
                System.out.println("Gia hạn thành công phòng " + maPhong);
            } else {
                System.out.println("Gia hạn thất bại phòng " + maPhong);
            }
            
            return thanhCong;
            
        } catch (Exception e) {
            System.out.println("Lỗi khi gia hạn phòng: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Helper methods

    private PhieuDatPhong taoPhieuDatPhong(ResultSet rs) throws Exception {
        String maPhieuDatPhong = rs.getString("maPhieuDatPhong");
        return new PhieuDatPhong(maPhieuDatPhong);
    }

    private LoaiPhong taoLoaiPhong(ResultSet rs) throws Exception {
        String maLoaiPhong = rs.getString("maLoaiPhong");
        String tenLoaiPhong = rs.getString("tenLoaiPhong");
        double gia = rs.getDouble("gia");
        return new LoaiPhong(maLoaiPhong, tenLoaiPhong, gia, new java.util.ArrayList<>());
    }

    private Phong taoPhong(ResultSet rs, LoaiPhong loaiPhong) throws Exception {
        String maPhong = rs.getString("maPhong");
        String soPhong = rs.getString("soPhong");
        String trangThai = rs.getString("trangThai");
        int tang = rs.getInt("tang");
        return new Phong(maPhong, soPhong, loaiPhong, trangThai, tang);
    }

    private ChiTietPhieuDatPhong taoChiTietPhieuDatPhong(ResultSet rs, PhieuDatPhong pdp, Phong p) throws Exception {
        LocalDateTime gioBatDau = rs.getTimestamp("thoiGianNhanPhong").toLocalDateTime();
        LocalDateTime gioKetThuc = rs.getTimestamp("thoiGianTraPhong") != null ? rs.getTimestamp("thoiGianTraPhong").toLocalDateTime() : null;
        String maLoaiDatPhong = rs.getString("maLoaiDatPhong");
        int soNguoi = rs.getInt("soNguoi");
        LoaiDatPhong ldp = new LoaiDatPhong(maLoaiDatPhong);
        List<DichVu> dsDV = new ArrayList<>(); // No maDichVu in this table
        Duration thoiGianThue = gioKetThuc != null ? Duration.between(gioBatDau, gioKetThuc) : Duration.ZERO;
        int soGioLuuTru = gioKetThuc != null ? (int) Math.ceil(thoiGianThue.toMinutes() / 60.0) : 0;
        return new ChiTietPhieuDatPhong(pdp, ldp, dsDV, soGioLuuTru, gioBatDau, gioKetThuc, p, soNguoi);
    }

    private LocalDateTime layThoiGianBatDau(Connection connect, String maPhieuDatPhong, String maPhong) throws Exception {
        String sql = "SELECT gioBatDau FROM ChiTietPhieuDatPhong WHERE maPhieuDatPhong = ? AND maPhong = ?";
        PreparedStatement ps = connect.prepareStatement(sql);
        ps.setString(1, maPhieuDatPhong);
        ps.setString(2, maPhong);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            return rs.getTimestamp("gioBatDau").toLocalDateTime();
        }
        return null;
    }

    private LocalDateTime layThoiGianKetThuc(Connection connect, String maPhieuDatPhong, String maPhong) throws Exception {
        String sql = "SELECT gioKetThuc FROM ChiTietPhieuDatPhong WHERE maPhieuDatPhong = ? AND maPhong = ?";
        PreparedStatement ps = connect.prepareStatement(sql);
        ps.setString(1, maPhieuDatPhong);
        ps.setString(2, maPhong);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            return rs.getTimestamp("gioKetThuc").toLocalDateTime();
        }
        return null;
    }

    private boolean kiemTraThoiGianHopLe(LocalDateTime gioBatDau, LocalDateTime gioKetThucCu, LocalDateTime gioKetThucMoi) {
        return gioKetThucMoi.isAfter(gioBatDau) && gioKetThucMoi.isAfter(gioKetThucCu);
    }

    private boolean coXungDotLich(Connection connect, String maPhong, LocalDateTime gioKetThucCu, LocalDateTime gioKetThucMoi) throws Exception {
        String sql = "SELECT COUNT(*) as soLuong FROM ChiTietPhieuDatPhong " +
                     "WHERE maPhong = ? AND maPhieuDatPhong != (SELECT maPhieuDatPhong FROM ChiTietPhieuDatPhong WHERE maPhong = ? AND gioKetThuc = ?) " +
                     "AND ((gioBatDau < ? AND gioKetThuc > ?) OR (gioBatDau < ? AND gioKetThuc > ?))";
        
        PreparedStatement ps = connect.prepareStatement(sql);
        ps.setString(1, maPhong);
        ps.setString(2, maPhong);
        ps.setTimestamp(3, java.sql.Timestamp.valueOf(gioKetThucCu));
        ps.setTimestamp(4, java.sql.Timestamp.valueOf(gioKetThucMoi));
        ps.setTimestamp(5, java.sql.Timestamp.valueOf(gioKetThucCu));
        ps.setTimestamp(6, java.sql.Timestamp.valueOf(gioKetThucMoi));
        ps.setTimestamp(7, java.sql.Timestamp.valueOf(gioKetThucCu));
        
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("soLuong") > 0;
        }
        return false;
    }

    private boolean capNhatThoiGianKetThuc(Connection connect, String maPhieuDatPhong, String maPhong, LocalDateTime gioKetThucMoi) {
        try {
            connect.setAutoCommit(false);
            
            String sql = "UPDATE ChiTietPhieuDatPhong SET gioKetThuc = ? WHERE maPhieuDatPhong = ? AND maPhong = ?";
            PreparedStatement ps = connect.prepareStatement(sql);
            ps.setTimestamp(1, java.sql.Timestamp.valueOf(gioKetThucMoi));
            ps.setString(2, maPhieuDatPhong);
            ps.setString(3, maPhong);
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                connect.commit();
                return true;
            } else {
                connect.rollback();
                return false;
            }
            
        } catch (Exception e) {
            try {
                connect.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connect.setAutoCommit(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
