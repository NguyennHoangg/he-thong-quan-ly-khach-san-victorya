package dao;

import java.sql.Statement;

import config.ConnectDatabase;
import model.TaiKhoan;

public class User_DAO {

    // Tìm tài khoản theo tên đăng nhập (không kiểm tra mật khẩu) — dùng cho xác thực bằng BCrypt
    public TaiKhoan timKiemTheoTenDangNhap(String tenDangNhap) {
        TaiKhoan result = null;
        try {
            var connection = ConnectDatabase.getConnection();
            String query = "SELECT * FROM TaiKhoan WHERE tenDangNhap = '" + tenDangNhap + "'";
            Statement statement = connection.createStatement();
            var resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                String ten = resultSet.getString("tenDangNhap");
                String matKhauHashNhanVien = resultSet.getString("matKhau");
                String vaiTro = resultSet.getString("vaiTro");
                TaiKhoan taiKhoan = new TaiKhoan(ten, matKhauHashNhanVien, vaiTro);
                return taiKhoan;
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}
