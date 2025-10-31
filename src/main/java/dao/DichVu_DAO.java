package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

    public boolean themDichVu(DichVu dvu) {
        String sql = "INSERT INTO DichVu(maDichVu, tenDichVu, gia, moTa, donViTinh) VALUES(?, ?, ?, ?, ?)";
        try (Connection connect = ConnectDatabase.getConnection();
                PreparedStatement ps = connect.prepareStatement(sql)) {

            ps.setString(1, phatSinhMaDichVu());
            ps.setString(2, dvu.getTenDichVu());
            ps.setDouble(3, dvu.getGia());
            ps.setString(4, dvu.getMoTa());
            ps.setString(5, dvu.getDonViTinh());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean capNhatDichVuTheoMa(String ma, DichVu dv) {
        String sql = "UPDATE DichVu SET gia = ?, moTa = ?, donViTinh = ?, tenDichVu = ? WHERE maDichVu = ?";
        try (Connection con = ConnectDatabase.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, dv.getGia());
            ps.setString(2, dv.getMoTa());
            ps.setString(3, dv.getDonViTinh());
            ps.setString(4, dv.getTenDichVu());
            ps.setString(5, ma);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String phatSinhMaDichVu() {
        String sql = "SELECT COUNT(*) as soLuong FROM DichVu;";
        try {
            Connection con = ConnectDatabase.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            int soLuong = 0;
            if (rs.next()) {
                soLuong = rs.getInt("soLuong");
            }
            return String.format("DV-%05d", soLuong + 1);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }

    }

    public DichVu timDichVuTheoTen(String ten) {
        String sql = "SELECT * FROM DichVu WHERE tenDichVu = ?";

        try (Connection connect = ConnectDatabase.getConnection();
                PreparedStatement ps = connect.prepareStatement(sql)) {

            ps.setString(1, ten);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String maDV = rs.getString("maDichVu");
                    String tenDV = rs.getString("tenDichVu");
                    double gia = rs.getDouble("gia");
                    String moTa = rs.getString("moTa");
                    String donViTinh = rs.getString("donViTinh");

                    return new DichVu(maDV, tenDV, gia, moTa, donViTinh);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean xoaDichVuTheoMa(String maDichVu) {
        String sql = "DELETE FROM DichVu WHERE maDichVu = ?";

        try (Connection con = ConnectDatabase.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maDichVu);

            int affectedRows = ps.executeUpdate();

            return affectedRows > 0; // Nếu có ít nhất 1 dòng bị xóa → thành công

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
