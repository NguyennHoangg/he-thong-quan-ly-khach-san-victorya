package controller;

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
}
