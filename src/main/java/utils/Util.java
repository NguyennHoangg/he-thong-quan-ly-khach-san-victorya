package utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import org.mindrot.jbcrypt.BCrypt;

public class Util {
    // Cache SVG content thread-safe để tránh đọc file nhiều lần
    private static final Map<String, String> svgCache = new ConcurrentHashMap<>();
    
    /**
     * Đọc file SVG đơn giản và trả về đối tượng SVGPath.
     * 
     * @param filePath    Đường dẫn đến file SVG
     * @param fillColor   Màu nền (fill)
     * @param strokeColor Màu viền (stroke)
     * @return SVGPath có thể hiển thị trong JavaFX
     */
    public static SVGPath readSimpleSVG(String filePath, Color fillColor, Color strokeColor) {
        try {
            // Kiểm tra cache trước
            String pathData = svgCache.get(filePath);
            
            if (pathData == null) {
                // Chưa có trong cache, đọc file
                InputStream svgStream = Util.class.getResourceAsStream(filePath);
                if (svgStream == null) {
                    System.err.println("Cannot find SVG resource: " + filePath);
                    return null;
                }
                
                // Đọc từ InputStream
                String svgContent = new String(svgStream.readAllBytes());
                svgStream.close();

                // Tìm chuỗi d="..." trong nội dung SVG
                pathData = svgContent.split("d=\"")[1].split("\"")[0];
                
                // Lưu vào cache
                svgCache.put(filePath, pathData);
            }

            // Tạo SVGPath từ cached data
            SVGPath svg = new SVGPath();
            svg.setContent(pathData);
            svg.setFill(fillColor);
            svg.setStroke(strokeColor);
            svg.setStrokeWidth(1.5);

            return svg;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Đọc và trả về hình ảnh từ đường dẫn tài nguyên.
     * Nếu đọc ảnh kiểu JavaFX thất bại, sẽ thử đọc bằng Swing.
     *
     * @param path     Đường dẫn tương đối trong resources (classpath)
     * @param fallback Đường dẫn dự phòng trong hệ thống file (ví dụ:
     *                 "src/main/resources/img/Logo.png")
     * @return {@link javafx.scene.image.Image} nếu đọc được bằng JavaFX,
     *         hoặc {@link java.awt.Image} nếu chỉ đọc được bằng Swing,
     *         hoặc {@code null} nếu không đọc được.
     */
    public static Object readImage(String path, String fallback) {
        // Thử đọc ảnh kiểu JavaFX
        try {
            InputStream stream = Util.class.getResourceAsStream(path);
            return new javafx.scene.image.Image(stream);
        } catch (Exception ignored) {
        }

        // Thử đọc ảnh kiểu Swing nếu JavaFX thất bại
        try {
            URL imgUrl = Util.class.getResource(path);
            if (imgUrl != null) {
                return ImageIO.read(imgUrl);
            } else {
                File file = new File(fallback);
                if (file.exists()) {
                    return ImageIO.read(file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

     /**
         * Tạo và cấu hình một nút cho sidebar (có thể chỉ icon hoặc icon + text).
         * Mặc định chỉ đánh dấu nút "Trang chủ" là active; các nút khác sẽ không có class "active"
         * cho đến khi người dùng click vào chúng.
         *
         * @param text        Văn bản hiển thị trên nút (có thể là null để chỉ hiện icon)
         * @param url         Đường dẫn resource tới file SVG của icon
         * @param screenWidth Chiều ngang màn hình, dùng để tính kích thước tương đối
         * @return Button đã cấu hình sẵn icon, kích thước và kiểu hiển thị
         */
        public static Button createSidebarButton(String text, String url, double screenWidth) {
                Button btn = new Button(text, Util.readSimpleSVG(url, null, Color.web("#5D6679")));

                // Kích thước theo tỉ lệ màn hình (điều chỉnh để phù hợp với sidebar)
                btn.setPrefWidth(screenWidth * 0.16);
                btn.setPrefHeight(44);

                // Padding bên trong, khoảng cách giữa icon và text, căn trái
                btn.setPadding(new Insets(5, 5, 5, 5));
                btn.setGraphicTextGap(12);
                btn.setAlignment(Pos.CENTER_LEFT);
                btn.setContentDisplay(javafx.scene.control.ContentDisplay.LEFT);

                // Chỉ thêm class "button" mặc định. Class "active" chỉ thêm cho nút "Trang chủ" ban đầu
                btn.getStyleClass().add("button");
                if (text != null && "Trang chủ".equalsIgnoreCase(text.trim())) {
                        btn.getStyleClass().add("active");
                }

                btn.setFocusTraversable(false);

                // Khi click: bỏ active của các nút cùng nhóm rồi đánh dấu nút này là active
                btn.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, ev -> {
                        javafx.scene.Parent parent = btn.getParent();
                        if (parent instanceof javafx.scene.layout.Pane) {
                                javafx.scene.layout.Pane pane = (javafx.scene.layout.Pane) parent;
                                for (javafx.scene.Node node : pane.getChildren()) {
                                        if (node instanceof Button) {
                                                ((Button) node).getStyleClass().removeAll(java.util.Collections.singleton("active"));
                                        }
                                }
                        } else {
                                // Fallback: tìm theo scene (các nút có class "button")
                                if (btn.getScene() != null && btn.getScene().getRoot() != null) {
                                        btn.getScene().getRoot().lookupAll(".button").forEach(n -> {
                                                if (n instanceof Button) ((Button) n).getStyleClass().removeAll(java.util.Collections.singleton("active"));
                                        });
                                }
                        }
                        if (!btn.getStyleClass().contains("active")) {
                                btn.getStyleClass().add("active");
                        }
                });

                return btn;
        }
                /**
         * Hash mật khẩu sử dụng BCrypt
         * 
         * @param plainPassword Mật khẩu dạng plain text
         * @return Mật khẩu đã được hash
         */
        public static String hashPassword(String plainPassword) {
                return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        }

        /**
         * Kiểm tra mật khẩu có khớp với hash không
         * 
         * @param plainPassword   Mật khẩu dạng plain text cần kiểm tra
         * @param hashedPassword  Mật khẩu đã được hash từ database
         * @return true nếu mật khẩu đúng, false nếu sai
         */
        public static boolean checkPassword(String plainPassword, String hashedPassword) {
                try {
                        return BCrypt.checkpw(plainPassword, hashedPassword);
                } catch (Exception e) {
                        return false;
                }
        }

}
