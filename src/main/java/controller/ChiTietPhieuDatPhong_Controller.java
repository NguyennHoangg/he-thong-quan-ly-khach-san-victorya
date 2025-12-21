package controller;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import dao.ChiTietPhieuDatPhong_DAO;
import dao.HuyPhong_DAO;
import dao.PhieuDatPhong_DAO;
import dao.Phong_DAO;
import model.ChiTietPhieuDatPhong;
import model.PhieuDatPhong;
import model.Phong;

public class ChiTietPhieuDatPhong_Controller {
    Phong_DAO phong_dao = new Phong_DAO();
    ChiTietPhieuDatPhong_DAO cTietPhieuDatPhong_dao = new ChiTietPhieuDatPhong_DAO();
    PhieuDatPhong_DAO pdp_dao = new PhieuDatPhong_DAO();
    List<ChiTietPhieuDatPhong> dsPhongChonHuy = new ArrayList<>();
    HuyPhong_DAO huyPhong_dao = new HuyPhong_DAO();

    public double tinhTienCoc(double thanhTien) {
        return thanhTien * 0.3;
    }

    // @formatter:off
    // Chọn phòng cần Hủy -> Nhấn xác nhận -> hiện Modal kiểm tra thông tin, tổng tiền, tiền khách đã cọc, tiền phải hoàn
    // trả cho khách nếu có -> Nhấn xác nhận -> Thêm Object HuyPhong vào database, xóa ChiTietPhieuDatPhong vừa được tạo có liên quan
    // đến phòng vừa đặt -> Nếu phiếu có nhiều phòng thì dừng lại, hiển thị Hủy phòng thành công
    //                   |
    //                    -> Chỉ có 1 phòng thì Phiếu đặt phòng đó không còn thông tin chi tiết nào cả, phải set trạng thái thành "Đã hủy"
    // @formatter:on
    public boolean themHuyPhong(List<ChiTietPhieuDatPhong> dsHuy, String lyDo) {
        LocalDate ngayHuy = LocalDate.now();
        for (ChiTietPhieuDatPhong ct : dsHuy) {
            PhieuDatPhong tam = ct.getPhieuDatPhong();
            phong_dao.capNhatTrangThaiPhong(ct.getPhong().getMaPhong(), "Trống");
            // cập nhật trạng thái phiếu đặt phòng
            cTietPhieuDatPhong_dao.xoaChiTietPhieuDatPhongTheoMa(ct);
            int soLuong = cTietPhieuDatPhong_dao.demChiTiet(ct);
            if (soLuong < 1) {
                pdp_dao.capNhatPhieuDatPhongTheoMa(tam, "Đã hủy");
            }
            tam = null;
        }
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
            // Giờ đầu tiên giảm 30% giá phòng
            return giaPhong * 0.7;
        } else if (thoiGianThue <= 12) {
            // Giờ thứ 2 đổ lên giảm 10% giá phòng
            return giaPhong * 0.9 * thoiGianThue;
        } else {
            // qua 12h thì tính theo giờ giá phòng/h
            return thoiGianThue * giaPhong;
        }
    }

    public double tinhChenhLech(double tien1, double tien2) {
        return tien2 - tien1;
    }

    public String tinhNgay(int gio) {
        int ngay = gio / 24;
        int gioLe = gio % 24;

        if (gio < 24) {
            return String.format("%d giờ", gio);
        } else {
            if (gioLe > 0) {
                return String.format("%d ngày, %d giờ", ngay, gioLe);
            } else {
                return String.format("%d ngày", ngay);
            }
        }

    }

    public List<ChiTietPhieuDatPhong> getDsPhongTheoTrangThai(String trangThai, String tinhTrang) {
        List<ChiTietPhieuDatPhong> dsKetQua = new ArrayList<>();
        for (ChiTietPhieuDatPhong ctpdp : cTietPhieuDatPhong_dao.getDsPhieuDatPhongTheoTrangThai(trangThai,
                tinhTrang)) {
            ChiTietPhieuDatPhong ctpdpMoi = new ChiTietPhieuDatPhong(
                    ctpdp.getPhieuDatPhong(),
                    ctpdp.getLoaiDatPhong(),
                    ctpdp.getDsachDichVu(),
                    ctpdp.getSoGioLuuTru(),
                    ctpdp.getThoiGianNhanPhong(),
                    ctpdp.getThoiGianTraPhong(),
                    ctpdp.getPhong(),
                    ctpdp.getSoNguoi(),
                    tinhThanhTien(ctpdp.getPhong().getLoaiPhong().getGia(),
                            ctpdp.getPhong().getLoaiPhong().getTenLoaiPhong(),
                            ctpdp.getSoGioLuuTru())

            );
            dsKetQua.add(ctpdpMoi);
        }
        return dsKetQua;
    }

    public List<ChiTietPhieuDatPhong> getChiTietPhieuDatPhongTheoCCCD(
            String cccd, List<ChiTietPhieuDatPhong> dsChiTietCanTim) {

        List<ChiTietPhieuDatPhong> dsKetQua = new ArrayList<>();
        for (ChiTietPhieuDatPhong ct : dsChiTietCanTim) {
            if (ct.getPhieuDatPhong() != null &&
                    ct.getPhieuDatPhong().getKhachHang() != null &&
                    cccd.equalsIgnoreCase(ct.getPhieuDatPhong().getKhachHang().getCCCD())) {
                dsKetQua.add(ct);
            }
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

    public List<ChiTietPhieuDatPhong> layDanhSachPhongDaLoc(
            String trangThai,
            String tinhTrang,
            String timKiem // số phòng hoặc CCCD hoặc rỗng
    ) {

        List<ChiTietPhieuDatPhong> dsGoc = getDsPhongTheoTrangThai(trangThai, tinhTrang);

        if (timKiem == null || timKiem.trim().isEmpty()) {
            return dsGoc;
        }

        timKiem = timKiem.trim();

        List<ChiTietPhieuDatPhong> dsKetQua = new ArrayList<>();

        for (ChiTietPhieuDatPhong ct : dsGoc) {
            if (ct.getPhong().getSoPhong().equalsIgnoreCase(timKiem)) {
                dsKetQua.add(ct);
                return dsKetQua;
            }
        }

        if (timKiem.matches("\\d{12}")) {
            for (ChiTietPhieuDatPhong ct : dsGoc) {
                if (ct.getPhieuDatPhong() != null
                        && ct.getPhieuDatPhong().getKhachHang() != null
                        && timKiem.equals(ct.getPhieuDatPhong().getKhachHang().getCCCD())) {
                    dsKetQua.add(ct);
                }
            }
            return dsKetQua;
        }

        return dsKetQua;
    }

    public boolean doiPhong(ChiTietPhieuDatPhong ctpdpCu, Phong phongMoi) {
        if (cTietPhieuDatPhong_dao.doiPhong(ctpdpCu, phongMoi))
            return true;
        return false;
    }

}
