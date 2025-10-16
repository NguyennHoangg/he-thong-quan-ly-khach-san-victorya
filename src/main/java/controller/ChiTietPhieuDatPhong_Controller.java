package controller;

import java.util.ArrayList;
import java.util.List;

import dao.ChiTietPhieuDatPhong_DAO;
import dao.Phong_DAO;
import model.ChiTietPhieuDatPhong;
import model.Phong;

public class ChiTietPhieuDatPhong_Controller {
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

    public List<Phong> getDsachPhong_TrangTimKiem() {
        List<Phong> dsachPhong = phong_dao.getTatCaPhong();
        return dsachPhong;
    }

    public String tinhNgay(int gio) {
        int ngayDem = gio / 24;
        int gioLe = gio % 24;
        String thoiGian = "";

        if (ngayDem > 0) {
            // Nối chuỗi cho phần "ngày đêm"
            thoiGian = ngayDem + " ngày " + ngayDem + " đêm";

            // Thêm dấu phẩy nếu có giờ lẻ
            if (gioLe > 0) {
                thoiGian = thoiGian + ", ";
            }
        }

        if (gioLe > 0) {
            // Nối chuỗi cho phần "giờ lẻ"
            thoiGian = thoiGian + gioLe + " giờ";
        }

        // Trường hợp dưới 24 giờ (chỉ có giờ lẻ)
        if (ngayDem == 0 && gioLe > 0) {
            // Trường hợp này đã được xử lý bởi khối if (gioLe > 0) ở trên
            // nhưng để đảm bảo logic gọn nhất, ta có thể viết như sau:
            if (thoiGian.isEmpty()) {
                thoiGian = gioLe + " giờ";
            }
        }

        return thoiGian;
    }

    public List<ChiTietPhieuDatPhong> getDsPhongTheoTrangThai(String trangThai) {
        List<ChiTietPhieuDatPhong> dsKetQua = new ArrayList<>();
        for (ChiTietPhieuDatPhong ctpdp : cTietPhieuDatPhong_dao.getDsChiTietPhieuDatPhong()) {
            for (Phong p : phong_dao.getPhongTheoTrangThai(trangThai)) {
                if (ctpdp.getPhong() != null && p.getMaPhong().equals(ctpdp.getPhong().getMaPhong())) {
                    ChiTietPhieuDatPhong ctpdpMoi = new ChiTietPhieuDatPhong(
                            ctpdp.getPhieuDatPhong(),
                            ctpdp.getLoaiDatPhong(),
                            ctpdp.getDsachDichVu(),
                            ctpdp.getSoGioLuuTru(),
                            ctpdp.getThoiGianNhanPhong(),
                            ctpdp.getThoiGianTraPhong(),
                            p,
                            ctpdp.getSoNguoi(),
                            tinhNgay(ctpdp.getSoGioLuuTru()),
                            tinhThanhTien(p.getLoaiPhong().getGia(), p.getLoaiPhong().getTenLoaiPhong(),
                                    ctpdp.getSoGioLuuTru())

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
