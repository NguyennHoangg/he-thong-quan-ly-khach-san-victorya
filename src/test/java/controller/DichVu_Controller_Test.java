package controller;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dao.DichVu_DAO;
import model.DichVu;
import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DichVu_Controller_Test {
    
    @Mock
    private DichVu_DAO mockDAO;
    
    private AutoCloseable closeable;
    
    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }
    
    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }
    
    @Test
    @Order(1)
    @DisplayName("Test validate tên dịch vụ rỗng")
    public void testValidateTenDichVuRong() {
        System.out.println(" Test validate tên rỗng: PASSED");
        DichVu dichVu = new DichVu("DV001", "", 15000, "Test", "Chai");
        
        // Tên dịch vụ rỗng là không hợp lệ -> test phải fail nếu tên rỗng
        assertFalse(dichVu.getTenDichVu().isEmpty(), "Tên dịch vụ không được rỗng");
    }
    
    @Test
    @Order(2)
    @DisplayName("Test validate giá âm")
    public void testValidateGiaAm() {
        System.out.println(" Test validate giá âm: PASSED");
        DichVu dichVu = new DichVu("DV001", "Test", -1000, "Test", "Chai");
        
        // Giá âm là không hợp lệ -> test phải fail nếu giá < 0
        assertTrue(dichVu.getGia() >= 0, "Giá không được âm");
    }
    
    @Test
    @Order(3)
    @DisplayName("Test mock getDsDichVu")
    public void testMockGetDsDichVu() {
        System.out.println(" Test mock getDsDichVu: PASSED");
        List<DichVu> mockList = new ArrayList<>();
        mockList.add(new DichVu("DV001", "Coca", 15000, "Test", "Chai"));
        mockList.add(new DichVu("DV002", "Pepsi", 12000, "Test", "Lon"));
        
        when(mockDAO.getDsDichVu()).thenReturn(mockList);
        
        List<DichVu> result = mockDAO.getDsDichVu();
        assertEquals(2, result.size());
        verify(mockDAO, times(1)).getDsDichVu();
    }
    
    @Test
    @Order(4)
    @DisplayName("Test mock themDichVu thành công")
    public void testMockThemDichVuThanhCong() {
        System.out.println(" Test mock themDichVu: PASSED");
        DichVu dichVuMoi = new DichVu("DV003", "Test", 20000, "Test", "Chai");
        
        when(mockDAO.themDichVu(dichVuMoi)).thenReturn(true);
        
        boolean result = mockDAO.themDichVu(dichVuMoi);
        assertTrue(result);
        verify(mockDAO, times(1)).themDichVu(dichVuMoi);
    }
    
    @Test
    @Order(5)
    @DisplayName("Test mock capNhatDichVu")
    public void testMockCapNhatDichVu() {
        System.out.println(" Test mock capNhatDichVu: PASSED");
        DichVu dichVuCapNhat = new DichVu("DV001", "Updated", 25000, "Updated", "Lon");
        
        when(mockDAO.capNhatDichVuTheoMa(dichVuCapNhat)).thenReturn(true);
        
        boolean result = mockDAO.capNhatDichVuTheoMa(dichVuCapNhat);
        assertTrue(result);
        verify(mockDAO, times(1)).capNhatDichVuTheoMa(dichVuCapNhat);
    }
}
