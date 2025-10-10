package view;

import controller.User_Controller;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class TrangDangNhap extends Application {
        private User_Controller user_Controller = new User_Controller();

        @Override
        public void start(Stage primaryStage) {
                // Khởi tạo các thông số kích thước panel trái, phải, chiều cao tổng, và overlay
                // nhỏ
                // Fixed panel sizes as requested
                final double LEFT_W = 985;
                final double RIGHT_W = 920;
                final double PANEL_H = 950;
                final double OVERLAY_SIZE = 200;

                // Tạo StackPane gốc để chứa toàn bộ giao diện (layer các thành phần)
                StackPane base = new StackPane();
                base.setStyle("-fx-background-color: #f8f9fa;");
                StackPane.setAlignment(base, Pos.CENTER);

                // Panel bên trái: màu xanh, bo góc phải dưới, chiếm chiều rộng LEFT_W
                StackPane leftPane = new StackPane();
                leftPane.setPrefSize(LEFT_W, PANEL_H);
                leftPane.setMinSize(LEFT_W, PANEL_H);
                BackgroundFill leftOverlay = new BackgroundFill(
                                Color.web("#3971FF", 0.88),
                                new CornerRadii(0, 0, 80, 0, false),
                                Insets.EMPTY);
                leftPane.setBackground(new Background(leftOverlay));
                StackPane.setAlignment(leftPane, Pos.TOP_LEFT);
                base.getChildren().add(leftPane);

                // Ô vuông nhỏ overlay phía trên panel trái (chỉ để trang trí)
                StackPane overlayContainer = new StackPane();
                overlayContainer.setPrefSize(OVERLAY_SIZE, OVERLAY_SIZE);
                overlayContainer.setMaxSize(OVERLAY_SIZE, OVERLAY_SIZE);

                Region innerWhite = new Region();
                innerWhite.setPrefSize(OVERLAY_SIZE, OVERLAY_SIZE);
                innerWhite.setMaxSize(OVERLAY_SIZE, OVERLAY_SIZE);
                innerWhite.setStyle(
                                "-fx-background-color: #ffffffff; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: rgba(0,0,0,0.08); -fx-border-width: 1;");

                overlayContainer.getChildren().add(innerWhite);
                StackPane.setAlignment(overlayContainer, Pos.TOP_LEFT);
                overlayContainer.setTranslateX(((LEFT_W + RIGHT_W) / 2) - 100);
                overlayContainer.setTranslateY(0);
                overlayContainer.toFront();

                base.getChildren().add(overlayContainer);

                // Ảnh nền thành phố, đặt phía trên overlay vuông
                try {
                        Image bgImg = new Image(getClass().getResource("/img/Backgruond-area.png").toExternalForm(),
                                        1080, 1080,
                                        false, true);
                        ImageView bgView = new ImageView(bgImg);
                        bgView.setFitWidth(1080);
                        bgView.setFitHeight(1080);
                        bgView.setPreserveRatio(false);
                        StackPane.setAlignment(bgView, Pos.TOP_LEFT);
                        bgView.setTranslateX(0);
                        bgView.setTranslateY(0);
                        base.getChildren().add(bgView);

                } catch (Exception ex) {
                        // ignore if not found
                }

                // Panel bên phải: màu trắng, bo góc trái trên/dưới, chứa form đăng nhập
                StackPane rightPane = new StackPane();
                rightPane.setPrefSize(RIGHT_W, PANEL_H);
                rightPane.setMinSize(RIGHT_W, PANEL_H);
                BackgroundFill rightBg = new BackgroundFill(
                                Color.WHITE,
                                new CornerRadii(0, 0, 80, 80, false),
                                Insets.EMPTY);
                rightPane.setBackground(new Background(rightBg));
                StackPane.setAlignment(rightPane, Pos.TOP_LEFT);
                rightPane.setTranslateX(LEFT_W);

                // Tạo giao diện login đơn giản và rõ ràng cho rightPanel
                VBox loginContainer = new VBox(25);
                loginContainer.setAlignment(Pos.TOP_LEFT);
                loginContainer.setPadding(new Insets(200));

                // Tiêu đề Victorya
                Label titleLabel = new Label("Victorya");
                titleLabel.setFont(Font.font("System", 48));
                titleLabel.setTextFill(Color.web("#3971FF"));
                titleLabel.setStyle("-fx-font-weight: bold;");

                // Welcome back
                Label welcomeLabel = new Label("Chào mừng trở lại!");
                welcomeLabel.setFont(Font.font("Poppins", 32));
                welcomeLabel.setTextFill(Color.web("#333333"));

                // Form container
                VBox formBox = new VBox(15);
                formBox.setAlignment(Pos.TOP_LEFT);
                formBox.setMaxWidth(350);

                // Tài khoản
                Label userLabel = new Label("Tài khoản");
                userLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");

                TextField usernameField = new TextField();
                usernameField.setPromptText("Nhập số điện thoại của bạn");
                usernameField.setPrefHeight(45);
                usernameField.setStyle(
                                "-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e0e0e0; -fx-font-size: 14px;");

                // Mật khẩu
                Label passLabel = new Label("Mật khẩu");
                passLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");

                PasswordField passwordField = new PasswordField();
                passwordField.setPromptText("Nhập mật khẩu");
                passwordField.setPrefHeight(45);
                passwordField.setStyle(
                                "-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e0e0e0; -fx-font-size: 14px;");

                // Quên mật khẩu link
                Hyperlink forgotPasswordLink = new Hyperlink("Quên mật khẩu?");
                forgotPasswordLink.setStyle("-fx-font-size: 13px; -fx-text-fill: #0C2A92;");
                forgotPasswordLink.setOnAction(e -> {
                        ((Stage) ((Hyperlink) e.getSource()).getScene().getWindow()).close();
                        new QuenMatKhau_GUI();
                });

                // Nút đăng nhập
                Button loginButton = new Button("Đăng nhập");
                loginButton.setPrefHeight(50);
                loginButton.setPrefWidth(350);
                loginButton.setStyle(
                                "-fx-background-color: #0088FF; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8; ");

                // Xử lý sự kiện đăng nhập: nếu xác thực thành công, chuyển sang màn hình
                // TrangQuanLy
                loginButton.setOnAction(e -> {
                        String tenDangNhap = usernameField.getText();
                        String matKhau = passwordField.getText();

                        boolean authenticated = user_Controller.xacThucNguoiDung(tenDangNhap, matKhau);
                        if (!authenticated) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Đăng nhập thất bại");
                                alert.setHeaderText(null);
                                alert.setContentText("Tên đăng nhập hoặc mật khẩu không đúng.");
                                alert.showAndWait();
                                return;
                        }

                        // Nếu là admin (hoặc quyền phù hợp), mở TrangQuanLy trên cùng một Stage
                        try {
                                boolean isAdmin = user_Controller.isAdmin(tenDangNhap, matKhau);
                                if (isAdmin) {
                                        TrangQuanLy trangQuanLy = new TrangQuanLy();
                                        // Sử dụng primary stage hiện tại để tránh mở cửa sổ phụ
                                        Stage current = (Stage) loginButton.getScene().getWindow();
                                        trangQuanLy.start(current);
                                }
                                else{}
                        } catch (Exception ex) {
                                ex.printStackTrace();
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Lỗi");
                                alert.setHeaderText(null);
                                alert.setContentText("Không thể mở giao diện quản lý: " + ex.getMessage());
                                alert.showAndWait();
                        }
                });

                // Thêm tất cả vào form
                formBox.getChildren().addAll(
                                userLabel, usernameField,
                                passLabel, passwordField,
                                forgotPasswordLink, loginButton);

                // Thêm tất cả vào container chính
                loginContainer.getChildren().addAll(titleLabel, welcomeLabel, formBox);

                // Căn giữa loginContainer trong rightPane
                rightPane.getChildren().add(loginContainer);
                // Đảm bảo loginContainer nằm ở giữa rightPane
                rightPane.setAlignment(Pos.TOP_CENTER);

                // Thêm panel phải vào StackPane gốc, đảm bảo nằm trên cùng
                base.getChildren().add(rightPane);

                // Đảm bảo rightPane luôn ở trên cùng
                rightPane.toFront();

                // Lấy kích thước màn hình
                javafx.geometry.Rectangle2D screen = Screen.getPrimary().getBounds();
                double width = screen.getWidth();
                double height = screen.getHeight();

                Scene scene = new Scene(base, width, height);
                scene.getStylesheets().add(getClass().getResource("/css/TrangQuanLy.css").toExternalForm());
                primaryStage.setScene(scene);
                primaryStage.setTitle("Trang Quản Lý - Victorya");
                primaryStage.setX(screen.getMinX());
                primaryStage.setY(screen.getMinY());
                primaryStage.setWidth(width);
                primaryStage.setHeight(height);
                primaryStage.setMaximized(true); // Đặt cửa sổ ở chế độ toàn màn hình
                primaryStage.show();

                // Hiển thị cửa sổ chính với scene vừa tạo
                primaryStage.setTitle("Hotel Victorya - Login");
                primaryStage.setScene(scene);
                primaryStage.setResizable(true);
                primaryStage.centerOnScreen();
                primaryStage.show();
        }
}
