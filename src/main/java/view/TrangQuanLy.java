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
import view.DangNhap.TrangDangNhap;

public class TrangQuanLy extends Application {
        private Button btnLogout;
        private VBox submenuPhong;
        private boolean isSubmenuVisible = false;
        private double screenWidth;
        private double screenHeight;
        private PanelLoader panelLoader;

        private NhanVien nhanVien;
        // Dùng chung contentPane cho sidebar và right area
        private BorderPane contentPane;
        
        // Ca làm việc
        private CaLamViec_GUI caLamViecGUI;

        public TrangQuanLy(NhanVien nhanVien) {
                this.nhanVien = nhanVien;
                // Khởi tạo CaLamViec_GUI sớm
                caLamViecGUI = new CaLamViec_GUI();
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
                                "/icon/eight-oclock.svg", "/icon/logout.svg",
                                "/icon/person-20-regular.svg", "/icon/bell.svg", "/icon/info.svg"
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
                stage.setMaximized(true);
                stage.setResizable(true);
                stage.centerOnScreen();

                // Cleanup khi đóng ứng dụng
                stage.setOnCloseRequest(event -> {
                        if (panelLoader != null) {
                                panelLoader.clearCache();
                        }
                });

                javafx.application.Platform.runLater(() -> {
                        stage.show();
                        // Load Dashboard mặc định sau khi stage đã hiển thị
                        contentPane.setCenter(new DashBoard_GUI());
                        preloadPanelsInBackground();
                });
        }

        private HBox createRootLayout(Stage stage) {
                // Tạo contentPane dùng chung
                contentPane = new BorderPane();
                contentPane.setPadding(new Insets(0));
                contentPane.setStyle(
                                "-fx-background-color: #ffffffff; -fx-border-radius: 6; -fx-background-radius: 6; -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.06), 8, 0, 0, 2);");

                VBox sidebar = createSidebar(stage);
                BorderPane rightArea = createRightArea(stage);
                HBox root = new HBox();
                root.setPrefSize(screenWidth, screenHeight);
                root.getChildren().addAll(sidebar, rightArea);

                // Bind sidebar width to stage width
                sidebar.prefWidthProperty().bind(stage.widthProperty().multiply(0.15));
                sidebar.minWidthProperty().set(200);
                sidebar.maxWidthProperty().set(300);

                // Right area takes remaining space
                HBox.setHgrow(rightArea, Priority.ALWAYS);

                return root;
        }

        private VBox createSidebar(Stage stage) {
                VBox sidebar = new VBox();
                sidebar.setPadding(new Insets(5));
                sidebar.setStyle(
                                "-fx-background-color: #ffffff; -fx-border-color: transparent #e6e9ee transparent transparent; -fx-border-radius:8");

                Image logo = new Image(getClass().getResourceAsStream("/img/Logo.png"), 250, 0, true,
                                false);
                ImageView logoView = new ImageView(logo);
                logoView.setPreserveRatio(true);
                logoView.fitWidthProperty().bind(sidebar.widthProperty().subtract(20));

                VBox menu = createSidebarMenu(sidebar);

                Region bottomSpacer = new Region();
                VBox.setVgrow(bottomSpacer, Priority.ALWAYS);

                Button btnCaiDatHeThong = createSidebarButton("Cài đặt hệ thống", "/icon/caidat_icon.svg");

                 Button btnGioiThieu = createSidebarButton("Giới thiệu", "/icon/info.svg");
                 btnGioiThieu.setOnAction(e -> About_GUI.showDialog());
                 btnGioiThieu.prefWidthProperty().bind(sidebar.widthProperty().subtract(10));

                Button btnTaiKhoan = createSidebarButton("Tài khoản", "/icon/taikhoan_icon.svg");
                btnTaiKhoan.setOnAction(e -> {
                        String tenDangNhap = nhanVien != null && nhanVien.getTaiKhoan() != null
                                        ? nhanVien.getTaiKhoan().getTenDangNhap()
                                        : null;
                        contentPane.setCenter(panelLoader.getPanelTaiKhoan(tenDangNhap));
                });
                btnLogout = createSidebarButton("Đăng xuất", "/icon/logout.svg");
                btnLogout.setOnAction(e -> confirmLogout());
                
                // Bind button widths to sidebar width
                btnCaiDatHeThong.prefWidthProperty().bind(sidebar.widthProperty().subtract(10));
                btnLogout.prefWidthProperty().bind(sidebar.widthProperty().subtract(10));

                sidebar.getChildren().addAll(logoView, menu, bottomSpacer, btnGioiThieu, btnTaiKhoan, btnLogout);
                return sidebar;
        }

        private VBox createSidebarMenu(VBox sidebar) {
                VBox menu = new VBox(6);
                menu.setPadding(new Insets(5));

                Button btnTrangChu = createSidebarButton("Dashboard", "/icon/home_icon.svg");
                Button btnPhong = createSidebarButton("Phòng", "/icon/house.svg");

                submenuPhong = createSubmenuPhong(sidebar);
                submenuPhong.setVisible(false);
                submenuPhong.setManaged(false);

                Button btnKhuyenMai = createSidebarButton("Khuyến mãi", "/icon/Deals.svg");
                Button btnThongKe = createSidebarButton("Thống kê", "/icon/thongke_icon.svg");
                Button btnThanhToan = createSidebarButton("Thanh toán", "/icon/thanhtoan_iconn.svg");
                Button btnQuanLyPhong = createSidebarButton("Quản lý phòng", "/icon/house-check.svg");
                Button btnQuanLyDichVu = createSidebarButton("Quản lý dịch vụ", "/icon/dichvu_icon.svg");
                Button btnQuanLyNhanVien = createSidebarButton("Quản lý nhân viên", "/icon/nhanvien_icon.svg");
                Button btnQuanLyKhachHang = createSidebarButton("Quản lý khách hàng", "/icon/person-20-regular.svg");
                Button btnQuanLyHoaDon = createSidebarButton("Quản lý hóa đơn", "/icon/hoadon_icon.svg");
                Button btnCa = createSidebarButton("Ca làm việc", "/icon/eight-oclock.svg");
                

                menu.getChildren().addAll(
                                btnTrangChu, btnPhong, submenuPhong, btnKhuyenMai,
                                btnThongKe, btnThanhToan, btnQuanLyPhong, btnQuanLyDichVu,
                                btnQuanLyNhanVien, btnQuanLyKhachHang, btnQuanLyHoaDon, btnCa);

                // Bind all button widths to sidebar width
                menu.getChildren().stream()
                                .filter(node -> node instanceof Button)
                                .map(node -> (Button) node)
                                .forEach(btn -> btn.prefWidthProperty().bind(sidebar.widthProperty().subtract(10)));

                btnTrangChu.requestFocus();

                // Event handlers
                btnTrangChu.setOnAction(e -> contentPane.setCenter(new DashBoard_GUI()));
                btnKhuyenMai.setOnAction(e -> contentPane.setCenter(panelLoader.getPanelKhuyenMai()));
                btnPhong.setOnAction(e -> toggleSubmenu());
                btnThongKe.setOnAction(e -> contentPane.setCenter(panelLoader.getPanelThongKe()));
                btnQuanLyPhong.setOnAction(e -> contentPane.setCenter(panelLoader.getPanelQuanLiPhong()));
                btnQuanLyNhanVien.setOnAction(e -> contentPane.setCenter(panelLoader.getPanelQuanLiNhanVien()));
                btnQuanLyDichVu.setOnAction(e -> contentPane.setCenter(panelLoader.getPanelQuanLiDichVu()));
                btnQuanLyKhachHang.setOnAction(e -> contentPane.setCenter(panelLoader.getPanelQuanLiKhachHang()));
                btnThanhToan.setOnAction(e -> {
                        if (!caLamViecGUI.hasOpenShift()) {
                                showAlert(Alert.AlertType.WARNING, "Chưa mở ca", "Vui lòng mở ca làm việc trước khi thanh toán!");
                                contentPane.setCenter(caLamViecGUI);
                        } else {
                                contentPane.setCenter(panelLoader.getPanelThanhToan(caLamViecGUI));
                        }
                });
                btnQuanLyHoaDon.setOnAction(e -> contentPane.setCenter(panelLoader.getPanelQuanLyHoaDon()));
                btnCa.setOnAction(e -> contentPane.setCenter(caLamViecGUI));
                btnCa.prefWidthProperty().bind(sidebar.widthProperty().subtract(10));
                
                return menu;
        }

        private VBox createSubmenuPhong(VBox sidebar) {
                VBox submenu = new VBox(3);
                submenu.setPadding(new Insets(0, 0, 0, 15));
                Button btnDatPhong = createSidebarButton("Đặt phòng", "/icon/datphong_icon.svg");
                Button btnNhanPhong = createSidebarButton("Nhận phòng", "/icon/giahan_icon.svg");
                Button btnDoiPhong = createSidebarButton("Đổi phòng", "/icon/doiphong_icon.svg");
                Button btnGiaHanPhong = createSidebarButton("Gia hạn phòng", "/icon/giahan_icon.svg");
                Button btnHuyPhong = createSidebarButton("Hủy phòng", "/icon/cancel.svg");

                btnDatPhong.setOnAction(e -> {
                        if (!caLamViecGUI.hasOpenShift()) {
                                showAlert(Alert.AlertType.WARNING, "Chưa mở ca", "Vui lòng mở ca làm việc trước khi đặt phòng!");
                                contentPane.setCenter(caLamViecGUI);
                        } else {
                                contentPane.setCenter(panelLoader.getPanelDatPhong(caLamViecGUI));
                        }
                });
                btnNhanPhong.setOnAction(e -> {
                        if (!caLamViecGUI.hasOpenShift()) {
                                showAlert(Alert.AlertType.WARNING, "Chưa mở ca", "Vui lòng mở ca làm việc trước khi nhận phòng!");
                                contentPane.setCenter(caLamViecGUI);
                        } else {
                                contentPane.setCenter(panelLoader.getPanelNhanPhong());
                        }
                });
                btnDoiPhong.setOnAction(e -> {
                        if (!caLamViecGUI.hasOpenShift()) {
                                showAlert(Alert.AlertType.WARNING, "Chưa mở ca", "Vui lòng mở ca làm việc trước khi đổi phòng!");
                                contentPane.setCenter(caLamViecGUI);
                        } else {
                                contentPane.setCenter(panelLoader.getPanelDoiPhong());
                        }
                });
                btnGiaHanPhong.setOnAction(e -> {
                        if (!caLamViecGUI.hasOpenShift()) {
                                showAlert(Alert.AlertType.WARNING, "Chưa mở ca", "Vui lòng mở ca làm việc trước khi gia hạn phòng!");
                                contentPane.setCenter(caLamViecGUI);
                        } else {
                                contentPane.setCenter(panelLoader.getPanelGiaHanPhong());
                        }
                });
                btnHuyPhong.setOnAction(e -> {
                        if (!caLamViecGUI.hasOpenShift()) {
                                showAlert(Alert.AlertType.WARNING, "Chưa mở ca", "Vui lòng mở ca làm việc trước khi hủy phòng!");
                                contentPane.setCenter(caLamViecGUI);
                        } else {
                                contentPane.setCenter(panelLoader.getPanelHuyPhong(caLamViecGUI));
                        }
                });

                submenu.getChildren().addAll(btnDatPhong, btnNhanPhong, btnDoiPhong, btnGiaHanPhong, btnHuyPhong);

                // Bind submenu button widths to sidebar width
                submenu.getChildren().stream()
                                .filter(node -> node instanceof Button)
                                .map(node -> (Button) node)
                                .forEach(btn -> btn.prefWidthProperty().bind(sidebar.widthProperty().subtract(25)));

                return submenu;
        }

        private BorderPane createRightArea(Stage stage) {
                BorderPane rightArea = new BorderPane();
                rightArea.setMinWidth(600);
                HBox.setHgrow(rightArea, Priority.ALWAYS);

                HBox header = taoHeader();
                header.setPadding(new Insets(5, 5, 0, 5));
                rightArea.setTop(header);

                StackPane centerStack = new StackPane();
                centerStack.setStyle("-fx-background-color: #f0f2f5;");
                centerStack.setPadding(new Insets(5));
                centerStack.setPrefHeight(screenHeight * 0.875);

                centerStack.getChildren().add(contentPane);

                contentPane.prefWidthProperty().bind(centerStack.widthProperty().subtract(10));
                contentPane.prefHeightProperty().bind(centerStack.heightProperty().subtract(10));

                rightArea.setCenter(centerStack);
                return rightArea;
        }

        private Button createSidebarButton(String text, String url) {
                Button btn = new Button(text, Util.readSimpleSVG(url, null, Color.web("#5D6679")));
                btn.setMinWidth(180);
                btn.setMaxWidth(320);
                btn.setPrefHeight(40);
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
                // Kiểm tra ca làm việc trước khi logout
                if (caLamViecGUI.hasOpenShift()) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Chưa đóng ca");
                        alert.setHeaderText("Bạn chưa đóng ca làm việc!");
                        alert.setContentText("Vui lòng đóng ca làm việc trước khi đăng xuất.");
                        alert.showAndWait();
                        // Chuyển sang màn hình ca làm việc
                        contentPane.setCenter(caLamViecGUI);
                        return;
                }
                
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

        private void showAlert(Alert.AlertType type, String title, String message) {
                Alert alert = new Alert(type);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
        }
        
        private void handleLogout() {
                // Cleanup trước khi đăng xuất
                if (panelLoader != null) {
                        panelLoader.clearCache();
                }
                
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

                header.getChildren().addAll(titleBox, spacer, userBox);
                headerContainer.getChildren().add(header);
                return headerContainer;
        }
}
