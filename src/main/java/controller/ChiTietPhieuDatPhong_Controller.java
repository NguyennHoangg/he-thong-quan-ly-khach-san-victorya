package controller;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import dao.ChiTietPhieuDatPhong_DAO;
import dao.HuyPhong_DAO;
import dao.Phong_DAO;
import model.ChiTietPhieuDatPhong;
import model.Phong;

public class ChiTietPhieuDatPhong_Controller {
    Phong_DAO phong_dao = new Phong_DAO();
    ChiTietPhieuDatPhong_DAO cTietPhieuDatPhong_dao = new ChiTietPhieuDatPhong_DAO();
    List<ChiTietPhieuDatPhong> dsPhongChonHuy = new ArrayList<>();
    HuyPhong_DAO huyPhong_dao = new HuyPhong_DAO();

    public double tinhTienCoc(double thanhTien) {
        return thanhTien * 0.3;
    }

    public boolean themHuyPhong(List<ChiTietPhieuDatPhong> dsHuy, String lyDo) {
        LocalDate ngayHuy = LocalDate.now();
        return huyPhong_dao.themHuyPhong(dsHuy, lyDo, ngayHuy);
    }

    public double tinhTienHoan(ChiTietPhieuDatPhong ctpdp) {
        if (ctpdp == null || ctpdp.getThoiGianNhanPhong() == null)
            return 0;

        LocalDateTime hienTai = LocalDateTime.now();
        LocalDateTime thoiGianNhan = ctpdp.getThoiGianNhanPhong();

        Duration duration = Duration.between(hienTai, thoiGianNhan);
        long soGioConLai = duration.toHours();

        double giaPhong = ctpdp.getPhong().getLoaiPhong().getGia();
        String tenLoaiDatPhong = ctpdp.getLoaiDatPhong().getMaLoaiDatPhong();
        double thoiGianThue = ctpdp.getSoGioLuuTru();

        double thanhTien = tinhThanhTien(giaPhong, tenLoaiDatPhong, thoiGianThue);
        double tienCoc = tinhTienCoc(thanhTien);

        double tienHoan = 0.0;
        if (soGioConLai >= 72) {
            tienHoan = tienCoc; // 100%
        } else if (soGioConLai >= 24 && soGioConLai < 72) {
            tienHoan = tienCoc * 0.5; // 50%
        } else {
            tienHoan = 0; // Dưới 24h
        }

        return tienHoan;
    }

    public void setDsPhongHuy(List<ChiTietPhieuDatPhong> dsPhongChonHuy) {
        this.dsPhongChonHuy = dsPhongChonHuy;
    }

    public List<ChiTietPhieuDatPhong> getDsPhongHuy() {
        return dsPhongChonHuy;
    }

    public double tinhThanhTien(double giaPhong, String tenLoaiDatPhong, double thoiGianThue) {
        if (thoiGianThue <= 1) {
            return giaPhong;
        } else if (thoiGianThue < 24) {
            return giaPhong + (thoiGianThue - 1) * (giaPhong * 0.5);
        } else {
            // qua 24h thì tính theo ngày (1 ngày = giá phòng đầy đủ)
            // double soNgay = Math.ceil(thoiGianThue / 24.0);
            return thoiGianThue * giaPhong;
        }
    }

    public double tinhChenhLech(double tien1, double tien2) {
        return tien2 - tien1;
    }

    public String tinhNgay(int gio) {
        int ngayDem = gio / 24;
        int gioLe = gio % 24;
        String thoiGian = "";

        if (ngayDem > 0) {
            thoiGian = ngayDem + " ngày " + ngayDem + " đêm";
            if (gioLe > 0) {
                thoiGian += ", " + gioLe + " giờ";
            }
        } else if (gioLe > 0) {
            thoiGian = gioLe + " giờ";
        } else {
            thoiGian = "0 giờ";
        }

        return thoiGian;
    }

    public List<ChiTietPhieuDatPhong> getDsPhongTheoTrangThai(String trangThai) {
        List<ChiTietPhieuDatPhong> dsKetQua = new ArrayList<>();
        for (ChiTietPhieuDatPhong ctpdp : cTietPhieuDatPhong_dao.getDsPhieuDatPhongTheoTrangThai(trangThai)) {
            ChiTietPhieuDatPhong ctpdpMoi = new ChiTietPhieuDatPhong(
                    ctpdp.getPhieuDatPhong(),
                    ctpdp.getLoaiDatPhong(),
                    ctpdp.getDsachDichVu(),
                    ctpdp.getSoGioLuuTru(),
                    ctpdp.getThoiGianNhanPhong(),
                    ctpdp.getThoiGianTraPhong(),
                    ctpdp.getPhong(),
                    ctpdp.getSoNguoi(),
                    tinhNgay(ctpdp.getSoGioLuuTru()),
                    tinhThanhTien(ctpdp.getPhong().getLoaiPhong().getGia(),
                            ctpdp.getPhong().getLoaiPhong().getTenLoaiPhong(),
                            ctpdp.getSoGioLuuTru())

            );
            dsKetQua.add(ctpdpMoi);
        }
        return dsKetQua;
    }

    public ChiTietPhieuDatPhong getChiTietPhieuDatPhongTheoPhong(String maPhong,
            List<ChiTietPhieuDatPhong> dsChiTietCanTim) {
        for (ChiTietPhieuDatPhong ct : dsChiTietCanTim) {
            if (ct.getPhong().getSoPhong().equalsIgnoreCase(maPhong)) {
                return ct;
            }
        }
        return null;
    }

    public boolean doiPhong(ChiTietPhieuDatPhong ctpdpCu, Phong phongMoi) {
        if (cTietPhieuDatPhong_dao.doiPhong(ctpdpCu, phongMoi))
            return true;
        return false;
    }

}
