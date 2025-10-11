package view;

import java.util.Optional;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.control.*;
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

                // --- Thanh điều hướng bên (Sidebar) - chiếm 1/5 chiều ngang ---
                VBox sidebar = new VBox();
                sidebar.setPadding(new Insets(5, 5, 5, 5));
                sidebar.setStyle(
                                "-fx-background-color: #ffffff; -fx-border-color: transparent #e6e9ee transparent transparent; -fx-border-radius:8");
                sidebar.setPrefWidth(screenWidth * 0.15); // 15% chiều ngang
                sidebar.setMinWidth(screenWidth * 0.15);
                sidebar.setMaxWidth(screenWidth * 0.15);

                // Logo
                Image logo = new Image(getClass().getResourceAsStream("/img/Logo.png"));
                ImageView logoView = new ImageView(logo);
                logoView.setFitWidth(screenWidth * 0.15); // Điều chỉnh logo theo tỉ lệ
                logoView.setPreserveRatio(true);
                logoView.setSmooth(true);
                logoView.setCache(true);

                VBox menu = new VBox(8);
                VBox.setMargin(menu, new Insets(5, 5, 5, 5));
                menu.setPadding(new Insets(5, 5, 5, 5));

                // Các button
                Button btnTrangChu = Util.createSidebarButton("Trang chủ", "/icon/home_icon.svg", screenWidth);
                Button btnPhong = Util.createSidebarButton("Tìm kiếm phòng", "/icon/search.svg", screenWidth);
                Button btnDatPhong = Util.createSidebarButton("Đặt phòng", "/icon/datphong_icon.svg", screenWidth);
                Button btnGiaHanPhong = Util.createSidebarButton("Gia hạn phòng", "/icon/giahan_icon.svg", screenWidth);
                Button btnHuyPhong = Util.createSidebarButton("Hủy phòng", "/icon/cancel.svg", screenWidth);
                Button btnKhuyenMai = Util.createSidebarButton("Khuyến mãi", "/icon/Deals.svg", screenWidth);
                Button btnThongKe = Util.createSidebarButton("Thống kê", "/icon/thongke_icon.svg", screenWidth);
                Button btnThanhToan = Util.createSidebarButton("Thanh toán", "/icon/thanhtoan_iconn.svg", screenWidth);
                Button btnTaiKhoan = Util.createSidebarButton("Tài khoản", "/icon/taikhoan_icon.svg", screenWidth);
                Button btnQuanLyDichVu = Util.createSidebarButton("Quản lý dịch vụ", "/icon/dichvu_icon.svg", screenWidth);
                Button btnQuanLyNhanVien = Util.createSidebarButton("Quản lý nhân viên", "/icon/nhanvien_icon.svg", screenWidth);
                Button btnQuanLyHoaDon = Util.createSidebarButton("Quản lý hóa đơn", "/icon/hoadon_icon.svg", screenWidth);

                menu.getChildren().addAll(
                        btnTrangChu, btnPhong, btnDatPhong, btnGiaHanPhong, btnHuyPhong, btnKhuyenMai,
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
                        Optional <ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                                handleLogout();
                        }
                });

                sidebar.getChildren().addAll(logoView, menu, bottomSpacer, btnCaiDatHeThong, btnLogout);

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
                search2.setFocusTraversable(false);
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

                // --- Các panel mẫu ---
                BorderPane panelTrangChu = new BorderPane();
                BorderPane panelDatPhong = new DatPhong();
                BorderPane panelTimKiemPhong = new TimKiemPhong();

                content.setCenter(panelTrangChu);
                btnTrangChu.setOnAction(e -> content.setCenter(panelDatPhong));
                btnPhong.setOnAction(e -> content.setCenter(panelTimKiemPhong));

                // Đặt header và content vào rightArea
                rightArea.setTop(topHeader);
                rightArea.setCenter(centerStack);

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


        /**
         * Xử lý đăng xuất khỏi ứng dụng.
         *
         * Thay vì phụ thuộc vào trường btnLogout (có thể chưa được khởi tạo do shadowing),
         * phương thức này tìm Stage hiện tại bằng cách kiểm tra các Window đang hiển thị.
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
