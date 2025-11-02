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
                LocalDateTime thoiGianNhanPhong = tsNhan != null ? tsNhan.toLocalDateTime() : null;
                LocalDateTime thoiGianTraPhong = tsTra != null ? tsTra.toLocalDateTime() : null;
                Duration thoiGianThue = (thoiGianNhanPhong != null && thoiGianTraPhong != null)
                        ? Duration.between(thoiGianNhanPhong, thoiGianTraPhong)
                        : Duration.ZERO;
                int soGioLuuTru = (thoiGianNhanPhong != null && thoiGianTraPhong != null)
                        ? (int) Math.ceil(thoiGianThue.toMinutes() / 60.0)
                        : 0;
                String maPhong = rs.getString("maPhong");
                int soNguoi = rs.getInt("soNguoi");
                PhieuDatPhong pdp = new PhieuDatPhong(maPhieuDatPhong);
                LoaiDatPhong ldp = new LoaiDatPhong(maLoaiDatPhong);
                List<DichVu> dsDV = new ArrayList<>(); // No maDichVu in this table
                Phong p = new Phong(maPhong);
                ChiTietPhieuDatPhong ctpdp = new ChiTietPhieuDatPhong(pdp, ldp, dsDV, soGioLuuTru, thoiGianNhanPhong,
                        thoiGianTraPhong, p, soNguoi);
                dsKetQua.add(ctpdp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsKetQua;
    }

    /**
     * Tìm danh sách phòng đang thuê theo CCCD khách hàng
     * 
     * @param cccd CCCD của khách hàng
     * @return Danh sách ChiTietPhieuDatPhong đang hoạt động
     */
    public List<ChiTietPhieuDatPhong> getDatPhongHienTaiTheoCCCD(String cccd) {
        List<ChiTietPhieuDatPhong> danhSachPhong = new ArrayList<>();

        String sql = "SELECT kh.maKhachHang, kh.CCCD, kh.hoTen, kh.soDienThoai, kh.email, kh.ngayTao AS ngayTaoKH, " +
                "       pdp.maPhieuDatPhong, pdp.ngayTao AS ngayTaoPDP, " +
                "       ctpdp.thoiGianNhanPhong, ctpdp.thoiGianTraPhong, ctpdp.maLoaiDatPhong, NULL as maDichVu, ctpdp.maPhong, ctpdp.soNguoi, "
                +
                "       p.soPhong, p.trangThai, p.tang, lp.maLoaiPhong, lp.tenLoaiPhong, lp.gia " +
                "FROM KhachHang kh " +
                "JOIN PhieuDatPhong pdp ON pdp.maKhachHang = kh.maKhachHang " +
                "JOIN ChiTietPhieuDatPhong ctpdp ON ctpdp.maPhieuDatPhong = pdp.maPhieuDatPhong " +
                "JOIN Phong p ON p.maPhong = ctpdp.maPhong " +
                "JOIN LoaiPhong lp ON lp.maLoaiPhong = p.maLoaiPhong " +
                "WHERE kh.CCCD = ? " +
                "  AND GETDATE() BETWEEN ctpdp.thoiGianNhanPhong AND ctpdp.thoiGianTraPhong " +
                "ORDER BY p.tang, p.maPhong";

        try (Connection connect = ConnectDatabase.getConnection();
                PreparedStatement ps = connect.prepareStatement(sql)) {

            ps.setString(1, cccd);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    try {
                        // Tạo PhieuDatPhong
                        PhieuDatPhong pdp = taoPhieuDatPhong(rs);

                        // Tạo LoaiPhong
                        LoaiPhong lp = taoLoaiPhong(rs);

                        // Tạo Phong
                        Phong p = taoPhong(rs, lp);
                        // Tạo ChiTietPhieuDatPhong
                        ChiTietPhieuDatPhong ctpdp = taoChiTietPhieuDatPhong(rs, pdp, p);

                        danhSachPhong.add(ctpdp);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return danhSachPhong;
    }

    /**
     * Gia hạn phòng đến thời gian mới
     * 
     * @param maPhieuDatPhong     Mã phiếu đặt phòng
     * @param maPhong             Mã phòng
     * @param thoiGianTraPhongMoi Thời gian kết thúc mới
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean giaHanDen(String maPhieuDatPhong, String maPhong, LocalDateTime thoiGianTraPhongMoi) {
        try (Connection connect = ConnectDatabase.getConnection()) {
            System.out.println("Bắt đầu gia hạn phòng " + maPhong + " đến " + thoiGianTraPhongMoi);

            // Bước 1: Lấy thông tin hiện tại
            LocalDateTime thoiGianNhanPhong = layThoiGianBatDau(connect, maPhieuDatPhong, maPhong);
            LocalDateTime thoiGianTraPhongCu = layThoiGianKetThuc(connect, maPhieuDatPhong, maPhong);

            if (thoiGianNhanPhong == null || thoiGianTraPhongCu == null) {
                System.out.println("Không tìm thấy phòng " + maPhong + " trong phiếu " + maPhieuDatPhong);
                return false;
            }

            // Bước 2: Kiểm tra tính hợp lệ
            if (!kiemTraThoiGianHopLe(thoiGianNhanPhong, thoiGianTraPhongCu, thoiGianTraPhongMoi)) {
                System.out.println("Thời gian gia hạn không hợp lệ");
                return false;
            }

            // Bước 3: Kiểm tra xung đột lịch
            if (coXungDotLich(connect, maPhong, thoiGianTraPhongCu, thoiGianTraPhongMoi)) {
                System.out.println("Có xung đột lịch với phòng khác");
                return false;
            }

            // Bước 4: Cập nhật thời gian kết thúc
            boolean thanhCong = capNhatThoiGianKetThuc(connect, maPhieuDatPhong, maPhong, thoiGianTraPhongMoi);

            if (thanhCong) {
                System.out.println("Gia hạn thành công phòng " + maPhong);
            } else {
                System.out.println("Gia hạn thất bại phòng " + maPhong);
            }

            return thanhCong;

        } catch (Exception e) {
            System.out.println("Lỗi khi gia hạn phòng: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật thời gian nhận phòng (check-in)
     */
    public boolean capNhatThoiGianNhanPhong(String maPhieuDatPhong, String maPhong, LocalDateTime thoiGianNhanPhong) {
        String sql = "UPDATE ChiTietPhieuDatPhong SET thoiGianNhanPhong = ? WHERE maPhieuDatPhong = ? AND maPhong = ?";
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, java.sql.Timestamp.valueOf(thoiGianNhanPhong));
            ps.setString(2, maPhieuDatPhong);
            ps.setString(3, maPhong);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
        LocalDateTime thoiGianNhanPhong = rs.getTimestamp("thoiGianNhanPhong").toLocalDateTime();
        LocalDateTime thoiGianTraPhong = rs.getTimestamp("thoiGianTraPhong") != null
                ? rs.getTimestamp("thoiGianTraPhong").toLocalDateTime()
                : null;
        String maLoaiDatPhong = rs.getString("maLoaiDatPhong");
        int soNguoi = rs.getInt("soNguoi");
        LoaiDatPhong ldp = new LoaiDatPhong(maLoaiDatPhong);
        List<DichVu> dsDV = new ArrayList<>(); // No maDichVu in this table
        Duration thoiGianThue = thoiGianTraPhong != null ? Duration.between(thoiGianNhanPhong, thoiGianTraPhong)
                : Duration.ZERO;
        int soGioLuuTru = thoiGianTraPhong != null ? (int) Math.ceil(thoiGianThue.toMinutes() / 60.0) : 0;
        return new ChiTietPhieuDatPhong(pdp, ldp, dsDV, soGioLuuTru, thoiGianNhanPhong, thoiGianTraPhong, p, soNguoi);
    }

    private LocalDateTime layThoiGianBatDau(Connection connect, String maPhieuDatPhong, String maPhong)
            throws Exception {
        String sql = "SELECT thoiGianNhanPhong FROM ChiTietPhieuDatPhong WHERE maPhieuDatPhong = ? AND maPhong = ?";
        PreparedStatement ps = connect.prepareStatement(sql);
        ps.setString(1, maPhieuDatPhong);
        ps.setString(2, maPhong);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getTimestamp("thoiGianNhanPhong").toLocalDateTime();
        }
        return null;
    }

    private LocalDateTime layThoiGianKetThuc(Connection connect, String maPhieuDatPhong, String maPhong)
            throws Exception {
        String sql = "SELECT thoiGianTraPhong FROM ChiTietPhieuDatPhong WHERE maPhieuDatPhong = ? AND maPhong = ?";
        PreparedStatement ps = connect.prepareStatement(sql);
        ps.setString(1, maPhieuDatPhong);
        ps.setString(2, maPhong);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getTimestamp("thoiGianTraPhong").toLocalDateTime();
        }
        return null;
    }

    private boolean kiemTraThoiGianHopLe(LocalDateTime thoiGianNhanPhong, LocalDateTime thoiGianTraPhongCu,
            LocalDateTime thoiGianTraPhongMoi) {
        return thoiGianTraPhongMoi.isAfter(thoiGianNhanPhong) && thoiGianTraPhongMoi.isAfter(thoiGianTraPhongCu);
    }

    private boolean coXungDotLich(Connection connect, String maPhong, LocalDateTime thoiGianTraPhongCu,
            LocalDateTime thoiGianTraPhongMoi) throws Exception {
        String sql = "SELECT COUNT(*) as soLuong FROM ChiTietPhieuDatPhong " +
                "WHERE maPhong = ? AND maPhieuDatPhong != (SELECT maPhieuDatPhong FROM ChiTietPhieuDatPhong WHERE maPhong = ? AND thoiGianTraPhong = ?) "
                +
                "AND ((thoiGianNhanPhong < ? AND thoiGianTraPhong > ?) OR (thoiGianNhanPhong < ? AND thoiGianTraPhong > ?))";

        PreparedStatement ps = connect.prepareStatement(sql);
        ps.setString(1, maPhong);
        ps.setString(2, maPhong);
        ps.setTimestamp(3, java.sql.Timestamp.valueOf(thoiGianTraPhongCu));
        ps.setTimestamp(4, java.sql.Timestamp.valueOf(thoiGianTraPhongMoi));
        ps.setTimestamp(5, java.sql.Timestamp.valueOf(thoiGianTraPhongCu));
        ps.setTimestamp(6, java.sql.Timestamp.valueOf(thoiGianTraPhongMoi));
        ps.setTimestamp(7, java.sql.Timestamp.valueOf(thoiGianTraPhongCu));

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("soLuong") > 0;
        }
        return false;
    }

    private boolean capNhatThoiGianKetThuc(Connection connect, String maPhieuDatPhong, String maPhong,
            LocalDateTime thoiGianTraPhongMoi) {
        try {
            connect.setAutoCommit(false);

            String sql = "UPDATE ChiTietPhieuDatPhong SET thoiGianTraPhong = ? WHERE maPhieuDatPhong = ? AND maPhong = ?";
            PreparedStatement ps = connect.prepareStatement(sql);
            ps.setTimestamp(1, java.sql.Timestamp.valueOf(thoiGianTraPhongMoi));
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

    public List<ChiTietPhieuDatPhong> getDsPhieuDatPhongTheoTrangThai(String trangThai, String tinhTrang) {
        List<ChiTietPhieuDatPhong> dsKetQua = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietPhieuDatPhong ctpdp\r\n" + //
                "JOIN Phong p ON ctpdp.maPhong = p.maPhong\r\n" + //
                "JOIN LoaiPhong lp ON lp.maLoaiPhong = p.maLoaiPhong\r\n" + //
                "WHERE p.trangThai = N'" + trangThai + "' AND p.tinhTrang = N'" + tinhTrang + "'";
        try (Connection connect = ConnectDatabase.getConnection();
                Statement stmt = connect.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {

                String maLoaiPhong = rs.getString("maLoaiPhong");
                String tenLoaiPhong = rs.getString("tenLoaiPhong");
                double gia = rs.getDouble("gia");
                LoaiPhong lp = new LoaiPhong(maLoaiPhong, tenLoaiPhong, gia);

                String maPhong = rs.getString("maPhong");
                String soPhong = rs.getString("soPhong");
                int tang = rs.getInt("tang");
                Phong p = new Phong(maPhong, soPhong, lp, trangThai, tang);

                String maPhieuDatPhong = rs.getString("maPhieuDatPhong");
                PhieuDatPhong pdp = new PhieuDatPhong(maPhieuDatPhong);

                String maLoaiDatPhong = rs.getString("maLoaiDatPhong");
                LoaiDatPhong ldp = new LoaiDatPhong(maLoaiDatPhong);

                int soNguoi = rs.getInt("soNguoi");
                LocalDateTime thoiGianNhanPhong = rs.getTimestamp("thoiGianNhanPhong").toLocalDateTime();
                LocalDateTime thoiGianTraPhong = rs.getTimestamp("thoiGianTraPhong").toLocalDateTime();
                Duration thoiGianThue = thoiGianTraPhong != null ? Duration.between(thoiGianNhanPhong, thoiGianTraPhong)
                        : Duration.ZERO;
                int soGioLuuTru = thoiGianTraPhong != null ? (int) Math.ceil(thoiGianThue.toMinutes() / 60.0) : 0;
                List<DichVu> dsDV = new ArrayList<>();
                ChiTietPhieuDatPhong ctpdp = new ChiTietPhieuDatPhong(pdp, ldp, dsDV, soGioLuuTru, thoiGianNhanPhong,
                        thoiGianTraPhong, p, soNguoi);
                dsKetQua.add(ctpdp);
            }
            connect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dsKetQua;
    }

    public boolean doiPhong(ChiTietPhieuDatPhong ctpdpCu, Phong phongMoi) {
        String sql = """
                UPDATE ChiTietPhieuDatPhong
                SET maPhong = ?
                WHERE maPhieuDatPhong = ? AND maPhong = ?
                """;

        try {
            Connection connect = ConnectDatabase.getConnection();
            PreparedStatement ps = connect.prepareStatement(sql);

            ps.setString(1, phongMoi.getMaPhong());
            ps.setString(2, ctpdpCu.getPhieuDatPhong().getMaPhieuDatPhong());
            ps.setString(3, ctpdpCu.getPhong().getMaPhong());

            int rows = ps.executeUpdate();
            connect.close();
            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean xoaChiTietPhieuDatPhongTheoMa(ChiTietPhieuDatPhong ctpdp) {
        String sql = "DELETE FROM ChiTietPhieuDatPhong WHERE maPhong = ? AND maPhieuDatPhong = ?";
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ctpdp.getPhieuDatPhong().getMaPhieuDatPhong());
            ps.setString(2, ctpdp.getPhong().getMaPhong());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return false;
    }

}
