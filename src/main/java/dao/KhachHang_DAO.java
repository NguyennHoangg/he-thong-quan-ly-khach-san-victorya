package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import config.ConnectDatabase;
import model.KhachHang;

public class KhachHang_DAO {

    public List<KhachHang> getDsKhachHang() {
        List<KhachHang> dsKetQua = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang";

        try (Connection connect = ConnectDatabase.getConnection();
                Statement stmt = connect.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String maKH = rs.getString("maKhachHang");
                String cccd = rs.getString("CCCD");
                String ten = rs.getString("hoTen");
                String soDienThoai = rs.getString("soDienThoai");
                String email = rs.getString("email");
                LocalDate ngayTao = rs.getDate("ngayTao") != null ? rs.getDate("ngayTao").toLocalDate() : null;

                KhachHang kh = new KhachHang(maKH, cccd, ten, soDienThoai, email, ngayTao);
                dsKetQua.add(kh);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsKetQua;
    }

    public boolean themKhachHang(KhachHang kh) {
        String sql = "INSERT INTO KhachHang(maKhachHang, CCCD, hoTen, soDienThoai, email, ngayTao) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection connect = ConnectDatabase.getConnection();
                PreparedStatement ps = connect.prepareStatement(sql)) {

            ps.setString(1, phatSinhMaKhachHang());
            ps.setString(2, kh.getCCCD());
            ps.setString(3, kh.getTenKhachHang());
            ps.setString(4, kh.getSoDienThoai());
            ps.setString(5, kh.getEmail());
            ps.setDate(6, kh.getNgayTao() != null ? Date.valueOf(kh.getNgayTao()) : Date.valueOf(LocalDate.now()));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean capNhatKhachHang(KhachHang kh) {
        String sql = "UPDATE KhachHang SET hoTen = ?, soDienThoai = ?, email = ? WHERE maKhachHang = ?";
        try (Connection con = ConnectDatabase.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, kh.getTenKhachHang());
            ps.setString(2, kh.getSoDienThoai());
            ps.setString(3, kh.getEmail());
            ps.setString(4, kh.getMaKhachHang());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean xoaKhachHang(String maKhachHang) {
        if (maKhachHang == null || maKhachHang.trim().isEmpty()) {
            System.err.println("Lỗi: Mã khách hàng không hợp lệ!");
            return false;
        }
        
        String sql = "DELETE FROM KhachHang WHERE maKhachHang = ?";
        try (Connection con = ConnectDatabase.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maKhachHang.trim());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                return true;
            } else {
                System.err.println("Không tìm thấy khách hàng với mã: " + maKhachHang);
                return false;
            }
        } catch (java.sql.SQLException e) {
            // Kiểm tra nếu là foreign key constraint
            String errorMessage = e.getMessage();
            if (errorMessage != null && (errorMessage.contains("FK__") || errorMessage.contains("foreign key") 
                    || errorMessage.contains("REFERENCE") || errorMessage.contains("constraint"))) {
                System.err.println("Không thể xóa khách hàng vì có dữ liệu liên quan (Phiếu đặt phòng, Hóa đơn)!");
                System.err.println("Chi tiết: " + errorMessage);
            } else {
                System.err.println("Lỗi khi xóa khách hàng: " + errorMessage);
            }
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi xóa khách hàng: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public KhachHang timKhachHangTheoCCCD(String cccd) {
        String sql = "SELECT * FROM KhachHang WHERE CCCD = ?";
        try (Connection connect = ConnectDatabase.getConnection();
                PreparedStatement ps = connect.prepareStatement(sql)) {

            ps.setString(1, cccd);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String maKH = rs.getString("maKhachHang");
                    String ten = rs.getString("hoTen");
                    String soDienThoai = rs.getString("soDienThoai");
                    String email = rs.getString("email");
                    LocalDate ngayTao = rs.getDate("ngayTao") != null ? rs.getDate("ngayTao").toLocalDate() : null;
                    return new KhachHang(maKH, cccd, ten, soDienThoai, email, ngayTao);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Tìm danh sách khách hàng có CCCD bắt đầu bằng chuỗi tìm kiếm (dùng cho autocomplete)
     * @param cccdPrefix - Chuỗi ký tự đầu của CCCD
     * @return Danh sách khách hàng có CCCD bắt đầu bằng cccdPrefix
     */
    public List<KhachHang> timKhachHangTheoCCCDStartsWith(String cccdPrefix) {
        List<KhachHang> dsKhachHang = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang WHERE CCCD LIKE ?";
        
        try (Connection connect = ConnectDatabase.getConnection();
                PreparedStatement ps = connect.prepareStatement(sql)) {

            ps.setString(1, cccdPrefix + "%"); // Tìm CCCD bắt đầu bằng cccdPrefix
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maKH = rs.getString("maKhachHang");
                    String cccd = rs.getString("CCCD");
                    String ten = rs.getString("hoTen");
                    String soDienThoai = rs.getString("soDienThoai");
                    String email = rs.getString("email");
                    LocalDate ngayTao = rs.getDate("ngayTao") != null ? rs.getDate("ngayTao").toLocalDate() : null;
                    dsKhachHang.add(new KhachHang(maKH, cccd, ten, soDienThoai, email, ngayTao));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsKhachHang;
    }

    public String phatSinhMaKhachHang() {
        String sql = "SELECT COUNT(*) as soLuong FROM KhachHang";
        try {
            Connection con = ConnectDatabase.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            int soLuong = 0;
            if (rs.next()) {
                soLuong = rs.getInt("soLuong");
            }
            return String.format("KH-%05d", soLuong + 1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public KhachHang timKhachHangTheoMa(String maKhachHang) {
        String sql = "SELECT * FROM KhachHang WHERE maKhachHang = ?";
        try (Connection connect = ConnectDatabase.getConnection();
                PreparedStatement ps = connect.prepareStatement(sql)) {

            ps.setString(1, maKhachHang);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String cccd = rs.getString("CCCD");
                    String ten = rs.getString("hoTen");
                    String soDienThoai = rs.getString("soDienThoai");
                    String email = rs.getString("email");
                    LocalDate ngayTao = rs.getDate("ngayTao") != null ? rs.getDate("ngayTao").toLocalDate() : null;
                    return new KhachHang(maKhachHang, cccd, ten, soDienThoai, email, ngayTao);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public KhachHang timKhachHangTheoEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT * FROM KhachHang WHERE email = ?";
        try (Connection connect = ConnectDatabase.getConnection();
                PreparedStatement ps = connect.prepareStatement(sql)) {

            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String maKH = rs.getString("maKhachHang");
                    String cccd = rs.getString("CCCD");
                    String ten = rs.getString("hoTen");
                    String soDienThoai = rs.getString("soDienThoai");
                    LocalDate ngayTao = rs.getDate("ngayTao") != null ? rs.getDate("ngayTao").toLocalDate() : null;
                    return new KhachHang(maKH, cccd, ten, soDienThoai, email, ngayTao);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public KhachHang timKhachHangTheoSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT * FROM KhachHang WHERE soDienThoai = ?";
        try (Connection connect = ConnectDatabase.getConnection();
                PreparedStatement ps = connect.prepareStatement(sql)) {

            ps.setString(1, soDienThoai.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String maKH = rs.getString("maKhachHang");
                    String cccd = rs.getString("CCCD");
                    String ten = rs.getString("hoTen");
                    String email = rs.getString("email");
                    LocalDate ngayTao = rs.getDate("ngayTao") != null ? rs.getDate("ngayTao").toLocalDate() : null;
                    return new KhachHang(maKH, cccd, ten, soDienThoai, email, ngayTao);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

