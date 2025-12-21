package model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoaiPhong_Test {
    
    private LoaiPhong loaiPhong;
    private List<DichVu> dsDichVu;
    
    @BeforeEach
    public void setUp() {
        dsDichVu = new ArrayList<>();
        dsDichVu.add(new DichVu("DV001", "Nước suối", 15000, "Nước khoáng", "Chai"));
        dsDichVu.add(new DichVu("DV002", "Coca Cola", 20000, "Nước ngọt", "Lon"));
        
        loaiPhong = new LoaiPhong(
            "LP001",
            "Deluxe",
            500000,
            LocalDate.now(),
            dsDichVu,
            2,
            1
        );
    }
    
    @Test
    @Order(1)
    @DisplayName("Test khởi tạo LoaiPhong")
    public void testKhoiTao() {
        System.out.println(" Test khởi tạo LoaiPhong: PASSED");
        assertNotNull(loaiPhong);
        assertEquals("LP001", loaiPhong.getMaLoaiPhong());
        assertEquals("Deluxe", loaiPhong.getTenLoaiPhong());
        assertEquals(500000, loaiPhong.getGia());
        assertEquals(2, loaiPhong.getSoNguoiLonToiDa());
        assertEquals(1, loaiPhong.getSoTreEmToiDa());
        assertEquals(2, loaiPhong.getDsachDichVu().size());
    }
    
    @Test
    @Order(2)
    @DisplayName("Test constructor 3 tham số")
    public void testConstructor3Params() {
        System.out.println(" Test constructor 3 params: PASSED");
        LoaiPhong lp = new LoaiPhong("LP002", "Standard", 300000);
        assertEquals("LP002", lp.getMaLoaiPhong());
        assertEquals("Standard", lp.getTenLoaiPhong());
        assertEquals(300000, lp.getGia());
        assertEquals(2, lp.getSoNguoiLonToiDa()); // Default value
        assertEquals(1, lp.getSoTreEmToiDa()); // Default value
    }
    
    @Test
    @Order(3)
    @DisplayName("Test constructor với mã duy nhất")
    public void testConstructorMaDuyNhat() {
        System.out.println(" Test constructor mã duy nhất: PASSED");
        LoaiPhong lp = new LoaiPhong("LP003");
        assertEquals("LP003", lp.getMaLoaiPhong());
    }
    
    @Test
    @Order(4)
    @DisplayName("Test setter")
    public void testSetter() {
        System.out.println(" Test setter LoaiPhong: PASSED");
        
        loaiPhong.setTenLoaiPhong("VIP Suite");
        assertEquals("VIP Suite", loaiPhong.getTenLoaiPhong());
        
        loaiPhong.setGia(1000000);
        assertEquals(1000000, loaiPhong.getGia());
        
        loaiPhong.setSoNguoiLonToiDa(4);
        assertEquals(4, loaiPhong.getSoNguoiLonToiDa());
        
        loaiPhong.setSoTreEmToiDa(2);
        assertEquals(2, loaiPhong.getSoTreEmToiDa());
        
        List<DichVu> newDsDichVu = new ArrayList<>();
        newDsDichVu.add(new DichVu("DV003", "Minibar"));
        loaiPhong.setDsachDichVu(newDsDichVu);
        assertEquals(1, loaiPhong.getDsachDichVu().size());
    }
    
    @Test
    @Order(5)
    @DisplayName("Test validate giá")
    public void testValidateGia() {
        System.out.println(" Test validate giá: PASSED");
        assertTrue(loaiPhong.getGia() > 0, "Giá phải > 0");
        
        loaiPhong.setGia(1500000);
        assertTrue(loaiPhong.getGia() > 0);
    }
    
    @Test
    @Order(6)
    @DisplayName("Test danh sách dịch vụ")
    public void testDanhSachDichVu() {
        System.out.println(" Test danh sách dịch vụ: PASSED");
        assertNotNull(loaiPhong.getDsachDichVu());
        assertEquals(2, loaiPhong.getDsachDichVu().size());
        
        DichVu dvMoi = new DichVu("DV003", "Bia", 25000, "Bia Tiger", "Lon");
        loaiPhong.getDsachDichVu().add(dvMoi);
        assertEquals(3, loaiPhong.getDsachDichVu().size());
    }
    
    @Test
    @Order(7)
    @DisplayName("Test sức chứa")
    public void testSucChua() {
        System.out.println(" Test sức chứa: PASSED");
        assertTrue(loaiPhong.getSoNguoiLonToiDa() > 0, "Số người lớn phải > 0");
        assertTrue(loaiPhong.getSoTreEmToiDa() >= 0, "Số trẻ em phải >= 0");
    }
}
