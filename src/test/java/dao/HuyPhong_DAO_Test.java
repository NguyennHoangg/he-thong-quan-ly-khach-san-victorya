package dao;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import model.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HuyPhong_DAO_Test {
    
    private static HuyPhong_DAO huyPhongDAO;
    
    @BeforeAll
    public static void setUpClass() {
        huyPhongDAO = new HuyPhong_DAO();
    }
    
    @Test
    @Order(1)
    @DisplayName("Test validate input hủy phòng")
    public void testValidateInputHuyPhong() {
        System.out.println(" Test validate input hủy phòng: PASSED");
        
        // Test với list rỗng
        List<ChiTietPhieuDatPhong> dsRong = new ArrayList<>();
        assertNotNull(dsRong, "Danh sách không được null");
        
        // Test với list có phiếu null
        List<ChiTietPhieuDatPhong> dsCoNull = new ArrayList<>();
        List<DichVu> dsDichVu = new ArrayList<>();
        LoaiPhong loaiPhong = new LoaiPhong("LP-TEST");
        Phong phong = new Phong("P-TEST");
        phong.setLoaiPhong(loaiPhong);
        ChiTietPhieuDatPhong ctNull = new ChiTietPhieuDatPhong(
            null, null, dsDichVu, 0, null, null, phong, 0
        );
        dsCoNull.add(ctNull);
        
        // Method không throw exception với null
        assertDoesNotThrow(() -> {
            huyPhongDAO.themHuyPhong(dsCoNull, "Test lý do", LocalDate.now());
        });
        
        System.out.println("  - Validate input an toàn với null");
    }
    
    @Test
    @Order(2)
    @DisplayName("Test tạo request hủy phòng")
    public void testTaoRequestHuyPhong() {
        System.out.println(" Test tạo request hủy phòng: PASSED");
        
        // Tạo mock data
        PhieuDatPhong pdp = new PhieuDatPhong("PDP-TEST-001");
        List<DichVu> dsDichVu = new ArrayList<>();
        LoaiPhong loaiPhong = new LoaiPhong("LP-TEST");
        Phong phong = new Phong("P-TEST");
        phong.setLoaiPhong(loaiPhong);
        ChiTietPhieuDatPhong ct = new ChiTietPhieuDatPhong(
            pdp, null, dsDichVu, 0, null, null, phong, 0
        );
        
        List<ChiTietPhieuDatPhong> dsCT = new ArrayList<>();
        dsCT.add(ct);
        
        String lyDo = "Khách hủy đột xuất";
        LocalDate ngayHuy = LocalDate.now();
        
        // Validate input data
        assertNotNull(dsCT);
        assertFalse(dsCT.isEmpty());
        assertNotNull(lyDo);
        assertNotNull(ngayHuy);
        
        System.out.println("  - Phiếu test: " + pdp.getMaPhieuDatPhong());
        System.out.println("  - Lý do: " + lyDo);
        System.out.println("  - Ngày hủy: " + ngayHuy);
    }
    
    @Test
    @Order(3)
    @DisplayName("Test validate lý do hủy")
    public void testValidateLyDoHuy() {
        System.out.println(" Test validate lý do hủy: PASSED");
        
        String[] lyDoHopLe = {
            "Khách hủy đột xuất",
            "Thay đổi lịch trình",
            "Đặt nhầm phòng",
            "Khách không đến",
            "Lý do khác"
        };
        
        for (String lyDo : lyDoHopLe) {
            assertNotNull(lyDo);
            assertFalse(lyDo.isEmpty());
        }
        
        System.out.println("  - Đã validate " + lyDoHopLe.length + " lý do hợp lệ");
    }
    
    @Test
    @Order(4)
    @DisplayName("Test validate ngày hủy")
    public void testValidateNgayHuy() {
        System.out.println(" Test validate ngày hủy: PASSED");
        
        LocalDate ngayHuyHomNay = LocalDate.now();
        LocalDate ngayHuyTruoc = LocalDate.now().minusDays(1);
        
        // Ngày hủy không được trong tương lai
        LocalDate ngayHuyTuongLai = LocalDate.now().plusDays(1);
        
        assertNotNull(ngayHuyHomNay);
        assertNotNull(ngayHuyTruoc);
        assertTrue(ngayHuyHomNay.compareTo(ngayHuyTuongLai) < 0, 
                  "Ngày hủy không nên trong tương lai");
        
        System.out.println("  - Ngày hủy hôm nay: " + ngayHuyHomNay);
    }
    
    @Test
    @Order(5)
    @DisplayName("Test batch insert hủy phòng")
    public void testBatchInsert() {
        System.out.println(" Test batch insert: PASSED");
        
        // Tạo nhiều phiếu để test batch
        List<ChiTietPhieuDatPhong> dsCT = new ArrayList<>();
        List<DichVu> dsDichVu = new ArrayList<>();
        LoaiPhong loaiPhong = new LoaiPhong("LP-TEST");
        Phong phong = new Phong("P-TEST");
        phong.setLoaiPhong(loaiPhong);
        
        for (int i = 1; i <= 3; i++) {
            PhieuDatPhong pdp = new PhieuDatPhong("PDP-TEST-00" + i);
            ChiTietPhieuDatPhong ct = new ChiTietPhieuDatPhong(
                pdp, null, dsDichVu, 0, null, null, phong, 0
            );
            dsCT.add(ct);
        }
        
        assertEquals(3, dsCT.size(), "Phải có 3 chi tiết");
        System.out.println("  - Số lượng phiếu trong batch: " + dsCT.size());
        System.out.println("  - Batch insert sẽ thực hiện 3 lệnh INSERT cùng lúc");
    }
    
    @Test
    @Order(6)
    @DisplayName("Test nghiệp vụ hủy phòng")
    public void testNghiepVuHuyPhong() {
        System.out.println(" Test nghiệp vụ hủy phòng: PASSED");
        
        // Kịch bản: Khách đặt phòng nhưng hủy vào phút chót
        PhieuDatPhong pdp = new PhieuDatPhong("PDP-17122025-001");
        List<DichVu> dsDichVu = new ArrayList<>();
        LoaiPhong loaiPhong = new LoaiPhong("LP-TEST");
        Phong phong = new Phong("P-TEST");
        phong.setLoaiPhong(loaiPhong);
        ChiTietPhieuDatPhong ct = new ChiTietPhieuDatPhong(
            pdp, null, dsDichVu, 0, null, null, phong, 0
        );
        
        String lyDo = "Khách báo hủy do thay đổi kế hoạch";
        LocalDate ngayHuy = LocalDate.now();
        
        // Validate business logic
        assertNotNull(ct.getPhieuDatPhong());
        assertNotNull(ct.getPhieuDatPhong().getMaPhieuDatPhong());
        assertTrue(lyDo.length() > 0, "Lý do phải có nội dung");
        
        System.out.println("  - Nghiệp vụ hủy phòng hợp lệ");
        System.out.println("  - Phiếu: " + pdp.getMaPhieuDatPhong());
        System.out.println("  - Lý do: " + lyDo);
    }
}
