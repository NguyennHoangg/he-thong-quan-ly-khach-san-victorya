package view;

import java.util.List;
import java.util.Optional;

import controller.ChiTietPhieuDatPhong_Controller;
import controller.Phong_Controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.ChiTietPhieuDatPhong;
import model.Phong;

public class DoiPhong_GUI extends BorderPane {
        private TextField lblTimSoPhong;
        private Button btnTimKiem;

        private Button btnXacNhan;
        private ChiTietPhieuDatPhong_Controller ctpdp_ctrl = new ChiTietPhieuDatPhong_Controller();
        private VBox vboxPhongDaChon = new VBox(10);
        private String maPhongChonDoi;
        private ChiTietPhieuDatPhong ctpdpChonDoi;

        private Label lblTongTienCocGiaTri;
        private Label lblLoaiPhongBanDauGiaTri;
        private Label lblSoPhongBanDauGiaTri;
        private Label lblSoPhongSauGiaTri;
        private Label lblLoaiPhongSauGiaTri;
        private Label lblTongTienSauGiaTri;
        private Label lblSoPhongChenhLechGiaTri;
        private Label lblLoaiPhongChenhLechGiaTri;
        private Label lblTongTienChenhLechGiaTri;

        private double tienCoc;
        private double tienPhongSau;
        private double tienChenhLech;
        private int soGio;
        private Phong phongDaChon;
        private Phong_Controller phong_Ctrl = new Phong_Controller();
        private TableView<ChiTietPhieuDatPhong> table = new TableView<>();
        private DoiPhong_Modal doiPhong_Modal;
        private VBox containPhai = new VBox();
        private VBox containTrai = new VBox();
        private Label lblTieuDe = new Label("Chọn phòng cần đổi");
        private VBox timKiem = taoPhanTimKiem();

        public DoiPhong_GUI() {
                this.setPadding(new Insets(20));
                this.setStyle("-fx-background-color: #f0f2f5;");

                lblTieuDe.setStyle("-fx-padding: 10; -fx-font-weight: bold;");
                lblTieuDe.setFont(Font.font("System", FontWeight.SEMI_BOLD, 22));

                HBox containChinh = new HBox();

                containChinh.getStylesheets().add(
                                getClass().getResource("/css/Button.css").toExternalForm());

                containTrai.getChildren().addAll(lblTieuDe, timKiem, taoBang(), chonPhongDoi(), vboxPhongDaChon);

                btnXacNhan = new Button("Xác nhận");
                btnXacNhan.setPrefWidth(600);
                btnXacNhan.setPrefHeight(120);
                btnXacNhan.getStyleClass().add("btn");
                btnXacNhan.setOnAction(e -> xacNhan(ctpdpChonDoi, phongDaChon));
                containPhai.getChildren().addAll(taoPhongBanDau(), taoPhongSau(), taoPhiChecnhLech(), btnXacNhan);
                // Cách giữa các ô
                containPhai.setSpacing(20);

                // Padding cho toàn bộ vùng bên phải
                containPhai.setPadding(new Insets(10, 20, 10, 30));
                containChinh.getChildren().addAll(containTrai, containPhai);
                this.setCenter(containChinh);

                containChinh.getStylesheets().add(getClass().getResource("/css/Label.css").toExternalForm());

        }

        private VBox taoPhanTimKiem() {
                VBox container = new VBox(15);
                container.setStyle("-fx-padding: 10;");
                HBox timKiemBox = new HBox(10);
                timKiemBox.setAlignment(Pos.CENTER_LEFT);

                lblTimSoPhong = new TextField();
                lblTimSoPhong.setPromptText("Nhập số phòng");
                lblTimSoPhong.setPrefWidth(350);
                lblTimSoPhong.setPrefHeight(40);
                lblTimSoPhong.setStyle(
                                "-fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #d1d5db; -fx-background-color: white; -fx-padding: 0 15;");

                lblTimSoPhong.setOnKeyPressed(e -> {
                        if (e.getCode() == KeyCode.ENTER) {
                                timKiem();
                        }
                });
                btnTimKiem = new Button("Tìm kiếm");
                btnTimKiem.setPrefHeight(40);
                btnTimKiem.setPrefWidth(110);
                btnTimKiem.getStyleClass().add("btn");
                btnTimKiem.setOnAction(e -> timKiem());
                timKiemBox.getChildren().addAll(lblTimSoPhong, btnTimKiem);
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

                HBox soPhongBox = new HBox();
                HBox loaiPhongBox = new HBox();
                HBox donViBox = new HBox();
                HBox totalBox = new HBox();

                // Hàng số phòng
                Label lblSoPhong = new Label("Số phòng: ");
                Label lblLoai = new Label("Loại phòng: ");
                Label lblDonVi = new Label("Đơn vị tính: ");
                Label lblTongTienCoc = new Label("Giá phòng");

                lblSoPhongBanDauGiaTri = new Label("-");
                lblLoaiPhongBanDauGiaTri = new Label("-");
                Label lblDonViGiaTri = new Label("vnđ/h");
                lblTongTienCocGiaTri = new Label("-");

                Region spacer1 = new Region();
                Region spacer2 = new Region();
                Region spacer3 = new Region();
                Region spacer4 = new Region();// Khối căn khoảng cách

                HBox.setHgrow(spacer1, Priority.ALWAYS); // Tràn hết ra nếu ô còn khoảng trống
                HBox.setHgrow(spacer2, Priority.ALWAYS);
                HBox.setHgrow(spacer3, Priority.ALWAYS);
                HBox.setHgrow(spacer4, Priority.ALWAYS);

                soPhongBox.getChildren().addAll(lblSoPhong, spacer1, lblSoPhongBanDauGiaTri);
                loaiPhongBox.getChildren().addAll(lblLoai, spacer2, lblLoaiPhongBanDauGiaTri);
                donViBox.getChildren().addAll(lblDonVi, spacer3, lblDonViGiaTri);
                totalBox.getChildren().addAll(lblTongTienCoc, spacer4, lblTongTienCocGiaTri);

                lblTongTienCoc.setFont(Font.font("System", FontWeight.BOLD, 14));
                lblTongTienCocGiaTri.setFont(Font.font("System", FontWeight.BOLD, 14));

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

                HBox soPhongBox = new HBox();
                HBox loaiPhongBox = new HBox();
                HBox donViBox = new HBox();
                HBox totalBox = new HBox();

                // Hàng số phòng
                Label lblSoPhong = new Label("Số phòng: ");
                Label lblLoai = new Label("Loại phòng: ");
                Label lblDonVi = new Label("Đơn vị tính: ");
                Label lblTongTienSau = new Label("Giá phòng");

                lblSoPhongSauGiaTri = new Label("-");
                lblLoaiPhongSauGiaTri = new Label("-");
                Label lblDonViGiaTri = new Label("vnđ/h");
                lblTongTienSauGiaTri = new Label("-");

                Region spacer1 = new Region();
                Region spacer2 = new Region();
                Region spacer3 = new Region();
                Region spacer4 = new Region();// Khối căn khoảng cách

                HBox.setHgrow(spacer1, Priority.ALWAYS); // Tràn hết ra nếu ô còn khoảng trống
                HBox.setHgrow(spacer2, Priority.ALWAYS);
                HBox.setHgrow(spacer3, Priority.ALWAYS);
                HBox.setHgrow(spacer4, Priority.ALWAYS);

                soPhongBox.getChildren().addAll(lblSoPhong, spacer1, lblSoPhongSauGiaTri);
                loaiPhongBox.getChildren().addAll(lblLoai, spacer2, lblLoaiPhongSauGiaTri);
                donViBox.getChildren().addAll(lblDonVi, spacer3, lblDonViGiaTri);
                totalBox.getChildren().addAll(lblTongTienSau, spacer4, lblTongTienSauGiaTri);

                lblTongTienSau.setFont(Font.font("System", FontWeight.BOLD, 14));
                lblTongTienSauGiaTri.setFont(Font.font("System", FontWeight.BOLD, 14));

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

                HBox soPhongBox = new HBox();
                HBox loaiPhongBox = new HBox();
                HBox donViBox = new HBox();
                HBox totalBox = new HBox();

                // Hàng số phòng
                Label lblSoPhong = new Label("Số phòng: ");
                Label lblLoai = new Label("Loại phòng: ");
                Label lblDonVi = new Label("Đơn vị tính: ");
                Label lblTongTienCoc = new Label("Giá chênh lệch: ");

                lblSoPhongChenhLechGiaTri = new Label("-");
                lblLoaiPhongChenhLechGiaTri = new Label("-");
                Label lblDonViGiaTri = new Label("vnđ/h");
                lblTongTienChenhLechGiaTri = new Label("-");

                Region spacer1 = new Region();
                Region spacer2 = new Region();
                Region spacer3 = new Region();
                Region spacer4 = new Region();// Khối căn khoảng cách

                HBox.setHgrow(spacer1, Priority.ALWAYS); // Tràn hết ra nếu ô còn khoảng trống
                HBox.setHgrow(spacer2, Priority.ALWAYS);
                HBox.setHgrow(spacer3, Priority.ALWAYS);
                HBox.setHgrow(spacer4, Priority.ALWAYS);

                soPhongBox.getChildren().addAll(lblSoPhong, spacer1, lblSoPhongChenhLechGiaTri);
                loaiPhongBox.getChildren().addAll(lblLoai, spacer2, lblLoaiPhongChenhLechGiaTri);
                donViBox.getChildren().addAll(lblDonVi, spacer3, lblDonViGiaTri);
                totalBox.getChildren().addAll(lblTongTienCoc, spacer4, lblTongTienChenhLechGiaTri);

                lblTongTienCoc.setFont(Font.font("System", FontWeight.BOLD, 14));
                lblTongTienChenhLechGiaTri.setFont(Font.font("System", FontWeight.BOLD, 14));

                chiTietBox.getChildren().addAll(soPhongBox, loaiPhongBox, donViBox, totalBox);
                container.getChildren().addAll(lblTieuDe, chiTietBox);

                return container;
        }

        private ScrollPane taoBang() {
                table.getColumns().clear();
                // ======== Cột ========
                TableColumn<ChiTietPhieuDatPhong, String> colSoPhong = new TableColumn<>("Số phòng");
                colSoPhong.setCellValueFactory(
                                data -> new SimpleStringProperty(data.getValue().getPhong().getSoPhong()));

                TableColumn<ChiTietPhieuDatPhong, String> colLoaiPhong = new TableColumn<>("Loại phòng");
                colLoaiPhong.setCellValueFactory(data -> new SimpleStringProperty(
                                data.getValue().getPhong().getLoaiPhong().getTenLoaiPhong()));

                TableColumn<ChiTietPhieuDatPhong, String> colTang = new TableColumn<>("Tầng");
                colTang.setCellValueFactory(
                                data -> new SimpleStringProperty(String.valueOf(data.getValue().getPhong().getTang())));

                TableColumn<ChiTietPhieuDatPhong, String> colThoiGianLuuTru = new TableColumn<>("Thời gian lưu trú");
                colThoiGianLuuTru.setCellValueFactory(data -> new SimpleStringProperty(
                                String.format("%d giờ", data.getValue().getSoGioLuuTru())));

                TableColumn<ChiTietPhieuDatPhong, String> colGia = new TableColumn<>("Giá");
                colGia.setCellValueFactory(data -> new SimpleStringProperty(
                                String.format("%,.0f VND", data.getValue().tinhThanhTien())));

                // ======== Dữ liệu ========
                List<ChiTietPhieuDatPhong> dsPhongDaDat = ctpdp_ctrl.getDsPhongTheoTrangThai("Đang ở");
                ObservableList<ChiTietPhieuDatPhong> data = FXCollections.observableArrayList(dsPhongDaDat);

                table.getColumns().add(colSoPhong);
                table.getColumns().add(colLoaiPhong);
                table.getColumns().add(colTang);
                table.getColumns().add(colThoiGianLuuTru);
                table.getColumns().add(colGia);
                table.setItems(data);

                // ======== Kích thước ========
                table.setPrefWidth(700);
                table.setPrefHeight(300);
                table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

                // ======== Giao diện ========
                table.getStylesheets().add(getClass().getResource("/css/Table.css").toExternalForm());
                table.setRowFactory(tv -> {
                        TableRow<ChiTietPhieuDatPhong> row = new TableRow<>() {
                                @Override
                                protected void updateItem(ChiTietPhieuDatPhong item, boolean empty) {
                                        super.updateItem(item, empty);
                                        if (empty) {
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
                // Chọn
                table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                        if (newSelection != null) {
                                maPhongChonDoi = newSelection.getPhong().getSoPhong();
                                capNhatThongTinPhongChonDoi();
                                if (ctpdpChonDoi != null) {
                                        try {
                                                capNhatThongTinPhongChenhLech(phongDaChon);
                                        }

                                        catch (Exception e) {
                                                return;
                                        }
                                }
                        }
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

                btnChon.setOnAction(e -> {
                        tienChenhLech = 0;
                        tienPhongSau = 0;
                        doiPhong_Modal = new DoiPhong_Modal();
                        doiPhong_Modal.hienThi();

                        // Lấy phòng đã chọn sau khi đóng modal
                        phongDaChon = doiPhong_Modal.getPhongDaChon();
                        if (phongDaChon != null) {
                                // Cập nhật các thông tin khác nếu cần
                                capNhatThongTinPhongDoi(phongDaChon);
                                if (ctpdpChonDoi != null) {
                                        capNhatThongTinPhongChenhLech(phongDaChon);
                                }

                                // Xóa toàn bộ các phòng đã chọn cũ
                                vboxPhongDaChon.getChildren().clear();

                                // Tạo và thêm lại phòng mới
                                vboxPhongDaChon.getChildren().add(taoHangPhongDaChon(phongDaChon));
                        }
                });
                btnChon.getStyleClass().add("btn");
                container.getChildren().addAll(lblChonDoi, btnChon);
                return container;

        }

        private HBox taoHangPhongDaChon(Phong phongDaChon) {
                String soPhong = phongDaChon.getSoPhong();
                String loaiPhong = phongDaChon.getLoaiPhong().getTenLoaiPhong();
                String tang = String.format(" %d", phongDaChon.getTang());
                String gia = String.format("%,.0f VND", phongDaChon.getLoaiPhong().getGia());
                HBox container = new HBox();
                container.setPrefHeight(60);
                container.setAlignment(Pos.CENTER_LEFT);
                container.setStyle("""
                                -fx-background-color: white;
                                -fx-border-color: #d0d7de;
                                -fx-border-radius: 8;
                                -fx-background-radius: 8;
                                -fx-padding: 15 30;
                                """);

                Label lblSoPhong = new Label(soPhong);
                lblSoPhong.setPrefWidth(100);
                lblSoPhong.getStyleClass().add("label");

                Label lblLoaiPhong = new Label(loaiPhong);
                lblLoaiPhong.setPrefWidth(150);
                lblLoaiPhong.getStyleClass().add("label");

                Label lblTang = new Label("Tầng " + tang);
                lblTang.setPrefWidth(100);
                lblTang.getStyleClass().add("label");

                Label lblGiaNgay = new Label(gia);
                lblGiaNgay.setPrefWidth(150);
                lblGiaNgay.getStyleClass().add("label");

                lblLoaiPhong.setTranslateX(10);
                lblTang.setTranslateX(20);

                Region spacer = new Region();
                spacer.setPrefWidth(150);

                container.getChildren().addAll(lblSoPhong, lblLoaiPhong, lblTang, spacer, lblGiaNgay);

                return container;
        }

        private void capNhatThongTinPhongChonDoi() {
                List<ChiTietPhieuDatPhong> dsPhongDaDat = ctpdp_ctrl.getDsPhongTheoTrangThai("Đang ở");
                ctpdpChonDoi = ctpdp_ctrl.getChiTietPhieuDatPhongTheoPhong(maPhongChonDoi, dsPhongDaDat);

                if (ctpdpChonDoi == null) {
                        Alert a = new Alert(Alert.AlertType.WARNING, "Vui lòng chọn phòng ban đầu trước!");
                        a.showAndWait();
                        return;
                }
                soGio = ctpdpChonDoi.getSoGioLuuTru();
                lblSoPhongBanDauGiaTri.setText(ctpdpChonDoi.getPhong().getSoPhong());
                lblLoaiPhongBanDauGiaTri.setText(ctpdpChonDoi.getPhong().getLoaiPhong().getTenLoaiPhong());
                tienCoc = ctpdp_ctrl.tinhTienCoc(ctpdp_ctrl.tinhThanhTien(
                                ctpdpChonDoi.getPhong().getLoaiPhong().getGia(),
                                ctpdpChonDoi.getPhong().getLoaiPhong().getTenLoaiPhong(),
                                ctpdpChonDoi.getSoGioLuuTru()));
                lblTongTienCocGiaTri.setText(String.format("%,.0f VND", tienCoc));
        }

        private void capNhatThongTinPhongDoi(Phong phong) {
                lblSoPhongSauGiaTri.setText(phong.getSoPhong());
                lblLoaiPhongSauGiaTri.setText(phong.getLoaiPhong().getTenLoaiPhong());
                tienPhongSau = ctpdp_ctrl.tinhThanhTien(phong.getLoaiPhong().getGia(),
                                phong.getLoaiPhong().getTenLoaiPhong(), soGio);
                lblTongTienSauGiaTri.setText(String.format("%,.0f VND", tienPhongSau));
        }

        private void capNhatThongTinPhongChenhLech(Phong phong) {
                lblSoPhongChenhLechGiaTri.setText(phong.getSoPhong());
                lblLoaiPhongChenhLechGiaTri.setText(phong.getLoaiPhong().getTenLoaiPhong());
                tienChenhLech = ctpdp_ctrl.tinhChenhLech(tienCoc, tienPhongSau);
                lblTongTienChenhLechGiaTri.setText(String.format("%,.0f VND", tienChenhLech));
        }

        private void xacNhan(ChiTietPhieuDatPhong chiTietPhieuCu, Phong phongMoi) {
                if (chiTietPhieuCu == null || phongMoi == null) {
                        Alert canhBao = new Alert(Alert.AlertType.WARNING);
                        canhBao.setTitle("Cảnh báo");
                        canhBao.setHeaderText(null);
                        canhBao.setContentText("Vui lòng chọn đủ phòng ban đầu và phòng đổi!");
                        canhBao.showAndWait();
                        return;
                }

                Phong phongCu = phong_Ctrl.getPhongTheoSoPhong(chiTietPhieuCu.getPhong().getSoPhong());

                Alert xacNhanAlert = new Alert(Alert.AlertType.CONFIRMATION);
                xacNhanAlert.setTitle("Xác nhận");
                xacNhanAlert.setHeaderText(null);
                xacNhanAlert.setContentText(
                                "Bạn có chắc chắn muốn đổi từ phòng " + phongCu.getSoPhong() +
                                                " sang phòng " + phongMoi.getSoPhong() + " không?");

                Optional<ButtonType> ketQua = xacNhanAlert.showAndWait();

                if (ketQua.isPresent() && ketQua.get() == ButtonType.OK) {
                        phong_Ctrl.capNhatTrangThaiPhong(phongCu.getMaPhong(), "Trống");
                        phong_Ctrl.capNhatTrangThaiPhong(phongMoi.getMaPhong(), "Đang ở");

                        boolean doiThanhCong = ctpdp_ctrl.doiPhong(chiTietPhieuCu, phongMoi);

                        if (doiThanhCong) {
                                lamMoiGUI();

                                Alert thongBao = new Alert(Alert.AlertType.INFORMATION);
                                thongBao.setTitle("Thông báo");
                                thongBao.setHeaderText(null);
                                thongBao.setContentText("Đổi phòng thành công!");
                                thongBao.showAndWait();
                        } else {
                                Alert canhBaoLoi = new Alert(Alert.AlertType.ERROR);
                                canhBaoLoi.setTitle("Cảnh báo");
                                canhBaoLoi.setHeaderText(null);
                                canhBaoLoi.setContentText("Không thể đổi phòng!");
                                canhBaoLoi.showAndWait();
                        }
                }
        }

        private void timKiem() {
                String soPhongTimKiem = lblTimSoPhong.getText().trim();

                if (soPhongTimKiem.isEmpty()) {
                        ObservableList<ChiTietPhieuDatPhong> data = FXCollections
                                        .observableArrayList(ctpdp_ctrl.getDsPhongTheoTrangThai("Đang ở"));
                        table.getItems().setAll(data);
                        return;
                }

                ChiTietPhieuDatPhong ketQuaTimKiem = ctpdp_ctrl.getChiTietPhieuDatPhongTheoPhong(
                                soPhongTimKiem,
                                ctpdp_ctrl.getDsPhongTheoTrangThai("Đang ở"));

                if (ketQuaTimKiem != null) {
                        // Hiển thị kết quả tìm thấy
                        ObservableList<ChiTietPhieuDatPhong> data = FXCollections
                                        .observableArrayList(ketQuaTimKiem);
                        table.getItems().setAll(data);
                } else {
                        Alert thongBao = new Alert(Alert.AlertType.INFORMATION);
                        thongBao.setTitle("Kết quả tìm kiếm");
                        thongBao.setHeaderText(null);
                        thongBao.setContentText("Không tìm thấy phòng có số: " + soPhongTimKiem);
                        thongBao.showAndWait();

                        ObservableList<ChiTietPhieuDatPhong> data = FXCollections
                                        .observableArrayList(ctpdp_ctrl.getDsPhongTheoTrangThai("Đang ở"));
                        table.getItems().setAll(data);
                }
        }

        private void lamMoiGUI() {
                lblTimSoPhong.setText("");
                phongDaChon = null;
                ctpdpChonDoi = null;
                maPhongChonDoi = null;
                tienChenhLech = 0;
                tienCoc = 0;
                tienPhongSau = 0;
                table.getItems().setAll(ctpdp_ctrl.getDsPhongTheoTrangThai("Đang ở"));
                table.getSelectionModel().clearSelection();
                table.refresh();
                vboxPhongDaChon.getChildren().clear();

                if (doiPhong_Modal != null) {
                        doiPhong_Modal.lamMoiModal();
                }
                containPhai.getChildren().clear();
                containTrai.getChildren().clear();
                containTrai.getChildren().addAll(lblTieuDe, timKiem, taoBang(), chonPhongDoi(), vboxPhongDaChon);
                containPhai.getChildren().addAll(taoPhongBanDau(), taoPhongSau(), taoPhiChecnhLech(), btnXacNhan);
        }

}