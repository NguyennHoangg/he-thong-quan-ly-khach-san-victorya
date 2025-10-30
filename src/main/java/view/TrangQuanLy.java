package view;

import java.util.Optional;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import model.NhanVien;
import utils.*;

public class TrangQuanLy extends Application {
        private Button btnLogout;
        private VBox submenuPhong;
        private boolean isSubmenuVisible = false;
        private double screenWidth;
        private double screenHeight;
        private PanelLoader panelLoader;
        private Stage stageWifi;

        private NhanVien nhanVien;
        // Dùng chung contentPane cho sidebar và right area
        private BorderPane contentPane;

        public TrangQuanLy(NhanVien nhanVien) {
                this.nhanVien = nhanVien;
        }

        private Stage getStageWifi() {
                if (stageWifi == null) {
                        Wifi_Modal modalWifi = new Wifi_Modal();
                        stageWifi = modalWifi.getStage();
                }
                return stageWifi;
        }

        @Override
        public void init() throws Exception {
                initScreenDimensions();
                panelLoader = PanelLoader.getInstance();
                preloadAllSVGIcons();
        }

        private void initScreenDimensions() {
                javafx.geometry.Rectangle2D screen = javafx.stage.Screen.getPrimary().getVisualBounds();
                screenWidth = screen.getWidth();
                screenHeight = screen.getHeight();
        }

        private void preloadAllSVGIcons() {
                String[] iconPaths = {
                                "/icon/home_icon.svg", "/icon/house.svg", "/icon/search.svg",
                                "/icon/datphong_icon.svg", "/icon/doiphong_icon.svg", "/icon/giahan_icon.svg",
                                "/icon/cancel.svg", "/icon/Deals.svg", "/icon/thongke_icon.svg",
                                "/icon/thanhtoan_iconn.svg", "/icon/taikhoan_icon.svg", "/icon/house-check.svg",
                                "/icon/dichvu_icon.svg", "/icon/nhanvien_icon.svg", "/icon/hoadon_icon.svg",
                                "/icon/wifi.svg", "/icon/caidat_icon.svg", "/icon/logout.svg",
                                "/icon/person-20-regular.svg", "/icon/bell.svg"
                };
                java.util.Arrays.stream(iconPaths).parallel().forEach(path -> {
                        Util.readSimpleSVG(path, null, Color.web("#5D6679"));
                });
        }

        @Override
        public void start(Stage stage) {
                if (panelLoader == null)
                        panelLoader = PanelLoader.getInstance();
                if (screenWidth == 0 || screenHeight == 0)
                        initScreenDimensions();

                HBox root = createRootLayout(stage);
                Scene scene = new Scene(root, screenWidth, screenHeight);
                scene.getStylesheets().add(getClass().getResource("/css/TrangQuanLy.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("Trang Quản Lý - Victorya Hotel");
                stage.setMaximized(true); // Luôn full màn hình
                stage.setResizable(true);
                stage.centerOnScreen();

                javafx.application.Platform.runLater(() -> {
                        stage.show();
                        preloadPanelsInBackground();
                });
        }

        private HBox createRootLayout(Stage stage) {
                // Tạo contentPane dùng chung
                contentPane = new BorderPane();
                contentPane.setPadding(new Insets(5));
                contentPane.setStyle(
                                "-fx-background-color: #ffffffff; -fx-border-radius: 6; -fx-background-radius: 6; -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.06), 8, 0, 0, 2);");
                contentPane.setCenter(panelLoader.getPanelTrangChu());

                VBox sidebar = createSidebar();
                BorderPane rightArea = createRightArea();
                HBox root = new HBox();
                root.setPrefSize(screenWidth, screenHeight);
                root.getChildren().addAll(sidebar, rightArea);
                return root;
        }

        private VBox createSidebar() {
                VBox sidebar = new VBox();
                sidebar.setPadding(new Insets(5));
                sidebar.setStyle(
                                "-fx-background-color: #ffffff; -fx-border-color: transparent #e6e9ee transparent transparent; -fx-border-radius:8");
                sidebar.setPrefWidth(screenWidth * 0.15);

                Image logo = new Image(getClass().getResourceAsStream("/img/Logo.png"), screenWidth * 0.15, 0, true,
                                false);
                ImageView logoView = new ImageView(logo);

                VBox menu = createSidebarMenu();

                Region bottomSpacer = new Region();
                VBox.setVgrow(bottomSpacer, Priority.ALWAYS);

                Button btnWifi = createSidebarButton("Wifi", "/icon/wifi.svg", screenWidth);
                Button btnCaiDatHeThong = createSidebarButton("Cài đặt hệ thống", "/icon/caidat_icon.svg", screenWidth);
                btnLogout = createSidebarButton("Đăng xuất", "/icon/logout.svg", screenWidth);
                btnLogout.setOnAction(e -> confirmLogout());
                btnWifi.setOnAction(e -> getStageWifi());
                sidebar.getChildren().addAll(logoView, menu, bottomSpacer, btnWifi, btnCaiDatHeThong, btnLogout);
                return sidebar;
        }

        private VBox createSidebarMenu() {
                VBox menu = new VBox(8);
                menu.setPadding(new Insets(5));

                Button btnTrangChu = createSidebarButton("Dashboard", "/icon/home_icon.svg", screenWidth);
                Button btnPhong = createSidebarButton("Phòng", "/icon/house.svg", screenWidth);

                submenuPhong = createSubmenuPhong();
                submenuPhong.setVisible(false);
                submenuPhong.setManaged(false);

                Button btnKhuyenMai = createSidebarButton("Khuyến mãi", "/icon/Deals.svg", screenWidth);
                Button btnThongKe = createSidebarButton("Thống kê", "/icon/thongke_icon.svg", screenWidth);
                Button btnThanhToan = createSidebarButton("Thanh toán", "/icon/thanhtoan_iconn.svg", screenWidth);
                Button btnTaiKhoan = createSidebarButton("Tài khoản", "/icon/taikhoan_icon.svg", screenWidth);
                Button btnQuanLyPhong = createSidebarButton("Quản lý phòng", "/icon/house-check.svg", screenWidth);
                Button btnQuanLyDichVu = createSidebarButton("Quản lý dịch vụ", "/icon/dichvu_icon.svg", screenWidth);
                Button btnQuanLyNhanVien = createSidebarButton("Quản lý nhân viên", "/icon/nhanvien_icon.svg",
                                screenWidth);
                Button btnQuanLyHoaDon = createSidebarButton("Quản lý hóa đơn", "/icon/hoadon_icon.svg", screenWidth);

                menu.getChildren().addAll(
                                btnTrangChu, btnPhong, submenuPhong, btnKhuyenMai,
                                btnThongKe, btnThanhToan, btnTaiKhoan, btnQuanLyPhong, btnQuanLyDichVu,
                                btnQuanLyNhanVien, btnQuanLyHoaDon);

                btnTrangChu.requestFocus();

                // Event handlers
                btnTrangChu.setOnAction(e -> contentPane.setCenter(panelLoader.getPanelTrangChu()));
                btnKhuyenMai.setOnAction(e -> contentPane.setCenter(panelLoader.getPanelKhuyenMai()));
                btnPhong.setOnAction(e -> toggleSubmenu());
                btnTaiKhoan.setOnAction(e -> contentPane.setCenter(panelLoader.getPanelTaiKhoan()));
                btnQuanLyPhong.setOnAction(e -> contentPane.setCenter(panelLoader.getPanelQuanLiPhong()));
                btnQuanLyNhanVien.setOnAction(e -> contentPane.setCenter(panelLoader.getPanelQuanLiNhanVien()));
                btnQuanLyDichVu.setOnAction(e -> contentPane.setCenter(panelLoader.getPanelQuanLiDichVu()));
                btnThanhToan.setOnAction(e -> contentPane.setCenter(panelLoader.getPanelThanhToan()));
                btnQuanLyHoaDon.setOnAction(e -> contentPane.setCenter(panelLoader.getPanelQuanLyHoaDon()));

                return menu;
        }

        private VBox createSubmenuPhong() {
                VBox submenu = new VBox(4);
                submenu.setPadding(new Insets(0, 0, 0, 20));
                Button btnTimKiemPhong = createSidebarButton("Tìm kiếm phòng", "/icon/search.svg", screenWidth);
                Button btnDatPhong = createSidebarButton("Đặt phòng", "/icon/datphong_icon.svg", screenWidth);
                Button btnDoiPhong = createSidebarButton("Đổi phòng", "/icon/doiphong_icon.svg", screenWidth);
                Button btnGiaHanPhong = createSidebarButton("Gia hạn phòng", "/icon/giahan_icon.svg", screenWidth);
                Button btnHuyPhong = createSidebarButton("Hủy phòng", "/icon/cancel.svg", screenWidth);

                btnTimKiemPhong.setOnAction(e -> contentPane.setCenter(panelLoader.getPanelTimKiem()));
                btnDatPhong.setOnAction(e -> contentPane.setCenter(panelLoader.getPanelDatPhong()));
                btnDoiPhong.setOnAction(e -> contentPane.setCenter(panelLoader.getPanelDoiPhong()));
                btnGiaHanPhong.setOnAction(e -> contentPane.setCenter(panelLoader.getPanelGiaHanPhong()));
                btnHuyPhong.setOnAction(e -> contentPane.setCenter(panelLoader.getPanelHuyPhong()));

                submenu.getChildren().addAll(btnTimKiemPhong, btnDatPhong, btnDoiPhong, btnGiaHanPhong, btnHuyPhong);
                return submenu;
        }

        private BorderPane getContentPane() {
                // Đã dùng contentPane ở cấp class, trả về nó
                return contentPane;
        }

        private BorderPane createRightArea() {
                BorderPane rightArea = new BorderPane();
                rightArea.setPrefWidth(screenWidth * 0.875);

                HBox header = taoHeader();
                header.setPadding(new Insets(5, 5, 0, 5));
                rightArea.setTop(header);

                StackPane centerStack = new StackPane();
                centerStack.setStyle("-fx-background-color: #f0f2f5;");
                centerStack.setPadding(new Insets(5));
                centerStack.setPrefHeight(screenHeight * 0.875);

                centerStack.getChildren().add(contentPane);

                contentPane.prefWidthProperty().bind(centerStack.widthProperty().subtract(36));
                contentPane.prefHeightProperty().bind(centerStack.heightProperty().subtract(36));

                rightArea.setCenter(centerStack);
                return rightArea;
        }

        private Button createSidebarButton(String text, String url, double screenWidth) {
                Button btn = new Button(text, Util.readSimpleSVG(url, null, Color.web("#5D6679")));
                btn.setPrefWidth(screenWidth * 0.16);
                btn.setPrefHeight(44);
                btn.setPadding(new Insets(5));
                btn.setGraphicTextGap(12);
                btn.setAlignment(Pos.CENTER_LEFT);
                btn.setContentDisplay(ContentDisplay.LEFT);
                btn.getStyleClass().add("button");
                if (text != null && "Trang chủ".equalsIgnoreCase(text.trim())) {
                        btn.getStyleClass().add("active");
                }
                btn.setFocusTraversable(false);

                btn.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, ev -> {
                        javafx.scene.Parent parent = btn.getParent();
                        if (parent instanceof Pane) {
                                Pane pane = (Pane) parent;
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
                                                        ((Button) n).getStyleClass().removeAll(
                                                                        java.util.Collections.singleton("active"));
                                        });
                                }
                        }
                        if (!btn.getStyleClass().contains("active")) {
                                btn.getStyleClass().add("active");
                        }
                });
                return btn;
        }

        private void toggleSubmenu() {
                isSubmenuVisible = !isSubmenuVisible;
                submenuPhong.setVisible(isSubmenuVisible);
                submenuPhong.setManaged(isSubmenuVisible);
        }

        private void confirmLogout() {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Đăng xuất");
                alert.setContentText("Bạn muốn đăng xuất?");
                alert.setHeaderText(null);
                alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                        handleLogout();
                }
        }

        private void handleLogout() {
                TrangDangNhap trangDangNhap = new TrangDangNhap();
                Optional<javafx.stage.Window> optWindow = javafx.stage.Window.getWindows()
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

        private void preloadPanelsInBackground() {
                new Thread(() -> {
                        try {
                                javafx.application.Platform.runLater(() -> {
                                        panelLoader.preloadRoomPanels((progress, message) -> {
                                        });
                                });
                                Thread.sleep(3000);
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                }, "PanelPreloader").start();
        }

        private HBox taoHeader() {
                HBox headerContainer = new HBox();
                headerContainer.setPadding(new Insets(5, 5, 0, 5));
                headerContainer.setStyle("-fx-background-color: #f0f2f5;");

                HBox header = new HBox(30);
                header.setPadding(new Insets(15, 30, 15, 30));
                header.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.03), 6, 0, 0, 1);");
                header.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(header, Priority.ALWAYS);

                VBox titleBox = new VBox(2);
                Label title = new Label("Victorya Hotel");
                title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
                Label subtitle = new Label("Management System");
                subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
                titleBox.getChildren().addAll(title, subtitle);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                StackPane bellPane = new StackPane();
                bellPane.setCursor(javafx.scene.Cursor.HAND);
                Circle bellBg = new Circle(20);
                bellBg.setStyle("-fx-fill: #f1f5f9;");
                Label bellIcon = new Label("🔔");
                bellIcon.setStyle("-fx-font-size: 20px;");
                Circle badge = new Circle(9);
                badge.setStyle("-fx-fill: #ef4444;");
                Label badgeLabel = new Label("3");
                badgeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;");
                StackPane badgeStack = new StackPane(badge, badgeLabel);
                badgeStack.setTranslateX(14);
                badgeStack.setTranslateY(-14);
                bellPane.getChildren().addAll(bellBg, bellIcon, badgeStack);
                bellPane.setPadding(new Insets(0, 20, 0, 0));

                HBox userBox = new HBox(12);
                userBox.setAlignment(Pos.CENTER);
                userBox.setCursor(javafx.scene.Cursor.HAND);
                userBox.setPadding(new Insets(8, 12, 8, 12));
                userBox.setStyle(" -fx-background-radius: 8;");
                Button btnAvt = Util.createSidebarButton(null, "/icon/person-20-regular.svg", 150);
                StackPane avatarStack = new StackPane(btnAvt);
                VBox userInfo = new VBox(2);
                Label userName = new Label(nhanVien.getTenNhanVien());
                userName.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #0f172a;");
                Label userEmail = new Label(nhanVien.getEmail());
                userEmail.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
                userInfo.getChildren().addAll(userName, userEmail);
                userBox.getChildren().addAll(avatarStack, userInfo);

                header.getChildren().addAll(titleBox, spacer, bellPane, userBox);
                headerContainer.getChildren().add(header);
                return headerContainer;
        }
}
