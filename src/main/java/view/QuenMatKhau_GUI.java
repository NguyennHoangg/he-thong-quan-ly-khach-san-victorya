package view;

import javax.swing.*;

import javafx.scene.control.Alert;

import java.awt.*;
import notification.model.Email;
import notification.service.EmailService;

public class QuenMatKhau_GUI extends JFrame {
    private Email email = new Email();
    private EmailService emailService = new EmailService(email);

    public QuenMatKhau_GUI() {
    // Khởi tạo JavaFX runtime để dùng Alert trong Swing
    new javafx.embed.swing.JFXPanel();
        setTitle("Quên Mật Khẩu?");
        setSize(1920, 1080);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel pnlTrai = new JPanel();
        pnlTrai.setBackground(Color.WHITE);
        pnlTrai.setLayout(new BoxLayout(pnlTrai, BoxLayout.Y_AXIS));
        pnlTrai.setBorder(BorderFactory.createEmptyBorder(120, 200, 0, 0)); // padding top/left

        ImageIcon bieuTuongQuayLai;
        try {
            // Thử load từ resources trước
            java.net.URL iconUrl = getClass().getResource("/img/chevron_back.png");
            if (iconUrl != null) {
                bieuTuongQuayLai = new ImageIcon(iconUrl);
            } else {
                // Nếu không có trong resources, thử đường dẫn file
                bieuTuongQuayLai = new ImageIcon("src/main/resources/img/chevron_back.png");
            }
            Image anhQuayLai = bieuTuongQuayLai.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            bieuTuongQuayLai = new ImageIcon(anhQuayLai);
        } catch (Exception e) {
            // Nếu không load được, tạo button không có icon
            bieuTuongQuayLai = null;
        }

        JButton btnQuayLai = new JButton("Quay lại", bieuTuongQuayLai);
        btnQuayLai.setFont(new Font("Poppins", Font.PLAIN, 14));
        btnQuayLai.setHorizontalAlignment(SwingConstants.LEFT);
        btnQuayLai.setIconTextGap(4);
        btnQuayLai.setFocusPainted(false);
        btnQuayLai.setBorderPainted(false);
        btnQuayLai.setContentAreaFilled(false);
        btnQuayLai.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuayLai.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTieuDe = new JLabel("Quên mật khẩu?");
        lblTieuDe.setFont(new Font("Poppins", Font.BOLD, 40));
        lblTieuDe.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblHuongDan = new JLabel("Vui lòng nhập email của bạn");
        lblHuongDan.setFont(new Font("Poppins", Font.PLAIN, 16));
        lblHuongDan.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblEmail = new JLabel("Email");
        lblEmail.setFont(new Font("Poppins", Font.PLAIN, 16));
        lblEmail.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtEmail = new JTextField();
        txtEmail.setMaximumSize(new Dimension(512, 56)); // giữ đúng kích thước
        txtEmail.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnTiepTuc = new JButton("Tiếp tục");
        btnTiepTuc.setFont(new Font("Poppins", Font.PLAIN, 16));
        btnTiepTuc.setBackground(new Color(0x515DEF));
        btnTiepTuc.setForeground(Color.WHITE);
        btnTiepTuc.setFocusPainted(false);
        btnTiepTuc.setBorderPainted(false);
        btnTiepTuc.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTiepTuc.setMaximumSize(new Dimension(512, 56));
        btnTiepTuc.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlTrai.add(Box.createVerticalStrut(200));
        pnlTrai.add(btnQuayLai);
        pnlTrai.add(Box.createVerticalStrut(20));
        pnlTrai.add(lblTieuDe);
        pnlTrai.add(Box.createVerticalStrut(10));
        pnlTrai.add(lblHuongDan);
        pnlTrai.add(Box.createVerticalStrut(10));
        pnlTrai.add(lblEmail);
        pnlTrai.add(Box.createVerticalStrut(5));
        pnlTrai.add(txtEmail);
        pnlTrai.add(Box.createVerticalStrut(20));
        pnlTrai.add(btnTiepTuc);

        JPanel pnlPhai = new JPanel();
        pnlPhai.setBackground(Color.WHITE);
        pnlPhai.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlPhai.setBorder(BorderFactory.createEmptyBorder(117, 122, 118, 224));

        ImageIcon bieuTuongHinh;
        try {
            // Thử load từ resources trước
            java.net.URL imgUrl = getClass().getResource("/img/QuenMatKhau_img1.png");
            if (imgUrl != null) {
                bieuTuongHinh = new ImageIcon(imgUrl);
            } else {
                // Nếu không có trong resources, thử đường dẫn file
                bieuTuongHinh = new ImageIcon("src/main/resources/img/QuenMatKhau_img1.png");
            }
            Image anhQuenMatKhau = bieuTuongHinh.getImage().getScaledInstance(614, 845, Image.SCALE_SMOOTH);
            bieuTuongHinh = new ImageIcon(anhQuenMatKhau);
        } catch (Exception e) {
            // Tạo ảnh placeholder nếu không load được - màu xám nhạt
            java.awt.image.BufferedImage placeholder = new java.awt.image.BufferedImage(614, 845, java.awt.image.BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = placeholder.createGraphics();
            g2.setColor(new Color(240, 240, 240));
            g2.fillRect(0, 0, 614, 845);
            g2.setColor(Color.GRAY);
            g2.drawString("Không tìm thấy ảnh", 250, 400);
            g2.dispose();
            bieuTuongHinh = new ImageIcon(placeholder);
        }
        ImageIcon bieuTuongHinhDaChinh = bieuTuongHinh;

        JPanel pnlKhungHinh = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(240, 240, 240));
                g2.fillRoundRect(0, 0, 614, 845, 30, 30);
                g2.dispose();
            }
        };
        pnlKhungHinh.setPreferredSize(new Dimension(614, 845));
        pnlKhungHinh.setLayout(new BorderLayout());
        pnlKhungHinh.setOpaque(false);

        // Tạo JLabel với hình ảnh bo góc
        JLabel lblHinh = new JLabel(bieuTuongHinhDaChinh) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, 614, 845, 30, 30));
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        lblHinh.setOpaque(false);

        pnlKhungHinh.add(lblHinh, BorderLayout.CENTER);
        pnlPhai.add(pnlKhungHinh);

        add(pnlTrai);
        add(pnlPhai);
        setLayout(new GridLayout(1, 2));
        setBackground(Color.WHITE);

        setVisible(true);

        btnTiepTuc.addActionListener(e -> {
            // TODO: Implement the action to handle "Tiếp tục" button click
            String emailNguoiDung = txtEmail.getText();
            email.setAddress(emailNguoiDung);
            boolean result = emailService.sendOtpEmail(emailNguoiDung);
            if(result){
                javafx.application.Platform.runLater(() -> {
                    Alert alert = new Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Gửi mã OTP thành công");
                    alert.setHeaderText(null);
                    alert.setContentText("Chúng tôi đã gửi mã OTP về email của bạn. Vui lòng kiểm tra email!");
                    alert.showAndWait();
                });
            }
        });
    }
}
