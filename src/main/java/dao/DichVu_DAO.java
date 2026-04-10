package dao;

import model.DichVu;
import java.util.ArrayList;
import java.util.List;

public class DichVu_DAO {
    public DichVu_DAO() {}
    public List<DichVu> getDsDichVu() { return new ArrayList<>(); }
    public boolean themDichVu(DichVu dvu) { return true; }
    public boolean capNhatDichVuTheoMa(DichVu dv) { return true; }
    public int getTongSoDichVu() { return 0; }
    public DichVu timDichVuTheoMa(String ma) { return null; }
    public boolean xoaDichVuTheoMa(String maDichVu) { return true; }
}
