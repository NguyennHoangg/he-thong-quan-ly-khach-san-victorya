package controller;

import java.time.LocalDateTime;
import java.util.List;

import dao.ChiTietPhieuDatPhong_DAO;
import dao.PhieuDatPhong_DAO;
import dao.Phong_DAO;
import model.ChiTietPhieuDatPhong;
import model.PhieuDatPhong;

/**
 * Controller xử lý logic cho trang Nhận phòng
 * Đóng vai trò trung gian giữa GUI và DAO
 */
public class NhanPhong_Controller {

    private ChiTietPhieuDatPhong_DAO chiTietPhieuDatPhongDAO;
    private PhieuDatPhong_DAO phieuDatPhongDAO;
    private Phong_DAO phongDAO;

    /**
     * Constructor khởi tạo controller
     */
    public NhanPhong_Controller() {
        this.chiTietPhieuDatPhongDAO = new ChiTietPhieuDatPhong_DAO();
        this.phieuDatPhongDAO = new PhieuDatPhong_DAO();
        this.phongDAO = new Phong_DAO();
    }

    /**
     * Tìm phiếu đặt phòng theo CCCD
     * 
     * @param cccd CCCD của khách hàng
     * @return PhieuDatPhong nếu tìm thấy, null nếu không
     */
    public PhieuDatPhong timPhieuDatPhongTheoCCCD(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) {
            return null;
        }

        return phieuDatPhongDAO.getPhieuDatPhongChuaNhanTheoCCCD(cccd.trim());
    }

    /**
     * Nhận phòng (check-in)
     * Cập nhật thoiGianNhanPhong = thời gian hiện tại và trạng thái phòng = "Đang
     * ở"
     * 
     * @param maPhieuDatPhong Mã phiếu đặt phòng
     * @param maPhong         Mã phòng
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean nhanPhong(String maPhieuDatPhong, String maPhong) {
        try {
            LocalDateTime thoiGianHienTai = LocalDateTime.now();

            // Cập nhật thời gian nhận phòng
            boolean capNhatThoiGian = chiTietPhieuDatPhongDAO.capNhatThoiGianNhanPhong(
                    maPhieuDatPhong, maPhong, thoiGianHienTai);

            if (!capNhatThoiGian) {
                return false;
            }

            // Cập nhật trạng thái phòng thành "Đang ở"
            boolean capNhatTrangThai = phongDAO.capNhatTrangThaiPhong(maPhong, "Đang ở");

            return capNhatTrangThai;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Nhận nhiều phòng cùng lúc
     * 
     * @param danhSachPhong Danh sách ChiTietPhieuDatPhong cần nhận
     * @return true nếu tất cả thành công, false nếu có lỗi
     */
    public boolean nhanNhieuPhong(List<ChiTietPhieuDatPhong> danhSachPhong) {
        if (danhSachPhong == null || danhSachPhong.isEmpty()) {
            return false;
        }

        boolean tatCaThanhCong = true;
        for (ChiTietPhieuDatPhong ctpdp : danhSachPhong) {
            boolean ketQua = nhanPhong(
                    ctpdp.getPhieuDatPhong().getMaPhieuDatPhong(),
                    ctpdp.getPhong().getMaPhong());
            if (!ketQua) {
                tatCaThanhCong = false;
            }
        }

        return tatCaThanhCong;
    }
}
