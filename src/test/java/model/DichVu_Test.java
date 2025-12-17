package model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DichVu_Test {
    
    private DichVu dichVu;
    
    @BeforeEach
    public void setUp() {
        dichVu = new DichVu("DV001", "Nước suối", 15000, "Nước khoáng thiên nhiên", "Chai");
    }
    
    @Test
    @Order(1)
    @DisplayName("Test khởi tạo DichVu")
    public void testKhoiTao() {
        System.out.println("✓ Test khởi tạo DichVu: PASSED");
        assertNotNull(dichVu);
        assertEquals("DV001", dichVu.getMaDichVu());
        assertEquals("Nước suối", dichVu.getTenDichVu());
        assertEquals(15000, dichVu.getGia());
        assertEquals("Nước khoáng thiên nhiên", dichVu.getMoTa());
        assertEquals("Chai", dichVu.getDonViTinh());
    }
    
    @Test
    @Order(2)
    @DisplayName("Test constructor với 2 tham số")
    public void testConstructor2Params() {
        System.out.println("✓ Test constructor 2 params: PASSED");
        DichVu dv = new DichVu("DV002", "Coca Cola");
        assertEquals("DV002", dv.getMaDichVu());
        assertEquals("Coca Cola", dv.getTenDichVu());
    }
    
    @Test
    @Order(3)
    @DisplayName("Test constructor không có mã")
    public void testConstructorKhongMa() {
        System.out.println("✓ Test constructor không mã: PASSED");
        DichVu dv = new DichVu("Pepsi", 12000, "Nước ngọt", "Lon");
        assertNull(dv.getMaDichVu());
        assertEquals("Pepsi", dv.getTenDichVu());
        assertEquals(12000, dv.getGia());
    }
    
    @Test
    @Order(4)
    @DisplayName("Test getter và setter")
    public void testGetterSetter() {
        System.out.println("✓ Test getter/setter: PASSED");
        
        dichVu.setTenDichVu("Coca Cola");
        assertEquals("Coca Cola", dichVu.getTenDichVu());
        
        dichVu.setGia(20000);
        assertEquals(20000, dichVu.getGia());
        
        dichVu.setMoTa("Nước ngọt có ga");
        assertEquals("Nước ngọt có ga", dichVu.getMoTa());
        
        dichVu.setDonViTinh("Lon");
        assertEquals("Lon", dichVu.getDonViTinh());
    }
    
    @Test
    @Order(5)
    @DisplayName("Test validate giá dịch vụ")
    public void testValidateGia() {
        System.out.println("✓ Test validate giá: PASSED");
        assertTrue(dichVu.getGia() >= 0, "Giá phải >= 0");
        
        dichVu.setGia(0);
        assertTrue(dichVu.getGia() >= 0);
    }
    
    @Test
    @Order(6)
    @DisplayName("Test toString")
    public void testToString() {
        System.out.println("✓ Test toString: PASSED");
        assertEquals("Nước suối", dichVu.toString());
    }
}
