
package controller;

import dao.*;
import model.ChiTietPhieuDatPhong;
import model.HoaDon;
import model.PhieuDatPhong;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ThongKeDashboard_Controller {

    private final Phong_DAO phongDAO = new Phong_DAO();
    private final HoaDon_DAO hoaDonDAO = new HoaDon_DAO();              // dùng cho doanh thu
    private final KhachHang_DAO khachHangDAO = new KhachHang_DAO();     // dùng cho khách hàng mới
    private final PhieuDatPhong_DAO phieuDatPhongDAO = new PhieuDatPhong_DAO(); // dùng cho thống kê đặt/hủy
    private final ChiTietPhieuDatPhong_DAO ctpdpDAO = new ChiTietPhieuDatPhong_DAO();


    public int getTongSoPhong() {
        return phongDAO.countAll();
    }


    public int getSoPhongTrong() {
        return phongDAO.countPhongTrong();
    }


    // 2.tỷ lệ hủy phòng trên phiếu đặt phòng


    /** Phiếu được coi là bị hủy nếu trạng thái chứa "hủy" / "huy" */
    private boolean laPhieuBiHuy(PhieuDatPhong pdp) {
        String st = pdp.getTrangThai();
        if (st == null) return false;
        st = st.toLowerCase();
        return st.contains("hủy") || st.contains("huy");
    }

    /**
     * Tính tỷ lệ hủy phòng theo PHIẾU ĐẶT PHÒNG trong khoảng ngày [từ, đến].
     *
     * @param tuNgay   ngày bắt đầu (bao gồm)
     * @param denNgay  ngày kết thúc (bao gồm)
     * @return giá trị từ 0.0 → 1.0
     */
    private double tinhTiLeHuyPhong(LocalDate tuNgay, LocalDate denNgay) {
        List<PhieuDatPhong> ds = phieuDatPhongDAO.getTatCaPhieuDatPhong();

        int tong = 0;
        int soPhieuHuy = 0;

        for (PhieuDatPhong pdp : ds) {
            LocalDate ngay = pdp.getNgayTao();
            if (ngay == null) continue;

            if (ngay.isBefore(tuNgay) || ngay.isAfter(denNgay)) {
                continue; // nằm ngoài khoảng
            }

            tong++;
            if (laPhieuBiHuy(pdp)) {
                soPhieuHuy++;
            }
        }

        if (tong == 0) return 0d;
        return (double) soPhieuHuy / tong;
    }

    /**
     * Tỷ lệ hủy phòng trong TUẦN HIỆN TẠI (thứ 2 → chủ nhật),
     * tính theo ngày tạo phiếu đặt phòng.
     */
    /** Tỷ lệ hủy phòng trong THÁNG NÀY (từ ngày 1 → hôm nay) */
    public double getTiLeHuyPhongThangNay() {
        LocalDate homNay = LocalDate.now();
        LocalDate ngayDauThang = homNay.withDayOfMonth(1);
        return tinhTiLeHuyPhong(ngayDauThang, homNay);
    }

    /** Tỷ lệ hủy phòng trong THÁNG TRƯỚC (theo tháng dương lịch) */
    public double getTiLeHuyPhongThangTruoc() {
        LocalDate homNay = LocalDate.now();
        LocalDate ngayDauThangNay = homNay.withDayOfMonth(1);
        LocalDate ngayDauThangTruoc = ngayDauThangNay.minusMonths(1);
        LocalDate ngayCuoiThangTruoc = ngayDauThangNay.minusDays(1);

        return tinhTiLeHuyPhong(ngayDauThangTruoc, ngayCuoiThangTruoc);
    }

    // 3. ĐẶT PHÒNG THEO THÁNG (PHIẾU ĐẶT PHÒNG)


    /** Tổng số phiếu đặt phòng trong THÁNG NÀY (từ ngày 1 → hôm nay) */
    public int getTongDatPhongThangNay() {
        LocalDate homNay = LocalDate.now();
        LocalDate ngayDauThang = homNay.withDayOfMonth(1);

        int soLuong = 0;
        for (PhieuDatPhong pdp : phieuDatPhongDAO.getTatCaPhieuDatPhong()) {
            LocalDate ngay = pdp.getNgayTao();
            if (ngay == null) continue;

            if (!ngay.isBefore(ngayDauThang) && !ngay.isAfter(homNay)) {
                soLuong++;
            }
        }
        return soLuong;
    }

    /** Tổng số phiếu đặt phòng trong THÁNG TRƯỚC (nguyên tháng) */
    public int getTongDatPhongThangTruoc() {
        LocalDate homNay = LocalDate.now();
        LocalDate ngayDauThangNay = homNay.withDayOfMonth(1);
        LocalDate ngayDauThangTruoc = ngayDauThangNay.minusMonths(1);
        LocalDate ngayCuoiThangTruoc = ngayDauThangNay.minusDays(1);

        int soLuong = 0;
        for (PhieuDatPhong pdp : phieuDatPhongDAO.getTatCaPhieuDatPhong()) {
            LocalDate ngay = pdp.getNgayTao();
            if (ngay == null) continue;

            if (!ngay.isBefore(ngayDauThangTruoc) && !ngay.isAfter(ngayCuoiThangTruoc)) {
                soLuong++;
            }
        }
        return soLuong;
    }

    /**
     * Phần trăm thay đổi số lượng đặt phòng THÁNG NÀY so với THÁNG TRƯỚC.
     * Dương: tăng, âm: giảm.
     */
    public double getPhanTramThayDoiThangNaySoVoiThangTruoc() {
        int thangNay = getTongDatPhongThangNay();
        int thangTruoc = getTongDatPhongThangTruoc();

        if (thangTruoc == 0) {
            if (thangNay == 0) return 0d;
            return 100d; // mặc định: tháng trước = 0, tháng này > 0 → tăng 100%
        }

        return (thangNay - thangTruoc) * 100.0 / thangTruoc;
    }


    // 4. BIỂU ĐỒ CỘT: TỶ LỆ ĐẶT PHÒNG THEO THÁNG (PHIẾU ĐẶT PHÒNG)


    public static class BookingRatePoint {
        public final String month;   // nhãn tháng: "Jan", "Feb", ...
        public final int percent;    // % so với tháng có nhiều đặt phòng nhất

        public BookingRatePoint(String month, int percent) {
            this.month = month;
            this.percent = percent;
        }
    }

    /**
     * Tạo dữ liệu cho biểu đồ cột "Thống kê tỷ lệ đặt phòng".
     * Mỗi tháng là 0–100% so với tháng có số lượng phiếu đặt nhiều nhất trong năm hiện tại.
     */
    public List<BookingRatePoint> getBookingRateByMonth() {
        List<PhieuDatPhong> all = phieuDatPhongDAO.getTatCaPhieuDatPhong();
        LocalDate homNay = LocalDate.now();
        int year = homNay.getYear();

        Map<Month, Integer> countByMonth = new EnumMap<>(Month.class);
        for (Month m : Month.values()) {
            countByMonth.put(m, 0);
        }

        // Đếm số phiếu đặt từng tháng trong năm hiện tại
        for (PhieuDatPhong pdp : all) {
            LocalDate ngay = pdp.getNgayTao();
            if (ngay == null) continue;
            if (ngay.getYear() != year) continue;

            Month m = ngay.getMonth();
            countByMonth.put(m, countByMonth.get(m) + 1);
        }

        // Tìm tháng có nhiều phiếu nhất để chuẩn hóa về 100%
        int max = 0;
        for (int v : countByMonth.values()) {
            if (v > max) max = v;
        }

        List<BookingRatePoint> result = new ArrayList<>();
        for (Month m : Month.values()) {
            String raw = m.name().toLowerCase(); // "january"
            String label = Character.toUpperCase(raw.charAt(0)) + raw.substring(1, 3); // "Jan", "Feb", ...

            int count = countByMonth.get(m);
            int percent = (max == 0) ? 0 : (int) Math.round(count * 100.0 / max);

            result.add(new BookingRatePoint(label, percent));
        }
        return result;
    }

    // 5. BIỂU ĐỒ ĐƯỜNG: DOANH THU THEO THÁNG (HÓA ĐƠN)

    public static class RevenuePoint {
        public final int index;     // số tháng (1..12)
        public final double value;  // tổng doanh thu tháng đó

        public RevenuePoint(int index, double value) {
            this.index = index;
            this.value = value;
        }
    }

    /** Tạo dữ liệu doanh thu theo tháng trong NĂM HIỆN TẠI, dựa trên Hóa Đơn */
    public List<RevenuePoint> getRevenuePoints() {
        List<HoaDon> all = hoaDonDAO.getAll();
        LocalDate homNay = LocalDate.now();
        int year = homNay.getYear();

        double[] totalByMonth = new double[12]; // 0..11

        for (HoaDon hd : all) {
            LocalDateTime nd = hd.getNgayDat();
            if (nd == null) continue;

            LocalDate d = nd.toLocalDate();
            if (d.getYear() != year) continue;

            int monthIndex = d.getMonthValue() - 1;
            if (monthIndex >= 0 && monthIndex < 12) {
                totalByMonth[monthIndex] += hd.getTongTien();
            }
        }

        List<RevenuePoint> result = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            result.add(new RevenuePoint(i + 1, totalByMonth[i]));
        }
        return result;
    }


    /** Tổng số khách hàng trong hệ thống */
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
