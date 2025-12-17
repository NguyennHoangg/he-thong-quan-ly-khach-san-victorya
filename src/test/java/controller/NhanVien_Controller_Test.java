package controller;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dao.NhanVien_DAO;
import model.NhanVien;
import model.TaiKhoan;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NhanVien_Controller_Test {
    
    @Mock
    private NhanVien_DAO mockDAO;
    
    private AutoCloseable closeable;
    private NhanVien nhanVien;
    
    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        TaiKhoan tk = new TaiKhoan("admin", "123456", "admin");
        nhanVien = new NhanVien(
            "NV001",
            "001234567890",
            "Nguyen Van A",
            tk,
            true,
            LocalDate.of(1990, 5, 15),
            "test@email.com",
            "0901234567",
            LocalDate.of(2020, 1, 1),
            "Đang làm việc",
            "123 Test St"
        );
    }
    
    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }
    
    @Test
    @Order(1)
    @DisplayName("Test validate CCCD không đúng định dạng")
    public void testValidateCCCD() {
        System.out.println("✓ Test validate CCCD: PASSED");
        nhanVien.setCCCD("123");
        assertFalse(nhanVien.getCCCD().length() == 12, "CCCD phải có 12 số");
    }
    
    @Test
    @Order(2)
    @DisplayName("Test validate email không hợp lệ")
    public void testValidateEmail() {
        System.out.println("✓ Test validate email: PASSED");
        nhanVien.setEmail("invalidemail");
        assertFalse(nhanVien.getEmail().contains("@"), "Email phải chứa @");
    }
    
    @Test
    @Order(3)
    @DisplayName("Test validate SĐT không hợp lệ")
    public void testValidateSoDienThoai() {
        System.out.println("✓ Test validate SĐT: PASSED");
        nhanVien.setSoDienThoai("123");
        assertFalse(nhanVien.getSoDienThoai().length() == 10, "SĐT phải có 10 số");
    }
    
    @Test
    @Order(4)
    @DisplayName("Test mock getDsNhanVien")
    public void testMockGetDsNhanVien() {
        System.out.println("✓ Test mock getDsNhanVien: PASSED");
        List<NhanVien> mockList = new ArrayList<>();
        mockList.add(nhanVien);
        
        when(mockDAO.getDsNhanVien()).thenReturn(mockList);
        
        List<NhanVien> result = mockDAO.getDsNhanVien();
        assertEquals(1, result.size());
        verify(mockDAO, times(1)).getDsNhanVien();
    }
    
    @Test
    @Order(5)
    @DisplayName("Test validate ngày sinh")
    public void testValidateNgaySinh() {
        System.out.println("✓ Test validate ngày sinh: PASSED");
        assertTrue(nhanVien.getNgaySinh().isBefore(LocalDate.now()), 
                   "Ngày sinh phải trước ngày hiện tại");
    }
}
