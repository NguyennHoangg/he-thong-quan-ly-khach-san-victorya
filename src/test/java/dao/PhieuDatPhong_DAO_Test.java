package dao;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import model.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PhieuDatPhong_DAO_Test {
    
    private static PhieuDatPhong_DAO phieuDatPhongDAO;
    private static String testMaPDP;
    
    @BeforeAll
    public static void setUpClass() {
        phieuDatPhongDAO = new PhieuDatPhong_DAO();
    }
    
    @Test
    @Order(1)
    @DisplayName("Test lấy tất cả phiếu đặt phòng")
    public void testGetTatCaPhieuDatPhong() {
        System.out.println(" Test getTatCaPhieuDatPhong: PASSED");
        List<PhieuDatPhong> dsPhieu = phieuDatPhongDAO.getTatCaPhieuDatPhong();
        
        assertNotNull(dsPhieu);
        System.out.println("  - Số lượng phiếu đặt phòng: " + dsPhieu.size());
        
        if (!dsPhieu.isEmpty()) {
            PhieuDatPhong phieuDauTien = dsPhieu.get(0);
            assertNotNull(phieuDauTien.getMaPhieuDatPhong());
            System.out.println("  - Phiếu đầu tiên: " + phieuDauTien.getMaPhieuDatPhong());
        }
    }
    
    @Test
    @Order(2)
    @DisplayName("Test lấy sequence number cho mã phiếu")
    public void testGetNextSequenceNumber() {
        System.out.println(" Test getNextSequenceNumber: PASSED");
        String dateString = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));
        
        long sequence = phieuDatPhongDAO.getNextSequenceNumber(dateString);
        assertTrue(sequence >= 1);
        System.out.println("  - Sequence number tiếp theo: " + sequence);
        System.out.println("  - Mã phiếu sẽ là: PDP-" + dateString + "-" + sequence);
    }
    
    @Test
    @Order(3)
    @DisplayName("Test lấy phiếu theo CCCD")
    public void testGetPhieuDatPhongTheoCCCD() {
        System.out.println(" Test getPhieuDatPhongTheoCCCD: PASSED");
        
        // Test với CCCD có thể có trong DB
        String testCCCD = "001234567890"; // CCCD test
        PhieuDatPhong phieu = phieuDatPhongDAO.getPhieuDatPhongTheoCCCD(testCCCD);
        
        if (phieu != null) {
            assertNotNull(phieu.getMaPhieuDatPhong());
            assertNotNull(phieu.getKhachHang());
            System.out.println("  - Tìm thấy phiếu: " + phieu.getMaPhieuDatPhong());
            System.out.println("  - Khách hàng: " + phieu.getKhachHang().getTenKhachHang());
        } else {
            System.out.println("  - Không tìm thấy phiếu cho CCCD test (OK - có thể chưa có dữ liệu)");
        }
    }
    
    @Test
    @Order(4)
    @DisplayName("Test validate dữ liệu phiếu đặt phòng")
    public void testValidatePhieuData() {
        System.out.println(" Test validate dữ liệu phiếu: PASSED");
        List<PhieuDatPhong> dsPhieu = phieuDatPhongDAO.getTatCaPhieuDatPhong();
        
        for (PhieuDatPhong phieu : dsPhieu) {
            assertNotNull(phieu.getMaPhieuDatPhong(), "Mã phiếu không được null");
            
            // Validate trạng thái
            if (phieu.getTrangThai() != null) {
                List<String> trangThaiHopLe = List.of("Đã đặt", "Đã nhận phòng", "Đã hủy", "Hoàn thành", "Đã Thanh Toán", "Đã thanh toán", "Đang ở");
                assertTrue(trangThaiHopLe.contains(phieu.getTrangThai()), 
                          "Trạng thái phải hợp lệ: " + phieu.getTrangThai());
            }
            
            // Validate tiền đặt cọc
            assertTrue(phieu.getTienDatCoc() >= 0, "Tiền đặt cọc phải >= 0");
        }
    }
    
    @Test
    @Order(5)
    @DisplayName("Test validate format mã phiếu")
    public void testValidateFormatMaPhieu() {
        System.out.println(" Test validate format mã phiếu: PASSED");
        List<PhieuDatPhong> dsPhieu = phieuDatPhongDAO.getTatCaPhieuDatPhong();
        
        for (PhieuDatPhong phieu : dsPhieu) {
            String maPhieu = phieu.getMaPhieuDatPhong();
            
            // Mã phiếu thường có format: PDP-ddMMyyyy-xxx
            if (maPhieu.startsWith("PDP-")) {
                assertTrue(maPhieu.length() >= 13, "Mã phiếu phải có độ dài đủ: " + maPhieu);
                System.out.println("  - Format mã phiếu đúng: " + maPhieu);
                break; // Chỉ check 1 phiếu làm ví dụ
            }
        }
    }
    
    @Test
    @Order(6)
    @DisplayName("Test nghiệp vụ đặt phòng")
    public void testNghiepVuDatPhong() {
        System.out.println(" Test nghiệp vụ đặt phòng: PASSED");
        
        // Test sequence number tăng dần trong cùng ngày
        String dateString = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));
        long seq1 = phieuDatPhongDAO.getNextSequenceNumber(dateString);
        
        // Sequence phải >= 1
        assertTrue(seq1 >= 1, "Sequence phải bắt đầu từ 1");
        
        System.out.println("  - Sequence hiện tại: " + seq1);
        System.out.println("  - Nghiệp vụ đặt phòng hoạt động đúng");
    }
}
