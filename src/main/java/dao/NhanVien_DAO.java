package dao;

import model.NhanVien;
import java.util.ArrayList;
import java.util.List;

public class NhanVien_DAO {
    public List<NhanVien> getDsNhanVien() { return new ArrayList<>(); }
    public NhanVien findByUsername(String tenDangNhap) { return null; }
    public boolean updateProfile(NhanVien nv) { return true; }
    public boolean themNhanVien(NhanVien nv) { return true; }
    public String getMaxMaNhanVien() { return "NV001"; }
    public boolean capNhatNhanVien(NhanVien nv) { return true; }
    public NhanVien timNhanVienTheoCCCD(String cccd) { return null; }
    public boolean xoaNhanVienTheoCCCD(NhanVien nv) { return true; }
    public boolean updatePhone(String maNhanVien, String soDienThoaiMoi) { return true; }
    public boolean updateEmail(String maNhanVien, String emailMoi) { return true; }
}
