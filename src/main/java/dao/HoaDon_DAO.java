/*
 * @ (#) HoaDon_DAO.java     1.2    12/01/2025
 */
package dao;

import config.ConnectDatabase;
import model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDon_DAO {

    /** Hàm dùng chung để map 1 dòng ResultSet -> HoaDon (kèm KH, NV, KM) */
    private HoaDon mapRow(ResultSet rs) throws SQLException {
        String maHD = rs.getString("maHoaDon");

        Timestamp tsNgayDat = rs.getTimestamp("ngayDat");
        LocalDateTime ngayDat = tsNgayDat == null ? null : tsNgayDat.toLocalDateTime();

        Timestamp tsNgayTao = rs.getTimestamp("ngayTao");
        LocalDateTime ngayTao = tsNgayTao == null ? null : tsNgayTao.toLocalDateTime();

        double tongTien;
        try {
            if (rs.getBigDecimal("tongTien") != null) {
                tongTien = rs.getBigDecimal("tongTien").doubleValue();
            } else {
                tongTien = 0d;
            }
        } catch (Exception ignore) {
            tongTien = rs.getDouble("tongTien");
        }

        // Map KhachHang
        String maKH = rs.getString("maKhachHang");
        KhachHang kh = null;
        if (maKH != null) {
            kh = new KhachHang();
            kh.setMaKhachHang(maKH);
            kh.setTenKhachHang(rs.getString("tenKH"));
            kh.setSoDienThoai(rs.getString("soDienThoai"));
            kh.setEmail(rs.getString("email"));
        }

        // Map NhanVien
        String maNV = rs.getString("maNhanVien");
        NhanVien nv = null;
        if (maNV != null) {
            nv = new NhanVien();
            nv.setMaNhanVien(maNV);
            nv.setTenNhanVien(rs.getString("tenNhanVien"));
        }

        // Map KhuyenMai
        String maKM = rs.getString("maKhuyenMai");
        KhuyenMai km = null;
        if (maKM != null) {
            km = new KhuyenMai();
            km.setMaKhuyenMai(maKM);
            km.setTenKhuyenMai(rs.getString("tenKhuyenMai"));
            try {
                if (rs.getBigDecimal("heSo") != null) {
                    km.setHeSo(rs.getBigDecimal("heSo").floatValue());
                } else {
                    km.setHeSo(0f);
                }
            } catch (Exception e) {
                km.setHeSo(rs.getFloat("heSo"));
            }
        }

        // Map HoaDon
        HoaDon hd = new HoaDon();
        hd.setMaHoaDon(maHD);
        hd.setNgayDat(ngayDat);
        hd.setNgayTao(ngayTao);
        hd.setTrangThai(rs.getString("trangThai"));
        hd.setTongTien(tongTien);
        hd.setKhachHang(kh);
        hd.setNhanVien(nv);
        hd.setKhuyenMai(km);

        return hd;
    }

    /** Lấy tất cả hóa đơn + JOIN thông tin KH, NV, KM (dùng khi thực sự cần) */
    public List<HoaDon> getAll() {
        List<HoaDon> ds = new ArrayList<>();

        final String sql =
                "SELECT hd.maHoaDon, hd.ngayDat, hd.maKhachHang, hd.maNhanVien, hd.maKhuyenMai, " +
                        "       hd.ngayTao, hd.trangThai, hd.tongTien, " +
                        "       kh.hoTen AS tenKH, kh.soDienThoai, kh.email, " +
                        "       nv.tenNhanVien, " +
                        "       km.tenKhuyenMai, km.heSo " +
                        "FROM HoaDon hd " +
                        "LEFT JOIN KhachHang kh ON kh.maKhachHang = hd.maKhachHang " +
                        "LEFT JOIN NhanVien  nv ON nv.maNhanVien  = hd.maNhanVien " +
                        "LEFT JOIN KhuyenMai km ON km.maKhuyenMai = hd.maKhuyenMai " +
                        "ORDER BY hd.ngayDat DESC, hd.maHoaDon DESC";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ds.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** (tuỳ chọn) Lấy 1 hóa đơn theo mã, có JOIN đầy đủ – dùng khi bạn muốn refetch trước khi mở dialog */
    public HoaDon findById(String maHoaDon) {
        final String sql =
                "SELECT hd.maHoaDon, hd.ngayDat, hd.maKhachHang, hd.maNhanVien, hd.maKhuyenMai, " +
                        "       hd.ngayTao, hd.trangThai, hd.tongTien, " +
                        "       kh.hoTen AS tenKH, kh.soDienThoai, kh.email, " +
                        "       nv.tenNhanVien, " +
                        "       km.tenKhuyenMai, km.heSo " +
                        "FROM HoaDon hd " +
                        "LEFT JOIN KhachHang kh ON kh.maKhachHang = hd.maKhachHang " +
                        "LEFT JOIN NhanVien  nv ON nv.maNhanVien  = hd.maNhanVien " +
                        "LEFT JOIN KhuyenMai km ON km.maKhuyenMai = hd.maKhuyenMai " +
                        "WHERE hd.maHoaDon = ?";

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<HoaDon> timKiem(String tuKhoa,
                                String trangThaiRaw,
                                LocalDate tuNgay,
                                LocalDate denNgay) {

        List<HoaDon> ds = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT hd.maHoaDon, hd.ngayDat, hd.maKhachHang, hd.maNhanVien, hd.maKhuyenMai, " +
                        "       hd.ngayTao, hd.trangThai, hd.tongTien, " +
                        "       kh.hoTen AS tenKH, kh.soDienThoai, kh.email, " +
                        "       nv.tenNhanVien, " +
                        "       km.tenKhuyenMai, km.heSo " +
                        "FROM HoaDon hd " +
                        "LEFT JOIN KhachHang kh ON kh.maKhachHang = hd.maKhachHang " +
                        "LEFT JOIN NhanVien  nv ON nv.maNhanVien  = hd.maNhanVien " +
                        "LEFT JOIN KhuyenMai km ON km.maKhuyenMai = hd.maKhuyenMai " +
                        "WHERE 1 = 1 "
        );

        List<Object> params = new ArrayList<>();

        // 1. Từ khoá
        if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
            String kw = tuKhoa.trim().toLowerCase();
            sql.append(" AND (")
                    .append("      LOWER(hd.maHoaDon)   LIKE ? ")
                    .append("   OR LOWER(hd.maKhachHang) LIKE ? ")
                    .append("   OR LOWER(kh.hoTen)       LIKE ? ")
                    .append("   OR LOWER(hd.maNhanVien)  LIKE ? ")
                    .append(" ) ");
            String like = "%" + kw + "%";
            params.add(like);
            params.add(like);
            params.add(like);
            params.add(like);
        }

        // 2. Trạng thái
        if (trangThaiRaw != null && !trangThaiRaw.trim().isEmpty()) {
            sql.append(" AND hd.trangThai = ? ");
            params.add(trangThaiRaw.trim());
        }

        // 3. Khoảng ngày theo ngayDat
        if (tuNgay != null && denNgay != null) {
            // nếu người dùng lỡ chọn ngược thì đảo lại
            if (denNgay.isBefore(tuNgay)) {
                LocalDate tmp = tuNgay;
                tuNgay = denNgay;
                denNgay = tmp;
            }
            sql.append(" AND CAST(hd.ngayDat AS DATE) BETWEEN ? AND ? ");
            params.add(java.sql.Date.valueOf(tuNgay));
            params.add(java.sql.Date.valueOf(denNgay));
        } else if (tuNgay != null) {
            sql.append(" AND CAST(hd.ngayDat AS DATE) >= ? ");
            params.add(java.sql.Date.valueOf(tuNgay));
        } else if (denNgay != null) {
            sql.append(" AND CAST(hd.ngayDat AS DATE) <= ? ");
            params.add(java.sql.Date.valueOf(denNgay));
        }

        sql.append(" ORDER BY hd.ngayDat DESC, hd.maHoaDon DESC ");

        try (Connection conn = ConnectDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            // Gán tham số rõ type
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                int idx = i + 1;

                if (p instanceof String s) {
                    ps.setString(idx, s);
                } else if (p instanceof java.sql.Date d) {
                    ps.setDate(idx, d);
                } else if (p instanceof Timestamp ts) {
                    ps.setTimestamp(idx, ts);
                } else if (p instanceof Integer ii) {
                    ps.setInt(idx, ii);
                } else if (p instanceof Long l) {
                    ps.setLong(idx, l);
                } else if (p instanceof Double d) {
                    ps.setDouble(idx, d);
                } else if (p instanceof Float f) {
                    ps.setFloat(idx, f);
                } else {
                    ps.setObject(idx, p); // fallback
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ds;
    }

    public int getNextSequenceByDate(LocalDate date) {
        String datePart = String.format("%04d%02d%02d",
                date.getYear(), date.getMonthValue(), date.getDayOfMonth());

        String prefix = "HD-" + datePart + "-";

        String sql = """
        SELECT MAX(maHoaDon) AS maxMa
        FROM HoaDon
        WHERE maHoaDon LIKE ?
    """;

        try (Connection con = ConnectDatabase.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String maxMa = rs.getString("maxMa");
                    if (maxMa == null) return 1;

                    // maxMa ví dụ: HD-20251217-00000009
                    String last = maxMa.substring(prefix.length()); // "00000009"
                    int num = Integer.parseInt(last);
                    return num + 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // fallback an toàn (nhưng nếu lỗi DB thì vẫn trả 1)
        return 1;
    }

}
