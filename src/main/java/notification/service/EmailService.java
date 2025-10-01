package notification.service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import notification.model.Email;

import java.util.Properties;

/**
 * Simple EmailService using JavaMail to send OTP emails
 */
public class EmailService {
    private final Email config;
    private final Session session;

    public EmailService(Email config) {
        this.config = config;
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", String.valueOf(config.isTls()));
        props.put("mail.smtp.host", config.getHost());
        props.put("mail.smtp.port", String.valueOf(config.getPort()));
        // recommended timeouts
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");
        if (config.isSsl()) {
            props.put("mail.smtp.socketFactory.port", String.valueOf(config.getPort()));
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }

        // Basic validation and helpful logs (do not print secrets)
        if (config.getHost() == null || config.getUsername() == null || config.getPassword() == null) {
            System.err.println("[EmailService] Warning: Missing MAIL_HOST, MAIL_USERNAME or MAIL_PASSWORD. Email sending will fail until these are configured.");
        }

        session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(config.getUsername(), config.getPassword());
            }
        });
    }

    
    /**
     * Gửi email chứa mã OTP (One-Time Password) để đặt lại mật khẩu tới địa chỉ email đích.
     *
     * Mô tả:
     * - Tạo mã OTP bằng OTPService.generateOTP(toEmail, 300) (300 giây = 5 phút).
     * - Tạo MimeMessage sử dụng trường session và thiết lập địa chỉ người gửi (config.getFromAddress() nếu khác null, ngược lại dùng config.getUsername()).
     * - Thiết lập người nhận, tiêu đề và nội dung email dưới dạng HTML (charset UTF-8) với mã OTP và hướng dẫn sử dụng.
     * - Gửi email bằng Transport.send(message).
     *
     * Lưu ý:
     * - Phương thức bắt MessagingException và in stack trace; trong trường hợp bắt được exception sẽ trả về false.
     * - Phương thức trả về true khi gửi thành công, false khi có MessagingException xảy ra.
     * - Phương thức không kiểm tra chi tiết tính hợp lệ của toEmail (ví dụ null hoặc định dạng hợp lệ); các lỗi khác như NullPointerException có thể xảy ra nếu session hoặc config không được khởi tạo đúng.
     * - Thời hạn của OTP được xác định bởi OTPService (tại đây là 5 phút).
     *
     * @param toEmail địa chỉ email người nhận
     * @return true nếu email OTP được gửi thành công; false nếu có lỗi trong quá trình gửi (MessagingException)
     */
    public boolean sendOtpEmail(String toEmail) {
        //Check null 
        if(toEmail.isBlank()){
            return false;
        }
        
        try {
            String otpCode = OTPService.generateOTP(toEmail, 300);
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(config.getFromAddress() == null ? config.getUsername() : config.getFromAddress()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("[Victorya] Mã OTP đặt lại mật khẩu");

            String body = "<html><body>" +
                    "<p>Xin chào,</p>" +
                    "<p>Bạn yêu cầu đặt lại mật khẩu. Mã OTP của bạn là:</p>" +
                    "<h2 style=\"color:#2b6cb0;\">" + otpCode + "</h2>" +
                    "<p>Mã sẽ hết hạn sau 5 phút. Nếu bạn không yêu cầu, vui lòng bỏ qua email này.</p>" +
                    "<p>Trân trọng,<br/>Victorya Team</p>" +
                    "</body></html>";

            message.setContent(body, "text/html; charset=utf-8");

            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}
