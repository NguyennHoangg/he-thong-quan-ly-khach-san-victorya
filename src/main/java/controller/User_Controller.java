package controller;

import org.mindrot.jbcrypt.BCrypt;

import dao.User_DAO;
import model.TaiKhoan;

public class User_Controller {

    private User_DAO user_DAO = new User_DAO();

    // Hàm mã hóa mật khẩu bằng BCrypt
    /**
     * @param matKhau Mật khẩu gốc cần mã hóa
     * @return Chuỗi mật khẩu đã được mã hóa bằng BCrypt
     */
    public String HashPassWord(String matKhau) {
        String matKhauHash = BCrypt.hashpw(matKhau, BCrypt.gensalt());
        return matKhauHash; // Trả về mật khẩu đã mã hóa
    }

    // Hàm kiểm tra mật khẩu nhập vào có khớp với mật khẩu đã mã hóa không
    /**
     * @param matKhauHash Mật khẩu đã được mã hóa
     * @param matKhau     Mật khẩu gốc nhập vào để kiểm tra
     * @return true nếu mật khẩu nhập vào khớp với mật khẩu đã mã hóa, ngược lại trả
     *         về false
     */
    public boolean kiemTraMatKhauHash(String matKhauHash, String matKhau) {
        boolean result = BCrypt.checkpw(matKhau, matKhauHash); // Kiểm tra mật khẩu
        if (!result) {
            return false; // Nếu không khớp, trả về false
        }
        return true; // Nếu khớp, trả về true
    }

    // Hàm xác thực người dùng bằng tên đăng nhập và mật khẩu
    /**
     * @param tenDangNhap Tên đăng nhập của người dùng
     * @param matKhau     Mật khẩu gốc của người dùng
     * @return true nếu xác thực thành công, ngược lại trả về false
     */
    public boolean xacThucNguoiDung(String tenDangNhap, String matKhau) {
        // Lấy tài khoản theo tên đăng nhập
        TaiKhoan taiKhoan = user_DAO.timKiemTheoTenDangNhap(tenDangNhap);
        if (taiKhoan == null) {
            return false; // không tìm thấy tài khoản
        }
        // So sánh mật khẩu nhập với mật khẩu đã hash trong DB
        return kiemTraMatKhauHash(taiKhoan.getMatKhau(), matKhau);
    }

    // Hàm kiểm tra người dùng có phải là admin hay không
    /**
     * @param tenDangNhap Tên đăng nhập của người dùng
     * @return true nếu là admin, ngược lại trả về false
     */
    public boolean checkAdmin(String tenDangNhap, String matKhau) {
        TaiKhoan taiKhoan = user_DAO.timKiemTheoTenDangNhap(tenDangNhap);
        // So sánh trực tiếp với trường vaiTro gốc để tránh lỗi chuyển đổi tiếng Việt
        if (taiKhoan != null && taiKhoan != null && taiKhoanRawRoleIsAdmin(taiKhoan)) {
            return true;
        }
        return false;
    }

    // Helper để lấy vaiTro gốc (không qua getVaiTro)
    private boolean taiKhoanRawRoleIsAdmin(TaiKhoan tk) {
        try {
            java.lang.reflect.Field f = tk.getClass().getDeclaredField("vaiTro");
            f.setAccessible(true);
            Object raw = f.get(tk);
            return raw != null && raw.toString().equalsIgnoreCase("admin");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Đăng nhập người dùng và kiểm tra quyền admin.
     * @param tenDangNhap Tên đăng nhập của người dùng
     * @param matKhau Mật khẩu của người dùng
     * @return true nếu xác thực thành công và là admin, ngược lại trả về false
     */
    public boolean isAdmin(String tenDangNhap, String matKhau) {
        if (xacThucNguoiDung(tenDangNhap, matKhau) && checkAdmin(tenDangNhap, matKhau)) {
            return true;
        }
        return false;
    }

    
}
