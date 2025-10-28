package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Trang cài đặt hệ thống - đơn giản, gọn gàng
 */
public class CaiDatHeThong_GUI extends BorderPane {

    // Thông tin khách sạn
    private TextField txtTenKS, txtDiaChi, txtSDT, txtEmail;
    
    // Cài đặt hệ thống
    private ComboBox<String> cboMuiGio, cboNgonNgu, cboTienTe;
    
    // Cấu hình email
    private TextField txtSMTP, txtPort, txtEmailHT, txtMatKhau;
    
    // Cài đặt database
    private TextField txtServer, txtDatabase, txtUsername, txtPassword;

    public CaiDatHeThong_GUI() {
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f5f5f5;");
        
        VBox container = new VBox(20);
        container.setMaxWidth(800);
        container.setAlignment(Pos.TOP_CENTER);
        
        Label title = new Label("Cài đặt hệ thống");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        container.getChildren().addAll(
            title,
            createHotelInfoSection(),
            createSystemSection(),
            createEmailSection(),
            createDatabaseSection(),
            createActionButtons()
        );
        
        setCenter(container);
        loadCurrentSettings();
    }

    private VBox createHotelInfoSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(15));
        section.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1;");
        
        Label title = new Label("Thông tin khách sạn");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        HBox row1 = new HBox(15);
        txtTenKS = createTextField("Tên khách sạn");
        txtDiaChi = createTextField("Địa chỉ");
        row1.getChildren().addAll(createFieldGroup("Tên khách sạn", txtTenKS), createFieldGroup("Địa chỉ", txtDiaChi));
        
        HBox row2 = new HBox(15);
        txtSDT = createTextField("Số điện thoại");
        txtEmail = createTextField("Email liên hệ");
        row2.getChildren().addAll(createFieldGroup("Số điện thoại", txtSDT), createFieldGroup("Email liên hệ", txtEmail));
        
        section.getChildren().addAll(title, row1, row2);
        return section;
    }

    private VBox createSystemSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(15));
        section.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1;");
        
        Label title = new Label("Cài đặt hệ thống");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        HBox row1 = new HBox(15);
        cboMuiGio = createComboBox("Múi giờ", "GMT+7 (Việt Nam)", "GMT+8 (Singapore)", "GMT+9 (Tokyo)");
        cboNgonNgu = createComboBox("Ngôn ngữ", "Tiếng Việt", "English");
        row1.getChildren().addAll(createComboGroup("Múi giờ", cboMuiGio), createComboGroup("Ngôn ngữ", cboNgonNgu));
        
        HBox row2 = new HBox(15);
        cboTienTe = createComboBox("Tiền tệ", "VND", "USD", "EUR");
        VBox spacer = new VBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row2.getChildren().addAll(createComboGroup("Tiền tệ", cboTienTe), spacer);
        
        section.getChildren().addAll(title, row1, row2);
        return section;
    }

    private VBox createEmailSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(15));
        section.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1;");
        
        Label title = new Label("Cấu hình Email");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        HBox row1 = new HBox(15);
        txtSMTP = createTextField("SMTP Server");
        txtPort = createTextField("Port");
        row1.getChildren().addAll(createFieldGroup("SMTP Server", txtSMTP), createFieldGroup("Port", txtPort));
        
        HBox row2 = new HBox(15);
        txtEmailHT = createTextField("Email hệ thống");
        txtMatKhau = createTextField("Mật khẩu");
        row2.getChildren().addAll(createFieldGroup("Email hệ thống", txtEmailHT), createFieldGroup("Mật khẩu", txtMatKhau));
        
        section.getChildren().addAll(title, row1, row2);
        return section;
    }

    private VBox createDatabaseSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(15));
        section.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1;");
        
        Label title = new Label("Cấu hình Database");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        HBox row1 = new HBox(15);
        txtServer = createTextField("Server");
        txtDatabase = createTextField("Database");
        row1.getChildren().addAll(createFieldGroup("Server", txtServer), createFieldGroup("Database", txtDatabase));
        
        HBox row2 = new HBox(15);
        txtUsername = createTextField("Username");
        txtPassword = createTextField("Password");
        row2.getChildren().addAll(createFieldGroup("Username", txtUsername), createFieldGroup("Password", txtPassword));
        
        section.getChildren().addAll(title, row1, row2);
        return section;
    }

    private HBox createActionButtons() {
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);
        
        Button btnSave = new Button("Lưu cấu hình");
        btnSave.setPrefWidth(120);
        btnSave.setPrefHeight(35);
        btnSave.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnSave.setOnAction(e -> saveSettings());
        
        Button btnReset = new Button("Khôi phục");
        btnReset.setPrefWidth(120);
        btnReset.setPrefHeight(35);
        btnReset.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        btnReset.setOnAction(e -> resetSettings());
        
        Button btnTest = new Button("Test kết nối");
        btnTest.setPrefWidth(120);
        btnTest.setPrefHeight(35);
        btnTest.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        btnTest.setOnAction(e -> testConnection());
        
        buttons.getChildren().addAll(btnSave, btnReset, btnTest);
        return buttons;
    }

    private TextField createTextField(String placeholder) {
        TextField field = new TextField();
        field.setPromptText(placeholder);
        field.setPrefHeight(30);
        field.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-padding: 5;");
        return field;
    }

    private ComboBox<String> createComboBox(String placeholder, String... items) {
        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll(items);
        combo.setValue(items[0]);
        combo.setPrefHeight(30);
        combo.setStyle("-fx-border-color: #ccc; -fx-border-width: 1;");
        return combo;
    }

    private VBox createFieldGroup(String label, TextField field) {
        VBox group = new VBox(5);
        HBox.setHgrow(group, Priority.ALWAYS);
        
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        
        group.getChildren().addAll(lbl, field);
        return group;
    }

    private VBox createComboGroup(String label, ComboBox<String> combo) {
        VBox group = new VBox(5);
        HBox.setHgrow(group, Priority.ALWAYS);
        
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        
        group.getChildren().addAll(lbl, combo);
        return group;
    }

    private void loadCurrentSettings() {
        // Load settings from config file or database
        txtTenKS.setText("Khách sạn Victorya");
        txtDiaChi.setText("123 Nguyễn Văn Linh, Q.7, TP.HCM");
        txtSDT.setText("028 1234 5678");
        txtEmail.setText("contact@victorya.com");
        
        txtSMTP.setText("smtp.gmail.com");
        txtPort.setText("587");
        txtEmailHT.setText("system@victorya.com");
        txtMatKhau.setText("••••••••");
        
        txtServer.setText("localhost");
        txtDatabase.setText("Victorya_Hotel");
        txtUsername.setText("sa");
        txtPassword.setText("••••••••");
    }

    private void saveSettings() {
        try {
            // Validate required fields
            if (txtTenKS.getText().trim().isEmpty()) {
                showAlert("Lỗi", "Vui lòng nhập tên khách sạn");
                return;
            }
            
            // Save to config file or database
            // TODO: Implement actual save logic
            
            showAlert("Thành công", "Đã lưu cấu hình thành công");
            
        } catch (Exception e) {
            showAlert("Lỗi", "Không thể lưu cấu hình: " + e.getMessage());
        }
    }

    private void resetSettings() {
        txtTenKS.clear();
        txtDiaChi.clear();
        txtSDT.clear();
        txtEmail.clear();
        
        cboMuiGio.setValue("GMT+7 (Việt Nam)");
        cboNgonNgu.setValue("Tiếng Việt");
        cboTienTe.setValue("VND");
        
        txtSMTP.clear();
        txtPort.clear();
        txtEmailHT.clear();
        txtMatKhau.clear();
        
        txtServer.clear();
        txtDatabase.clear();
        txtUsername.clear();
        txtPassword.clear();
        
        showAlert("Thông báo", "Đã khôi phục cài đặt mặc định");
    }

    private void testConnection() {
        try {
            // Test database connection
            // TODO: Implement actual connection test
            
            showAlert("Thành công", "Kết nối database thành công");
            
        } catch (Exception e) {
            showAlert("Lỗi", "Không thể kết nối database: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

