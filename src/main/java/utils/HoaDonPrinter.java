package utils;

import model.ChiTietHoaDon;
import model.HoaDon;
import model.ChiTietHoaDonDichVu;

import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;

public class HoaDonPrinter {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,###");
    
    /**
     * Hiển thị hóa đơn trên màn hình
     */
    public static void printHoaDon(HoaDon hoaDon) {
        if (hoaDon == null) {
            System.err.println("Không thể hiển thị hóa đơn: dữ liệu null");
            return;
        }
        
        javafx.application.Platform.runLater(() -> {
            try {
                // Tạo WebView để render HTML
                javafx.scene.web.WebView webView = new javafx.scene.web.WebView();
                String htmlContent = generateHoaDonHTML(hoaDon);
                webView.getEngine().loadContent(htmlContent);
                
                // Tạo Scene và Stage để hiển thị
                javafx.scene.Scene scene = new javafx.scene.Scene(webView, 800, 1000);
                javafx.stage.Stage stage = new javafx.stage.Stage();
                stage.setTitle("Hóa đơn thanh toán - " + hoaDon.getMaHoaDon());
                stage.setScene(scene);
                stage.show();
            
                
            } catch (Exception e) {
                e.printStackTrace();
                showError("Lỗi khi hiển thị hóa đơn: " + e.getMessage());
            }
        });
    }
    
    /**
     * Hiển thị thông báo lỗi
     */
    private static void showError(String message) {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    /**
     * Tạo HTML cho hóa đơn
     */
    private static String generateHoaDonHTML(HoaDon hoaDon) {
        StringBuilder html = new StringBuilder();
        
        // Thông tin khách hàng
        String tenKhachHang = hoaDon.getKhachHang() != null ? hoaDon.getKhachHang().getTenKhachHang() : "Khách lẻ";
        String dienThoai = hoaDon.getKhachHang() != null ? hoaDon.getKhachHang().getSoDienThoai() : "";
        String diaChi = hoaDon.getKhachHang() != null ? hoaDon.getKhachHang().getEmail() : "";
        
        // Thông tin nhân viên
        String tenNhanVien = hoaDon.getNhanVien() != null ? hoaDon.getNhanVien().getTenNhanVien() : "";
        String sdtNhanVien = hoaDon.getNhanVien() != null ? hoaDon.getNhanVien().getSoDienThoai() : "";
        
        // Ngày tạo
        String ngayTao = hoaDon.getNgayTao() != null ? hoaDon.getNgayTao().format(DATE_FORMATTER) : "";
        
        html.append("<!DOCTYPE html>");
        html.append("<html><head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<style>");
        html.append("* { margin: 0; padding: 0; box-sizing: border-box; }");
        html.append("body { font-family: 'Segoe UI', Arial, sans-serif; padding: 40px; background: white; }");
        html.append(".container { max-width: 800px; margin: 0 auto; }");
        html.append(".header { display: flex; justify-content: space-between; align-items: start; margin-bottom: 40px; }");
        html.append(".logo-section { display: flex; align-items: center; gap: 15px; }");
        html.append(".logo { width: 60px; height: 60px; background: #3B82F6; border-radius: 8px; }");
        html.append(".company-info h1 { font-size: 24px; color: #1e293b; margin-bottom: 5px; }");
        html.append(".company-info p { font-size: 13px; color: #64748b; }");
        html.append(".invoice-info { text-align: right; }");
        html.append(".invoice-info h2 { font-size: 20px; color: #1e293b; margin-bottom: 5px; }");
        html.append(".invoice-info p { font-size: 13px; color: #64748b; }");
        html.append(".title { font-size: 36px; font-weight: bold; color: #1e293b; margin-bottom: 40px; text-align: center; }");
        html.append(".customer-info { background: #f8fafc; padding: 20px; border-radius: 8px; margin-bottom: 30px; }");
        html.append(".customer-info h3 { font-size: 18px; color: #1e293b; margin-bottom: 15px; }");
        html.append(".info-row { display: flex; margin-bottom: 8px; }");
        html.append(".info-label { width: 180px; color: #64748b; font-size: 14px; }");
        html.append(".info-value { color: #1e293b; font-size: 14px; font-weight: 500; }");
        html.append("table { width: 100%; border-collapse: collapse; margin-bottom: 30px; }");
        html.append("thead { background: #f1f5f9; }");
        html.append("th { padding: 15px; text-align: left; font-size: 13px; color: #475569; font-weight: 600; border-bottom: 2px solid #e2e8f0; }");
        html.append("th:last-child, td:last-child { text-align: right; }");
        html.append("td { padding: 15px; border-bottom: 1px solid #e2e8f0; font-size: 14px; color: #1e293b; }");
        html.append(".item-name { font-weight: 500; }");
        html.append(".item-desc { font-size: 12px; color: #64748b; margin-top: 4px; }");
        html.append(".summary { margin-left: auto; width: 400px; }");
        html.append(".summary-row { display: flex; justify-content: space-between; padding: 12px 0; font-size: 14px; }");
        html.append(".summary-row.total { border-top: 2px solid #e2e8f0; margin-top: 10px; padding-top: 15px; font-size: 18px; font-weight: bold; color: #1e293b; }");
        html.append(".footer { margin-top: 40px; padding: 20px; background: #f8fafc; border-radius: 8px; }");
        html.append(".footer h4 { font-size: 16px; color: #1e293b; margin-bottom: 15px; }");
        html.append(".footer-row { display: flex; margin-bottom: 8px; }");
        html.append(".footer-label { width: 180px; color: #64748b; font-size: 13px; }");
        html.append(".footer-value { color: #1e293b; font-size: 13px; }");
        html.append("@media print { body { padding: 20px; } }");
        html.append("</style>");
        html.append("</head><body>");
        html.append("<div class='container'>");
        
        // Header
        html.append("<div class='header'>");
        html.append("<div class='logo-section'>");
        html.append("<div class='logo'></div>");
        html.append("<div class='company-info'>");
        html.append("<h1>KHÁCH SẠN VICTORYA</h1>");
        html.append("<p>victorya.hotel@gmail.com</p>");
        html.append("</div></div>");
        html.append("<div class='invoice-info'>");
        html.append("<h2>HÓA ĐƠN #").append(hoaDon.getMaHoaDon()).append("</h2>");
        html.append("<p>Ngày ").append(ngayTao).append("</p>");
        html.append("</div></div>");
        
        // Title
        html.append("<div class='title'>HÓA ĐƠN</div>");
        
        // Customer Info
        html.append("<div class='customer-info'>");
        html.append("<h3>").append(tenKhachHang.toUpperCase()).append("</h3>");
        html.append("<div class='info-row'><div class='info-label'>Điện thoại Khách hàng</div><div class='info-value'>").append(dienThoai).append("</div></div>");
        html.append("<div class='info-row'><div class='info-label'>Địa chỉ Khách hàng</div><div class='info-value'>").append(diaChi).append("</div></div>");
        html.append("</div>");
        
        // Table
        html.append("<table>");
        html.append("<thead><tr>");
        html.append("<th>MỤC</th>");
        html.append("<th>SỐ LƯỢNG/GIỜ</th>");
        html.append("<th>ĐƠN GIÁ</th>");
        html.append("<th>THÀNH TIỀN</th>");
        html.append("</tr></thead>");
        html.append("<tbody>");
        
        // Chi tiết phòng và dịch vụ
        if (hoaDon.getChiTietHoaDon() != null) {
            for (ChiTietHoaDon chiTiet : hoaDon.getChiTietHoaDon()) {
                // Thông tin phòng
                String tenPhong = chiTiet.getPhong() != null ? 
                    "Phòng " + chiTiet.getPhong().getSoPhong() : "Phòng";
                String loaiPhong = chiTiet.getPhong() != null && chiTiet.getPhong().getLoaiPhong() != null ?
                    chiTiet.getPhong().getLoaiPhong().getTenLoaiPhong() : "";
                
                long donGiaPhong = chiTiet.getPhong() != null && chiTiet.getPhong().getLoaiPhong() != null ?
                    Math.round(chiTiet.getPhong().getLoaiPhong().getGia()) : 0;
                long thanhTienPhong = Math.round(chiTiet.getTongTien());
                
                // Lấy số giờ lưu trú từ PhieuDatPhong
                int soGio = 0;
                if (chiTiet.getPhieuDatPhong() != null && 
                    chiTiet.getPhieuDatPhong().getDsachPhieuDatPhong() != null) {
                    // Tìm chi tiết phiếu đặt phòng tương ứng với phòng này
                    for (model.ChiTietPhieuDatPhong ctpdp : chiTiet.getPhieuDatPhong().getDsachPhieuDatPhong()) {
                        if (ctpdp.getPhong() != null && chiTiet.getPhong() != null &&
                            ctpdp.getPhong().getMaPhong().equals(chiTiet.getPhong().getMaPhong())) {
                            soGio = ctpdp.getSoGioLuuTru();
                            break;
                        }
                    }
                }
                String soLuong = soGio > 0 ? soGio + " giờ" : "1";
                
                html.append("<tr>");
                html.append("<td><div class='item-name'>").append(tenPhong).append("</div>");
                html.append("<div class='item-desc'>").append(loaiPhong).append("</div></td>");
                html.append("<td>").append(soLuong).append("</td>");
                html.append("<td>").append(CURRENCY_FORMAT.format(donGiaPhong)).append("đ</td>");
                html.append("<td>").append(CURRENCY_FORMAT.format(thanhTienPhong)).append("đ</td>");
                html.append("</tr>");
                
                // Dịch vụ (nếu có)
                if (chiTiet.getDichVus() != null && !chiTiet.getDichVus().isEmpty()) {
                    for (ChiTietHoaDonDichVu dichVu : chiTiet.getDichVus()) {
                        String tenDV = dichVu.getDichVu() != null ? dichVu.getDichVu().getTenDichVu() : "Dịch vụ";
                        long giaDV = dichVu.getDichVu() != null ? Math.round(dichVu.getDichVu().getGia()) : 0;
                        
                        html.append("<tr>");
                        html.append("<td><div class='item-name'>").append(tenDV).append("</div></td>");
                        html.append("<td>1</td>");
                        html.append("<td>").append(CURRENCY_FORMAT.format(giaDV)).append("đ</td>");
                        html.append("<td>").append(CURRENCY_FORMAT.format(giaDV)).append("đ</td>");
                        html.append("</tr>");
                    }
                }
            }
        }
        
        html.append("</tbody></table>");
        
        // Summary
        long tongCong = Math.round(hoaDon.getTongTien());
        long thue = (long) (tongCong*0.1);
        long tongThanhToan = tongCong + thue;
        
        html.append("<div class='summary'>");
        html.append("<div class='summary-row'><span>Tổng cộng</span><span>").append(CURRENCY_FORMAT.format(tongCong)).append("đ</span></div>");
        html.append("<div class='summary-row'><span>Thuế (10%)</span><span>").append(CURRENCY_FORMAT.format(thue)).append("đ</span></div>");
        html.append("<div class='summary-row total'><span>TỔNG THANH TOÁN</span><span>").append(CURRENCY_FORMAT.format(tongThanhToan)).append("đ</span></div>");
        html.append("</div>");
        
        // Footer - Payment Info
        html.append("<div class='footer'>");
        html.append("<h4>THÔNG TIN THANH TOÁN</h4>");
        html.append("<div class='footer-row'><div class='footer-label'>Nhân viên</div><div class='footer-value'>").append(tenNhanVien).append("</div></div>");
        html.append("<div class='footer-row'><div class='footer-label'>Số điện thoại</div><div class='footer-value'>").append(sdtNhanVien).append("</div></div>");
        html.append("<div class='footer-row'><div class='footer-label'>Ngày thanh toán</div><div class='footer-value'>").append(ngayTao).append("</div></div>");
        html.append("<div class='footer-row'><div class='footer-label'>Địa chỉ</div><div class='footer-value'>123 Đường ABC, TP Hồ Chí Minh</div></div>");
        html.append("</div>");
        
        html.append("</div></body></html>");
        
        return html.toString();
    }
}
