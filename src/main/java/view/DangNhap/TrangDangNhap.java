package view.DangNhap;

import controller.TaiKhoan_Controller;
import controller.User_Controller;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.NhanVien;
import view.TrangNhanVien;
import view.TrangQuanLy;

public class TrangDangNhap extends Application {
        private static User_Controller user_Controller = new User_Controller();
        private static TaiKhoan_Controller taiKhoan_Controller = new TaiKhoan_Controller();

        @Override
        public void init() throws Exception {
                
                // Giả lập loading 6 giây với splash screen
                notifyPreloader(new javafx.application.Preloader.ProgressNotification(0.0));
                Thread.sleep(1000);

                notifyPreloader(new javafx.application.Preloader.ProgressNotification(0.2));
                Thread.sleep(1000);

                notifyPreloader(new javafx.application.Preloader.ProgressNotification(0.4));
                Thread.sleep(1000);

                notifyPreloader(new javafx.application.Preloader.ProgressNotification(0.6));
                Thread.sleep(1000);

                notifyPreloader(new javafx.application.Preloader.ProgressNotification(0.8));
                Thread.sleep(1000);

                notifyPreloader(new javafx.application.Preloader.ProgressNotification(0.95));
                Thread.sleep(1000);
                
                
               
                
                notifyPreloader(new javafx.application.Preloader.ProgressNotification(1.0));
        }

        @Override
        public void start(Stage primaryStage) {
                // Lấy kích thước màn hình
                javafx.geometry.Rectangle2D screen = Screen.getPrimary().getVisualBounds();
                double width = screen.getWidth();
                double height = screen.getHeight();

                // Tạo StackPane gốc để chứa toàn bộ giao diện (layer các thành phần)
                StackPane base = new StackPane();
                base.setStyle("-fx-background-color: #f8f9fa;");
                StackPane.setAlignment(base, Pos.CENTER);

                // Panel bên trái: màu xanh, chỉ bo góc dưới phải
                StackPane leftPane = new StackPane();
                leftPane.prefWidthProperty().bind(primaryStage.widthProperty().multiply(0.52));
                leftPane.prefHeightProperty().bind(primaryStage.heightProperty());
                leftPane.setMinWidth(400);
                BackgroundFill leftOverlay = new BackgroundFill(
                                Color.web("#3971FF", 0.88),
                                new CornerRadii(0, 0, 80, 0, false), // chỉ bo góc dưới phải
                                Insets.EMPTY);
                leftPane.setBackground(new Background(leftOverlay));
                StackPane.setAlignment(leftPane, Pos.TOP_LEFT);
                base.getChildren().add(leftPane);

                // Ô vuông nhỏ overlay phía trên panel trái (chỉ để trang trí)
                StackPane overlayContainer = new StackPane();
                double overlaySize = Math.min(width, height) * 0.18;
                overlayContainer.setPrefSize(overlaySize, overlaySize);
                overlayContainer.setMaxSize(overlaySize, overlaySize);

                Region innerWhite = new Region();
                innerWhite.setPrefSize(overlaySize, overlaySize);
                innerWhite.setMaxSize(overlaySize, overlaySize);
                innerWhite.setStyle(
                                "-fx-background-color: #ffffffff; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: rgba(0,0,0,0.08); -fx-border-width: 1;");

                overlayContainer.getChildren().add(innerWhite);
                StackPane.setAlignment(overlayContainer, Pos.TOP_LEFT);
                primaryStage.widthProperty().addListener((obs, oldW, newW) -> {
                    overlayContainer.setTranslateX((newW.doubleValue() / 2) - 100);
                });
                overlayContainer.setTranslateX((width / 2) - 100);
                overlayContainer.setTranslateY(0);
                overlayContainer.toFront();

                base.getChildren().add(overlayContainer);

                // Ảnh nền thành phố, đặt phía trên overlay vuông
                try {
                        Image bgImg = new Image(getClass().getResource("/img/Backgruond-area.png").toExternalForm());
                        ImageView bgView = new ImageView(bgImg);
                        bgView.setPreserveRatio(true);
                        bgView.fitWidthProperty().bind(primaryStage.widthProperty().multiply(0.52));
                        StackPane.setAlignment(bgView, Pos.TOP_LEFT);
                        base.getChildren().add(bgView);

                } catch (Exception ex) {
                        // ignore if not found
                }

                // Panel bên phải: màu trắng, bo góc dưới trái
                StackPane rightPane = new StackPane();
                rightPane.prefWidthProperty().bind(primaryStage.widthProperty().multiply(0.48));
                rightPane.prefHeightProperty().bind(primaryStage.heightProperty());
                rightPane.setMinWidth(400);
                BackgroundFill rightBg = new BackgroundFill(
                                Color.WHITE,
                                new CornerRadii(0, 0, 0, 80, false), // bo góc dưới trái
                                Insets.EMPTY);
                rightPane.setBackground(new Background(rightBg));
                StackPane.setAlignment(rightPane, Pos.TOP_LEFT);
                primaryStage.widthProperty().addListener((obs, oldW, newW) -> {
                    rightPane.setTranslateX(newW.doubleValue() * 0.52);
                });
                rightPane.setTranslateX(width * 0.52);

                // Tạo giao diện login đơn giản và rõ ràng cho rightPanel
                VBox loginContainer = new VBox();
                loginContainer.setAlignment(Pos.TOP_LEFT);
                primaryStage.heightProperty().addListener((obs, oldH, newH) -> {
                    double padding = Math.max(60, newH.doubleValue() * 0.15);
                    double spacing = Math.max(15, newH.doubleValue() * 0.02);
                    loginContainer.setPadding(new Insets(padding, 80, 60, 80));
                    loginContainer.setSpacing(spacing);
                });
                loginContainer.setPadding(new Insets(Math.max(60, height * 0.15), 80, 60, 80));
                loginContainer.setSpacing(Math.max(15, height * 0.02));

                // Tiêu đề Victorya
                Label titleLabel = new Label("Victorya");
                titleLabel.setTextFill(Color.web("#3971FF"));
                titleLabel.setStyle("-fx-font-weight: bold;");
                primaryStage.heightProperty().addListener((obs, oldH, newH) -> {
                    titleLabel.setFont(Font.font("System", Math.max(32, Math.min(48, newH.doubleValue() * 0.045))));
                });
                titleLabel.setFont(Font.font("System", Math.max(32, Math.min(48, height * 0.045))));

                // Welcome back
                Label welcomeLabel = new Label("Chào mừng trở lại!");
                welcomeLabel.setTextFill(Color.web("#333333"));
                primaryStage.heightProperty().addListener((obs, oldH, newH) -> {
                    welcomeLabel.setFont(Font.font("Poppins", Math.max(22, Math.min(32, newH.doubleValue() * 0.03))));
                });
                welcomeLabel.setFont(Font.font("Poppins", Math.max(22, Math.min(32, height * 0.03))));

                // Form container
                VBox formBox = new VBox();
                formBox.setAlignment(Pos.TOP_LEFT);
                primaryStage.widthProperty().addListener((obs, oldW, newW) -> {
                    formBox.setMaxWidth(Math.max(280, Math.min(380, newW.doubleValue() * 0.2)));
                    formBox.setSpacing(Math.max(10, newW.doubleValue() * 0.008));
                });
                formBox.setMaxWidth(Math.max(280, Math.min(380, width * 0.2)));
                formBox.setSpacing(Math.max(10, width * 0.008));

                // Tài khoản
                Label userLabel = new Label("Tài khoản");
                userLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");

                TextField usernameField = new TextField();
                usernameField.setPromptText("Nhập số điện thoại của bạn");
                usernameField.setStyle(
                                "-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e0e0e0; -fx-font-size: 14px;");
                primaryStage.heightProperty().addListener((obs, oldH, newH) -> {
                    double inputHeight = Math.max(38, Math.min(50, newH.doubleValue() * 0.042));
                    usernameField.setPrefHeight(inputHeight);
                });
                usernameField.setPrefHeight(Math.max(38, Math.min(50, height * 0.042)));

                // Mật khẩu
                Label passLabel = new Label("Mật khẩu");
                passLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");

                PasswordField passwordField = new PasswordField();
                passwordField.setPromptText("Nhập mật khẩu");
                primaryStage.heightProperty().addListener((obs, oldH, newH) -> {
                    double inputHeight = Math.max(38, Math.min(50, newH.doubleValue() * 0.042));
                    passwordField.setPrefHeight(inputHeight);
                });
                passwordField.setPrefHeight(Math.max(38, Math.min(50, height * 0.042)));
                passwordField.setStyle(
                                "-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e0e0e0; -fx-font-size: 14px;");

                // Quên mật khẩu link
                Hyperlink forgotPasswordLink = new Hyperlink("Quên mật khẩu?");
                forgotPasswordLink.setStyle("-fx-font-size: 13px; -fx-text-fill: #0C2A92;");
                forgotPasswordLink.setOnAction(e -> {
                        // Tạo pane QuenMatKhau với nút quay lại
                        BorderPane quenMatKhau = new QuenMatKhau_GUI();
                        primaryStage.getScene().setRoot(quenMatKhau);
                });

                // Nút đăng nhập
                Button loginButton = new Button("Đăng nhập");
                primaryStage.heightProperty().addListener((obs, oldH, newH) -> {
                    loginButton.setPrefHeight(Math.max(42, Math.min(55, newH.doubleValue() * 0.047)));
                });
                primaryStage.widthProperty().addListener((obs, oldW, newW) -> {
                    loginButton.setPrefWidth(Math.max(280, Math.min(380, newW.doubleValue() * 0.2)));
                });
                loginButton.setPrefHeight(Math.max(42, Math.min(55, height * 0.047)));
                loginButton.setPrefWidth(Math.max(280, Math.min(380, width * 0.2)));
                loginButton.setStyle(
                                "-fx-background-color: #0088FF; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8; ");

                // Xử lý sự kiện đăng nhập: nếu xác thực thành công, chuyển sang màn hình
                // TrangQuanLy
                loginButton.setOnAction(e -> {
                        String tenDangNhap = usernameField.getText();
                        String matKhau = passwordField.getText();

                        boolean authenticated = user_Controller.xacThucNguoiDung(tenDangNhap, matKhau);
                        boolean isAdmin = user_Controller.isAdmin(tenDangNhap, matKhau);
                        NhanVien nhanVien = taiKhoan_Controller.layThongTinNhanVien(tenDangNhap);

                        if (!authenticated) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Đăng nhập thất bại");
                                alert.setHeaderText(null);
                                alert.setContentText("Tên đăng nhập hoặc mật khẩu không đúng.");
                                alert.showAndWait();
                                return;
                        }


                        // Lấy thông tin tài khoản và nhân viên
                        try {
                                if (isAdmin) {
                                        TrangQuanLy trangQuanLy = new TrangQuanLy(nhanVien);
                                        Stage current = (Stage) loginButton.getScene().getWindow();
                                        trangQuanLy.start(current);
                                } else {
                                        TrangNhanVien trangNhanVien = new TrangNhanVien();
                                        Stage current = (Stage) loginButton.getScene().getWindow();
                                        trangNhanVien.start(current);
                                }
                        } catch (Exception ex) {
                                ex.printStackTrace();
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Lỗi");
                                alert.setHeaderText(null);
                                alert.setContentText("Không thể mở giao diện quản lý: " + ex.getMessage());
                                alert.showAndWait();
                        }
                });

                passwordField.setOnKeyPressed(e -> {
                        if (e.getCode() == KeyCode.ENTER) {
                                String tenDangNhap = usernameField.getText();
                                String matKhau = passwordField.getText();

                                boolean authenticated = user_Controller.xacThucNguoiDung(tenDangNhap, matKhau);
                                boolean isAdmin = user_Controller.isAdmin(tenDangNhap, matKhau);
                                NhanVien nhanVien = taiKhoan_Controller.layThongTinNhanVien(tenDangNhap);

                                if (!authenticated) {
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setTitle("Đăng nhập thất bại");
                                        alert.setHeaderText(null);
                                        alert.setContentText("Tên đăng nhập hoặc mật khẩu không đúng.");
                                        alert.showAndWait();
                                        return;
                                }

                                // Lấy thông tin tài khoản và nhân viên
                                try {
                                        if (isAdmin) {
                                                TrangQuanLy trangQuanLy = new TrangQuanLy(nhanVien);
                                                Stage current = (Stage) loginButton.getScene().getWindow();
                                                trangQuanLy.start(current);
                                        } else {
                                                TrangNhanVien trangNhanVien = new TrangNhanVien();
                                                Stage current = (Stage) loginButton.getScene().getWindow();
                                                trangNhanVien.start(current);
                                        }
                                } catch (Exception ex) {
                                        ex.printStackTrace();
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setTitle("Lỗi");
                                        alert.setHeaderText(null);
                                        alert.setContentText("Không thể mở giao diện quản lý: " + ex.getMessage());
                                        alert.showAndWait();
                                }
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

                Scene scene = new Scene(base, width, height);
                try {
                        scene.getStylesheets().add(getClass().getResource("/css/Login.css").toExternalForm());
                } catch (Exception ex) {
                        // CSS file không tồn tại, bỏ qua
                }
                primaryStage.setScene(scene);
                primaryStage.setTitle("Trang Quản Lý - Victorya");
                primaryStage.setMaximized(true); // Luôn full màn hình
                primaryStage.setResizable(true);
                primaryStage.centerOnScreen();
                primaryStage.show();
        }

        // Static method for returning login UI pane for use in other screens
        public static StackPane createLoginPane(double width, double height) {
                final double LEFT_W = width * 0.52;
                final double RIGHT_W = width * 0.48;
                final double PANEL_H = height;
                final double OVERLAY_SIZE = Math.min(width, height) * 0.18;

                StackPane base = new StackPane();
                base.setStyle("-fx-background-color: #f8f9fa;");
                StackPane.setAlignment(base, Pos.CENTER);

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

                StackPane overlayContainer = new StackPane();
                overlayContainer.setPrefSize(OVERLAY_SIZE, OVERLAY_SIZE);
                overlayContainer.setMaxSize(OVERLAY_SIZE, OVERLAY_SIZE);
                Region innerWhite = new Region();
                innerWhite.setPrefSize(OVERLAY_SIZE, OVERLAY_SIZE);
                innerWhite.setMaxSize(OVERLAY_SIZE, OVERLAY_SIZE);
                innerWhite.setStyle("-fx-background-color: #ffffffff; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: rgba(0,0,0,0.08); -fx-border-width: 1;");
                overlayContainer.getChildren().add(innerWhite);
                StackPane.setAlignment(overlayContainer, Pos.TOP_LEFT);
                overlayContainer.setTranslateX(((LEFT_W + RIGHT_W) / 2) - 100);
                overlayContainer.setTranslateY(0);
                overlayContainer.toFront();
                base.getChildren().add(overlayContainer);

                try {
                    Image bgImg = new Image(TrangDangNhap.class.getResource("/img/Backgruond-area.png").toExternalForm(),
                        1080, 1080, false, true);
                    ImageView bgView = new ImageView(bgImg);
                    bgView.setFitWidth(980);
                    bgView.setFitHeight(1080);
                    bgView.setPreserveRatio(false);
                    StackPane.setAlignment(bgView, Pos.TOP_LEFT);
                    bgView.setTranslateX(0);
                    bgView.setTranslateY(0);
                    base.getChildren().add(bgView);
                } catch (Exception ex) {
                    // ignore if not found
                }

                StackPane rightPane = new StackPane();
                rightPane.setPrefSize(RIGHT_W, PANEL_H);
                rightPane.setMinSize(RIGHT_W, PANEL_H);
                BackgroundFill rightBg = new BackgroundFill(
                    Color.WHITE,
                    new CornerRadii(0, 0, 0, 80, false),
                    Insets.EMPTY);
                rightPane.setBackground(new Background(rightBg));
                StackPane.setAlignment(rightPane, Pos.TOP_LEFT);
                rightPane.setTranslateX(LEFT_W);

                VBox loginContainer = new VBox(25);
                loginContainer.setAlignment(Pos.TOP_LEFT);
                loginContainer.setPadding(new Insets(200));

                Label titleLabel = new Label("Victorya");
                titleLabel.setFont(Font.font("System", 48));
                titleLabel.setTextFill(Color.web("#3971FF"));
                titleLabel.setStyle("-fx-font-weight: bold;");

                Label welcomeLabel = new Label("Chào mừng trở lại!");
                welcomeLabel.setFont(Font.font("Poppins", 32));
                welcomeLabel.setTextFill(Color.web("#333333"));

                VBox formBox = new VBox(15);
                formBox.setAlignment(Pos.TOP_LEFT);
                formBox.setMaxWidth(350);

                Label userLabel = new Label("Tài khoản");
                userLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");

                TextField usernameField = new TextField();
                usernameField.setPromptText("Nhập số điện thoại của bạn");
                usernameField.setPrefHeight(45);
                usernameField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e0e0e0; -fx-font-size: 14px;");

                Label passLabel = new Label("Mật khẩu");
                passLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");

                PasswordField passwordField = new PasswordField();
                passwordField.setPromptText("Nhập mật khẩu");
                passwordField.setPrefHeight(45);
                passwordField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e0e0e0; -fx-font-size: 14px;");

                Hyperlink forgotPasswordLink = new Hyperlink("Quên mật khẩu?");
                forgotPasswordLink.setStyle("-fx-font-size: 13px; -fx-text-fill: #0C2A92;");
                // Chuyển sang trang QuenMatKhau
                forgotPasswordLink.setOnAction(e -> {
                    Scene scene = forgotPasswordLink.getScene();
                    if (scene != null) {
                        scene.setRoot(new QuenMatKhau_GUI());
                    }
                });

                Button loginButton = new Button("Đăng nhập");
                loginButton.setPrefHeight(50);
                loginButton.setPrefWidth(350);
                loginButton.setStyle("-fx-background-color: #0088FF; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8; ");

                loginButton.setOnAction(e -> {
                        String tenDangNhap = usernameField.getText();
                        String matKhau = passwordField.getText();

                        boolean authenticated = user_Controller.xacThucNguoiDung(tenDangNhap, matKhau);
                        boolean isAdmin = user_Controller.isAdmin(tenDangNhap, matKhau);
                        NhanVien nhanVien = taiKhoan_Controller.layThongTinNhanVien(tenDangNhap);


                        if (!authenticated) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Đăng nhập thất bại");
                                alert.setHeaderText(null);
                                alert.setContentText("Tên đăng nhập hoặc mật khẩu không đúng.");
                                alert.showAndWait();
                                return;
                        }

                        // Lấy thông tin tài khoản và nhân viên
                        try {
                                if (isAdmin) {
                                        TrangQuanLy trangQuanLy = new TrangQuanLy(nhanVien);
                                        Stage current = (Stage) loginButton.getScene().getWindow();
                                        trangQuanLy.start(current);
                                } else {
                                        TrangNhanVien trangNhanVien = new TrangNhanVien();
                                        Stage current = (Stage) loginButton.getScene().getWindow();
                                        trangNhanVien.start(current);
                                }
                        } catch (Exception ex) {
                                ex.printStackTrace();
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Lỗi");
                                alert.setHeaderText(null);
                                alert.setContentText("Không thể mở giao diện quản lý: " + ex.getMessage());
                                alert.showAndWait();
                        }
                });

                formBox.getChildren().addAll(
                    userLabel, usernameField,
                    passLabel, passwordField,
                    forgotPasswordLink, loginButton);
                loginContainer.getChildren().addAll(titleLabel, welcomeLabel, formBox);
                rightPane.getChildren().add(loginContainer);
                rightPane.setAlignment(Pos.TOP_CENTER);
                base.getChildren().add(rightPane);
                rightPane.toFront();
                return base;
        }
}