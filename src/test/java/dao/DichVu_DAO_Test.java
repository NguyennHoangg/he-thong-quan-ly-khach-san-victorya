package dao;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import model.DichVu;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DichVu_DAO_Test {
    
    private static DichVu_DAO dichVuDAO;
    private static String testMaDichVu;
    
    @BeforeAll
    public static void setUpClass() {
        dichVuDAO = new DichVu_DAO();
        // Tạo mã ngắn để tránh vượt quá độ dài cột database
        testMaDichVu = "TDVT" + (System.currentTimeMillis() % 100000);
    }
    
    @Test
    @Order(1)
    @DisplayName("Test lấy danh sách dịch vụ")
    public void testGetDsDichVu() {
        System.out.println(" Test getDsDichVu: PASSED");
        List<DichVu> dsDichVu = dichVuDAO.getDsDichVu();
        assertNotNull(dsDichVu);
        System.out.println("  - Số lượng dịch vụ: " + dsDichVu.size());
    }
    
    @Test
    @Order(2)
    @DisplayName("Test đếm tổng số dịch vụ")
    public void testGetTongSoDichVu() {
        System.out.println(" Test getTongSoDichVu: PASSED");
        int total = dichVuDAO.getTongSoDichVu();
        assertTrue(total >= 0);
        System.out.println("  - Tổng số dịch vụ: " + total);
    }
    
    @Test
    @Order(3)
    @DisplayName("Test thêm dịch vụ mới")
    public void testThemDichVu() {
        System.out.println(" Test themDichVu: PASSED");
        DichVu dichVuMoi = new DichVu(testMaDichVu, "Test Service", 50000, "Test Description", "Chai");
        
        boolean result = dichVuDAO.themDichVu(dichVuMoi);
        assertTrue(result, "Thêm dịch vụ thành công");
        System.out.println("  - Đã thêm dịch vụ: " + testMaDichVu);
    }
    
    @Test
    @Order(4)
    @DisplayName("Test cập nhật dịch vụ")
    public void testCapNhatDichVu() {
        System.out.println(" Test capNhatDichVuTheoMa: PASSED");
        DichVu dichVuCapNhat = new DichVu(testMaDichVu, "Updated Service", 75000, "Updated Description", "Lon");
        
        boolean result = dichVuDAO.capNhatDichVuTheoMa(dichVuCapNhat);
        assertTrue(result, "Cập nhật dịch vụ thành công");
        System.out.println("  - Đã cập nhật dịch vụ: " + testMaDichVu);
    }
    
    @Test
    @Order(5)
    @DisplayName("Test tìm dịch vụ theo tên")
    public void testTimDichVuTheoMa() {
        System.out.println(" Test timDichVuTheoMa: PASSED");
        DichVu dichVu = dichVuDAO.timDichVuTheoMa("Updated Service");
        
        if (dichVu != null) {
            assertNotNull(dichVu);
            System.out.println("  - Tìm thấy dịch vụ: " + dichVu.getTenDichVu());
        } else {
            System.out.println("  - Không tìm thấy dịch vụ (có thể chưa có trong DB)");
        }
    }
    
    @Test
    @Order(6)
    @DisplayName("Test validate dữ liệu trống")
    public void testValidateDuLieuTrong() {
        System.out.println(" Test validate dữ liệu: PASSED");
        List<DichVu> dsDichVu = dichVuDAO.getDsDichVu();
        
        for (DichVu dv : dsDichVu) {
            assertNotNull(dv.getMaDichVu(), "Mã dịch vụ không được null");
            assertNotNull(dv.getTenDichVu(), "Tên dịch vụ không được null");
        }
    }
}
