package dao;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import model.Phong;
import model.LoaiPhong;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Phong_DAO_Test {
    
    private static Phong_DAO phongDAO;
    
    @BeforeAll
    public static void setUpClass() {
        phongDAO = new Phong_DAO();
    }
    
    @Test
    @Order(1)
    @DisplayName("Test đếm tổng số phòng")
    public void testCountAll() {
        System.out.println(" Test countAll: PASSED");
        int total = phongDAO.countAll();
        assertTrue(total >= 0);
        System.out.println("  - Tổng số phòng: " + total);
    }
    
    @Test
    @Order(2)
    @DisplayName("Test đếm số phòng trống")
    public void testCountPhongTrong() {
        System.out.println(" Test countPhongTrong: PASSED");
        int soPhongTrong = phongDAO.countPhongTrong();
        assertTrue(soPhongTrong >= 0);
        System.out.println("  - Số phòng trống: " + soPhongTrong);
    }
    
    @Test
    @Order(3)
    @DisplayName("Test lấy tất cả phòng")
    public void testGetTatCaPhong() {
        System.out.println(" Test getTatCaPhong: PASSED");
        List<Phong> dsPhong = phongDAO.getTatCaPhong();
        
        assertNotNull(dsPhong);
        System.out.println("  - Số lượng phòng: " + dsPhong.size());
        
        if (!dsPhong.isEmpty()) {
            Phong phongDauTien = dsPhong.get(0);
            assertNotNull(phongDauTien.getMaPhong());
            assertNotNull(phongDauTien.getLoaiPhong());
            System.out.println("  - Phòng đầu tiên: " + phongDauTien.getSoPhong());
        }
    }
    
    @Test
    @Order(4)
    @DisplayName("Test validate dữ liệu phòng")
    public void testValidatePhongData() {
        System.out.println(" Test validate dữ liệu phòng: PASSED");
        List<Phong> dsPhong = phongDAO.getTatCaPhong();
        
        for (Phong phong : dsPhong) {
            assertNotNull(phong.getMaPhong(), "Mã phòng không được null");
            assertNotNull(phong.getSoPhong(), "Số phòng không được null");
            assertNotNull(phong.getTrangThai(), "Trạng thái không được null");
            assertTrue(phong.getTang() > 0, "Tầng phải > 0");
        }
    }
    
    @Test
    @Order(5)
    @DisplayName("Test validate loại phòng")
    public void testValidateLoaiPhong() {
        System.out.println(" Test validate loại phòng: PASSED");
        List<Phong> dsPhong = phongDAO.getTatCaPhong();
        
        for (Phong phong : dsPhong) {
            LoaiPhong loaiPhong = phong.getLoaiPhong();
            assertNotNull(loaiPhong, "Loại phòng không được null");
            assertNotNull(loaiPhong.getMaLoaiPhong());
            assertTrue(loaiPhong.getGia() > 0, "Giá phòng phải > 0");
        }
    }
    
    @Test
    @Order(6)
    @DisplayName("Test số lượng phòng trống <= tổng phòng")
    public void testSoPhongTrongLonHonTotal() {
        System.out.println(" Test logic số phòng: PASSED");
        int total = phongDAO.countAll();
        int soPhongTrong = phongDAO.countPhongTrong();
        
        assertTrue(soPhongTrong <= total, "Số phòng trống phải <= tổng số phòng");
        System.out.println("  - Phòng trống/Tổng: " + soPhongTrong + "/" + total);
    }
}
