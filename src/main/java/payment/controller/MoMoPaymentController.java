package payment.controller;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
    private Stage paymentStage;
    private Label statusLabel;
    private ProgressIndicator progressIndicator;
    
    /**
     * Hiển thị dialog thanh toán MoMo
     * @param amount Số tiền thanh toán
     * @param orderInfo Thông tin đơn hàng
     * @param onSuccess Callback khi thanh toán thành công
     * @param onFailure Callback khi thanh toán thất bại
     */
    public void showPaymentDialog(long amount, String orderInfo, 
                                   Runnable onSuccess, Runnable onFailure) {
        // Tạo stage mới cho payment
        paymentStage = new Stage();
        paymentStage.initModality(Modality.APPLICATION_MODAL);
        paymentStage.setTitle("Thanh toán MoMo");
        paymentStage.setResizable(false);
        
        // Main layout
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: white;");
        
        // Header
        Label headerLabel = new Label("THANH TOÁN QUA MOMO");
        headerLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        headerLabel.setStyle("-fx-text-fill: #A50064;");
        
        // Amount info
        Label amountLabel = new Label(String.format("Số tiền: %,d VNĐ", amount));
        amountLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        
        Label orderInfoLabel = new Label(orderInfo);
        orderInfoLabel.setFont(Font.font("System", 14));
        orderInfoLabel.setStyle("-fx-text-fill: #666;");
        
        // QR Code container
        VBox qrContainer = new VBox(10);
        qrContainer.setAlignment(Pos.CENTER);
        qrContainer.setPadding(new Insets(20));
        qrContainer.setStyle("-fx-border-color: #ddd; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");
        
        ImageView qrImageView = new ImageView();
        qrImageView.setFitWidth(300);
        qrImageView.setFitHeight(300);
        
        Label scanLabel = new Label("Quét mã QR bằng ứng dụng MoMo");
        scanLabel.setFont(Font.font("System", 14));
        
        qrContainer.getChildren().addAll(qrImageView, scanLabel);
        
        // Status area
        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER);
        
        progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(30, 30);
        
        statusLabel = new Label("Đang tạo mã thanh toán...");
        statusLabel.setFont(Font.font("System", 14));
        
        statusBox.getChildren().addAll(progressIndicator, statusLabel);
        
        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button cancelButton = new Button("Hủy");
        cancelButton.setPrefWidth(120);
        cancelButton.setStyle("-fx-background-color: #e0e0e0; -fx-font-size: 14px; -fx-padding: 10 20;");
        cancelButton.setOnAction(e -> {
            closePayment();
            if (onFailure != null) onFailure.run();
        });
        
        Button refreshButton = new Button("Kiểm tra thanh toán");
        refreshButton.setPrefWidth(160);
        refreshButton.setStyle("-fx-background-color: #A50064; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        refreshButton.setOnAction(e -> checkPaymentStatus(onSuccess, onFailure));
        
        buttonBox.getChildren().addAll(cancelButton, refreshButton);
        
        // Add all to main layout
        mainLayout.getChildren().addAll(
            headerLabel,
            amountLabel,
            orderInfoLabel,
            qrContainer,
            statusBox,
            buttonBox
        );
        
        Scene scene = new Scene(mainLayout, 500, 650);
        paymentStage.setScene(scene);
        paymentStage.show();
        
        // Tạo payment request
        createPaymentRequest(amount, orderInfo, qrImageView, onSuccess, onFailure);
    }
    
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
                            
                            System.out.println("✓ Tạo QR code thành công!");
                            System.out.println("PayUrl: " + currentPayment.getPayUrl());
                            
                        } catch (Exception e) {
                            statusLabel.setText("Không thể tạo QR code: " + e.getMessage());
                            statusLabel.setStyle("-fx-text-fill: red;");
                            e.printStackTrace();
                        }
                    });
                    
                    // Bắt đầu kiểm tra trạng thái thanh toán định kỳ
                    startStatusCheck(onSuccess, onFailure);
                    
                } else {
                    // Lỗi
                    Platform.runLater(() -> {
                        statusLabel.setText("Lỗi: " + currentPayment.getMessage());
                        statusLabel.setStyle("-fx-text-fill: red;");
                        progressIndicator.setVisible(false);
                        
                        showError("Không thể tạo thanh toán", currentPayment.getMessage());
                    });
                }
                
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Lỗi kết nối đến MoMo");
                    statusLabel.setStyle("-fx-text-fill: red;");
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
                                    closePayment();
                                    if (onSuccess != null) onSuccess.run();
                                });
                            }
                        }, 2000);
                        
                    } else if (status.getResultCode() == 1006) {
                        // Giao dịch bị từ chối
                        statusLabel.setText("✗ Thanh toán thất bại");
                        statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                        
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
     * Đóng dialog thanh toán
     */
    private void closePayment() {
        stopStatusCheck();
        if (paymentStage != null) {
            paymentStage.close();
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
