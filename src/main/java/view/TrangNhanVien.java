package view;



import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import utils.*;

public class TrangNhanVien extends Application {

        @Override
        public void start(Stage stage) {
                // Dùng HBox làm root để sidebar chiếm toàn bộ chiều cao (bao gồm cả vùng
                // header)
                HBox root = new HBox();
                root.setPrefHeight(980);
                root.setPadding(new Insets(20, 0, 10, 0));

                // --- Thanh điều hướng bên (Sidebar) - chiếm toàn bộ chiều cao ---
                VBox sidebar = new VBox();
                sidebar.setPadding(new Insets(18)); // padding bên trong sidebar
                sidebar.setStyle(
                                "-fx-background-color: #ffffff; -fx-border-color: transparent #e6e9ee transparent transparent; -fx-border-radius:8");
                sidebar.setPrefWidth(250);

                // Đọc ảnh logo dưới dạng javafx.scene.image.Image
                Image logo = new Image(getClass().getResourceAsStream("/img/Logo.png"));
                ImageView logoView = new ImageView(logo);
                logoView.setFitWidth(120);
                logoView.setPreserveRatio(true);
                logoView.setSmooth(true);
                logoView.setCache(true);

                VBox menu = new VBox(8);
                // Thêm khoảng cách 50px giữa logo và menu
                VBox.setMargin(menu, new Insets(50, 0, 0, 0));
                menu.setPadding(new Insets(8, 0, 8, 0));

                //Các button
                Button btnTrangChu = createSidebarButton("Trang chủ", "/icon/home_icon.svg");
                Button btnPhong = createSidebarButton("Tìm kiếm phòng", "/icon/search.svg");
                Button btnDatPhong = createSidebarButton("Đặt phòng", "/icon/datphong_icon.svg");
                Button btnGiaHanPhong = createSidebarButton("Gia hạn phòng", "/icon/giahan_icon.svg");
                Button btnHuyPhong = createSidebarButton("Hủy phòng", "/icon/cancel.svg");
               
                Button btnThanhToan = createSidebarButton("Thanh toán", "/icon/thanhtoan_iconn.svg");
                Button btnTaiKhoan = createSidebarButton("Tài khoản", "/icon/taikhoan_icon.svg");
                

                menu.getChildren().addAll(
                        btnTrangChu, btnPhong, btnDatPhong, btnGiaHanPhong, btnHuyPhong, btnThanhToan, btnTaiKhoan
                );

                Region bottomSpacer = new Region();
                VBox.setVgrow(bottomSpacer, Priority.ALWAYS);

                // Nút đăng xuất đặt ở cuối sidebar
                Button btnCaiDatHeThong = createSidebarButton("Cài đặt hệ thống", "/icon/caidat_icon.svg");
                Button btnLogout = createSidebarButton("Đăng xuất", "/icon/logout.svg");

                sidebar.getChildren().addAll(logoView, menu, bottomSpacer,btnCaiDatHeThong, btnLogout);

                // --- Vùng nội dung chính (center) ---
                // centerStack là nền xám của trang; content (thẻ trắng) nằm ở trong để hiển thị
                // nội dung
                StackPane centerStack = new StackPane();
                centerStack.setStyle("-fx-background-color: #f0f2f5;"); // nền xám bên ngoài chứa các thẻ trắng
                centerStack.setPadding(new Insets(18));

                // White card chứa nội dung thực tế (giao diện: các panel nền trắng trên nền
                // xám)
                BorderPane content = new BorderPane();
                content.setPadding(new Insets(5));
                content.setStyle(
                                "-fx-background-color: #ffffffff; -fx-border-radius: 6; -fx-background-radius: 6; -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.06), 8, 0, 0, 2);");
                content.setPrefSize(760, 520);

                Label cardTitle = new Label("Tổng quan");
                cardTitle.setFont(Font.font(20));

                content.setTop(cardTitle);

                // Ví dụ các panel con bên trong content (thay bằng nội dung thật khi cần)
                Label contentLabel = new Label("Nội dung trang chính hiển thị ở đây.");
                contentLabel.setPadding(new Insets(10, 0, 0, 0));
                content.setCenter(contentLabel);

                centerStack.getChildren().add(content);

                // --- Các panel mẫu để chuyển đổi (ví dụ) ---
                BorderPane panelTrangChu = new BorderPane();
                BorderPane panelDatPhong = new DatPhong();

                // Thêm các panel mẫu vào content; khởi tạo hiển thị panelTrangChu
                content.setCenter(panelTrangChu);

                // Khi nhấn các nút bên menu sẽ thay center của content bằng panel tương ứng
                btnTrangChu.setOnAction(e -> content.setCenter(panelDatPhong));

                // Tạo header sau khi đã có content để dễ căn chỉnh header trùng với vùng
                // content
                HBox topHeader = new HBox();
                topHeader.setPrefWidth(1400);
                topHeader.setPrefHeight(130);
                topHeader.setPadding(new Insets(0, 0, 0, 15));
                topHeader.setStyle("-fx-background-color: transparent;");

                // headerCard: hộp trắng bo góc chứa ô tìm kiếm + avatar, được căn theo chiều
                // rộng content
                BorderPane headerCard = new BorderPane();
                headerCard.setPadding(new Insets(5));
                headerCard.setStyle(
                                "-fx-background-color: white; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.03), 6, 0, 0, 1);");
                headerCard.setPrefHeight(72);
                // Ràng buộc headerCard có chiều rộng bằng content để header có chiều dài đúng
                // bằng content
                headerCard.setPrefWidth(1420);
                ;

                // Center của headerCard: ô tìm kiếm (Search)
                HBox centerBox2 = new HBox();
                centerBox2.setAlignment(Pos.CENTER_LEFT);
                centerBox2.setPadding(new Insets(6));
                TextField search2 = new TextField();
                search2.setPrefWidth(500);
                search2.setPromptText("Nhập số phòng hoặc CCCD khách hàng");
                search2.setStyle(
                                "-fx-background-radius: 8; -fx-background-color: #f7fafc; -fx-border-radius: 8; -fx-padding: 8 12 8 12;");
                centerBox2.getChildren().add(search2);
                headerCard.setCenter(centerBox2);

                // Right của headerCard: các icon (thông báo, avatar)
                HBox rightBox2 = new HBox(10);
                rightBox2.setAlignment(Pos.CENTER_RIGHT);
                Button btnAcc = createSidebarButton(null, "/icon/person-20-regular.svg");
                btnAcc.setPrefWidth(50);
                Button btnThongBao = createSidebarButton(null, "/icon/bell.svg");
                btnThongBao.setPrefWidth(20);
                rightBox2.getChildren().addAll(btnThongBao, btnAcc);
                headerCard.setRight(rightBox2);

                topHeader.getChildren().add(headerCard);

                // Tạo vùng bên phải chứa header và content (không bao gồm sidebar)
                BorderPane rightArea = new BorderPane();
                rightArea.setTop(topHeader);
                rightArea.setCenter(centerStack);

                // Thêm sidebar và rightArea vào root HBox
                root.getChildren().addAll(sidebar, rightArea);
                // Cho rightArea chiếm phần còn lại của không gian ngang
                HBox.setHgrow(rightArea, Priority.ALWAYS);

                // Lấy kích thước màn hình
                javafx.geometry.Rectangle2D screen = Screen.getPrimary().getBounds();
                double width = screen.getWidth();
                double height = screen.getHeight();

                Scene scene = new Scene(root, width, height);
                scene.getStylesheets().add(getClass().getResource("/css/TrangQuanLy.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("Trang Quản Lý - Victorya");
                stage.setX(screen.getMinX());
                stage.setY(screen.getMinY());
                stage.setWidth(width);
                stage.setHeight(height);
                stage.setMaximized(true); // Đặt cửa sổ ở chế độ toàn màn hình
                stage.show();

                // Làm cho sidebar chiếm toàn bộ chiều cao màn hình (binding với scene height)
                sidebar.prefHeightProperty().bind(scene.heightProperty());
                sidebar.maxHeightProperty().bind(scene.heightProperty());
                sidebar.setFillWidth(true);

                stage.show();
        }

        private Button createSidebarButton(String text, String url) {

                Button b = new Button(text, Util.readSimpleSVG(url, null, Color.web("#5D6679")));
                // ensure vertical centering and comfortable hit area
                b.setPrefWidth(200);
                b.setPrefHeight(44);
                b.setPadding(new Insets(8, 12, 8, 16));
                b.setGraphicTextGap(12);
                b.setAlignment(Pos.CENTER_LEFT);
                // Force the button to render graphic and text side-by-side centered vertically
                b.setContentDisplay(javafx.scene.control.ContentDisplay.LEFT);
                b.getStyleClass().add(".button");
                return b;
        }
}
