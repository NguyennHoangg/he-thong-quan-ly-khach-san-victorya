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
import javafx.stage.Stage;

import utils.*;

public class TrangQuanLy extends Application {
        private Button btnLogout;
        private VBox submenuPhong;
        private boolean isSubmenuVisible = false;
        
        // Cache pre-loaded data
        private double screenWidth;
        private double screenHeight;
        
        // Panel loader utility
        private PanelLoader panelLoader;
        private Stage stageWifi;
        
        // Thông tin người dùng hiện tại
        private model.TaiKhoan currentUser;
        private model.NhanVien currentEmployee;
        
        private Stage getStageWifi() {
                if (stageWifi == null) {
                        Wifi_Modal modalWifi = new Wifi_Modal();
                        stageWifi = modalWifi.getStage();
                }
                return stageWifi;
        }

        @Override
        public void init() throws Exception {
                // Khởi tạo screen dimensions
                javafx.geometry.Rectangle2D screen = javafx.stage.Screen.getPrimary().getBounds();
                screenWidth = screen.getWidth();
                screenHeight = screen.getHeight();
                
                // Khởi tạo PanelLoader
                panelLoader = utils.PanelLoader.getInstance();
                
                // Preload SVG icons để cache
                preloadAllSVGIcons();
        }
        
        /**
         * Pre-load tất cả SVG icons để cache lại, tránh load chậm khi render UI
         */
        private void preloadAllSVGIcons() {
                String[] iconPaths = {
                        "/icon/home_icon.svg",
                        "/icon/house.svg",
                        "/icon/search.svg",
                        "/icon/datphong_icon.svg",
                        "/icon/doiphong_icon.svg",
                        "/icon/giahan_icon.svg",
                        "/icon/cancel.svg",
                        "/icon/Deals.svg",
                        "/icon/thongke_icon.svg",
                        "/icon/thanhtoan_iconn.svg",
                        "/icon/taikhoan_icon.svg",
                        "/icon/house-check.svg",
                        "/icon/dichvu_icon.svg",
                        "/icon/nhanvien_icon.svg",
                        "/icon/hoadon_icon.svg",
                        "/icon/wifi.svg",
                        "/icon/caidat_icon.svg",
                        "/icon/logout.svg",
                        "/icon/person-20-regular.svg",
                        "/icon/bell.svg"
                };
                
                // Load song song bằng parallel stream
                java.util.Arrays.stream(iconPaths).parallel().forEach(path -> {
                        Util.readSimpleSVG(path, null, Color.web("#5D6679"));
                });
        }

        // Constructor để nhận thông tin người dùng
        public TrangQuanLy(model.TaiKhoan taiKhoan, model.NhanVien nhanVien) {
                this.currentUser = taiKhoan;
                this.currentEmployee = nhanVien;
        }
        
        // Constructor mặc định (để tương thích với Application)
        public TrangQuanLy() {
                // Constructor mặc định
        }

        @Override
        public void start(Stage stage) {
                // Khởi tạo panelLoader nếu chưa có (trường hợp gọi từ TrangDangNhap)
                if (panelLoader == null) {
                        panelLoader = PanelLoader.getInstance();
                }
                
                // Khởi tạo screen dimensions nếu chưa có
                if (screenWidth == 0 || screenHeight == 0) {
                        javafx.geometry.Rectangle2D screen = javafx.stage.Screen.getPrimary().getBounds();
                        screenWidth = screen.getWidth();
                        screenHeight = screen.getHeight();
                }
                
                // Lấy kích thước màn hình đã cache
                // javafx.geometry.Rectangle2D screen = Screen.getPrimary().getBounds();
                // double screenWidth = screen.getWidth();
                // double screenHeight = screen.getHeight();

                // Dùng HBox làm root để sidebar chiếm toàn bộ chiều cao
                HBox root = new HBox();
                root.setPrefSize(screenWidth, screenHeight);

                // --- Thanh điều hướng bên (Sidebar) - chiếm 1/5 chiều ngang ---
                VBox sidebar = new VBox();
                sidebar.setPadding(new Insets(5, 5, 5, 5));
                sidebar.setStyle(
                                "-fx-background-color: #ffffff; -fx-border-color: transparent #e6e9ee transparent transparent; -fx-border-radius:8");
                sidebar.setPrefWidth(screenWidth * 0.15); // 15% chiều ngang
                sidebar.setMinWidth(screenWidth * 0.15);
                sidebar.setMaxWidth(screenWidth * 0.15);

                // Logo - load với kích thước cố định để nhanh hơn
                Image logo = new Image(getClass().getResourceAsStream("/img/Logo.png"), 
                        screenWidth * 0.15, 0, true, false); // width, height, preserveRatio, smooth
                ImageView logoView = new ImageView(logo);

                VBox menu = new VBox(8);
                VBox.setMargin(menu, new Insets(5, 5, 5, 5));
                menu.setPadding(new Insets(5, 5, 5, 5));

                // Các button
                Button btnTrangChu = createSidebarButton("Trang chủ", "/icon/home_icon.svg", screenWidth);

                // Button Phòng với submenu
                Button btnPhong = createSidebarButton("Phòng", "/icon/house.svg", screenWidth);

                // Tạo submenu cho Phòng
                submenuPhong = new VBox(4);
                submenuPhong.setPadding(new Insets(0, 0, 0, 20)); // Indent để tạo cảm giác submenu
                submenuPhong.setVisible(false);
                submenuPhong.setManaged(false);

                Button btnTimKiemPhong = createSidebarButton("Tìm kiếm phòng", "/icon/search.svg", screenWidth);
                Button btnDatPhong = createSidebarButton("Đặt phòng", "/icon/datphong_icon.svg", screenWidth);
                Button btnNhanPhong = createSidebarButton("Nhận phòng", "/icon/house-check.svg", screenWidth);
                Button btnDoiPhong = createSidebarButton("Đổi phòng", "/icon/doiphong_icon.svg", screenWidth);
                Button btnGiaHanPhong = createSidebarButton("Gia hạn phòng", "/icon/giahan_icon.svg", screenWidth);
                Button btnHuyPhong = createSidebarButton("Hủy phòng", "/icon/cancel.svg", screenWidth);

                submenuPhong.getChildren().addAll(btnTimKiemPhong, btnDatPhong, btnNhanPhong, btnDoiPhong, btnGiaHanPhong,
                                btnHuyPhong);

                Button btnKhuyenMai = createSidebarButton("Khuyến mãi", "/icon/Deals.svg", screenWidth);
                Button btnThongKe = createSidebarButton("Thống kê", "/icon/thongke_icon.svg", screenWidth);
                Button btnThanhToan = createSidebarButton("Thanh toán", "/icon/thanhtoan_iconn.svg", screenWidth);
                Button btnTaiKhoan = createSidebarButton("Tài khoản", "/icon/taikhoan_icon.svg", screenWidth);
                Button btnQuanLyPhong = createSidebarButton("Quản lý phòng", "/icon/house-check.svg", screenWidth);
                Button btnQuanLyDichVu = createSidebarButton("Quản lý dịch vụ", "/icon/dichvu_icon.svg", screenWidth);
                Button btnQuanLyNhanVien = createSidebarButton("Quản lý nhân viên", "/icon/nhanvien_icon.svg",
                                screenWidth);
                Button btnQuanLyHoaDon = createSidebarButton("Quản lý hóa đơn", "/icon/hoadon_icon.svg", screenWidth);
                Button btnWifi = createSidebarButton("Wifi", "/icon/wifi.svg", screenWidth);

                menu.getChildren().addAll(
                                btnTrangChu, btnPhong, submenuPhong, btnKhuyenMai,
                                btnThongKe, btnThanhToan, btnTaiKhoan, btnQuanLyPhong, btnQuanLyDichVu,
                                btnQuanLyNhanVien,
                                btnQuanLyHoaDon);

                btnTrangChu.requestFocus();

                Region bottomSpacer = new Region();
                VBox.setVgrow(bottomSpacer, Priority.ALWAYS);

                Button btnCaiDatHeThong = createSidebarButton("Cài đặt hệ thống", "/icon/caidat_icon.svg", screenWidth);
                btnLogout = createSidebarButton("Đăng xuất", "/icon/logout.svg", screenWidth);

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

                sidebar.getChildren().addAll(logoView, menu, bottomSpacer, btnWifi, btnCaiDatHeThong, btnLogout);

                // --- Vùng bên phải (chiếm 4/5 chiều ngang) ---
                BorderPane rightArea = new BorderPane();
                rightArea.setPrefWidth(screenWidth * 0.875); // 80% chiều ngang

                // --- Header (chiếm 1/8 chiều dọc của vùng bên phải) ---
                HBox topHeader = new HBox();
                topHeader.setPrefHeight(screenHeight * 0.1); // 12.5% chiều dọc
                topHeader.setPadding(new Insets(5, 5, 5, 5));
                topHeader.setStyle("-fx-background-color: #f0f2f5;");

                BorderPane headerCard = new BorderPane();
                headerCard.setPadding(new Insets(5, 5, 5, 5));
                headerCard.setStyle(
                                "-fx-background-color: white; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.03), 6, 0, 0, 1);");
                headerCard.setPrefHeight(screenHeight * 0.08); // Chiều cao header card

                // Center của headerCard: ô tìm kiếm
                HBox centerBox2 = new HBox();
                centerBox2.setAlignment(Pos.CENTER_LEFT);
                centerBox2.setPadding(new Insets(5, 5, 5, 5));
                TextField search2 = new TextField();
                search2.setPrefWidth(screenWidth * 0.3); // 30% chiều ngang màn hình
                search2.setPromptText("Nhập số phòng hoặc CCCD khách hàng");
                search2.setStyle(
                                "-fx-background-radius: 8; -fx-background-color: #f7fafc; -fx-border-radius: 8; -fx-padding: 8 12 8 12;");
                centerBox2.getChildren().add(search2);
                headerCard.setCenter(centerBox2);

                // Khi người dùng click vào bất kỳ Button nào (ở sidebar hoặc nơi khác),
                // bỏ focus khỏi TextField bằng cách requestFocus cho headerCard.
                // Gắn handler sau khi scene được tạo.
                headerCard.sceneProperty().addListener((obs, oldScene, newScene) -> {
                        if (newScene != null) {
                                newScene.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, ev -> {
                                        javafx.scene.Node node = (javafx.scene.Node) ev.getTarget();
                                        // leo lên cây node để kiểm tra xem có phải click vào Button hay không
                                        while (node != null && !(node instanceof Button)) {
                                                node = node.getParent();
                                        }
                                        if (node instanceof Button) {
                                                // request focus lên headerCard => search2 sẽ mất focus
                                                headerCard.requestFocus();
                                        }
                                });
                        }
                });

                // Right của headerCard: các icon
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

                // --- Content (chiếm 7/8 chiều dọc của vùng bên phải) ---
                StackPane centerStack = new StackPane();
                centerStack.setStyle("-fx-background-color: #f0f2f5;");
                centerStack.setPadding(new Insets(5, 5, 5, 5));
                centerStack.setPrefHeight(screenHeight * 0.875); // 87.5% chiều dọc

                BorderPane content = new BorderPane();
                content.setPadding(new Insets(5, 5, 5, 5));
                content.setStyle(
                                "-fx-background-color: #ffffffff; -fx-border-radius: 6; -fx-background-radius: 6; -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.06), 8, 0, 0, 2);");

                Label contentLabel = new Label("Nội dung trang chính hiển thị ở đây.");
                contentLabel.setPadding(new Insets(5, 5, 5, 5));
                content.setCenter(contentLabel);

                centerStack.getChildren().add(content);

                // Binding content size với centerStack
                content.prefWidthProperty().bind(centerStack.widthProperty().subtract(36));
                content.prefHeightProperty().bind(centerStack.heightProperty().subtract(36));

                // Set panel trang chủ làm mặc định
                content.setCenter(panelLoader.getPanelTrangChu());

                // Event handlers - sử dụng PanelLoader
                btnTrangChu.setOnAction(e -> content.setCenter(panelLoader.getPanelTrangChu()));
                btnKhuyenMai.setOnAction(e -> content.setCenter(panelLoader.getPanelKhuyenMai()));

                // Xử lý toggle submenu cho button Phòng
                btnPhong.setOnAction(e -> toggleSubmenu());

                // Xử lý các submenu button - lazy load từ PanelLoader
                btnTimKiemPhong.setOnAction(e -> content.setCenter(panelLoader.getPanelTimKiem()));
                btnDatPhong.setOnAction(e -> content.setCenter(panelLoader.getPanelDatPhong()));
                btnNhanPhong.setOnAction(e -> content.setCenter(panelLoader.getPanelNhanPhong()));
                btnDoiPhong.setOnAction(e -> content.setCenter(panelLoader.getPanelDoiPhong()));
                btnGiaHanPhong.setOnAction(e -> content.setCenter(panelLoader.getPanelGiaHanPhong()));
                btnHuyPhong.setOnAction(e -> content.setCenter(panelLoader.getPanelHuyPhong()));
                btnTaiKhoan.setOnAction(e -> {
                        if (currentUser != null && currentEmployee != null) {
                                content.setCenter(panelLoader.getPanelTaiKhoan(currentUser, currentEmployee));
                        } else {
                                content.setCenter(panelLoader.getPanelTaiKhoan());
                        }
                });
                btnCaiDatHeThong.setOnAction(e -> content.setCenter(panelLoader.getPanelCauHinh()));
                btnQuanLyPhong.setOnAction(e -> content.setCenter(panelLoader.getPanelQuanLiPhong()));
                btnQuanLyNhanVien.setOnAction(e -> content.setCenter(panelLoader.getPanelQuanLiNhanVien()));
                btnQuanLyDichVu.setOnAction(e -> content.setCenter(panelLoader.getPanelQuanLiDichVu()));
                btnThanhToan.setOnAction(e -> content.setCenter(panelLoader.getPanelThanhToan()));
                btnQuanLyHoaDon.setOnAction(e->content.setCenter(panelLoader.getPanelQuanLyHoaDon()));
                btnWifi.setOnAction(e -> getStageWifi().showAndWait());

                // Đặt header và content vào rightArea
             
                rightArea.setCenter(centerStack);

                // Thêm sidebar và rightArea vào root
                root.getChildren().addAll(sidebar, rightArea);

                Scene scene = new Scene(root, screenWidth, screenHeight);
                scene.getStylesheets().add(getClass().getResource("/css/TrangQuanLy.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("Trang Quản Lý - Victorya Hotel");
                
                // Set fullscreen để tương thích với mọi màn hình
                
                // Hoặc dùng maximized nếu muốn vẫn thấy taskbar
                // stage.setMaximized(true);
                
                // Đợi UI render hoàn tất rồi mới hiển thị stage
                javafx.application.Platform.runLater(() -> {
                        stage.show();
                        
                        // Preload panels sau khi stage đã hiển thị (để có đúng JavaFX context)
                        preloadPanelsInBackground();
                });
        }

        /**
         * Toggle hiển thị/ẩn submenu phòng
         */
        private void toggleSubmenu() {
                isSubmenuVisible = !isSubmenuVisible;
                submenuPhong.setVisible(isSubmenuVisible);
                submenuPhong.setManaged(isSubmenuVisible);
        }

        /**
         * Tạo và cấu hình một nút cho sidebar (có thể chỉ icon hoặc icon + text).
         * Mặc định chỉ đánh dấu nút "Trang chủ" là active; các nút khác sẽ không có
         * class "active"
         * cho đến khi người dùng click vào chúng.
         *
         * @param text        Văn bản hiển thị trên nút (có thể là null để chỉ hiện
         *                    icon)
         * @param url         Đường dẫn resource tới file SVG của icon
         * @param screenWidth Chiều ngang màn hình, dùng để tính kích thước tương đối
         * @return Button đã cấu hình sẵn icon, kích thước và kiểu hiển thị
         */
        private Button createSidebarButton(String text, String url, double screenWidth) {
                Button btn = new Button(text, Util.readSimpleSVG(url, null, Color.web("#5D6679")));

                // Kích thước theo tỉ lệ màn hình (điều chỉnh để phù hợp với sidebar)
                btn.setPrefWidth(screenWidth * 0.16);
                btn.setPrefHeight(44);

                // Padding bên trong, khoảng cách giữa icon và text, căn trái
                btn.setPadding(new Insets(5, 5, 5, 5));
                btn.setGraphicTextGap(12);
                btn.setAlignment(Pos.CENTER_LEFT);
                btn.setContentDisplay(javafx.scene.control.ContentDisplay.LEFT);

                // Chỉ thêm class "button" mặc định. Class "active" chỉ thêm cho nút "Trang chủ"
                // ban đầu
                btn.getStyleClass().add("button");
                if (text != null && "Trang chủ".equalsIgnoreCase(text.trim())) {
                        btn.getStyleClass().add("active");
                }

                btn.setFocusTraversable(false);

                // Khi click: bỏ active của các nút cùng nhóm rồi đánh dấu nút này là active
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
                                // Fallback: tìm theo scene (các nút có class "button")
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
        
        /**
         * Preload các panels trong background sau khi Stage đã hiển thị
         */
        private void preloadPanelsInBackground() {
                new Thread(() -> {
                        try {
                                // Preload panels trên JavaFX thread để có đúng context
                                javafx.application.Platform.runLater(() -> {
                                        panelLoader.preloadRoomPanels((progress, message) -> {
                                                // Không cần notify vì không có splash screen
                                        });
                                });
                                
                                // Đợi một chút để panels load
                                Thread.sleep(3000);
                                
                        } catch (Exception e) {
                                System.err.println("❌ Lỗi khi preload panels: " + e.getMessage());
                                e.printStackTrace();
                        }
                }, "PanelPreloader").start();
        }

        /**
         * Xử lý đăng xuất khỏi ứng dụng.
         *
         * Thay vì phụ thuộc vào trường btnLogout (có thể chưa được khởi tạo do
         * shadowing),
         * phương thức này tìm Stage hiện tại bằng cách kiểm tra các Window đang hiển
         * thị.
         * Nếu không tìm thấy Stage đang hiển thị, tạo một Stage mới làm fallback.
         */
        private void handleLogout() {
                // Tạo màn hình đăng nhập mới
                TrangDangNhap trangDangNhap = new TrangDangNhap();

                // Cố gắng tìm Stage đang hiển thị (tránh phụ thuộc vào btnLogout có thể null)
                java.util.Optional<javafx.stage.Window> optWindow = javafx.stage.Window.getWindows()
                                .stream()
                                .filter(javafx.stage.Window::isShowing)
                                .findFirst();

                if (optWindow.isPresent()) {
                        // Nếu tìm thấy window đang hiển thị, dùng nó làm Stage hiện tại
                        Stage current = (Stage) optWindow.get();
                        trangDangNhap.start(current);
                } else {
                        // Nếu không tìm thấy, tạo Stage mới làm fallback
                        Stage newStage = new Stage();
                        trangDangNhap.start(newStage);
                }
        }
}