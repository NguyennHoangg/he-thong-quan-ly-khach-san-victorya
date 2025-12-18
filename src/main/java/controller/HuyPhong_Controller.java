package controller;

import java.time.LocalDate;
import java.util.List;

import dao.HuyPhong_DAO;
import model.PhieuHuyPhong;

public class HuyPhong_Controller {

    private final HuyPhong_DAO hp_dao;

    public HuyPhong_Controller() {
        hp_dao = new HuyPhong_DAO();
    }

    public List<PhieuHuyPhong> layDsHuyPhong() {
        return hp_dao.layDsPhieuHuyPhongTheoNgay(LocalDate.now());
    }

    public List<PhieuHuyPhong> layDsHuyPhongTheoKhoangNgay(LocalDate tuNgay, LocalDate denNgay) {

        if (tuNgay == null && denNgay == null) {
            return List.of();
        }

        if (tuNgay == null) {
            tuNgay = denNgay;
        }

        if (denNgay == null) {
            denNgay = tuNgay;
        }

        return hp_dao.layDsPhieuHuyPhongTheoKhoangNgay(tuNgay, denNgay);
    }
}
