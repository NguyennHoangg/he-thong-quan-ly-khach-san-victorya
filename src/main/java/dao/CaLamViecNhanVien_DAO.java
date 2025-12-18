package dao;

import config.ConnectDatabase;
import model.CaLamViecNhanVien;

import java.sql.*;
import java.time.LocalDate;

import java.util.ArrayList;

import java.util.List;

public class CaLamViecNhanVien_DAO {
    
    public boolean moCaLamViec(String maNhanVien, double tienMoCa) {
        Connection con = null;
        try {
            con = ConnectDatabase.getConnection();
            con.setAutoCommit(false);  // Start transaction
            
            // Check if employee already has an open shift
            String checkOpenShift = "SELECT COUNT(*) FROM CaLamViecNhanVien WHERE maNhanVien = ? AND trangThai = N'Đang mở'";
            try (PreparedStatement checkStmt = con.prepareStatement(checkOpenShift)) {
                checkStmt.setString(1, maNhanVien);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.err.println("Nhân viên đã có ca đang mở. Không thể mở ca mới.");
                    con.rollback();
                    return false;
                }
            }
            
            String maCaLamViec = generateMaCaLamViec();
            String maCa = generateMaCa();
            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
            
            // Insert into Ca table if not exists
            String checkCaSql = "IF NOT EXISTS (SELECT 1 FROM Ca WHERE maCa = ?) " +
                               "INSERT INTO Ca (maCa, ngayBatDau, ngayKetThuc) VALUES (?, ?, ?)";
            try (PreparedStatement checkStmt = con.prepareStatement(checkCaSql)) {
                checkStmt.setString(1, maCa);
                checkStmt.setString(2, maCa);
                checkStmt.setDate(3, today);
                checkStmt.setDate(4, today);
                checkStmt.executeUpdate();
            }
            
            // Insert into CaLamViecNhanVien table
            String sql = "INSERT INTO CaLamViecNhanVien (maCaLamViec, maNhanVien, ngay, tienMoCa, tienKetCa, tongChi, tongThu, maCa, trangThai) " +
                        "VALUES (?, ?, ?, ?, NULL, 0, 0, ?, ?)";
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setString(1, maCaLamViec);
                pstmt.setString(2, maNhanVien);
                pstmt.setDate(3, today);
                pstmt.setDouble(4, tienMoCa);
                pstmt.setString(5, maCa);
                pstmt.setString(6, "Đang mở");
                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted == 0) {
                    System.err.println("Không thể insert ca làm việc vào database");
                    con.rollback();
                    return false;
                }
            }
            
            con.commit();  // Commit transaction
            System.out.println("Đã mở ca làm việc thành công: " + maCaLamViec);
            return true;
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();  // Rollback on error
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            System.err.println("Lỗi khi mở ca làm việc: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public boolean ketCaLamViec(String maCaLamViec, double tienKetCa) {
        String sql = "UPDATE CaLamViecNhanVien SET tienKetCa = ?, trangThai = ? WHERE maCaLamViec = ? AND trangThai = N'Đang mở'";
        
        try (Connection con = ConnectDatabase.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setDouble(1, tienKetCa);
            pstmt.setString(2, "Đã hoàn thành");
            pstmt.setString(3, maCaLamViec);
            
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated == 0) {
                System.err.println("Không thể kết ca: Không tìm thấy ca làm việc đang mở với mã: " + maCaLamViec);
                return false;
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi khi kết ca làm việc: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public CaLamViecNhanVien getCaLamViecDangMoByNhanVien(String maNhanVien) {
        String sql = "SELECT * FROM CaLamViecNhanVien WHERE maNhanVien = ? AND trangThai = N'Đang mở'";
        
        try (Connection con = ConnectDatabase.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setString(1, maNhanVien);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCaLamViec(rs);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy ca làm việc đang mở: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public CaLamViecNhanVien getCaLamViecByMa(String maCaLamViec) {
        String sql = "SELECT * FROM CaLamViecNhanVien WHERE maCaLamViec = ?";
        
        try (Connection con = ConnectDatabase.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setString(1, maCaLamViec);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCaLamViec(rs);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy ca làm việc theo mã: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public List<CaLamViecNhanVien> getAllCaLamViecByNhanVien(String maNhanVien) {
        List<CaLamViecNhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM CaLamViecNhanVien WHERE maNhanVien = ? ORDER BY ngay DESC";
        
        try (Connection con = ConnectDatabase.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setString(1, maNhanVien);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                list.add(mapResultSetToCaLamViec(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public boolean capNhatCaLamViec(CaLamViecNhanVien ca) {
        // Validate values to prevent overflow (DECIMAL(18,2) max: 9,999,999,999,999,999.99)
        final double MAX_VALUE = 9999999999999999.99;
        
        if (ca.getTongChi() > MAX_VALUE || ca.getTongThu() > MAX_VALUE) {
            System.err.println("ERROR: Giá trị quá lớn! tongChi=" + ca.getTongChi() + ", tongThu=" + ca.getTongThu());
            System.err.println("Giá trị tối đa: " + MAX_VALUE);
            return false;
        }
        
        if (ca.getTongChi() < 0 || ca.getTongThu() < 0) {
            System.err.println("ERROR: Giá trị không được âm! tongChi=" + ca.getTongChi() + ", tongThu=" + ca.getTongThu());
            return false;
        }
        
        String sql = "UPDATE CaLamViecNhanVien SET tongChi = ?, tongThu = ? WHERE maCaLamViec = ?";
        
        try (Connection con = ConnectDatabase.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setDouble(1, ca.getTongChi());
            pstmt.setDouble(2, ca.getTongThu());
            pstmt.setString(3, ca.getMaCaLamViec());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✓ Cập nhật ca làm việc thành công: " + ca.getMaCaLamViec());
                return true;
            } else {
                System.err.println("✗ Không tìm thấy ca làm việc để cập nhật: " + ca.getMaCaLamViec());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("✗ Lỗi khi cập nhật ca làm việc: " + e.getMessage());
            if (e.getMessage().contains("overflow") || e.getMessage().contains("Arithmetic")) {
                System.err.println("Giá trị quá lớn cho cột DECIMAL. tongChi=" + ca.getTongChi() + ", tongThu=" + ca.getTongThu());
                System.err.println("Hãy chạy script Fix_Overflow_Issue.sql để tăng kích thước cột!");
            }
            e.printStackTrace();
            return false;
        }
    }
    
    private CaLamViecNhanVien mapResultSetToCaLamViec(ResultSet rs) throws SQLException {
        // Get tienKetCa and handle NULL value
        double tienKetCa = rs.getDouble("tienKetCa");
        if (rs.wasNull()) {
            tienKetCa = 0.0;
        }
        
        CaLamViecNhanVien ca = new CaLamViecNhanVien(
            rs.getString("maCaLamViec"),
            "",
            null,
            (float) rs.getDouble("tienMoCa"),
            tienKetCa,
            rs.getString("trangThai"),
            rs.getDouble("tongChi"),
            rs.getDouble("tongThu"),
            null,
            null
        );
        ca.setNgay(rs.getDate("ngay"));
        return ca;
    }
    
    private String generateMaCaLamViec() {
        // Generate unique ID using timestamp and random number
        long timestamp = System.currentTimeMillis() % 100000;
        int random = (int)(Math.random() * 1000);
        return "CLV" + timestamp + random;
    }
    
    private String generateMaCa() {
        LocalDate today = LocalDate.now();
        java.time.LocalTime now = java.time.LocalTime.now();
        int hour = now.getHour();
        
        // Xác định ca dựa trên giờ hiện tại
        // Ca 1: 6h - 14h
        // Ca 2: 14h - 22h  
        // Ca 3: 22h - 6h (qua đêm)
        int caSo;
        if (hour >= 6 && hour < 14) {
            caSo = 1;
        } else if (hour >= 14 && hour < 22) {
            caSo = 2;
        } else {
            caSo = 3;
            // Nếu là ca 3 và đang trong khoảng 0h-6h (sau nửa đêm)
            // thì ngày của ca này là ngày hôm trước
            if (hour >= 0 && hour < 6) {
                today = today.minusDays(1);
            }
        }
        
        return "CA-" + today.getYear() + String.format("%02d", today.getMonthValue()) + 
               String.format("%02d", today.getDayOfMonth()) + "-" + caSo;
    }
}
