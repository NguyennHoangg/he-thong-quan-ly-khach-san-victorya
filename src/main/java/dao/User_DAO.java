package dao;

import java.sql.Statement;

import config.ConnectDatabase;
import model.TaiKhoan;

public class User_DAO {
    private ConnectDatabase conn = new ConnectDatabase();

    public TaiKhoan timKiemNhanVienBangTaiKhoan(String tenDangNhap, String matKhau) {
        TaiKhoan result = null;
        try {
            var connection = conn.getConnection();
            String query = "SELECT * FROM TaiKhoan WHERE tenDangNhap = '" + tenDangNhap +
                           "' AND matKhau = '" + matKhau + "'";
            Statement statement = connection.createStatement();
            var resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                String tenDangNhapNhanVien = resultSet.getString("tenDangNhap");
                String matKhauHashNhanVien = resultSet.getString("matKhau");
                String vaiTro = resultSet.getString("vaiTro");
                TaiKhoan taiKhoan = new TaiKhoan(tenDangNhapNhanVien, matKhauHashNhanVien, vaiTro);
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
