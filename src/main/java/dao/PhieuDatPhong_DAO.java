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
     * Lấy phiếu đặt phòng của khách hàng có phòng chờ nhận
     * 
     * @param cccd CCCD của khách hàng
     * @return PhieuDatPhong của khách hàng với các phòng chờ nhận (trạng thái "Đã đặt")
     */
    public PhieuDatPhong getPhieuDatPhongChoNhanTheoCCCD(String cccd) {
        PhieuDatPhong phieuDatPhong = null;

        String sqlPhieu = "SELECT DISTINCT pdp.maPhieuDatPhong, pdp.ngayTao, " +
                "       kh.maKhachHang, kh.CCCD, kh.hoTen, kh.soDienThoai, kh.email, kh.ngayTao AS ngayTaoKH " +
                "FROM PhieuDatPhong pdp " +
                "JOIN KhachHang kh ON pdp.maKhachHang = kh.maKhachHang " +
                "JOIN ChiTietPhieuDatPhong ctpdp ON pdp.maPhieuDatPhong = ctpdp.maPhieuDatPhong " +
                "JOIN Phong p ON p.maPhong = ctpdp.maPhong " +
                "WHERE kh.CCCD = ? AND p.trangThai = N'Đã đặt' " +
                "  AND ctpdp.thoiGianNhanPhong <= GETDATE() " +
                "  AND ctpdp.thoiGianTraPhong >= GETDATE() " +
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

                    List<ChiTietPhieuDatPhong> dsChiTiet = getChiTietPhieuDatPhongChoDat(connect, maPhieuDatPhong);

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
     * Lấy danh sách chi tiết phiếu đặt phòng có trạng thái "Đã đặt"
     * 
     * @param connect         Connection đã mở
     * @param maPhieuDatPhong Mã phiếu đặt phòng
     * @return Danh sách ChiTietPhieuDatPhong có phòng chờ nhận
     */
    private List<ChiTietPhieuDatPhong> getChiTietPhieuDatPhongChoDat(Connection connect, String maPhieuDatPhong) {
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
                "WHERE ctpdp.maPhieuDatPhong = ? AND p.trangThai = N'Đã đặt' " +
                "  AND ctpdp.thoiGianNhanPhong <= GETDATE() " +
                "  AND ctpdp.thoiGianTraPhong >= GETDATE()";

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
}