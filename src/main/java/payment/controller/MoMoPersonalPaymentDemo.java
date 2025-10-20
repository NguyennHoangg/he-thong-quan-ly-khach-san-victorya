package payment.controller;

import com.google.zxing.WriterException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * ============================================================
 * DEMO THANH TOÁN MOMO - DÀNH CHO PROJECT (KHÔNG CẦN ĐĂNG KÝ)
 * ============================================================
 * 
 * Cách hoạt động:
 * 1. Tạo QR code chuyển tiền đến số MoMo của bạn
 * 2. Khách quét QR → Chuyển tiền
 * 3. Bạn nhận SMS từ MoMo → Kiểm tra nội dung
 * 4. Xác nhận thủ công trong hệ thống
 */
public class MoMoPersonalPaymentDemo extends Application {
    
    // ========================================================================
    // ⚠️⚠️⚠️ QUAN TRỌNG: THAY SỐ ĐIỆN THOẠI MOMO CỦA BẠN Ở ĐÂY ⚠️⚠️⚠️
    // ========================================================================
    private static final String PHONE_NUMBER = "0987654321"; // ← THAY SỐ ĐIỆN THOẠI MOMO
    private static final String ACCOUNT_NAME = "Khach San Victorya"; // Tên hiển thị
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Demo Thanh Toán MoMo - Khách Sạn Victorya");
        
        // ============ MAIN LAYOUT ============
        VBox mainLayout = new VBox(25);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #f8f9fa, #e9ecef);");
        
        // ============ HEADER ============
        VBox headerBox = new VBox(5);
        headerBox.setAlignment(Pos.CENTER);
        
        Label headerLabel = new Label("💰 THANH TOÁN QUA MOMO");
        headerLabel.setFont(Font.font("System", FontWeight.BOLD, 32));
        headerLabel.setTextFill(Color.web("#A50064"));
        
        Label subHeaderLabel = new Label("Chuyển tiền trực tiếp - Không cần API");
        subHeaderLabel.setFont(Font.font("System", 14));
        subHeaderLabel.setStyle("-fx-text-fill: #666;");
        
        headerBox.getChildren().addAll(headerLabel, subHeaderLabel);
        
        // ============ FORM NHẬP LIỆU ============
        VBox formBox = new VBox(15);
        formBox.setPadding(new Insets(25));
        formBox.setAlignment(Pos.CENTER);
        formBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        // Số tiền
        HBox amountBox = new HBox(10);
        amountBox.setAlignment(Pos.CENTER_LEFT);
        Label amountLabel = new Label("💵 Số tiền:");
        amountLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        amountLabel.setPrefWidth(120);
        
        TextField amountField = new TextField("100000");
        amountField.setPromptText("Nhập số tiền (VNĐ)");
        amountField.setPrefWidth(250);
        amountField.setStyle("-fx-font-size: 16px; -fx-padding: 10;");
        
        amountBox.getChildren().addAll(amountLabel, amountField);
        
        // Mã hóa đơn
        HBox orderBox = new HBox(10);
        orderBox.setAlignment(Pos.CENTER_LEFT);
        Label orderLabel = new Label("📋 Mã hóa đơn:");
        orderLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        orderLabel.setPrefWidth(120);
        
        TextField orderField = new TextField("HD" + System.currentTimeMillis());
        orderField.setPromptText("Mã hóa đơn");
        orderField.setPrefWidth(250);
        orderField.setStyle("-fx-font-size: 16px; -fx-padding: 10;");
        
        orderBox.getChildren().addAll(orderLabel, orderField);
        
        // Thông tin tài khoản
        Label accountInfoLabel = new Label("📱 Số điện thoại nhận: " + PHONE_NUMBER);
        accountInfoLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        accountInfoLabel.setStyle("-fx-text-fill: #A50064;");
        
        formBox.getChildren().addAll(amountBox, orderBox, new Separator(), accountInfoLabel);
        
        // ============ NÚT TẠO QR ============
        Button generateButton = new Button("🎫 TẠO MÃ QR THANH TOÁN");
        generateButton.setPrefWidth(300);
        generateButton.setPrefHeight(50);
        generateButton.setStyle(
            "-fx-background-color: #A50064; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand;"
        );
        generateButton.setOnMouseEntered(e -> 
            generateButton.setStyle(
                "-fx-background-color: #8a0054; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 18px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand;"
            )
        );
        generateButton.setOnMouseExited(e -> 
            generateButton.setStyle(
                "-fx-background-color: #A50064; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 18px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand;"
            )
        );
        
        // ============ VÙNG QR CODE ============
        VBox qrBox = new VBox(15);
        qrBox.setAlignment(Pos.CENTER);
        qrBox.setPadding(new Insets(25));
        qrBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-border-color: #A50064; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(165,0,100,0.2), 15, 0, 0, 3);"
        );
        qrBox.setVisible(false);
        
        ImageView qrImageView = new ImageView();
        qrImageView.setFitWidth(350);
        qrImageView.setFitHeight(350);
        
        Label qrTitleLabel = new Label("📱 QUÉT MÃ QR BẰNG APP MOMO");
        qrTitleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        qrTitleLabel.setTextFill(Color.web("#A50064"));
        
        Label qrNoteLabel = new Label("");
        qrNoteLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        qrNoteLabel.setStyle("-fx-text-fill: #28a745;");
        qrNoteLabel.setWrapText(true);
        qrNoteLabel.setMaxWidth(350);
        qrNoteLabel.setAlignment(Pos.CENTER);
        
        qrBox.getChildren().addAll(qrImageView, qrTitleLabel, qrNoteLabel);
        
        // ============ VÙNG TRẠNG THÁI ============
        VBox statusBox = new VBox(15);
        statusBox.setPadding(new Insets(20));
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setStyle(
            "-fx-background-color: #fff9e6; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: #ffc107; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10;"
        );
        
        Label statusTitleLabel = new Label("📋 HƯỚNG DẪN THANH TOÁN");
        statusTitleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        TextArea statusArea = new TextArea();
        statusArea.setPrefHeight(200);
        statusArea.setEditable(false);
        statusArea.setWrapText(true);
        statusArea.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-background-color: white; " +
            "-fx-border-radius: 5;"
        );
        statusArea.setText(
            "📱 CÁCH SỬ DỤNG:\n\n" +
            "1️⃣ Nhập số tiền và mã hóa đơn\n" +
            "2️⃣ Nhấn 'TẠO MÃ QR THANH TOÁN'\n" +
            "3️⃣ Khách quét QR code bằng app MoMo\n" +
            "4️⃣ Khách xác nhận chuyển tiền\n" +
            "5️⃣ Bạn nhận SMS/thông báo từ MoMo\n" +
            "6️⃣ Kiểm tra nội dung tin nhắn có đúng mã hóa đơn\n" +
            "7️⃣ Nhấn 'XÁC NHẬN ĐÃ NHẬN TIỀN' để hoàn tất\n\n" +
            "⚠️ LƯU Ý:\n" +
            "• Luôn kiểm tra số tiền và nội dung chuyển khoản\n" +
            "• Mã hóa đơn phải khớp với thông báo từ MoMo\n" +
            "• Đây là thanh toán trực tiếp, không qua API"
        );
        
        statusBox.getChildren().addAll(statusTitleLabel, statusArea);
        
        // ============ NÚT XÁC NHẬN ============
        Button confirmButton = new Button("✅ XÁC NHẬN ĐÃ NHẬN TIỀN");
        confirmButton.setPrefWidth(300);
        confirmButton.setPrefHeight(50);
        confirmButton.setDisable(true);
        confirmButton.setStyle(
            "-fx-background-color: #28a745; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 10; " +
            "-fx-opacity: 0.5;"
        );
        
        // ============ ACTION HANDLERS ============
        
        // Tạo QR Code
        generateButton.setOnAction(e -> {
            try {
                String amount = amountField.getText().trim();
                String orderId = orderField.getText().trim();
                
                // Validate
                if (amount.isEmpty() || Long.parseLong(amount) <= 0) {
                    showAlert(Alert.AlertType.WARNING, "⚠️ Lỗi", "Vui lòng nhập số tiền hợp lệ!");
                    return;
                }
                if (orderId.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "⚠️ Lỗi", "Vui lòng nhập mã hóa đơn!");
                    return;
                }
                
                long amountValue = Long.parseLong(amount);
                
                // Tạo QR code
                String qrContent = String.format("2|99|%s|%s||0|0|%s|%s", 
                    PHONE_NUMBER, ACCOUNT_NAME, amount, orderId);
                
                Image qrImage = QRCodeGenerator.generateQRCodeImage(qrContent, 350, 350);
                qrImageView.setImage(qrImage);
                
                qrNoteLabel.setText(
                    "💬 Nội dung chuyển khoản: " + orderId + "\n" +
                    "💰 Số tiền: " + String.format("%,d VNĐ", amountValue)
                );
                
                qrBox.setVisible(true);
                confirmButton.setDisable(false);
                confirmButton.setStyle(
                    "-fx-background-color: #28a745; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 18px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-radius: 10; " +
                    "-fx-cursor: hand; " +
                    "-fx-opacity: 1.0;"
                );
                
                statusArea.setText(
                    "✅ ĐÃ TẠO MÃ QR THÀNH CÔNG!\n\n" +
                    "📋 THÔNG TIN GIAO DỊCH:\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                    "💵 Số tiền: " + String.format("%,d VNĐ", amountValue) + "\n" +
                    "📝 Mã hóa đơn: " + orderId + "\n" +
                    "📱 Số điện thoại: " + PHONE_NUMBER + "\n" +
                    "👤 Tên: " + ACCOUNT_NAME + "\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                    "⏳ ĐANG CHỜ KHÁCH THANH TOÁN...\n\n" +
                    "👉 SAU KHI NHẬN THÔNG BÁO TỪ MOMO:\n" +
                    "   1. Kiểm tra số tiền: " + String.format("%,d VNĐ", amountValue) + "\n" +
                    "   2. Kiểm tra nội dung: " + orderId + "\n" +
                    "   3. Nhấn 'XÁC NHẬN ĐÃ NHẬN TIỀN'\n\n" +
                    "💡 Mẹo: Mở app MoMo để xem thông báo real-time!"
                );
                
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "❌ Lỗi", "Số tiền không hợp lệ!");
            } catch (WriterException ex) {
                showAlert(Alert.AlertType.ERROR, "❌ Lỗi", "Không thể tạo QR code: " + ex.getMessage());
            }
        });
        
        // Xác nhận thanh toán
        confirmButton.setOnAction(e -> {
            String amount = amountField.getText().trim();
            String orderId = orderField.getText().trim();
            
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("🔔 Xác nhận thanh toán");
            confirmAlert.setHeaderText("Bạn đã nhận được tiền từ MoMo?");
            confirmAlert.setContentText(
                "Vui lòng kiểm tra kỹ:\n\n" +
                "✓ Số tiền: " + String.format("%,d VNĐ", Long.parseLong(amount)) + "\n" +
                "✓ Nội dung: " + orderId + "\n\n" +
                "Bạn có chắc chắn đã nhận được thông báo từ MoMo?"
            );
            
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    String timestamp = java.time.LocalDateTime.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                    
                    statusArea.setText(
                        "🎉 THANH TOÁN THÀNH CÔNG!\n\n" +
                        "✅ ĐÃ XÁC NHẬN NHẬN TIỀN\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                        "💵 Số tiền: " + String.format("%,d VNĐ", Long.parseLong(amount)) + "\n" +
                        "📝 Mã hóa đơn: " + orderId + "\n" +
                        "📱 Số điện thoại: " + PHONE_NUMBER + "\n" +
                        "⏰ Thời gian: " + timestamp + "\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                        "✨ Giao dịch đã hoàn tất!\n" +
                        "📊 Bạn có thể tạo giao dịch mới bằng cách\n" +
                        "   nhập thông tin và nhấn 'TẠO MÃ QR' lại."
                    );
                    
                    statusBox.setStyle(
                        "-fx-background-color: #d4edda; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-color: #28a745; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10;"
                    );
                    
                    showAlert(Alert.AlertType.INFORMATION, "🎉 Thành công", 
                        "Thanh toán đã được xác nhận thành công!\n\n" +
                        "Mã hóa đơn: " + orderId + "\n" +
                        "Số tiền: " + String.format("%,d VNĐ", Long.parseLong(amount))
                    );
                    
                    // Reset
                    confirmButton.setDisable(true);
                    confirmButton.setStyle(
                        "-fx-background-color: #28a745; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 18px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 10; " +
                        "-fx-opacity: 0.5;"
                    );
                    qrBox.setVisible(false);
                    orderField.setText("HD" + System.currentTimeMillis());
                    
                    // Reset status box color sau 3 giây
                    new Thread(() -> {
                        try {
                            Thread.sleep(3000);
                            javafx.application.Platform.runLater(() -> {
                                statusBox.setStyle(
                                    "-fx-background-color: #fff9e6; " +
                                    "-fx-background-radius: 10; " +
                                    "-fx-border-color: #ffc107; " +
                                    "-fx-border-width: 2; " +
                                    "-fx-border-radius: 10;"
                                );
                            });
                        } catch (InterruptedException ignored) {}
                    }).start();
                }
            });
        });
        
        // ============ THÊM TẤT CẢ VÀO MAIN LAYOUT ============
        mainLayout.getChildren().addAll(
            headerBox,
            formBox,
            generateButton,
            qrBox,
            confirmButton,
            statusBox
        );
        
        ScrollPane scrollPane = new ScrollPane(mainLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        Scene scene = new Scene(scrollPane, 800, 1000);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
