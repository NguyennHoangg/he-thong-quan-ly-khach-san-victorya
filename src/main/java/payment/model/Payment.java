package payment.model;

import java.time.LocalDateTime;

/**
 * Model cho thông tin thanh toán MoMo
 */
public class Payment {
    private String orderId;          // Mã đơn hàng
    private String requestId;        // Mã request
    private long amount;             // Số tiền
    private String orderInfo;        // Thông tin đơn hàng
    private String partnerCode;      // Mã đối tác
    private String redirectUrl;      // URL để chuyển hướng
    private String ipnUrl;           // URL nhận thông báo
    private String requestType;      // Loại request
    private String extraData;        // Dữ liệu thêm
    private String lang;             // Ngôn ngữ
    private String signature;        // Chữ ký
    
    // Response fields
    private int resultCode;          // Mã kết quả
    private String message;          // Thông báo
    private String payUrl;           // URL thanh toán (QR code)
    private String deeplink;         // Deep link cho app
    private String qrCodeUrl;        // URL QR code
    
    // Transaction info
    private LocalDateTime createdAt; // Thời gian tạo
    private LocalDateTime updatedAt; // Thời gian cập nhật
    private String transId;          // Mã giao dịch MoMo
    private String payType;          // Loại thanh toán
    
    // Constructors
    public Payment() {
        this.createdAt = LocalDateTime.now();
        this.lang = "vi";
    }
    
    public Payment(String orderId, long amount, String orderInfo) {
        this();
        this.orderId = orderId;
        this.amount = amount;
        this.orderInfo = orderInfo;
    }
    
    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public long getAmount() {
        return amount;
    }
    
    public void setAmount(long amount) {
        this.amount = amount;
    }
    
    public String getOrderInfo() {
        return orderInfo;
    }
    
    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }
    
    public String getPartnerCode() {
        return partnerCode;
    }
    
    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }
    
    public String getRedirectUrl() {
        return redirectUrl;
    }
    
    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
    
    public String getIpnUrl() {
        return ipnUrl;
    }
    
    public void setIpnUrl(String ipnUrl) {
        this.ipnUrl = ipnUrl;
    }
    
    public String getRequestType() {
        return requestType;
    }
    
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
    
    public String getExtraData() {
        return extraData;
    }
    
    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }
    
    public String getLang() {
        return lang;
    }
    
    public void setLang(String lang) {
        this.lang = lang;
    }
    
    public String getSignature() {
        return signature;
    }
    
    public void setSignature(String signature) {
        this.signature = signature;
    }
    
    public int getResultCode() {
        return resultCode;
    }
    
    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getPayUrl() {
        return payUrl;
    }
    
    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }
    
    public String getDeeplink() {
        return deeplink;
    }
    
    public void setDeeplink(String deeplink) {
        this.deeplink = deeplink;
    }
    
    public String getQrCodeUrl() {
        return qrCodeUrl;
    }
    
    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getTransId() {
        return transId;
    }
    
    public void setTransId(String transId) {
        this.transId = transId;
    }
    
    public String getPayType() {
        return payType;
    }
    
    public void setPayType(String payType) {
        this.payType = payType;
    }
    
    @Override
    public String toString() {
        return "Payment{" +
                "orderId='" + orderId + '\'' +
                ", amount=" + amount +
                ", orderInfo='" + orderInfo + '\'' +
                ", resultCode=" + resultCode +
                ", message='" + message + '\'' +
                ", transId='" + transId + '\'' +
                '}';
    }
}

