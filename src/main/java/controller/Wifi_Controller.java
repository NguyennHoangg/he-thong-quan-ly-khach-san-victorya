package controller;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class Wifi_Controller {

    // Hàm lấy SSID hiện tại
    // dùng static ở đấy do hàm chỉ đọc thông tin nên không cần đôi tượng để gọi hàm
    // nếu bỏ thì phải dùng đối tượng WifiUtils wifi = new WifiUtils();
    public static String laySSIDHienTai() {
        try {
            // Khởi chạy trên cmd để lấy ssid hay còn gọi là tên wifi -> vd SSID : ThanhTung
            Process p = Runtime.getRuntime().exec(new String[] { "cmd", "/c", "netsh wlan show interfaces" });
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"))) {
                String hang;
                while ((hang = br.readLine()) != null) {
                    hang = hang.trim(); // bỏ các khoảng trắng
                    if (hang.toLowerCase().startsWith("ssid")) {
                        String[] parts = hang.split(":", 2); // Mảng chứa 2 phần tử ssid và tên
                        if (parts.length == 2) {
                            String ssid = parts[1].trim(); // Lấy tên
                            if (!ssid.isEmpty() && !ssid.equals("0")) {
                                return ssid;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Hàm lấy mật khẩu từ SSID
    public static String layMatKhauTuSSID(String ssid) {
        if (ssid == null || ssid.isEmpty())
            return null;
        try {
            String cmd = String.format("netsh wlan show profile name=\"%s\" key=clear", ssid);
            Process p = Runtime.getRuntime().exec(new String[] { "cmd", "/c", cmd });
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"))) {
                String hang;
                while ((hang = br.readLine()) != null) {
                    hang = hang.trim();
                    if (hang.toLowerCase().startsWith("key content")) {
                        String[] parts = hang.split(":", 2);
                        if (parts.length == 2)
                            return parts[1].trim();
                    }
                    if (hang.toLowerCase().contains("nội dung khóa")) {
                        String[] parts = hang.split(":", 2);
                        if (parts.length == 2)
                            return parts[1].trim();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String layThongTinLAN() {
        StringBuilder thongTin = new StringBuilder();
        try {
            // Lấy danh sách các card mạng
            Enumeration<NetworkInterface> danhSachCard = NetworkInterface.getNetworkInterfaces();
            while (danhSachCard.hasMoreElements()) {
                NetworkInterface card = danhSachCard.nextElement();

                // Bỏ card ảo hoặc không hoạt động
                if (card.isLoopback() || !card.isUp())
                    continue;

                thongTin.append("Tên card mạng: ").append(card.getDisplayName()).append("\n");

                // Lấy địa chỉ IPv4
                Enumeration<InetAddress> danhSachIP = card.getInetAddresses();
                while (danhSachIP.hasMoreElements()) {
                    InetAddress ip = danhSachIP.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) { // chỉ IPv4
                        thongTin.append("Địa chỉ IP: ").append(ip.getHostAddress()).append("\n");
                    }
                }

                thongTin.append("-----------------------------\n");
            }
        } catch (SocketException e) {
            thongTin.append("Không thể lấy thông tin mạng LAN.\n");
        }

        if (thongTin.length() == 0) {
            thongTin.append("Không phát hiện kết nối LAN nào.\n");
        }

        return thongTin.toString();
    }

    public static Image taoQRCodeWifi(String ssid, String matKhau, int kichThuoc) {
        if (ssid == null || ssid.isEmpty())
            return null;

        try {
            String loaiBaoMat = "WPA"; // loại bảo mật mặc định
            String qrText;

            if (matKhau == null || matKhau.isEmpty()) {
                qrText = String.format("WIFI:T:nopass;S:%s;;", ssid);
            } else {
                qrText = String.format("WIFI:T:%s;S:%s;P:%s;;", loaiBaoMat, ssid, matKhau);
            }

            QRCodeWriter qrWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrWriter.encode(qrText, BarcodeFormat.QR_CODE, kichThuoc, kichThuoc);
            BufferedImage bf = MatrixToImageWriter.toBufferedImage(bitMatrix);

            return SwingFXUtils.toFXImage(bf, null);

        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String thongDiepLoiLayMatKhau() {
        return "Không thể lấy mật khẩu Wi-Fi. Hãy đảm bảo bạn chạy ứng dụng bằng quyền Quản trị viên (Administrator).";
    }
}
