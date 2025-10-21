package payment.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class để tạo QR code từ URL hoặc text
 */
public class QRCodeGenerator {
    
    /**
     * Tạo QR code dưới dạng JavaFX Image
     * @param text Nội dung QR code (URL hoặc text)
     * @param width Chiều rộng ảnh
     * @param height Chiều cao ảnh
     * @return JavaFX Image của QR code
     * @throws WriterException nếu không thể tạo QR code
     */
    public static Image generateQRCodeImage(String text, int width, int height) throws WriterException {
        // Cấu hình QR code
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);
        
        // Tạo QR code matrix
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        
        // Chuyển BitMatrix thành JavaFX Image
        WritableImage image = new WritableImage(width, height);
        PixelWriter pixelWriter = image.getPixelWriter();
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Đen nếu bit = 1, trắng nếu bit = 0
                Color color = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
                pixelWriter.setColor(x, y, color);
            }
        }
        
        return image;
    }
}
