package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Trang cài đặt - thiết kế đơn giản
 */
public class CaiDatHeThong_GUI extends BorderPane {

    private TextField txtTenKS, txtDiaChi, txtSDT, txtEmail;
    private ComboBox<String> cboMuiGio, cboNgonNgu, cboTienTe;
    private TextField txtSMTP, txtPort, txtEmailHT, txtMatKhau;

    public CaiDatHeThong_GUI() {
        setPadding(new Insets(8));
        setStyle("-fx-background-color: #f8f9fa;");

        VBox container = new VBox(12);
        container.setMaxWidth(920);
        container.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("⚙️ Cài đặt hệ thống");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1e293b; -fx-padding: 0 0 6 0;");

        container.getChildren().addAll(title, createHotelInfoCard(), createSystemCard(), createEmailCard(), createActionButtons());
        setCenter(container);
    }

    private VBox createHotelInfoCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e5e7eb; -fx-border-width: 1; -fx-border-radius: 10;");

        Label title = new Label("🏨 Thông tin khách sạn");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        HBox row1 = new HBox(10);
        txtTenKS = createField("Khách sạn Victorya");
        txtDiaChi = createField("123 Nguyễn Văn Linh, Q.7, TP.HCM");
        row1.getChildren().addAll(createFieldGroup("Tên khách sạn", txtTenKS), createFieldGroup("Địa chỉ", txtDiaChi));

        HBox row2 = new HBox(10);
        txtSDT = createField("028 1234 5678");
        txtEmail = createField("contact@victorya.com");
        row2.getChildren().addAll(createFieldGroup("Số điện thoại", txtSDT), createFieldGroup("Email liên hệ", txtEmail));

        card.getChildren().addAll(title, row1, row2);
        return card;
    }

    private VBox createSystemCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e5e7eb; -fx-border-width: 1; -fx-border-radius: 10;");

        Label title = new Label("🌐 Cài đặt hệ thống");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        HBox row1 = new HBox(10);
        
        cboMuiGio = new ComboBox<>();
        cboMuiGio.getItems().addAll("GMT+7 (Việt Nam)", "GMT+8 (Singapore)", "GMT+9 (Tokyo)");
        cboMuiGio.setValue("GMT+7 (Việt Nam)");
        cboMuiGio.setPrefHeight(34);
        cboMuiGio.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8;");

        cboNgonNgu = new ComboBox<>();
        cboNgonNgu.getItems().addAll("Tiếng Việt", "English", "中文");
        cboNgonNgu.setValue("Tiếng Việt");
        cboNgonNgu.setPrefHeight(34);
        cboNgonNgu.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8;");

        row1.getChildren().addAll(createComboGroup("Múi giờ", cboMuiGio), createComboGroup("Ngôn ngữ", cboNgonNgu));

        HBox row2 = new HBox(10);
        
        cboTienTe = new ComboBox<>();
        cboTienTe.getItems().addAll("VND (₫)", "USD ($)", "EUR (€)");
        cboTienTe.setValue("VND (₫)");
        cboTienTe.setPrefHeight(34);
        cboTienTe.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8;");

        VBox spacer = new VBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row2.getChildren().addAll(createComboGroup("Đơn vị tiền tệ", cboTienTe), spacer);

        card.getChildren().addAll(title, row1, row2);
        return card;
    }

    private VBox createEmailCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e5e7eb; -fx-border-width: 1; -fx-border-radius: 10;");

        Label title = new Label("📧 Cấu hình Email");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        HBox row1 = new HBox(10);
        txtSMTP = createField("smtp.gmail.com");
        txtPort = createField("587");
        row1.getChildren().addAll(createFieldGroup("SMTP Server", txtSMTP), createFieldGroup("Port", txtPort));

        HBox row2 = new HBox(10);
        txtEmailHT = createField("system@victorya.com");
        txtMatKhau = createField("••••••••");
        row2.getChildren().addAll(createFieldGroup("Email hệ thống", txtEmailHT), createFieldGroup("Mật khẩu", txtMatKhau));

        card.getChildren().addAll(title, row1, row2);
        return card;
    }

    private HBox createActionButtons() {
        HBox box = new HBox(12);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(6, 0, 0, 0));

        Button btnSave = new Button("💾 Lưu cấu hình");
        btnSave.setPrefWidth(160);
        btnSave.setPrefHeight(38);
        btnSave.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 8;");

        Button btnReset = new Button("🔄 Khôi phục mặc định");
        btnReset.setPrefWidth(160);
        btnReset.setPrefHeight(38);
        btnReset.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 8;");

        Button btnTest = new Button("📨 Test Email");
        btnTest.setPrefWidth(140);
        btnTest.setPrefHeight(38);
        btnTest.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 8;");

        box.getChildren().addAll(btnSave, btnReset, btnTest);
        return box;
    }

    private TextField createField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefHeight(34);
        field.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8; -fx-padding: 0 10;");
        return field;
    }

    private VBox createFieldGroup(String label, TextField field) {
        VBox group = new VBox(5);
        HBox.setHgrow(group, Priority.ALWAYS);

        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b; -fx-font-weight: 500;");

        group.getChildren().addAll(lbl, field);
        return group;
    }

    private VBox createComboGroup(String label, ComboBox<String> combo) {
        VBox group = new VBox(5);
        HBox.setHgrow(group, Priority.ALWAYS);

        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b; -fx-font-weight: 500;");

        group.getChildren().addAll(lbl, combo);
        return group;
    }
}

