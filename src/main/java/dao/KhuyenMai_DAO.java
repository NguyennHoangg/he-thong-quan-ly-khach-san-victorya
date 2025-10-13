package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import config.ConnectDatabase;
import model.KhuyenMai;

public class KhuyenMai_DAO {
    public ArrayList<KhuyenMai> getallKhuyenMai() {
        ArrayList<KhuyenMai> dskm = new ArrayList<KhuyenMai>();

        try {
            ConnectDatabase.getInstance().;
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String sql = "Select * from KhuyenMai";
        try {
            ConnectDB.getInstance();
            Connection con = ConnectDB.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String maKM = rs.getString("MaKM");
                String tenKM = rs.getString("TenKM");
                String loaiKM = rs.getString("LoaiKM");
                float giaTri = rs.getFloat("GiaTri");
                LocalDate bd = rs.getDate("NgayBatDau").toLocalDate();
                LocalDate kt = rs.getDate("NgayKetThuc").toLocalDate();
                String trangThai = rs.getString("TrangThai");
                KhuyenMai km = new KhuyenMai(maKM, tenKM, loaiKM, giaTri, bd, kt, trangThai);
                dskm.add(km);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dskm;
    }

    public boolean create(KhuyenMai km) {
        ConnectDB.getInstance();
        Connection con = ConnectDB.getConnection();
        PreparedStatement statement = null;
        int n = 0;
        try {
            String sql = "INSERT INTO KhuyenMai (MaKM, TenKM, LoaiKM, GiaTri, NgayBatDau, NgayKetThuc, TrangThai)\r\n"
                    + "VALUES\r\n" + "(?, ?, ?, ?, ?, ?, ?)";
            statement = con.prepareStatement(sql);
            statement.setString(1, km.getMaKM());
            statement.setString(2, km.getTenKM());
            statement.setString(3, km.getLoai());
            statement.setFloat(4, km.getGiaTri());
            statement.setDate(5, java.sql.Date.valueOf(km.getNgayBatDau()));
            statement.setDate(6, java.sql.Date.valueOf(km.getNgayKetThuc()));
            statement.setString(7, km.getTrangThai());
            n = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return n > 0;
    }

    public KhuyenMai tim(String ma) {
        for (KhuyenMai km : getallKhuyenMai()) {
            if (km.getMaKM().equals(ma)) {
                return km;
            }
        }
        return null;
    }

    public ArrayList<KhuyenMai> timTen(String ten) {
        ten = ten.toLowerCase().trim();
        ArrayList<KhuyenMai> ketQua = new ArrayList<>();
        for (KhuyenMai km : getallKhuyenMai()) {
            if (km.getTenKM().toLowerCase().contains(ten)) {
                ketQua.add(km);
            }
        }

        return ketQua;
    }

    public boolean update(KhuyenMai km) {
        ConnectDB.getInstance();
        Connection con = ConnectDB.getConnection();
        PreparedStatement stmt = null;
        int n = 0;
        try {
            String sql = "UPDATE KhuyenMai SET TenKM = ?, LoaiKM = ?, GiaTri = ?,  NgayBatDau = ?, NgayKetThuc = ?, TrangThai = ? WHERE MaKM = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, km.getTenKM());
            stmt.setString(2, km.getLoai());
            stmt.setFloat(3, km.getGiaTri());
            stmt.setDate(4, java.sql.Date.valueOf(km.getNgayBatDau()));
            stmt.setDate(5, java.sql.Date.valueOf(km.getNgayKetThuc()));
            stmt.setString(6, km.getTrangThai());
            stmt.setString(7, km.getMaKM());

            n = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return n > 0;
    }

    public ArrayList<KhuyenMai> getKhuyenMaiByLoai(String idL) {
        ArrayList<KhuyenMai> dskm = new ArrayList<>();

        try {
            ConnectDB.getInstance().connect();
            Connection con = ConnectDB.getConnection();
            String sql = "SELECT * FROM KhuyenMai WHERE LoaiKM = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, idL);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String maKM = rs.getString("MaKM");
                String tenKM = rs.getString("TenKM");
                String loaiKM = rs.getString("LoaiKM");
                float giaTri = rs.getFloat("GiaTri");
                LocalDate bd = rs.getDate("NgayBatDau").toLocalDate();
                LocalDate kt = rs.getDate("NgayKetThuc").toLocalDate();
                String trangThai = rs.getString("TrangThai");
                KhuyenMai km = new KhuyenMai(maKM, tenKM, loaiKM, giaTri, bd, kt, trangThai);
                dskm.add(km);
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dskm;
    }
}
