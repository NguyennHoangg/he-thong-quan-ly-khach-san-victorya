package controller;

import java.util.List;

import dao.KhachHang_DAO;
import model.KhachHang;

public class KhachHang_Controller {
    private static KhachHang_DAO khachHang_DAO = new KhachHang_DAO();

    public static List<KhachHang> getDsachKH(String CCCD){
        List<KhachHang> dsachKH = khachHang_DAO.timKiemKhachHangTheoCCCD(CCCD);
        return dsachKH;
    }
    
    /**
     * Tạo mã khách hàng tự động
     * Logic: Lấy count từ DB, tăng lên 1
     * Format: KH + 4 chữ số (VD: KH0001, KH0002, ...)
     */
    public static String generateMaKhachHang() {
        try {
            // Lấy số lượng khách hàng hiện có từ DB
            int count = khachHang_DAO.getCountKhachHang();
            count++;
            // Format thành KH + 4 chữ số
            return String.format("KH%04d", count);
        } catch (Exception e) {
            e.printStackTrace();
            return "KH0001"; // Nếu có lỗi, trả về mã đầu tiên
        }
    }
    
    /**
     * Tìm khách hàng theo CCCD chính xác
     */
    public static KhachHang timKhachHangTheoCCCD(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) {
            return null;
        }
        return khachHang_DAO.timKhachHangTheoCCCD(cccd);
    }
    
    /**
     * Thêm khách hàng mới
     * Validate trước khi thêm
     */
    public static boolean themKhachHang(KhachHang khachHang) {
        // Validate
        if (khachHang == null) {
            return false;
        }
        
        if (khachHang.getCCCD() == null || khachHang.getCCCD().trim().isEmpty()) {
            System.err.println("Lỗi: CCCD không được rỗng");
            return false;
        }
        
        if (khachHang.getTenKhachHang() == null || khachHang.getTenKhachHang().trim().isEmpty()) {
            System.err.println("Lỗi: Tên khách hàng không được rỗng");
            return false;
        }
        
        return khachHang_DAO.themKhachHang(khachHang);
    }
}
