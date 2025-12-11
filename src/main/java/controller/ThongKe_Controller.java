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
    // HELPER: Format số tiền
    // ============================================================

    public static String formatCurrency(double amount) {
        return CURRENCY_FORMATTER.format(amount) + " đ";
    }

    public static String formatNumber(int number) {
        return CURRENCY_FORMATTER.format(number);
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
        double tongDoanhThu = dao.getTongDoanhThu(from, to);
        kpis.add(new KpiItem("Tổng doanh thu", formatCurrency(tongDoanhThu),
                "Từ " + from.format(DATE_FORMATTER) + " đến " + to.format(DATE_FORMATTER)));

        // KPI 2: Số hóa đơn
        int soHoaDon = dao.getSoHoaDon(from, to);
        kpis.add(new KpiItem("Số hóa đơn", formatNumber(soHoaDon), "hóa đơn đã thanh toán"));

        // KPI 3: Doanh thu trung bình
        double doanhThuTB = dao.getDoanhThuTrungBinh(from, to);
        kpis.add(new KpiItem("Doanh thu TB/HĐ", formatCurrency(doanhThuTB), "trung bình mỗi hóa đơn"));

        // KPI 4: Ngày doanh thu cao nhất
        TimeSeriesPoint topDay = dao.getNgayDoanhThuCaoNhat(from, to);
        if (topDay != null) {
            kpis.add(new KpiItem("Ngày cao nhất", topDay.getDate().format(DATE_FORMATTER),
                    formatCurrency(topDay.getValue())));
        } else {
            kpis.add(new KpiItem("Ngày cao nhất", "N/A", "Chưa có dữ liệu"));
        }

        return kpis;
    }

    /**
     * Lấy dữ liệu biểu đồ xu hướng doanh thu
     */
    public List<TimeSeriesPoint> getChartDoanhThuTheoNgay(TimeFilter filter) {
        return dao.getDoanhThuTheoNgay(filter.getFromDate(), filter.getToDate());
    }

    /**
     * Lấy dữ liệu doanh thu theo giờ (chỉ dùng khi filter theo ngày)
     */
    public List<TimeSeriesPoint> getChartDoanhThuTheoGio(TimeFilter filter) {
        return dao.getDoanhThuTheoGio(filter.getFromDate());
    }

    /**
     * Lấy dữ liệu biểu đồ cơ cấu doanh thu theo nhân viên
     */
    public List<GroupSeriesPoint> getChartDoanhThuTheoNhanVien(TimeFilter filter) {
        return dao.getDoanhThuTheoNhanVien(filter.getFromDate(), filter.getToDate());
    }

    /**
     * Lấy top 5 nhân viên có doanh thu cao nhất
     */
    public List<GroupSeriesPoint> getTop5DoanhThuTheoNhanVien(TimeFilter filter) {
        return dao.getTop5DoanhThuTheoNhanVien(filter.getFromDate(), filter.getToDate());
    }

    /**
     * Lấy dữ liệu biểu đồ cơ cấu doanh thu theo khuyến mãi
     */
    public List<GroupSeriesPoint> getChartDoanhThuTheoKhuyenMai(TimeFilter filter) {
        return dao.getDoanhThuTheoKhuyenMai(filter.getFromDate(), filter.getToDate());
    }

    /**
     * Lấy cơ cấu doanh thu phòng vs dịch vụ
     */
    public List<GroupSeriesPoint> getChartDoanhThuPhongVsDichVu(TimeFilter filter) {
        return dao.getDoanhThuPhongVsDichVu(filter.getFromDate(), filter.getToDate());
    }

    /**
     * Lấy bảng doanh thu theo ngày
     */
    public List<TableRowDoanhThu> getTableDoanhThu(TimeFilter filter) {
        return dao.getBangDoanhThuTheoNgay(filter.getFromDate(), filter.getToDate());
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
        int soPhieu = dao.getSoPhieuDatPhong(from, to);
        kpis.add(new KpiItem("Tổng phiếu đặt", formatNumber(soPhieu), "phiếu trong kỳ"));

        // KPI 2: Số đêm lưu trú
        int soDem = dao.getTongSoDemLuuTru(from, to);
        kpis.add(new KpiItem("Tổng đêm lưu trú", formatNumber(soDem), "room-nights"));

        // KPI 3,4: Phân bố theo trạng thái
        List<GroupSeriesPoint> trangThaiList = dao.getSoPhieuTheoTrangThai(from, to);
        for (GroupSeriesPoint g : trangThaiList) {
            kpis.add(new KpiItem(g.getGroupName(), formatNumber(g.getCount()), "phiếu"));
        }

        return kpis;
    }

    /**
     * Lấy dữ liệu biểu đồ số phiếu theo ngày
     */
    public List<TimeSeriesPoint> getChartPhieuTheoNgay(TimeFilter filter) {
        return dao.getSoPhieuTheoNgay(filter.getFromDate(), filter.getToDate());
    }

    /**
     * Lấy dữ liệu số phiếu theo giờ (filter ngày)
     */
    public List<TimeSeriesPoint> getChartPhieuTheoGio(TimeFilter filter) {
        return dao.getSoPhieuTheoGio(filter.getFromDate());
    }

    /**
     * Lấy dữ liệu biểu đồ theo loại đặt phòng
     */
    public List<GroupSeriesPoint> getChartTheoLoaiDatPhong(TimeFilter filter) {
        return dao.getSoPhieuTheoLoaiDatPhong(filter.getFromDate(), filter.getToDate());
    }

    /**
     * Lấy bảng đặt phòng
     */
    public List<TableRowDatPhong> getTableDatPhong(TimeFilter filter) {
        return dao.getBangDatPhong(filter.getFromDate(), filter.getToDate());
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
        int tongPhong = dao.getTongSoPhong();
        kpis.add(new KpiItem("Tổng số phòng", formatNumber(tongPhong), "phòng"));

        // KPI 2: Số phòng trống
        int phongTrong = dao.getSoPhongTrong();
        kpis.add(new KpiItem("Phòng trống", formatNumber(phongTrong), "phòng hiện tại"));

        // KPI 3: Công suất TB
        double congSuat = dao.getCongSuatPhongTrungBinh(filter.getFromDate(), filter.getToDate());
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
        return dao.getSoPhongTheoTrangThai();
    }

    /**
     * Lấy dữ liệu biểu đồ phòng theo loại
     */
    public List<GroupSeriesPoint> getChartPhongTheoLoai() {
        return dao.getSoPhongTheoLoaiPhong();
    }

    /**
     * Lấy bảng phòng
     */
    public List<TableRowPhong> getTablePhong(TimeFilter filter) {
        return dao.getBangPhong(filter.getFromDate(), filter.getToDate());
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
        int tongKhach = dao.getTongSoKhachHang();
        kpis.add(new KpiItem("Tổng khách hàng", formatNumber(tongKhach), "trong hệ thống"));

        // KPI 2: Khách mới trong kỳ
        int khachMoi = dao.getSoKhachHangMoi(from, to);
        kpis.add(new KpiItem("Khách mới", formatNumber(khachMoi), "trong kỳ"));

        // KPI 3: Khách có hóa đơn
        int khachHoaDon = dao.getSoKhachCoHoaDon(from, to);
        kpis.add(new KpiItem("Khách có HĐ", formatNumber(khachHoaDon), "trong kỳ"));

        // KPI 4: Tỷ lệ quay lại
        double tyLeQuayLai = dao.getTyLeKhachQuayLai();
        kpis.add(new KpiItem("Tỷ lệ quay lại", String.format("%.1f%%", tyLeQuayLai), "tổng"));

        return kpis;
    }

    /**
     * Lấy dữ liệu biểu đồ khách mới theo ngày
     */
    public List<TimeSeriesPoint> getChartKhachMoiTheoNgay(TimeFilter filter) {
        return dao.getSoKhachMoiTheoNgay(filter.getFromDate(), filter.getToDate());
    }

    /**
     * Lấy dữ liệu khách mới theo giờ (filter ngày)
     */
    public List<TimeSeriesPoint> getChartKhachMoiTheoGio(TimeFilter filter) {
        return dao.getSoKhachMoiTheoGio(filter.getFromDate());
    }

    /**
     * Lấy bảng top khách hàng
     */
    public List<TableRowKhachHang> getTableTopKhachHang(TimeFilter filter, int top) {
        return dao.getTopKhachHang(filter.getFromDate(), filter.getToDate(), top);
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
        int soNV = dao.getSoNhanVienDangLamViec();
        kpis.add(new KpiItem("Nhân viên", formatNumber(soNV), "đang làm việc"));

        // KPI 2: Số ca làm việc
        int soCa = dao.getSoCaLamViec(from, to);
        kpis.add(new KpiItem("Số ca làm việc", formatNumber(soCa), "trong kỳ"));

        // KPI 3,4: Doanh thu TB/NV
        List<GroupSeriesPoint> doanhThuNV = dao.getDoanhThuTheoNhanVien(from, to);
        if (!doanhThuNV.isEmpty()) {
            double total = doanhThuNV.stream().mapToDouble(GroupSeriesPoint::getValue).sum();
            double avg = total / doanhThuNV.size();
            kpis.add(new KpiItem("Doanh thu TB/NV", formatCurrency(avg), "trung bình"));
            kpis.add(new KpiItem("Tổng doanh thu", formatCurrency(total), "của nhân viên"));
        }

        return kpis;
    }

    /**
     * Lấy dữ liệu biểu đồ doanh thu theo nhân viên
     */
    public List<GroupSeriesPoint> getChartDoanhThuNhanVien(TimeFilter filter) {
        return dao.getDoanhThuTheoNhanVienChart(filter.getFromDate(), filter.getToDate());
    }

    /**
     * Lấy dữ liệu biểu đồ số ca theo nhân viên
     */
    public List<GroupSeriesPoint> getChartCaTheoNhanVien(TimeFilter filter) {
        return dao.getSoCaTheoNhanVien(filter.getFromDate(), filter.getToDate());
    }

    /**
     * Lấy bảng nhân viên
     */
    public List<TableRowNhanVien> getTableNhanVien(TimeFilter filter) {
        return dao.getBangNhanVien(filter.getFromDate(), filter.getToDate());
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
        int soKM = dao.getSoKhuyenMaiDangApDung(from, to);
        kpis.add(new KpiItem("KM đang áp dụng", formatNumber(soKM), "chương trình"));

        // KPI 2: Số HĐ có KM
        int soHDCoKM = dao.getSoHoaDonCoKhuyenMai(from, to);
        kpis.add(new KpiItem("HĐ có khuyến mãi", formatNumber(soHDCoKM), "hóa đơn"));

        // KPI 3: Doanh thu từ HĐ có KM
        double doanhThuKM = dao.getDoanhThuHoaDonCoKhuyenMai(from, to);
        kpis.add(new KpiItem("Doanh thu có KM", formatCurrency(doanhThuKM), "tổng"));

        // KPI 4: Tỷ lệ HĐ có KM
        int tongHD = dao.getSoHoaDon(from, to);
        double tyLe = tongHD > 0 ? (soHDCoKM * 100.0 / tongHD) : 0;
        kpis.add(new KpiItem("Tỷ lệ HĐ có KM", String.format("%.1f%%", tyLe), "trong tổng số HĐ"));

        return kpis;
    }

    /**
     * Lấy bảng khuyến mãi
     */
    public List<TableRowKhuyenMai> getTableKhuyenMai(TimeFilter filter) {
        return dao.getBangKhuyenMai(filter.getFromDate(), filter.getToDate());
    }

    // ============================================================
    // 8. TAB HÓA ĐƠN
    // ============================================================

    public List<KpiItem> getKpiHoaDon(TimeFilter filter) {
        List<KpiItem> kpis = new ArrayList<>();
        LocalDate from = filter.getFromDate();
        LocalDate to = filter.getToDate();

        int soHD = dao.getSoHoaDon(from, to);
        kpis.add(new KpiItem("Số hóa đơn", formatNumber(soHD), "trong kỳ"));

        double tong = dao.getTongDoanhThu(from, to);
        kpis.add(new KpiItem("Tổng tiền", formatCurrency(tong), "đã thanh toán"));

        double avg = dao.getDoanhThuTrungBinh(from, to);
        kpis.add(new KpiItem("TB/HĐ", formatCurrency(avg), "trung bình"));

        TimeSeriesPoint topDay = dao.getNgayDoanhThuCaoNhat(from, to);
        if (topDay != null && topDay.getDate() != null) {
            kpis.add(new KpiItem("Ngày cao nhất", topDay.getDate().format(DATE_FORMATTER),
                    formatCurrency(topDay.getValue())));
        } else {
            kpis.add(new KpiItem("Ngày cao nhất", "N/A", "Chưa có dữ liệu"));
        }
        return kpis;
    }

    public List<TimeSeriesPoint> getChartHoaDonTheoNgay(TimeFilter filter) {
        return dao.getSoHoaDonTheoNgay(filter.getFromDate(), filter.getToDate());
    }

    public List<GroupSeriesPoint> getChartHoaDonTheoTrangThai(TimeFilter filter) {
        return dao.getSoHoaDonTheoTrangThai(filter.getFromDate(), filter.getToDate());
    }

    public List<TableRowHoaDon> getTableHoaDon(TimeFilter filter) {
        return dao.getBangHoaDon(filter.getFromDate(), filter.getToDate());
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
        int tongLuot = dao.getTongLuotSuDungDichVu(from, to);
        kpis.add(new KpiItem("Tổng lượt dùng", formatNumber(tongLuot), "lượt"));

        // KPI 2: Doanh thu dịch vụ
        double doanhThu = dao.getDoanhThuDichVu(from, to);
        kpis.add(new KpiItem("Doanh thu DV", formatCurrency(doanhThu), "tổng"));

        // KPI 3: Doanh thu TB/lượt
        double doanhThuTB = tongLuot > 0 ? doanhThu / tongLuot : 0;
        kpis.add(new KpiItem("DT TB/lượt", formatCurrency(doanhThuTB), "trung bình"));

        return kpis;
    }

    /**
     * Lấy dữ liệu biểu đồ doanh thu theo loại dịch vụ
     */
    public List<GroupSeriesPoint> getChartDoanhThuDichVu(TimeFilter filter) {
        return dao.getDoanhThuTheoLoaiDichVu(filter.getFromDate(), filter.getToDate());
    }

    /**
     * Lấy dữ liệu lượt sử dụng dịch vụ
     */
    public List<GroupSeriesPoint> getChartLuotSuDungDichVu(TimeFilter filter) {
        return dao.getLuotSuDungDichVu(filter.getFromDate(), filter.getToDate());
    }

    /**
     * Lấy dữ liệu biểu đồ doanh thu dịch vụ theo ngày
     */
    public List<TimeSeriesPoint> getChartDichVuTheoNgay(TimeFilter filter) {
        return dao.getDoanhThuDichVuTheoNgay(filter.getFromDate(), filter.getToDate());
    }

    /**
     * Lấy dữ liệu doanh thu dịch vụ theo giờ (filter ngày)
     */
    public List<TimeSeriesPoint> getChartDichVuTheoGio(TimeFilter filter) {
        return dao.getDoanhThuDichVuTheoGio(filter.getFromDate());
    }

    /**
     * Lấy bảng dịch vụ
     */
    public List<TableRowDichVu> getTableDichVu(TimeFilter filter) {
        return dao.getBangDichVu(filter.getFromDate(), filter.getToDate());
    }
}
