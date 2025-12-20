package view.Phong;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import model.PhieuDatPhong;
import utils.PhieuXacNhanDatPhong_PDFGenerator;

/**
 * Dialog hiển thị phiếu xác nhận đặt phòng
 * Sử dụng WebView để render HTML với khả năng in
 */
public class PhieuXacNhanDatPhong_GUI extends BorderPane {

    private WebView webView;
    private WebEngine webEngine;
    private PhieuDatPhong phieuDatPhong;

    /**
     * Constructor
     * @param phieuDatPhong Phiếu đặt phòng cần hiển thị
     */
    public PhieuXacNhanDatPhong_GUI(PhieuDatPhong phieuDatPhong) {
        this.phieuDatPhong = phieuDatPhong;
        init();
    }

    /**
     * Khởi tạo giao diện
     */
    private void init() {
        // Tạo WebView
        webView = new WebView();
        webEngine = webView.getEngine();
        
        // Load HTML content - không còn sử dụng WebView nữa
        // Chỉ dùng để tương thích, thực tế sẽ tạo PDF trực tiếp
        webEngine.loadContent("<html><body><h1>Đang tạo phiếu PDF...</h1></body></html>");
        
        // Set center
        setCenter(webView);
        
        // Create footer with buttons
        setBottom(createFooterButtons());
        
        // Set preferred size
        setPrefSize(900, 800);
    }

    /**
     * Tạo footer buttons
     */
    private HBox createFooterButtons() {
        HBox footer = new HBox(15);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(15, 20, 15, 20));
        footer.setStyle(
            "-fx-background-color: linear-gradient(to top, #ffffff, #f7f9fb);" +
            "-fx-border-color: #E1E6EC transparent transparent transparent;" +
            "-fx-border-width: 1 0 0 0;"
        );

        // Button In
        Button btnIn = createStyledButton("In phiếu", "#3498db", "#2980b9");
        btnIn.setOnAction(e -> printPhieu());

        // Button Lưu PDF (tùy chọn - có thể thêm sau)
        // Button btnLuuPDF = createStyledButton("Lưu PDF", "#27ae60", "#229954");
        // btnLuuPDF.setOnAction(e -> savePDF());

        // Button Đóng
        Button btnDong = createStyledButton("Đóng", "#95a5a6", "#7f8c8d");
        btnDong.setOnAction(e -> {
            Stage stage = (Stage) getScene().getWindow();
            stage.close();
        });

        footer.getChildren().addAll(btnIn, btnDong);
        return footer;
    }

    /**
     * Tạo styled button
     */
    private Button createStyledButton(String text, String color, String hoverColor) {
        Button button = new Button(text);
        button.setPrefSize(120, 35);
        
        String baseStyle = String.format(
            "-fx-background-color: %s;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;",
            color
        );
        
        String hoverStyle = String.format(
            "-fx-background-color: %s;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;",
            hoverColor
        );
        
        button.setStyle(baseStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
        
        return button;
    }

    /**
     * In phiếu
     */
    private void printPhieu() {
        PrinterJob printerJob = PrinterJob.createPrinterJob();
        
        if (printerJob != null && printerJob.showPrintDialog(getScene().getWindow())) {
            // Scale the webview to fit the page
            webView.getEngine().executeScript("document.body.style.transform = 'scale(0.9)';");
            webView.getEngine().executeScript("document.body.style.transformOrigin = '0 0';");
            
            boolean success = printerJob.printPage(webView);
            
            if (success) {
                printerJob.endJob();
                
                // Show success notification
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.INFORMATION
                );
                alert.setTitle("Thành công");
                alert.setHeaderText(null);
                alert.setContentText("In phiếu xác nhận thành công!");
                alert.showAndWait();
            } else {
                // Show error notification
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR
                );
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Không thể in phiếu. Vui lòng thử lại!");
                alert.showAndWait();
            }
            
            // Reset scale
            webView.getEngine().executeScript("document.body.style.transform = 'scale(1)';");
        }
    }

    /**
     * Hiển thị dialog
     */
    public void show() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Phiếu xác nhận đặt phòng - " + phieuDatPhong.getMaPhieuDatPhong());
        
        Scene scene = new Scene(this, 900, 800);
        stage.setScene(scene);
        stage.setResizable(true);
        
        // Center on screen
        stage.centerOnScreen();
        
        stage.showAndWait();
    }

    /**
     * Static method để hiển thị phiếu
     */
    public static void showPhieu(PhieuDatPhong phieuDatPhong) {
        PhieuXacNhanDatPhong_GUI gui = new PhieuXacNhanDatPhong_GUI(phieuDatPhong);
        gui.show();
    }
}
