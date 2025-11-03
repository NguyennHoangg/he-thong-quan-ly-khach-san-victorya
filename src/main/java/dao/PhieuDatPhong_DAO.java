package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.ChiTietPhieuDatPhong;
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

        String sqlPhieu = "SELECT DISTINCT pdp.maPhieuDatPhong, pdp.ngayTao, pdp.trangThai, pdp.tienDatCoc, " +
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
                    String trangThai = rsPhieu.getString("trangThai");
                    long tienDatCoc = rsPhieu.getLong("tienDatCoc");

                    String maKhachHang = rsPhieu.getString("maKhachHang");
                    String cccdKH = rsPhieu.getString("CCCD");
                    String hoTen = rsPhieu.getString("hoTen");
                    String soDienThoai = rsPhieu.getString("soDienThoai");
                    String email = rsPhieu.getString("email");
                   

                    KhachHang khachHang = new KhachHang(maKhachHang, cccdKH, hoTen, soDienThoai, email);

                    List<ChiTietPhieuDatPhong> dsChiTiet = getChiTietPhieuDatPhongDangO(connect, maPhieuDatPhong);

                    phieuDatPhong = new PhieuDatPhong(maPhieuDatPhong, khachHang, ngayTao, dsChiTiet, trangThai, tienDatCoc);
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

                    ChiTietPhieuDatPhong ctpdp = new ChiTietPhieuDatPhong(
                            pdp, loaiDatPhong, new ArrayList<>(), soGioLuuTru,
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
     * Lấy phiếu đặt phòng của khách hàng có phòng chưa nhận (chưa check-in)
     * 
     * @param cccd CCCD của khách hàng
     * @return PhieuDatPhong của khách hàng với các phòng chưa nhận
     */
    public PhieuDatPhong getPhieuDatPhongChuaNhanTheoCCCD(String cccd) {
        PhieuDatPhong phieuDatPhong = null;

        String sqlPhieu = "SELECT DISTINCT pdp.maPhieuDatPhong, pdp.ngayTao, " +
                "       kh.maKhachHang, kh.CCCD, kh.hoTen, kh.soDienThoai, kh.email, kh.ngayTao AS ngayTaoKH " +
                "FROM PhieuDatPhong pdp " +
                "JOIN KhachHang kh ON pdp.maKhachHang = kh.maKhachHang " +
                "JOIN ChiTietPhieuDatPhong ctpdp ON pdp.maPhieuDatPhong = ctpdp.maPhieuDatPhong " +
                "JOIN Phong p ON p.maPhong = ctpdp.maPhong " +
                "WHERE kh.CCCD = ? AND p.trangThai = N'Đã đặt' AND ctpdp.thoiGianNhanPhong IS NULL " +
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

                    List<ChiTietPhieuDatPhong> dsChiTiet = getChiTietPhieuDatPhongChuaNhan(connect, maPhieuDatPhong);

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
     * Lấy danh sách chi tiết phiếu đặt phòng chưa nhận (chưa check-in)
     * 
     * @param connect         Connection đã mở
     * @param maPhieuDatPhong Mã phiếu đặt phòng
     * @return Danh sách ChiTietPhieuDatPhong có phòng chưa nhận
     */
    private List<ChiTietPhieuDatPhong> getChiTietPhieuDatPhongChuaNhan(Connection connect, String maPhieuDatPhong) {
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
                "WHERE ctpdp.maPhieuDatPhong = ? AND p.trangThai = N'Đã đặt' AND ctpdp.thoiGianNhanPhong IS NULL";

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

                    ChiTietPhieuDatPhong ctpdp = new ChiTietPhieuDatPhong(
                            pdp, loaiDatPhong, new ArrayList<>(), soGioLuuTru,
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
     * Thêm phiếu đặt phòng mới vào database
     * @param phieuDatPhong Phiếu đặt phòng cần thêm
     * @return true nếu thêm thành công, false nếu thất bại
     */
    public boolean themPhieuDatPhong(PhieuDatPhong phieuDatPhong) {
        Connection conn = null;
        try {
            conn = ConnectDatabase.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            // 1. Thêm PhieuDatPhong
            String sqlPhieu = "INSERT INTO PhieuDatPhong (maPhieuDatPhong, ngayTao, maKhachHang, trangThai, tienDatCoc) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement psPhieu = conn.prepareStatement(sqlPhieu)) {
                psPhieu.setString(1, phieuDatPhong.getMaPhieuDatPhong());
                psPhieu.setDate(2, java.sql.Date.valueOf(phieuDatPhong.getNgayTao()));
                psPhieu.setString(3, phieuDatPhong.getKhachHang().getMaKhachHang());
                psPhieu.setString(4, phieuDatPhong.getTrangThai());
                psPhieu.setLong(5, phieuDatPhong.getTienDatCoc());
                psPhieu.executeUpdate();
            }

            // 2. Thêm ChiTietPhieuDatPhong và cập nhật trạng thái phòng
            String sqlChiTiet = "INSERT INTO ChiTietPhieuDatPhong (maPhieuDatPhong, maPhong, thoiGianNhanPhong, thoiGianTraPhong, maLoaiDatPhong, soNguoi) VALUES (?, ?, ?, ?, ?, ?)";
            String sqlUpdatePhong = "UPDATE Phong SET trangThai = N'Đã đặt' WHERE maPhong = ?";
            
            try (PreparedStatement psChiTiet = conn.prepareStatement(sqlChiTiet);
                 PreparedStatement psUpdatePhong = conn.prepareStatement(sqlUpdatePhong)) {
                
                for (ChiTietPhieuDatPhong chiTiet : phieuDatPhong.getDsachPhieuDatPhong()) {
                    // Thêm chi tiết
                    psChiTiet.setString(1, phieuDatPhong.getMaPhieuDatPhong());
                    psChiTiet.setString(2, chiTiet.getPhong().getMaPhong());
                    psChiTiet.setTimestamp(3, chiTiet.getThoiGianNhanPhong() != null ? java.sql.Timestamp.valueOf(chiTiet.getThoiGianNhanPhong()) : null);
                    psChiTiet.setTimestamp(4, chiTiet.getThoiGianTraPhong() != null ? java.sql.Timestamp.valueOf(chiTiet.getThoiGianTraPhong()) : null);
                    psChiTiet.setString(5, chiTiet.getLoaiDatPhong().getMaLoaiDatPhong());
                    psChiTiet.setInt(6, chiTiet.getSoNguoi());
                    psChiTiet.executeUpdate();

                    // Cập nhật trạng thái phòng
                    psUpdatePhong.setString(1, chiTiet.getPhong().getMaPhong());
                    psUpdatePhong.executeUpdate();
                }
            }

            conn.commit(); // Commit transaction
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback nếu có lỗi
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Lấy danh sách tất cả phòng chờ nhận.
     * Phòng được lấy nếu thời điểm hiện tại nằm trong khoảng cho phép nhận:
     * (thoiGianNhanPhong - 1 giờ) <= thời điểm hiện tại <= (thoiGianNhanPhong + 6 giờ)
     * Ví dụ: Phòng có thoiGianNhanPhong = 14:00 thì được nhận trong khoảng 13:00-20:00
     * @return Danh sách ChiTietPhieuDatPhong đang chờ nhận
     */
    public List<ChiTietPhieuDatPhong> layTatCaPhongChoNhanTheoThoiGian() {
        List<ChiTietPhieuDatPhong> dsChiTiet = new ArrayList<>();
        LocalDateTime thoiGianHienTai = LocalDateTime.now();

        String sql = "SELECT DISTINCT ctpdp.maPhieuDatPhong, ctpdp.maPhong, ctpdp.thoiGianNhanPhong, ctpdp.thoiGianTraPhong, " +
                "       ctpdp.maLoaiDatPhong, ctpdp.soNguoi, ctpdp.trangThai, " +
                "       p.soPhong, p.trangThai AS trangThaiPhong, p.tang, " +
                "       lp.maLoaiPhong, lp.tenLoaiPhong, lp.gia, " +
                "       ldp.tenLoaiDatPhong, " +
                "       pdp.maPhieuDatPhong, pdp.ngayTao, " +
                "       kh.maKhachHang, kh.CCCD, kh.hoTen, kh.soDienThoai, kh.email " +
                "FROM ChiTietPhieuDatPhong ctpdp " +
                "JOIN PhieuDatPhong pdp ON ctpdp.maPhieuDatPhong = pdp.maPhieuDatPhong " +
                "JOIN KhachHang kh ON pdp.maKhachHang = kh.maKhachHang " +
                "JOIN Phong p ON p.maPhong = ctpdp.maPhong " +
                "JOIN LoaiPhong lp ON lp.maLoaiPhong = p.maLoaiPhong " +
                "JOIN LoaiDatPhong ldp ON ldp.maLoaiDatPhong = ctpdp.maLoaiDatPhong " +
                "WHERE p.trangThai = N'Đã đặt' " +
                "  AND ctpdp.thoiGianNhanPhong IS NOT NULL " +
                "  AND DATEADD(HOUR, -1, ctpdp.thoiGianNhanPhong) <= ? " +
                "  AND ? <= DATEADD(HOUR, 6, ctpdp.thoiGianNhanPhong) " +
                "ORDER BY ctpdp.thoiGianNhanPhong ASC";

        try (Connection connect = ConnectDatabase.getConnection();
                PreparedStatement ps = connect.prepareStatement(sql)) {

            ps.setTimestamp(1, java.sql.Timestamp.valueOf(thoiGianHienTai));
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(thoiGianHienTai));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dsChiTiet.add(taoChiTietPhieuDatPhongTuResultSet(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dsChiTiet;
    }

    /**
     * Lấy danh sách phòng chờ nhận theo số điện thoại khách hàng.
     * Phòng được lấy nếu thời điểm hiện tại nằm trong khoảng cho phép nhận:
     * (thoiGianNhanPhong - 1 giờ) <= thời điểm hiện tại <= (thoiGianNhanPhong + 6 giờ)
     * @param soDienThoai Số điện thoại khách hàng
     * @return Danh sách ChiTietPhieuDatPhong đang chờ nhận
     */
    public List<ChiTietPhieuDatPhong> layPhongChoNhanTheoSoDienThoai(String soDienThoai) {
        List<ChiTietPhieuDatPhong> dsChiTiet = new ArrayList<>();
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            return dsChiTiet;
        }

        LocalDateTime thoiGianHienTai = LocalDateTime.now();

        String sql = "SELECT DISTINCT ctpdp.maPhieuDatPhong, ctpdp.maPhong, ctpdp.thoiGianNhanPhong, ctpdp.thoiGianTraPhong, " +
                "       ctpdp.maLoaiDatPhong, ctpdp.soNguoi, ctpdp.trangThai, " +
                "       p.soPhong, p.trangThai AS trangThaiPhong, p.tang, " +
                "       lp.maLoaiPhong, lp.tenLoaiPhong, lp.gia, " +
                "       ldp.tenLoaiDatPhong, " +
                "       pdp.maPhieuDatPhong, pdp.ngayTao, " +
                "       kh.maKhachHang, kh.CCCD, kh.hoTen, kh.soDienThoai, kh.email " +
                "FROM ChiTietPhieuDatPhong ctpdp " +
                "JOIN PhieuDatPhong pdp ON ctpdp.maPhieuDatPhong = pdp.maPhieuDatPhong " +
                "JOIN KhachHang kh ON pdp.maKhachHang = kh.maKhachHang " +
                "JOIN Phong p ON p.maPhong = ctpdp.maPhong " +
                "JOIN LoaiPhong lp ON lp.maLoaiPhong = p.maLoaiPhong " +
                "JOIN LoaiDatPhong ldp ON ldp.maLoaiDatPhong = ctpdp.maLoaiDatPhong " +
                "WHERE kh.soDienThoai = ? " +
                "  AND p.trangThai = N'Đã đặt' " +
                "  AND ctpdp.thoiGianNhanPhong IS NOT NULL " +
                "  AND DATEADD(HOUR, -1, ctpdp.thoiGianNhanPhong) <= ? " +
                "  AND ? <= DATEADD(HOUR, 6, ctpdp.thoiGianNhanPhong) " +
                "ORDER BY ctpdp.thoiGianNhanPhong ASC";

        try (Connection connect = ConnectDatabase.getConnection();
                PreparedStatement ps = connect.prepareStatement(sql)) {

            ps.setString(1, soDienThoai.trim());
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(thoiGianHienTai));
            ps.setTimestamp(3, java.sql.Timestamp.valueOf(thoiGianHienTai));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dsChiTiet.add(taoChiTietPhieuDatPhongTuResultSet(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dsChiTiet;
    }

    /**
     * Lấy tất cả phòng đã đặt theo số điện thoại (bao gồm cả chưa đến thời gian, đang chờ nhận, quá thời gian)
     * @param soDienThoai Số điện thoại khách hàng
     * @return Danh sách ChiTietPhieuDatPhong đã đặt
     */
    public List<ChiTietPhieuDatPhong> layTatCaPhongDaDatTheoSoDienThoai(String soDienThoai) {
        List<ChiTietPhieuDatPhong> dsChiTiet = new ArrayList<>();
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            return dsChiTiet;
        }

        String sql = "SELECT DISTINCT ctpdp.maPhieuDatPhong, ctpdp.maPhong, ctpdp.thoiGianNhanPhong, ctpdp.thoiGianTraPhong, " +
                "       ctpdp.maLoaiDatPhong, ctpdp.soNguoi, ctpdp.trangThai, " +
                "       p.soPhong, p.trangThai AS trangThaiPhong, p.tang, " +
                "       lp.maLoaiPhong, lp.tenLoaiPhong, lp.gia, " +
                "       ldp.tenLoaiDatPhong, " +
                "       pdp.maPhieuDatPhong, pdp.ngayTao, " +
                "       kh.maKhachHang, kh.CCCD, kh.hoTen, kh.soDienThoai, kh.email " +
                "FROM ChiTietPhieuDatPhong ctpdp " +
                "JOIN PhieuDatPhong pdp ON ctpdp.maPhieuDatPhong = pdp.maPhieuDatPhong " +
                "JOIN KhachHang kh ON pdp.maKhachHang = kh.maKhachHang " +
                "JOIN Phong p ON p.maPhong = ctpdp.maPhong " +
                "JOIN LoaiPhong lp ON lp.maLoaiPhong = p.maLoaiPhong " +
                "JOIN LoaiDatPhong ldp ON ldp.maLoaiDatPhong = ctpdp.maLoaiDatPhong " +
                "WHERE kh.soDienThoai = ? " +
                "  AND pdp.trangThai IN (N'Đã đặt', N'Đang ở') " +
                "ORDER BY ctpdp.thoiGianNhanPhong ASC";

        try (Connection connect = ConnectDatabase.getConnection();
                PreparedStatement ps = connect.prepareStatement(sql)) {

            ps.setString(1, soDienThoai.trim());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dsChiTiet.add(taoChiTietPhieuDatPhongTuResultSet(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dsChiTiet;
    }

    /**
     * Helper method: Tạo ChiTietPhieuDatPhong từ ResultSet
     */
    private ChiTietPhieuDatPhong taoChiTietPhieuDatPhongTuResultSet(ResultSet rs) throws Exception {
        String maPhong = rs.getString("maPhong");
        String soPhong = rs.getString("soPhong");
        String trangThaiPhong = rs.getString("trangThaiPhong");
        int tang = rs.getInt("tang");

        String maLoaiPhong = rs.getString("maLoaiPhong");
        String tenLoaiPhong = rs.getString("tenLoaiPhong");
        double gia = rs.getDouble("gia");
        LoaiPhong loaiPhong = new LoaiPhong(maLoaiPhong, tenLoaiPhong, gia, new ArrayList<>());

        Phong phong = new Phong(maPhong, soPhong, loaiPhong, trangThaiPhong, tang);

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

        // Tạo PhieuDatPhong
        String maPhieuDatPhong = rs.getString("maPhieuDatPhong");
        java.sql.Date sqlNgayTao = rs.getDate("ngayTao");
        LocalDate ngayTao = sqlNgayTao != null ? sqlNgayTao.toLocalDate() : null;

        String maKhachHang = rs.getString("maKhachHang");
        String cccdKH = rs.getString("CCCD");
        String hoTen = rs.getString("hoTen");
        String soDienThoai = rs.getString("soDienThoai");
        String email = rs.getString("email");

        KhachHang khachHang = new KhachHang(maKhachHang, cccdKH, hoTen, soDienThoai, email);
        PhieuDatPhong pdp = new PhieuDatPhong(maPhieuDatPhong, khachHang, ngayTao, new ArrayList<>());

        ChiTietPhieuDatPhong ctpdp = new ChiTietPhieuDatPhong(
                pdp, loaiDatPhong, new ArrayList<>(), soGioLuuTru,
                thoiGianNhanPhong, thoiGianTraPhong, phong, soNguoi);

        return ctpdp;
    }
}