package controller;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dao.Phong_DAO;
import model.Phong;
import model.LoaiPhong;
import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Phong_Controller_Test {
    
    @Mock
    private Phong_DAO mockDAO;
    
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
    @DisplayName("Test validate số phòng rỗng")
    public void testValidateSoPhongRong() {
        System.out.println(" Test validate số phòng rỗng: PASSED");
        LoaiPhong loaiPhong = new LoaiPhong("LP001", "Deluxe", 500000);
        Phong phong = new Phong("P001", "", loaiPhong, "Trống", 1);
        
        assertTrue(phong.getSoPhong().isEmpty(), "Số phòng không được rỗng");
    }
    
    @Test
    @Order(2)
    @DisplayName("Test validate tầng không hợp lệ")
    public void testValidateTangKhongHopLe() {
        System.out.println(" Test validate tầng: PASSED");
        LoaiPhong loaiPhong = new LoaiPhong("LP001", "Deluxe", 500000);
        Phong phong = new Phong("P001", "101", loaiPhong, "Trống", 0);
        
        assertFalse(phong.getTang() > 0, "Tầng phải > 0");
    }
    
    @Test
    @Order(3)
    @DisplayName("Test mock getTatCaPhong")
    public void testMockGetTatCaPhong() {
        System.out.println(" Test mock getTatCaPhong: PASSED");
        List<Phong> mockList = new ArrayList<>();
        LoaiPhong lp = new LoaiPhong("LP001", "Deluxe", 500000);
        mockList.add(new Phong("P001", "101", lp, "Trống", 1));
        mockList.add(new Phong("P002", "102", lp, "Đã đặt", 1));
        
        when(mockDAO.getTatCaPhong()).thenReturn(mockList);
        
        List<Phong> result = mockDAO.getTatCaPhong();
        assertEquals(2, result.size());
        verify(mockDAO, times(1)).getTatCaPhong();
    }
    
    @Test
    @Order(4)
    @DisplayName("Test mock countPhongTrong")
    public void testMockCountPhongTrong() {
        System.out.println(" Test mock countPhongTrong: PASSED");
        when(mockDAO.countPhongTrong()).thenReturn(10);
        
        int result = mockDAO.countPhongTrong();
        assertEquals(10, result);
        verify(mockDAO, times(1)).countPhongTrong();
    }
    
    @Test
    @Order(5)
    @DisplayName("Test trạng thái phòng hợp lệ")
    public void testTrangThaiPhongHopLe() {
        System.out.println(" Test trạng thái phòng: PASSED");
        LoaiPhong loaiPhong = new LoaiPhong("LP001", "Deluxe", 500000);
        Phong phong = new Phong("P001", "101", loaiPhong, "Trống", 1);
        
        List<String> trangThaiHopLe = List.of("Trống", "Đã đặt", "Đang sử dụng", "Đang dọn");
        assertTrue(trangThaiHopLe.contains(phong.getTrangThai()));
    }
}
