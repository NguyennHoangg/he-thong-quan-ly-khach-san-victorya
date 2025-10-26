package payment.model;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Cấu hình MoMo Payment Gateway
 * Lưu thông tin partner, access key, secret key
 * Đọc từ file .env
 */
public class MoMoConfig {
    private static final Dotenv dotenv = Dotenv.configure().directory(".").ignoreIfMissing().load();
    
    // Thông tin từ MoMo (đọc từ .env hoặc giá trị mặc định cho test)
    private static final String PARTNER_CODE = dotenv.get("MOMO_PARTNER_CODE");
    private static final String ACCESS_KEY = dotenv.get("MOMO_ACCESS_KEY");
    private static final String SECRET_KEY = dotenv.get("MOMO_SECRET_KEY");
    
    // Endpoint API
    private static final String PAYMENT_URL = dotenv.get("MOMO_PAYMENT_URL", "https://test-payment.momo.vn/v2/gateway/api/create");
    private static final String QUERY_URL = dotenv.get("MOMO_QUERY_URL", "https://test-payment.momo.vn/v2/gateway/api/query");
    
    // Redirect URLs 
    private static final String RETURN_URL = dotenv.get("MOMO_RETURN_URL", "https://webhook.site/b3088a6a-2d17-4f80-a92c-2fca1b96dcf3");
    private static final String NOTIFY_URL = dotenv.get("MOMO_NOTIFY_URL", "https://webhook.site/b3088a6a-2d17-4f80-a92c-2fca1b96dcf3");
    
    // Request type
    private static final String REQUEST_TYPE = dotenv.get("MOMO_REQUEST_TYPE", "payWithMethod");
    
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
