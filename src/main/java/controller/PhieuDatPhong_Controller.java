package controller;

import dao.PhieuDatPhong_DAO;
import model.PhieuDatPhong;

public class PhieuDatPhong_Controller {
    private static PhieuDatPhong_DAO phieuDatPhongDAO = new PhieuDatPhong_DAO();
    
    /**
     * Tạo mã phiếu đặt phòng tự động
     * Format: PDP{DDMMYYYY}XXX
     * - PDP: Phiếu đặt phòng
     * - {DDMMYYYY}: Ngày đặt phòng
     * - XXX: Số thứ tự trong ngày (001, 002, ...), reset về 001 mỗi ngày mới
     * VD: PDP25122024001, PDP25122024002, ...
     */
    public static String generateMaPhieuDatPhong(java.time.LocalDate ngayDatPhong) {
        if (ngayDatPhong == null) {
            ngayDatPhong = java.time.LocalDate.now();
        }
        
        // Format ngày thành DDMMYYYY
        String ngayStr = ngayDatPhong.format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));
        
        // Lấy mã cuối cùng của ngày hiện tại
        String maCuoi = phieuDatPhongDAO.getMaPhieuDatPhongCuoiCungTheoNgay(ngayDatPhong);
        
        int soThuTu = 1;
        if (maCuoi != null && !maCuoi.isEmpty() && maCuoi.startsWith("PDP" + ngayStr)) {
            try {
                // Lấy phần số thứ tự (3 chữ số cuối)
                soThuTu = Integer.parseInt(maCuoi.substring(maCuoi.length() - 3));
                soThuTu++;
            } catch (Exception e) {
                e.printStackTrace();
                soThuTu = 1;
            }
        }
        
        // Format: PDP + DDMMYYYY + XXX
        return String.format("PDP%s%03d", ngayStr, soThuTu);
    }
    
    /**
     * Tạo phiếu đặt phòng mới
     * Validate và xử lý business logic trước khi lưu
     */
    public static boolean taoPhieuDatPhong(PhieuDatPhong phieuDatPhong) {
        // Validate
        if (phieuDatPhong == null) {
            return false;
        }
        
        if (phieuDatPhong.getKhachHang() == null) {
            System.err.println("Lỗi: Khách hàng không được null");
            return false;
        }
        
        if (phieuDatPhong.getDsachPhieuDatPhong() == null || 
            phieuDatPhong.getDsachPhieuDatPhong().isEmpty()) {
            System.err.println("Lỗi: Phải có ít nhất 1 phòng");
            return false;
        }
        
        // Lưu vào database
        return phieuDatPhongDAO.taoPhieuDatPhong(phieuDatPhong);
    }
    
    /**
     * Lấy phiếu đặt phòng theo CCCD khách hàng
     */
    public static PhieuDatPhong getPhieuDatPhongTheoCCCD(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) {
            return null;
        }
        return phieuDatPhongDAO.getPhieuDatPhongTheoCCCD(cccd);
    }
}
