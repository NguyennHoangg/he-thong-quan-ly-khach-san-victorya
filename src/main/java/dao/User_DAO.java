package dao;

import java.sql.Statement;

import config.ConnectDatabase;
import model.NhanVien;
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

     public NhanVien timKiemTheoCCCD(String CCCD) {
        NhanVien result = null;
        try {
            var connection = ConnectDatabase.getConnection();
            String query = "SELECT nv.email, tk.tenDangNhap, tk.matKhau, tk.vaiTro FROM NhanVien nv JOIN TaiKhoan tk ON nv.tenDangNhap = tk.tenDangNhap WHERE nv.CCCD = ?";
            var preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, CCCD);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String email = resultSet.getString("email");
                String tenDangNhap = resultSet.getString("tenDangNhap");
                String matKhau = resultSet.getString("matKhau");
                String vaiTro = resultSet.getString("vaiTro");
                TaiKhoan taiKhoan = new TaiKhoan(tenDangNhap, matKhau, vaiTro);
                result = new NhanVien(email);
                result.setTaiKhoan(taiKhoan);
                return result;
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}
