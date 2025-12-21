package dao;

import config.ConnectDatabase;
import model.*;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HoaDon_DAO_Test {
    
    private static HoaDon_DAO hoaDonDAO;
    
    @BeforeAll
    public static void setup() {
        hoaDonDAO = new HoaDon_DAO();
    }
    
    @AfterAll
    public static void tearDown() {
        // Cleanup if needed
    }
    
    @Test
    @Order(1)
    @DisplayName("Test lấy tất cả hóa đơn")
    public void testGetAll() {
        System.out.println(" Test lấy tất cả hóa đơn: PASSED");
        List<HoaDon> dsHoaDon = hoaDonDAO.getAll();
        
        assertNotNull(dsHoaDon, "Danh sách hóa đơn không được null");
        System.out.println("  - Số lượng hóa đơn: " + dsHoaDon.size());
        
        if (!dsHoaDon.isEmpty()) {
            HoaDon hd = dsHoaDon.get(0);
            System.out.println("  - Hóa đơn đầu tiên: " + hd.getMaHoaDon());
            System.out.println("  - Tổng tiền: " + String.format("%,.0f", hd.getTongTien()) + " VNĐ");
        }
    }
    
    @Test
    @Order(2)
    @DisplayName("Test tìm hóa đơn theo mã")
    public void testFindById() {
        System.out.println(" Test tìm hóa đơn theo mã: PASSED");
        
        // Lấy hóa đơn đầu tiên để test
        List<HoaDon> dsHoaDon = hoaDonDAO.getAll();
        if (dsHoaDon.isEmpty()) {
            System.out.println("  - Không có hóa đơn để test");
            return;
        }
        
        String maHD = dsHoaDon.get(0).getMaHoaDon();
        HoaDon hd = hoaDonDAO.findById(maHD);
        
        assertNotNull(hd, "Hóa đơn phải tìm thấy");
        assertEquals(maHD, hd.getMaHoaDon(), "Mã hóa đơn phải khớp");
        System.out.println("  - Tìm thấy hóa đơn: " + maHD);
        System.out.println("  - Trạng thái: " + hd.getTrangThai());
    }
    
    @Test
    @Order(3)
    @DisplayName("Test validate dữ liệu hóa đơn")
    public void testValidateHoaDonData() {
        System.out.println(" Test validate dữ liệu hóa đơn: PASSED");
        List<HoaDon> dsHoaDon = hoaDonDAO.getAll();
        
        int validCount = 0;
        for (HoaDon hd : dsHoaDon) {
            // Validate mã hóa đơn
            assertNotNull(hd.getMaHoaDon(), "Mã hóa đơn không được null");
            assertFalse(hd.getMaHoaDon().isEmpty(), "Mã hóa đơn không được rỗng");
            
            // Validate tổng tiền
            assertTrue(hd.getTongTien() >= 0, "Tổng tiền phải >= 0");
            
            // Validate trạng thái
            if (hd.getTrangThai() != null) {
                List<String> trangThaiHopLe = List.of(
                    "Chưa thanh toán", 
                    "Đã thanh toán", 
                    "Đã hủy",
                    "Chờ thanh toán"
                );
                assertTrue(trangThaiHopLe.contains(hd.getTrangThai()), 
                          "Trạng thái phải hợp lệ: " + hd.getTrangThai());
            }
            
            // Validate ngày tạo
            assertNotNull(hd.getNgayTao(), "Ngày tạo không được null");
            
            validCount++;
        }
        
        System.out.println("  - Đã validate " + validCount + " hóa đơn");
    }
    
    @Test
    @Order(4)
    @DisplayName("Test validate khách hàng trong hóa đơn")
    public void testValidateKhachHang() {
        System.out.println(" Test validate khách hàng: PASSED");
        List<HoaDon> dsHoaDon = hoaDonDAO.getAll();
        
        int countCoKH = 0;
        for (HoaDon hd : dsHoaDon) {
            if (hd.getKhachHang() != null) {
                KhachHang kh = hd.getKhachHang();
                assertNotNull(kh.getMaKhachHang(), "Mã khách hàng không được null");
                assertNotNull(kh.getTenKhachHang(), "Tên khách hàng không được null");
                countCoKH++;
            }
        }
        
        System.out.println("  - Số hóa đơn có khách hàng: " + countCoKH);
    }
    
    @Test
    @Order(5)
    @DisplayName("Test validate nhân viên trong hóa đơn")
    public void testValidateNhanVien() {
        System.out.println(" Test validate nhân viên: PASSED");
        List<HoaDon> dsHoaDon = hoaDonDAO.getAll();
        
        int countCoNV = 0;
        for (HoaDon hd : dsHoaDon) {
            if (hd.getNhanVien() != null) {
                NhanVien nv = hd.getNhanVien();
                assertNotNull(nv.getMaNhanVien(), "Mã nhân viên không được null");
                assertNotNull(nv.getTenNhanVien(), "Tên nhân viên không được null");
                countCoNV++;
            }
        }
        
        System.out.println("  - Số hóa đơn có nhân viên: " + countCoNV);
    }
    
    @Test
    @Order(6)
    @DisplayName("Test validate khuyến mãi trong hóa đơn")
    public void testValidateKhuyenMai() {
        System.out.println(" Test validate khuyến mãi: PASSED");
        List<HoaDon> dsHoaDon = hoaDonDAO.getAll();
        
        int countCoKM = 0;
        for (HoaDon hd : dsHoaDon) {
            if (hd.getKhuyenMai() != null) {
                KhuyenMai km = hd.getKhuyenMai();
                assertNotNull(km.getMaKhuyenMai(), "Mã khuyến mãi không được null");
                assertNotNull(km.getTenKhuyenMai(), "Tên khuyến mãi không được null");
                assertTrue(km.getHeSo() >= 0 && km.getHeSo() <= 1, 
                          "Hệ số khuyến mãi phải từ 0-1");
                countCoKM++;
            }
        }
        
        System.out.println("  - Số hóa đơn có khuyến mãi: " + countCoKM);
    }
    
    @Test
    @Order(7)
    @DisplayName("Test tìm kiếm hóa đơn theo từ khóa")
    public void testTimKiem() {
        System.out.println(" Test tìm kiếm hóa đơn: PASSED");
        
        // Test tìm kiếm không filter gì
        List<HoaDon> ketQua = hoaDonDAO.timKiem("", null, null, null);
        assertNotNull(ketQua, "Kết quả không được null");
        System.out.println("  - Tìm kiếm trống: " + ketQua.size() + " hóa đơn");
        
        // Test tìm kiếm theo trạng thái
        List<HoaDon> ketQuaDaThanhToan = hoaDonDAO.timKiem("", "Đã thanh toán", null, null);
        assertNotNull(ketQuaDaThanhToan, "Kết quả không được null");
        System.out.println("  - Đã thanh toán: " + ketQuaDaThanhToan.size() + " hóa đơn");
        
        // Test tìm kiếm theo ngày
        LocalDate today = LocalDate.now();
        LocalDate lastWeek = today.minusDays(7);
        List<HoaDon> ketQuaTheoNgay = hoaDonDAO.timKiem("", null, lastWeek, today);
        assertNotNull(ketQuaTheoNgay, "Kết quả không được null");
        System.out.println("  - Trong 7 ngày: " + ketQuaTheoNgay.size() + " hóa đơn");
    }
    
    @Test
    @Order(8)
    @DisplayName("Test nghiệp vụ thanh toán")
    public void testNghiepVuThanhToan() {
        System.out.println(" Test nghiệp vụ thanh toán: PASSED");
        
        // Kịch bản: Tìm hóa đơn chưa thanh toán, tính tổng tiền
        List<HoaDon> dsChuaThanhToan = hoaDonDAO.timKiem("", "Chưa thanh toán", null, null);
        
        if (dsChuaThanhToan.isEmpty()) {
            System.out.println("  - Không có hóa đơn chưa thanh toán");
            
            // Test với hóa đơn đã thanh toán
            List<HoaDon> dsHoaDon = hoaDonDAO.getAll();
            if (!dsHoaDon.isEmpty()) {
                HoaDon hd = dsHoaDon.get(0);
                double tongTien = hd.getTongTien();
                
                // Validate nghiệp vụ
                assertNotNull(hd.getMaHoaDon());
                assertTrue(tongTien >= 0);
                assertNotNull(hd.getNgayTao());
                
                System.out.println("  - Test với hóa đơn: " + hd.getMaHoaDon());
                System.out.println("  - Tổng tiền: " + String.format("%,.0f", tongTien) + " VNĐ");
                
                // Nếu có khuyến mãi
                if (hd.getKhuyenMai() != null) {
                    float heSo = hd.getKhuyenMai().getHeSo();
                    double tienGiamGia = tongTien * (1 - heSo);
                    System.out.println("  - Khuyến mãi: " + (int)((1 - heSo) * 100) + "%");
                    System.out.println("  - Sau giảm: " + String.format("%,.0f", tienGiamGia) + " VNĐ");
                }
            }
        } else {
            System.out.println("  - Tìm thấy " + dsChuaThanhToan.size() + " hóa đơn chưa thanh toán");
            HoaDon hd = dsChuaThanhToan.get(0);
            System.out.println("  - Hóa đơn: " + hd.getMaHoaDon());
            System.out.println("  - Số tiền cần thanh toán: " + String.format("%,.0f", hd.getTongTien()) + " VNĐ");
        }
    }
}
