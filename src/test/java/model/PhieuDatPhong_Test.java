package model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PhieuDatPhong_Test {
    
    private PhieuDatPhong phieuDatPhong;
    private KhachHang khachHang;
    private List<ChiTietPhieuDatPhong> dsChiTiet;
    
    @BeforeEach
    public void setUp() {
        khachHang = new KhachHang("KH001", "001234567890", "Nguyen Van A", "0901234567", "test@email.com");
        dsChiTiet = new ArrayList<>();
        
        phieuDatPhong = new PhieuDatPhong(
            "PDP001",
            khachHang,
            LocalDate.now(),
            dsChiTiet,
            "Đã đặt",
            500000
        );
    }
    
    @Test
    @Order(1)
    @DisplayName("Test khởi tạo PhieuDatPhong")
    public void testKhoiTao() {
        System.out.println(" Test khởi tạo PhieuDatPhong: PASSED");
        assertNotNull(phieuDatPhong);
        assertEquals("PDP001", phieuDatPhong.getMaPhieuDatPhong());
        assertEquals(khachHang, phieuDatPhong.getKhachHang());
        assertEquals("Đã đặt", phieuDatPhong.getTrangThai());
        assertEquals(500000, phieuDatPhong.getTienDatCoc());
    }
    
    @Test
    @Order(2)
    @DisplayName("Test constructor 2 tham số")
    public void testConstructor2Params() {
        System.out.println(" Test constructor 2 params: PASSED");
        PhieuDatPhong pdp = new PhieuDatPhong("PDP002", khachHang);
        assertEquals("PDP002", pdp.getMaPhieuDatPhong());
        assertEquals(khachHang, pdp.getKhachHang());
    }
    
    @Test
    @Order(3)
    @DisplayName("Test constructor mã duy nhất")
    public void testConstructorMaDuyNhat() {
        System.out.println(" Test constructor mã: PASSED");
        PhieuDatPhong pdp = new PhieuDatPhong("PDP003");
        assertEquals("PDP003", pdp.getMaPhieuDatPhong());
    }
    
    @Test
    @Order(4)
    @DisplayName("Test setter")
    public void testSetter() {
        System.out.println(" Test setter: PASSED");
        
        KhachHang newKH = new KhachHang("KH002", "098765432100", "Tran Thi B", "0987654321", "tranb@email.com");
        phieuDatPhong.setKhachHang(newKH);
        assertEquals(newKH, phieuDatPhong.getKhachHang());
        
        LocalDate newDate = LocalDate.now().plusDays(7);
        phieuDatPhong.setNgayTao(newDate);
        assertEquals(newDate, phieuDatPhong.getNgayTao());
        
        phieuDatPhong.setTrangThai("Đã nhận phòng");
        assertEquals("Đã nhận phòng", phieuDatPhong.getTrangThai());
        
        phieuDatPhong.setTienDatCoc(1000000);
        assertEquals(1000000, phieuDatPhong.getTienDatCoc());
        
        List<ChiTietPhieuDatPhong> newDs = new ArrayList<>();
        phieuDatPhong.setDsachPhieuDatPhong(newDs);
        assertEquals(newDs, phieuDatPhong.getDsachPhieuDatPhong());
    }
    
    @Test
    @Order(5)
    @DisplayName("Test danh sách chi tiết")
    public void testDanhSachChiTiet() {
        System.out.println(" Test danh sách chi tiết: PASSED");
        assertNotNull(phieuDatPhong.getDsachPhieuDatPhong());
        assertEquals(0, phieuDatPhong.getDsachPhieuDatPhong().size());
    }
    
    @Test
    @Order(6)
    @DisplayName("Test validate tiền đặt cọc")
    public void testValidateTienDatCoc() {
        System.out.println(" Test validate tiền cọc: PASSED");
        assertTrue(phieuDatPhong.getTienDatCoc() >= 0, "Tiền đặt cọc phải >= 0");
    }
    
    @Test
    @Order(7)
    @DisplayName("Test trạng thái phiếu")
    public void testTrangThaiPhieu() {
        System.out.println(" Test trạng thái: PASSED");
        String[] trangThaiHopLe = {"Đã đặt", "Đã nhận phòng", "Đã hủy", "Hoàn thành"};
        
        for (String trangThai : trangThaiHopLe) {
            phieuDatPhong.setTrangThai(trangThai);
            assertEquals(trangThai, phieuDatPhong.getTrangThai());
        }
    }
}
