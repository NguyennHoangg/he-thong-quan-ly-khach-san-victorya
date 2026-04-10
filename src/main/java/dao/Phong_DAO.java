package dao;

import model.Phong;
import java.util.ArrayList;
import java.util.List;

public class Phong_DAO {
    public Phong_DAO() {}
    public int countAll() { return 0; }
    public int countPhongTrong() { return 0; }
    public List<Phong> getTatCaPhong() { return new ArrayList<>(); }
    public boolean capNhatTrangThaiPhong(String maPhong, String trangThaiMoi) { return true; }
    public List<Phong> timKiemPhongTrongTheoThoiGian(String loaiPhong, String thoiGianNhanPhong, String thoiGianTraPhong) { return new ArrayList<>(); }
    public List<Phong> getPhongTheoTrangThai(String trangThai) { return new ArrayList<>(); }
    public List<Phong> getPhongTheoTrangThaiVaLoaiPhong(String trangThai, String loaiPhong) { return new ArrayList<>(); }
    public Phong getPhongTheoMa(String ma) { return null; }
}
