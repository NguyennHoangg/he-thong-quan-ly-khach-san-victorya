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
    public String HashPassWord(String matKhau){
        String matKhauHash = BCrypt.hashpw(matKhau, BCrypt.gensalt());
        return matKhauHash; // Trả về mật khẩu đã mã hóa
    }

    // Hàm kiểm tra mật khẩu nhập vào có khớp với mật khẩu đã mã hóa không
    /**
     * @param matKhauHash Mật khẩu đã được mã hóa
     * @param matKhau Mật khẩu gốc nhập vào để kiểm tra
     * @return true nếu mật khẩu nhập vào khớp với mật khẩu đã mã hóa, ngược lại trả về false
     */
    public boolean kiemTraMatKhauHash(String matKhauHash, String matKhau){
        boolean result = BCrypt.checkpw(matKhau, matKhauHash); // Kiểm tra mật khẩu
        if(!result){
            return false; // Nếu không khớp, trả về false
        }
        return true; // Nếu khớp, trả về true
    }
    
    // Hàm xác thực người dùng bằng tên đăng nhập và mật khẩu
    /**
     * @param tenDangNhap Tên đăng nhập của người dùng
     * @param matKhau Mật khẩu gốc của người dùng
     * @return true nếu xác thực thành công, ngược lại trả về false
     */
    public boolean xacThucNguoiDung(String tenDangNhap, String matKhau){
        String matKhauHash = HashPassWord(matKhau); // Mã hóa mật khẩu nhập vào
        TaiKhoan taiKhoan = user_DAO.timKiemNhanVienBangTaiKhoan(tenDangNhap, matKhauHash); // Tìm kiếm tài khoản
        // Kiểm tra tên đăng nhập và mật khẩu
        if(tenDangNhap.equals(taiKhoan.getTenDangNhap()) && kiemTraMatKhauHash(taiKhoan.getMatKhau(), matKhau)){
            return true; // Nếu đúng, trả về true
        }
        else{
            return false; // Nếu sai, trả về false
        }
    }

    // Hàm kiểm tra người dùng có phải là admin hay không
    /**
     * @param tenDangNhap Tên đăng nhập của người dùng
     * @return true nếu là admin, ngược lại trả về false
     */
    public boolean checkAdmin(String tenDangNhap, String matKhau){
         String matKhauHash = HashPassWord(matKhau);
        TaiKhoan taiKhoan = user_DAO.timKiemNhanVienBangTaiKhoan(tenDangNhap, matKhauHash);
        if (taiKhoan != null && "admin".equalsIgnoreCase(taiKhoan.getVaiTro())) {
            return true;
        }
        return false;
    }

}
