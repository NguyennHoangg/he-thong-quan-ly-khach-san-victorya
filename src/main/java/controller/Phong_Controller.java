package controller;

import java.util.ArrayList;
import java.util.List;

import dao.ChiTietPhieuDatPhong_DAO;
import dao.Phong_DAO;
import model.ChiTietPhieuDatPhong;
import model.Phong;

public class Phong_Controller {
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

    public List<Object> getDsPhongTheoTrangThai(String trangThai) {
        List<Object> dsKetQua = new ArrayList<>();
        List<Object> ds = phong_dao.getPhongTheoTrangThai(trangThai);

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

    public List<Object[]> getDsPhongTheoTrangThai(String trangThai) {
        List<Object[]> dsKetQua = new ArrayList<>();
        for (ChiTietPhieuDatPhong ctpdp : cTietPhieuDatPhong_dao.getDsChiTietPhieuDatPhong()) {
            for (Phong p : phong_dao.getDsPhongByTrangThai(trangThai)) {
                if (ctpdp.getPhong() != null && p.getMaPhong().equals(ctpdp.getPhong().getMaPhong())) {
                    Object[] obj = {
                            p.getMaPhong(), // 0
                            p.getSoPhong(), // 1
                            p.getLoaiPhong().getTenLoaiPhong(), // 2
                            p.getLoaiPhong().getGia(), // 3
                            p.getTang(), // 4
                            tinhNgay(ctpdp.getSoGioLuuTru()), // 5
                            ctpdp.getThoiGianNhanPhong(), // 6
                            ctpdp.getThoiGianTraPhong(), // 7
                            ctpdp.getSoNguoi(), // 8
                            tinhThanhTien(p.getLoaiPhong().getGia(), p.getLoaiPhong().getTenLoaiPhong(),
                                    ctpdp.getSoGioLuuTru())// 9

                    };
                    dsKetQua.add(obj);
                }
            }
        }
        if (dsKetQua.isEmpty()) {
            System.out.println("Khong co danh sach");
        }
        return dsKetQua;
    }
}
