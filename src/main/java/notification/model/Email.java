package notification.model;

/**
 * Load SMTP configuration from resources/email.properties or environment variables
 */
public class Email {
    private String host;
    private int port;
    private String username;
    private String password;
    private boolean ssl;
    private boolean tls;
    private String fromAddress;

    public Email() {
        // Use AppConfig constants directly per user request
        host = EmailConfig.MAIL_HOST;
        port = Integer.parseInt(EmailConfig.MAIL_PORT);
        username = EmailConfig.MAIL_USERNAME;
        password = EmailConfig.MAIL_PASSWORD;
        ssl = Boolean.parseBoolean(EmailConfig.MAIL_SSL);
        tls = Boolean.parseBoolean(EmailConfig.MAIL_TLS);
        setAddress(fromAddress);
    }

    // Helpers removed: values are taken directly from AppConfig

    public String getHost() { return host; }
    public int getPort() { return port; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public boolean isSsl() { return ssl; }
    public boolean isTls() { return tls; }
    public String getFromAddress() { return fromAddress; }
    public void setAddress(String address){this.fromAddress = address;}
}