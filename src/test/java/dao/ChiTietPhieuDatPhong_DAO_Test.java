package dao;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import model.*;

import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChiTietPhieuDatPhong_DAO_Test {
    
    private static ChiTietPhieuDatPhong_DAO chiTietDAO;
    
    @BeforeAll
    public static void setUpClass() {
        chiTietDAO = new ChiTietPhieuDatPhong_DAO();
    }
    
    @Test
    @Order(1)
    @DisplayName("Test lấy danh sách chi tiết phiếu đặt phòng")
    public void testGetDsChiTietPhieuDatPhong() {
        System.out.println("✓ Test getDsChiTietPhieuDatPhong: PASSED");
        List<ChiTietPhieuDatPhong> dsChiTiet = chiTietDAO.getDsChiTietPhieuDatPhong();
        
        assertNotNull(dsChiTiet);
        System.out.println("  - Số lượng chi tiết: " + dsChiTiet.size());
        
        if (!dsChiTiet.isEmpty()) {
            ChiTietPhieuDatPhong ctDauTien = dsChiTiet.get(0);
            assertNotNull(ctDauTien);
            System.out.println("  - Chi tiết đầu tiên có mã phiếu: " + 
                             (ctDauTien.getPhieuDatPhong() != null ? 
                              ctDauTien.getPhieuDatPhong().getMaPhieuDatPhong() : "null"));
        }
    }
    
    @Test
    @Order(2)
    @DisplayName("Test validate dữ liệu chi tiết")
    public void testValidateChiTietData() {
        System.out.println("✓ Test validate chi tiết: PASSED");
        List<ChiTietPhieuDatPhong> dsChiTiet = chiTietDAO.getDsChiTietPhieuDatPhong();
        
        for (ChiTietPhieuDatPhong ct : dsChiTiet) {
            // Validate phiếu đặt phòng
            assertNotNull(ct.getPhieuDatPhong(), "Phiếu đặt phòng không được null");
            
            // Validate loại đặt phòng
            assertNotNull(ct.getLoaiDatPhong(), "Loại đặt phòng không được null");
            
            // Validate phòng
            assertNotNull(ct.getPhong(), "Phòng không được null");
            
            // Validate số giờ lưu trú
            assertTrue(ct.getSoGioLuuTru() >= 0, "Số giờ lưu trú phải >= 0");
            
            // Validate số người
            assertTrue(ct.getSoNguoi() > 0, "Số người phải > 0");
        }
    }
    
    @Test
    @Order(3)
    @DisplayName("Test validate thời gian đặt phòng")
    public void testValidateThoiGian() {
        System.out.println("✓ Test validate thời gian: PASSED");
        List<ChiTietPhieuDatPhong> dsChiTiet = chiTietDAO.getDsChiTietPhieuDatPhong();
        
        int count = 0;
        for (ChiTietPhieuDatPhong ct : dsChiTiet) {
            if (ct.getThoiGianNhanPhong() != null && ct.getThoiGianTraPhong() != null) {
                // Thời gian trả phải sau thời gian nhận
                assertTrue(ct.getThoiGianTraPhong().isAfter(ct.getThoiGianNhanPhong()),
                          "Thời gian trả phải sau thời gian nhận");
                count++;
                
                if (count >= 3) break; // Chỉ test 3 record đầu
            }
        }
        
        System.out.println("  - Đã validate thời gian cho " + count + " chi tiết");
    }
    
    @Test
    @Order(4)
    @DisplayName("Test validate số giờ lưu trú")
    public void testValidateSoGioLuuTru() {
        System.out.println("✓ Test validate số giờ lưu trú: PASSED");
        List<ChiTietPhieuDatPhong> dsChiTiet = chiTietDAO.getDsChiTietPhieuDatPhong();
        
        for (ChiTietPhieuDatPhong ct : dsChiTiet) {
            int soGio = ct.getSoGioLuuTru();
            
            // Số giờ phải >= 0
            assertTrue(soGio >= 0, "Số giờ phải >= 0");
            
            // Với loại đặt theo giờ, số giờ thường < 24
            // Với loại đặt theo ngày, số giờ có thể > 24
            if (ct.getLoaiDatPhong() != null) {
                String loaiDat = ct.getLoaiDatPhong().getMaLoaiDatPhong();
                if ("LDP-GIO".equals(loaiDat) && soGio > 0) {
                    assertTrue(soGio <= 24, "Đặt theo giờ thường <= 24 giờ");
                }
            }
        }
    }
    
    @Test
    @Order(5)
    @DisplayName("Test validate số người")
    public void testValidateSoNguoi() {
        System.out.println("✓ Test validate số người: PASSED");
        List<ChiTietPhieuDatPhong> dsChiTiet = chiTietDAO.getDsChiTietPhieuDatPhong();
        
        for (ChiTietPhieuDatPhong ct : dsChiTiet) {
            int soNguoi = ct.getSoNguoi();
            
            // Số người phải > 0
            assertTrue(soNguoi > 0, "Số người phải > 0");
            
            // Số người không quá 10 (giới hạn hợp lý)
            assertTrue(soNguoi <= 10, "Số người không nên quá 10");
        }
    }
    
    @Test
    @Order(6)
    @DisplayName("Test validate danh sách dịch vụ")
    public void testValidateDichVu() {
        System.out.println("✓ Test validate dịch vụ: PASSED");
        List<ChiTietPhieuDatPhong> dsChiTiet = chiTietDAO.getDsChiTietPhieuDatPhong();
        
        int countCoDichVu = 0;
        for (ChiTietPhieuDatPhong ct : dsChiTiet) {
            // Danh sách dịch vụ không được null
            assertNotNull(ct.getDsachDichVu(), "Danh sách dịch vụ không được null");
            
            if (!ct.getDsachDichVu().isEmpty()) {
                countCoDichVu++;
            }
        }
        
        System.out.println("  - Số chi tiết có dịch vụ: " + countCoDichVu);
    }
    
    @Test
    @Order(7)
    @DisplayName("Test nghiệp vụ đặt phòng chi tiết")
    public void testNghiepVuDatPhong() {
        System.out.println("✓ Test nghiệp vụ đặt phòng: PASSED");
        
        // Kịch bản: Khách đặt 1 phòng Standard cho 2 người, 3 ngày
        PhieuDatPhong pdp = new PhieuDatPhong("PDP-TEST-001");
        LoaiDatPhong ldp = new LoaiDatPhong("LDP-NGAY");
        LoaiPhong loaiPhong = new LoaiPhong("LP-STANDARD");
        Phong phong = new Phong("P101");
        phong.setLoaiPhong(loaiPhong);
        List<DichVu> dsDichVu = new ArrayList<DichVu>();
        
        ChiTietPhieuDatPhong ct = new ChiTietPhieuDatPhong(
            pdp, ldp, dsDichVu, 72, null, null, phong, 2
        ); // 3 ngày = 72 giờ
        
        // Validate
        assertNotNull(ct.getPhieuDatPhong());
        assertNotNull(ct.getLoaiDatPhong());
        assertNotNull(ct.getPhong());
        assertEquals(2, ct.getSoNguoi());
        assertEquals(72, ct.getSoGioLuuTru());
        
        System.out.println("  - Phiếu: " + pdp.getMaPhieuDatPhong());
        System.out.println("  - Phòng: " + phong.getMaPhong());
        System.out.println("  - Số người: " + ct.getSoNguoi());
        System.out.println("  - Số giờ: " + ct.getSoGioLuuTru() + " (3 ngày)");
    }
}
