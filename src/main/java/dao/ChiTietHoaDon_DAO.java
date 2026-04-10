package dao;

import model.ChiTietHoaDon;
import model.ChiTietHoaDonDichVu;
import model.HoaDon;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDon_DAO {
    public List<ChiTietHoaDon> findByMaHoaDon(String maHoaDon) { return new ArrayList<>(); }
    public List<ChiTietHoaDonDichVu> findDichVuByMaHDAndMaPDP(String maHoaDon, String maPhieuDatPhong) { return new ArrayList<>(); }
    public boolean themChiTietHoaDon(ChiTietHoaDon chiTietHoaDon) { return true; }
    public boolean themChiTietHoaDonDichVu(String maHoaDon, String maPhieuDatPhong, String maDichVu) { return true; }
    public boolean luuHoaDonDayDu(HoaDon hoaDon) { return true; }
}
