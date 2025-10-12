package iuh.fit.se.group13.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class DangNhap_GUI extends JFrame {

    public DangNhap_GUI() {
        setTitle("Victorya - Đăng nhập");
        setSize(1920, 1080);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel trái
        JPanel panelTrai = new JPanel() {
            private final Image background = new ImageIcon("src/main/java/iuh/fit/se/group13/img/khachSan.png")
                    .getImage();

            // tạo bo tròn
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();

                // Bật khử răng cưa cho đẹp
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();
                int arc = 90; // độ bo góc

                // Tạo hình tròn góc
                Shape clip = new RoundRectangle2D.Float(0, 0, width, height, arc, arc);
                g2.setClip(clip);

                // Vẽ ảnh bo góc
                g2.drawImage(background, 0, 0, width, height, this);

                g2.dispose();
            }
        };
        panelTrai.setPreferredSize(new Dimension(850, 0));

        // Panel phải (form login)
        JPanel panelPhai = new JPanel(new GridBagLayout());
        panelPhai.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        // Tiêu đề
        JLabel lblTieuDe = new JLabel("Victorya", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 50));
        lblTieuDe.setForeground(new Color(33, 150, 243));

        JLabel lblChaoMung = new JLabel("WELCOME BACK", SwingConstants.CENTER);
        lblChaoMung.setFont(new Font("Arial", Font.BOLD, 16));
        lblChaoMung.setForeground(Color.BLACK);

        // Label + Input
        JLabel lblTaiKhoan = new JLabel("Tài khoản");
        lblTaiKhoan.setFont(new Font("Arial", Font.PLAIN, 14));

        JTextField txtTaiKhoan = new JTextField("Enter your email");
        txtTaiKhoan.setBorder(new RoundedBorder(20)); // bo góc tròn 20px

        JLabel lblMatKhau = new JLabel("Mật khẩu");
        lblMatKhau.setFont(new Font("Arial", Font.PLAIN, 14));

        JPasswordField txtMatKhau = new JPasswordField("Password");
        txtMatKhau.setBorder(new RoundedBorder(20));

        // Checkbox + quên mật khẩu
        JCheckBox chkNhoMatKhau = new JCheckBox("Remember for 30 days");
        chkNhoMatKhau.setFont(new Font("Arial", Font.BOLD, 13));
        chkNhoMatKhau.setBackground(Color.WHITE);

        JLabel lblQuenMatKhau = new JLabel("Quên mật khẩu?");
        lblQuenMatKhau.setForeground(new Color(33, 150, 243));
        lblQuenMatKhau.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel panelOptions = new JPanel(new BorderLayout());
        panelOptions.setBackground(Color.WHITE);
        panelOptions.add(chkNhoMatKhau, BorderLayout.WEST);
        panelOptions.add(Box.createHorizontalStrut(30));
        panelOptions.add(lblQuenMatKhau, BorderLayout.EAST);

        // Button
        JButton btnDangNhap = new JButton("Đăng nhập");
        btnDangNhap.setFont(new Font("Poppins", Font.BOLD, 15));
        btnDangNhap.setForeground(Color.WHITE);
        btnDangNhap.setFocusPainted(false);
        btnDangNhap.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDangNhap.setPreferredSize(new Dimension(200, 40));
        btnDangNhap.setBackground(new Color(33, 150, 243));
        btnDangNhap.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btnDangNhap.setOpaque(true);

        btnDangNhap.addChangeListener(e -> {
            if (btnDangNhap.getModel().isRollover()) {
                btnDangNhap.setBackground(new Color(30, 136, 229));
            } else {
                btnDangNhap.setBackground(new Color(33, 150, 243));
            }
        });

        // Thêm vào form
        gbc.gridy = 0;
        panelPhai.add(lblTieuDe, gbc);
        gbc.gridy++;
        panelPhai.add(lblChaoMung, gbc);

        gbc.gridy++;
        panelPhai.add(lblTaiKhoan, gbc);
        gbc.gridy++;
        panelPhai.add(txtTaiKhoan, gbc);

        gbc.gridy++;
        panelPhai.add(lblMatKhau, gbc);
        gbc.gridy++;
        panelPhai.add(txtMatKhau, gbc);

        gbc.gridy++;
        panelPhai.add(panelOptions, gbc);
        gbc.gridy++;
        panelPhai.add(btnDangNhap, gbc);

        // Add vào frame
        add(panelTrai, BorderLayout.WEST);
        add(panelPhai, BorderLayout.CENTER);
    }

    // Class vẽ viền bo góc
    class RoundedBorder extends javax.swing.border.AbstractBorder {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(Color.LIGHT_GRAY); // màu viền
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(5, 10, 5, 10); // padding để text không dính viền
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = 10;
            insets.right = 10;
            insets.top = 5;
            insets.bottom = 5;
            return insets;
        }
    }
}
