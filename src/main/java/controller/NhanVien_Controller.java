package controller;

import java.util.ArrayList;
import java.util.List;

import dao.NhanVien_DAO;
import model.NhanVien;

public class NhanVien_Controller {
    private NhanVien_DAO nv_dao = new NhanVien_DAO();

    public NhanVien_Controller() {
    }

    public List<NhanVien> getDsNhanVien() {
        return nv_dao.getDsNhanVien();
    }

    public List<String> getDsVaiTroNhanVien() {
        List<String> dsVaiTro = new ArrayList<>();
        for (NhanVien nv : nv_dao.getDsNhanVien()) {
            if (!dsVaiTro.contains(nv.getTaiKhoan().getVaiTro())) {
                dsVaiTro.add(nv.getTaiKhoan().getVaiTro());
            }
        }
        dsVaiTro.add(0, "Tất cả");
        return dsVaiTro;
    }

    public boolean themNhanVien(NhanVien nv) {
        return nv_dao.themNhanVien(nv);
    }
}
