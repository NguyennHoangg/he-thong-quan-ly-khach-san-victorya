package payment.controller;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * ============================================================
 * DEMO MOMO BUSINESS API - TỰ ĐỘNG XÁC NHẬN THANH TOÁN
 * ============================================================
 * 
 * Phương thức: MoMo Payment Gateway API
 * - Sử dụng API key từ MoMo Business
 * - Tự động tạo QR code và theo dõi trạng thái
 * - Tự động xác nhận khi thanh toán thành công
 * 
 * Lưu ý: Đang dùng Test Environment của MoMo
 */
public class MoMoPaymentDemo extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Demo MoMo Business API - Khách sạn Victorya");
        
        // Main layout
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(40));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");
        
        // Header
        Label headerLabel = new Label("💼 DEMO MOMO BUSINESS API");
        headerLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        headerLabel.setStyle("-fx-text-fill: #A50064;");
        
        Label subHeaderLabel = new Label("Tự động tạo QR và xác nhận thanh toán");
        subHeaderLabel.setFont(Font.font("System", 14));
        subHeaderLabel.setStyle("-fx-text-fill: #666;");
        
        // Form
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(30));
        form.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        Label amountLabel = new Label("Số tiền:");
        amountLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        TextField amountField = new TextField("100000");
        amountField.setPrefWidth(250);
        amountField.setPromptText("Nhập số tiền (VNĐ)");
        
        Label orderInfoLabel = new Label("Thông tin:");
        orderInfoLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        TextField orderInfoField = new TextField("Thanh toán phòng VIP001");
        orderInfoField.setPrefWidth(250);
        orderInfoField.setPromptText("Nhập thông tin đơn hàng");
        
        form.add(amountLabel, 0, 0);
        form.add(amountField, 1, 0);
        form.add(orderInfoLabel, 0, 1);
        form.add(orderInfoField, 1, 1);
        
        // Button
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button payButton = new Button("🚀 Thanh toán MoMo Business API");
        payButton.setPrefWidth(300);
        payButton.setPrefHeight(45);
        payButton.setStyle(
            "-fx-background-color: #A50064; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 5;"
        );
        
        // Result area
        TextArea resultArea = new TextArea();
        resultArea.setPrefHeight(250);
        resultArea.setEditable(false);
        resultArea.setWrapText(true);
        resultArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        resultArea.setText(
            "📋 THÔNG TIN MOMO BUSINESS API:\n\n" +
            "✅ Ưu điểm:\n" +
            "  • Tự động tạo QR code từ MoMo server\n" +
            "  • Tự động theo dõi trạng thái thanh toán\n" +
            "  • Tự động xác nhận khi khách thanh toán\n" +
            "  • Có API callback (IPN)\n" +
            "  • Chuyên nghiệp, đáng tin cậy\n\n" +
            "⚠️ Yêu cầu:\n" +
            "  • Đăng ký tài khoản MoMo Business\n" +
            "  • Có Partner Code, Access Key, Secret Key\n" +
            "  • Xác minh doanh nghiệp\n\n" +
            "🔧 Trạng thái hiện tại:\n" +
            "  • Đang dùng MoMo Test Environment\n" +
            "  • Partner Code: MOMOBKUN20180529\n" +
            "  • API Endpoint: test-payment.momo.vn\n\n" +
            "💡 Lưu ý:\n" +
            "  • Test environment có thể không chấp nhận thanh toán thật\n" +
            "  • Cần credentials production để nhận tiền thật\n"
        );
        
        // Payment action
        payButton.setOnAction(e -> {
            try {
                long amount = Long.parseLong(amountField.getText().trim());
                String orderInfo = orderInfoField.getText().trim();
                
                if (amount <= 0) {
                    showAlert(Alert.AlertType.WARNING, "Lỗi", "Số tiền phải lớn hơn 0!");
                    return;
                }
                
                if (orderInfo.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Lỗi", "Vui lòng nhập thông tin đơn hàng!");
                    return;
                }
                
                resultArea.setText("⏳ Đang gọi MoMo API...\n");
                resultArea.appendText("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
                
                // Tạo controller và hiển thị payment dialog
                MoMoPaymentController controller = new MoMoPaymentController();
                controller.showPaymentDialog(
                    amount,
                    orderInfo,
                    // onSuccess
                    () -> {
                        resultArea.appendText("✅ THANH TOÁN THÀNH CÔNG!\n");
                        resultArea.appendText("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                        resultArea.appendText("📋 Thông tin:\n");
                        resultArea.appendText("  • Đơn hàng: " + orderInfo + "\n");
                        resultArea.appendText("  • Số tiền: " + String.format("%,d VNĐ", amount) + "\n");
                        resultArea.appendText("  • Phương thức: MoMo Business API\n");
                        resultArea.appendText("  • Thời gian: " + java.time.LocalDateTime.now().format(
                            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                        ) + "\n");
                        resultArea.appendText("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
                        resultArea.appendText("🎉 Giao dịch đã được xác nhận tự động!\n");
                        
                        showAlert(Alert.AlertType.INFORMATION, "Thành công", 
                            "Thanh toán thành công!\nSố tiền: " + String.format("%,d VNĐ", amount));
                    },
                    // onFailure
                    () -> {
                        resultArea.appendText("❌ THANH TOÁN THẤT BẠI!\n");
                        resultArea.appendText("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                        resultArea.appendText("⚠️ Có thể do:\n");
                        resultArea.appendText("  • Khách hủy thanh toán\n");
                        resultArea.appendText("  • Hết thời gian chờ\n");
                        resultArea.appendText("  • Lỗi kết nối\n");
                        resultArea.appendText("  • Test environment không hỗ trợ\n");
                        
                        showAlert(Alert.AlertType.ERROR, "Thất bại", "Thanh toán không thành công!");
                    }
                );
                
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Số tiền không hợp lệ!");
            }
        });
        
        buttonBox.getChildren().add(payButton);
        
        // Instruction
        Label instructionLabel = new Label(
            "📱 HƯỚNG DẪN SỬ DỤNG:\n\n" +
            "1. Nhập số tiền và thông tin đơn hàng\n" +
            "2. Nhấn 'Thanh toán MoMo Business API'\n" +
            "3. Hệ thống tự động gọi API MoMo để tạo giao dịch\n" +
            "4. QR code tự động hiển thị từ MoMo server\n" +
            "5. Khách quét QR bằng app MoMo\n" +
            "6. Khách xác nhận thanh toán trên điện thoại\n" +
            "7. Hệ thống TỰ ĐỘNG kiểm tra và xác nhận (mỗi 3 giây)\n" +
            "8. Thông báo kết quả tự động\n\n" +
            "🔄 So sánh với Personal Payment:\n" +
            "  Personal: Tạo QR thủ công, xác nhận thủ công\n" +
            "  Business API: Tạo QR tự động, xác nhận tự động ✨\n\n" +
            "⚠️ Test Environment:\n" +
            "  • Hiện tại đang dùng MoMo test credentials\n" +
            "  • Có thể không thanh toán được tiền thật\n" +
            "  • Để nhận tiền thật, cần đăng ký MoMo Business chính thức"
        );
        instructionLabel.setFont(Font.font("System", 12));
        instructionLabel.setStyle(
            "-fx-text-fill: #666; " +
            "-fx-background-color: #e7f3ff; " +
            "-fx-padding: 20; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: #2196F3; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10;"
        );
        
        // Add all to main layout
        mainLayout.getChildren().addAll(
            headerLabel,
            subHeaderLabel,
            form,
            buttonBox,
            new Label("📊 Kết quả:"),
            resultArea,
            instructionLabel
        );
        
        ScrollPane scrollPane = new ScrollPane(mainLayout);
        scrollPane.setFitToWidth(true);
        
        Scene scene = new Scene(scrollPane, 700, 950);
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
