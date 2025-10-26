package payment.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import payment.model.MoMoConfig;
import payment.model.Payment;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Service xử lý thanh toán MoMo
 * Tạo chữ ký, gửi request, xử lý response
 */
public class MoMoPaymentService {
    private static final OkHttpClient httpClient = new OkHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Tạo thanh toán MoMo
     * @param amount Số tiền thanh toán
     * @param orderInfo Thông tin đơn hàng
     * @return Payment object với payUrl để hiển thị QR code
     * @throws Exception nếu có lỗi
     */
    public static Payment createPayment(long amount, String orderInfo) throws Exception {
        // Tạo orderId và requestId unique
        String orderId = "HD" + System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();
        
        // Tạo Payment object
        Payment payment = new Payment(orderId, amount, orderInfo);
        payment.setRequestId(requestId);
        payment.setPartnerCode(MoMoConfig.getPartnerCode());
        payment.setRedirectUrl(MoMoConfig.getReturnUrl());
        payment.setIpnUrl(MoMoConfig.getNotifyUrl());
        payment.setRequestType(MoMoConfig.getRequestType());
        payment.setExtraData(""); // Có thể thêm data tùy chỉnh
        
        // Tạo chữ ký
        String signature = generateSignature(payment);
        payment.setSignature(signature);
        
        // Gửi request đến MoMo
        Payment response = sendPaymentRequest(payment);
        
        return response;
    }
    
    /**
     * Tạo chữ ký HMAC SHA256 theo quy định của MoMo
     * Chuỗi raw signature: accessKey=$accessKey&amount=$amount&extraData=$extraData
     * &ipnUrl=$ipnUrl&orderId=$orderId&orderInfo=$orderInfo&partnerCode=$partnerCode
     * &redirectUrl=$redirectUrl&requestId=$requestId&requestType=$requestType
     */
    private static String generateSignature(Payment payment) throws Exception {
        // Clean orderInfo - chỉ giữ ký tự cơ bản cho signature
        String cleanOrderInfo = payment.getOrderInfo()
            .replace("\n", " ")   // Thay xuống dòng = space
            .replace("\r", " ")   // Thay carriage return = space
            .replace("\t", " ")   // Thay tab = space
            .trim();              // Xóa space đầu cuối
        
        // Tạo raw signature theo thứ tự alphabet
        String rawSignature = "accessKey=" + MoMoConfig.getAccessKey() +
                "&amount=" + payment.getAmount() +
                "&extraData=" + payment.getExtraData() +
                "&ipnUrl=" + payment.getIpnUrl() +
                "&orderId=" + payment.getOrderId() +
                "&orderInfo=" + cleanOrderInfo +
                "&partnerCode=" + payment.getPartnerCode() +
                "&redirectUrl=" + payment.getRedirectUrl() +
                "&requestId=" + payment.getRequestId() +
                "&requestType=" + payment.getRequestType();
        
        System.out.println("🔍 Raw Signature: " + rawSignature);
        
        // Tạo HMAC SHA256
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(
            MoMoConfig.getSecretKey().getBytes(StandardCharsets.UTF_8), 
            "HmacSHA256"
        );
        sha256_HMAC.init(secret_key);
        
        byte[] hash = sha256_HMAC.doFinal(rawSignature.getBytes(StandardCharsets.UTF_8));
        
        // Convert to hex string
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        
        String signature = hexString.toString();
        System.out.println("🔍 Generated Signature: " + signature);
        
        return signature;
    }
    
    /**
     * Gửi request thanh toán đến MoMo API
     */
    private static Payment sendPaymentRequest(Payment payment) throws Exception {
        // Tạo JSON request body
        String jsonBody = String.format(
            "{\"partnerCode\":\"%s\"," +
            "\"partnerName\":\"Test\"," +
            "\"storeId\":\"MomoTestStore\"," +
            "\"requestId\":\"%s\"," +
            "\"amount\":%d," +
            "\"orderId\":\"%s\"," +
            "\"orderInfo\":\"%s\"," +
            "\"redirectUrl\":\"%s\"," +
            "\"ipnUrl\":\"%s\"," +
            "\"lang\":\"%s\"," +
            "\"extraData\":\"%s\"," +
            "\"requestType\":\"%s\"," +
            "\"signature\":\"%s\"}",
            payment.getPartnerCode(),
            payment.getRequestId(),
            payment.getAmount(),
            payment.getOrderId(),
            payment.getOrderInfo(),
            payment.getRedirectUrl(),
            payment.getIpnUrl(),
            payment.getLang(),
            payment.getExtraData(),
            payment.getRequestType(),
            payment.getSignature()
        );
        
        // Tạo request
        RequestBody body = RequestBody.create(
            jsonBody,
            MediaType.parse("application/json; charset=UTF-8")
        );
        
        Request request = new Request.Builder()
            .url(MoMoConfig.getPaymentUrl())
            .post(body)
            .build();
        
        // Gửi request và nhận response
        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            
            if (!response.isSuccessful()) {
                throw new Exception("MoMo API Error: " + response.code() + " - " + responseBody);
            }
            
            // Parse JSON response
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            
            payment.setResultCode(jsonNode.get("resultCode").asInt());
            payment.setMessage(jsonNode.get("message").asText());
            
            if (payment.getResultCode() == 0) {
                // Thành công
                payment.setPayUrl(jsonNode.get("payUrl").asText());
            }
            
            return payment;
        }
    }
    
    /**
     * Xác thực chữ ký callback từ MoMo
     * @param params Các tham số từ callback
     * @param receivedSignature Chữ ký nhận được
     * @return true nếu hợp lệ
     */
    public static boolean verifySignature(String rawData, String receivedSignature) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(
            MoMoConfig.getSecretKey().getBytes(StandardCharsets.UTF_8), 
            "HmacSHA256"
        );
        sha256_HMAC.init(secret_key);
        
        byte[] hash = sha256_HMAC.doFinal(rawData.getBytes(StandardCharsets.UTF_8));
        
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        
        return hexString.toString().equals(receivedSignature);
    }
    
    /**
     * Kiểm tra trạng thái thanh toán
     * @param orderId Mã đơn hàng
     * @param requestId Mã request
     * @return Payment với thông tin mới nhất
     */
    public static Payment queryPaymentStatus(String orderId, String requestId) throws Exception {
        // Tạo signature cho query
        String rawSignature = "accessKey=" + MoMoConfig.getAccessKey() +
                "&orderId=" + orderId +
                "&partnerCode=" + MoMoConfig.getPartnerCode() +
                "&requestId=" + requestId;
        
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(
            MoMoConfig.getSecretKey().getBytes(StandardCharsets.UTF_8), 
            "HmacSHA256"
        );
        sha256_HMAC.init(secret_key);
        
        byte[] hash = sha256_HMAC.doFinal(rawSignature.getBytes(StandardCharsets.UTF_8));
        
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        String signature = hexString.toString();
        
        // Tạo JSON request
        String jsonBody = String.format(
            "{\"partnerCode\":\"%s\"," +
            "\"requestId\":\"%s\"," +
            "\"orderId\":\"%s\"," +
            "\"lang\":\"vi\"," +
            "\"signature\":\"%s\"}",
            MoMoConfig.getPartnerCode(),
            requestId,
            orderId,
            signature
        );
        
        RequestBody body = RequestBody.create(
            jsonBody,
            MediaType.parse("application/json; charset=UTF-8")
        );
        
        Request request = new Request.Builder()
            .url(MoMoConfig.getQueryUrl())
            .post(body)
            .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("MoMo Query API Error: " + response.code());
            }
            
            String responseBody = response.body().string();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            
            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setRequestId(requestId);
            payment.setResultCode(jsonNode.get("resultCode").asInt());
            payment.setMessage(jsonNode.get("message").asText());
            
            if (jsonNode.has("amount")) {
                payment.setAmount(jsonNode.get("amount").asLong());
            }
            if (jsonNode.has("transId")) {
                payment.setTransId(jsonNode.get("transId").asText());
            }
            if (jsonNode.has("payType")) {
                payment.setPayType(jsonNode.get("payType").asText());
            }
            
            return payment;
        }
    }
}
