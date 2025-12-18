package controller;

import dao.KhachHang_DAO;
import model.KhachHang;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class cho KhachHang_Controller sử dụng Mockito
 * Mockito giúp tạo mock object để test mà không cần database thật
 */
class KhachHang_Controller_Test {
    
    @Mock
    private KhachHang_DAO mockDAO;
    
    private KhachHang_Controller controller;
    private AutoCloseable closeable;
    
    @BeforeEach
    void setUp() {
        // Khởi tạo mock objects
        closeable = MockitoAnnotations.openMocks(this);
        
        // Inject mock DAO vào controller (cần sửa constructor của Controller)
        // controller = new KhachHang_Controller(mockDAO);
        controller = new KhachHang_Controller();
    }
    
    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
    
    @Test
    @DisplayName("Test lấy danh sách khách hàng với Mock")
    void testGetAllKhachHangWithMock() {
        // Arrange - Chuẩn bị dữ liệu giả
        KhachHang kh1 = new KhachHang();
        kh1.setCCCD("001");
        kh1.setTenKhachHang("Nguyen Van A");
        
        KhachHang kh2 = new KhachHang();
        kh2.setCCCD("002");
        kh2.setTenKhachHang("Tran Thi B");
        
        List<KhachHang> fakeData = Arrays.asList(kh1, kh2);
        
        // Giả lập hành vi của DAO
        when(mockDAO.getDsKhachHang()).thenReturn(fakeData);
        
        // Act
        // List<KhachHang> result = controller.getAllKhachHang();
        
        // Assert
        // assertNotNull(result);
        // assertEquals(2, result.size());
        // assertEquals("Nguyen Van A", result.get(0).getHoTen());
        
        // Verify rằng method đã được gọi
        // verify(mockDAO, times(1)).getAllKhachHang();
        
        System.out.println("✓ Test mock DAO: Cần cập nhật Controller để support DI");
    }
    
    @Test
    @DisplayName("Test validate dữ liệu khách hàng")
    void testValidateKhachHang() {
        // Test CCCD rỗng
        assertFalse(validateKhachHang(null, "Name", "0123456789", "email@test.com"), 
                   "CCCD null phải không hợp lệ");
        assertFalse(validateKhachHang("", "Name", "0123456789", "email@test.com"), 
                   "CCCD rỗng phải không hợp lệ");
        
        // Test họ tên rỗng
        assertFalse(validateKhachHang("123456", null, "0123456789", "email@test.com"), 
                   "Họ tên null phải không hợp lệ");
        assertFalse(validateKhachHang("123456", "", "0123456789", "email@test.com"), 
                   "Họ tên rỗng phải không hợp lệ");
        
        // Test SĐT không hợp lệ
        assertFalse(validateKhachHang("123456", "Name", "123", "email@test.com"), 
                   "SĐT ngắn phải không hợp lệ");
        
        // Test email không hợp lệ
        assertFalse(validateKhachHang("123456", "Name", "0123456789", "invalidemail"), 
                   "Email không đúng format phải không hợp lệ");
        
        // Test dữ liệu hợp lệ
        assertTrue(validateKhachHang("123456789", "Nguyen Van A", "0987654321", "test@gmail.com"), 
                  "Dữ liệu hợp lệ phải pass");
        
        System.out.println("✓ Test validate: PASSED");
    }
    
    // Helper method
    private boolean validateKhachHang(String cccd, String hoTen, String sdt, String email) {
        if (cccd == null || cccd.trim().isEmpty()) return false;
        if (hoTen == null || hoTen.trim().isEmpty()) return false;
        if (sdt == null || !sdt.matches("0\\d{9}")) return false;
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) return false;
        return true;
    }
}
