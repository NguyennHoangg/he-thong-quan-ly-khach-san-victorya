package view;

import java.time.LocalDate;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.LoaiPhong;
import model.Phong;

public class DoiPhong_GUI extends BorderPane {
        private TextField txtNhapCCCD;
        private Button btnTimKiem;
        private Label lblTongTienPhongGiaTri;
        private Label lblTongTienCocGiaTri;
        private Label lblSoPhongGiaTri;
        private Button btnXacNhan;

        public DoiPhong_GUI() {
                this.setPadding(new Insets(20));
                this.setStyle("-fx-background-color: #f0f2f5;");

                Label lblTieuDe = new Label("Chọn phòng cần đổi");
                lblTieuDe.setStyle("-fx-padding: 10; -fx-font-weight: bold;");
                lblTieuDe.setFont(Font.font("System", FontWeight.SEMI_BOLD, 22));
                HBox containChinh = new HBox();
                VBox containTrai = new VBox();
                VBox containPhai = new VBox();

                containChinh.getStylesheets().add(
                                getClass().getResource("/css/Button.css").toExternalForm());

                VBox timKiem = taoPhanTimKiem();
                containTrai.getChildren().addAll(lblTieuDe, timKiem, taoBang(), chonPhongDoi(),
                                taoHangPhongDaChon(phongDaChon));

                btnXacNhan = new Button("Xác nhận");
                btnXacNhan.setPrefWidth(600);
                btnXacNhan.setPrefHeight(120);
                btnXacNhan.getStyleClass().add("btn");
                containPhai.getChildren().addAll(taoPhongBanDau(), taoPhongSau(), taoPhiChecnhLech(), btnXacNhan);
                // Cách giữa các ô
                containPhai.setSpacing(20);

                // Padding cho toàn bộ vùng bên phải
                containPhai.setPadding(new Insets(10, 20, 10, 30));
                containChinh.getChildren().addAll(containTrai, containPhai);
                this.setCenter(containChinh);
        }

        // ======== Dữ liệu mẫu ========
        LoaiPhong vip = new LoaiPhong("LP01", "VIP", 300000, LocalDate.now());
        LoaiPhong thuong = new LoaiPhong("LP02", "Phòng thường", 400000, LocalDate.now());

        ObservableList<Phong> data = FXCollections.observableArrayList(
                        new Phong("P01", "#001", vip, "Đang thuê", 1),
                        new Phong("P02", "#002", thuong, "Trống", 2),
                        new Phong("P03", "#003", vip, "Đang dọn dẹp", 1));
        Phong phongDaChon = new Phong("P03", "#003", vip, "Đang dọn dẹp", 1);

        private VBox taoPhanTimKiem() {
                VBox container = new VBox(15);
                container.setStyle("-fx-padding: 10;");
                HBox timKiemBox = new HBox(10);
                timKiemBox.setAlignment(Pos.CENTER_LEFT);

                txtNhapCCCD = new TextField();
                txtNhapCCCD.setPromptText("Nhập CCCD");
                txtNhapCCCD.setPrefWidth(350);
                txtNhapCCCD.setPrefHeight(40);
                txtNhapCCCD.setStyle(
                                "-fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #d1d5db; -fx-background-color: white; -fx-padding: 0 15;");

                btnTimKiem = new Button("Tìm kiếm");
                btnTimKiem.setPrefHeight(40);
                btnTimKiem.setPrefWidth(110);
                btnTimKiem.getStyleClass().add("btn");

                timKiemBox.getChildren().addAll(txtNhapCCCD, btnTimKiem);
                container.getChildren().add(timKiemBox);

                return container;
        }

        private VBox taoPhongBanDau() {
                VBox container = new VBox(15);
                container.setPadding(new Insets(20));
                container.setMinSize(600, 180);
                container.setMaxSize(600, 160);

                container.setStyle(
                                "-fx-border-color: #2CD9FF;" + // màu viền
                                                "-fx-border-width: 1;" + // độ dày viền (px)
                                                "-fx-border-radius: 14;" + // bo góc (tùy chọn)
                                                "-fx-background-radius: 14;" + // màu nền bên trong
                                                "-fx-background-color: white;");
                Label lblTieuDe = new Label("Phòng ban đầu");
                lblTieuDe.setFont(Font.font("System", FontWeight.SEMI_BOLD, 15));
                lblTieuDe.setStyle("-fx-text-fill: #2CD9FF;");

                VBox chiTietBox = new VBox(12);

                // Hàng số phòng
                HBox soPhongBox = new HBox();
                Label lblSoPhong = new Label("Số phòng: ");
                lblSoPhongGiaTri = new Label("001");
                Region spacer1 = new Region();
                HBox.setHgrow(spacer1, Priority.ALWAYS); // Tràn hết ra nếu ô còn khoảng trống
                soPhongBox.getChildren().addAll(lblSoPhong, spacer1, lblSoPhongGiaTri);

                // Hàng loại phòng
                HBox loaiPhongBox = new HBox();
                Label lblLoai = new Label("Loại phòng");
                Label lblTenLoaiPhong = new Label("Thường");
                Region spacer2 = new Region();
                HBox.setHgrow(spacer2, Priority.ALWAYS);

                loaiPhongBox.getChildren().addAll(lblLoai, spacer2, lblTenLoaiPhong);

                // Hàng đơn vị tính
                HBox donViBox = new HBox();
                Label lblDonVi = new Label("Đơn vị tính: ");
                Label lblDonViGiaTri = new Label("vnđ/ngày");
                Region spacer3 = new Region();
                HBox.setHgrow(spacer3, Priority.ALWAYS);

                donViBox.getChildren().addAll(lblDonVi, spacer3, lblDonViGiaTri);

                // Hàng giá
                HBox totalBox = new HBox();
                Label lblTongTienCoc = new Label("Giá phòng");
                lblTongTienCoc.setFont(Font.font("System", FontWeight.BOLD, 14));
                lblTongTienCocGiaTri = new Label("7.000.000");
                lblTongTienCocGiaTri.setFont(Font.font("System", FontWeight.BOLD, 14));
                Region spacer4 = new Region();// Khối căn khoảng cách
                HBox.setHgrow(spacer4, Priority.ALWAYS);

                totalBox.getChildren().addAll(lblTongTienCoc, spacer4, lblTongTienCocGiaTri);

                chiTietBox.getChildren().addAll(soPhongBox, loaiPhongBox, donViBox, totalBox);
                container.getChildren().addAll(lblTieuDe, chiTietBox);

                return container;
        }

        private VBox taoPhongSau() {
                VBox container = new VBox(15);
                container.setPadding(new Insets(20));
                container.setMinSize(600, 180);
                container.setMaxSize(600, 160);

                container.setStyle(
                                "-fx-border-color: #14AE5C;" + // màu viền
                                                "-fx-border-width: 1;" + // độ dày viền (px)
                                                "-fx-border-radius: 14;" + // bo góc (tùy chọn)
                                                "-fx-background-radius: 14;" + // màu nền bên trong
                                                "-fx-background-color: white;");
                Label lblTieuDe = new Label("Phòng đổi sang");
                lblTieuDe.setFont(Font.font("System", FontWeight.SEMI_BOLD, 15));
                lblTieuDe.setStyle("-fx-text-fill: #14AE5C;");

                VBox chiTietBox = new VBox(12);

                // Hàng số phòng
                HBox soPhongBox = new HBox();
                Label lblSoPhong = new Label("Số phòng: ");
                lblSoPhongGiaTri = new Label("001");
                Region spacer1 = new Region();
                HBox.setHgrow(spacer1, Priority.ALWAYS); // Tràn hết ra nếu ô còn khoảng trống
                soPhongBox.getChildren().addAll(lblSoPhong, spacer1, lblSoPhongGiaTri);

                // Hàng loại phòng
                HBox loaiPhongBox = new HBox();
                Label lblLoai = new Label("Loại phòng");
                Label lblTenLoaiPhong = new Label("Thường");
                Region spacer2 = new Region();
                HBox.setHgrow(spacer2, Priority.ALWAYS);

                loaiPhongBox.getChildren().addAll(lblLoai, spacer2, lblTenLoaiPhong);

                // Hàng đơn vị tính
                HBox donViBox = new HBox();
                Label lblDonVi = new Label("Đơn vị tính: ");
                Label lblDonViGiaTri = new Label("vnđ/ngày");
                Region spacer3 = new Region();
                HBox.setHgrow(spacer3, Priority.ALWAYS);

                donViBox.getChildren().addAll(lblDonVi, spacer3, lblDonViGiaTri);

                // Hàng giá
                HBox totalBox = new HBox();
                Label lblTongTienCoc = new Label("Giá phòng");
                lblTongTienCoc.setFont(Font.font("System", FontWeight.BOLD, 14));
                lblTongTienCocGiaTri = new Label("7.000.000");
                lblTongTienCocGiaTri.setFont(Font.font("System", FontWeight.BOLD, 14));
                Region spacer4 = new Region();// Khối căn khoảng cách
                HBox.setHgrow(spacer4, Priority.ALWAYS);

                totalBox.getChildren().addAll(lblTongTienCoc, spacer4, lblTongTienCocGiaTri);

                chiTietBox.getChildren().addAll(soPhongBox, loaiPhongBox, donViBox, totalBox);
                container.getChildren().addAll(lblTieuDe, chiTietBox);

                return container;
        }

        private VBox taoPhiChecnhLech() {
                VBox container = new VBox(15);
                container.setPadding(new Insets(20));
                container.setMinSize(600, 180);
                container.setMaxSize(600, 160);

                container.setStyle(
                                "-fx-border-color: #EC221F;" + // màu viền
                                                "-fx-border-width: 1;" + // độ dày viền (px)
                                                "-fx-border-radius: 14;" + // bo góc (tùy chọn)
                                                "-fx-background-radius: 14;" + // màu nền bên trong
                                                "-fx-background-color: white;");
                Label lblTieuDe = new Label("Phí chênh lệch");
                lblTieuDe.setFont(Font.font("System", FontWeight.SEMI_BOLD, 15));
                lblTieuDe.setStyle("-fx-text-fill: #EC221F;");

                VBox chiTietBox = new VBox(12);

                // Tổng tiền phòng
                HBox tongTienBox = new HBox();
                Label lblTongTienTitle = new Label("Tổng tiền phòng");
                lblTongTienPhongGiaTri = new Label("0  VND");
                Region spacer1 = new Region();
                HBox.setHgrow(spacer1, Priority.ALWAYS); // Tràn hết ra nếu ô còn khoảng trống
                tongTienBox.getChildren().addAll(lblTongTienTitle, spacer1, lblTongTienPhongGiaTri);

                // Cọc
                HBox cocBox = new HBox();
                Label lblCoc = new Label("Cọc");
                Label lblCocGiaTri = new Label("30%");
                Region spacer2 = new Region();
                HBox.setHgrow(spacer2, Priority.ALWAYS);

                cocBox.getChildren().addAll(lblCoc, spacer2, lblCocGiaTri);

                Separator sep = new Separator(); // đường dọc ngăn cách

                // Tổng tiền cọc
                HBox totalBox = new HBox();
                Label lblTongTienCoc = new Label("Tổng tiền cọc");
                lblTongTienCoc.setFont(Font.font("System", FontWeight.BOLD, 14));
                lblTongTienCocGiaTri = new Label("0  VND");
                lblTongTienCocGiaTri.setFont(Font.font("System", FontWeight.BOLD, 14));
                // Khối căn khoảng cách
                Region spacer3 = new Region();
                HBox.setHgrow(spacer3, Priority.ALWAYS);

                totalBox.getChildren().addAll(lblTongTienCoc, spacer3, lblTongTienCocGiaTri);

                chiTietBox.getChildren().addAll(tongTienBox, cocBox, sep, totalBox);
                container.getChildren().addAll(lblTieuDe, chiTietBox);

                return container;
        }

        private ScrollPane taoBang() {
                TableView<Phong> table = new TableView<>();

                // ======== Cột ========
                TableColumn<Phong, String> colSoPhong = new TableColumn<>("Số phòng");
                colSoPhong.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSoPhong()));
                colSoPhong.setPrefWidth(100);

                TableColumn<Phong, String> colLoaiPhong = new TableColumn<>("Loại phòng");
                colLoaiPhong.setCellValueFactory(
                                data -> new SimpleStringProperty(data.getValue().getLoaiPhong().getTenLoaiPhong()));
                colLoaiPhong.setPrefWidth(150);

                TableColumn<Phong, String> colTang = new TableColumn<>("Tầng");
                colTang.setCellValueFactory(
                                data -> new SimpleStringProperty("Tầng " + data.getValue().getTang()));
                colTang.setPrefWidth(100);

                TableColumn<Phong, String> colGiaNgay = new TableColumn<>("Giá theo ngày");
                colGiaNgay.setCellValueFactory(
                                data -> new SimpleStringProperty(
                                                String.format("%,.0f VND", data.getValue().getLoaiPhong().getGia())));
                colGiaNgay.setPrefWidth(150);

                TableColumn<Phong, String> colGiaTuan = new TableColumn<>("Giá theo tuần");
                colGiaTuan.setCellValueFactory(
                                data -> new SimpleStringProperty(String.format("%,.0f VND",
                                                data.getValue().getLoaiPhong().getGia() * 10)));
                colGiaTuan.setPrefWidth(150);

                table.getColumns().add(colSoPhong);
                table.getColumns().add(colLoaiPhong);
                table.getColumns().add(colTang);
                table.getColumns().add(colGiaNgay);
                table.getColumns().add(colGiaTuan);

                table.setItems(data);

                table.setPrefWidth(700);
                table.setPrefHeight(300);
                // JavaFX 17 không còn hằng số ALL_COLUMNS; dùng CONSTRAINED_RESIZE_POLICY
                table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

                table.getStylesheets().add(getClass().getResource("/css/Table.css").toExternalForm());

                table.setRowFactory(tv -> {
                        TableRow<Phong> row = new TableRow<>() {
                                @Override
                                protected void updateItem(Phong item, boolean empty) {
                                        super.updateItem(item, empty);
                                        updateRowStyle();
                                }

                                private void updateRowStyle() {
                                        if (isEmpty()) {
                                                setStyle("");
                                        } else if (isSelected()) {
                                                setStyle("-fx-background-color: #E8F1FD; -fx-text-fill: black;");
                                        } else {
                                                setStyle("-fx-background-color: white; -fx-text-fill: black;");
                                        }
                                }
                        };

                        row.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                                if (!row.isEmpty()) {
                                        if (isNowSelected) {
                                                row.setStyle("-fx-background-color: #E8F1FD; -fx-text-fill: black;");
                                        } else {
                                                row.setStyle("-fx-background-color: white; -fx-text-fill: black;");
                                        }
                                }
                        });

                        return row;
                });

                ScrollPane scrollPane = new ScrollPane(table);
                scrollPane.setFitToWidth(true);
                scrollPane.setFitToHeight(true);
                scrollPane.getStyleClass().add("table-scroll-pane");
                scrollPane.getStylesheets().add(getClass().getResource("/css/Table.css").toExternalForm());

                return scrollPane;
        }

        private HBox chonPhongDoi() {
                HBox container = new HBox(15);
                container.setAlignment(Pos.CENTER_LEFT);
                container.setPadding(new Insets(10));

                Label lblChonDoi = new Label("Chọn phòng cần đổi");
                lblChonDoi.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 22));
                lblChonDoi.setStyle("-fx-padding: 10; -fx-font-weight: bold;");
                lblChonDoi.setStyle("-fx-text-fill: #1a1a1a;");

                Button btnChon = new Button("Chọn");
                btnChon.setPrefSize(70, 25);
                btnChon.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

                // ✅ Gán class CSS
                btnChon.getStyleClass().add("btn");
                container.getChildren().addAll(lblChonDoi, btnChon);
                return container;
        }

        private HBox taoHangPhongDaChon(Phong phong) {
                // ======== Container chính ========
                HBox container = new HBox();
                container.setPrefHeight(60);
                container.setAlignment(Pos.CENTER_LEFT);
                container.setStyle("""
                                -fx-background-color: white;
                                -fx-border-color: #d0d7de;
                                -fx-border-radius: 8;
                                -fx-background-radius: 8;
                                -fx-padding: 15 20;
                                """);

                // ======== Các label cho từng cột ========

                // Số phòng
                Label lblSoPhong = new Label(phong.getSoPhong());
                lblSoPhong.setPrefWidth(100);
                lblSoPhong.setStyle("""
                                -fx-font-family: 'Segoe UI';
                                -fx-font-size: 14px;
                                -fx-text-fill: #1a1a1a;
                                -fx-font-weight: 600;
                                """);

                // Loại phòng
                Label lblLoaiPhong = new Label(phong.getLoaiPhong().getTenLoaiPhong());
                lblLoaiPhong.setPrefWidth(150);
                lblLoaiPhong.setStyle("""
                                -fx-font-family: 'Segoe UI';
                                -fx-font-size: 14px;
                                -fx-text-fill: #4a4a4a;
                                """);

                // Tầng
                Label lblTang = new Label("Tầng" + phong.getTang());
                lblTang.setPrefWidth(100);
                lblTang.setStyle("""
                                -fx-font-family: 'Segoe UI';
                                -fx-font-size: 14px;
                                -fx-text-fill: #4a4a4a;
                                """);

                // Giá theo ngày
                Label lblGiaNgay = new Label(String.format("%,.0f VND", phong.getLoaiPhong().getGia()));
                lblGiaNgay.setPrefWidth(150);
                lblGiaNgay.setStyle("""
                                -fx-font-family: 'Segoe UI';
                                -fx-font-size: 14px;
                                -fx-text-fill: #4a4a4a;
                                """);

                // Giá theo tuần
                Label lblGiaTuan = new Label(String.format("%,.0f VND", phong.getLoaiPhong().getGia() * 10));
                lblGiaTuan.setPrefWidth(150);
                lblGiaTuan.setStyle("""
                                -fx-font-family: 'Segoe UI';
                                -fx-font-size: 14px;
                                -fx-text-fill: #4a4a4a;
                                """);

                // ======== Thêm tất cả vào container ========
                container.getChildren().addAll(lblSoPhong, lblLoaiPhong, lblTang, lblGiaNgay, lblGiaTuan);

                return container;
        }
}
