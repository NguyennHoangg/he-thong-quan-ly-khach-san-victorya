package controller;

import java.util.ArrayList;
import java.util.List;

import dao.DichVu_DAO;
import model.DichVu;

public class DichVu_Controller {
    private DichVu_DAO dv_dao = new DichVu_DAO();

    public DichVu_Controller() {
    }

    public List<DichVu> getDsDichVu() {
        return dv_dao.getDsDichVu();
    }

    public List<String> getDsDonViTinh() {
        List<String> dsKetQua = new ArrayList<>();
        for (DichVu dvu : dv_dao.getDsDichVu()) {
            if (!dsKetQua.contains(dvu.getDonViTinh()))
                dsKetQua.add(dvu.getDonViTinh());
        }
        return dsKetQua;
    }

    public boolean themDichVu(DichVu dvu, StringBuilder tinNhan) {
        List<DichVu> ds = dv_dao.getDsDichVu();

        for (DichVu dv : ds) {
            if (dv.getTenDichVu().equalsIgnoreCase(dvu.getTenDichVu().trim())) {
                // Cập nhật dựa theo tên — dùng mã DV cũ
                if (dv_dao.capNhatDichVuTheoMa(dv.getMaDichVu(), dvu)) {
                    tinNhan.append("Cập nhật thông tin dịch vụ thành công");
                    return true;
                } else {
                    tinNhan.append("Cập nhật thông tin dịch vụ thất bại");
                    return false;
                }
            }
        }

        // Nếu không trùng, thêm mới
        if (dv_dao.themDichVu(dvu)) {
            tinNhan.append("Thêm thông tin dịch vụ thành công");
            return true;
        } else {
            tinNhan.append("Thêm thông tin dịch vụ thất bại");
            return false;
        }
    }

    public boolean kiemTraDauVao(String tenDichVu, String donViTinh, double gia, StringBuilder tinNhan) {
        if (tenDichVu == null || tenDichVu.isEmpty()) {
            tinNhan.append("Tên dịch vụ không được rỗng!");
            return false;
        }

        if (donViTinh == null || donViTinh.isEmpty()) {
            tinNhan.append("Đơn vị tính không được rỗng!");
            return false;
        }

        if (gia <= 0.0) {
            tinNhan.append("Giá không được rỗng, phải lớn hơn 0");
            return false;
        }

        return true;
    }

    public DichVu timDichVu(String ten) {
        return dv_dao.timDichVuTheoTen(ten);
    }

    public boolean xoaDichVu(String maDichVu) {
        return dv_dao.xoaDichVuTheoMa(maDichVu);
    }
}
