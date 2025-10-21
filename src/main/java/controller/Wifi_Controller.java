package controller;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Optional;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 * Wifi_Controller.java
 * - Chứa toàn bộ hàm xử lý (theo yêu cầu): lấy SSID hiện tại, lấy mật khẩu (nỗ
 * lực theo OS),
 * tạo QR code cho WiFi (chuẩn: WIFI:T:WPA;S:<SSID>;P:<PASSWORD>;;)
 *
 * Lưu ý:
 * - Việc lấy mật khẩu phụ thuộc OS và quyền. Nếu không có quyền, hàm trả về
 * Optional.empty()
 * - Để tạo QR cần thư viện ZXing (core + javase). Thêm dependency vào project.
 *
 * Tên phương thức và chú thích bằng tiếng Việt theo yêu cầu.
 */
public class Wifi_Controller {

    /**
     * Lấy tên WiFi (SSID) đang kết nối trên thiết bị (nỗ lực với nhiều OS).
     * 
     * @return Optional chứa SSID nếu tìm được, Optional.empty() nếu không tìm được.
     */
    public static Optional<String> laySSIDHienTai() {
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        try {
            if (os.contains("win")) {
                // Windows: sử dụng netsh wlan show interfaces
                Process p = Runtime.getRuntime().exec(new String[] { "cmd", "/c", "netsh wlan show interfaces" });
                try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        if (line.toLowerCase().startsWith("ssid")) {
                            // Các hệ thống tiếng Việt/Anh khác nhau, nên xử lý kỹ
                            // ví dụ: "SSID : MyWifi"
                            String[] parts = line.split(":", 2);
                            if (parts.length == 2) {
                                String ssid = parts[1].trim();
                                if (!ssid.isEmpty() && !ssid.equals("0")) {
                                    return Optional.of(ssid);
                                }
                            }
                        }
                    }
                }
            } else if (os.contains("mac")) {
                // macOS: thử airport / networksetup
                // Thử dùng:
                // /System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport
                // -I
                String[] cmd1 = {
                        "/System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport",
                        "-I" };
                Process p1 = Runtime.getRuntime().exec(cmd1);
                try (BufferedReader br = new BufferedReader(new InputStreamReader(p1.getInputStream(), "UTF-8"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        if (line.startsWith("SSID:")) {
                            String[] parts = line.split("SSID:\\s*", 2);
                            if (parts.length == 2)
                                return Optional.of(parts[1].trim());
                        }
                    }
                } catch (Exception ignored) {
                }
                // fallback: networksetup -getairportnetwork en0
                try {
                    Process p2 = Runtime.getRuntime()
                            .exec(new String[] { "bash", "-c", "networksetup -getairportnetwork en0" });
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(p2.getInputStream(), "UTF-8"))) {
                        String line = br.readLine();
                        if (line != null) {
                            // Ví dụ: "Current Wi-Fi Network: MyWifi"
                            int idx = line.indexOf(":");
                            if (idx >= 0 && idx + 1 < line.length()) {
                                String ssid = line.substring(idx + 1).trim();
                                if (!ssid.isEmpty())
                                    return Optional.of(ssid);
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            } else {
                // Linux (nhiều distro): thử nmcli
                Process p = Runtime.getRuntime().exec(
                        new String[] { "bash", "-c", "nmcli -t -f active,ssid dev wifi | egrep '^yes' | cut -d: -f2" });
                try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"))) {
                    String ssid = br.readLine();
                    if (ssid != null && !ssid.trim().isEmpty()) {
                        return Optional.of(ssid.trim());
                    }
                } catch (Exception ignored) {
                }
                // fallback: iwgetid -r
                try {
                    Process p2 = Runtime.getRuntime().exec(new String[] { "bash", "-c", "iwgetid -r" });
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(p2.getInputStream(), "UTF-8"))) {
                        String ssid = br.readLine();
                        if (ssid != null && !ssid.trim().isEmpty()) {
                            return Optional.of(ssid.trim());
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
            // Không throw, trả Optional.empty()
        }
        return Optional.empty();
    }

    /**
     * Cố gắng lấy mật khẩu cho SSID đã biết.
     * Phương thức này chỉ "nỗ lực" — có thể thất bại do quyền hoặc OS.
     * 
     * @param ssid tên wifi
     * @return Optional chứa mật khẩu nếu lấy được, Optional.empty() nếu không.
     */
    public static Optional<String> layMatKhauTuSSID(String ssid) {
        if (ssid == null || ssid.isEmpty())
            return Optional.empty();
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        try {
            if (os.contains("win")) {
                // Windows: netsh wlan show profile name="SSID" key=clear
                String cmd = String.format("netsh wlan show profile name=\"%s\" key=clear", ssid);
                Process p = Runtime.getRuntime().exec(new String[] { "cmd", "/c", cmd });
                try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        // Tìm dòng "Key Content : password"
                        if (line.toLowerCase().startsWith("key content")) {
                            String[] parts = line.split(":", 2);
                            if (parts.length == 2)
                                return Optional.of(parts[1].trim());
                        }
                        // Một số bản windows tiếng Việt có thể là "Nội dung khóa"
                        if (line.toLowerCase().contains("nội dung khóa")) {
                            String[] parts = line.split(":", 2);
                            if (parts.length == 2)
                                return Optional.of(parts[1].trim());
                        }
                    }
                }
            } else if (os.contains("mac")) {
                // macOS: security find-generic-password -D "AirPort network password" -a "SSID"
                // -gw
                String[] cmd = { "bash", "-lc", String
                        .format("security find-generic-password -D \"AirPort network password\" -a \"%s\" -gw", ssid) };
                Process p = Runtime.getRuntime().exec(cmd);
                try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream(), "UTF-8"))) {
                    // note: 'security -gw' prints password to stderr
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    String result = sb.toString().trim();
                    if (!result.isEmpty()) {
                        // Thường có dạng: "password: "mypw""
                        result = result.replaceAll("^password:\\s*\"", "").replaceAll("\"$", "");
                        return Optional.of(result);
                    }
                }
            } else {
                // Linux: không có chuẩn chung — thử đọc NetworkManager connection (thường cần
                // root)
                // Thử dùng nmcli to show password (nhiều distro không cho)
                String[] cmd = { "bash", "-lc",
                        String.format("nmcli -s -g 802-11-wireless-security.psk connection show \"%s\"", ssid) };
                Process p = Runtime.getRuntime().exec(cmd);
                try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"))) {
                    String line = br.readLine();
                    if (line != null && !line.trim().isEmpty()) {
                        return Optional.of(line.trim());
                    }
                } catch (Exception ignored) {
                }
                // Fallbacks sẽ rất khó — thường yêu cầu đọc file ở
                // /etc/NetworkManager/system-connections (root)
            }
        } catch (Exception e) {
            // swallow và trả empty
        }
        return Optional.empty();
    }

    /**
     * Tạo QR code dạng Image (JavaFX Image) cho wifi.
     * Chuẩn nội dung: WIFI:T:WPA;S:<SSID>;P:<PASSWORD>;; (T có thể là WPA, WEP hoặc
     * nopass)
     * 
     * @param ssid      tên wifi
     * @param matKhau   mật khẩu (null hoặc empty => sử dụng "nopass")
     * @param kichThuoc chiều rộng/chiều cao ảnh QR (px)
     * @return Optional<Image> chứa Image nếu tạo được, Optional.empty() nếu lỗi
     */
    public static Optional<Image> taoQRCodeWifi(String ssid, String matKhau, int kichThuoc) {
        if (ssid == null || ssid.isEmpty())
            return Optional.empty();
        try {
            String loaiBaoMat = "WPA"; // mặc định assume WPA — không thể tự động biết kiểu
            String passPart;
            if (matKhau == null || matKhau.isEmpty()) {
                loaiBaoMat = "nopass";
                passPart = "";
            } else {
                passPart = matKhau;
            }
            // escape dấu ; hoặc , hoặc : theo chuẩn (ở đây đơn giản thay \ )
            String safeSsid = ssid.replace("\\", "\\\\").replace(";", "\\;").replace("\"", "\\\"");
            String safePass = passPart.replace("\\", "\\\\").replace(";", "\\;").replace("\"", "\\\"");

            String qrText;
            if ("nopass".equals(loaiBaoMat)) {
                qrText = String.format("WIFI:T:nopass;S:%s;;", safeSsid);
            } else {
                qrText = String.format("WIFI:T:%s;S:%s;P:%s;;", loaiBaoMat, safeSsid, safePass);
            }

            QRCodeWriter qrWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrWriter.encode(qrText, BarcodeFormat.QR_CODE, kichThuoc, kichThuoc);
            BufferedImage bf = MatrixToImageWriter.toBufferedImage(bitMatrix);
            Image fxImage = SwingFXUtils.toFXImage(bf, null);
            return Optional.of(fxImage);
        } catch (WriterException we) {
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Một hàm tiện ích để trả lời lỗi thân thiện khi không lấy được mật khẩu.
     * (Controller giữ logic, View chỉ gọi)
     */
    public static String thongDiepLoiLayMatKhau() {
        return "Không thể lấy mật khẩu tự động — có thể do quyền hệ thống hoặc OS không cho phép. "
                + "Vui lòng cấp quyền hoặc nhập mật khẩu thủ công.";
    }
}
