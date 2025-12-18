package dao;

import model.KhachHang;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test class cho KhachHang_DAO
 * Hướng dẫn chạy test: Click chuột phải vào class -> Run 'KhachHang_DAO_Test'
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class KhachHang_DAO_Test {
    
    private KhachHang_DAO khachHangDAO;
    private static String testCCCD = "TEST123456789";
    private static String testMaKH = "";
    
    @BeforeEach
    void setUp() {
        // Khởi tạo trước mỗi test
        khachHangDAO = new KhachHang_DAO();
    }
    
    @AfterEach
    void tearDown() {
        // Dọn dẹp sau mỗi test (nếu cần)
    }
    
    /**
     * Test 1: Kiểm tra lấy tất cả khách hàng
     */
    @Test
    @Order(1)
    @DisplayName("Test lấy danh sách khách hàng")
    void testGetAllKhachHang() {
        // Arrange (Chuẩn bị)
        
        // Act (Thực hiện)
        List<KhachHang> danhSach = khachHangDAO.getDsKhachHang();
        
        // Assert (Kiểm tra kết quả)
        assertNotNull(danhSach, "Danh sách không được null");
        assertTrue(danhSach.size() >= 0, "Danh sách phải có ít nhất 0 phần tử");
        
        System.out.println("✓ Test lấy danh sách: PASSED - Tìm thấy " + danhSach.size() + " khách hàng");
    }
    
    /**
     * Test 2: Kiểm tra thêm khách hàng mới
     */
    @Test
    @Order(2)
    @DisplayName("Test thêm khách hàng mới")
    void testThemKhachHang() {
        // Arrange
        testMaKH = khachHangDAO.phatSinhMaKhachHang();
        KhachHang kh = new KhachHang();
        kh.setMaKhachHang(testMaKH);
        kh.setCCCD(testCCCD);
        kh.setTenKhachHang("Nguyen Van Test");
        kh.setSoDienThoai("0123456789");
        kh.setEmail("test@example.com");
        
        // Act
        boolean result = khachHangDAO.themKhachHang(kh);
        
        // Assert
        assertTrue(result, "Thêm khách hàng phải thành công");
        
        // Verify - Kiểm tra lại trong DB
        KhachHang khTimThay = khachHangDAO.timKhachHangTheoCCCD(testCCCD);
        assertNotNull(khTimThay, "Phải tìm thấy khách hàng vừa thêm");
        assertEquals("Nguyen Van Test", khTimThay.getTenKhachHang(), "Tên phải khớp");
        
        System.out.println("✓ Test thêm khách hàng: PASSED - Mã KH: " + testMaKH);
    }
    
    /**
     * Test 3: Kiểm tra tìm khách hàng theo CCCD
     */
    @Test
    @Order(3)
    @DisplayName("Test tìm khách hàng theo CCCD")
    void testTimKhachHangTheoCCCD() {
        // Act
        KhachHang kh = khachHangDAO.timKhachHangTheoCCCD(testCCCD);
        
        // Assert
        assertNotNull(kh, "Phải tìm thấy khách hàng");
        assertEquals(testCCCD, kh.getCCCD(), "CCCD phải khớp");
        
        System.out.println("✓ Test tìm theo CCCD: PASSED - " + kh.getTenKhachHang());
    }
    
    /**
     * Test 4: Kiểm tra cập nhật khách hàng
     */
    @Test
    @Order(4)
    @DisplayName("Test cập nhật khách hàng")
    void testCapNhatKhachHang() {
        // Arrange
        KhachHang kh = khachHangDAO.timKhachHangTheoCCCD(testCCCD);
        assertNotNull(kh, "Khách hàng phải tồn tại trước khi update");
        
        String tenMoi = "Nguyen Van Test Updated";
        kh.setTenKhachHang(tenMoi);
        
        // Act
        boolean result = khachHangDAO.capNhatKhachHang(kh);
        
        // Assert
        assertTrue(result, "Cập nhật phải thành công");
        
        // Verify
        KhachHang khCapNhat = khachHangDAO.timKhachHangTheoCCCD(testCCCD);
        assertEquals(tenMoi, khCapNhat.getTenKhachHang(), "Tên đã cập nhật phải khớp");
        
        System.out.println("✓ Test cập nhật: PASSED");
    }
    
    /**
     * Test 5: Kiểm tra xóa khách hàng
     */
    @Test
    @Order(5)
    @DisplayName("Test xóa khách hàng")
    void testXoaKhachHang() {
        // Get mã khách hàng từ CCCD
        KhachHang kh = khachHangDAO.timKhachHangTheoCCCD(testCCCD);
        assertNotNull(kh, "Khách hàng phải tồn tại");
        
        // Act
        boolean result = khachHangDAO.xoaKhachHang(kh.getMaKhachHang());
        
        // Assert
        assertTrue(result, "Xóa phải thành công");
        
        // Verify
        KhachHang khXoa = khachHangDAO.timKhachHangTheoCCCD(testCCCD);
        assertNull(khXoa, "Khách hàng đã xóa không được tìm thấy");
        
        System.out.println("✓ Test xóa: PASSED");
    }
    
    /**
     * Test 6: Kiểm tra validate dữ liệu không hợp lệ
     */
    @Test
    @DisplayName("Test validate CCCD null")
    void testValidateCCCDNull() {
        // Act
        KhachHang kh = khachHangDAO.timKhachHangTheoCCCD(null);
        
        // Assert
        assertNull(kh, "CCCD null phải trả về null");
        
        System.out.println("✓ Test validate: PASSED");
    }
}
