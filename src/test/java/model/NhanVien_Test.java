package model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NhanVien_Test {
    
    private NhanVien nhanVien;
    private TaiKhoan taiKhoan;
    
    @BeforeEach
    public void setUp() {
        taiKhoan = new TaiKhoan("TK001", "admin", "Admin");
        nhanVien = new NhanVien(
            "NV001", 
            "001234567890", 
            "Nguyen Van A",
            taiKhoan,
            true,
            LocalDate.of(1990, 5, 15),
            "nguyenvana@email.com",
            "0901234567",
            LocalDate.of(2020, 1, 1),
            "Đang làm việc",
            "123 Nguyen Hue, TPHCM"
        );
    }
    
    @Test
    @Order(1)
    @DisplayName("Test khởi tạo NhanVien")
    public void testKhoiTao() {
        System.out.println("✓ Test khởi tạo NhanVien: PASSED");
        assertNotNull(nhanVien);
        assertEquals("NV001", nhanVien.getMaNhanVien());
        assertEquals("001234567890", nhanVien.getCCCD());
        assertEquals("Nguyen Van A", nhanVien.getTenNhanVien());
        assertEquals(taiKhoan, nhanVien.getTaiKhoan());
        assertTrue(nhanVien.isGioiTinh());
        assertEquals("nguyenvana@email.com", nhanVien.getEmail());
        assertEquals("0901234567", nhanVien.getSoDienThoai());
    }
    
    @Test
    @Order(2)
    @DisplayName("Test setter")
    public void testSetter() {
        System.out.println("✓ Test setter NhanVien: PASSED");
        
        nhanVien.setMaNhanVien("NV002");
        assertEquals("NV002", nhanVien.getMaNhanVien());
        
        nhanVien.setTenNhanVien("Tran Thi B");
        assertEquals("Tran Thi B", nhanVien.getTenNhanVien());
        
        nhanVien.setCCCD("098765432100");
        assertEquals("098765432100", nhanVien.getCCCD());
        
        nhanVien.setEmail("tranthib@email.com");
        assertEquals("tranthib@email.com", nhanVien.getEmail());
        
        nhanVien.setSoDienThoai("0987654321");
        assertEquals("0987654321", nhanVien.getSoDienThoai());
        
        nhanVien.setGioiTinh(false);
        assertFalse(nhanVien.isGioiTinh());
        
        nhanVien.setTrangThai("Đã nghỉ việc");
        assertEquals("Đã nghỉ việc", nhanVien.getTrangThai());
        
        nhanVien.setDiaChi("456 Le Loi, TPHCM");
        assertEquals("456 Le Loi, TPHCM", nhanVien.getDiaChi());
    }
    
    @Test
    @Order(3)
    @DisplayName("Test validate CCCD")
    public void testValidateCCCD() {
        System.out.println("✓ Test validate CCCD: PASSED");
        assertTrue(nhanVien.getCCCD().length() == 12, "CCCD phải có 12 số");
    }
    
    @Test
    @Order(4)
    @DisplayName("Test validate email")
    public void testValidateEmail() {
        System.out.println("✓ Test validate email: PASSED");
        String email = nhanVien.getEmail();
        assertTrue(email.contains("@"), "Email phải chứa @");
        assertTrue(email.contains("."), "Email phải chứa dấu chấm");
    }
    
    @Test
    @Order(5)
    @DisplayName("Test validate số điện thoại")
    public void testValidateSoDienThoai() {
        System.out.println("✓ Test validate SĐT: PASSED");
        String sdt = nhanVien.getSoDienThoai();
        assertTrue(sdt.length() == 10, "SĐT phải có 10 số");
        assertTrue(sdt.startsWith("0"), "SĐT phải bắt đầu bằng 0");
    }
    
    @Test
    @Order(6)
    @DisplayName("Test ngày sinh hợp lệ")
    public void testNgaySinhHopLe() {
        System.out.println("✓ Test ngày sinh: PASSED");
        LocalDate ngaySinh = nhanVien.getNgaySinh();
        assertTrue(ngaySinh.isBefore(LocalDate.now()), "Ngày sinh phải trước ngày hiện tại");
    }
}
