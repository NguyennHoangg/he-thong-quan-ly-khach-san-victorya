package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import config.ConnectDatabase;
import model.ChiTietPhieuDatPhong;
import model.DichVu;
import model.LoaiDatPhong;
import model.PhieuDatPhong;
import model.Phong;

public class ChiTietPhieuDatPhong_DAO {
    public ChiTietPhieuDatPhong_DAO() {
    }

    public List<ChiTietPhieuDatPhong> getDsChiTietPhieuDatPhong() {
        List<ChiTietPhieuDatPhong> dsKetQua = new ArrayList<>();
        String sql = "select * from ChiTietPhieuDatPhong";
        try {
            Connection connect = ConnectDatabase.getConnection();
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String maPhieuDatPhong = rs.getString("maPhieuDatPhong");
                String maLoaiDatPhong = rs.getString("maLoaiDatPhong");
                String maDichVu = rs.getString("maDichVu");
                LocalDateTime gioBatDau = rs.getTimestamp("gioBatDau").toLocalDateTime();
                LocalDateTime gioKetThuc = rs.getTimestamp("gioKetThuc").toLocalDateTime();
                Duration thoiGianThue = Duration.between(gioBatDau, gioKetThuc);
                int soGioLuuTru = (int) thoiGianThue.toMinutes() / 60;
                soGioLuuTru = (int) Math.ceil(soGioLuuTru);

                String maPhong = rs.getString("maPhong");
                int soNguoi = rs.getInt("soNguoi");

                PhieuDatPhong pdp = new PhieuDatPhong(maPhieuDatPhong);
                LoaiDatPhong ldp = new LoaiDatPhong(maLoaiDatPhong);
                DichVu dv = new DichVu(maDichVu);
                List<DichVu> dsDV = new ArrayList<>();
                dsDV.add(dv);
                Phong p = new Phong(maPhong);
                ChiTietPhieuDatPhong ctpdp = new ChiTietPhieuDatPhong(pdp, ldp, dsDV, soGioLuuTru, gioBatDau,
                        gioKetThuc, p, soNguoi);
                dsKetQua.add(ctpdp);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsKetQua;

    }
}
