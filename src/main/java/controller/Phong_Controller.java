package controller;

import java.util.List;

import dao.Phong_DAO;
import model.Phong;

public class Phong_Controller {
    private Phong_DAO phong_DAO = new Phong_DAO();


    public List<Phong> getDsachPhong_TrangTimKiem() {
        List<Phong> dsachPhong = phong_DAO.getTatCaPhong();
        return dsachPhong;
    }

    public List<Phong> getDsachPhongTheoThoiGian(String tenLoaiPhong, String thoiGianCheckIn, String thoiGianCheckOut){
        List<Phong> dsachPhongTheoThoiGian = phong_DAO.timKiemPhongTheoThoiGian(tenLoaiPhong, thoiGianCheckIn, thoiGianCheckOut);
        return dsachPhongTheoThoiGian;
    }

}
