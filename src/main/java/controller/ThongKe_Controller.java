package controller;

import dao.ThongKe_DAO;
import model.thongke.*;
import model.thongke.TableRowHoaDon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller cho module Thống kê - kết nối GUI với DAO
 * Cung cấp dữ liệu đã format cho UI hiển thị
 */
public class ThongKe_Controller {

    private final ThongKe_DAO dao;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final java.text.NumberFormat CURRENCY_FORMATTER = java.text.NumberFormat
            .getInstance(new java.util.Locale("vi", "VN"));

    public ThongKe_Controller() {
        this.dao = new ThongKe_DAO();
    }

    // ============================================================
    // HELPER: Định dạng số / tiền (dùng chung cho toàn bộ thống kê)
    // ============================================================

    /** Định dạng tiền tệ theo locale Việt Nam (thêm " đ" ở cuối). */
    public static String dinhDangTien(double soTien) {
        return CURRENCY_FORMATTER.format(soTien) + " đ";
    }

    /** Định dạng số nguyên (có dấu chấm ngăn cách phần nghìn). */
    public static String dinhDangSo(int soNguyen) {
        return CURRENCY_FORMATTER.format(soNguyen);
    }

    // ============================================================
    // 1. TAB DOANH THU
    // ============================================================

    /**
     * Lấy KPIs cho tab Doanh thu
     */
    public List<KpiItem> getKpiDoanhThu(TimeFilter filter) {
        List<KpiItem> kpis = new ArrayList<>();
        LocalDate from = filter.getFromDate();
        LocalDate to = filter.getToDate();

        // KPI 1: Tổng doanh thu
        double tongDoanhThu = dao.layTongDoanhThu(from, to);
        kpis.add(new KpiItem("Tổng doanh thu", dinhDangTien(tongDoanhThu),
                "Từ " + from.format(DATE_FORMATTER) + " đến " + to.format(DATE_FORMATTER)));

        // KPI 2: Số hóa đơn
        int soHoaDon = dao.demHoaDon(from, to);
        kpis.add(new KpiItem("Số hóa đơn", dinhDangSo(soHoaDon), "hóa đơn đã thanh toán"));

        // KPI 3: Doanh thu trung bình
        double doanhThuTB = dao.layDoanhThuTrungBinh(from, to);
        kpis.add(new KpiItem("Doanh thu TB/HĐ", dinhDangTien(doanhThuTB), "trung bình mỗi hóa đơn"));

        // KPI 4: Ngày doanh thu cao nhất
        TimeSeriesPoint topDay = dao.layNgayDoanhThuCaoNhat(from, to);
        if (topDay != null) {
            kpis.add(new KpiItem("Ngày cao nhất", topDay.getDate().format(DATE_FORMATTER),
                    dinhDangTien(topDay.getValue())));
        } else {
            kpis.add(new KpiItem("Ngày cao nhất", "N/A", "Chưa có dữ liệu"));
        }

        return kpis;
    }

    /**
     * Lấy dữ liệu biểu đồ xu hướng doanh thu
     */
    public List<TimeSeriesPoint> getChartDoanhThuTheoNgay(TimeFilter filter) {
        return dao.layDoanhThuTheoNgay(filter.getFromDate(), filter.getToDate());
    }

    /**
     * Lấy dữ liệu doanh thu theo giờ (chỉ dùng khi filter theo ngày)
     */
    public List<TimeSeriesPoint> getChartDoanhThuTheoGio(TimeFilter filter) {
        return dao.layDoanhThuTheoGio(filter.getFromDate());
    }

    /**
     * Lấy dữ liệu biểu đồ cơ cấu doanh thu theo nhân viên
     */
    public List<GroupSeriesPoint> getChartDoanhThuTheoNhanVien(TimeFilter filter) {
        return dao.layDoanhThuTheoNhanVien(filter.getFromDate(), filter.getToDate());
    }

    /**
     * Lấy top 5 nhân viên có doanh thu cao nhất
     */
    public List<GroupSeriesPoint> getTop5DoanhThuTheoNhanVien(TimeFilter filter) {
        return dao.layTop5DoanhThuTheoNhanVien(filter.getFromDate(), filter.getToDate());
    }

    /**
     * Lấy dữ liệu biểu đồ cơ cấu doanh thu theo khuyến mãi
     */
    public List<GroupSeriesPoint> getChartDoanhThuTheoKhuyenMai(TimeFilter filter) {
        return dao.layDoanhThuTheoKhuyenMai(filter.getFromDate(), filter.getToDate());
    }

    /**
     * Lấy cơ cấu doanh thu phòng vs dịch vụ
     */
    public List<GroupSeriesPoint> getChartDoanhThuPhongVsDichVu(TimeFilter filter) {
        return dao.layDoanhThuPhongVsDichVu(filter.getFromDate(), filter.getToDate());
    }

    /**
     * Lấy bảng doanh thu theo ngày
     */
    public List<TableRowDoanhThu> getTableDoanhThu(TimeFilter filter) {
        return dao.layBangDoanhThuTheoNgay(filter.getFromDate(), filter.getToDate());
    }

    // ============================================================
    // 2. TAB ĐẶT PHÒNG
    // ============================================================

    /**
     * Lấy KPIs cho tab Đặt phòng
     */
    public List<KpiItem> getKpiDatPhong(TimeFilter filter) {
        List<KpiItem> kpis = new ArrayList<>();
        LocalDate from = filter.getFromDate();
        LocalDate to = filter.getToDate();

        // KPI 1: Tổng số phiếu đặt phòng
        int soPhieu = dao.demPhieuDatPhong(from, to);
        kpis.add(new KpiItem("Tổng phiếu đặt", dinhDangSo(soPhieu), "phiếu trong kỳ"));

        // KPI 2: Số đêm lưu trú
        int soDem = dao.layTongSoDemLuuTru(from, to);
        kpis.add(new KpiItem("Tổng đêm lưu trú", dinhDangSo(soDem), "room-nights"));

        // KPI 3,4: Phân bố theo trạng thái
        List<GroupSeriesPoint> trangThaiList = dao.laySoPhieuTheoTrangThai(from, to);
        for (GroupSeriesPoint g : trangThaiList) {
            kpis.add(new KpiItem(g.getGroupName(), dinhDangSo(g.getCount()), "phiếu"));
        }

        return kpis;
    }

    /**
     * Lấy dữ liệu biểu đồ số phiếu theo ngày
     */
    public List<TimeSeriesPoint> getChartPhieuTheoNgay(TimeFilter filter) {
        return dao.laySoPhieuTheoNgay(filter.getFromDate(), filter.getToDate());
    }

    /**
     * Lấy dữ liệu số phiếu theo giờ (filter ngày)
     */
    public List<TimeSeriesPoint> getChartPhieuTheoGio(TimeFilter filter) {
        return dao.laySoPhieuTheoGio(filter.getFromDate());
    }

    /**
     * Lấy dữ liệu biểu đồ theo loại đặt phòng
     */
    public List<GroupSeriesPoint> getChartTheoLoaiDatPhong(TimeFilter filter) {
        return dao.laySoPhieuTheoLoaiDatPhong(filter.getFromDate(), filter.getToDate());
    }

    /**
     * Lấy bảng đặt phòng
     */
    public List<TableRowDatPhong> getTableDatPhong(TimeFilter filter) {
        return dao.layBangDatPhong(filter.getFromDate(), filter.getToDate());
    }

    // ============================================================
    // 3. TAB PHÒNG
    // ============================================================

    /**
     * Lấy KPIs cho tab Phòng
     */
    public List<KpiItem> getKpiPhong(TimeFilter filter) {
        List<KpiItem> kpis = new ArrayList<>();

        // KPI 1: Tổng số phòng
        int tongPhong = dao.layTongSoPhong();
        kpis.add(new KpiItem("Tổng số phòng", dinhDangSo(tongPhong), "phòng"));

        // KPI 2: Số phòng trống
        int phongTrong = dao.laySoPhongTrong();
        kpis.add(new KpiItem("Phòng trống", dinhDangSo(phongTrong), "phòng hiện tại"));

        // KPI 3: Công suất TB
        double congSuat = dao.layCongSuatPhongTrungBinh(filter.getFromDate(), filter.getToDate());
        kpis.add(new KpiItem("Công suất TB", String.format("%.1f%%", congSuat), "trong kỳ"));

        // KPI 4: Tỷ lệ trống
        double tyLeTrong = tongPhong > 0 ? (phongTrong * 100.0 / tongPhong) : 0;
        kpis.add(new KpiItem("Tỷ lệ trống", String.format("%.1f%%", tyLeTrong), "hiện tại"));

        return kpis;
    }

    /**
     * Lấy dữ liệu biểu đồ phòng theo trạng thái
     */
    public List<GroupSeriesPoint> getChartPhongTheoTrangThai() {
        return dao.laySoPhongTheoTrangThai();
    }

    /**
     * Lấy dữ liệu biểu đồ phòng theo loại
     */
    public List<GroupSeriesPoint> getChartPhongTheoLoai() {
        return dao.laySoPhongTheoLoaiPhong();
    }

    /**
     * Lấy bảng phòng
     */
    public List<TableRowPhong> getTablePhong(TimeFilter filter) {
        return dao.layBangPhong(filter.getFromDate(), filter.getToDate());
    }

    // ============================================================
    // 4. TAB KHÁCH HÀNG
    // ============================================================

    /**
     * Lấy KPIs cho tab Khách hàng
     */
    public List<KpiItem> getKpiKhachHang(TimeFilter filter) {
        List<KpiItem> kpis = new ArrayList<>();
        LocalDate from = filter.getFromDate();
        LocalDate to = filter.getToDate();

        // KPI 1: Tổng khách hàng
        int tongKhach = dao.layTongSoKhachHang();
        kpis.add(new KpiItem("Tổng khách hàng", dinhDangSo(tongKhach), "trong hệ thống"));

        // KPI 2: Khách mới trong kỳ
        int khachMoi = dao.demKhachHangMoi(from, to);
        kpis.add(new KpiItem("Khách mới", dinhDangSo(khachMoi), "trong kỳ"));

        // KPI 3: Khách có hóa đơn
        int khachHoaDon = dao.demKhachCoHoaDon(from, to);
        kpis.add(new KpiItem("Khách có HĐ", dinhDangSo(khachHoaDon), "trong kỳ"));

        // KPI 4: Tỷ lệ quay lại
        double tyLeQuayLai = dao.layTyLeKhachQuayLai();
        kpis.add(new KpiItem("Tỷ lệ quay lại", String.format("%.1f%%", tyLeQuayLai), "tổng"));

        return kpis;
    }

    /**
     * Lấy dữ liệu biểu đồ khách mới theo ngày
     */
    public List<TimeSeriesPoint> getChartKhachMoiTheoNgay(TimeFilter filter) {
        return dao.laySoKhachMoiTheoNgay(filter.getFromDate(), filter.getToDate());
    }

    /**
     * Lấy dữ liệu khách mới theo giờ (filter ngày)
     */
    public List<TimeSeriesPoint> getChartKhachMoiTheoGio(TimeFilter filter) {
        return dao.laySoKhachMoiTheoGio(filter.getFromDate());
    }

    /**
     * Lấy bảng top khách hàng
     */
    public List<TableRowKhachHang> getTableTopKhachHang(TimeFilter filter, int top) {
        return dao.layTopKhachHang(filter.getFromDate(), filter.getToDate(), top);
    }

    // ============================================================
    // 5. TAB NHÂN VIÊN
    // ============================================================

    /**
     * Lấy KPIs cho tab Nhân viên
     */
    public List<KpiItem> getKpiNhanVien(TimeFilter filter) {
        List<KpiItem> kpis = new ArrayList<>();
        LocalDate from = filter.getFromDate();
        LocalDate to = filter.getToDate();

        // KPI 1: Số nhân viên đang làm việc
        int soNV = dao.demNhanVienDangLamViec();
        kpis.add(new KpiItem("Nhân viên", dinhDangSo(soNV), "đang làm việc"));

        // KPI 2: Số ca làm việc
        int soCa = dao.demCaLamViec(from, to);
        kpis.add(new KpiItem("Số ca làm việc", dinhDangSo(soCa), "trong kỳ"));

        // KPI 3,4: Doanh thu TB/NV
        List<GroupSeriesPoint> doanhThuNV = dao.layDoanhThuTheoNhanVien(from, to);
        if (!doanhThuNV.isEmpty()) {
            double total = doanhThuNV.stream().mapToDouble(GroupSeriesPoint::getValue).sum();
            double avg = total / doanhThuNV.size();
            kpis.add(new KpiItem("Doanh thu TB/NV", dinhDangTien(avg), "trung bình"));
            kpis.add(new KpiItem("Tổng doanh thu", dinhDangTien(total), "của nhân viên"));
        }

        return kpis;
    }

    /**
     * Lấy dữ liệu biểu đồ doanh thu theo nhân viên
     */
    public List<GroupSeriesPoint> getChartDoanhThuNhanVien(TimeFilter filter) {
        return dao.layDoanhThuTheoNhanVienChart(filter.getFromDate(), filter.getToDate());
    }

    /**
     * Lấy dữ liệu biểu đồ số ca theo nhân viên
     */
    public List<GroupSeriesPoint> getChartCaTheoNhanVien(TimeFilter filter) {
        return dao.laySoCaTheoNhanVien(filter.getFromDate(), filter.getToDate());
    }

    /**
     * Lấy bảng nhân viên
     */
    public List<TableRowNhanVien> getTableNhanVien(TimeFilter filter) {
        return dao.layBangNhanVien(filter.getFromDate(), filter.getToDate());
    }

    // ============================================================
    // 6. TAB KHUYẾN MÃI
    // ============================================================

    /**
     * Lấy KPIs cho tab Khuyến mãi
     */
    public List<KpiItem> getKpiKhuyenMai(TimeFilter filter) {
        List<KpiItem> kpis = new ArrayList<>();
        LocalDate from = filter.getFromDate();
        LocalDate to = filter.getToDate();

        // KPI 1: Số KM đang áp dụng
        int soKM = dao.demKhuyenMaiDangApDung(from, to);
        kpis.add(new KpiItem("KM đang áp dụng", dinhDangSo(soKM), "chương trình"));

        // KPI 2: Số HĐ có KM
        int soHDCoKM = dao.demHoaDonCoKhuyenMai(from, to);
        kpis.add(new KpiItem("HĐ có khuyến mãi", dinhDangSo(soHDCoKM), "hóa đơn"));

        // KPI 3: Doanh thu từ HĐ có KM
        double doanhThuKM = dao.layDoanhThuHoaDonCoKhuyenMai(from, to);
        kpis.add(new KpiItem("Doanh thu có KM", dinhDangTien(doanhThuKM), "tổng"));

        // KPI 4: Tỷ lệ HĐ có KM
        int tongHD = dao.demHoaDon(from, to);
        double tyLe = tongHD > 0 ? (soHDCoKM * 100.0 / tongHD) : 0;
        kpis.add(new KpiItem("Tỷ lệ HĐ có KM", String.format("%.1f%%", tyLe), "trong tổng số HĐ"));

        return kpis;
    }

    /**
     * Lấy bảng khuyến mãi
     */
    public List<TableRowKhuyenMai> getTableKhuyenMai(TimeFilter filter) {
        return dao.layBangKhuyenMai(filter.getFromDate(), filter.getToDate());
    }

    // ============================================================
    // 8. TAB HÓA ĐƠN
    // ============================================================

    public List<KpiItem> getKpiHoaDon(TimeFilter filter) {
        List<KpiItem> kpis = new ArrayList<>();
        LocalDate from = filter.getFromDate();
        LocalDate to = filter.getToDate();

        int soHD = dao.demHoaDon(from, to);
        kpis.add(new KpiItem("Số hóa đơn", dinhDangSo(soHD), "trong kỳ"));

        double tong = dao.layTongDoanhThu(from, to);
        kpis.add(new KpiItem("Tổng tiền", dinhDangTien(tong), "đã thanh toán"));

        double avg = dao.layDoanhThuTrungBinh(from, to);
        kpis.add(new KpiItem("TB/HĐ", dinhDangTien(avg), "trung bình"));

        TimeSeriesPoint topDay = dao.layNgayDoanhThuCaoNhat(from, to);
        if (topDay != null && topDay.getDate() != null) {
            kpis.add(new KpiItem("Ngày cao nhất", topDay.getDate().format(DATE_FORMATTER),
                    dinhDangTien(topDay.getValue())));
        } else {
            kpis.add(new KpiItem("Ngày cao nhất", "N/A", "Chưa có dữ liệu"));
        }
        return kpis;
    }

    public List<TimeSeriesPoint> getChartHoaDonTheoNgay(TimeFilter filter) {
        return dao.laySoHoaDonTheoNgay(filter.getFromDate(), filter.getToDate());
    }

    public List<GroupSeriesPoint> getChartHoaDonTheoTrangThai(TimeFilter filter) {
        return dao.laySoHoaDonTheoTrangThai(filter.getFromDate(), filter.getToDate());
    }

    public List<TableRowHoaDon> getTableHoaDon(TimeFilter filter) {
        return dao.layBangHoaDon(filter.getFromDate(), filter.getToDate());
    }

    // ============================================================
    // 7. TAB DỊCH VỤ
    // ============================================================

    /**
     * Lấy KPIs cho tab Dịch vụ
     */
    public List<KpiItem> getKpiDichVu(TimeFilter filter) {
        List<KpiItem> kpis = new ArrayList<>();
        LocalDate from = filter.getFromDate();
        LocalDate to = filter.getToDate();

        // KPI 1: Tổng lượt sử dụng
        int tongLuot = dao.layTongLuotSuDungDichVu(from, to);
        kpis.add(new KpiItem("Tổng lượt dùng", dinhDangSo(tongLuot), "lượt"));

        // KPI 2: Doanh thu dịch vụ
        double doanhThu = dao.layDoanhThuDichVu(from, to);
        kpis.add(new KpiItem("Doanh thu DV", dinhDangTien(doanhThu), "tổng"));

        // KPI 3: Doanh thu TB/lượt
        double doanhThuTB = tongLuot > 0 ? doanhThu / tongLuot : 0;
        kpis.add(new KpiItem("DT TB/lượt", dinhDangTien(doanhThuTB), "trung bình"));

        return kpis;
    }

    /**
     * Lấy dữ liệu biểu đồ doanh thu theo loại dịch vụ
     */
    public List<GroupSeriesPoint> getChartDoanhThuDichVu(TimeFilter filter) {
        return dao.layDoanhThuTheoLoaiDichVu(filter.getFromDate(), filter.getToDate());
    }

    /**
     * Lấy dữ liệu lượt sử dụng dịch vụ
     */
    public List<GroupSeriesPoint> getChartLuotSuDungDichVu(TimeFilter filter) {
        return dao.layLuotSuDungDichVu(filter.getFromDate(), filter.getToDate());
    }

    /**
     * Lấy dữ liệu biểu đồ doanh thu dịch vụ theo ngày
     */
    public List<TimeSeriesPoint> getChartDichVuTheoNgay(TimeFilter filter) {
        return dao.layDoanhThuDichVuTheoNgay(filter.getFromDate(), filter.getToDate());
    }

    /**
     * Lấy dữ liệu doanh thu dịch vụ theo giờ (filter ngày)
     */
    public List<TimeSeriesPoint> getChartDichVuTheoGio(TimeFilter filter) {
        return dao.layDoanhThuDichVuTheoGio(filter.getFromDate());
    }

    /**
     * Lấy bảng dịch vụ
     */
    public List<TableRowDichVu> getTableDichVu(TimeFilter filter) {
        return dao.layBangDichVu(filter.getFromDate(), filter.getToDate());
    }
}