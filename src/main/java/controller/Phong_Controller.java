package controller;

import java.util.ArrayList;
import java.util.List;

import dao.Phong_DAO;
import model.Phong;

public class Phong_Controller {
    private Phong_DAO phong_DAO = new Phong_DAO();

    public Phong_Controller() {

    }

    public boolean capNhatTrangThaiPhong(String maPhong, String trangThaiMoi) {
        return phong_DAO.capNhatTrangThaiPhong(maPhong, trangThaiMoi);
    }

    public List<Phong> getDsPhongTheoTrangThai(String trangThai) {
        List<Phong> dsPhong = phong_DAO.getPhongTheoTrangThai(trangThai);

        return dsPhong;
    }

    public Phong getPhongTheoSoPhong(String soPhong) {
        Phong p_ketQua = null;
        for (Phong p : phong_DAO.getTatCaPhong()) {
            if (p.getSoPhong().trim().equalsIgnoreCase(soPhong)) {
                p_ketQua = p;
            }
        }
        if (p_ketQua != null)

        {
            return p_ketQua;
        } else {
            System.out.println("Không tìm thấy mã phòng");
            return null;
        }
    }

    public List<Phong> getDsachPhong_TrangTimKiem() {
        List<Phong> dsachPhong = phong_DAO.getTatCaPhong();
        return dsachPhong;
    }

    public List<Phong> getDsachPhongTheoThoiGian(String tenLoaiPhong, String thoiGianCheckIn, String thoiGianCheckOut) {
        List<Phong> dsachPhongTheoThoiGian = phong_DAO.timKiemPhongTheoThoiGian(tenLoaiPhong, thoiGianCheckIn,
                thoiGianCheckOut);
        return dsachPhongTheoThoiGian;
    }

    public List<String> getDsTang() {
        List<String> ketQua = new ArrayList<>();
        for (Phong p : phong_DAO.getTatCaPhong()) {
            String soTang = String.valueOf(p.getTang());
            ketQua.add(soTang);
        }
        return ketQua;
    }
}