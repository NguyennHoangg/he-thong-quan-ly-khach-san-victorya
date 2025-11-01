/*
 * @ (#) ChiTietHoaDon_Controller.java     1.0    10/30/2025
 */
package controller;

import dao.ChiTietHoaDon_DAO;
import model.ChiTietHoaDon;
import model.ChiTietHoaDonDichVu;

import java.util.List;

public class ChiTietHoaDon_Controller {
    private final ChiTietHoaDon_DAO dao = new ChiTietHoaDon_DAO();

    /** Lấy các dòng ChiTietHoaDon theo mã hóa đơn */
    public List<ChiTietHoaDon> getByMaHoaDon(String maHoaDon) {
        return dao.findByMaHoaDon(maHoaDon);
    }

    /** Lấy danh sách dịch vụ của một dòng chi tiết (maHD + maPDP) */
    public List<ChiTietHoaDonDichVu> getDichVu(String maHoaDon, String maPhieuDatPhong) {
        return dao.findDichVuByMaHDAndMaPDP(maHoaDon, maPhieuDatPhong);
    }
}
