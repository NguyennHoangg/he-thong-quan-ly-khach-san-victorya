package dao;

import model.ChiTietPhieuDatPhong;
import model.HuyPhong;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HuyPhong_DAO {
    public HuyPhong_DAO() {}
    public boolean themHuyPhong(List<ChiTietPhieuDatPhong> dsPhieuDatPhong, String lyDo, LocalDate ngayHuy) { return true; }
    public List<HuyPhong> getTatCaHuyPhong() { return new ArrayList<>(); }
}
