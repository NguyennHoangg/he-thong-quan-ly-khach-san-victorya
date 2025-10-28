package view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Panel quên mật khẩu - 3 bước: Email -> OTP -> Reset Password
 */
public class QuenMatKhau_GUI extends Application {

    private TextField txtEmail, txtOTP, txtNewPassword, txtConfirmPassword;
    private Label lblEmailResult, lblOTPResult, lblPasswordResult;
    private Button btnTiepTuc, btnXacNhanOTP, btnDatMatKhau, btnQuayLai;
    private VBox step1Container, step2Container, step3Container;
    private String currentEmail = "";
    private String generatedOTP = "";

    @Override
    public void start(Stage primaryStage) {
        // Kích thước panel
        final double PANEL_W = 1200;
        final double PANEL_H = 800;

        // Tạo StackPane gốc
        StackPane base = new StackPane();
        base.setStyle("-fx-background-color: #f5f5f5;");

        // Panel chính - màu trắng, bo góc
        StackPane mainPanel = new StackPane();
        mainPanel.setPrefSize(PANEL_W, PANEL_H);
        mainPanel.setMinSize(PANEL_W, PANEL_H);
        
        BackgroundFill panelBg = new BackgroundFill(
            Color.WHITE,
            new CornerRadii(20, 20, 20, 20, false),
            Insets.EMPTY);
        mainPanel.setBackground(new Background(panelBg));
        
        // Thêm shadow effect
        mainPanel.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 20, 0, 0, 0);");

        // Layout chính: 2 cột
        HBox mainLayout = new HBox();
        mainLayout.setPrefSize(PANEL_W, PANEL_H);

        // Container chính cho các bước
        VBox mainContent = new VBox();
        mainContent.setPrefWidth(PANEL_W);
        mainContent.setPrefHeight(PANEL_H);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setPadding(new Insets(40));

        // Nút quay lại
        btnQuayLai = new Button("← Quay lại");
        btnQuayLai.setStyle("-fx-background-color: transparent; -fx-text-fill: #666666; -fx-font-size: 14px; -fx-underline: true;");
        btnQuayLai.setOnAction(e -> {
            primaryStage.close();
            new TrangDangNhap().start(new Stage());
        });

        // Tạo các bước
        step1Container = createStep1Container();
        step2Container = createStep2Container();
        step3Container = createStep3Container();
        
        // Ẩn bước 2 và 3 ban đầu
        step2Container.setVisible(false);
        step3Container.setVisible(false);

        // Thêm vào container chính
        mainContent.getChildren().addAll(btnQuayLai, step1Container, step2Container, step3Container);

        // Thêm vào layout chính
        mainLayout.getChildren().add(mainContent);

        // Thêm vào panel chính
        mainPanel.getChildren().add(mainLayout);
        mainPanel.setAlignment(Pos.CENTER);

        // Thêm panel vào base
        base.getChildren().add(mainPanel);
        base.setAlignment(Pos.CENTER);

        // Tạo scene và stage
        Scene scene = new Scene(base, PANEL_W, PANEL_H);
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("Quên mật khẩu - Victorya");
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private VBox createStep1Container() {
        VBox container = new VBox(25);
        container.setAlignment(Pos.CENTER);
        container.setMaxWidth(500);

        // Tiêu đề
        Label titleLabel = new Label("Quên mật khẩu?");
        titleLabel.setFont(Font.font("System", 32));
        titleLabel.setTextFill(Color.web("#333333"));
        titleLabel.setStyle("-fx-font-weight: bold;");

        // Mô tả
        Label descLabel = new Label("Vui lòng nhập email của bạn");
        descLabel.setFont(Font.font("System", 16));
        descLabel.setTextFill(Color.web("#666666"));

        // Email field
        VBox emailGroup = new VBox(8);
        Label emailLabel = new Label("Email");
        emailLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-font-weight: bold;");
        
        txtEmail = new TextField();
        txtEmail.setPromptText("john.doe@gmail.com");
        txtEmail.setPrefHeight(50);
        txtEmail.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e0e0e0; -fx-font-size: 16px; -fx-padding: 0 15;");
        
        // Kết quả kiểm tra email
        lblEmailResult = new Label("");
        lblEmailResult.setStyle("-fx-font-size: 12px; -fx-padding: 5 0;");
        lblEmailResult.setVisible(false);
        
        emailGroup.getChildren().addAll(emailLabel, txtEmail, lblEmailResult);

        // Nút tiếp tục
        btnTiepTuc = new Button("Tiếp tục");
        btnTiepTuc.setPrefHeight(50);
        btnTiepTuc.setPrefWidth(500);
        btnTiepTuc.setStyle("-fx-background-color: #3971FF; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8;");
        btnTiepTuc.setOnAction(e -> kiemTraEmail());

        container.getChildren().addAll(titleLabel, descLabel, emailGroup, btnTiepTuc);
        return container;
    }

    private VBox createStep2Container() {
        VBox container = new VBox(25);
        container.setAlignment(Pos.CENTER);
        container.setMaxWidth(500);

        // Tiêu đề
        Label titleLabel = new Label("Mã xác nhận");
        titleLabel.setFont(Font.font("System", 32));
        titleLabel.setTextFill(Color.web("#333333"));
        titleLabel.setStyle("-fx-font-weight: bold;");

        // Mô tả
        Label descLabel = new Label("Nhập mã xác nhận gửi về email của bạn");
        descLabel.setFont(Font.font("System", 16));
        descLabel.setTextFill(Color.web("#666666"));

        // OTP fields (4 ô riêng biệt)
        VBox otpContainer = new VBox(10);
        Label otpLabel = new Label("Mã xác nhận");
        otpLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-font-weight: bold;");
        
        HBox otpFields = new HBox(10);
        otpFields.setAlignment(Pos.CENTER_LEFT);
        
        // Tạo 4 TextField cho OTP
        TextField[] otpInputs = new TextField[4];
        for (int i = 0; i < 4; i++) {
            final int index = i;
            otpInputs[i] = new TextField();
            otpInputs[i].setPrefSize(60, 60);
            otpInputs[i].setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e0e0e0; -fx-font-size: 24px; -fx-alignment: center;");
            
            // Giới hạn 1 ký tự cho mỗi ô OTP
            otpInputs[i].textProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue.length() > 1) {
                    otpInputs[index].setText(oldValue);
                }
            });
            
            // Auto focus sang ô tiếp theo
            otpInputs[i].textProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue.length() == 1 && index < 3) {
                    otpInputs[index + 1].requestFocus();
                }
            });
            
            otpFields.getChildren().add(otpInputs[i]);
        }
        
        // Gộp OTP từ 4 field
        txtOTP = new TextField();
        txtOTP.setVisible(false);
        for (TextField field : otpInputs) {
            field.textProperty().addListener((obs, oldValue, newValue) -> {
                StringBuilder otp = new StringBuilder();
                for (TextField f : otpInputs) {
                    otp.append(f.getText());
                }
                txtOTP.setText(otp.toString());
            });
        }
        
        // Timer
        Label timerLabel = new Label("00:30");
        timerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff6b6b; -fx-font-weight: bold;");
        
        // Kết quả xác nhận OTP
        lblOTPResult = new Label("");
        lblOTPResult.setStyle("-fx-font-size: 12px; -fx-padding: 5 0;");
        lblOTPResult.setVisible(false);
        
        otpContainer.getChildren().addAll(otpLabel, otpFields, timerLabel, lblOTPResult);

        // Nút xác nhận OTP
        btnXacNhanOTP = new Button("XÁC NHẬN");
        btnXacNhanOTP.setPrefHeight(50);
        btnXacNhanOTP.setPrefWidth(500);
        btnXacNhanOTP.setStyle("-fx-background-color: #3971FF; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8;");
        btnXacNhanOTP.setOnAction(e -> xacNhanOTP());

        container.getChildren().addAll(titleLabel, descLabel, otpContainer, btnXacNhanOTP);
        return container;
    }

    private VBox createStep3Container() {
        VBox container = new VBox(25);
        container.setAlignment(Pos.CENTER);
        container.setMaxWidth(500);

        // Tiêu đề
        Label titleLabel = new Label("Đặt lại mật khẩu mới");
        titleLabel.setFont(Font.font("System", 32));
        titleLabel.setTextFill(Color.web("#333333"));
        titleLabel.setStyle("-fx-font-weight: bold;");

        // Mô tả
        Label descLabel = new Label("Vui lòng nhập mật khẩu mới của bạn gồm 8-32 ký tự");
        descLabel.setFont(Font.font("System", 16));
        descLabel.setTextFill(Color.web("#666666"));

        // Password fields
        VBox passwordGroup = new VBox(15);
        
        // Mật khẩu mới
        VBox newPassGroup = new VBox(8);
        Label newPassLabel = new Label("Nhập mật khẩu mới");
        newPassLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-font-weight: bold;");
        
        txtNewPassword = new PasswordField();
        txtNewPassword.setPromptText("7789BM6X@@H&$K_");
        txtNewPassword.setPrefHeight(50);
        txtNewPassword.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e0e0e0; -fx-font-size: 16px; -fx-padding: 0 15;");
        
        newPassGroup.getChildren().addAll(newPassLabel, txtNewPassword);

        // Xác nhận mật khẩu
        VBox confirmPassGroup = new VBox(8);
        Label confirmPassLabel = new Label("Xác nhận mật khẩu mới");
        confirmPassLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-font-weight: bold;");
        
        txtConfirmPassword = new PasswordField();
        txtConfirmPassword.setPromptText("7789BM6X@@H&$K_");
        txtConfirmPassword.setPrefHeight(50);
        txtConfirmPassword.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e0e0e0; -fx-font-size: 16px; -fx-padding: 0 15;");
        
        confirmPassGroup.getChildren().addAll(confirmPassLabel, txtConfirmPassword);

        // Kết quả xác nhận password
        lblPasswordResult = new Label("");
        lblPasswordResult.setStyle("-fx-font-size: 12px; -fx-padding: 5 0;");
        lblPasswordResult.setVisible(false);

        passwordGroup.getChildren().addAll(newPassGroup, confirmPassGroup, lblPasswordResult);

        // Nút đặt mật khẩu
        btnDatMatKhau = new Button("Đặt mật khẩu");
        btnDatMatKhau.setPrefHeight(50);
        btnDatMatKhau.setPrefWidth(500);
        btnDatMatKhau.setStyle("-fx-background-color: #3971FF; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8;");
        btnDatMatKhau.setOnAction(e -> datMatKhau());

        container.getChildren().addAll(titleLabel, descLabel, passwordGroup, btnDatMatKhau);
        return container;
    }

    private void kiemTraEmail() {
        String email = txtEmail.getText().trim();
        
        if (email.isEmpty()) {
            showResult(lblEmailResult, "Vui lòng nhập email", false);
            return;
        }
        
        if (!isValidEmail(email)) {
            showResult(lblEmailResult, "Email không hợp lệ", false);
            return;
        }
        
        // Demo: Giả lập kiểm tra email
        showResult(lblEmailResult, "✓ Email hợp lệ", true);
        currentEmail = email;
        
        // Tạo mã OTP và chuyển sang bước 2
        generatedOTP = String.format("%04d", (int)(Math.random() * 10000));
        showAlert("Thành công", "Đã gửi mã OTP đến: " + currentEmail + "\nMã OTP: " + generatedOTP);
        
        // Chuyển sang bước 2
        step1Container.setVisible(false);
        step2Container.setVisible(true);
    }

    private void xacNhanOTP() {
        String otp = txtOTP.getText().trim();
        
        if (otp.isEmpty()) {
            showResult(lblOTPResult, "Vui lòng nhập mã OTP", false);
            return;
        }
        
        if (otp.length() != 4) {
            showResult(lblOTPResult, "Mã OTP phải có 4 số", false);
            return;
        }
        
        if (otp.equals(generatedOTP)) {
            showResult(lblOTPResult, "✓ Xác nhận thành công", true);
            
            // Chuyển sang bước 3
            step2Container.setVisible(false);
            step3Container.setVisible(true);
        } else {
            showResult(lblOTPResult, "Mã OTP không đúng", false);
        }
    }

    private void datMatKhau() {
        String newPass = txtNewPassword.getText().trim();
        String confirmPass = txtConfirmPassword.getText().trim();
        
        if (newPass.isEmpty()) {
            showResult(lblPasswordResult, "Vui lòng nhập mật khẩu mới", false);
            return;
        }
        
        if (confirmPass.isEmpty()) {
            showResult(lblPasswordResult, "Vui lòng xác nhận mật khẩu", false);
            return;
        }
        
        if (newPass.length() < 8 || newPass.length() > 32) {
            showResult(lblPasswordResult, "Mật khẩu phải có 8-32 ký tự", false);
            return;
        }
        
        if (!newPass.equals(confirmPass)) {
            showResult(lblPasswordResult, "Mật khẩu xác nhận không khớp", false);
            return;
        }
        
        showResult(lblPasswordResult, "✓ Đặt mật khẩu thành công", true);
        showAlert("Thành công", "Đã đặt lại mật khẩu thành công!\nBạn có thể đăng nhập với mật khẩu mới.");
        
        // Quay về đăng nhập
        try {
            Thread.sleep(2000);
            ((Stage) txtNewPassword.getScene().getWindow()).close();
            new TrangDangNhap().start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private void showResult(Label label, String message, boolean success) {
        label.setText(message);
        label.setVisible(true);
        if (success) {
            label.setStyle("-fx-font-size: 12px; -fx-padding: 5 0; -fx-text-fill: #28a745;");
        } else {
            label.setStyle("-fx-font-size: 12px; -fx-padding: 5 0; -fx-text-fill: #dc3545;");
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
