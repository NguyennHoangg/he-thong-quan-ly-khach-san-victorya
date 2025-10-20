package payment.model;

/**
 * Cấu hình MoMo Payment Gateway
 * Lưu thông tin partner, access key, secret key
 */
public class MoMoConfig {
    // Thông tin từ MoMo Test Environment (Updated 2025)
    private static final String PARTNER_CODE = "MOMOBKUN20180529"; // Mã đối tác
    private static final String ACCESS_KEY = "klm05TvNBzhg7h7j"; // Access key từ MoMo
    private static final String SECRET_KEY = "at67qH6mk8w5Y1nAyMoYKMWACiEi2bsa"; // Secret key từ MoMo
    
    // Endpoint API
    private static final String PAYMENT_URL = "https://test-payment.momo.vn/v2/gateway/api/create";
    private static final String QUERY_URL = "https://test-payment.momo.vn/v2/gateway/api/query";
    
    // Redirect URLs 
    private static final String RETURN_URL = "https://webhook.site/b3088a6a-2d17-4f80-a92c-2fca1b96dcf3";
    private static final String NOTIFY_URL = "https://webhook.site/b3088a6a-2d17-4f80-a92c-2fca1b96dcf3";
    
    // Request type
    private static final String REQUEST_TYPE = "payWithMethod";
    
    public static String getPartnerCode() {
        return PARTNER_CODE;
    }
    
    public static String getAccessKey() {
        return ACCESS_KEY;
    }
    
    public static String getSecretKey() {
        return SECRET_KEY;
    }
    
    public static String getPaymentUrl() {
        return PAYMENT_URL;
    }
    
    public static String getQueryUrl() {
        return QUERY_URL;
    }
    
    public static String getReturnUrl() {
        return RETURN_URL;
    }
    
    public static String getNotifyUrl() {
        return NOTIFY_URL;
    }
    
    public static String getRequestType() {
        return REQUEST_TYPE;
    }
}
