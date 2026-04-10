package dao;

import model.ChiTietPhieuDatPhong;
import model.PhieuDatPhong;
import java.util.ArrayList;
import java.util.List;

public class PhieuDatPhong_DAO {
    public List<PhieuDatPhong> getTatCaPhieuDatPhong() { return new ArrayList<>(); }
    public PhieuDatPhong getPhieuDatPhongTheoCCCD(String cccd) { return null; }
    public PhieuDatPhong getPhieuDatPhongChuaNhanTheoCCCD(String cccd) { return null; }
    public boolean themPhieuDatPhong(PhieuDatPhong phieuDatPhong) { return true; }
    public List<ChiTietPhieuDatPhong> layTatCaPhongChoNhanTheoThoiGian() { return new ArrayList<>(); }
    public List<ChiTietPhieuDatPhong> layTatCaPhongDangOTheoThoiGian() { return new ArrayList<>(); }
    public List<ChiTietPhieuDatPhong> layPhongChoNhanTheoSoDienThoai(String soDienThoai) { return new ArrayList<>(); }
    public List<ChiTietPhieuDatPhong> layPhongDangOTheoSoDienThoai(String soDienThoai) { return new ArrayList<>(); }
    public List<ChiTietPhieuDatPhong> layTatCaPhongDaDatTheoSoDienThoai(String soDienThoai) { return new ArrayList<>(); }
    public boolean capNhatPhieuDatPhongTheoMa(PhieuDatPhong phieuDatPhong, String trangThaiMoi) { return true; }
    public long getNextSequenceNumber(String dateString) { return 1L; }
    public boolean capNhatTrangThai(String maPhieuDatPhong, String trangThaiMoi) { return true; }
}
