package controller;

import dao.NhanVien_DAO;
import dao.TaiKhoan_DAO;
import model.NhanVien;
import model.TaiKhoan;
import controller.User_Controller;

/**
 * Controller xử lý logic tài khoản và hồ sơ nhân viên
 * - Tải thông tin tài khoản và hồ sơ theo tên đăng nhập
 * - Cập nhật mật khẩu và thông tin cá nhân
 */
public class TaiKhoan_Controller {
    private TaiKhoan_DAO taiKhoanDAO = new TaiKhoan_DAO();
    private NhanVien_DAO nhanVienDAO = new NhanVien_DAO();
    private User_Controller userController = new User_Controller();

    /**
     * Lấy thông tin tài khoản theo tên đăng nhập
     * 
     * @param tenDangNhap Tên đăng nhập
     * @return Đối tượng TaiKhoan hoặc null nếu không tìm thấy
     */
    public TaiKhoan layThongTinTaiKhoan(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.isBlank()) {
            return null;
        }

        TaiKhoan taiKhoan = taiKhoanDAO.findByUsername(tenDangNhap);
        return taiKhoan;
    }

    /**
     * Lấy thông tin hồ sơ nhân viên theo tên đăng nhập
     * 
     * @param tenDangNhap Tên đăng nhập
     * @return Đối tượng NhanVien hoặc null nếu không tìm thấy
     */
    public NhanVien layThongTinNhanVien(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.isBlank()) {
            return null;
        }

        NhanVien nhanVien = nhanVienDAO.findByUsername(tenDangNhap);
        return nhanVien;
    }

    /**
     * Cập nhật mật khẩu cho tài khoản
     * 
     * @param tenDangNhap Tên đăng nhập
     * @param matKhauMoi  Mật khẩu mới (đã được hash)
     * @return true nếu cập nhật thành công
     */
    public boolean capNhatMatKhau(String tenDangNhap, String matKhauMoi) {
        if (tenDangNhap == null || tenDangNhap.isBlank() || matKhauMoi == null || matKhauMoi.isBlank()) {
            return false;
        }

        boolean thanhCong = taiKhoanDAO.updatePassword(tenDangNhap, matKhauMoi);
        return thanhCong;
    }

    /**
     * Cập nhật thông tin cá nhân nhân viên
     * 
     * @param nhanVien Đối tượng NhanVien với thông tin mới
     * @return true nếu cập nhật thành công
     */
    public boolean capNhatThongTinCaNhan(NhanVien nhanVien) {
        if (nhanVien == null || nhanVien.getMaNhanVien() == null || nhanVien.getMaNhanVien().isBlank()) {
            return false;
        }

        boolean thanhCong = nhanVienDAO.updateProfile(nhanVien);
        return thanhCong;
    }

    /**
     * Cập nhật vai trò tài khoản
     * 
     * @param tenDangNhap Tên đăng nhập
     * @param vaiTroMoi   Vai trò mới
     * @return true nếu cập nhật thành công
     */
    public boolean capNhatVaiTro(String tenDangNhap, String vaiTroMoi) {
        if (tenDangNhap == null || tenDangNhap.isBlank() || vaiTroMoi == null || vaiTroMoi.isBlank()) {
            return false;
        }

        boolean thanhCong = taiKhoanDAO.updateRole(tenDangNhap, vaiTroMoi);
        return thanhCong;
    }

    /**
     * Kiểm tra mật khẩu cũ có đúng không
     * 
     * @param tenDangNhap Tên đăng nhập
     * @param matKhauCu   Mật khẩu cũ (plain text)
     * @return true nếu mật khẩu đúng, false nếu sai
     */
    public boolean kiemTraMatKhauCu(String tenDangNhap, String matKhauCu) {
        if (tenDangNhap == null || tenDangNhap.isBlank() || matKhauCu == null || matKhauCu.isBlank()) {
            return false;
        }

        TaiKhoan taiKhoan = taiKhoanDAO.findByUsername(tenDangNhap);
        if (taiKhoan == null || taiKhoan.getMatKhau() == null) {
            return false;
        }

        return userController.kiemTraMatKhauHash(taiKhoan.getMatKhau(), matKhauCu);
    }

    /**
     * Cập nhật số điện thoại cho nhân viên
     * 
     * @param maNhanVien     Mã nhân viên
     * @param soDienThoaiMoi Số điện thoại mới
     * @return true nếu cập nhật thành công
     */
    public boolean capNhatSoDienThoai(String maNhanVien, String soDienThoaiMoi) {
        if (maNhanVien == null || maNhanVien.isBlank() || soDienThoaiMoi == null || soDienThoaiMoi.isBlank()) {
            return false;
        }

        boolean thanhCong = nhanVienDAO.updatePhone(maNhanVien, soDienThoaiMoi);
        return thanhCong;
    }

    /**
     * Cập nhật email cho nhân viên
     * 
     * @param maNhanVien Mã nhân viên
     * @param emailMoi   Email mới
     * @return true nếu cập nhật thành công
     */
    public boolean capNhatEmail(String maNhanVien, String emailMoi) {
        if (maNhanVien == null || maNhanVien.isBlank() || emailMoi == null || emailMoi.isBlank()) {
            return false;
        }

        boolean thanhCong = nhanVienDAO.updateEmail(maNhanVien, emailMoi);
        return thanhCong;
    }

}
