package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import config.ConnectDatabase;
import model.KhachHang;

public class KhachHang_DAO {
    /**
     * Tìm kiếm khách hàng theo CCCD (tương tự LIKE)
     */
    public List<KhachHang> timKiemKhachHangTheoCCCD(String cccdPattern) {
        List<KhachHang> result = new ArrayList<>();
        try {
            Connection connection = ConnectDatabase.getConnection();
            String query = "SELECT maKhachHang, CCCD, hoTen, soDienThoai, email FROM KhachHang WHERE CCCD LIKE ? ORDER BY CCCD";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, "%" + cccdPattern + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String maKH = rs.getString("maKhachHang");
                String cccd = rs.getString("CCCD");
                String hoTen = rs.getString("hoTen");
                String sdt = rs.getString("soDienThoai");
                String email = rs.getString("email");

                KhachHang kh = new KhachHang(maKH, cccd, hoTen, sdt, email);
                result.add(kh);
            }

            rs.close();
            ps.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * Lấy số lượng khách hàng hiện có trong database
     * @return Số lượng khách hàng
     */
    public int getCountKhachHang() {
        String sql = "SELECT COUNT(*) as total FROM KhachHang";
        
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Lấy mã khách hàng cuối cùng từ database
     * @return Mã khách hàng cuối cùng hoặc null nếu chưa có khách hàng nào
     */
    public String getMaKhachHangCuoiCung() {
        String sql = "SELECT TOP 1 maKhachHang FROM KhachHang ORDER BY maKhachHang DESC";
        
        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getString("maKhachHang");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Tìm khách hàng theo CCCD chính xác
     */
    public KhachHang timKhachHangTheoCCCD(String cccd) {
        try (Connection conn = ConnectDatabase.getConnection()) {
            String sql = "SELECT maKhachHang, CCCD, hoTen, soDienThoai, email FROM KhachHang WHERE CCCD = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, cccd);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String maKH = rs.getString("maKhachHang");
                String cccdKH = rs.getString("CCCD");
                String hoTen = rs.getString("hoTen");
                String sdt = rs.getString("soDienThoai");
                String email = rs.getString("email");
                
                return new KhachHang(maKH, cccdKH, hoTen, sdt, email);
            }
            
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Thêm khách hàng mới vào database
     */
    public boolean themKhachHang(KhachHang khachHang) {
        try (Connection conn = ConnectDatabase.getConnection()) {
            String sql = "INSERT INTO KhachHang (maKhachHang, CCCD, hoTen, soDienThoai, email, ngayTao) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            
            ps.setString(1, khachHang.getMaKhachHang());
            ps.setString(2, khachHang.getCCCD());
            ps.setString(3, khachHang.getTenKhachHang());
            ps.setString(4, khachHang.getSoDienThoai());
            ps.setString(5, khachHang.getEmail());
            ps.setDate(6, java.sql.Date.valueOf(java.time.LocalDate.now()));
            
            int result = ps.executeUpdate();
            ps.close();
            
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
