package dao;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import model.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NghiepVuDatPhong_Test {

    @Test
    @Order(1)
    @DisplayName("Test tạo model Phieu + ChiTiet (không ghi DB)")
    public void testCreatePhieuVaChiTietModels() {
        PhieuDatPhong pdp = new PhieuDatPhong("PDP-TEST-ADD-001");
        KhachHang kh = new KhachHang(); kh.setMaKhachHang("KH-TEST-001"); kh.setTenKhachHang("Test KH");
        pdp.setKhachHang(kh);
        LoaiDatPhong ldp = new LoaiDatPhong("LDP-NGAY");
        LoaiPhong lp = new LoaiPhong("LP-S");
        Phong phong = new Phong("P-TEST-101"); phong.setLoaiPhong(lp);

        List<DichVu> dsDv = new ArrayList<>();
        ChiTietPhieuDatPhong ct = new ChiTietPhieuDatPhong(pdp, ldp, dsDv, 48, null, null, phong, 2);

        assertEquals("PDP-TEST-ADD-001", pdp.getMaPhieuDatPhong());
        assertEquals(48, ct.getSoGioLuuTru());
        assertEquals(2, ct.getSoNguoi());
        assertNotNull(ct.getPhong());
    }

    @Test
    @Order(2)
    @DisplayName("Test chuyển trạng thái khi nhận phòng (model-level)")
    public void testNhanPhongStateTransitionModel() {
        PhieuDatPhong pdp = new PhieuDatPhong("PDP-TEST-ADD-002");
        ChiTietPhieuDatPhong ct = new ChiTietPhieuDatPhong(pdp, null, new ArrayList<>(), 24, null, null, new Phong("P-02"), 1);

        // Pre-condition
        assertNull(ct.getThoiGianNhanPhong());
        assertNull(ct.getTrangThai());

        // Simulate receptionist action
        LocalDateTime now = LocalDateTime.now();
        ct.setThoiGianNhanPhong(now);
        ct.setTrangThai("Đang ở");
        pdp.setTrangThai("Đã nhận");

        assertNotNull(ct.getThoiGianNhanPhong());
        assertEquals("Đang ở", ct.getTrangThai());
        assertEquals("Đã nhận", pdp.getTrangThai());
    }

    @Test
    @Order(3)
    @DisplayName("Test tạo hóa đơn cơ bản (model-level)")
    public void testCreateHoaDonModel() {
        HoaDon hd = new HoaDon("HD-TEST-0001");
        KhachHang kh = new KhachHang(); kh.setMaKhachHang("KH-TEST-002"); kh.setTenKhachHang("KH Invoice");
        hd.setKhachHang(kh);
        hd.setTongTien(250000);
        hd.setTrangThai("Chưa thanh toán");

        assertEquals("HD-TEST-0001", hd.getMaHoaDon());
        assertEquals(250000, hd.getTongTien());
        assertEquals("Chưa thanh toán", hd.getTrangThai());
        assertNotNull(hd.getKhachHang());
    }
}
