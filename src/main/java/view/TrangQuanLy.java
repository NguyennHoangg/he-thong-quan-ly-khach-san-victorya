package view;

import java.util.Optional;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import utils.*;

public class TrangQuanLy extends Application {

        private Button btnLogout;

        @Override
        public void start(Stage stage) {
                // Lấy kích thước màn hình trước
                javafx.geometry.Rectangle2D screen = Screen.getPrimary().getBounds();
                double screenWidth = screen.getWidth();
                double screenHeight = screen.getHeight();

                // Dùng HBox làm root để sidebar chiếm toàn bộ chiều cao
                HBox root = new HBox();
                root.setPrefSize(screenWidth, screenHeight);

                // --- Thanh điều hướng bên (Sidebar) - chiếm ~15% chiều ngang ---
                VBox sidebar = new VBox();
                sidebar.setPadding(new Insets(5, 5, 5, 5));
                sidebar.setStyle(
                        "-fx-background-color: #ffffff; -fx-border-color: transparent #e6e9ee transparent transparent; -fx-border-radius:8");
                sidebar.setPrefWidth(screenWidth * 0.15);
                sidebar.setMinWidth(screenWidth * 0.15);
                sidebar.setMaxWidth(screenWidth * 0.15);

                // Logo
                Image logo = new Image(getClass().getResourceAsStream("/img/Logo.png"));
                ImageView logoView = new ImageView(logo);
                logoView.setFitWidth(screenWidth * 0.15);
                logoView.setPreserveRatio(true);
                logoView.setSmooth(true);
                logoView.setCache(true);

                VBox menu = new VBox(8);
                VBox.setMargin(menu, new Insets(5, 5, 5, 5));
                menu.setPadding(new Insets(5, 5, 5, 5));

                // Các button
                Button btnTrangChu         = createSidebarButton("Trang chủ",           "/icon/home_icon.svg",        screenWidth);
                Button btnPhong            = createSidebarButton("Tìm kiếm phòng",      "/icon/search.svg",           screenWidth);
                Button btnDatPhong         = createSidebarButton("Đặt phòng",           "/icon/datphong_icon.svg",    screenWidth);
                Button btnGiaHanPhong      = createSidebarButton("Gia hạn phòng",       "/icon/giahan_icon.svg",      screenWidth);
                Button btnHuyPhong         = createSidebarButton("Hủy phòng",           "/icon/cancel.svg",           screenWidth);
                Button btnKhuyenMai        = createSidebarButton("Khuyến mãi",          "/icon/Deals.svg",            screenWidth);
                Button btnThongKe          = createSidebarButton("Thống kê",            "/icon/thongke_icon.svg",     screenWidth);
                Button btnThanhToan        = createSidebarButton("Thanh toán",          "/icon/thanhtoan_iconn.svg",  screenWidth);
                Button btnTaiKhoan         = createSidebarButton("Tài khoản",           "/icon/taikhoan_icon.svg",    screenWidth);
                Button btnQuanLyDichVu     = createSidebarButton("Quản lý dịch vụ",     "/icon/dichvu_icon.svg",      screenWidth);
                Button btnQuanLyNhanVien   = createSidebarButton("Quản lý nhân viên",   "/icon/nhanvien_icon.svg",    screenWidth);
                Button btnQuanLyHoaDon     = createSidebarButton("Quản lý hóa đơn",     "/icon/hoadon_icon.svg",      screenWidth);
                Button btnQuanLyPhong      = createSidebarButton("Quản lý phòng",       "/icon/phong_icon.svg",       screenWidth); // NEW: QuanLyPhong

                menu.getChildren().addAll(
                        btnTrangChu, btnPhong, btnDatPhong, btnGiaHanPhong, btnHuyPhong, btnKhuyenMai,
                        // Bạn có thể di chuyển btnQuanLyPhong đến vị trí mong muốn trong danh sách dưới:
                        btnQuanLyPhong, // NEW: QuanLyPhong - vị trí hiển thị trong menu
                        btnThongKe, btnThanhToan, btnTaiKhoan, btnQuanLyDichVu, btnQuanLyNhanVien, btnQuanLyHoaDon
                );

                btnTrangChu.requestFocus();

                Region bottomSpacer = new Region();
                VBox.setVgrow(bottomSpacer, Priority.ALWAYS);

                Button btnCaiDatHeThong = Util.createSidebarButton("Cài đặt hệ thống", "/icon/caidat_icon.svg", screenWidth);
                btnLogout = Util.createSidebarButton("Đăng xuất", "/icon/logout.svg", screenWidth);

                btnLogout.setOnAction(e -> {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Đăng xuất");
                        alert.setContentText("Bạn muốn đăng xuất?");
                        alert.setHeaderText(null);
                        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                                handleLogout();
                        }
                });

                sidebar.getChildren().addAll(logoView, menu, bottomSpacer, btnCaiDatHeThong, btnLogout);

                // --- Vùng bên phải ---
                BorderPane rightArea = new BorderPane();
                rightArea.setPrefWidth(screenWidth * 0.875);

                // --- Header ---
                HBox topHeader = new HBox();
                topHeader.setPrefHeight(screenHeight * 0.1);
                topHeader.setPadding(new Insets(5, 5, 5, 5));
                topHeader.setStyle("-fx-background-color: #f0f2f5;");

                BorderPane headerCard = new BorderPane();
                headerCard.setPadding(new Insets(5, 5, 5, 5));
                headerCard.setStyle(
                        "-fx-background-color: white; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.03), 6, 0, 0, 1);");
                headerCard.setPrefHeight(screenHeight * 0.08);

                // Center của headerCard: ô tìm kiếm
                HBox centerBox2 = new HBox();
                centerBox2.setAlignment(Pos.CENTER_LEFT);
                centerBox2.setPadding(new Insets(5, 5, 5, 5));
                TextField search2 = new TextField();
                search2.setPrefWidth(screenWidth * 0.3);
                search2.setPromptText("Nhập số phòng hoặc CCCD khách hàng");
                search2.setStyle(
                        "-fx-background-radius: 8; -fx-background-color: #f7fafc; -fx-border-radius: 8; -fx-padding: 8 12 8 12;");
                centerBox2.getChildren().add(search2);
                headerCard.setCenter(centerBox2);

                // Remove focus khi click button
                headerCard.sceneProperty().addListener((obs, oldScene, newScene) -> {
                        if (newScene != null) {
                                newScene.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, ev -> {
                                        javafx.scene.Node node = (javafx.scene.Node) ev.getTarget();
                                        while (node != null && !(node instanceof Button)) {
                                                node = node.getParent();
                                        }
                                        if (node instanceof Button) {
                                                headerCard.requestFocus();
                                        }
                                });
                        }
                });

                // Right của headerCard: icon
                HBox rightBox2 = new HBox(10);
                rightBox2.setAlignment(Pos.CENTER_RIGHT);
                rightBox2.setPadding(new Insets(5, 5, 5, 5));
                Button btnAcc = Util.createSidebarButton(null, "/icon/person-20-regular.svg", screenWidth);
                btnAcc.setPrefWidth(50);
                Button btnThongBao = Util.createSidebarButton(null, "/icon/bell.svg", screenWidth);
                btnThongBao.setPrefWidth(50);
                rightBox2.getChildren().addAll(btnThongBao, btnAcc);
                headerCard.setRight(rightBox2);

                topHeader.getChildren().add(headerCard);
                HBox.setHgrow(headerCard, Priority.ALWAYS);

                // --- Content ---
                StackPane centerStack = new StackPane();
                centerStack.setStyle("-fx-background-color: #f0f2f5;");
                centerStack.setPadding(new Insets(5, 5, 5, 5));
                centerStack.setPrefHeight(screenHeight * 0.875);

                BorderPane content = new BorderPane();
                content.setPadding(new Insets(5, 5, 5, 5));
                content.setStyle(
                        "-fx-background-color: #ffffffff; -fx-border-radius: 6; -fx-background-radius: 6; -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.06), 8, 0, 0, 2);");

                // --- Các panel ---
                BorderPane panelTrangChu   = new BorderPane();
                BorderPane panelDatPhong   = new DatPhong();
                BorderPane panelKhuyenMai  = new KhuyenMai_GUI();
                BorderPane panelHuyPhong   = new HuyPhong_GUI();
                BorderPane panelQuanLyPhong = new QuanLiPhong_GUI(); // NEW: QuanLyPhong

                content.setCenter(panelTrangChu);

                // Gán sự kiện chuyển trang
                btnTrangChu.setOnAction(e -> content.setCenter(panelDatPhong));
                btnKhuyenMai.setOnAction(e -> content.setCenter(panelKhuyenMai));
                btnHuyPhong.setOnAction(e -> content.setCenter(panelHuyPhong));
                btnQuanLyPhong.setOnAction(e -> content.setCenter(panelQuanLyPhong)); // NEW: QuanLyPhong

                // Đặt header và content vào rightArea
                rightArea.setTop(topHeader);
                rightArea.setCenter(centerStack);

                // Thêm content vào centerStack (đặt dưới headerCard)
                centerStack.getChildren().add(content);
                content.prefWidthProperty().bind(centerStack.widthProperty().subtract(36));
                content.prefHeightProperty().bind(centerStack.heightProperty().subtract(36));

                // Thêm sidebar và rightArea vào root
                root.getChildren().addAll(sidebar, rightArea);

                Scene scene = new Scene(root, screenWidth, screenHeight);
                scene.getStylesheets().add(getClass().getResource("/css/TrangQuanLy.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("Trang Quản Lý - Victorya");
                stage.setX(screen.getMinX());
                stage.setY(screen.getMinY());
                stage.setWidth(screenWidth);
                stage.setHeight(screenHeight);
                stage.setMaximized(true);
                stage.setResizable(false);
                stage.show();
        }

        private Button createSidebarButton(String text, String url, double screenWidth) {
                Button btn = new Button(text, Util.readSimpleSVG(url, null, Color.web("#5D6679")));
                btn.setPrefWidth(screenWidth * 0.16);
                btn.setPrefHeight(44);
                btn.setPadding(new Insets(5, 5, 5, 5));
                btn.setGraphicTextGap(12);
                btn.setAlignment(Pos.CENTER_LEFT);
                btn.setContentDisplay(javafx.scene.control.ContentDisplay.LEFT);
                btn.getStyleClass().add("button");
                if (text != null && "Trang chủ".equalsIgnoreCase(text.trim())) {
                        btn.getStyleClass().add("active");
                }
                btn.setFocusTraversable(false);

                btn.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, ev -> {
                        javafx.scene.Parent parent = btn.getParent();
                        if (parent instanceof javafx.scene.layout.Pane) {
                                javafx.scene.layout.Pane pane = (javafx.scene.layout.Pane) parent;
                                for (javafx.scene.Node node : pane.getChildren()) {
                                        if (node instanceof Button) {
                                                ((Button) node).getStyleClass()
                                                        .removeAll(java.util.Collections.singleton("active"));
                                        }
                                }
                        } else {
                                if (btn.getScene() != null && btn.getScene().getRoot() != null) {
                                        btn.getScene().getRoot().lookupAll(".button").forEach(n -> {
                                                if (n instanceof Button)
                                                        ((Button) n).getStyleClass()
                                                                .removeAll(java.util.Collections.singleton("active"));
                                        });
                                }
                        }
                        if (!btn.getStyleClass().contains("active")) {
                                btn.getStyleClass().add("active");
                        }
                });

                return btn;
        }

        private void handleLogout() {
                TrangDangNhap trangDangNhap = new TrangDangNhap();
                java.util.Optional<javafx.stage.Window> optWindow = javafx.stage.Window.getWindows()
                        .stream()
                        .filter(javafx.stage.Window::isShowing)
                        .findFirst();

                if (optWindow.isPresent()) {
                        Stage current = (Stage) optWindow.get();
                        trangDangNhap.start(current);
                } else {
                        Stage newStage = new Stage();
                        trangDangNhap.start(newStage);
                }
        }
}
