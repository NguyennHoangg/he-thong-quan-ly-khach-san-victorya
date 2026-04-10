package dao;

import model.CaLamViecNhanVien;
import java.util.ArrayList;
import java.util.List;

public class CaLamViecNhanVien_DAO {
    public boolean moCaLamViec(String maNhanVien, double tienMoCa) { return true; }
    public boolean ketCaLamViec(String maCaLamViec, double tienKetCa) { return true; }
    public CaLamViecNhanVien getCaLamViecDangMoByNhanVien(String maNhanVien) { return null; }
    public CaLamViecNhanVien getCaLamViecByMa(String maCaLamViec) { return null; }
    public List<CaLamViecNhanVien> getAllCaLamViecByNhanVien(String maNhanVien) { return new ArrayList<>(); }
    public boolean capNhatCaLamViec(CaLamViecNhanVien ca) { return true; }
}
