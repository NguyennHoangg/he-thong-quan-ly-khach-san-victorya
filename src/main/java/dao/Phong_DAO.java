package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import config.ConnectDatabase;
import model.LoaiPhong;
import model.Phong;

public class Phong_DAO {
    public Phong_DAO() {

    }

    public List<Phong> getDsPhongByTrangThai(String trangThai) {
        List<Phong> dsKetQua = new ArrayList<>();
        LoaiPhong_DAO lp_dao = new LoaiPhong_DAO();
        String sql = "select * from Phong \r\n" +
                "where trangThai = N'" + trangThai + "';";
        try {
            Connection connect = ConnectDatabase.getConnection();
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String maPHong = rs.getString("maPhong");
                String soPhong = rs.getString("tenPhong");
                String maLoaiPhong = rs.getString("maLoaiPhong");
                int soTang = rs.getInt("tang");

                LoaiPhong lp = lp_dao.getLoaiPhongTheoMa(maLoaiPhong);
                Phong p = new Phong(maPHong, soPhong, lp, trangThai, soTang);
                dsKetQua.add(p);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return dsKetQua;
    }

}
