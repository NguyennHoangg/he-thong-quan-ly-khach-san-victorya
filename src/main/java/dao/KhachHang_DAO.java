package dao;

import model.KhachHang;
import java.util.ArrayList;
import java.util.List;

public class KhachHang_DAO {
    public List<KhachHang> getDsKhachHang() { return new ArrayList<>(); }
    public boolean themKhachHang(KhachHang kh) { return true; }
    public boolean capNhatKhachHang(KhachHang kh) { return true; }
    public boolean xoaKhachHang(String maKhachHang) { return true; }
    public KhachHang timKhachHangTheoCCCD(String cccd) { return null; }
    public int countTongKhachHang() { return 0; }
    public int countKhachHangMoiThangNay() { return 0; }
    public List<KhachHang> timKhachHangTheoCCCDStartsWith(String cccdPrefix) { return new ArrayList<>(); }
    public String phatSinhMaKhachHang() { return "KH001"; }
    public KhachHang timKhachHangTheoMa(String maKhachHang) { return null; }
    public KhachHang timKhachHangTheoEmail(String email) { return null; }
    public KhachHang timKhachHangTheoSoDienThoai(String soDienThoai) { return null; }
}
