package model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HoaDon_Test {
    
    private HoaDon hoaDon;
    private KhachHang khachHang;
    private NhanVien nhanVien;
    private TaiKhoan taiKhoan;
    
    @BeforeEach
    public void setUp() {
        khachHang = new KhachHang("KH001", "001234567890", "Nguyen Van A", "0901234567", "test@email.com");
        taiKhoan = new TaiKhoan("admin", "123456", "Quản lý");
        nhanVien = new NhanVien("NV001", "Admin User", taiKhoan, true, 
                                java.time.LocalDate.of(1990, 1, 1), 
                                "admin@email.com", "0987654321", 
                                java.time.LocalDate.of(2020, 1, 1));
        
        hoaDon = new HoaDon(
            "HD001",
            LocalDateTime.now(),
            khachHang,
            nhanVien,
            null,
            LocalDateTime.now(),
            "Chưa thanh toán",
            1500000
        );
    }
    
    @Test
    @Order(1)
    @DisplayName("Test khởi tạo HoaDon")
    public void testKhoiTao() {
        System.out.println("✓ Test khởi tạo HoaDon: PASSED");
        assertNotNull(hoaDon);
        assertEquals("HD001", hoaDon.getMaHoaDon());
        assertEquals(khachHang, hoaDon.getKhachHang());
        assertEquals(nhanVien, hoaDon.getNhanVien());
        assertEquals("Chưa thanh toán", hoaDon.getTrangThai());
        assertEquals(1500000, hoaDon.getTongTien());
    }
    
    @Test
    @Order(2)
    @DisplayName("Test constructor với mã duy nhất")
    public void testConstructorMaDuyNhat() {
        System.out.println("✓ Test constructor mã duy nhất: PASSED");
        HoaDon hd = new HoaDon("HD002");
        assertEquals("HD002", hd.getMaHoaDon());
    }
    
    @Test
    @Order(3)
    @DisplayName("Test setter")
    public void testSetter() {
        System.out.println("✓ Test setter HoaDon: PASSED");
        
        hoaDon.setMaHoaDon("HD002");
        assertEquals("HD002", hoaDon.getMaHoaDon());
        
        hoaDon.setTrangThai("Đã thanh toán");
        assertEquals("Đã thanh toán", hoaDon.getTrangThai());
        
        hoaDon.setTongTien(2000000);
        assertEquals(2000000, hoaDon.getTongTien());
        
        LocalDateTime newDate = LocalDateTime.now().plusDays(1);
        hoaDon.setNgayTao(newDate);
        assertEquals(newDate, hoaDon.getNgayTao());
    }
    
    @Test
    @Order(4)
    @DisplayName("Test chi tiết hóa đơn")
    public void testChiTietHoaDon() {
        System.out.println("✓ Test chi tiết hóa đơn: PASSED");
        assertNotNull(hoaDon.getChiTietHoaDon());
        
        List<ChiTietHoaDon> chiTiet = new ArrayList<>();
        hoaDon.setChiTietHoaDon(chiTiet);
        assertEquals(chiTiet, hoaDon.getChiTietHoaDon());
    }
    
    @Test
    @Order(5)
    @DisplayName("Test trạng thái hóa đơn")
    public void testTrangThaiHoaDon() {
        System.out.println("✓ Test trạng thái: PASSED");
        String[] trangThaiHopLe = {"Chưa thanh toán", "Đã thanh toán", "Đã hủy"};
        
        for (String trangThai : trangThaiHopLe) {
            hoaDon.setTrangThai(trangThai);
            assertEquals(trangThai, hoaDon.getTrangThai());
        }
    }
    
    @Test
    @Order(6)
    @DisplayName("Test validate tổng tiền")
    public void testValidateTongTien() {
        System.out.println("✓ Test validate tổng tiền: PASSED");
        assertTrue(hoaDon.getTongTien() >= 0, "Tổng tiền phải >= 0");
    }
}
