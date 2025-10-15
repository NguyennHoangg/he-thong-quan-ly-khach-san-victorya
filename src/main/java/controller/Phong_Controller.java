package controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dao.Phong_DAO;
import model.Phong;

public class Phong_Controller {
    Phong_DAO phong_dao = new Phong_DAO();

    public double tinhThanhTien(double giaPhong, String tenLoaiDatPhong, double thoiGianThue) {
        double thanhTien = 0;

        if (thoiGianThue < 24) {
            // Ví dụ: 1 giờ đầu 60k, mỗi giờ sau +30k
            thanhTien = giaPhong * (1 + (thoiGianThue - 1) * 0.5);
        } else {
            thanhTien = giaPhong;
        }

        return thanhTien;
    }

    public List<Phong> getDsachPhong_TrangTimKiem(){
        List<Phong> dsachPhong = phong_dao.getTatCaPhong();
        return dsachPhong;
    }

    public List<Object> getDsPhongTheoTrangThai(String trangThai) {
        List<Object> dsKetQua = new ArrayList<>();
        List<Object> ds = phong_dao.getPhongTheoTrangThai(trangThai);

        for (Object obj : ds) {
            if (obj instanceof Map<?, ?>) {
                Map<?, ?> record = (Map<?, ?>) obj;

                // Lấy dữ liệu cần thiết
                String tenLoaiPhong = (String) record.get("tenLoaiPhong");
                LocalDateTime gioBatDau = (LocalDateTime) record.get("gioBatDau");
                LocalDateTime gioKetThuc = (LocalDateTime) record.get("gioKetThuc");
                String tenLoaiDatPhong = (String) record.get("loaiDatPhong");
                double giaCoBan = (double) record.get("giaPhong");
                int soNguoi = (Integer) record.get("soNguoi");
                // Tính thời gian thuê (đơn vị: giờ)
                Duration duration = Duration.between(gioBatDau, gioKetThuc);
                double thoiGianThue = duration.toMinutes() / 60.0;
                double thanhTien = tinhThanhTien(giaCoBan, tenLoaiDatPhong, thoiGianThue);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                // Tạo object chứa kết quả
                Object[] recordData = {
                        tenLoaiPhong,
                        gioBatDau.toLocalDate().format(formatter), // Ngày nhận phòng
                        thoiGianThue,
                        thanhTien,
                        soNguoi
                };

                // Thêm vào danh sách
                dsKetQua.add(recordData);
            }
        }

        return dsKetQua;
    }
}
