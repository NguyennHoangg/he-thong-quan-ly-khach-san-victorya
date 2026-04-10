package payment.controller;

import payment.model.Payment;

public class MoMoPaymentService {
    public static Payment createPayment(long amount, String orderInfo) throws Exception {
        Payment p = new Payment();
        p.setResultCode(0);
        p.setMessage("Mock payment created");
        p.setPayUrl("https://mock.momo.vn/pay");
        p.setOrderId("MOCK-" + System.currentTimeMillis());
        p.setRequestId("REQ-" + System.currentTimeMillis());
        return p;
    }

    public static boolean verifySignature(String rawData, String receivedSignature) throws Exception {
        return true;
    }

    public static Payment queryPaymentStatus(String orderId, String requestId) throws Exception {
        Payment p = new Payment();
        p.setOrderId(orderId);
        p.setRequestId(requestId);
        p.setResultCode(0);
        p.setMessage("Mock payment success");
        return p;
    }
}
