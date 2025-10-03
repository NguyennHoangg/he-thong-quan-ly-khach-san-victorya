package iuh.fit.se.group13.view;

import javax.swing.*;
import java.awt.*;

public class QuenMatKhau_GUI extends JFrame {
    public QuenMatKhau_GUI() {
        setTitle("Quên Mật Khẩu?");
        setSize(1920, 1080);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel pnlTrai = new JPanel();
        pnlTrai.setBackground(Color.WHITE);
        pnlTrai.setLayout(new BoxLayout(pnlTrai, BoxLayout.Y_AXIS));
        pnlTrai.setBorder(BorderFactory.createEmptyBorder(120, 200, 0, 0)); // padding top/left

        ImageIcon bieuTuongQuayLai = new ImageIcon("src/img/chevron_back.png");
        Image anhQuayLai = bieuTuongQuayLai.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        bieuTuongQuayLai = new ImageIcon(anhQuayLai);

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

        pnlTrai.add(Box.createVerticalStrut(220));
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

        ImageIcon bieuTuongHinh = new ImageIcon("src/img/quenMatKhau.png");
        Image anhQuenMatKhau = bieuTuongHinh.getImage().getScaledInstance(614, 845, Image.SCALE_SMOOTH);
        ImageIcon bieuTuongHinhDaChinh = new ImageIcon(anhQuenMatKhau);

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
    }
}
