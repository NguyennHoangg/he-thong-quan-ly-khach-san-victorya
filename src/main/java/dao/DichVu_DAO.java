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
            return dsKetQua;
        }

        return dsKetQua;
    }

    public boolean themDichVu(DichVu dvu) {
        String sql = "INSERT INTO DichVu(maDichVu, tenDichVu, gia, moTa, donViTinh) VALUES(?, ?, ?, ?, ?)";
        try (Connection connect = ConnectDatabase.getConnection();
                PreparedStatement ps = connect.prepareStatement(sql)) {

            ps.setString(1, dvu.getMaDichVu());
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

    public boolean capNhatDichVuTheoMa(DichVu dv) {
        String sql = "UPDATE DichVu SET gia = ?, moTa = ?, donViTinh = ?, tenDichVu = ? WHERE maDichVu = ?";
        try (Connection con = ConnectDatabase.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, dv.getGia());
            ps.setString(2, dv.getMoTa());
            ps.setString(3, dv.getDonViTinh());
            ps.setString(4, dv.getTenDichVu());
            ps.setString(5, dv.getMaDichVu());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getTongSoDichVu() {
        String sql = "SELECT COUNT(*) FROM DichVu";
        try (Connection connect = ConnectDatabase.getConnection();
                PreparedStatement ps = connect.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next())
                return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public DichVu timDichVuTheoMa(String ma) {
        String sql = "SELECT * FROM DichVu WHERE tenDichVu = ?";

        try (Connection connect = ConnectDatabase.getConnection();
                PreparedStatement ps = connect.prepareStatement(sql)) {

            ps.setString(1, ma);

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
