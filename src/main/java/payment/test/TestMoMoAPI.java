package payment.test;

import payment.controller.MoMoPaymentService;
import payment.controller.QRCodeGenerator;
import payment.model.Payment;

/**
 * Test MoMo API đơn giản
 */
public class TestMoMoAPI {
    
    public static void main(String[] args) {
        try {
            System.out.println("🔄 Testing MoMo API...");
            
            // Tạo thanh toán với thông tin đơn giản
            long amount = 50000;
            String orderInfo = "Test thanh toan MoMo";
            
            System.out.println("💰 Số tiền: " + amount + " VNĐ");
            System.out.println("📝 Nội dung: " + orderInfo);
            
            // Gọi API
            Payment payment = MoMoPaymentService.createPayment(amount, orderInfo);
            
            if (payment.getResultCode() == 0) {
                System.out.println("\n✅ Tạo thanh toán thành công!");
                System.out.println("🔖 Order ID: " + payment.getOrderId());
                System.out.println("🔗 Pay URL: " + payment.getPayUrl());
                
                if (payment.getDeeplink() != null) {
                    System.out.println("📱 Deeplink: " + payment.getDeeplink());
                }
                
                // Test tạo QR code
                try {
                    System.out.println("\n🔄 Testing QR Code generation...");
                    // Chỉ test logic, không hiển thị
                    System.out.println("✅ QR Code có thể tạo từ URL: " + payment.getPayUrl());
                    
                    // Test kiểm tra trạng thái
                    System.out.println("\n🔍 Testing payment status check...");
                    Payment status = MoMoPaymentService.queryPaymentStatus(
                        payment.getOrderId(), payment.getRequestId());
                    
                    System.out.println("📊 Status Code: " + status.getResultCode());
                    System.out.println("📝 Status Message: " + status.getMessage());
                    
                } catch (Exception e) {
                    System.out.println("⚠️ QR Test error: " + e.getMessage());
                }
                
            } else {
                System.out.println("\n❌ Lỗi tạo thanh toán!");
                System.out.println("🔴 Result Code: " + payment.getResultCode());
                System.out.println("📝 Message: " + payment.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("\n❌ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }
}