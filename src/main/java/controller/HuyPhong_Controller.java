package controller;

import java.util.ArrayList;
import java.util.List;

import dao.ChiTietPhieuDatPhong_DAO;
import dao.Phong_DAO;
import model.ChiTietPhieuDatPhong;
import model.Phong;

public class HuyPhong_Controller {
    Phong_DAO phong_dao = new Phong_DAO();
    ChiTietPhieuDatPhong_DAO cTietPhieuDatPhong_dao = new ChiTietPhieuDatPhong_DAO();

    public double tinhThanhTien(double giaPhong, String tenLoaiDatPhong, double thoiGianThue) {
        if (thoiGianThue <= 1) {
            return giaPhong;
        } else if (thoiGianThue < 24) {
            return giaPhong + (thoiGianThue - 1) * (giaPhong * 0.5);
        } else {
            // qua 24h thì tính theo ngày (1 ngày = giá phòng đầy đủ)
            double soNgay = Math.ceil(thoiGianThue / 24.0);
            return soNgay * giaPhong;
        }
    }

    public List<Phong> getDsachPhong_TrangTimKiem(){
        List<Phong> dsachPhong = phong_dao.getTatCaPhong();
        return dsachPhong;
    }

    
    public List<ChiTietPhieuDatPhong> getDsPhongTheoTrangThai(String trangThai) {
        List<ChiTietPhieuDatPhong> dsKetQua = new ArrayList<>();
        for (ChiTietPhieuDatPhong ctpdp : cTietPhieuDatPhong_dao.getDsChiTietPhieuDatPhong()) {
            for (ChiTietPhieuDatPhong p : phong_dao.getPhongTheoTrangThai(trangThai)) {
                if (ctpdp.getPhong() != null && p.getPhong().getMaPhong().equals(ctpdp.getPhong().getMaPhong())) {
                    ChiTietPhieuDatPhong ctpdpMoi = new ChiTietPhieuDatPhong(
                            ctpdp.getPhieuDatPhong(),
                            ctpdp.getLoaiDatPhong(),
                            ctpdp.getDsachDichVu(),
                            ctpdp.getSoGioLuuTru(),
                            ctpdp.getThoiGianNhanPhong(),
                            ctpdp.getThoiGianTraPhong(),
                            p.getPhong(),
                            ctpdp.getSoNguoi()
                    );
                    dsKetQua.add(ctpdpMoi);
                }
            }
        }
        if (dsKetQua.isEmpty()) {
            System.out.println("Khong co danh sach");
        }
        return dsKetQua;
    }
}
