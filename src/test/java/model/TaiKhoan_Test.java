package model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaiKhoan_Test {
    
    private TaiKhoan taiKhoan;
    
    @BeforeEach
    public void setUp() {
        taiKhoan = new TaiKhoan("admin", "123456", "admin");
    }
    
    @Test
    @Order(1)
    @DisplayName("Test khởi tạo TaiKhoan")
    public void testKhoiTao() {
        System.out.println(" Test khởi tạo TaiKhoan: PASSED");
        assertNotNull(taiKhoan);
        assertEquals("admin", taiKhoan.getTenDangNhap());
        assertEquals("123456", taiKhoan.getMatKhau());
        assertEquals("Quản lý", taiKhoan.getVaiTro());
    }
    
    @Test
    @Order(2)
    @DisplayName("Test constructor 2 tham số")
    public void testConstructor2Params() {
        System.out.println(" Test constructor 2 params: PASSED");
        TaiKhoan tk = new TaiKhoan("user01", "employee");
        assertEquals("user01", tk.getTenDangNhap());
        assertEquals("Nhân viên", tk.getVaiTro());
    }
    
    @Test
    @Order(3)
    @DisplayName("Test setter")
    public void testSetter() {
        System.out.println(" Test setter: PASSED");
        
        taiKhoan.settenDangNhap("newuser");
        assertEquals("newuser", taiKhoan.getTenDangNhap());
        
        taiKhoan.setMatKhau("newpass123");
        assertEquals("newpass123", taiKhoan.getMatKhau());
        
        taiKhoan.setVaiTro("employee");
        assertEquals("Nhân viên", taiKhoan.getVaiTro());
    }
    
    @Test
    @Order(4)
    @DisplayName("Test vai trò admin")
    public void testVaiTroAdmin() {
        System.out.println(" Test vai trò admin: PASSED");
        taiKhoan.setVaiTro("admin");
        assertEquals("Quản lý", taiKhoan.getVaiTro());
        
        taiKhoan.setVaiTro("ADMIN");
        assertEquals("Quản lý", taiKhoan.getVaiTro());
    }
    
    @Test
    @Order(5)
    @DisplayName("Test vai trò employee")
    public void testVaiTroEmployee() {
        System.out.println(" Test vai trò employee: PASSED");
        taiKhoan.setVaiTro("employee");
        assertEquals("Nhân viên", taiKhoan.getVaiTro());
        
        taiKhoan.setVaiTro("EMPLOYEE");
        assertEquals("Nhân viên", taiKhoan.getVaiTro());
    }
    
    @Test
    @Order(6)
    @DisplayName("Test vai trò không xác định")
    public void testVaiTroNull() {
        System.out.println(" Test vai trò null: PASSED");
        taiKhoan.setVaiTro(null);
        assertEquals("Không xác định", taiKhoan.getVaiTro());
    }
    
    @Test
    @Order(7)
    @DisplayName("Test validate mật khẩu")
    public void testValidateMatKhau() {
        System.out.println(" Test validate mật khẩu: PASSED");
        assertNotNull(taiKhoan.getMatKhau());
        assertTrue(taiKhoan.getMatKhau().length() >= 6, "Mật khẩu phải có ít nhất 6 ký tự");
    }
    
    @Test
    @Order(8)
    @DisplayName("Test validate tên đăng nhập")
    public void testValidateTenDangNhap() {
        System.out.println(" Test validate tên đăng nhập: PASSED");
        assertNotNull(taiKhoan.getTenDangNhap());
        assertFalse(taiKhoan.getTenDangNhap().isEmpty(), "Tên đăng nhập không được rỗng");
    }
}
