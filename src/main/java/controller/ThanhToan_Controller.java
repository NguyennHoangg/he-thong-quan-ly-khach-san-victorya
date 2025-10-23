package controller;

import dao.PhieuDatPhong_DAO;
import model.PhieuDatPhong;

public class ThanhToan_Controller {
    private PhieuDatPhong_DAO phieuDatPhong_DAO = new PhieuDatPhong_DAO();

    
    public PhieuDatPhong getPhieuDatPhongTheoCCCD(String CCCD){
        return phieuDatPhong_DAO.getPhieuDatPhongTheoCCCD(CCCD);
    }
}
