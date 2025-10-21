package view;

import controller.Wifi_Controller;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * Wifi_Modal.java
 * - Chỉ chứa phần hiển thị. Mọi xử lý gọi Wifi_Controller.
 * - Tên biến, chú thích, label... bằng tiếng Việt.
 */
public class Wifi_Modal {
    private Stage stage;

    public Stage getStage() {
        return stage;
    }

    public Wifi_Modal() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Wifi");

        VBox container = new VBox(12);
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.TOP_CENTER);
        container.setStyle(
                "-fx-background-color: #E8F1FD; -fx-padding: 16; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label lblTieuDe = new Label("Wi-Fi đang kết nối");
        lblTieuDe.setFont(Font.font("System", FontWeight.BOLD, 25));
        lblTieuDe.setStyle(" -fx-text-fill: #2d3748");

        // Khung hiển thị tên SSID
        HBox khungTen = new HBox(10);
        khungTen.setAlignment(Pos.CENTER_LEFT);
        Label lblTenTitle = new Label("Tên (SSID): ");
        lblTenTitle
                .setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2d3748");
        Label lblTenValue = new Label();
        lblTenValue
                .setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2d3748");
        khungTen.getChildren().addAll(lblTenTitle, lblTenValue);

        // Khung hiển thị mật khẩu
        HBox khungMatKhau = new HBox(10);
        khungMatKhau.setAlignment(Pos.CENTER_LEFT);
        Label lblMKTitle = new Label("Mật khẩu: ");
        lblMKTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2d3748");
        Label lblMKValue = new Label();
        lblMKValue.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2d3748");
        khungMatKhau.getChildren().addAll(lblMKTitle, lblMKValue);

        // Khung QR
        VBox khungQR = new VBox(8);
        khungQR.setAlignment(Pos.CENTER);
        ImageView imageQr = new ImageView();
        imageQr.setFitWidth(200);
        imageQr.setFitHeight(200);
        khungQR.getChildren().add(imageQr);

        // Nút đóng / nhập thủ công
        HBox khungNut = new HBox(10);
        khungNut.setAlignment(Pos.CENTER);
        container.getStylesheets().add(getClass().getResource("/css/Button.css").toExternalForm());
        Button btnDong = new Button("Đóng");
        btnDong.getStyleClass().add("btn-huy");
        btnDong.setPrefHeight(30);
        btnDong.setPrefWidth(200);
        khungNut.getChildren().addAll(btnDong);

        Region khoangTrang = new Region();
        khoangTrang.setPrefHeight(30);
        VBox.setVgrow(khoangTrang, Priority.ALWAYS);

        container.getChildren().addAll(lblTieuDe, khungQR, khungTen, khungMatKhau, khoangTrang, khungNut);

        // --- Gọi controller để lấy SSID và mật khẩu ---
        Optional<String> optSsid = Wifi_Controller.laySSIDHienTai();
        if (optSsid.isPresent()) {
            String ssid = optSsid.get();
            lblTenValue.setText(ssid);

            Optional<String> optMatKhau = Wifi_Controller.layMatKhauTuSSID(ssid);
            if (optMatKhau.isPresent()) {
                String matKhau = optMatKhau.get();
                lblMKValue.setText(matKhau);

                // tạo QR
                Optional<javafx.scene.image.Image> optImg = Wifi_Controller.taoQRCodeWifi(ssid, matKhau, 500);
                optImg.ifPresent(imageQr::setImage);
            } else {
                // Không lấy được mật khẩu tự động
                lblMKValue.setText("[Không lấy được mật khẩu tự động]");
                // tạo QR dạng chỉ SSID (nếu muốn quét thì user cần nhập mật khẩu)
                Optional<javafx.scene.image.Image> optImg = Wifi_Controller.taoQRCodeWifi(ssid, "", 500);
                optImg.ifPresent(imageQr::setImage);
            }
        } else {
            lblTenValue.setText("[Không tìm thấy SSID đang kết nối]");
            lblMKValue.setText("[Không có mật khẩu]");
            // Không tạo QR
        }

        btnDong.setOnAction(evt -> stage.close());

        Scene sc = new Scene(container);
        stage.setScene(sc);
        stage.setWidth(480);
        stage.setHeight(500);
    }
}
