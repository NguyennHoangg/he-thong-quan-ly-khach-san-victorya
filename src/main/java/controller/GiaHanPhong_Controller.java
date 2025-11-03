package controller;

import java.time.LocalDateTime;
import java.util.List;

import dao.ChiTietPhieuDatPhong_DAO;
import dao.KhachHang_DAO;
import model.ChiTietPhieuDatPhong;
import model.KhachHang;

/**
 * Controller xử lý logic cho trang Gia hạn phòng
 * Đóng vai trò trung gian giữa GUI và DAO
 */
public class GiaHanPhong_Controller {
    
    private ChiTietPhieuDatPhong_DAO chiTietPhieuDatPhongDAO;
    private KhachHang_DAO khachHangDAO;
    
    /**
     * Constructor khởi tạo controller
     */
    public GiaHanPhong_Controller() {
        this.chiTietPhieuDatPhongDAO = new ChiTietPhieuDatPhong_DAO();
        this.khachHangDAO = new KhachHang_DAO();
    }
    
    /**
     * Lấy tất cả phòng đang ở
     * @return Danh sách ChiTietPhieuDatPhong đang ở
     */
    public List<ChiTietPhieuDatPhong> layTatCaPhongDangO() {
        return chiTietPhieuDatPhongDAO.layTatCaPhongDangO();
    }
    
    /**
     * Tìm danh sách phòng đang ở theo số điện thoại khách hàng
     * @param soDienThoai Số điện thoại khách hàng
     * @return Danh sách ChiTietPhieuDatPhong đang ở
     */
    public List<ChiTietPhieuDatPhong> timDatPhongHienTaiTheoSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }
        return chiTietPhieuDatPhongDAO.getDatPhongHienTaiTheoSoDienThoai(soDienThoai.trim());
    }
    
    /**
     * Tìm danh sách phòng đang thuê theo CCCD khách hàng (deprecated - sử dụng theo số điện thoại)
     * @param cccd CCCD của khách hàng
     * @return Danh sách ChiTietPhieuDatPhong đang hoạt động
     */
    @Deprecated
    public List<ChiTietPhieuDatPhong> timDatPhongHienTaiTheoCCCD(String cccd) {
        return chiTietPhieuDatPhongDAO.getDatPhongHienTaiTheoCCCD(cccd);
    }
    
    /**
     * Lấy thời gian gia hạn tối đa cho một phòng (2 giờ trước khi có đặt phòng tiếp theo)
     * @param maPhong Mã phòng
     * @param thoiGianTraPhongHienTai Thời gian trả phòng hiện tại
     * @return Thời gian gia hạn tối đa hoặc null nếu không có giới hạn
     */
    public LocalDateTime layThoiGianGiaHanToiDa(String maPhong, LocalDateTime thoiGianTraPhongHienTai) {
        if (maPhong == null || maPhong.trim().isEmpty() || thoiGianTraPhongHienTai == null) {
            return null;
        }
        return chiTietPhieuDatPhongDAO.layThoiGianDatPhongTiepTheo(maPhong, thoiGianTraPhongHienTai);
    }
    
    /**
     * Tìm khách hàng theo số điện thoại
     * @param soDienThoai Số điện thoại khách hàng
     * @return KhachHang nếu tìm thấy, null nếu không
     */
    public KhachHang timKhachHangTheoSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            return null;
        }
        return khachHangDAO.timKhachHangTheoSoDienThoai(soDienThoai.trim());
    }
    
    /**
     * Lấy tất cả phòng đang ở của một khách hàng (để hiển thị trong modal)
     * @param soDienThoai Số điện thoại khách hàng
     * @return Danh sách ChiTietPhieuDatPhong đang ở
     */
    public List<ChiTietPhieuDatPhong> layTatCaPhongDangOTheoSoDienThoai(String soDienThoai) {
        return timDatPhongHienTaiTheoSoDienThoai(soDienThoai);
    }
    
    /**
     * Gia hạn phòng đến thời gian mới
     * @param maPhieuDatPhong Mã phiếu đặt phòng
     * @param maPhong Mã phòng
     * @param gioKetThucMoi Thời gian kết thúc mới
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean giaHanDen(String maPhieuDatPhong, String maPhong, LocalDateTime gioKetThucMoi) {
        return chiTietPhieuDatPhongDAO.giaHanDen(maPhieuDatPhong, maPhong, gioKetThucMoi);
    }
}
