package controller;

import java.util.ArrayList;
import java.util.List;

import dao.LoaiPhong_DAO;
import model.LoaiPhong;

public class LoaiPhong_Controller {
    private LoaiPhong_DAO lp_dao = new LoaiPhong_DAO();

    public LoaiPhong_Controller() {
    };

    public List<String> getDsTenLoaiPhong() {
        List<String> ketQua = new ArrayList<>();
        for (LoaiPhong lp : lp_dao.getDsLoaiPhong()) {
            ketQua.add(lp.getTenLoaiPhong());
        }
        return ketQua;
    }
}
