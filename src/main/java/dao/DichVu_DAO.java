package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import config.ConnectDatabase;
import model.DichVu;

public class DichVu_DAO {
    public DichVu_DAO() {
    }

    public List<DichVu> getDsDichVu() {
        String sql = "SELECT * FROM DichVu";
        List<DichVu> dsKetQua = new ArrayList<>();
        
        try (Connection connect = ConnectDatabase.getConnection();
             Statement stmt = connect.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String maDV = rs.getString("maDichVu");
                String ten = rs.getString("tenDichVu");
                double gia = rs.getDouble("gia");
                String moTa = rs.getString("moTa");
                String donViTinh = rs.getString("donViTinh");

                DichVu dv = new DichVu(maDV, ten, gia, moTa, donViTinh);
                dsKetQua.add(dv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dsKetQua;
    }
}
