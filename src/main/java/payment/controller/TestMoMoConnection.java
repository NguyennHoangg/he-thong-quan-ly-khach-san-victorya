package payment.controller;

import io.github.cdimascio.dotenv.Dotenv;
import payment.model.MoMoConfig;

/**
 * Test kết nối và hiển thị thông tin MoMo Config
 */
public class TestMoMoConnection {
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("     TEST MOMO CONFIGURATION");
        System.out.println("===========================================\n");
        
        // Load .env
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        
        System.out.println("📋 THÔNG TIN TỪ FILE .ENV:");
        System.out.println("-------------------------------------------");
        System.out.println("MOMO_PARTNER_CODE: " + dotenv.get("MOMO_PARTNER_CODE", "NOT_FOUND"));
        System.out.println("MOMO_ACCESS_KEY: " + dotenv.get("MOMO_ACCESS_KEY", "NOT_FOUND"));
        System.out.println("MOMO_SECRET_KEY: " + maskString(dotenv.get("MOMO_SECRET_KEY", "NOT_FOUND")));
        System.out.println("MOMO_PAYMENT_URL: " + dotenv.get("MOMO_PAYMENT_URL", "NOT_FOUND"));
        System.out.println("MOMO_QUERY_URL: " + dotenv.get("MOMO_QUERY_URL", "NOT_FOUND"));
        System.out.println("MOMO_RETURN_URL: " + dotenv.get("MOMO_RETURN_URL", "NOT_FOUND"));
        System.out.println("MOMO_NOTIFY_URL: " + dotenv.get("MOMO_NOTIFY_URL", "NOT_FOUND"));
        System.out.println("MOMO_REQUEST_TYPE: " + dotenv.get("MOMO_REQUEST_TYPE", "NOT_FOUND"));
        
        System.out.println("\n📋 THÔNG TIN TỪ MoMoConfig CLASS:");
        System.out.println("-------------------------------------------");
        System.out.println("Partner Code: " + MoMoConfig.getPartnerCode());
        System.out.println("Access Key: " + MoMoConfig.getAccessKey());
        System.out.println("Secret Key: " + maskString(MoMoConfig.getSecretKey()));
        System.out.println("Payment URL: " + MoMoConfig.getPaymentUrl());
        System.out.println("Query URL: " + MoMoConfig.getQueryUrl());
        System.out.println("Return URL: " + MoMoConfig.getReturnUrl());
        System.out.println("Notify URL: " + MoMoConfig.getNotifyUrl());
        System.out.println("Request Type: " + MoMoConfig.getRequestType());
        
        System.out.println("\n===========================================");
        System.out.println("     TEST PAYMENT REQUEST");
        System.out.println("===========================================\n");
        
        try {
            System.out.println("✅ MoMoPaymentService sẵn sàng!");
            System.out.println("⏳ Đang thử tạo payment request...\n");
            
            // Test với số tiền nhỏ
            var payment = MoMoPaymentService.createPayment(10000, "Test Order " + System.currentTimeMillis());
            
            System.out.println("🎉 PAYMENT CREATED SUCCESSFULLY!");
            System.out.println("-------------------------------------------");
            System.out.println("Order ID: " + payment.getOrderId());
            System.out.println("Request ID: " + payment.getRequestId());
            System.out.println("Amount: " + payment.getAmount());
            System.out.println("Result Code: " + payment.getResultCode());
            System.out.println("Message: " + payment.getMessage());
            System.out.println("Pay URL: " + payment.getPayUrl());
            System.out.println("QR Code URL: " + payment.getQrCodeUrl());
            
        } catch (Exception e) {
            System.err.println("❌ LỖI KHI TẠO PAYMENT:");
            System.err.println("-------------------------------------------");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            
            System.out.println("\n💡 HƯỚNG DẪN KHẮC PHỤC:");
            System.out.println("-------------------------------------------");
            System.out.println("1. Kiểm tra Partner Code đã được kích hoạt chưa");
            System.out.println("2. Kiểm tra Access Key và Secret Key có đúng không");
            System.out.println("3. Kiểm tra IP có được whitelist bởi MoMo chưa");
            System.out.println("4. Liên hệ MoMo support: business@momo.vn");
            System.out.println("5. Hoặc sử dụng phương pháp Personal Payment (không cần API)");
        }
        
        System.out.println("\n===========================================");
    }
    
    private static String maskString(String str) {
        if (str == null || str.equals("NOT_FOUND")) return str;
        if (str.length() <= 8) return "***" + str.substring(str.length() - 2);
        return str.substring(0, 4) + "***" + str.substring(str.length() - 4);
    }
}
