package dao;

import model.HoaDon;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HoaDon_DAO {
    public List<HoaDon> getAll() { return new ArrayList<>(); }
    public HoaDon findById(String maHoaDon) { return null; }
    public List<HoaDon> timKiem(String tuKhoa, String trangThai, LocalDate tuNgay, LocalDate denNgay) { return new ArrayList<>(); }
    public int getNextSequenceByDate(LocalDate date) { return 1; }
    public boolean themHoaDon(HoaDon hoaDon) { return true; }
    public int countHoaDonDangCho() { return 0; }
}
