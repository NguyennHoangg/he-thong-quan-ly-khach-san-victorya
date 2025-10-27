package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import config.ConnectDatabase;
import model.LoaiPhong;

public class LoaiPhong_DAO {
    public LoaiPhong_DAO() {
    }

    public List<LoaiPhong> getDsLoaiPhong() {
        String sql = "SELECT * FROM LoaiPhong";
        List<LoaiPhong> ketQua = new ArrayList<>();

        try (Connection connect = ConnectDatabase.getConnection();
                Statement stmt = connect.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) { // <-- đổi từ if thành while
                String maLP = rs.getString("maLoaiPhong");
                String tenLP = rs.getString("tenLoaiPhong");
                double gia = rs.getDouble("gia");

                Date sqlDate = rs.getDate("ngayTao");
                LocalDate ngayTao = (sqlDate != null) ? sqlDate.toLocalDate() : null;

                LoaiPhong lp = new LoaiPhong(maLP, tenLP, gia, ngayTao);
                ketQua.add(lp);
            }
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ketQua;
    }

}
