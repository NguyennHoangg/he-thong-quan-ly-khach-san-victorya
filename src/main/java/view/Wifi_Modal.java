package view;

import controller.Wifi_Controller;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Wifi_Modal {
    private Stage stage;

    public Stage getStage() {
        return stage;
    }

    public Wifi_Modal() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Wi-Fi");

        VBox container = new VBox(12);
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.TOP_CENTER);
        container.setStyle(
                "-fx-background-color: #E8F1FD; -fx-padding: 16; -fx-border-radius: 8; -fx-background-radius: 8;");
        container.getStylesheets().add(getClass().getResource("/css/Button.css").toExternalForm());
        Label lblTieuDe = new Label("Wi-Fi đang kết nối");
        lblTieuDe.setFont(Font.font("System", FontWeight.BOLD, 25));
        lblTieuDe.setStyle("-fx-text-fill: #2d3748");

        // Khung hiển thị SSID
        HBox khungTen = new HBox(10);
        khungTen.setAlignment(Pos.CENTER_LEFT);
        Label lblTenTitle = new Label("Tên (SSID): ");
        lblTenTitle.setStyle("-fx-text-fill: #2d3748; -fx-font-weight: bold; -fx-font-size: 16px;");
        Label lblTenValue = new Label();
        lblTenValue.setStyle("-fx-text-fill: #2d3748; -fx-font-weight: bold; -fx-font-size: 16px;");
        khungTen.getChildren().addAll(lblTenTitle, lblTenValue);

        // Khung hiển thị mật khẩu
        HBox khungMatKhau = new HBox(10);
        khungMatKhau.setAlignment(Pos.CENTER_LEFT);
        Label lblMKTitle = new Label("Mật khẩu: ");
        lblMKTitle.setStyle("-fx-text-fill: #2d3748; -fx-font-weight: bold; -fx-font-size: 16px;");
        Label lblMKValue = new Label();
        lblMKValue.setStyle("-fx-text-fill: #2d3748; -fx-font-weight: bold; -fx-font-size: 16px;");
        khungMatKhau.getChildren().addAll(lblMKTitle, lblMKValue);

        // Khung QR
        VBox khungQR = new VBox(8);
        khungQR.setAlignment(Pos.CENTER);
        ImageView imageQr = new ImageView();
        imageQr.setFitWidth(200);
        imageQr.setFitHeight(200);
        khungQR.getChildren().add(imageQr);

        HBox khungNut = new HBox(10);
        khungNut.setAlignment(Pos.CENTER);
        Button btnXacNhan = new Button("Xác nhận");
        btnXacNhan.setPrefSize(200, 30);
        btnXacNhan.getStyleClass().add("btn");
        khungNut.getChildren().add(btnXacNhan);

        Region khoangTrang = new Region();
        khoangTrang.setPrefHeight(30);
        VBox.setVgrow(khoangTrang, Priority.ALWAYS);

        container.getChildren().addAll(lblTieuDe, khungQR, khungTen, khungMatKhau, khoangTrang, khungNut);

        // --- Lấy SSID và mật khẩu trực tiếp từ controller ---
        String ssid = Wifi_Controller.laySSIDHienTai();
        if (ssid != null) {
            lblTenValue.setText(ssid);

            String matKhau = Wifi_Controller.layMatKhauTuSSID(ssid);
            if (matKhau != null && !matKhau.isEmpty()) {
                lblMKValue.setText(matKhau);
                Image qr = Wifi_Controller.taoQRCodeWifi(ssid, matKhau, 500);
                if (qr != null)
                    imageQr.setImage(qr);
            } else {
                lblMKValue.setText("[Không lấy được mật khẩu tự động]");
                Image qr = Wifi_Controller.taoQRCodeWifi(ssid, "", 500);
                if (qr != null)
                    imageQr.setImage(qr);
            }
        } else {
            lblTenValue.setText("[Không tìm thấy SSID]");
            lblMKValue.setText("[Không có mật khẩu]");
        }

        btnXacNhan.setOnAction(evt -> stage.close());

        Scene sc = new Scene(container, 480, 500);
        stage.setScene(sc);
    }
}
