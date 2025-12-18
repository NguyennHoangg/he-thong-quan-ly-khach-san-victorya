
package controller;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import dao.NhanVien_DAO;
import model.NhanVien;
import model.TaiKhoan;

public class NhanVien_Controller {
    private NhanVien_DAO nv_dao = new NhanVien_DAO();

    public NhanVien_Controller() {
    }

    public List<NhanVien> getDsNhanVien() {
        return nv_dao.getDsNhanVien();
    }

    public String HashPassWord(String matKhau) {
        String matKhauHash = BCrypt.hashpw(matKhau, BCrypt.gensalt());
        return matKhauHash; // Trả về mật khẩu đã mã hóa
    }

    public List<String> getDsVaiTroNhanVien() {
        List<String> dsVaiTro = new ArrayList<>();
        for (NhanVien nv : nv_dao.getDsNhanVien()) {
            if (!dsVaiTro.contains(nv.getTaiKhoan().getVaiTro())) {
                dsVaiTro.add(nv.getTaiKhoan().getVaiTro());
            }
        }
        dsVaiTro.add(0, "Tất cả");
        return dsVaiTro;
    }

    public String phatSinhMaNhanVien() {
        String maxMa = nv_dao.getMaxMaNhanVien();

        if (maxMa == null) {
            return "NV001";
        }
        int so = Integer.parseInt(maxMa.substring(2));

        return String.format("NV%03d", so + 1);
    }

    public boolean capNhatNhanVien(NhanVien nv, StringBuilder loiNhan) {
        NhanVien nvTim = timNhanVienTheoCCCD(nv.getCCCD());
        if (nvTim == null) {
            loiNhan.append("không tìm thấy nhân viên để cập nhật");
            return false;
        }
        if (nv_dao.capNhatNhanVien(nv)) {
            loiNhan.append("Cập nhật nhân viên thành công");
            return true;
        } else {
            loiNhan.append("Cập nhật nhân viên Thất bại!");
            return false;
        }
    }

    public boolean themNhanVien(NhanVien nv, StringBuilder loiNhan) {
        NhanVien nvTim = nv_dao.timNhanVienTheoCCCD(nv.getCCCD());

        if (nvTim != null) {
            loiNhan.append("CCCD đã tồn tại, không thể thêm nhân viên");
            return false;
        }

        if (nv_dao.themNhanVien(nv)) {
            loiNhan.append("Thêm nhân viên thành công");
            return true;
        } else {
            loiNhan.append("Thêm nhân viên thất bại");
            return false;
        }
    }

    public boolean luu(NhanVien nv, StringBuilder loiNhan) {
        if (nv.getMaNhanVien() == null || nv.getMaNhanVien().trim().isEmpty()) {
            String maMoi = phatSinhMaNhanVien();
            nv.setMaNhanVien(maMoi);
            String matKhauMacDinh = HashPassWord("1111");
            TaiKhoan tk = new TaiKhoan(nv.getMaNhanVien(), matKhauMacDinh, nv.getTaiKhoan().getVaiTro());
            nv.setTaiKhoan(tk);

            return themNhanVien(nv, loiNhan);
        }

        return capNhatNhanVien(nv, loiNhan);
    }

    public boolean xoaNhanVien(NhanVien nv, StringBuilder loiNhan) {
        NhanVien nvCanXoa = timNhanVienTheoCCCD(nv.getCCCD());

        if (nvCanXoa == null)
            return false;
        else {
            if (nv_dao.xoaNhanVienTheoCCCD(nvCanXoa)) {
                loiNhan.append("Xóa nhân viên thành công");
                return true;
            } else {
                loiNhan.append("Xóa nhân viên Thất bại!");
                return false;
            }
        }

    }

    public List<NhanVien> timNhanVien(String tuKhoa) {
        List<NhanVien> dsTimDuoc = new ArrayList<>();
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) {
            return null;
        }
        tuKhoa = tuKhoa.trim();
        if (tuKhoa.matches("^\\d{12}$")) {
            dsTimDuoc.add(nv_dao.timNhanVienTheoCCCD(tuKhoa));
            return dsTimDuoc;
        }
        if (tuKhoa.matches("^0\\d{9}$")) {

            for (NhanVien nv : nv_dao.getDsNhanVien()) {
                if (nv.getSoDienThoai().equalsIgnoreCase(tuKhoa)) {
                    dsTimDuoc.add(nv);
                }
            }
            return dsTimDuoc;
        }
        for (NhanVien nv : nv_dao.getDsNhanVien()) {
            if (nv.getTenNhanVien().toLowerCase().contains(tuKhoa.toLowerCase())) {
                dsTimDuoc.add(nv);
            }
        }
        return dsTimDuoc;
    }

    public NhanVien timNhanVienTheoCCCD(String cccd) {
        if (nv_dao.timNhanVienTheoCCCD(cccd) == null)
            return null;
        return nv_dao.timNhanVienTheoCCCD(cccd);
    }
    
    public NhanVien timNhanVienTheoTenDangNhap(String tenDangNhap) {
        for (NhanVien nv : nv_dao.getDsNhanVien()) {
            if (nv.getTaiKhoan() != null && nv.getTaiKhoan().getTenDangNhap().equals(tenDangNhap)) {
                return nv;
            }
        }
        return null;
    }

    public boolean kiemTra(NhanVien nv, StringBuilder loi) {
        if (nv.getTenNhanVien() == null || nv.getTenNhanVien().trim().isEmpty()) {
            loi.append("Tên nhân viên không được bỏ trống");
            return false;
        }
        if (!nv.getTenNhanVien().matches("^[\\p{L}\\s]+$")) {
            loi.append("Tên nhân viên chỉ được chứa chữ cái và khoảng trắng");
            return false;
        }
        if (nv.getNgaySinh() == null) {
            loi.append("Ngày sinh không hợp lệ");
            return false;
        }
        if (Period.between(nv.getNgaySinh(), LocalDate.now()).getYears() < 18) {
            loi.append("Nhân viên phải trên 18 tuổi");
            return false;
        }
        if (!nv.getSoDienThoai().matches("^0\\d{9}$")) {
            loi.append("Số điện thoại không hợp lệ");
            return false;
        }
        if (!nv.getCCCD().matches("^0\\d{11}$")) {
            loi.append("CCCD không hợp lệ");
            return false;
        }
        return true;
    }
}
