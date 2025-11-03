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
        for (DichVu dvu : getDsDichVu()) {
            if (!dsKetQua.contains(dvu.getDonViTinh()))
                dsKetQua.add(dvu.getDonViTinh());
        }
        return dsKetQua;
    }

    public String phatSinhMaDichVu() {
        int count = dv_dao.getTongSoDichVu();
        return String.format("DV-%05d", count + 1);
    }

    public boolean themDichVu(DichVu dvu, StringBuilder tinNhan) {
        if (dvu.getMaDichVu() != null) {
            return false;
        }
        // List<DichVu> ds = dv_dao.getDsDichVu();
        DichVu dvuMoi = new DichVu(dvu.getTenDichVu(), dvu.getGia(), dvu.getMoTa(), dvu.getDonViTinh());
        // Nếu không trùng, thêm mới
        if (dv_dao.themDichVu(dvuMoi)) {
            tinNhan.append("Thêm thông tin dịch vụ thành công");
            return true;
        } else {
            tinNhan.append("Thêm thông tin dịch vụ thất bại");
            return false;
        }
    }

    public boolean capNhatDichVuTheoMa(DichVu dvu, StringBuilder tinNhan) {
        // List<DichVu> ds = dv_dao.getDsDichVu();
        if (dvu.getMaDichVu() != null) {
            // Cập nhật dựa theo tên — dùng mã DV cũ
            if (dv_dao.capNhatDichVuTheoMa(dvu)) {
                tinNhan.append("Cập nhật thông tin dịch vụ thành công");
                return true;
            } else {
                tinNhan.append("Cập nhật thông tin dịch vụ thất bại");
                return false;
            }
        }
        return false;
    }

    public boolean luuDichVu(DichVu dvu, StringBuilder loiNhan) {
        if (dvu.getTenDichVu() == null || dvu.getTenDichVu().trim().isEmpty()) {
            loiNhan.append("Tên dịch vụ không được để trống!");
            return false;
        }

        DichVu tonTai = dv_dao.timDichVuTheoMa(dvu.getMaDichVu());
        boolean ketQua;
        DichVu dvuMoi = null;

        if (tonTai == null) { // chưa có => thêm mới
            if (dvu.getMaDichVu() == null || dvu.getMaDichVu().isEmpty()) {
                dvuMoi = new DichVu(phatSinhMaDichVu(), dvu.getTenDichVu(), dvu.getGia(), dvu.getMoTa(),
                        dvu.getDonViTinh());
            }
            ketQua = dv_dao.themDichVu(dvuMoi);
            loiNhan.append(ketQua ? "Thêm dịch vụ thành công!" : "Thêm dịch vụ thất bại!");
        } else { // đã có => cập nhật
            ketQua = dv_dao.capNhatDichVuTheoMa(dvu);
            loiNhan.append(ketQua ? "Cập nhật dịch vụ thành công!" : "Cập nhật dịch vụ thất bại!");
        }

        return ketQua;
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

    public DichVu timDichVu(String ma) {
        return dv_dao.timDichVuTheoMa(ma);
    }

    public boolean xoaDichVu(DichVu dvu, StringBuilder loiNhan) {
        if (dvu.getMaDichVu() == null)
            return false;
        else {
            if (dv_dao.xoaDichVuTheoMa(dvu.getMaDichVu())) {
                loiNhan.append("Xóa dịch vụ thành công");
                return true;
            } else {
                loiNhan.append("Xóa dịch vụ Thất bại!");
                return false;
            }
        }
    }
}
