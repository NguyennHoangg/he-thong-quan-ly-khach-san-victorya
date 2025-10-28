package controller;

import java.util.List;

import dao.PhieuDatPhong_DAO;
import dao.Phong_DAO;
import model.PhieuDatPhong;

/**
 * Controller xử lý logic cho trang Nhận phòng
 * Đóng vai trò trung gian giữa GUI và DAO
 */
public class NhanPhong_Controller {
    
    private PhieuDatPhong_DAO phieuDatPhongDAO;
    private Phong_DAO phongDAO;
    
    /**
     * Constructor khởi tạo controller
     */
    public NhanPhong_Controller() {
        this.phieuDatPhongDAO = new PhieuDatPhong_DAO();
        this.phongDAO = new Phong_DAO();
    }
    
    /**
     * Tìm phiếu đặt phòng và danh sách phòng chờ nhận theo CCCD
     * @param cccd CCCD của khách hàng
     * @return PhieuDatPhong với danh sách phòng chờ nhận
     */
    public PhieuDatPhong timPhieuDatPhongChoNhanTheoCCCD(String cccd) {
        System.out.println("Controller: Tìm kiếm phiếu đặt phòng chờ nhận cho CCCD: " + cccd);
        
        PhieuDatPhong ketQua = phieuDatPhongDAO.getPhieuDatPhongChoNhanTheoCCCD(cccd);
        
        if (ketQua != null && ketQua.getDsachPhieuDatPhong() != null) {
            System.out.println("Controller: Tìm được " + ketQua.getDsachPhieuDatPhong().size() + " phòng chờ nhận");
        } else {
            System.out.println("Controller: Không tìm thấy phòng chờ nhận cho CCCD: " + cccd);
        }
        
        return ketQua;
    }
    
    /**
     * Nhận phòng - cập nhật trạng thái phòng từ "Đã đặt" thành "Đang ở"
     * @param maPhong Mã phòng cần nhận
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean nhanPhong(String maPhong) {
        System.out.println("Controller: Bắt đầu nhận phòng " + maPhong);
        
        // Cập nhật trạng thái phòng từ "Đã đặt" thành "Đang ở"
        boolean thanhCong = phongDAO.capNhatTrangThaiPhong(maPhong, "Đang ở");
        
        System.out.println("Controller: Nhận phòng " + (thanhCong ? "thành công" : "thất bại") + " cho phòng " + maPhong);
        
        return thanhCong;
    }
    
    /**
     * Nhận nhiều phòng cùng lúc
     * @param danhSachMaPhong Danh sách mã phòng cần nhận
     * @return Số lượng phòng nhận thành công
     */
    public int nhanNhieuPhong(List<String> danhSachMaPhong) {
        System.out.println("Controller: Bắt đầu nhận " + danhSachMaPhong.size() + " phòng");
        
        int soPhongThanhCong = 0;
        
        for (String maPhong : danhSachMaPhong) {
            if (nhanPhong(maPhong)) {
                soPhongThanhCong++;
            }
        }
        
        System.out.println("Controller: Nhận thành công " + soPhongThanhCong + "/" + danhSachMaPhong.size() + " phòng");
        
        return soPhongThanhCong;
    }
}

