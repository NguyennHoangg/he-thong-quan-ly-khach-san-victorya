package controller;

import dao.ChiTietHoaDon_DAO;
import dao.HoaDon_DAO;
import dao.PhieuDatPhong_DAO;
import model.ChiTietHoaDon;
import model.ChiTietPhieuDatPhong;
import model.HoaDon;
import model.PhieuDatPhong;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ThanhToan_Controller {
    private PhieuDatPhong_DAO phieuDatPhong_DAO = new PhieuDatPhong_DAO();
    private HoaDon_DAO hoaDon_DAO = new HoaDon_DAO();
    private ChiTietHoaDon_DAO chiTietHoaDon_DAO = new ChiTietHoaDon_DAO();

    /**
     * Lấy phiếu đặt phòng theo CCCD
     */
    public PhieuDatPhong getPhieuDatPhongTheoCCCD(String CCCD){
        return phieuDatPhong_DAO.getPhieuDatPhongTheoCCCD(CCCD);
    }

    /**
     * Tạo mã hóa đơn tự động theo format: HD-YYYYMMDD-XXXXXXXX
     * @return Mã hóa đơn mới
     */
    public String taoMaHoaDon() {
        LocalDate today = LocalDate.now();
        int sequence = hoaDon_DAO.getNextSequenceByDate(today);
        
        String datePart = String.format("%04d%02d%02d",
                today.getYear(), today.getMonthValue(), today.getDayOfMonth());
        
        return String.format("HD-%s-%08d", datePart, sequence);
    }

    /**
     * Thanh toán và lưu hóa đơn vào database
     * @param hoaDon Hóa đơn cần thanh toán
     * @return true nếu thanh toán thành công, false nếu thất bại
     */
    public boolean thanhToanHoaDon(HoaDon hoaDon) {
        if (hoaDon == null) {
            System.err.println("Hóa đơn null");
            return false;
        }

        try {
            // 1. Kiểm tra và tạo mã hóa đơn nếu chưa có
            if (hoaDon.getMaHoaDon() == null || hoaDon.getMaHoaDon().isEmpty()) {
                hoaDon.setMaHoaDon(taoMaHoaDon());
            }

            // 2. Set thời gian tạo nếu chưa có
            if (hoaDon.getNgayTao() == null) {
                hoaDon.setNgayTao(LocalDateTime.now());
            }

            // 3. Set trạng thái đã thanh toán
            if (hoaDon.getTrangThai() == null || hoaDon.getTrangThai().isEmpty()) {
                hoaDon.setTrangThai("Đã thanh toán");
            }

            // 4. Tạo chi tiết hóa đơn từ phiếu đặt phòng nếu chưa có
            if (hoaDon.getChiTietHoaDon() == null || hoaDon.getChiTietHoaDon().isEmpty()) {
                PhieuDatPhong phieuDatPhong = getPhieuDatPhongTheoCCCD(
                    hoaDon.getKhachHang() != null ? hoaDon.getKhachHang().getCCCD() : null
                );
                
                if (phieuDatPhong != null && phieuDatPhong.getDsachPhieuDatPhong() != null) {
                    for (ChiTietPhieuDatPhong chiTietPDP : phieuDatPhong.getDsachPhieuDatPhong()) {
                        ChiTietHoaDon chiTiet = new ChiTietHoaDon();
                        chiTiet.setHoaDon(hoaDon);
                        chiTiet.setPhieuDatPhong(phieuDatPhong);
                        chiTiet.setNgayTao(LocalDateTime.now());
                        chiTiet.setTongTien(chiTietPDP.tinhThanhTien());
                        
                        // Thêm dịch vụ nếu có - convert từ List<DichVu> sang List<ChiTietHoaDonDichVu>
                        if (chiTietPDP.getDsachDichVu() != null && !chiTietPDP.getDsachDichVu().isEmpty()) {
                            java.util.List<model.ChiTietHoaDonDichVu> dsDichVu = new java.util.ArrayList<>();
                            for (model.DichVu dichVu : chiTietPDP.getDsachDichVu()) {
                                model.ChiTietHoaDonDichVu chiTietDV = new model.ChiTietHoaDonDichVu();
                                chiTietDV.setHoaDon(hoaDon);
                                chiTietDV.setPhieuDatPhong(phieuDatPhong);
                                chiTietDV.setDichVu(dichVu);
                                dsDichVu.add(chiTietDV);
                            }
                            chiTiet.setDichVus(dsDichVu);
                        }
                        
                        hoaDon.getChiTietHoaDon().add(chiTiet);
                    }
                }
            }

            // 5. Lưu hóa đơn đầy đủ vào database với transaction
            boolean success = chiTietHoaDon_DAO.luuHoaDonDayDu(hoaDon);
            
            if (success) {
                System.out.println("✅ Thanh toán thành công - Mã hóa đơn: " + hoaDon.getMaHoaDon());
                
                // 6. Cập nhật trạng thái phiếu đặt phòng
                if (hoaDon.getChiTietHoaDon() != null) {
                    for (ChiTietHoaDon chiTiet : hoaDon.getChiTietHoaDon()) {
                        PhieuDatPhong pdp = chiTiet.getPhieuDatPhong();
                        if (pdp != null) {
                            phieuDatPhong_DAO.capNhatPhieuDatPhongTheoMa(pdp, "Đã thanh toán");
                        }
                    }
                }
            } else {
                System.err.println("Lỗi khi lưu hóa đơn vào database");
            }
            
            return success;
            
        } catch (Exception e) {
            System.err.println("Lỗi thanh toán: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Kiểm tra tính hợp lệ của hóa đơn trước khi thanh toán
     */
    public boolean kiemTraHoaDonHopLe(HoaDon hoaDon) {
        if (hoaDon == null) {
            return false;
        }
        
        if (hoaDon.getKhachHang() == null) {
            System.err.println("Thiếu thông tin khách hàng");
            return false;
        }
        
        if (hoaDon.getTongTien() <= 0) {
            System.err.println("Tổng tiền không hợp lệ");
            return false;
        }
        
        return true;
    }

    /**
     * Tính tổng tiền hóa đơn (bao gồm khuyến mãi nếu có)
     */
    public double tinhTongTienHoaDon(HoaDon hoaDon) {
        if (hoaDon == null || hoaDon.getChiTietHoaDon() == null) {
            return 0;
        }
        
        double tongTien = 0;
        for (ChiTietHoaDon chiTiet : hoaDon.getChiTietHoaDon()) {
            tongTien += chiTiet.getTongTien();
        }
        
        // Áp dụng khuyến mãi nếu có
        if (hoaDon.getKhuyenMai() != null && hoaDon.getKhuyenMai().getHeSo() > 0) {
            tongTien = tongTien * (1 - hoaDon.getKhuyenMai().getHeSo());
        }
        
        return tongTien;
    }
}
