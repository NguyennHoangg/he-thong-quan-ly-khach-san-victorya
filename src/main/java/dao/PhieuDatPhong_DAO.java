package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.ChiTietPhieuDatPhong;
import model.DichVu;
import model.KhachHang;
import model.LoaiDatPhong;
import model.LoaiPhong;
import model.PhieuDatPhong;
import model.Phong;
import config.ConnectDatabase;

public class PhieuDatPhong_DAO {

    /**
     * Lấy phiếu đặt phòng của khách hàng có phòng đang ở
     * 
     * @param cccd CCCD của khách hàng
     * @return PhieuDatPhong của khách hàng với các phòng đang ở
     */
    public PhieuDatPhong getPhieuDatPhongTheoCCCD(String cccd) {
        PhieuDatPhong phieuDatPhong = null;

        String sqlPhieu = "SELECT DISTINCT pdp.maPhieuDatPhong, pdp.ngayTao, " +
                "       kh.maKhachHang, kh.CCCD, kh.hoTen, kh.soDienThoai, kh.email, kh.ngayTao AS ngayTaoKH " +
                "FROM PhieuDatPhong pdp " +
                "JOIN KhachHang kh ON pdp.maKhachHang = kh.maKhachHang " +
                "JOIN ChiTietPhieuDatPhong ctpdp ON pdp.maPhieuDatPhong = ctpdp.maPhieuDatPhong " +
                "JOIN Phong p ON p.maPhong = ctpdp.maPhong " +
                "WHERE kh.CCCD = ? AND p.trangThai = N'Đang ở' " +
                "ORDER BY pdp.ngayTao DESC";

        try (Connection connect = ConnectDatabase.getConnection();
                PreparedStatement psPhieu = connect.prepareStatement(sqlPhieu)) {

            psPhieu.setString(1, cccd);

            try (ResultSet rsPhieu = psPhieu.executeQuery()) {
                if (rsPhieu.next()) {
                    String maPhieuDatPhong = rsPhieu.getString("maPhieuDatPhong");
                    java.sql.Date sqlNgayTao = rsPhieu.getDate("ngayTao");
                    LocalDate ngayTao = sqlNgayTao != null ? sqlNgayTao.toLocalDate() : null;

                    String maKhachHang = rsPhieu.getString("maKhachHang");
                    String cccdKH = rsPhieu.getString("CCCD");
                    String hoTen = rsPhieu.getString("hoTen");
                    String soDienThoai = rsPhieu.getString("soDienThoai");
                    String email = rsPhieu.getString("email");
                   

                    KhachHang khachHang = new KhachHang(maKhachHang, cccdKH, hoTen, soDienThoai, email);

                    List<ChiTietPhieuDatPhong> dsChiTiet = getChiTietPhieuDatPhongDangO(connect, maPhieuDatPhong);

                    phieuDatPhong = new PhieuDatPhong(maPhieuDatPhong, khachHang, ngayTao, dsChiTiet);
                    return phieuDatPhong;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy danh sách chi tiết phiếu đặt phòng có trạng thái đang ở
     * 
     * @param connect         Connection đã mở
     * @param maPhieuDatPhong Mã phiếu đặt phòng
     * @return Danh sách ChiTietPhieuDatPhong có phòng đang ở
     */
    private List<ChiTietPhieuDatPhong> getChiTietPhieuDatPhongDangO(Connection connect, String maPhieuDatPhong) {
        List<ChiTietPhieuDatPhong> dsChiTiet = new ArrayList<>();

        String sql = "SELECT ctpdp.maPhieuDatPhong, ctpdp.maPhong, ctpdp.thoiGianNhanPhong, ctpdp.thoiGianTraPhong, " +
                "       ctpdp.maLoaiDatPhong, ctpdp.soNguoi, " +
                "       p.soPhong, p.trangThai, p.tang, " +
                "       lp.maLoaiPhong, lp.tenLoaiPhong, lp.gia, " +
                "       ldp.tenLoaiDatPhong " +
                "FROM ChiTietPhieuDatPhong ctpdp " +
                "JOIN Phong p ON p.maPhong = ctpdp.maPhong " +
                "JOIN LoaiPhong lp ON lp.maLoaiPhong = p.maLoaiPhong " +
                "JOIN LoaiDatPhong ldp ON ldp.maLoaiDatPhong = ctpdp.maLoaiDatPhong " +
                "WHERE ctpdp.maPhieuDatPhong = ? AND p.trangThai = N'Đang ở'";

        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setString(1, maPhieuDatPhong);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maPhong = rs.getString("maPhong");
                    String soPhong = rs.getString("soPhong");
                    String trangThai = rs.getString("trangThai");
                    int tang = rs.getInt("tang");

                    String maLoaiPhong = rs.getString("maLoaiPhong");
                    String tenLoaiPhong = rs.getString("tenLoaiPhong");
                    double gia = rs.getDouble("gia");
                    LoaiPhong loaiPhong = new LoaiPhong(maLoaiPhong, tenLoaiPhong, gia, new ArrayList<>());

                    Phong phong = new Phong(maPhong, soPhong, loaiPhong, trangThai, tang);

                    String maLoaiDatPhong = rs.getString("maLoaiDatPhong");
                    LoaiDatPhong loaiDatPhong = new LoaiDatPhong(maLoaiDatPhong);

                    java.sql.Timestamp tsNhan = rs.getTimestamp("thoiGianNhanPhong");
                    java.sql.Timestamp tsTra = rs.getTimestamp("thoiGianTraPhong");
                    LocalDateTime thoiGianNhanPhong = tsNhan != null ? tsNhan.toLocalDateTime() : null;
                    LocalDateTime thoiGianTraPhong = tsTra != null ? tsTra.toLocalDateTime() : null;

                    int soGioLuuTru = 0;
                    if (thoiGianNhanPhong != null && thoiGianTraPhong != null) {
                        java.time.Duration duration = java.time.Duration.between(thoiGianNhanPhong, thoiGianTraPhong);
                        soGioLuuTru = (int) duration.toHours();
                    }

                    int soNguoi = rs.getInt("soNguoi");

                    PhieuDatPhong pdp = new PhieuDatPhong(maPhieuDatPhong);

                    // Load danh sách dịch vụ cho chi tiết phiếu đặt phòng
                    List<DichVu> dsDichVu = getDichVuTheoPhieuVaPhong(connect, maPhieuDatPhong, maPhong);

                    ChiTietPhieuDatPhong ctpdp = new ChiTietPhieuDatPhong(
                            pdp, loaiDatPhong, dsDichVu, soGioLuuTru,
                            thoiGianNhanPhong, thoiGianTraPhong, phong, soNguoi);

                    dsChiTiet.add(ctpdp);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dsChiTiet;
    }

    /**
     * Lấy danh sách dịch vụ theo mã phiếu đặt phòng và mã phòng
     * 
     * @param connect         Connection đã mở
     * @param maPhieuDatPhong Mã phiếu đặt phòng
     * @param maPhong         Mã phòng
     * @return Danh sách DichVu
     */
    private List<DichVu> getDichVuTheoPhieuVaPhong(Connection connect, String maPhieuDatPhong, String maPhong) {
        List<DichVu> dsDichVu = new ArrayList<>();
        
        String sql = "SELECT dv.maDichVu, dv.tenDichVu, dv.gia, dv.moTa, dv.donViTinh, ctpdp_dv.soLuong " +
                     "FROM ChiTietPhieuDatPhong_DichVu ctpdp_dv " +
                     "JOIN DichVu dv ON dv.maDichVu = ctpdp_dv.maDichVu " +
                     "WHERE ctpdp_dv.maPhieuDatPhong = ? AND ctpdp_dv.maPhong = ?";
        
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setString(1, maPhieuDatPhong);
            ps.setString(2, maPhong);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maDichVu = rs.getString("maDichVu");
                    String tenDichVu = rs.getString("tenDichVu");
                    double gia = rs.getDouble("gia");
                    String moTa = rs.getString("moTa");
                    String donViTinh = rs.getString("donViTinh");
                    int soLuong = rs.getInt("soLuong");
                    
                    // Thêm dịch vụ vào danh sách theo số lượng
                    for (int i = 0; i < soLuong; i++) {
                        DichVu dichVu = new DichVu(maDichVu, tenDichVu, gia, moTa, donViTinh);
                        dsDichVu.add(dichVu);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return dsDichVu;
    }

    /**
     * Lấy mã phiếu đặt phòng cuối cùng từ database
     * @return Mã phiếu cuối cùng hoặc null nếu chưa có phiếu nào
     */
    public String getMaPhieuDatPhongCuoiCung() {
        String sql = "SELECT TOP 1 maPhieuDatPhong FROM PhieuDatPhong ORDER BY maPhieuDatPhong DESC";
        
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getString("maPhieuDatPhong");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Lấy mã phiếu đặt phòng cuối cùng theo ngày
     * @param ngayDatPhong Ngày đặt phòng
     * @return Mã phiếu cuối cùng trong ngày hoặc null
     */
    public String getMaPhieuDatPhongCuoiCungTheoNgay(LocalDate ngayDatPhong) {
        String sql = "SELECT TOP 1 maPhieuDatPhong FROM PhieuDatPhong " +
                    "WHERE ngayTao = ? " +
                    "ORDER BY maPhieuDatPhong DESC";
        
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, java.sql.Date.valueOf(ngayDatPhong));
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("maPhieuDatPhong");
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Tạo phiếu đặt phòng mới lên database
     * 
     * @param phieuDatPhong PhieuDatPhong cần tạo
     * @return true nếu tạo thành công, false nếu thất bại
     */
    public boolean taoPhieuDatPhong(PhieuDatPhong phieuDatPhong) {
        Connection conn = null;
        try {
            conn = ConnectDatabase.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction
            
            // 1. Insert PhieuDatPhong
            String sqlPhieu = "INSERT INTO PhieuDatPhong (maPhieuDatPhong, maKhachHang, ngayTao, trangThai, tienDatCoc) " +
                            "VALUES (?, ?, ?, ?, ?)";
            
            try (PreparedStatement psPhieu = conn.prepareStatement(sqlPhieu)) {
                psPhieu.setString(1, phieuDatPhong.getMaPhieuDatPhong());
                psPhieu.setString(2, phieuDatPhong.getKhachHang().getMaKhachHang());
                psPhieu.setDate(3, java.sql.Date.valueOf(phieuDatPhong.getNgayTao()));
                psPhieu.setString(4, phieuDatPhong.getTrangThai() != null ? phieuDatPhong.getTrangThai() : "Đã đặt");
                psPhieu.setLong(5, (long) (phieuDatPhong.tinhTongTien() * 0.3)); // Tiền cọc 30%
                
                psPhieu.executeUpdate();
            }
            
            // 2. Insert ChiTietPhieuDatPhong
            String sqlChiTiet = "INSERT INTO ChiTietPhieuDatPhong " +
                              "(maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi, trangThai) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement psChiTiet = conn.prepareStatement(sqlChiTiet)) {
                for (ChiTietPhieuDatPhong chiTiet : phieuDatPhong.getDsachPhieuDatPhong()) {
                    psChiTiet.setString(1, phieuDatPhong.getMaPhieuDatPhong());
                    psChiTiet.setString(2, chiTiet.getPhong().getMaPhong());
                    psChiTiet.setTimestamp(3, java.sql.Timestamp.valueOf(chiTiet.getThoiGianNhanPhong()));
                    psChiTiet.setTimestamp(4, java.sql.Timestamp.valueOf(chiTiet.getThoiGianTraPhong()));
                    psChiTiet.setString(5, chiTiet.getLoaiDatPhong().getMaLoaiDatPhong());
                    psChiTiet.setInt(6, chiTiet.getSoNguoi());
                    psChiTiet.setString(7, "Đã đặt"); // Trạng thái chi tiết phiếu đặt phòng
                    
                    psChiTiet.executeUpdate();
                }
            }
            
            // 3. Insert ChiTietPhieuDatPhong_DichVu (dịch vụ cho mỗi phòng)
            String sqlDichVu = "INSERT INTO ChiTietPhieuDatPhong_DichVu (maPhieuDatPhong, maPhong, maDichVu, soLuong) " +
                             "VALUES (?, ?, ?, ?)";
            
            try (PreparedStatement psDichVu = conn.prepareStatement(sqlDichVu)) {
                for (ChiTietPhieuDatPhong chiTiet : phieuDatPhong.getDsachPhieuDatPhong()) {
                    if (chiTiet.getDsachDichVu() != null && !chiTiet.getDsachDichVu().isEmpty()) {
                        for (DichVu dichVu : chiTiet.getDsachDichVu()) {
                            psDichVu.setString(1, phieuDatPhong.getMaPhieuDatPhong());
                            psDichVu.setString(2, chiTiet.getPhong().getMaPhong());
                            psDichVu.setString(3, dichVu.getMaDichVu());
                            psDichVu.setInt(4, 1); // Số lượng mặc định = 1
                            
                            psDichVu.executeUpdate();
                        }
                    }
                }
            }
            
            // 4. Cập nhật trạng thái phòng thành "Đã đặt"
            String sqlUpdatePhong = "UPDATE Phong SET trangThai = N'Đã đặt' WHERE maPhong = ?";
            
            try (PreparedStatement psUpdatePhong = conn.prepareStatement(sqlUpdatePhong)) {
                for (ChiTietPhieuDatPhong chiTiet : phieuDatPhong.getDsachPhieuDatPhong()) {
                    psUpdatePhong.setString(1, chiTiet.getPhong().getMaPhong());
                    psUpdatePhong.executeUpdate();
                }
            }
            
            conn.commit(); // Commit transaction
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback nếu có lỗi
                }
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception closeEx) {
                closeEx.printStackTrace();
            }
        }
    }
}