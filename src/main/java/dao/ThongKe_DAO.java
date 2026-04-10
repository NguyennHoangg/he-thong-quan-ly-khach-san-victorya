package dao;

import model.thongke.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ThongKe_DAO {
    public double layTongDoanhThu(LocalDate fromDate, LocalDate toDate) { return 0.0; }
    public int demHoaDon(LocalDate fromDate, LocalDate toDate) { return 0; }
    public double layDoanhThuTrungBinh(LocalDate fromDate, LocalDate toDate) { return 0.0; }
    public TimeSeriesPoint layNgayDoanhThuCaoNhat(LocalDate fromDate, LocalDate toDate) { return null; }
    public List<TimeSeriesPoint> layDoanhThuTheoNgay(LocalDate fromDate, LocalDate toDate) { return new ArrayList<>(); }
    public List<TimeSeriesPoint> layDoanhThuTheoGio(LocalDate date) { return new ArrayList<>(); }
    public List<GroupSeriesPoint> layDoanhThuTheoNhanVien(LocalDate fromDate, LocalDate toDate) { return new ArrayList<>(); }
    public List<GroupSeriesPoint> layTop5DoanhThuTheoNhanVien(LocalDate fromDate, LocalDate toDate) { return new ArrayList<>(); }
    public List<GroupSeriesPoint> layDoanhThuTheoKhuyenMai(LocalDate fromDate, LocalDate toDate) { return new ArrayList<>(); }
    public List<GroupSeriesPoint> layDoanhThuPhongVsDichVu(LocalDate fromDate, LocalDate toDate) { return new ArrayList<>(); }
    public List<TableRowDoanhThu> layBangDoanhThuTheoNgay(LocalDate fromDate, LocalDate toDate) { return new ArrayList<>(); }
    public int demPhieuDatPhong(LocalDate fromDate, LocalDate toDate) { return 0; }
    public List<GroupSeriesPoint> laySoPhieuTheoTrangThai(LocalDate fromDate, LocalDate toDate) { return new ArrayList<>(); }
    public int layTongSoDemLuuTru(LocalDate fromDate, LocalDate toDate) { return 0; }
    public List<TimeSeriesPoint> laySoPhieuTheoNgay(LocalDate fromDate, LocalDate toDate) { return new ArrayList<>(); }
    public List<TimeSeriesPoint> laySoPhieuTheoGio(LocalDate date) { return new ArrayList<>(); }
    public List<GroupSeriesPoint> laySoPhieuTheoLoaiDatPhong(LocalDate fromDate, LocalDate toDate) { return new ArrayList<>(); }
    public List<TableRowDatPhong> layBangDatPhong(LocalDate fromDate, LocalDate toDate) { return new ArrayList<>(); }
    public List<TimeSeriesPoint> laySoHoaDonTheoNgay(LocalDate fromDate, LocalDate toDate) { return new ArrayList<>(); }
    public List<GroupSeriesPoint> laySoHoaDonTheoTrangThai(LocalDate fromDate, LocalDate toDate) { return new ArrayList<>(); }
    public List<TableRowHoaDon> layBangHoaDon(LocalDate fromDate, LocalDate toDate) { return new ArrayList<>(); }
    public List<GroupSeriesPoint> laySoPhongTheoTrangThai() { return new ArrayList<>(); }
    public int layTongSoPhong() { return 0; }
    public int laySoPhongTrong() { return 0; }
    public List<GroupSeriesPoint> laySoPhongTheoLoaiPhong() { return new ArrayList<>(); }
    public double layCongSuatPhongTrungBinh(LocalDate fromDate, LocalDate toDate) { return 0.0; }
    public List<TableRowPhong> layBangPhong(LocalDate fromDate, LocalDate toDate) { return new ArrayList<>(); }
    public int demKhachHangMoi(LocalDate fromDate, LocalDate toDate) { return 0; }
    public int demKhachCoHoaDon(LocalDate fromDate, LocalDate toDate) { return 0; }
    public double layTyLeKhachQuayLai() { return 0.0; }
    public int demDanhGia(LocalDate fromDate, LocalDate toDate) { return 0; }
    public List<TimeSeriesPoint> laySoKhachMoiTheoNgay(LocalDate fromDate, LocalDate toDate) { return new ArrayList<>(); }
    public List<TimeSeriesPoint> laySoKhachMoiTheoGio(LocalDate date) { return new ArrayList<>(); }
    public List<TableRowKhachHang> layTopKhachHang(LocalDate fromDate, LocalDate toDate, int top) { return new ArrayList<>(); }
    public int demNhanVienDangLamViec() { return 0; }
    public int demCaLamViec(LocalDate fromDate, LocalDate toDate) { return 0; }
    public List<GroupSeriesPoint> layDoanhThuTheoNhanVienChart(LocalDate fromDate, LocalDate toDate) { return new ArrayList<>(); }
    public List<GroupSeriesPoint> laySoCaTheoNhanVien(LocalDate fromDate, LocalDate toDate) { return new ArrayList<>(); }
    public List<TableRowNhanVien> layBangNhanVien(LocalDate fromDate, LocalDate toDate) { return new ArrayList<>(); }
    public int demKhuyenMaiDangApDung(LocalDate fromDate, LocalDate toDate) { return 0; }
    public int demHoaDonCoKhuyenMai(LocalDate fromDate, LocalDate toDate) { return 0; }
    public double layDoanhThuHoaDonCoKhuyenMai(LocalDate fromDate, LocalDate toDate) { return 0.0; }
    public List<TableRowKhuyenMai> layBangKhuyenMai(LocalDate fromDate, LocalDate toDate) { return new ArrayList<>(); }
    public int layTongLuotSuDungDichVu(LocalDate fromDate, LocalDate toDate) { return 0; }
    public double layDoanhThuDichVu(LocalDate fromDate, LocalDate toDate) { return 0.0; }
    public List<GroupSeriesPoint> layLuotSuDungDichVu(LocalDate fromDate, LocalDate toDate) { return new ArrayList<>(); }
    public List<GroupSeriesPoint> layDoanhThuTheoLoaiDichVu(LocalDate fromDate, LocalDate toDate) { return new ArrayList<>(); }
    public List<TimeSeriesPoint> layDoanhThuDichVuTheoNgay(LocalDate fromDate, LocalDate toDate) { return new ArrayList<>(); }
    public List<TimeSeriesPoint> layDoanhThuDichVuTheoGio(LocalDate date) { return new ArrayList<>(); }
    public List<TableRowDichVu> layBangDichVu(LocalDate fromDate, LocalDate toDate) { return new ArrayList<>(); }
    public int layTongSoKhachHang() { return 0; }
}
