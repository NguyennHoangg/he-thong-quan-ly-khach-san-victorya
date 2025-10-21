package controller;

import java.time.LocalDateTime;
import java.util.List;

import dao.ChiTietPhieuDatPhong_DAO;
import model.ChiTietPhieuDatPhong;

/**
 * Controller xử lý logic cho trang Gia hạn phòng
 * Đóng vai trò trung gian giữa GUI và DAO
 */
public class GiaHanPhong_Controller {
    
    private ChiTietPhieuDatPhong_DAO chiTietPhieuDatPhongDAO;
    
    /**
     * Constructor khởi tạo controller
     */
    public GiaHanPhong_Controller() {
        this.chiTietPhieuDatPhongDAO = new ChiTietPhieuDatPhong_DAO();
    }
    
    /**
     * Tìm danh sách phòng đang thuê theo CCCD khách hàng
     * @param cccd CCCD của khách hàng
     * @return Danh sách ChiTietPhieuDatPhong đang hoạt động
     */
    public List<ChiTietPhieuDatPhong> timDatPhongHienTaiTheoCCCD(String cccd) {
        System.out.println("Controller: Tìm kiếm phòng cho CCCD: " + cccd);
        
        List<ChiTietPhieuDatPhong> ketQua = chiTietPhieuDatPhongDAO.getDatPhongHienTaiTheoCCCD(cccd);
        
        System.out.println("Controller: Tìm được " + (ketQua != null ? ketQua.size() : 0) + " phòng cho CCCD: " + cccd);
        
        return ketQua;
    }
    
    /**
     * Gia hạn phòng đến thời gian mới
     * @param maPhieuDatPhong Mã phiếu đặt phòng
     * @param maPhong Mã phòng
     * @param gioKetThucMoi Thời gian kết thúc mới
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean giaHanDen(String maPhieuDatPhong, String maPhong, LocalDateTime gioKetThucMoi) {
        System.out.println("Controller: Bắt đầu gia hạn phòng " + maPhong + " đến " + gioKetThucMoi);
        
        boolean thanhCong = chiTietPhieuDatPhongDAO.giaHanDen(maPhieuDatPhong, maPhong, gioKetThucMoi);
        
        System.out.println("Controller: Gia hạn " + (thanhCong ? "thành công" : "thất bại") + " cho phòng " + maPhong);
        
        return thanhCong;
    }
}
