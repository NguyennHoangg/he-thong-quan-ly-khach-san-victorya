package payment.controller;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import payment.model.Payment;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Controller để hiển thị UI thanh toán MoMo
 * Hiển thị QR code và theo dõi trạng thái thanh toán
 */
public class MoMoPaymentController {
    
    private Payment currentPayment;
    private Timer statusCheckTimer;
    private Label statusLabel;
    private ProgressIndicator progressIndicator;
    
    
    /**
     * Tạo request thanh toán và hiển thị QR code
     */
    private void createPaymentRequest(long amount, String orderInfo, ImageView qrImageView,
                                       Runnable onSuccess, Runnable onFailure) {
        new Thread(() -> {
            try {
                // Gọi API MoMo
                currentPayment = MoMoPaymentService.createPayment(amount, orderInfo);
                
                if (currentPayment.getResultCode() == 0) {
                    // Thành công - hiển thị QR code
                    Platform.runLater(() -> {
                        statusLabel.setText("Vui lòng quét mã QR để thanh toán");
                        progressIndicator.setVisible(false);
                        
                        // Tạo QR code từ payUrl
                        try {
                            // Tạo QR code image từ payUrl
                            Image qrImage = QRCodeGenerator.generateQRCodeImage(
                                currentPayment.getPayUrl(), 
                                300, 
                                300
                            );
                            qrImageView.setImage(qrImage);
                            
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    
                    // Bắt đầu kiểm tra trạng thái thanh toán định kỳ
                    startStatusCheck(onSuccess, onFailure);
                    
                } else {
                    // Lỗi
                    Platform.runLater(() -> {
                        progressIndicator.setVisible(false);
                        
                        showError("Không thể tạo thanh toán", currentPayment.getMessage());
                    });
                }
                
            } catch (Exception e) {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    
                    showError("Lỗi kết nối", e.getMessage());
                });
                e.printStackTrace();
            }
        }).start();
    }
    
    /**
     * Bắt đầu kiểm tra trạng thái thanh toán định kỳ (mỗi 3 giây)
     */
    private void startStatusCheck(Runnable onSuccess, Runnable onFailure) {
        statusCheckTimer = new Timer();
        statusCheckTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkPaymentStatus(onSuccess, onFailure);
            }
        }, 3000, 3000); // Check mỗi 3 giây
    }
    
    /**
     * Kiểm tra trạng thái thanh toán
     */
    private void checkPaymentStatus(Runnable onSuccess, Runnable onFailure) {
        if (currentPayment == null) return;
        
        new Thread(() -> {
            try {
                Payment status = MoMoPaymentService.queryPaymentStatus(
                    currentPayment.getOrderId(),
                    currentPayment.getRequestId()
                );
                
                Platform.runLater(() -> {
                    if (status.getResultCode() == 0) {
                        // Thanh toán thành công
                        statusLabel.setText("✓ Thanh toán thành công!");
                        statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                        progressIndicator.setVisible(false);
                        
                        stopStatusCheck();
                        
                        // Đợi 2 giây rồi đóng
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Platform.runLater(() -> {
                                    if (onSuccess != null) onSuccess.run();
                                });
                            }
                        }, 2000);
                        
                    } else if (status.getResultCode() == 1006) {
                        
                        stopStatusCheck();
                        showError("Thanh toán thất bại", status.getMessage());
                        
                        if (onFailure != null) onFailure.run();
                    }
                    // Các mã khác (đang chờ) thì không làm gì, tiếp tục check
                });
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    /**
     * Dừng timer kiểm tra trạng thái
     */
    private void stopStatusCheck() {
        if (statusCheckTimer != null) {
            statusCheckTimer.cancel();
            statusCheckTimer = null;
        }
    }
    
    
    /**
     * Hiển thị dialog lỗi
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
