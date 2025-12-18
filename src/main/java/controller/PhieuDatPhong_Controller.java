package controller;

import dao.PhieuDatPhong_DAO;
import model.PhieuDatPhong;

public class PhieuDatPhong_Controller {
    private static PhieuDatPhong_DAO phieuDatPhongDAO = new PhieuDatPhong_DAO();
    
    /**
     * Tạo mã phiếu đặt phòng tự động
     * Format: PDP-DDMMYYYY-XXX
     * - PDP: Phiếu đặt phòng
     * - DDMMYYYY: Ngày đặt phòng
     * - XXX: Số thứ tự trong ngày (001, 002, ...), reset về 001 mỗi ngày mới
     * VD: PDP-25122024-001, PDP-25122024-002, ...
     */
    public static String generateMaPhieuDatPhong(java.time.LocalDate ngayDatPhong) {
        if (ngayDatPhong == null) {
            ngayDatPhong = java.time.LocalDate.now();
        }
        
        String dateString = ngayDatPhong.format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));
        long sequenceNumber = phieuDatPhongDAO.getNextSequenceNumber(dateString);
        
        return "PDP-" + dateString + "-" + String.format("%03d", sequenceNumber);
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

    /**
     * Thêm phiếu đặt phòng mới
     * @param phieuDatPhong Phiếu đặt phòng cần thêm
     * @return true nếu thêm thành công, false nếu thất bại
     */
    public static boolean themPhieuDatPhong(PhieuDatPhong phieuDatPhong) {
        if (phieuDatPhong == null) {
            return false;
        }
        return phieuDatPhongDAO.themPhieuDatPhong(phieuDatPhong);
    }
}
