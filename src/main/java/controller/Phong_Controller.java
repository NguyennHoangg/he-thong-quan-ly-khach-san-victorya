package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<Integer> getDsTang() {
        List<Integer> ketQua = new ArrayList<>();

        for (Phong p : phong_DAO.getTatCaPhong()) {
            int soTang = p.getTang();
            if (!ketQua.contains(soTang)) {
                ketQua.add(soTang);
            }
        }
        ketQua.add(0, 0);
        // Sắp xếp tăng
        ketQua.sort((a, b) -> a - b);

        return ketQua;
    }

    public List<Phong> locPhong(String trangThai, String loai, int tang) {
        List<Phong> ds = phong_DAO.getPhongTheoTrangThai(trangThai);

        return ds.stream()
                .filter(p -> {
                    boolean hopLoai = loai == null || loai.equalsIgnoreCase("Tất cả")
                            || p.getLoaiPhong().getTenLoaiPhong().equalsIgnoreCase(loai);
                    boolean hopTang = tang == 0 || p.getTang() == tang;
                    return hopLoai && hopTang;
                })
                .collect(Collectors.toList());
    }
}