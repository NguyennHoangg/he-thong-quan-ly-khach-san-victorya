package controller;

import org.mindrot.jbcrypt.BCrypt;

import dao.User_DAO;
import model.NhanVien;
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
        return tenDangNhap != null && !tenDangNhap.isEmpty() && matKhau != null && !matKhau.isEmpty();
    }

    // Hàm kiểm tra người dùng có phải là admin hay không
    /**
     * @param tenDangNhap Tên đăng nhập của người dùng
     * @return true nếu là admin, ngược lại trả về false
     */
    public boolean checkAdmin(String tenDangNhap, String matKhau) {
        return "admin".equalsIgnoreCase(tenDangNhap);
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

    /**
     * Trả về đối tượng NhanVien dựa trên số CCCD (Căn Cước Công Dân) được cung cấp.
     *
     * @param CCCD Số căn cước công dân của nhân viên cần tìm kiếm.
     * @return Đối tượng NhanVien tương ứng với CCCD, hoặc null nếu không tìm thấy.
     */
    public NhanVien getEmailTheoCCCD(String CCCD){
        return user_DAO.timKiemTheoCCCD(CCCD);
    }

    
}
