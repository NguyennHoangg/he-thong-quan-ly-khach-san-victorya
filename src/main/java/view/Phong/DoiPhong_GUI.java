package view.Phong;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import controller.ChiTietPhieuDatPhong_Controller;
import controller.Phong_Controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
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

        private TextField tfTimSoPhong;
        private Button btnTimKiem, btnLamMoi, btnXacNhan;

        private ChiTietPhieuDatPhong_Controller ctpdp_ctrl = new ChiTietPhieuDatPhong_Controller();
        private Phong_Controller phong_Ctrl = new Phong_Controller();

        private TableView<ChiTietPhieuDatPhong> table = new TableView<>();
        private VBox vboxPhongDaChon = new VBox(6);

        private ChiTietPhieuDatPhong ctpdpChonDoi;
        private Phong phongDaChon;
        private String maPhongChonDoi;

        private Label lblTongTienCocGiaTri, lblLoaiPhongBanDauGiaTri, lblSoPhongBanDauGiaTri;
        private Label lblSoPhongSauGiaTri, lblLoaiPhongSauGiaTri, lblTongTienSauGiaTri;

        private List<ChiTietPhieuDatPhong> dsCanDoi = new ArrayList<>();

        public DoiPhong_GUI() {
                this.setPadding(new Insets(20));
                this.setStyle("-fx-background-color: #f0f2f5;");

                Label lblTieuDe = new Label("Chọn phòng cần đổi");
                lblTieuDe.getStyleClass().add("tieu-de");

                VBox containTrai = new VBox(12);
                containTrai.getChildren().addAll(lblTieuDe, taoPhanTimKiem(), taoBang(), chonPhongDoi(),
                                vboxPhongDaChon);

                VBox containPhai = new VBox(20);
                Region spacerTop = new Region(); // khoảng cách với đầu trang

                spacerTop.prefHeightProperty().bind(
                                lblTieuDe.heightProperty()
                                                .add(taoPhanTimKiem().heightProperty())
                                                .add(12) // khoảng cách VBox spacing
                );
                containPhai.setPadding(new Insets(20, 10, 5, 25));
                containPhai.getChildren().addAll(
                                spacerTop, taoPhongBanDau(), taoPhongSau(), taoNutXacNhan());

                HBox root = new HBox(containTrai, containPhai);
                root.setSpacing(40);
                root.getStylesheets().add(getClass().getResource("/css/Button.css").toExternalForm());
                root.getStylesheets().add(getClass().getResource("/css/TieuDe.css").toExternalForm());
                this.setCenter(root);

                table.setItems(FXCollections.observableArrayList());
        }

        private VBox taoPhanTimKiem() {
                VBox box = new VBox(10);
                HBox h = new HBox(10);
                h.setAlignment(Pos.CENTER_LEFT);

                tfTimSoPhong = new TextField();
                tfTimSoPhong.setPrefWidth(400);
                tfTimSoPhong.setPrefHeight(38);
                tfTimSoPhong.setPromptText("Nhập số phòng...");
                tfTimSoPhong.setOnKeyPressed(e -> {
                        if (e.getCode() == KeyCode.ENTER)
                                timKiem();
                });

                btnTimKiem = new Button("Tìm");
                btnTimKiem.setPrefSize(90, 38);
                btnTimKiem.getStyleClass().add("btn");
                btnTimKiem.setOnAction(e -> timKiem());

                btnLamMoi = new Button("Làm mới");
                btnLamMoi.setPrefSize(90, 38);
                btnLamMoi.getStyleClass().add("btn");
                btnLamMoi.setOnAction(e -> lamMoiGUI());

                h.getChildren().addAll(tfTimSoPhong, btnTimKiem, btnLamMoi);
                box.getChildren().add(h);
                return box;
        }

        private TableView<ChiTietPhieuDatPhong> taoBang() {
                table.getColumns().clear();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

                // ======== CỘT ========
                TableColumn<ChiTietPhieuDatPhong, String> colSoPhong = new TableColumn<>("Số phòng");
                colSoPhong.setPrefWidth(80);
                colSoPhong.setCellValueFactory(
                                data -> new SimpleStringProperty(data.getValue().getPhong().getSoPhong()));

                TableColumn<ChiTietPhieuDatPhong, String> colLoaiPhong = new TableColumn<>("Loại phòng");
                colLoaiPhong.setPrefWidth(180);
                colLoaiPhong.setCellValueFactory(data -> new SimpleStringProperty(
                                data.getValue().getPhong().getLoaiPhong().getTenLoaiPhong()));

                TableColumn<ChiTietPhieuDatPhong, String> colTang = new TableColumn<>("Tầng");
                colTang.setPrefWidth(60);
                colTang.setCellValueFactory(
                                data -> new SimpleStringProperty(String.valueOf(data.getValue().getPhong().getTang())));

                TableColumn<ChiTietPhieuDatPhong, String> colTrangThaiPhong = new TableColumn<>("Trạng thái");
                colTrangThaiPhong.setPrefWidth(120);
                colTrangThaiPhong.setCellValueFactory(
                                data -> new SimpleStringProperty(data.getValue().getPhong().getTrangThai()));

                TableColumn<ChiTietPhieuDatPhong, String> colGia = new TableColumn<>("Giá phòng");
                colGia.setPrefWidth(120);
                colGia.setCellValueFactory(data -> new SimpleStringProperty(
                                String.format("%,.0f VND", data.getValue().getPhong().getLoaiPhong().getGia())));

                TableColumn<ChiTietPhieuDatPhong, String> colSoTienDaCoc = new TableColumn<>("Số tiền đã cọc");
                colSoTienDaCoc.setPrefWidth(150);
                colSoTienDaCoc.setCellValueFactory(data -> new SimpleStringProperty(
                                String.format("%,.0f VND", data.getValue().tinhThanhTien())));

                TableColumn<ChiTietPhieuDatPhong, String> colThoiGianNhanPhong = new TableColumn<>("Nhận phòng");
                colThoiGianNhanPhong.setPrefWidth(160);
                colThoiGianNhanPhong.setCellValueFactory(data -> {
                        LocalDateTime time = data.getValue().getThoiGianNhanPhong();
                        String formatted = time != null ? time.format(formatter) : "";
                        return new SimpleStringProperty(formatted);
                });

                table.getColumns().addAll(
                                colSoPhong,
                                colLoaiPhong,
                                colTang,
                                colTrangThaiPhong,
                                colGia,
                                colSoTienDaCoc,
                                colThoiGianNhanPhong);

                table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
                table.setPlaceholder(new Label(""));

                table.setPrefHeight(450);
                table.setPrefWidth(Region.USE_COMPUTED_SIZE);
                table.setMaxWidth(Double.MAX_VALUE);

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
                                capNhatPhongBanDau();
                        }
                });

                return table;
        }

        private void lamMoiGUI() {
                dsCanDoi.clear();
                dsCanDoi.addAll(ctpdp_ctrl.getDsPhongTheoTrangThai("Đã đặt", "Tốt"));
                dsCanDoi.addAll(ctpdp_ctrl.getDsPhongTheoTrangThai("Đang ở", "Tốt"));

                table.setItems(FXCollections.observableArrayList(dsCanDoi));

                tfTimSoPhong.clear();

                phongDaChon = null;
                ctpdpChonDoi = null;
                maPhongChonDoi = null;

                lblSoPhongBanDauGiaTri.setText("-");
                lblLoaiPhongBanDauGiaTri.setText("-");
                lblTongTienCocGiaTri.setText("-");

                lblSoPhongSauGiaTri.setText("-");
                lblLoaiPhongSauGiaTri.setText("-");
                lblTongTienSauGiaTri.setText("-");

                vboxPhongDaChon.getChildren().clear();

                table.getSelectionModel().clearSelection();
        }

        private void timKiem() {
                String keyword = tfTimSoPhong.getText().trim();

                if (keyword.isEmpty()) {
                        lamMoiGUI();
                        return;
                }

                List<ChiTietPhieuDatPhong> rs = new ArrayList<>();
                for (var ct : dsCanDoi) {
                        if (ct.getPhong().getSoPhong().contains(keyword)) {
                                rs.add(ct);
                        }
                }

                table.setItems(FXCollections.observableArrayList(rs));
        }

        private HBox chonPhongDoi() {

                HBox h = new HBox(15);
                h.setAlignment(Pos.CENTER_LEFT); // Giúp label và nút thẳng hàng

                Label lbl = new Label("Chọn phòng đổi");
                lbl.getStyleClass().add("chon-phong-doi");

                Button btn = new Button("Chọn");
                btn.setPrefSize(80, 30);
                btn.getStyleClass().add("btn");

                btn.setOnAction(e -> {
                        if (ctpdpChonDoi == null) {
                                Alert canhBao = new Alert(Alert.AlertType.WARNING);
                                canhBao.setTitle("Cảnh báo");
                                canhBao.setHeaderText(null);
                                canhBao.setContentText("Vui lòng chọn phòng ban đầu");
                                canhBao.showAndWait();
                                lamMoiGUI();
                                return;
                        }

                        DoiPhong_Modal modal = new DoiPhong_Modal(ctpdpChonDoi);
                        modal.hienThi();
                        phongDaChon = modal.getPhongDaChon();

                        capNhatPhongSau();

                        vboxPhongDaChon.getChildren().clear();
                        vboxPhongDaChon.getChildren().add(
                                        taoHangPhongDaChon(phongDaChon));
                });

                h.getChildren().addAll(lbl, btn);
                return h;
        }

        private HBox taoHangPhongDaChon(Phong p) {
                HBox h = new HBox(10);
                h.setPadding(new Insets(12));
                h.setStyle("-fx-background-color: white; -fx-border-color:#d0d7de; -fx-border-radius:8;-fx-background-radius:8;");
                h.setAlignment(Pos.CENTER_LEFT);

                Label so = new Label(p.getSoPhong());
                so.setPrefWidth(100);

                Label loai = new Label(p.getLoaiPhong().getTenLoaiPhong());
                loai.setPrefWidth(130);

                Label tang = new Label("Tầng " + p.getTang());
                tang.setPrefWidth(100);

                Label gia = new Label(String.format("%,.0f VND", p.getLoaiPhong().getGia()));
                gia.setPrefWidth(120);

                Region sp = new Region();
                HBox.setHgrow(sp, Priority.ALWAYS);

                h.getChildren().addAll(so, loai, tang, sp, gia);
                return h;
        }

        private VBox taoPhongBanDau() {
                VBox box = taoKhungThongTin("#EC221F", "Phòng ban đầu");
                box.setMinSize(420, 220);
                lblSoPhongBanDauGiaTri = taoGiaTri();
                lblLoaiPhongBanDauGiaTri = taoGiaTri();
                lblTongTienCocGiaTri = taoGiaTriBold();

                box.getChildren().addAll(
                                taoRow("Số phòng:", lblSoPhongBanDauGiaTri),
                                taoRow("Loại phòng:", lblLoaiPhongBanDauGiaTri),
                                taoRow("Đơn vị tính:", new Label("vnđ/h")),
                                taoRow("Số tiền đã cọc:", lblTongTienCocGiaTri));

                return box;
        }

        private void capNhatPhongBanDau() {
                List<ChiTietPhieuDatPhong> dsPhongDaDat = ctpdp_ctrl.getDsPhongTheoTrangThai("Đã đặt", "Tốt");
                ctpdpChonDoi = ctpdp_ctrl.getChiTietPhieuDatPhongTheoPhong(maPhongChonDoi, dsPhongDaDat);
                if (ctpdpChonDoi == null)
                        return;

                lblSoPhongBanDauGiaTri.setText(ctpdpChonDoi.getPhong().getSoPhong());
                lblLoaiPhongBanDauGiaTri.setText(ctpdpChonDoi.getPhong().getLoaiPhong().getTenLoaiPhong());
                lblTongTienCocGiaTri.setText(String.format("%,.0f VND", ctpdpChonDoi.tinhThanhTien()));

        }

        private VBox taoPhongSau() {
                VBox box = taoKhungThongTin("#14AE5C", "Phòng đổi sang");
                box.setMinSize(420, 220);
                lblSoPhongSauGiaTri = taoGiaTri();
                lblLoaiPhongSauGiaTri = taoGiaTri();
                lblTongTienSauGiaTri = taoGiaTriBold();

                box.getChildren().addAll(
                                taoRow("Số phòng:", lblSoPhongSauGiaTri),
                                taoRow("Loại phòng:", lblLoaiPhongSauGiaTri),
                                taoRow("Đơn vị tính:", new Label("vnđ/h")),
                                taoRow("Giá phòng:", lblTongTienSauGiaTri));

                return box;
        }

        private void capNhatPhongSau() {
                if (phongDaChon == null)
                        return;

                lblSoPhongSauGiaTri.setText(phongDaChon.getSoPhong());
                lblLoaiPhongSauGiaTri.setText(phongDaChon.getLoaiPhong().getTenLoaiPhong());
                lblTongTienSauGiaTri.setText(String.format("%,.0f VND", phongDaChon.getLoaiPhong().getGia()));
        }

        private Button taoNutXacNhan() {
                btnXacNhan = new Button("Xác nhận");
                btnXacNhan.setPrefSize(420, 50);
                btnXacNhan.getStyleClass().add("btn");

                btnXacNhan.setOnAction(e -> {
                        xacNhan(ctpdpChonDoi, phongDaChon);
                });

                return btnXacNhan;
        }

        private VBox taoKhungThongTin(String mau, String tuaDe) {
                VBox v = new VBox(10);
                v.setPadding(new Insets(14));
                v.setMinSize(420, 160);
                v.setStyle(
                                "-fx-border-color:" + mau + ";" +
                                                "-fx-border-width:1;" +
                                                "-fx-border-radius:12;" +
                                                "-fx-background-radius:12;" +
                                                "-fx-background-color:white;");

                Label lbl = new Label(tuaDe);
                lbl.setFont(Font.font("System", FontWeight.BOLD, 18));
                lbl.setStyle("-fx-text-fill:" + mau);

                v.getChildren().add(lbl);
                return v;
        }

        private Label taoGiaTri() {
                Label lbl = new Label("-");
                lbl.setFont(Font.font(15));
                return lbl;
        }

        private Label taoGiaTriBold() {
                Label lbl = new Label("-");
                lbl.setFont(Font.font("System", FontWeight.BOLD, 15));
                return lbl;
        }

        private HBox taoRow(String label, Label value) {
                HBox h = new HBox();
                Label l = new Label(label);
                l.setFont(Font.font(18));

                Region sp = new Region();
                HBox.setHgrow(sp, Priority.ALWAYS);

                h.getChildren().addAll(l, sp, value);
                return h;
        }

        // Chọn phòng cần Hủy -> Cập nhật phòng ban đầu
        // Nhấn nút chọn phòng cần đổi -> Chọn phòng
        // -> Nhấn xác nhận -> tắt modal -> cập nhật thông tin
        // phòng cần đổi, cập nhật phí chênh lệch -> nhấn Xác
        // nhận -> đổi Phong trong ChiTietPhieuDatPhong
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
                        phong_Ctrl.capNhatTrangThaiPhong(phongMoi.getMaPhong(), "Đã đặt");

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
}
