package dao;

import model.KhuyenMai;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMai_DAO {
    public List<KhuyenMai> getAll() { return new ArrayList<>(); }
    public KhuyenMai findById(String maKM) { return null; }
    public String getNextMaKM() { return "KM001"; }
    public boolean insert(KhuyenMai km) { return true; }
    public boolean update(KhuyenMai km) { return true; }
    public boolean delete(String maKM) { return true; }
    public int deleteMany(List<String> ids) { return ids != null ? ids.size() : 0; }
}
