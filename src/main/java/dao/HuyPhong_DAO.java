package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import config.ConnectDatabase;
import model.ChiTietPhieuDatPhong;
import model.DichVu;
import model.KhachHang;
import model.PhieuDatPhong;
import model.PhieuHuyPhong;
import model.Phong;

public class HuyPhong_DAO {
    public HuyPhong_DAO() {
    }

    public boolean themHuyPhong(List<ChiTietPhieuDatPhong> dsPhieuDatPhong, String lyDo, LocalDate ngayHuy) {
        String sql = "INSERT INTO HuyPhong(maPhieuDatPhong, lyDo, ngayHuy) VALUES (?, ?, ?)";

        try (Connection connect = ConnectDatabase.getConnection();
                PreparedStatement stmt = connect.prepareStatement(sql)) {

            // Duyệt danh sách các chi tiết phiếu đặt phòng
            for (ChiTietPhieuDatPhong ct : dsPhieuDatPhong) {
                if (ct.getPhieuDatPhong() == null || ct.getPhieuDatPhong().getMaPhieuDatPhong() == null)
                    continue; // bỏ qua dòng null

                stmt.setString(1, ct.getPhieuDatPhong().getMaPhieuDatPhong());
                stmt.setString(2, lyDo);
                stmt.setDate(3, Date.valueOf(ngayHuy));

                stmt.addBatch(); // gom lệnh vào batch chưa thực thi insert
            }

            // Thực thi batch (một lần gửi nhiều câu lệnh đến DB)
            int[] result = stmt.executeBatch();

            connect.close();
            return result.length > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<PhieuHuyPhong> layDsPhieuHuyPhong() {

        String sql = """
                SELECT
                    hp.maHuyPhong,
                    hp.lyDo,
                    hp.ngayHuy,

                    pdp.maPhieuDatPhong,
                    pdp.ngayTao,
                    pdp.trangThai,
                    pdp.tienDatCoc,

                    kh.maKhachHang,
                    kh.cccd,
                    kh.hoTen,
                    kh.soDienThoai,
                    kh.email,

                    p.maPhong,
                    p.soPhong,
                    p.tang,
                    p.trangThai AS trangThaiPhong,
                    p.tinhTrang
                FROM HuyPhong hp
                JOIN PhieuDatPhong pdp ON hp.maPhieuDatPhong = pdp.maPhieuDatPhong
                JOIN KhachHang kh ON kh.maKhachHang = pdp.maKhachHang
                JOIN ChiTietPhieuDatPhong ctpdp ON ctpdp.maPhieuDatPhong = pdp.maPhieuDatPhong
                JOIN Phong p ON p.maPhong = ctpdp.maPhong
                """;

        List<PhieuHuyPhong> ketQua = new ArrayList<>();

        try (
                Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                /* ===== Khách hàng ===== */
                KhachHang kh = new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("cccd"),
                        rs.getString("hoTen"),
                        rs.getString("soDienThoai"),
                        rs.getString("email"));

                /* ===== Phiếu đặt phòng ===== */
                PhieuDatPhong pdp = new PhieuDatPhong(
                        rs.getString("maPhieuDatPhong"),
                        kh);
                pdp.setNgayTao(rs.getDate("ngayTao").toLocalDate());
                pdp.setTrangThai(rs.getString("trangThai"));
                pdp.setTienDatCoc(rs.getLong("tienDatCoc"));

                /* ===== Phòng ===== */
                Phong phong = new Phong(
                        rs.getString("maPhong"));
                phong.setSoPhong(rs.getString("soPhong"));
                phong.setTang(rs.getInt("tang"));
                phong.setTrangThai(rs.getString("trangThaiPhong"));
                phong.setTinhTrang(rs.getString("tinhTrang"));

                /* ===== Chi tiết PDP (1 phòng) ===== */
                ChiTietPhieuDatPhong ct = new ChiTietPhieuDatPhong(
                        pdp,
                        null,
                        null,
                        0,
                        null,
                        null,
                        phong,
                        0);

                pdp.getDsachPhieuDatPhong().add(ct);

                /* ===== Phiếu hủy phòng ===== */
                PhieuHuyPhong php = new PhieuHuyPhong(
                        pdp,
                        rs.getString("lyDo"),
                        rs.getDate("ngayHuy"));

                php.setMaHuyPhong(rs.getString("maHuyPhong"));

                ketQua.add(php);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ketQua;
    }

    public List<PhieuHuyPhong> layDsPhieuHuyPhongTheoNgay(LocalDate ngay) {

        String sql = """
                SELECT
                    hp.maHuyPhong,
                    hp.lyDo,
                    hp.ngayHuy,

                    pdp.maPhieuDatPhong,

                    kh.maKhachHang,
                    kh.cccd,
                    kh.hoTen,
                    kh.soDienThoai,

                    p.maPhong,
                    p.soPhong
                FROM HuyPhong hp
                JOIN PhieuDatPhong pdp ON hp.maPhieuDatPhong = pdp.maPhieuDatPhong
                JOIN KhachHang kh ON kh.maKhachHang = pdp.maKhachHang
                JOIN ChiTietPhieuDatPhong ct ON ct.maPhieuDatPhong = pdp.maPhieuDatPhong
                JOIN Phong p ON p.maPhong = ct.maPhong
                WHERE hp.ngayHuy = ?
                ORDER BY hp.ngayHuy DESC
                """;

        List<PhieuHuyPhong> ds = new ArrayList<>();

        try (
                Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(ngay));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                KhachHang kh = new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("cccd"),
                        rs.getString("hoTen"),
                        rs.getString("soDienThoai"),
                        null);

                PhieuDatPhong pdp = new PhieuDatPhong(
                        rs.getString("maPhieuDatPhong"),
                        kh);

                Phong phong = new Phong(rs.getString("maPhong"));
                phong.setSoPhong(rs.getString("soPhong"));

                ChiTietPhieuDatPhong ct = new ChiTietPhieuDatPhong(
                        pdp, null, null, 0, null, null, phong, 0);

                pdp.getDsachPhieuDatPhong().add(ct);

                PhieuHuyPhong php = new PhieuHuyPhong(
                        pdp,
                        rs.getString("lyDo"),
                        rs.getDate("ngayHuy"));

                php.setMaHuyPhong(rs.getString("maHuyPhong"));
                ds.add(php);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }

    public List<PhieuHuyPhong> layDsPhieuHuyPhongTheoKhoangNgay(LocalDate tuNgay, LocalDate denNgay) {

        String sql = """
                SELECT
                    hp.maHuyPhong,
                    hp.lyDo,
                    hp.ngayHuy,

                    pdp.maPhieuDatPhong,

                    kh.maKhachHang,
                    kh.cccd,
                    kh.hoTen,
                    kh.soDienThoai,

                    p.maPhong,
                    p.soPhong
                FROM HuyPhong hp
                JOIN PhieuDatPhong pdp ON hp.maPhieuDatPhong = pdp.maPhieuDatPhong
                JOIN KhachHang kh ON kh.maKhachHang = pdp.maKhachHang
                JOIN ChiTietPhieuDatPhong ct ON ct.maPhieuDatPhong = pdp.maPhieuDatPhong
                JOIN Phong p ON p.maPhong = ct.maPhong
                WHERE hp.ngayHuy BETWEEN ? AND ?
                ORDER BY hp.ngayHuy DESC
                """;

        List<PhieuHuyPhong> ds = new ArrayList<>();

        try (
                Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(tuNgay));
            ps.setDate(2, Date.valueOf(denNgay));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                KhachHang kh = new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("cccd"),
                        rs.getString("hoTen"),
                        rs.getString("soDienThoai"),
                        null);

                PhieuDatPhong pdp = new PhieuDatPhong(
                        rs.getString("maPhieuDatPhong"),
                        kh);

                Phong phong = new Phong(rs.getString("maPhong"));
                phong.setSoPhong(rs.getString("soPhong"));

                ChiTietPhieuDatPhong ct = new ChiTietPhieuDatPhong(
                        pdp, null, null, 0, null, null, phong, 0);

                pdp.getDsachPhieuDatPhong().add(ct);

                PhieuHuyPhong php = new PhieuHuyPhong(
                        pdp,
                        rs.getString("lyDo"),
                        rs.getDate("ngayHuy"));

                php.setMaHuyPhong(rs.getString("maHuyPhong"));
                ds.add(php);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }

}
