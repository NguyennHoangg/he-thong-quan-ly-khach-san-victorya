package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import config.ConnectDatabase;
import model.LoaiPhong;

public class LoaiPhong_DAO {
    public LoaiPhong_DAO() {
    }

    public LoaiPhong getLoaiPhongTheoMa(String ma) {
        String sql = "SELECT * FROM LoaiPhong WHERE maLoaiPhong = '" + ma + "'";
        Connection connect = ConnectDatabase.getConnection();
        LoaiPhong lp = null; // ✅ khai báo trước

        try {
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                String maLP = rs.getString("maLoaiPhong");
                String tenLP = rs.getString("tenLoaiPhong");
                double gia = rs.getDouble("gia");
                LocalDate ngayTao = rs.getDate("ngayTao").toLocalDate();
                lp = new LoaiPhong(maLP, tenLP, gia, ngayTao);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lp;
    }
}
