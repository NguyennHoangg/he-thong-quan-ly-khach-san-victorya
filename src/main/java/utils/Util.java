package utils;

import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import java.net.URL;

public class Util {
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
            URL svgUrl = Util.class.getResource(filePath);
            if (svgUrl == null) {
                return null;
            }
            String svgContent = new String(Files.readAllBytes(Paths.get(svgUrl.toURI())));

            // Tìm chuỗi d="..." trong nội dung SVG
            String pathData = svgContent.split("d=\"")[1].split("\"")[0];

            // Tạo SVGPath
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
}
