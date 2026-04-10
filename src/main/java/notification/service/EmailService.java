package notification.service;

import notification.model.Email;

public class EmailService {
    private final Email config;

    public EmailService(Email config) {
        this.config = config;
    }

    public boolean sendOtpEmail(String toEmail) {
        System.out.println("[Mock] sendOtpEmail to: " + toEmail);
        return true;
    }
}
