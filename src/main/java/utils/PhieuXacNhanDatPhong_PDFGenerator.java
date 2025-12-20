package utils;

import model.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Generator để tạo file PDF phiếu xác nhận đặt phòng
 */
public class PhieuXacNhanDatPhong_PDFGenerator {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final float MARGIN = 50;
    private static final float FONT_SIZE_TITLE = 24;
    private static final float FONT_SIZE_HEADER = 18;
    private static final float FONT_SIZE_SECTION = 14;
    private static final float FONT_SIZE_NORMAL = 11;
    private static final float FONT_SIZE_SMALL = 9;
    private static final float LINE_HEIGHT = 15;

    /**
     * Load font hỗ trợ tiếng Việt
     */
    private static PDFont loadVietnameseFont(PDDocument document) throws IOException {
        // Thử load Arial từ Windows fonts
        String[] fontPaths = {
            "C:/Windows/Fonts/arial.ttf",
            "C:/Windows/Fonts/Arial.ttf",
            "C:/Windows/Fonts/times.ttf",
            "C:/Windows/Fonts/Times.ttf",
            "/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf",
            "/System/Library/Fonts/Supplemental/Arial.ttf"
        };
        
        for (String fontPath : fontPaths) {
            File fontFile = new File(fontPath);
            if (fontFile.exists()) {
                return PDType0Font.load(document, fontFile);
            }
        }
        
        throw new IOException("Không tìm thấy font hỗ trợ tiếng Việt. Vui lòng cài đặt font Arial hoặc Times New Roman.");
    }

    /**
     * Generate PDF và tự động mở file
     * 
     * @param phieuDatPhong Phiếu đặt phòng
     * @return File PDF đã tạo
     */
    public static File generateAndOpenPDF(PhieuDatPhong phieuDatPhong) {
        try {
            File pdfFile = generatePDF(phieuDatPhong);
            
            // Tự động mở file PDF
            if (pdfFile != null && pdfFile.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                }
            }
            
            return pdfFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generate PDF file
     * 
     * @param phieuDatPhong Phiếu đặt phòng
     * @return File PDF đã tạo
     */
    public static File generatePDF(PhieuDatPhong phieuDatPhong) throws IOException {
        PDDocument document = new PDDocument();
        
        try {
            // Load font hỗ trợ tiếng Việt
            PDFont font = loadVietnameseFont(document);
            PDFont fontBold = font; // Sử dụng cùng font (PDType0Font không có variant bold)
            
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            float yPosition = page.getMediaBox().getHeight() - MARGIN;
            float pageWidth = page.getMediaBox().getWidth();

            // Header - Khách sạn Victorya
            contentStream.setNonStrokingColor(103/255f, 126/255f, 234/255f); // Purple color
            contentStream.addRect(0, yPosition - 80, pageWidth, 80);
            contentStream.fill();
            
            contentStream.beginText();
            contentStream.setNonStrokingColor(1, 1, 1); // White
            contentStream.setFont(fontBold, FONT_SIZE_TITLE);
            String hotelName = "KHÁCH SẠN VICTORYA";
            float titleWidth = fontBold.getStringWidth(hotelName) / 1000 * FONT_SIZE_TITLE;
            contentStream.newLineAtOffset((pageWidth - titleWidth) / 2, yPosition - 40);
            contentStream.showText(hotelName);
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(font, FONT_SIZE_SMALL);
            String subtitle = "Hệ thống quản lý khách sạn";
            float subtitleWidth = font.getStringWidth(subtitle) / 1000 * FONT_SIZE_SMALL;
            contentStream.newLineAtOffset((pageWidth - subtitleWidth) / 2, yPosition - 60);
            contentStream.showText(subtitle);
            contentStream.endText();

            yPosition -= 100;

            // Title Section
            contentStream.setNonStrokingColor(0, 0, 0);
            contentStream.beginText();
            contentStream.setFont(fontBold, FONT_SIZE_HEADER);
            String title = "PHIẾU XÁC NHẬN ĐẶT PHÒNG";
            float titleWidth2 = fontBold.getStringWidth(title) / 1000 * FONT_SIZE_HEADER;
            contentStream.newLineAtOffset((pageWidth - titleWidth2) / 2, yPosition);
            contentStream.showText(title);
            contentStream.endText();

            yPosition -= 30;

            // Success message
            contentStream.beginText();
            contentStream.setNonStrokingColor(22/255f, 160/255f, 133/255f); // Green
            contentStream.setFont(font, FONT_SIZE_SMALL);
            String message = "Phiếu này xác nhận các dịch vụ đặt phòng dưới đây đã được đặt thành công.";
            float msgWidth = font.getStringWidth(message) / 1000 * FONT_SIZE_SMALL;
            contentStream.newLineAtOffset((pageWidth - msgWidth) / 2, yPosition);
            contentStream.showText(message);
            contentStream.endText();

            yPosition -= 30;
            contentStream.setNonStrokingColor(0, 0, 0);

            // Booking Information Section
            yPosition = drawSectionTitle(contentStream, fontBold, "DỊCH VỤ ĐẶT PHÒNG", yPosition, pageWidth);
            yPosition -= 10;

            KhachHang kh = phieuDatPhong.getKhachHang();
            yPosition = drawInfoRow(contentStream, fontBold, font, "Mã dịch vụ:", phieuDatPhong.getMaPhieuDatPhong(), MARGIN, yPosition);
            yPosition = drawInfoRow(contentStream, fontBold, font, "Tên khách:", kh.getTenKhachHang(), MARGIN, yPosition);
            yPosition = drawInfoRow(contentStream, fontBold, font, "Đặt phòng tại:", "Khách sạn Victorya", MARGIN, yPosition);
            yPosition = drawInfoRow(contentStream, fontBold, font, "Địa chỉ:", "12 Nguyễn Văn Bảo, P.4, Q.Gò Vấp, TP.HCM", MARGIN, yPosition);
            yPosition = drawInfoRow(contentStream, fontBold, font, "Điện thoại:", "028 3894 2929", MARGIN, yPosition);

            // Thông tin đặt phòng
            List<ChiTietPhieuDatPhong> danhSachPhong = phieuDatPhong.getDsachPhieuDatPhong();
            if (!danhSachPhong.isEmpty()) {
                ChiTietPhieuDatPhong ctFirst = danhSachPhong.get(0);
                
                if (ctFirst.getThoiGianNhanPhong() != null) {
                    yPosition = drawInfoRow(contentStream, fontBold, font, "Ngày nhận phòng:", 
                        ctFirst.getThoiGianNhanPhong().format(DATE_TIME_FORMATTER), MARGIN, yPosition);
                }
                
                if (ctFirst.getThoiGianTraPhong() != null) {
                    yPosition = drawInfoRow(contentStream, fontBold, font, "Ngày trả phòng:", 
                        ctFirst.getThoiGianTraPhong().format(DATE_TIME_FORMATTER), MARGIN, yPosition);
                }
                
                yPosition = drawInfoRow(contentStream, fontBold, font, "Số đêm:", 
                    String.valueOf(ctFirst.getSoGioLuuTru() / 24), MARGIN, yPosition);
            }
            
            yPosition = drawInfoRow(contentStream, fontBold, font, "Số phòng:", 
                String.valueOf(danhSachPhong.size()), MARGIN, yPosition);

            yPosition -= 20;

            // Room Details Section
            yPosition = drawSectionTitle(contentStream, fontBold, "LOẠI PHÒNG", yPosition, pageWidth);
            yPosition -= 10;

            for (int i = 0; i < danhSachPhong.size(); i++) {
                ChiTietPhieuDatPhong ct = danhSachPhong.get(i);
                Phong phong = ct.getPhong();
                
                String roomInfo = String.format("%d. Phòng %s - %s | Giá: %,d VNĐ/đêm", 
                    i + 1, 
                    phong.getSoPhong(),
                    phong.getLoaiPhong().getTenLoaiPhong(),
                    (long)phong.getLoaiPhong().getGia());
                
                if (ct.getSoNguoi() > 0) {
                    roomInfo += " | Số người: " + ct.getSoNguoi();
                }
                
                contentStream.beginText();
                contentStream.setFont(font, FONT_SIZE_NORMAL);
                contentStream.newLineAtOffset(MARGIN, yPosition);
                contentStream.showText(roomInfo);
                contentStream.endText();
                yPosition -= LINE_HEIGHT;
            }

            yPosition -= 15;

            // Dịch vụ cộng thêm
            yPosition = drawSectionTitle(contentStream, fontBold, "DỊCH VỤ CỘNG THÊM", yPosition, pageWidth);
            yPosition -= 10;

            boolean hasDichVu = false;
            for (ChiTietPhieuDatPhong ct : danhSachPhong) {
                List<DichVu> dsDV = ct.getDsachDichVu();
                if (dsDV != null && !dsDV.isEmpty()) {
                    hasDichVu = true;
                    for (DichVu dv : dsDV) {
                        String dvInfo = String.format("- %s (%,d VNĐ)", 
                            dv.getTenDichVu(), (long)dv.getGia());
                        
                        contentStream.beginText();
                        contentStream.setFont(font, FONT_SIZE_NORMAL);
                        contentStream.newLineAtOffset(MARGIN, yPosition);
                        contentStream.showText(dvInfo);
                        contentStream.endText();
                        yPosition -= LINE_HEIGHT;
                    }
                }
            }
            
            if (!hasDichVu) {
                contentStream.beginText();
                contentStream.setFont(font, FONT_SIZE_NORMAL);
                contentStream.newLineAtOffset(MARGIN, yPosition);
                contentStream.showText("Không có dịch vụ cộng thêm");
                contentStream.endText();
                yPosition -= LINE_HEIGHT;
            }

            yPosition -= 15;

            // Thông tin thêm
            yPosition = drawSectionTitle(contentStream, fontBold, "THÔNG TIN THÊM", yPosition, pageWidth);
            yPosition -= 10;

            String[] infoLines = {
                "Không hoàn, hủy, đổi khuyến mãi đã đặt",
                "Giá phòng bao gồm:",
                "  - Thuế VAT",
                "  - Ăn Sáng",
                "  - Phí Dịch Vụ",
                "Thông tin liên hệ:",
                "  Email: contact@victorya-hotel.com",
                "  Hotline: 1900 xxxx"
            };

            for (String line : infoLines) {
                contentStream.beginText();
                contentStream.setFont(font, FONT_SIZE_NORMAL);
                contentStream.newLineAtOffset(MARGIN, yPosition);
                contentStream.showText(line);
                contentStream.endText();
                yPosition -= LINE_HEIGHT;
            }

            yPosition -= 20;

            // Total Amount Section
            long tongTien = phieuDatPhong.tinhTongTien();
            long tienCoc = phieuDatPhong.getTienDatCoc();

            // Draw background for total section
            contentStream.setNonStrokingColor(248/255f, 249/255f, 250/255f);
            contentStream.addRect(MARGIN - 10, yPosition - 40, pageWidth - 2*MARGIN + 20, 50);
            contentStream.fill();

            contentStream.setNonStrokingColor(0, 0, 0);
            contentStream.beginText();
            contentStream.setFont(fontBold, FONT_SIZE_SECTION);
            contentStream.newLineAtOffset(MARGIN, yPosition);
            contentStream.showText("Tổng cộng:");
            contentStream.endText();

            contentStream.beginText();
            contentStream.setNonStrokingColor(231/255f, 76/255f, 60/255f); // Red
            contentStream.setFont(fontBold, FONT_SIZE_HEADER);
            String tongTienStr = String.format("%,d VNĐ", tongTien);
            float tongWidth = fontBold.getStringWidth(tongTienStr) / 1000 * FONT_SIZE_HEADER;
            contentStream.newLineAtOffset(pageWidth - MARGIN - tongWidth, yPosition);
            contentStream.showText(tongTienStr);
            contentStream.endText();

            yPosition -= 25;

            contentStream.setNonStrokingColor(133/255f, 100/255f, 4/255f);
            contentStream.beginText();
            contentStream.setFont(fontBold, FONT_SIZE_NORMAL);
            contentStream.newLineAtOffset(MARGIN, yPosition);
            contentStream.showText("Tiền đặt cọc (30%):");
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(fontBold, FONT_SIZE_SECTION);
            String cocStr = String.format("%,d VNĐ", tienCoc);
            float cocWidth = fontBold.getStringWidth(cocStr) / 1000 * FONT_SIZE_SECTION;
            contentStream.newLineAtOffset(pageWidth - MARGIN - cocWidth, yPosition);
            contentStream.showText(cocStr);
            contentStream.endText();

            // Footer (không có nền màu)
            yPosition = MARGIN + 40;

            contentStream.setNonStrokingColor(0, 0, 0); // Black text
            contentStream.beginText();
            contentStream.setFont(font, FONT_SIZE_NORMAL);
            String footerText = "Cảm ơn quý khách đã sử dụng dịch vụ của Khách sạn Victorya";
            float footerWidth = font.getStringWidth(footerText) / 1000 * FONT_SIZE_NORMAL;
            contentStream.newLineAtOffset((pageWidth - footerWidth) / 2, yPosition);
            contentStream.showText(footerText);
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(font, FONT_SIZE_SMALL);
            String dateText = "Ngày in: " + java.time.LocalDateTime.now().format(DATE_TIME_FORMATTER);
            float dateWidth = font.getStringWidth(dateText) / 1000 * FONT_SIZE_SMALL;
            contentStream.newLineAtOffset((pageWidth - dateWidth) / 2, yPosition - 15);
            contentStream.showText(dateText);
            contentStream.endText();

            contentStream.close();

            // Tạo thư mục lưu file nếu chưa tồn tại
            File outputDir = new File("phieu_xac_nhan");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            // Lưu file
            String fileName = String.format("PhieuDatPhong_%s_%s.pdf", 
                phieuDatPhong.getMaPhieuDatPhong(),
                java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
            
            File pdfFile = new File(outputDir, fileName);
            document.save(pdfFile);
            
            return pdfFile;

        } finally {
            document.close();
        }
    }

    /**
     * Draw section title
     */
    private static float drawSectionTitle(PDPageContentStream contentStream, PDFont font, String title, 
                                         float yPosition, float pageWidth) throws IOException {
        // Draw line
        contentStream.setStrokingColor(103/255f, 126/255f, 234/255f);
        contentStream.setLineWidth(2);
        contentStream.moveTo(MARGIN, yPosition - 5);
        contentStream.lineTo(pageWidth - MARGIN, yPosition - 5);
        contentStream.stroke();

        contentStream.beginText();
        contentStream.setNonStrokingColor(52/255f, 73/255f, 94/255f);
        contentStream.setFont(font, FONT_SIZE_SECTION);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText(title);
        contentStream.endText();

        return yPosition - 25;
    }

    /**
     * Draw info row
     */
    private static float drawInfoRow(PDPageContentStream contentStream, PDFont fontBold, PDFont fontNormal,
                                    String label, String value, float xPosition, float yPosition) throws IOException {
        contentStream.beginText();
        contentStream.setFont(fontBold, FONT_SIZE_NORMAL);
        contentStream.newLineAtOffset(xPosition, yPosition);
        contentStream.showText(label != null ? label : "");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(fontNormal, FONT_SIZE_NORMAL);
        contentStream.newLineAtOffset(xPosition + 150, yPosition);
        contentStream.showText(value != null ? value : "");
        contentStream.endText();

        return yPosition - LINE_HEIGHT;
    }
}
