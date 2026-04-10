package dao;

import model.LoaiPhong;
import model.Phong;
import java.util.ArrayList;
import java.util.List;

public class QuanLiPhong_DAO {
    public String getNextMaPhong() { return "P001"; }
    public List<LoaiPhong> findAllRoomTypes() { return new ArrayList<>(); }
    public List<Phong> findAll() { return new ArrayList<>(); }
    public Phong findById(String id) { return null; }
    public boolean existsBySoPhong(String soPhong) { return false; }
    public boolean existsBySoPhongExcludingId(String soPhong, String id) { return false; }
    public boolean insert(Phong p) { return true; }
    public boolean update(Phong p) { return true; }
    public boolean deleteById(String id) { return true; }
    public int deleteMany(List<String> ids) { return ids != null ? ids.size() : 0; }
    public List<Phong> search(String keyword, String maLoaiPhong, String trangThai, Integer tang) { return new ArrayList<>(); }
}
