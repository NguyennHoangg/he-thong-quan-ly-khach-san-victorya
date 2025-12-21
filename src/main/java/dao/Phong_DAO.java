package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.ConnectDatabase;
import model.DichVu;
import model.LoaiPhong;
import model.Phong;

public class Phong_DAO {
    public Phong_DAO() {

    }

    // ĐẾM TỔNG SỐ PHÒNG
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM Phong";
        try (Connection connection = ConnectDatabase.getConnection();
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ĐẾM SỐ PHÒNG TRỐNG (trangThai = 'Trống')
    public int countPhongTrong() {
        String sql = "SELECT COUNT(*) FROM Phong WHERE trangThai = N'Trống'";
        try (Connection connection = ConnectDatabase.getConnection();
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Phong> getTatCaPhong() {
        List<Phong> dsachPhong = new ArrayList<>();
        Map<String, Phong> phongMap = new HashMap<>();

        try {
            var connection = ConnectDatabase.getConnection();
            String query = "SELECT p.*, lp.*, dv.* " +
                    "FROM Phong p " +
                    "JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong " +
                    "LEFT JOIN DichVu_LoaiPhong dvp ON lp.maLoaiPhong = dvp.maLoaiPhong " +
                    "LEFT JOIN DichVu dv ON dvp.maDichVu = dv.maDichVu";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String maPhong = resultSet.getString("maPhong");
                String soPhong = resultSet.getString("soPhong");
                String trangThai = resultSet.getString("trangThai");
                int tang = resultSet.getInt("tang");
                String tenLoaiPhong = resultSet.getString("tenLoaiPhong");
                String maLoaiPhong = resultSet.getString("maLoaiPhong");
                double gia = resultSet.getDouble("gia");
                // Database: soNguoiLonToiDa = sức chứa người lớn, soTreEmToiDa = sức chứa trẻ
                // em
                int soNguoiLonToiDaToiDa = resultSet.getInt("soNguoiLonToiDa");
                int soTreEmToiDaToiDa = resultSet.getInt("soTreEmToiDa");

                if (!phongMap.containsKey(maPhong)) {
                    List<DichVu> dsDichVu = new ArrayList<>();
                    LoaiPhong loaiPhong = new LoaiPhong(maLoaiPhong, tenLoaiPhong, gia, null, dsDichVu,
                            soNguoiLonToiDaToiDa, soTreEmToiDaToiDa);
                    Phong phong = new Phong(maPhong, soPhong, loaiPhong, trangThai, tang);
                    phongMap.put(maPhong, phong);
                }

                String maDichVu = resultSet.getString("maDichVu");
                if (maDichVu != null) {
                    String tenDichVu = resultSet.getString("tenDichVu");
                    DichVu dichVu = new DichVu(maDichVu, tenDichVu);
                    phongMap.get(maPhong).getLoaiPhong().getDsachDichVu().add(dichVu);
                }
            }

            dsachPhong.addAll(phongMap.values());
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dsachPhong;
    }

    public boolean capNhatTrangThaiPhong(String maPhong, String trangThaiMoi) {
        String sql = "UPDATE Phong SET trangThai = ? WHERE maPhong = ?";

        try (Connection connection = ConnectDatabase.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, trangThaiMoi);
            stmt.setString(2, maPhong);

            System.out.println("🔄 Cập nhật trạng thái phòng: " + maPhong + " -> " + trangThaiMoi);

            int n = stmt.executeUpdate();
            connection.close();

            return n > 0;

        } catch (SQLException e) {
            return false;
        }

    }

    /**
     * Tìm kiếm phòng TRỐNG theo khoảng thời gian
     * Trả về danh sách phòng không bị trùng lịch đặt trong khoảng thời gian tìm
     * kiếm
     * 
     * @param loaiPhong         Tên loại phòng (VIP/Thường) hoặc null nếu tìm tất cả
     * @param thoiGianNhanPhong Thời gian check-in mong muốn (yyyy-MM-dd HH:mm:ss)
     * @param thoiGianTraPhong  Thời gian check-out mong muốn (yyyy-MM-dd HH:mm:ss)
     * @return Danh sách phòng trống
     */
    public List<Phong> timKiemPhongTrongTheoThoiGian(String loaiPhong, String thoiGianNhanPhong,
            String thoiGianTraPhong) {
        List<Phong> dsPhongTrong = new ArrayList<>();
        Map<String, Phong> phongMap = new HashMap<>();

        // Query tìm phòng KHÔNG bị đặt trong khoảng thời gian
        String sql = "SELECT DISTINCT " +
                "p.maPhong, p.soPhong, p.trangThai, p.tang, " +
                "lp.maLoaiPhong, lp.tenLoaiPhong, lp.gia, lp.soNguoiLonToiDa, lp.soTreEmToiDa, " +
                "dv.maDichVu, dv.tenDichVu " +
                "FROM Phong p " +
                "JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong " +
                "LEFT JOIN DichVu_LoaiPhong dvp ON lp.maLoaiPhong = dvp.maLoaiPhong " +
                "LEFT JOIN DichVu dv ON dvp.maDichVu = dv.maDichVu " +
                "WHERE (? IS NULL OR ? = '' OR lp.tenLoaiPhong = ?) " +
                "AND p.maPhong NOT IN ( " +
                "    SELECT ct.maPhong " +
                "    FROM ChiTietPhieuDatPhong ct " +
                "    WHERE NOT ( " +
                "        ct.thoiGianTraPhong <= ? OR ct.thoiGianNhanPhong >= ? " +
                "    ) " +
                ") " +
                "ORDER BY p.tang, p.soPhong";

        try (Connection connection = ConnectDatabase.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            // Set parameters cho loại phòng
            ps.setString(1, loaiPhong);
            ps.setString(2, loaiPhong);
            ps.setString(3, loaiPhong);

            // Set parameters cho khoảng thời gian
            // Loại trừ phòng có booking: NOT (kết thúc trước khi bắt đầu HOẶC bắt đầu sau
            // khi kết thúc)
            // = Chỉ lấy phòng: kết thúc <= check-in mong muốn HOẶC bắt đầu >= check-out
            // mong muốn
            ps.setTimestamp(4, Timestamp.valueOf(thoiGianNhanPhong));
            ps.setTimestamp(5, Timestamp.valueOf(thoiGianTraPhong));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String maPhong = rs.getString("maPhong");

                if (!phongMap.containsKey(maPhong)) {
                    String soPhong = rs.getString("soPhong");
                    int tang = rs.getInt("tang");

                    String maLoaiPhong = rs.getString("maLoaiPhong");
                    String tenLoaiPhong = rs.getString("tenLoaiPhong");
                    double gia = rs.getDouble("gia");
                    // Database: soNguoiLonToiDa = sức chứa người lớn, soTreEmToiDa = sức chứa trẻ
                    // em
                    int soNguoiLonToiDaToiDa = rs.getInt("soNguoiLonToiDa");
                    int soTreEmToiDaToiDa = rs.getInt("soTreEmToiDa");

                    List<DichVu> dsDichVu = new ArrayList<>();
                    LoaiPhong loaiPhongObj = new LoaiPhong(maLoaiPhong, tenLoaiPhong, gia, null, dsDichVu,
                            soNguoiLonToiDaToiDa, soTreEmToiDaToiDa);
                    // Set trạng thái = "Trống" vì đây là phòng trống trong khoảng thời gian
                    Phong phong = new Phong(maPhong, soPhong, loaiPhongObj, "Trống", tang);

                    phongMap.put(maPhong, phong);
                }

                // Thêm dịch vụ vào phòng
                String maDichVu = rs.getString("maDichVu");
                if (maDichVu != null) {
                    String tenDichVu = rs.getString("tenDichVu");
                    DichVu dichVu = new DichVu(maDichVu, tenDichVu);
                    phongMap.get(maPhong).getLoaiPhong().getDsachDichVu().add(dichVu);
                }
            }

            dsPhongTrong.addAll(phongMap.values());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dsPhongTrong;
    }

    public List<Phong> getPhongTheoTrangThai(String trangThai) {
        List<Phong> dsKetQua = new ArrayList<>();
        String sql = "SELECT * FROM Phong p JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong " +
                "where trangThai = N'" + trangThai + "';";
        try (Connection connection = ConnectDatabase.getConnection();
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                String maLoaiPhong = rs.getString("maLoaiPhong");
                String tenLoaiPhong = rs.getString("tenLoaiPhong");
                double gia = rs.getDouble("gia");
                LoaiPhong lp = new LoaiPhong(maLoaiPhong, tenLoaiPhong, gia);

                String maPHong = rs.getString("maPhong");
                String soPhong = rs.getString("soPhong");
                int soTang = rs.getInt("tang");

                Phong p = new Phong(maPHong, soPhong, lp, trangThai, soTang);
                dsKetQua.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dsKetQua;
    }

    public List<Phong> getPhongTheoTrangThaiVaLoaiPhong(String trangThai, String loaiPhong) {
        List<Phong> dsKetQua = new ArrayList<>();
        String sql = "SELECT * FROM Phong p " +
                "JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong " +
                "WHERE p.trangThai = N'" + trangThai + "' " +
                "AND lp.tenLoaiPhong = N'" + loaiPhong + "'";
        try (Connection connection = ConnectDatabase.getConnection();
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                String maLoaiPhong = rs.getString("maLoaiPhong");
                String tenLoaiPhong = rs.getString("tenLoaiPhong");
                double gia = rs.getDouble("gia");
                LoaiPhong lp = new LoaiPhong(maLoaiPhong, tenLoaiPhong, gia);

                String maPHong = rs.getString("maPhong");
                String soPhong = rs.getString("soPhong");
                int soTang = rs.getInt("tang");

                Phong p = new Phong(maPHong, soPhong, lp, trangThai, soTang);
                dsKetQua.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dsKetQua;
    }

    public Phong getPhongTheoMa(String ma) {
        String sql = "SELECT * FROM Phong " +
                "where maPhong = N'" + ma + "';";
        try (Connection connection = ConnectDatabase.getConnection();
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                String maLoaiPhong = rs.getString("maLoaiPhong");
                String tenLoaiPhong = rs.getString("tenLoaiPhong");
                double gia = rs.getDouble("gia");
                LoaiPhong lp = new LoaiPhong(maLoaiPhong, tenLoaiPhong, gia);

                String maPHong = rs.getString("maPhong");
                String soPhong = rs.getString("soPhong");
                int soTang = rs.getInt("tang");
                String trangThai = rs.getString("trangThai");

                Phong p = new Phong(maPHong, soPhong, lp, trangThai, soTang);
                connection.close();
                return p;
            }
        } catch (SQLException e) {
            return null;
        }

        return null;
    }

}
