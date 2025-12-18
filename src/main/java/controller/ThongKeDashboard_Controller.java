package controller;

import dao.*;
import model.ChiTietPhieuDatPhong;
import model.HoaDon;
import model.PhieuDatPhong;
import model.HuyPhong;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

public class ThongKeDashboard_Controller {

    private final Phong_DAO phongDAO = new Phong_DAO();
    private final HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
    private final KhachHang_DAO khachHangDAO = new KhachHang_DAO();
    private final PhieuDatPhong_DAO phieuDatPhongDAO = new PhieuDatPhong_DAO();
    private final ChiTietPhieuDatPhong_DAO ctpdpDAO = new ChiTietPhieuDatPhong_DAO();


    private final HuyPhong_DAO huyPhongDAO = new HuyPhong_DAO();

    // 1) PHÒNG
    public int getTongSoPhong() {
        return phongDAO.countAll();
    }

    public int getSoPhongTrong() {
        return phongDAO.countPhongTrong();
    }

    // 2) TỶ LỆ HỦY PHÒNG (THÁNG NÀY / THÁNG TRƯỚC)

    private static LocalDate firstDayOfMonth(LocalDate d) {
        return d.withDayOfMonth(1);
    }

    private static LocalDate lastDayOfMonth(LocalDate d) {
        return d.withDayOfMonth(d.lengthOfMonth());
    }

    private boolean isInRange(LocalDate date, LocalDate from, LocalDate to) {
        if (date == null) return false;
        return (!date.isBefore(from) && !date.isAfter(to));
    }

    /**
     * Đếm tổng số phiếu đặt PHÁT SINH (tạo) trong khoảng [from..to] theo ngayTao (PhieuDatPhong)
     */
    private int demTongPhieuTao(LocalDate from, LocalDate to) {
        int tong = 0;
        for (PhieuDatPhong pdp : phieuDatPhongDAO.getTatCaPhieuDatPhong()) {
            LocalDate ngayTao = pdp.getNgayTao();
            if (isInRange(ngayTao, from, to)) tong++;
        }
        return tong;
    }

    /**
     * Đếm số phiếu bị hủy trong khoảng [from..to] theo ngayHuy (HuyPhong)
     * - Đếm theo maPhieuDatPhong (distinct) để tránh double nếu lỡ insert trùng.
     */
    private int demSoPhieuHuy(LocalDate from, LocalDate to) {
        Set<String> distinctMaPdp = new HashSet<>();

        for (HuyPhong hp : huyPhongDAO.getTatCaHuyPhong()) {
            LocalDateTime ngayHuyDT = hp.getNgayHuy();     // giả định model trả LocalDateTime
            if (ngayHuyDT == null) continue;

            LocalDate ngayHuy = ngayHuyDT.toLocalDate();
            if (!isInRange(ngayHuy, from, to)) continue;

            String ma = hp.getMaPhieuDatPhong();
            if (ma != null) distinctMaPdp.add(ma);
        }

        return distinctMaPdp.size();
    }

    /**
     * Tỷ lệ hủy = số phiếu hủy / tổng phiếu tạo (trong cùng kỳ)
     * return 0..1
     */
    private double tinhTiLeHuyTheoThang(LocalDate from, LocalDate to) {
        int tongPhieuTao = demTongPhieuTao(from, to);
        if (tongPhieuTao == 0) return 0d;

        int soPhieuHuy = demSoPhieuHuy(from, to);
        return (double) soPhieuHuy / tongPhieuTao;
    }

    /**
     * Tháng này: từ ngày 1 -> hôm nay
     */
    public double getTiLeHuyPhongThangNay() {
        LocalDate today = LocalDate.now();
        LocalDate from = firstDayOfMonth(today);
        LocalDate to = today;
        return tinhTiLeHuyTheoThang(from, to);
    }


    //Tháng trước: nguyên tháng trước (1 -> ngày cuối tháng)

    public double getTiLeHuyPhongThangTruoc() {
        LocalDate today = LocalDate.now();
        LocalDate anyDayLastMonth = today.minusMonths(1);
        LocalDate from = firstDayOfMonth(anyDayLastMonth);
        LocalDate to = lastDayOfMonth(anyDayLastMonth);
        return tinhTiLeHuyTheoThang(from, to);
    }
    // 3) ĐẶT PHÒNG THÁNG NÀY + SO SÁNH
    public int getTongDatPhongThangNay() {
        LocalDate today = LocalDate.now();
        LocalDate from = firstDayOfMonth(today);
        return demTongPhieuTao(from, today);
    }

    public int getTongDatPhongThangTruoc() {
        LocalDate today = LocalDate.now();
        LocalDate anyDayLastMonth = today.minusMonths(1);
        LocalDate from = firstDayOfMonth(anyDayLastMonth);
        LocalDate to = lastDayOfMonth(anyDayLastMonth);
        return demTongPhieuTao(from, to);
    }

    public double getPhanTramThayDoiThangNaySoVoiThangTruoc() {
        int thisMonth = getTongDatPhongThangNay();
        int lastMonth = getTongDatPhongThangTruoc();

        if (lastMonth == 0) {
            if (thisMonth == 0) return 0d;
            return 100d;
        }
        return (thisMonth - lastMonth) * 100.0 / lastMonth;
    }
    // 4) BIỂU ĐỒ CỘT: TỶ LỆ ĐẶT PHÒNG THEO THÁNG (NĂM HIỆN TẠI)
    public static class BookingRatePoint {
        public final String month;   // "Jan"
        public final int percent;    // 0..100

        public BookingRatePoint(String month, int percent) {
            this.month = month;
            this.percent = percent;
        }
    }

    /**
     * Tỷ lệ đặt phòng theo tháng trong năm hiện tại:
     * - Đếm số phiếu tạo theo tháng
     * - Chuẩn hóa theo tháng max => max = 100%
     */
    public List<BookingRatePoint> getBookingRateByMonth() {
        List<PhieuDatPhong> all = phieuDatPhongDAO.getTatCaPhieuDatPhong();
        int year = LocalDate.now().getYear();

        Map<Month, Integer> countByMonth = new EnumMap<>(Month.class);
        for (Month m : Month.values()) countByMonth.put(m, 0);

        for (PhieuDatPhong pdp : all) {
            LocalDate ngay = pdp.getNgayTao();
            if (ngay == null || ngay.getYear() != year) continue;
            Month m = ngay.getMonth();
            countByMonth.put(m, countByMonth.get(m) + 1);
        }

        int max = 0;
        for (int v : countByMonth.values()) max = Math.max(max, v);

        List<BookingRatePoint> result = new ArrayList<>();
        for (Month m : Month.values()) {
            String raw = m.name().toLowerCase();
            String label = Character.toUpperCase(raw.charAt(0)) + raw.substring(1, 3); // Jan

            int count = countByMonth.get(m);
            int percent = (max == 0) ? 0 : (int) Math.round(count * 100.0 / max);

            result.add(new BookingRatePoint(label, percent));
        }
        return result;
    }

    // 5) BIỂU ĐỒ ĐƯỜNG: DOANH THU THEO THÁNG (NĂM HIỆN TẠI)

    public static class RevenuePoint {
        public final int index;     // 1..12
        public final double value;  // tổng doanh thu

        public RevenuePoint(int index, double value) {
            this.index = index;
            this.value = value;
        }
    }

    public List<RevenuePoint> getRevenuePoints() {
        List<HoaDon> all = hoaDonDAO.getAll();
        int year = LocalDate.now().getYear();

        double[] totalByMonth = new double[12];

        for (HoaDon hd : all) {
            LocalDateTime nd = hd.getNgayDat();
            if (nd == null) continue;

            LocalDate d = nd.toLocalDate();
            if (d.getYear() != year) continue;

            int idx = d.getMonthValue() - 1;
            if (idx >= 0 && idx < 12) totalByMonth[idx] += hd.getTongTien();
        }

        List<RevenuePoint> result = new ArrayList<>();
        for (int i = 0; i < 12; i++) result.add(new RevenuePoint(i + 1, totalByMonth[i]));
        return result;
    }


    // 6) KHÁCH HÀNG + VIỆC HÔM NAY
    public int getTongSoKhachHang() {
        return khachHangDAO.countTongKhachHang();
    }

    public int getSoKhachHangMoiThangNay() {
        return khachHangDAO.countKhachHangMoiThangNay();
    }

    public int getSoHoaDonChoThanhToan() {
        return hoaDonDAO.countHoaDonDangCho();
    }

    public int getSoCheckInHomNay() {
        return ctpdpDAO.demCheckInHomNay();
    }

    public int getSoCheckOutHomNay() {
        return ctpdpDAO.demCheckOutHomNay();
    }

    public int getSoPhongSapTraTrong24h() {
        return ctpdpDAO.demPhongSapTraTrong24h();
    }
}
