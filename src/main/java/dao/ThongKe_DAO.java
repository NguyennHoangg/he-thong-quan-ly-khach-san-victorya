package dao;

import config.ConnectDatabase;
import model.thongke.*;
import model.thongke.TableRowHoaDon;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho module Thống kê - chứa tất cả các SQL queries cho 7 tab thống kê
 */
public class ThongKe_DAO {

    // ============================================================
    // 1. TAB DOANH THU
    // ============================================================

    /**
     * Lấy tổng doanh thu trong khoảng thời gian
     */
    public double getTongDoanhThu(LocalDate fromDate, LocalDate toDate) {
        String sql = """
                    SELECT ISNULL(SUM(tongTien), 0) AS TongDoanhThu
                    FROM HoaDon
                    WHERE trangThai = N'Đã thanh toán'
                      AND CAST(ngayTao AS DATE) BETWEEN ? AND ?
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("TongDoanhThu");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy số hóa đơn trong khoảng thời gian
     */
    public int getSoHoaDon(LocalDate fromDate, LocalDate toDate) {
        String sql = """
                    SELECT COUNT(*) AS SoHoaDon
                    FROM HoaDon
                    WHERE trangThai = N'Đã thanh toán'
                      AND CAST(ngayTao AS DATE) BETWEEN ? AND ?
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("SoHoaDon");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy doanh thu trung bình mỗi hóa đơn
     */
    public double getDoanhThuTrungBinh(LocalDate fromDate, LocalDate toDate) {
        String sql = """
                    SELECT ISNULL(AVG(tongTien), 0) AS DoanhThuTB
                    FROM HoaDon
                    WHERE trangThai = N'Đã thanh toán'
                      AND CAST(ngayTao AS DATE) BETWEEN ? AND ?
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("DoanhThuTB");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy ngày có doanh thu cao nhất
     */
    public TimeSeriesPoint getNgayDoanhThuCaoNhat(LocalDate fromDate, LocalDate toDate) {
        String sql = """
                    SELECT TOP 1 CAST(ngayTao AS DATE) AS Ngay, SUM(tongTien) AS DoanhThu
                    FROM HoaDon
                    WHERE trangThai = N'Đã thanh toán'
                      AND CAST(ngayTao AS DATE) BETWEEN ? AND ?
                    GROUP BY CAST(ngayTao AS DATE)
                    ORDER BY DoanhThu DESC
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                LocalDate ngay = rs.getDate("Ngay").toLocalDate();
                double doanhThu = rs.getDouble("DoanhThu");
                return new TimeSeriesPoint(ngay, doanhThu);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy xu hướng doanh thu theo ngày
     */
    public List<TimeSeriesPoint> getDoanhThuTheoNgay(LocalDate fromDate, LocalDate toDate) {
        List<TimeSeriesPoint> result = new ArrayList<>();
        String sql = """
                    SELECT
                        CAST(ngayTao AS DATE) AS Ngay,
                        SUM(tongTien) AS DoanhThu
                    FROM HoaDon
                    WHERE trangThai = N'Đã thanh toán'
                      AND CAST(ngayTao AS DATE) BETWEEN ? AND ?
                    GROUP BY CAST(ngayTao AS DATE)
                    ORDER BY Ngay
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LocalDate ngay = rs.getDate("Ngay").toLocalDate();
                double doanhThu = rs.getDouble("DoanhThu");
                result.add(new TimeSeriesPoint(ngay, doanhThu));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy xu hướng doanh thu theo giờ cho một ngày cụ thể
     */
    public List<TimeSeriesPoint> getDoanhThuTheoGio(LocalDate date) {
        List<TimeSeriesPoint> result = new ArrayList<>();
        String sql = """
                    SELECT
                        DATEPART(HOUR, ngayTao) AS Gio,
                        SUM(tongTien) AS DoanhThu
                    FROM HoaDon
                    WHERE trangThai = N'Đã thanh toán'
                      AND CAST(ngayTao AS DATE) = ?
                    GROUP BY DATEPART(HOUR, ngayTao)
                    ORDER BY Gio
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int hour = rs.getInt("Gio");
                double doanhThu = rs.getDouble("DoanhThu");
                String label = String.format("%02d:00", hour);
                result.add(new TimeSeriesPoint(date, label, doanhThu, 1, hour));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy cơ cấu doanh thu theo nhân viên
     */
    public List<GroupSeriesPoint> getDoanhThuTheoNhanVien(LocalDate fromDate, LocalDate toDate) {
        List<GroupSeriesPoint> result = new ArrayList<>();
        String sql = """
                SELECT
                    nv.tenNhanVien,
                        SUM(hd.tongTien) AS DoanhThu,
                        COUNT(*) AS SoHoaDon
                    FROM HoaDon hd
                    JOIN NhanVien nv ON hd.maNhanVien = nv.maNhanVien
                    WHERE hd.trangThai = N'Đã thanh toán'
                      AND CAST(hd.ngayTao AS DATE) BETWEEN ? AND ?
                    GROUP BY nv.tenNhanVien
                    ORDER BY DoanhThu DESC
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String ten = rs.getString("tenNhanVien");
                double doanhThu = rs.getDouble("DoanhThu");
                int soHD = rs.getInt("SoHoaDon");
                result.add(new GroupSeriesPoint(ten, doanhThu, soHD));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy top 5 nhân viên có doanh thu cao nhất
     */
    public List<GroupSeriesPoint> getTop5DoanhThuTheoNhanVien(LocalDate fromDate, LocalDate toDate) {
        List<GroupSeriesPoint> result = new ArrayList<>();
        String sql = """
                SELECT TOP 5
                    nv.tenNhanVien,
                    SUM(hd.tongTien) AS DoanhThu,
                    COUNT(*) AS SoHoaDon
                FROM HoaDon hd
                JOIN NhanVien nv ON hd.maNhanVien = nv.maNhanVien
                WHERE hd.trangThai = N'Đã thanh toán'
                  AND CAST(hd.ngayTao AS DATE) BETWEEN ? AND ?
                GROUP BY nv.tenNhanVien
                ORDER BY DoanhThu DESC
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String ten = rs.getString("tenNhanVien");
                double doanhThu = rs.getDouble("DoanhThu");
                int soHD = rs.getInt("SoHoaDon");
                result.add(new GroupSeriesPoint(ten, doanhThu, soHD));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy cơ cấu doanh thu theo khuyến mãi
     */
    public List<GroupSeriesPoint> getDoanhThuTheoKhuyenMai(LocalDate fromDate, LocalDate toDate) {
        List<GroupSeriesPoint> result = new ArrayList<>();
        String sql = """
                    SELECT
                        ISNULL(km.tenKhuyenMai, N'Không KM') AS TenKM,
                        SUM(hd.tongTien) AS DoanhThu,
                        COUNT(*) AS SoHoaDon
                    FROM HoaDon hd
                    LEFT JOIN KhuyenMai km ON hd.maKhuyenMai = km.maKhuyenMai
                    WHERE hd.trangThai = N'Đã thanh toán'
                      AND CAST(hd.ngayTao AS DATE) BETWEEN ? AND ?
                    GROUP BY ISNULL(km.tenKhuyenMai, N'Không KM')
                    ORDER BY DoanhThu DESC
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String ten = rs.getString("TenKM");
                double doanhThu = rs.getDouble("DoanhThu");
                int soHD = rs.getInt("SoHoaDon");
                result.add(new GroupSeriesPoint(ten, doanhThu, soHD));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy cơ cấu doanh thu phòng vs. dịch vụ
     */
    public List<GroupSeriesPoint> getDoanhThuPhongVsDichVu(LocalDate fromDate, LocalDate toDate) {
        List<GroupSeriesPoint> result = new ArrayList<>();
        String sql = """
                WITH RoomGross AS (
                    SELECT chd.maHoaDon,
                           SUM(
                               CASE
                                   WHEN DATEDIFF(HOUR, ctpdp.thoiGianNhanPhong, ctpdp.thoiGianTraPhong) <= 0 THEN 1
                                   ELSE CEILING(DATEDIFF(MINUTE, ctpdp.thoiGianNhanPhong, ctpdp.thoiGianTraPhong) / 60.0)
                               END * lp.gia
                           ) AS grossRoom
                    FROM ChiTietHoaDon chd
                    JOIN ChiTietPhieuDatPhong ctpdp ON chd.maPhieuDatPhong = ctpdp.maPhieuDatPhong
                    JOIN Phong p ON ctpdp.maPhong = p.maPhong
                    JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong
                    GROUP BY chd.maHoaDon
                ),
                SvcGross AS (
                    SELECT chd.maHoaDon,
                           SUM(ctdv.soLuong * dv.gia) AS grossSvc
                    FROM ChiTietHoaDon chd
                    JOIN ChiTietPhieuDatPhong ctpdp ON chd.maPhieuDatPhong = ctpdp.maPhieuDatPhong
                    JOIN ChiTietPhieuDatPhong_DichVu ctdv ON ctdv.maPhieuDatPhong = ctpdp.maPhieuDatPhong
                                                      AND ctdv.maPhong = ctpdp.maPhong
                    JOIN DichVu dv ON dv.maDichVu = ctdv.maDichVu
                    GROUP BY chd.maHoaDon
                ),
                PerBill AS (
                    SELECT hd.maHoaDon,
                           ISNULL(rg.grossRoom, 0) AS grossRoom,
                           ISNULL(sg.grossSvc, 0) AS grossSvc,
                           hd.tongTien AS netTotal
                    FROM HoaDon hd
                    LEFT JOIN RoomGross rg ON rg.maHoaDon = hd.maHoaDon
                    LEFT JOIN SvcGross sg ON sg.maHoaDon = hd.maHoaDon
                    WHERE hd.trangThai = N'Đã thanh toán'
                      AND CAST(hd.ngayTao AS DATE) BETWEEN ? AND ?
                )
                SELECT
                    SUM(CASE WHEN grossRoom + grossSvc > 0 THEN grossRoom ELSE netTotal END) AS DoanhThuPhong,
                    SUM(CASE WHEN grossRoom + grossSvc > 0 THEN grossSvc ELSE 0 END) AS DoanhThuDichVu
                FROM PerBill
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double dtPhong = rs.getDouble("DoanhThuPhong");
                double dtDv = rs.getDouble("DoanhThuDichVu");
                result.add(new GroupSeriesPoint("Doanh thu phòng", dtPhong, (int) Math.round(dtPhong)));
                result.add(new GroupSeriesPoint("Doanh thu dịch vụ", dtDv, (int) Math.round(dtDv)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy bảng doanh thu theo ngày
     */
    public List<TableRowDoanhThu> getBangDoanhThuTheoNgay(LocalDate fromDate, LocalDate toDate) {
        List<TableRowDoanhThu> result = new ArrayList<>();
        String sql = """
                WITH RoomGross AS (
                    SELECT chd.maHoaDon,
                           SUM(
                               CASE
                                   WHEN DATEDIFF(HOUR, ctpdp.thoiGianNhanPhong, ctpdp.thoiGianTraPhong) <= 0 THEN 1
                                   ELSE CEILING(DATEDIFF(MINUTE, ctpdp.thoiGianNhanPhong, ctpdp.thoiGianTraPhong) / 60.0)
                               END * lp.gia
                           ) AS grossRoom
                    FROM ChiTietHoaDon chd
                    JOIN ChiTietPhieuDatPhong ctpdp ON chd.maPhieuDatPhong = ctpdp.maPhieuDatPhong
                    JOIN Phong p ON ctpdp.maPhong = p.maPhong
                    JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong
                    GROUP BY chd.maHoaDon
                ),
                SvcGross AS (
                    SELECT chd.maHoaDon,
                           SUM(ctdv.soLuong * dv.gia) AS grossSvc
                    FROM ChiTietHoaDon chd
                    JOIN ChiTietPhieuDatPhong ctpdp ON chd.maPhieuDatPhong = ctpdp.maPhieuDatPhong
                    JOIN ChiTietPhieuDatPhong_DichVu ctdv ON ctdv.maPhieuDatPhong = ctpdp.maPhieuDatPhong
                                                      AND ctdv.maPhong = ctpdp.maPhong
                    JOIN DichVu dv ON dv.maDichVu = ctdv.maDichVu
                    GROUP BY chd.maHoaDon
                ),
                PerBill AS (
                    SELECT hd.maHoaDon,
                           CAST(hd.ngayTao AS DATE) AS Ngay,
                           ISNULL(rg.grossRoom, 0) AS grossRoom,
                           ISNULL(sg.grossSvc, 0) AS grossSvc,
                           hd.tongTien AS netTotal
                    FROM HoaDon hd
                    LEFT JOIN RoomGross rg ON rg.maHoaDon = hd.maHoaDon
                    LEFT JOIN SvcGross sg ON sg.maHoaDon = hd.maHoaDon
                    WHERE hd.trangThai = N'Đã thanh toán'
                      AND CAST(hd.ngayTao AS DATE) BETWEEN ? AND ?
                )
                SELECT
                    Ngay,
                    COUNT(*) AS SoHoaDon,
                    SUM(CASE WHEN grossRoom + grossSvc > 0 THEN grossRoom ELSE netTotal END) AS DoanhThuPhong,
                    SUM(CASE WHEN grossRoom + grossSvc > 0 THEN grossSvc ELSE 0 END) AS DoanhThuDichVu,
                    SUM(CASE WHEN grossRoom + grossSvc > 0 THEN grossRoom + grossSvc ELSE netTotal END) AS DoanhThuTong
                FROM PerBill
                GROUP BY Ngay
                ORDER BY Ngay DESC
                """;

        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LocalDate ngay = rs.getDate("Ngay").toLocalDate();
                int soHD = rs.getInt("SoHoaDon");
                double dtPhong = rs.getDouble("DoanhThuPhong");
                double dtDv = rs.getDouble("DoanhThuDichVu");
                double dtTong = rs.getDouble("DoanhThuTong");
                result.add(new TableRowDoanhThu(ngay, soHD, dtPhong, dtDv, dtTong));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // ============================================================
    // 2. TAB ĐẶT PHÒNG
    // ============================================================

    /**
     * Lấy tổng số phiếu đặt phòng
     */
    public int getSoPhieuDatPhong(LocalDate fromDate, LocalDate toDate) {
        String sql = """
                    SELECT COUNT(*) AS SoPhieu
                    FROM PhieuDatPhong
                    WHERE CAST(ngayTao AS DATE) BETWEEN ? AND ?
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("SoPhieu");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy số phiếu theo trạng thái
     */
    public List<GroupSeriesPoint> getSoPhieuTheoTrangThai(LocalDate fromDate, LocalDate toDate) {
        List<GroupSeriesPoint> result = new ArrayList<>();
        String sql = """
                    SELECT trangThai, COUNT(*) AS SoPhieu
                    FROM PhieuDatPhong
                    WHERE CAST(ngayTao AS DATE) BETWEEN ? AND ?
                    GROUP BY trangThai
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String trangThai = rs.getString("trangThai");
                int soPhieu = rs.getInt("SoPhieu");
                result.add(new GroupSeriesPoint(trangThai, soPhieu, soPhieu));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy tổng số đêm lưu trú (room-nights)
     */
    public int getTongSoDemLuuTru(LocalDate fromDate, LocalDate toDate) {
        String sql = """
                    SELECT ISNULL(SUM(DATEDIFF(DAY, ctpdp.thoiGianNhanPhong, ctpdp.thoiGianTraPhong)), 0) AS SoDem
                    FROM ChiTietPhieuDatPhong ctpdp
                    JOIN PhieuDatPhong pdp ON ctpdp.maPhieuDatPhong = pdp.maPhieuDatPhong
                    WHERE CAST(pdp.ngayTao AS DATE) BETWEEN ? AND ?
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("SoDem");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy số phiếu đặt phòng theo ngày
     */
    public List<TimeSeriesPoint> getSoPhieuTheoNgay(LocalDate fromDate, LocalDate toDate) {
        List<TimeSeriesPoint> result = new ArrayList<>();
        String sql = """
                SELECT
                        CAST(ngayTao AS DATE) AS Ngay,
                        COUNT(*) AS SoPhieu
                    FROM PhieuDatPhong
                    WHERE CAST(ngayTao AS DATE) BETWEEN ? AND ?
                    GROUP BY CAST(ngayTao AS DATE)
                    ORDER BY Ngay
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LocalDate ngay = rs.getDate("Ngay").toLocalDate();
                int soPhieu = rs.getInt("SoPhieu");
                result.add(new TimeSeriesPoint(ngay, ngay.toString(), soPhieu, soPhieu));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy số phiếu đặt phòng theo giờ trong một ngày
     */
    public List<TimeSeriesPoint> getSoPhieuTheoGio(LocalDate date) {
        List<TimeSeriesPoint> result = new ArrayList<>();
        String sql = """
                    SELECT
                        DATEPART(HOUR, ngayTao) AS Gio,
                        COUNT(*) AS SoPhieu
                    FROM PhieuDatPhong
                    WHERE CAST(ngayTao AS DATE) = ?
                    GROUP BY DATEPART(HOUR, ngayTao)
                    ORDER BY Gio
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int hour = rs.getInt("Gio");
                int soPhieu = rs.getInt("SoPhieu");
                String label = String.format("%02d:00", hour);
                result.add(new TimeSeriesPoint(date, label, soPhieu, soPhieu, hour));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy cơ cấu theo loại đặt phòng (Online/Offline)
     */
    public List<GroupSeriesPoint> getSoPhieuTheoLoaiDatPhong(LocalDate fromDate, LocalDate toDate) {
        List<GroupSeriesPoint> result = new ArrayList<>();
        String sql = """
                    SELECT
                        ldp.tenLoaiDatPhong,
                        COUNT(*) AS SoChiTiet
                    FROM ChiTietPhieuDatPhong ctpdp
                    JOIN PhieuDatPhong pdp ON ctpdp.maPhieuDatPhong = pdp.maPhieuDatPhong
                    JOIN LoaiDatPhong ldp ON ctpdp.maLoaiDatPhong = ldp.maLoaiDatPhong
                    WHERE CAST(pdp.ngayTao AS DATE) BETWEEN ? AND ?
                    GROUP BY ldp.tenLoaiDatPhong
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String loai = rs.getString("tenLoaiDatPhong");
                int soChiTiet = rs.getInt("SoChiTiet");
                result.add(new GroupSeriesPoint(loai, soChiTiet, soChiTiet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy danh sách phiếu đặt phòng trong kỳ
     */
    public List<TableRowDatPhong> getBangDatPhong(LocalDate fromDate, LocalDate toDate) {
        List<TableRowDatPhong> result = new ArrayList<>();
        String sql = """
                    SELECT
                    pdp.maPhieuDatPhong,
                    pdp.ngayTao,
                        kh.hoTen AS TenKhach,
                    pdp.trangThai,
                        pdp.tienDatCoc
                FROM PhieuDatPhong pdp
                    JOIN KhachHang kh ON pdp.maKhachHang = kh.maKhachHang
                    WHERE CAST(pdp.ngayTao AS DATE) BETWEEN ? AND ?
                ORDER BY pdp.ngayTao DESC
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String maPhieu = rs.getString("maPhieuDatPhong");
                LocalDate ngayTao = rs.getDate("ngayTao").toLocalDate();
                String tenKhach = rs.getString("TenKhach");
                String trangThai = rs.getString("trangThai");
                double tienCoc = rs.getDouble("tienDatCoc");
                result.add(new TableRowDatPhong(maPhieu, ngayTao, tenKhach, trangThai, tienCoc));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // ============================================================
    // HÓA ĐƠN
    // ============================================================

    public List<TimeSeriesPoint> getSoHoaDonTheoNgay(LocalDate fromDate, LocalDate toDate) {
        List<TimeSeriesPoint> result = new ArrayList<>();
        String sql = """
                SELECT CAST(ngayTao AS DATE) AS Ngay, COUNT(*) AS SoHD
                FROM HoaDon
                WHERE CAST(ngayTao AS DATE) BETWEEN ? AND ?
                GROUP BY CAST(ngayTao AS DATE)
                ORDER BY Ngay
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LocalDate ngay = rs.getDate("Ngay").toLocalDate();
                int so = rs.getInt("SoHD");
                result.add(new TimeSeriesPoint(ngay, ngay.toString(), so, so));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<GroupSeriesPoint> getSoHoaDonTheoTrangThai(LocalDate fromDate, LocalDate toDate) {
        List<GroupSeriesPoint> result = new ArrayList<>();
        String sql = """
                SELECT trangThai, COUNT(*) AS SoHD
                FROM HoaDon
                WHERE CAST(ngayTao AS DATE) BETWEEN ? AND ?
                GROUP BY trangThai
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String tt = rs.getString("trangThai");
                int so = rs.getInt("SoHD");
                result.add(new GroupSeriesPoint(tt, so, so));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<TableRowHoaDon> getBangHoaDon(LocalDate fromDate, LocalDate toDate) {
        List<TableRowHoaDon> result = new ArrayList<>();
        String sql = """
                SELECT TOP 200 maHoaDon, CAST(ngayTao AS DATE) AS Ngay, trangThai, tongTien
                FROM HoaDon
                WHERE CAST(ngayTao AS DATE) BETWEEN ? AND ?
                ORDER BY ngayTao DESC
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new TableRowHoaDon(
                        rs.getString("maHoaDon"),
                        rs.getDate("Ngay").toLocalDate(),
                        rs.getString("trangThai"),
                        rs.getDouble("tongTien")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // ============================================================
    // 3. TAB PHÒNG
    // ============================================================

    /**
     * Lấy số phòng theo trạng thái hiện tại
     */
    public List<GroupSeriesPoint> getSoPhongTheoTrangThai() {
        List<GroupSeriesPoint> result = new ArrayList<>();
        String sql = """
                    SELECT trangThai, COUNT(*) AS SoPhong
                    FROM Phong
                    GROUP BY trangThai
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String trangThai = rs.getString("trangThai");
                int soPhong = rs.getInt("SoPhong");
                result.add(new GroupSeriesPoint(trangThai, soPhong, soPhong));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy tổng số phòng
     */
    public int getTongSoPhong() {
        String sql = "SELECT COUNT(*) AS TongPhong FROM Phong";
        try (Connection conn = ConnectDatabase.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("TongPhong");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy số phòng trống
     */
    public int getSoPhongTrong() {
        String sql = "SELECT COUNT(*) AS SoPhongTrong FROM Phong WHERE trangThai = N'Trống'";
        try (Connection conn = ConnectDatabase.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("SoPhongTrong");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy số phòng theo loại phòng
     */
    public List<GroupSeriesPoint> getSoPhongTheoLoaiPhong() {
        List<GroupSeriesPoint> result = new ArrayList<>();
        String sql = """
                SELECT
                    lp.tenLoaiPhong,
                        COUNT(*) AS SoPhong
                    FROM Phong p
                    JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong
                GROUP BY lp.tenLoaiPhong
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String loai = rs.getString("tenLoaiPhong");
                int soPhong = rs.getInt("SoPhong");
                result.add(new GroupSeriesPoint(loai, soPhong, soPhong));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Tính công suất phòng trung bình trong kỳ
     */
    public double getCongSuatPhongTrungBinh(LocalDate fromDate, LocalDate toDate) {
        String sql = """
                    DECLARE @SoNgay INT = DATEDIFF(DAY, ?, ?) + 1;
                    DECLARE @TongPhong INT = (SELECT COUNT(*) FROM Phong);

                SELECT
                        CASE
                            WHEN @TongPhong * @SoNgay = 0 THEN 0
                            ELSE ISNULL(SUM(
                                DATEDIFF(DAY,
                                    CASE WHEN ctpdp.thoiGianNhanPhong < ? THEN ? ELSE CAST(ctpdp.thoiGianNhanPhong AS DATE) END,
                                    CASE WHEN ctpdp.thoiGianTraPhong > DATEADD(DAY,1,?) THEN DATEADD(DAY,1,?) ELSE ctpdp.thoiGianTraPhong END
                                )
                            ), 0) * 100.0 / (@TongPhong * @SoNgay)
                        END AS CongSuatTB
                    FROM ChiTietPhieuDatPhong ctpdp
                    WHERE ctpdp.thoiGianNhanPhong < DATEADD(DAY,1,?)
                      AND ctpdp.thoiGianTraPhong > ?
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ps.setDate(3, Date.valueOf(fromDate));
            ps.setDate(4, Date.valueOf(fromDate));
            ps.setDate(5, Date.valueOf(toDate));
            ps.setDate(6, Date.valueOf(toDate));
            ps.setDate(7, Date.valueOf(toDate));
            ps.setDate(8, Date.valueOf(fromDate));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("CongSuatTB");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy danh sách phòng
     */
    public List<TableRowPhong> getBangPhong(LocalDate fromDate, LocalDate toDate) {
        List<TableRowPhong> result = new ArrayList<>();
        String sql = """
                    WITH Base AS (
                        SELECT
                            p.maPhong,
                            p.soPhong,
                            lp.tenLoaiPhong,
                            lp.gia,
                            p.tang,
                            p.trangThai,
                            p.tinhTrang,
                            ISNULL(SUM(CASE
                                WHEN ctpdp.maPhieuDatPhong IS NULL THEN 0
                                WHEN pdp.trangThai LIKE N'%hủy%' THEN 0
                                ELSE 1 END), 0) AS LuotDat,
                            ISNULL(SUM(CASE
                                WHEN ctpdp.maPhieuDatPhong IS NULL THEN 0
                                WHEN pdp.trangThai LIKE N'%hủy%' THEN 1
                                ELSE 0 END), 0) AS LuotHuy,
                            ISNULL(SUM(
                                CASE
                                    WHEN ctpdp.thoiGianNhanPhong IS NULL OR ctpdp.thoiGianTraPhong IS NULL THEN 0
                                    WHEN DATEDIFF(HOUR, ctpdp.thoiGianNhanPhong, ctpdp.thoiGianTraPhong) <= 0 THEN 1
                                    ELSE CEILING(DATEDIFF(MINUTE, ctpdp.thoiGianNhanPhong, ctpdp.thoiGianTraPhong) / 60.0)
                                END
                            ), 0) AS GioSuDung
                        FROM Phong p
                        JOIN LoaiPhong lp ON p.maLoaiPhong = lp.maLoaiPhong
                        LEFT JOIN ChiTietPhieuDatPhong ctpdp
                            ON ctpdp.maPhong = p.maPhong
                           AND ctpdp.thoiGianNhanPhong < DATEADD(DAY, 1, ?)
                           AND ctpdp.thoiGianTraPhong > ?
                        LEFT JOIN PhieuDatPhong pdp ON ctpdp.maPhieuDatPhong = pdp.maPhieuDatPhong
                        GROUP BY p.maPhong, p.soPhong, lp.tenLoaiPhong, lp.gia, p.tang, p.trangThai, p.tinhTrang
                    )
                    SELECT
                        maPhong, soPhong, tenLoaiPhong, tang, trangThai, tinhTrang,
                        LuotDat,
                        GioSuDung * gia AS DoanhThuPhong,
                        LuotHuy,
                        CASE
                            WHEN (LuotDat + LuotHuy) = 0 THEN 0
                            ELSE (LuotHuy * 100.0) / (LuotDat + LuotHuy)
                        END AS TiLeHuy,
                        CASE
                            WHEN DATEDIFF(HOUR, ?, DATEADD(DAY, 1, ?)) = 0 THEN 0
                            ELSE (GioSuDung * 100.0) / DATEDIFF(HOUR, ?, DATEADD(DAY, 1, ?))
                        END AS CongSuat
                    FROM Base
                    ORDER BY tang, soPhong
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(toDate));
            ps.setDate(2, Date.valueOf(fromDate));
            ps.setDate(3, Date.valueOf(fromDate));
            ps.setDate(4, Date.valueOf(toDate));
            ps.setDate(5, Date.valueOf(fromDate));
            ps.setDate(6, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new TableRowPhong(
                        rs.getString("maPhong"),
                        rs.getString("soPhong"),
                        rs.getString("tenLoaiPhong"),
                        rs.getInt("tang"),
                        rs.getString("trangThai"),
                        rs.getString("tinhTrang"),
                        rs.getInt("LuotDat"),
                        rs.getDouble("DoanhThuPhong"),
                        rs.getDouble("CongSuat"),
                        rs.getInt("LuotHuy"),
                        rs.getDouble("TiLeHuy")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // ============================================================
    // 4. TAB KHÁCH HÀNG
    // ============================================================

    /**
     * Lấy số khách hàng mới trong kỳ
     */
    public int getSoKhachHangMoi(LocalDate fromDate, LocalDate toDate) {
        String sql = """
                    SELECT COUNT(*) AS KhachMoi
                    FROM KhachHang
                    WHERE CAST(ngayTao AS DATE) BETWEEN ? AND ?
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("KhachMoi");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy số khách có hóa đơn trong kỳ
     */
    public int getSoKhachCoHoaDon(LocalDate fromDate, LocalDate toDate) {
        String sql = """
                    SELECT COUNT(DISTINCT maKhachHang) AS KhachCoHoaDon
                    FROM HoaDon
                    WHERE trangThai = N'Đã thanh toán'
                      AND CAST(ngayTao AS DATE) BETWEEN ? AND ?
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("KhachCoHoaDon");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Tính tỷ lệ khách quay lại
     */
    public double getTyLeKhachQuayLai() {
        String sql = """
                    WITH SoLan AS (
                        SELECT maKhachHang, COUNT(*) AS SoHoaDon
                        FROM HoaDon
                        WHERE trangThai = N'Đã thanh toán'
                        GROUP BY maKhachHang
                    )
                        SELECT
                        CASE
                            WHEN COUNT(*) = 0 THEN 0
                            ELSE 100.0 * SUM(CASE WHEN SoHoaDon >= 2 THEN 1 ELSE 0 END) / COUNT(*)
                        END AS TyLeQuayLai
                    FROM SoLan
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("TyLeQuayLai");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy số đánh giá trong kỳ
     */
    public int getSoDanhGia(LocalDate fromDate, LocalDate toDate) {
        String sql = """
                    SELECT COUNT(*) AS SoDanhGia
                    FROM DanhGia
                    WHERE CAST(ngayTao AS DATE) BETWEEN ? AND ?
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("SoDanhGia");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy số khách mới theo ngày
     */
    public List<TimeSeriesPoint> getSoKhachMoiTheoNgay(LocalDate fromDate, LocalDate toDate) {
        List<TimeSeriesPoint> result = new ArrayList<>();
        String sql = """
                        SELECT
                        CAST(ngayTao AS DATE) AS Ngay,
                        COUNT(*) AS KhachMoi
                    FROM KhachHang
                    WHERE CAST(ngayTao AS DATE) BETWEEN ? AND ?
                    GROUP BY CAST(ngayTao AS DATE)
                    ORDER BY Ngay
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LocalDate ngay = rs.getDate("Ngay").toLocalDate();
                int khachMoi = rs.getInt("KhachMoi");
                result.add(new TimeSeriesPoint(ngay, ngay.toString(), khachMoi, khachMoi));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy số khách mới theo giờ trong một ngày
     */
    public List<TimeSeriesPoint> getSoKhachMoiTheoGio(LocalDate date) {
        List<TimeSeriesPoint> result = new ArrayList<>();
        String sql = """
                    SELECT
                        DATEPART(HOUR, ngayTao) AS Gio,
                        COUNT(*) AS KhachMoi
                    FROM KhachHang
                    WHERE CAST(ngayTao AS DATE) = ?
                    GROUP BY DATEPART(HOUR, ngayTao)
                    ORDER BY Gio
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int hour = rs.getInt("Gio");
                int khachMoi = rs.getInt("KhachMoi");
                String label = String.format("%02d:00", hour);
                result.add(new TimeSeriesPoint(date, label, khachMoi, khachMoi, hour));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy top khách hàng theo doanh thu
     */
    public List<TableRowKhachHang> getTopKhachHang(LocalDate fromDate, LocalDate toDate, int top) {
        List<TableRowKhachHang> result = new ArrayList<>();
        String sql = """
                    SELECT TOP (?)
                        kh.maKhachHang,
                        kh.hoTen,
                        COUNT(hd.maHoaDon) AS SoHoaDon,
                        SUM(hd.tongTien) AS TongDoanhThu
                        FROM HoaDon hd
                    JOIN KhachHang kh ON hd.maKhachHang = kh.maKhachHang
                        WHERE hd.trangThai = N'Đã thanh toán'
                      AND CAST(hd.ngayTao AS DATE) BETWEEN ? AND ?
                    GROUP BY kh.maKhachHang, kh.hoTen
                    ORDER BY TongDoanhThu DESC
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, top);
            ps.setDate(2, Date.valueOf(fromDate));
            ps.setDate(3, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new TableRowKhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("hoTen"),
                        rs.getInt("SoHoaDon"),
                        rs.getDouble("TongDoanhThu")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // ============================================================
    // 5. TAB NHÂN VIÊN
    // ============================================================

    /**
     * Lấy số nhân viên đang làm việc
     */
    public int getSoNhanVienDangLamViec() {
        String sql = "SELECT COUNT(*) AS SoNhanVien FROM NhanVien WHERE trangThai = N'Đang làm việc'";
        try (Connection conn = ConnectDatabase.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("SoNhanVien");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy số ca làm việc trong kỳ
     */
    public int getSoCaLamViec(LocalDate fromDate, LocalDate toDate) {
        String sql = """
                    SELECT COUNT(*) AS SoCa
                    FROM CaLamViecNhanVien
                    WHERE ngay BETWEEN ? AND ?
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("SoCa");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy doanh thu theo nhân viên
     */
    public List<GroupSeriesPoint> getDoanhThuTheoNhanVienChart(LocalDate fromDate, LocalDate toDate) {
        return getDoanhThuTheoNhanVien(fromDate, toDate);
    }

    /**
     * Lấy số ca theo nhân viên
     */
    public List<GroupSeriesPoint> getSoCaTheoNhanVien(LocalDate fromDate, LocalDate toDate) {
        List<GroupSeriesPoint> result = new ArrayList<>();
        String sql = """
                    SELECT
                        nv.tenNhanVien,
                        COUNT(*) AS SoCa
                    FROM CaLamViecNhanVien clv
                    JOIN NhanVien nv ON clv.maNhanVien = nv.maNhanVien
                    WHERE clv.ngay BETWEEN ? AND ?
                    GROUP BY nv.tenNhanVien
                    ORDER BY SoCa DESC
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String ten = rs.getString("tenNhanVien");
                int soCa = rs.getInt("SoCa");
                result.add(new GroupSeriesPoint(ten, soCa, soCa));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy bảng thống kê nhân viên
     */
    public List<TableRowNhanVien> getBangNhanVien(LocalDate fromDate, LocalDate toDate) {
        List<TableRowNhanVien> result = new ArrayList<>();
        String sql = """
                    SELECT
                        nv.maNhanVien,
                        nv.tenNhanVien,
                        COUNT(DISTINCT clv.maCaLamViec) AS SoCa,
                        COUNT(DISTINCT hd.maHoaDon) AS SoHoaDon,
                        ISNULL(SUM(hd.tongTien), 0) AS TongDoanhThu
                    FROM NhanVien nv
                    LEFT JOIN CaLamViecNhanVien clv ON nv.maNhanVien = clv.maNhanVien
                         AND clv.ngay BETWEEN ? AND ?
                    LEFT JOIN HoaDon hd ON nv.maNhanVien = hd.maNhanVien
                         AND hd.trangThai = N'Đã thanh toán'
                         AND CAST(hd.ngayTao AS DATE) BETWEEN ? AND ?
                    GROUP BY nv.maNhanVien, nv.tenNhanVien
                    ORDER BY TongDoanhThu DESC
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ps.setDate(3, Date.valueOf(fromDate));
            ps.setDate(4, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new TableRowNhanVien(
                        rs.getString("maNhanVien"),
                        rs.getString("tenNhanVien"),
                        rs.getInt("SoCa"),
                        rs.getInt("SoHoaDon"),
                        rs.getDouble("TongDoanhThu")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // ============================================================
    // 6. TAB KHUYẾN MÃI
    // ============================================================

    /**
     * Lấy số chương trình khuyến mãi đang áp dụng trong kỳ
     */
    public int getSoKhuyenMaiDangApDung(LocalDate fromDate, LocalDate toDate) {
        String sql = """
                    SELECT COUNT(*) AS SoKM
                    FROM KhuyenMai
                    WHERE CAST(ngayBatDau AS DATE) <= ?
                      AND CAST(ngayKetThuc AS DATE) >= ?
                      AND trangThai = 'Đang áp dụng'
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(toDate));
            ps.setDate(2, Date.valueOf(fromDate));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("SoKM");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy số hóa đơn có khuyến mãi
     */
    public int getSoHoaDonCoKhuyenMai(LocalDate fromDate, LocalDate toDate) {
        String sql = """
                    SELECT COUNT(*) AS SoHoaDonCoKM
                    FROM HoaDon
                    WHERE trangThai = N'Đã thanh toán'
                      AND maKhuyenMai IS NOT NULL
                      AND CAST(ngayTao AS DATE) BETWEEN ? AND ?
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("SoHoaDonCoKM");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy tổng doanh thu từ hóa đơn có khuyến mãi
     */
    public double getDoanhThuHoaDonCoKhuyenMai(LocalDate fromDate, LocalDate toDate) {
        String sql = """
                    SELECT ISNULL(SUM(tongTien), 0) AS DoanhThuCoKM
                    FROM HoaDon
                    WHERE trangThai = N'Đã thanh toán'
                      AND maKhuyenMai IS NOT NULL
                      AND CAST(ngayTao AS DATE) BETWEEN ? AND ?
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("DoanhThuCoKM");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy doanh thu theo mã khuyến mãi
     */
    public List<TableRowKhuyenMai> getBangKhuyenMai(LocalDate fromDate, LocalDate toDate) {
        List<TableRowKhuyenMai> result = new ArrayList<>();
        String sql = """
                SELECT
                    km.maKhuyenMai,
                    km.tenKhuyenMai,
                        COUNT(hd.maHoaDon) AS SoHoaDon,
                        SUM(hd.tongTien) AS DoanhThu
                    FROM HoaDon hd
                    JOIN KhuyenMai km ON hd.maKhuyenMai = km.maKhuyenMai
                    WHERE hd.trangThai = N'Đã thanh toán'
                      AND CAST(hd.ngayTao AS DATE) BETWEEN ? AND ?
                    GROUP BY km.maKhuyenMai, km.tenKhuyenMai
                    ORDER BY DoanhThu DESC
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new TableRowKhuyenMai(
                        rs.getString("maKhuyenMai"),
                        rs.getString("tenKhuyenMai"),
                        rs.getInt("SoHoaDon"),
                        rs.getDouble("DoanhThu")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // ============================================================
    // 7. TAB DỊCH VỤ
    // ============================================================

    /**
     * Lấy tổng lượt sử dụng dịch vụ
     */
    public int getTongLuotSuDungDichVu(LocalDate fromDate, LocalDate toDate) {
        String sql = """
                    SELECT ISNULL(SUM(ctpdp_dv.soLuong), 0) AS TongLuot
                    FROM ChiTietPhieuDatPhong_DichVu ctpdp_dv
                    JOIN ChiTietPhieuDatPhong ctpdp
                        ON ctpdp_dv.maPhieuDatPhong = ctpdp.maPhieuDatPhong
                       AND ctpdp_dv.maPhong = ctpdp.maPhong
                    WHERE CAST(ctpdp.thoiGianNhanPhong AS DATE) BETWEEN ? AND ?
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("TongLuot");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy tổng doanh thu dịch vụ
     */
    public double getDoanhThuDichVu(LocalDate fromDate, LocalDate toDate) {
        String sql = """
                    SELECT ISNULL(SUM(ctpdp_dv.soLuong * dv.gia), 0) AS DoanhThuDichVu
                    FROM ChiTietPhieuDatPhong_DichVu ctpdp_dv
                    JOIN ChiTietPhieuDatPhong ctpdp
                        ON ctpdp_dv.maPhieuDatPhong = ctpdp.maPhieuDatPhong
                       AND ctpdp_dv.maPhong = ctpdp.maPhong
                    JOIN DichVu dv ON ctpdp_dv.maDichVu = dv.maDichVu
                    WHERE CAST(ctpdp.thoiGianNhanPhong AS DATE) BETWEEN ? AND ?
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("DoanhThuDichVu");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy lượt sử dụng theo dịch vụ
     */
    public List<GroupSeriesPoint> getLuotSuDungDichVu(LocalDate fromDate, LocalDate toDate) {
        List<GroupSeriesPoint> result = new ArrayList<>();
        String sql = """
                    SELECT
                        dv.tenDichVu,
                        SUM(ctpdp_dv.soLuong) AS SoLuot
                    FROM ChiTietPhieuDatPhong_DichVu ctpdp_dv
                    JOIN ChiTietPhieuDatPhong ctpdp
                        ON ctpdp_dv.maPhieuDatPhong = ctpdp.maPhieuDatPhong
                       AND ctpdp_dv.maPhong = ctpdp.maPhong
                    JOIN DichVu dv ON ctpdp_dv.maDichVu = dv.maDichVu
                    WHERE CAST(ctpdp.thoiGianNhanPhong AS DATE) BETWEEN ? AND ?
                    GROUP BY dv.tenDichVu
                    ORDER BY SoLuot DESC
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String ten = rs.getString("tenDichVu");
                int soLuot = rs.getInt("SoLuot");
                result.add(new GroupSeriesPoint(ten, soLuot, soLuot));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy doanh thu theo loại dịch vụ
     */
    public List<GroupSeriesPoint> getDoanhThuTheoLoaiDichVu(LocalDate fromDate, LocalDate toDate) {
        List<GroupSeriesPoint> result = new ArrayList<>();
        String sql = """
                    SELECT
                        dv.tenDichVu,
                        SUM(ctpdp_dv.soLuong * dv.gia) AS DoanhThu,
                        SUM(ctpdp_dv.soLuong) AS SoLuong
                    FROM ChiTietPhieuDatPhong_DichVu ctpdp_dv
                    JOIN ChiTietPhieuDatPhong ctpdp
                        ON ctpdp_dv.maPhieuDatPhong = ctpdp.maPhieuDatPhong
                       AND ctpdp_dv.maPhong = ctpdp.maPhong
                    JOIN DichVu dv ON ctpdp_dv.maDichVu = dv.maDichVu
                    WHERE CAST(ctpdp.thoiGianNhanPhong AS DATE) BETWEEN ? AND ?
                    GROUP BY dv.tenDichVu
                    ORDER BY DoanhThu DESC
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String ten = rs.getString("tenDichVu");
                double doanhThu = rs.getDouble("DoanhThu");
                int soLuong = rs.getInt("SoLuong");
                result.add(new GroupSeriesPoint(ten, doanhThu, soLuong));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy doanh thu dịch vụ theo ngày
     */
    public List<TimeSeriesPoint> getDoanhThuDichVuTheoNgay(LocalDate fromDate, LocalDate toDate) {
        List<TimeSeriesPoint> result = new ArrayList<>();
        String sql = """
                    SELECT
                        CAST(ctpdp.thoiGianNhanPhong AS DATE) AS Ngay,
                        SUM(ctpdp_dv.soLuong * dv.gia) AS DoanhThuDichVu
                    FROM ChiTietPhieuDatPhong_DichVu ctpdp_dv
                    JOIN ChiTietPhieuDatPhong ctpdp
                        ON ctpdp_dv.maPhieuDatPhong = ctpdp.maPhieuDatPhong
                       AND ctpdp_dv.maPhong = ctpdp.maPhong
                    JOIN DichVu dv ON ctpdp_dv.maDichVu = dv.maDichVu
                    WHERE CAST(ctpdp.thoiGianNhanPhong AS DATE) BETWEEN ? AND ?
                    GROUP BY CAST(ctpdp.thoiGianNhanPhong AS DATE)
                    ORDER BY Ngay
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LocalDate ngay = rs.getDate("Ngay").toLocalDate();
                double doanhThu = rs.getDouble("DoanhThuDichVu");
                result.add(new TimeSeriesPoint(ngay, doanhThu));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy doanh thu dịch vụ theo giờ trong một ngày
     */
    public List<TimeSeriesPoint> getDoanhThuDichVuTheoGio(LocalDate date) {
        List<TimeSeriesPoint> result = new ArrayList<>();
        String sql = """
                    SELECT
                        DATEPART(HOUR, ctpdp.thoiGianNhanPhong) AS Gio,
                        SUM(ctpdp_dv.soLuong * dv.gia) AS DoanhThuDichVu
                    FROM ChiTietPhieuDatPhong_DichVu ctpdp_dv
                    JOIN ChiTietPhieuDatPhong ctpdp
                        ON ctpdp_dv.maPhieuDatPhong = ctpdp.maPhieuDatPhong
                       AND ctpdp_dv.maPhong = ctpdp.maPhong
                    JOIN DichVu dv ON ctpdp_dv.maDichVu = dv.maDichVu
                    WHERE CAST(ctpdp.thoiGianNhanPhong AS DATE) = ?
                    GROUP BY DATEPART(HOUR, ctpdp.thoiGianNhanPhong)
                    ORDER BY Gio
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int hour = rs.getInt("Gio");
                double doanhThu = rs.getDouble("DoanhThuDichVu");
                String label = String.format("%02d:00", hour);
                result.add(new TimeSeriesPoint(date, label, doanhThu, 1, hour));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy bảng thống kê dịch vụ
     */
    public List<TableRowDichVu> getBangDichVu(LocalDate fromDate, LocalDate toDate) {
        List<TableRowDichVu> result = new ArrayList<>();
        String sql = """
                    SELECT
                        dv.maDichVu,
                        dv.tenDichVu,
                        ISNULL(SUM(ctpdp_dv.soLuong), 0) AS TongSoLuong,
                        ISNULL(SUM(ctpdp_dv.soLuong * dv.gia), 0) AS TongDoanhThu
                    FROM DichVu dv
                    LEFT JOIN ChiTietPhieuDatPhong_DichVu ctpdp_dv ON dv.maDichVu = ctpdp_dv.maDichVu
                    LEFT JOIN ChiTietPhieuDatPhong ctpdp
                        ON ctpdp_dv.maPhieuDatPhong = ctpdp.maPhieuDatPhong
                       AND ctpdp_dv.maPhong = ctpdp.maPhong
                       AND CAST(ctpdp.thoiGianNhanPhong AS DATE) BETWEEN ? AND ?
                    GROUP BY dv.maDichVu, dv.tenDichVu
                    ORDER BY TongDoanhThu DESC
                """;
        try (Connection conn = ConnectDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new TableRowDichVu(
                        rs.getString("maDichVu"),
                        rs.getString("tenDichVu"),
                        rs.getInt("TongSoLuong"),
                        rs.getDouble("TongDoanhThu")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================

    /**
     * Lấy tổng số khách hàng
     */
    public int getTongSoKhachHang() {
        String sql = "SELECT COUNT(*) AS TongKhach FROM KhachHang";
        try (Connection conn = ConnectDatabase.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("TongKhach");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
