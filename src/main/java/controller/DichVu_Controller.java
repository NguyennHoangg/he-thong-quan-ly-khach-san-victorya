package controller;

import java.util.List;

import dao.DichVu_DAO;
import model.DichVu;

public class DichVu_Controller {
    private DichVu_DAO dv_dao = new DichVu_DAO();

    public DichVu_Controller() {
    }

    public List<DichVu> getDsDichVu() {
        return dv_dao.getDsDichVu();
    }
}
