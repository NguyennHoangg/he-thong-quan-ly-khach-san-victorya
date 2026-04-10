package notification.model;

/**
 * Application-level defaults for environment variables.
 * NOTE: these values are embedded per user request. Do NOT commit secrets to
 * public repos.
 */
public final class EmailConfig {
    private EmailConfig() {
    }

    // SMTP
    public static final String MAIL_HOST = "smtp.gmail.com";
    public static final String MAIL_PORT = "587";
    public static final String MAIL_USERNAME = "victoryahotelptud@gmail.com";
    public static final String MAIL_PASSWORD = "zgpv jxol hblk wlod"; // App Password
    public static final String MAIL_TLS = "true";
    public static final String MAIL_SSL = "true";
    public static final String MAIL_DEBUG = "false";
    public static final String MAIL_FROM_NAME = "Victorya Hotel";
    public static final String MAIL_TIMEOUT_MS = "10000";

    // OTP
    public static final String OTP_EXPIRATION_MINUTES = "5";

    // MoMo placeholders
    public static final String MOMO_ENV = "test";
    public static final String MOMO_API_URL = "https://test-payment.momo.vn/gw_payment/transactionProcessor";
    public static final String MOMO_PARTNER_CODE = "";
    public static final String MOMO_ACCESS_KEY = "";
    public static final String MOMO_SECRET_KEY = "";
    public static final String MOMO_RETURN_URL = "http://localhost:8080/payment/momo/return";
    public static final String MOMO_NOTIFY_URL = "http://localhost:8080/payment/momo/notify";
}
