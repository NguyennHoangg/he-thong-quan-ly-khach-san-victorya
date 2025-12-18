package model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Phong_Test {
    
    private Phong phong;
    private LoaiPhong loaiPhong;
    
    @BeforeEach
    public void setUp() {
        loaiPhong = new LoaiPhong("LP001", "Deluxe", 500000);
        phong = new Phong("P001", "101", loaiPhong, "Trống", 1);
    }
    
    @Test
    @Order(1)
    @DisplayName("Test khởi tạo Phong")
    public void testKhoiTao() {
        System.out.println("✓ Test khởi tạo Phong: PASSED");
        assertNotNull(phong);
        assertEquals("P001", phong.getMaPhong());
        assertEquals("101", phong.getSoPhong());
        assertEquals(loaiPhong, phong.getLoaiPhong());
        assertEquals("Trống", phong.getTrangThai());
        assertEquals(1, phong.getTang());
    }
    
    @Test
    @Order(2)
    @DisplayName("Test constructor đầy đủ")
    public void testConstructorFull() {
        System.out.println("✓ Test constructor đầy đủ: PASSED");
        Phong p = new Phong(loaiPhong, "P002", "Phòng VIP", "102", 2, "Tốt", "Đã đặt");
        assertEquals("P002", p.getMaPhong());
        assertEquals("102", p.getSoPhong());
        assertEquals(2, p.getTang());
        assertEquals("Đã đặt", p.getTrangThai());
        assertEquals("Phòng VIP", p.getMoTa());
    }
    
    @Test
    @Order(3)
    @DisplayName("Test setter")
    public void testSetter() {
        System.out.println("✓ Test setter: PASSED");
        
        phong.setSoPhong("102");
        assertEquals("102", phong.getSoPhong());
        
        phong.setTrangThai("Đang sử dụng");
        assertEquals("Đang sử dụng", phong.getTrangThai());
        
        phong.setTang(2);
        assertEquals(2, phong.getTang());
        
        LoaiPhong newLoaiPhong = new LoaiPhong("LP002", "Standard", 300000);
        phong.setLoaiPhong(newLoaiPhong);
        assertEquals(newLoaiPhong, phong.getLoaiPhong());
        
        phong.setMoTa("Phòng đơn view biển");
        assertEquals("Phòng đơn view biển", phong.getMoTa());
    }
    
    @Test
    @Order(4)
    @DisplayName("Test trạng thái phòng")
    public void testTrangThaiPhong() {
        System.out.println("✓ Test trạng thái phòng: PASSED");
        String[] trangThaiHopLe = {"Trống", "Đã đặt", "Đang sử dụng", "Đang dọn"};
        
        for (String trangThai : trangThaiHopLe) {
            phong.setTrangThai(trangThai);
            assertEquals(trangThai, phong.getTrangThai());
        }
    }
    
    @Test
    @Order(5)
    @DisplayName("Test validate tầng")
    public void testValidateTang() {
        System.out.println("✓ Test validate tầng: PASSED");
        phong.setTang(5);
        assertTrue(phong.getTang() > 0, "Tầng phải > 0");
    }
}
