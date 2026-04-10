package dao;

import model.ChiTietPhieuDatPhong;
import model.Phong;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChiTietPhieuDatPhong_DAO {
    public ChiTietPhieuDatPhong_DAO() {}
    public List<ChiTietPhieuDatPhong> getDsChiTietPhieuDatPhong() { return new ArrayList<>(); }
    public List<ChiTietPhieuDatPhong> getDatPhongHienTaiTheoCCCD(String cccd) { return new ArrayList<>(); }
    public boolean giaHanDen(String maPhieuDatPhong, String maPhong, LocalDateTime thoiGianTraPhongMoi) { return true; }
    public boolean capNhatThoiGianNhanPhong(String maPhieuDatPhong, String maPhong, LocalDateTime thoiGianNhanPhong) { return true; }
    public boolean capNhatTrangThai(String maPhieuDatPhong, String maPhong, String trangThai) { return true; }
    public List<ChiTietPhieuDatPhong> getDsPhieuDatPhongTheoTrangThai(String trangThai, String tinhTrang) { return new ArrayList<>(); }
    public boolean doiPhong(ChiTietPhieuDatPhong ctpdpCu, Phong phongMoi) { return true; }
    public boolean xoaChiTietPhieuDatPhongTheoMa(ChiTietPhieuDatPhong ctpdp) { return true; }
    public List<ChiTietPhieuDatPhong> layTatCaPhongDangO() { return new ArrayList<>(); }
    public List<ChiTietPhieuDatPhong> getDatPhongHienTaiTheoSoDienThoai(String soDienThoai) { return new ArrayList<>(); }
    public LocalDateTime layThoiGianDatPhongTiepTheo(String maPhong, LocalDateTime thoiGianTraPhongHienTai) { return null; }
    public int demChiTiet(ChiTietPhieuDatPhong chiTiet) { return 0; }
    public int demCheckInHomNay() { return 0; }
    public int demCheckOutHomNay() { return 0; }
    public int demPhongSapTraTrong24h() { return 0; }
}
