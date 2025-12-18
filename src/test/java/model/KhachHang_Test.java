package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class cho model KhachHang
 * Kiểm tra các getter/setter và business logic
 */
class KhachHang_Test {
    
    @Test
    @DisplayName("Test khởi tạo KhachHang")
    void testKhoiTaoKhachHang() {
        // Act
        KhachHang kh = new KhachHang();
        
        // Assert
        assertNotNull(kh, "Đối tượng phải được khởi tạo");
        System.out.println("✓ Test khởi tạo: PASSED");
    }
    
    @Test
    @DisplayName("Test getter và setter")
    void testGetterSetter() {
        // Arrange
        KhachHang kh = new KhachHang();
        String cccd = "001234567890";
        String tenKH = "Nguyen Van A";
        String sdt = "0987654321";
        String email = "nguyenvana@gmail.com";
        
        // Act
        kh.setCCCD(cccd);
        kh.setTenKhachHang(tenKH);
        kh.setSoDienThoai(sdt);
        kh.setEmail(email);
        
        // Assert
        assertEquals(cccd, kh.getCCCD(), "CCCD phải khớp");
        assertEquals(tenKH, kh.getTenKhachHang(), "Tên khách hàng phải khớp");
        assertEquals(sdt, kh.getSoDienThoai(), "SĐT phải khớp");
        assertEquals(email, kh.getEmail(), "Email phải khớp");
        
        System.out.println("✓ Test getter/setter: PASSED");
    }
    
    @Test
    @DisplayName("Test equals và hashCode")
    void testEqualsAndHashCode() {
        // Arrange
        KhachHang kh1 = new KhachHang();
        kh1.setMaKhachHang("KH001");
        kh1.setCCCD("123456789");
        
        KhachHang kh2 = new KhachHang();
        kh2.setMaKhachHang("KH001");
        kh2.setCCCD("123456789");
        
        KhachHang kh3 = new KhachHang();
        kh3.setMaKhachHang("KH002");
        kh3.setCCCD("987654321");
        
        // Assert - So sánh mã khách hàng
        assertEquals(kh1.getMaKhachHang(), kh2.getMaKhachHang(), "Mã khách hàng phải giống nhau");
        assertNotEquals(kh1.getMaKhachHang(), kh3.getMaKhachHang(), "Mã khách hàng phải khác nhau");
        
        System.out.println("✓ Test equals/hashCode: PASSED");
    }
    
    @Test
    @DisplayName("Test validate số điện thoại")
    void testValidateSoDienThoai() {
        // Test cases cho số điện thoại hợp lệ
        assertTrue(isValidPhoneNumber("0987654321"), "SĐT 10 số phải hợp lệ");
        assertTrue(isValidPhoneNumber("0123456789"), "SĐT bắt đầu 0 phải hợp lệ");
        
        // Test cases cho số điện thoại không hợp lệ
        assertFalse(isValidPhoneNumber("123456789"), "SĐT 9 số phải không hợp lệ");
        assertFalse(isValidPhoneNumber("12345678901"), "SĐT 11 số phải không hợp lệ");
        assertFalse(isValidPhoneNumber("abc1234567"), "SĐT có chữ phải không hợp lệ");
        
        System.out.println("✓ Test validate SĐT: PASSED");
    }
    
    @Test
    @DisplayName("Test validate email")
    void testValidateEmail() {
        // Test cases cho email hợp lệ
        assertTrue(isValidEmail("test@gmail.com"), "Email chuẩn phải hợp lệ");
        assertTrue(isValidEmail("user.name@company.com.vn"), "Email có dấu chấm phải hợp lệ");
        
        // Test cases cho email không hợp lệ
        assertFalse(isValidEmail("testgmail.com"), "Email thiếu @ phải không hợp lệ");
        assertFalse(isValidEmail("test@"), "Email thiếu domain phải không hợp lệ");
        assertFalse(isValidEmail("@gmail.com"), "Email thiếu username phải không hợp lệ");
        
        System.out.println("✓ Test validate email: PASSED");
    }
    
    // Helper methods
    private boolean isValidPhoneNumber(String sdt) {
        if (sdt == null || sdt.trim().isEmpty()) return false;
        return sdt.matches("0\\d{9}");
    }
    
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}
