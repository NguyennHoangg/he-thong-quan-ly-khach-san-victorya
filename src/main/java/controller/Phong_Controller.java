package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dao.PhieuDatPhong_DAO;
import dao.Phong_DAO;
import model.PhieuDatPhong;
import model.Phong;

public class Phong_Controller {
    private Phong_DAO phong_DAO = new Phong_DAO();
    private PhieuDatPhong_DAO phieuDatPhong_DAO = new PhieuDatPhong_DAO();

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

    /**
     * Lấy danh sách phòng TRỐNG theo thời gian check-in và check-out
     * 
     * @param tenLoaiPhong Tên loại phòng (VIP/Thường) hoặc null
     * @param thoiGianCheckIn Thời gian check-in (yyyy-MM-dd HH:mm:ss)
     * @param thoiGianCheckOut Thời gian check-out (yyyy-MM-dd HH:mm:ss)
     * @return Danh sách phòng trống trong khoảng thời gian
     */
    public List<Phong> getDsachPhongTrongTheoThoiGian(String tenLoaiPhong, String thoiGianCheckIn, String thoiGianCheckOut) {
        return phong_DAO.timKiemPhongTrongTheoThoiGian(tenLoaiPhong, thoiGianCheckIn, thoiGianCheckOut);
    }

    /**
     * @deprecated Sử dụng getDsachPhongTrongTheoThoiGian() thay thế
     */
    @Deprecated
    public List<Phong> getDsachPhongTheoThoiGian(String tenLoaiPhong, String thoiGianCheckIn, String thoiGianCheckOut) {
        return getDsachPhongTrongTheoThoiGian(tenLoaiPhong, thoiGianCheckIn, thoiGianCheckOut);
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

    public boolean taoPhieuDatPhong(PhieuDatPhong phieuDatPhong){
        return phieuDatPhong_DAO.taoPhieuDatPhong(phieuDatPhong);
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