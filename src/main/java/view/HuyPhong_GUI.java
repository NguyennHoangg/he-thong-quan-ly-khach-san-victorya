package view;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import controller.ChiTietPhieuDatPhong_Controller;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.ChiTietPhieuDatPhong;

public class HuyPhong_GUI extends BorderPane {
    private TextField txtNhapCCCD;
    private Button btnTimKiem;
    public TextArea txtLyDoHuyPhong;
    private Button btnHuyNgay;
    private VBox danhSachPhongContainer;

    private double tongThanhTien;

    private Label lblTongTienPhongValue;
    private Label lblTongTienCocGiaTri;
    ChiTietPhieuDatPhong_Controller chiTietPhieuDatPhong_Controller = new ChiTietPhieuDatPhong_Controller();

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public List<ChiTietPhieuDatPhong> ctpdpDaChon = new ArrayList<>();

    public HuyPhong_GUI() {
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: #ffffffff;");

        VBox mainContainer = new VBox(20);
        VBox.setVgrow(mainContainer, Priority.ALWAYS);

        VBox topSection = taoPhanTimKiem();
        ScrollPane centerSection = taoPhanDanhSachPhong();
        VBox.setVgrow(centerSection, Priority.ALWAYS);

        HBox bottomSection = taoPhanDuoi();

        mainContainer.getStylesheets().add(getClass().getResource("/css/Button.css").toExternalForm());
        mainContainer.getChildren().addAll(topSection, centerSection, bottomSection);
        this.setCenter(mainContainer);

    }

    private VBox taoPhanTimKiem() {
        VBox container = new VBox(15);

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
        btnTimKiem.getStyleClass().addAll("btn");
        btnTimKiem.setOnAction(e -> {
            String maPhong = txtNhapCCCD.getText();
            hienThiPhong("Đã đặt", maPhong);

            ctpdpDaChon.clear();
            tongThanhTien = 0;
            capNhatThongTinThanhToan();
        });
        txtNhapCCCD.setOnAction(e -> btnTimKiem.fire());

        timKiemBox.getChildren().addAll(txtNhapCCCD, btnTimKiem);
        container.getChildren().add(timKiemBox);

        return container;
    }

    private ScrollPane taoPhanDanhSachPhong() {
        danhSachPhongContainer = new VBox(15);
        danhSachPhongContainer.setPadding(new Insets(20));
        danhSachPhongContainer.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e5e7eb; -fx-border-radius: 10;");

        Label lblTieuDe = new Label("Danh sách phòng đã đặt");
        lblTieuDe.setStyle("-fx-text-fill: #484848; -fx-font-weight: bold; -fx-font-size: 16px;");
        lblTieuDe.setPadding(new Insets(0, 0, 10, 0));

        danhSachPhongContainer.getChildren().add(lblTieuDe);
        hienThiPhong("Đã đặt", null);

        ScrollPane scrollPane = new ScrollPane(danhSachPhongContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setMinHeight(300);
        scrollPane.setMaxHeight(600);
        scrollPane.setPrefHeight(600);

        return scrollPane;
    }

    public void hienThiPhong(String trangThai, String maPhongCanTim) {
        // Xóa toàn bộ nội dung cũ
        danhSachPhongContainer.getChildren().clear();

        // Lấy danh sách phòng theo trạng thái
        List<ChiTietPhieuDatPhong> dsPhongDaDat = chiTietPhieuDatPhong_Controller.getDsPhongTheoTrangThai(trangThai);

        // Nếu có mã phòng cần tìm, lọc danh sách
        if (maPhongCanTim != null && !maPhongCanTim.trim().isEmpty()) {
            ChiTietPhieuDatPhong phongTimThay = chiTietPhieuDatPhong_Controller
                    .getChiTietPhieuDatPhongTheoPhong(maPhongCanTim.trim(), dsPhongDaDat);

            dsPhongDaDat.clear();
            if (phongTimThay != null) {
                dsPhongDaDat.add(phongTimThay);
            }
        }

        for (ChiTietPhieuDatPhong ctpdp : dsPhongDaDat) {
            HBox phongItem = taoPhongItem(ctpdp);
            danhSachPhongContainer.getChildren().add(phongItem);
        }

        // Nếu không có phòng nào
        if (dsPhongDaDat.isEmpty()) {
            Label lblThongBao = new Label("Không có phòng nào với trạng thái: " + trangThai);
            lblThongBao.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 14px;");
            danhSachPhongContainer.getChildren().add(lblThongBao);
        }
    }

    private HBox taoPhongItem(ChiTietPhieuDatPhong chiTietPhieuDatPhong) {
        HBox container = new HBox(15);
        container.setPadding(new Insets(15));
        container.setAlignment(Pos.CENTER_LEFT);
        container.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 8;
                -fx-border-color: #e5e7eb;
                -fx-border-radius: 8;
                -fx-border-width: 1;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1);
                """);

        Image anhThuong = new Image(getClass().getResource("/img/Thuong.jpg").toExternalForm());
        Image anhVip = new Image(getClass().getResource("/img/VIP.jpg").toExternalForm());
        ImageView hinhAnh;
        if (chiTietPhieuDatPhong.getPhong().getLoaiPhong().getTenLoaiPhong().equalsIgnoreCase("Phòng VIP")) {
            hinhAnh = new ImageView(anhVip);
        } else {
            hinhAnh = new ImageView(anhThuong);
        }
        hinhAnh.setFitWidth(120);
        hinhAnh.setFitHeight(100);
        hinhAnh.setStyle("""
                -fx-background-radius: 5;
                """);

        VBox thongTinPhong = new VBox(10);
        thongTinPhong.setAlignment(Pos.CENTER_LEFT);
        thongTinPhong.setMinWidth(400);
        HBox.setHgrow(thongTinPhong, Priority.ALWAYS);

        Label lblSoPhong = new Label(chiTietPhieuDatPhong.getPhong().getSoPhong());
        lblSoPhong.setStyle("-fx-text-fill: #111827; -fx-font-weight: bold; -fx-font-size: 16px;");

        HBox thongTinChiTiet = new HBox(40);
        thongTinChiTiet.setAlignment(Pos.CENTER_LEFT);

        VBox nhanPhongBox = new VBox(3);
        Label lblNhanPhong = new Label("Nhận phòng:");
        lblNhanPhong.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");
        String ngayNhanPhong = chiTietPhieuDatPhong.getThoiGianNhanPhong().format(formatter);
        Label lblNgayNhan = new Label(ngayNhanPhong);
        lblNgayNhan.setStyle("-fx-font-size: 13px; -fx-text-fill: #111827; -fx-font-weight: 600;");
        nhanPhongBox.getChildren().addAll(lblNhanPhong, lblNgayNhan);

        VBox thoiGianBox = new VBox(3);
        Label lblThoiGianTitle = new Label("Thời gian:");
        lblThoiGianTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");
        Label lblThoiGianValue = new Label(chiTietPhieuDatPhong.getNgayDem());
        lblThoiGianValue.setStyle("-fx-font-size: 13px; -fx-text-fill: #111827; -fx-font-weight: 600;");
        thoiGianBox.getChildren().addAll(lblThoiGianTitle, lblThoiGianValue);

        VBox soKhachBox = new VBox(3);
        Label lblSoKhachTitle = new Label("Số lượng khách:");
        lblSoKhachTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");
        Label lblSoKhachValue = new Label(String.valueOf(chiTietPhieuDatPhong.getSoNguoi()));
        lblSoKhachValue.setStyle("-fx-font-size: 13px; -fx-text-fill: #111827; -fx-font-weight: 600;");
        soKhachBox.getChildren().addAll(lblSoKhachTitle, lblSoKhachValue);

        thongTinChiTiet.getChildren().addAll(nhanPhongBox, thoiGianBox, soKhachBox);

        String tienStr = String.format("%,.0f VND", chiTietPhieuDatPhong.tinhThanhTien());
        Label lblGia = new Label(tienStr);
        lblGia.setStyle("-fx-text-fill: #374151; -fx-font-size: 14px; -fx-font-weight: 600;");

        thongTinPhong.getChildren().addAll(lblSoPhong, thongTinChiTiet, lblGia);

        RadioButton rbtnThanhTien = new RadioButton();
        rbtnThanhTien.setStyle("-fx-cursor: hand;");
        rbtnThanhTien.setUserData(chiTietPhieuDatPhong);

        rbtnThanhTien.setOnAction(e -> {
            ChiTietPhieuDatPhong ctpdp = (ChiTietPhieuDatPhong) rbtnThanhTien.getUserData();

            // Nếu được chọn -> thêm vào danh sách, ngược lại -> xóa ra
            if (rbtnThanhTien.isSelected()) {
                if (!ctpdpDaChon.contains(ctpdp)) {
                    ctpdpDaChon.add(ctpdp);
                }
            } else {
                ctpdpDaChon.remove(ctpdp);
            }

            // Tính lại tổng tiền
            double tongTienTam = 0;
            for (ChiTietPhieuDatPhong item : ctpdpDaChon) {
                tongTienTam += item.tinhThanhTien();
            }

            tongThanhTien = tongTienTam;
            capNhatThongTinThanhToan();
        });

        container.getChildren().addAll(hinhAnh, thongTinPhong, rbtnThanhTien);

        return container;
    }

    private HBox taoPhanDuoi() {
        HBox container = new HBox(20);
        container.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(container, Priority.ALWAYS);

        VBox lyDoSection = taoPhanLyDoHuyPhong();
        VBox thanhToanSection = taoPhanThanhToan();
        thanhToanSection.setPrefWidth(350);

        container.getChildren().addAll(lyDoSection, thanhToanSection);

        return container;
    }

    private VBox taoPhanThanhToan() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle(
                "-fx-border-color: #EC221F;" + // màu viền
                        "-fx-border-width: 1;" + // độ dày viền (px)
                        "-fx-border-radius: 14;" + // bo góc (tùy chọn)
                        "-fx-background-radius: 14;" + // màu nền bên trong
                        "-fx-background-color: white;");
        Label lblTieuDe = new Label("Tiền hoàn trả");
        lblTieuDe.setFont(Font.font("System", FontWeight.SEMI_BOLD, 15));
        lblTieuDe.setStyle("-fx-text-fill: #EC221F;");

        VBox chiTietBox = new VBox(12);

        // Tổng tiền phòng
        HBox tongTienBox = new HBox();
        Label lblTongTienTitle = new Label("Tổng tiền phòng");
        lblTongTienPhongValue = new Label("0 VND");
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS); // Tràn hết ra nếu ô còn khoảng trống
        tongTienBox.getChildren().addAll(lblTongTienTitle, spacer1, lblTongTienPhongValue);

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
        lblTongTienCocGiaTri = new Label("0 VND");
        lblTongTienCocGiaTri.setFont(Font.font("System", FontWeight.BOLD, 14));
        // Khối căn khoảng cách
        Region spacer3 = new Region();
        HBox.setHgrow(spacer3, Priority.ALWAYS);

        totalBox.getChildren().addAll(lblTongTienCoc, spacer3, lblTongTienCocGiaTri);

        chiTietBox.getChildren().addAll(tongTienBox, cocBox, sep, totalBox);

        btnHuyNgay = new Button("Hủy");
        btnHuyNgay.setPrefWidth(Double.MAX_VALUE);
        btnHuyNgay.setPrefHeight(45);
        btnHuyNgay.getStyleClass().add("btn-huy");
        btnHuyNgay.setOnAction(e -> {
            if (!ctpdpDaChon.isEmpty() && ctpdpDaChon.size() != 0) {
                chiTietPhieuDatPhong_Controller.setDsPhongHuy(ctpdpDaChon);
                String lyDo = txtLyDoHuyPhong.getText();
                HuyPhong_Modal hPhong_Modal = new HuyPhong_Modal(this, ctpdpDaChon, lyDo);
                hPhong_Modal.hienThi();

            } else {
                Alert thongBao = new Alert(Alert.AlertType.WARNING);
                thongBao.setTitle("Thông báo");
                thongBao.setHeaderText(null);
                thongBao.setContentText("Vui lòng chọn phòng muốn hủy");
                thongBao.showAndWait();
            }

        });

        VBox.setMargin(btnHuyNgay, new Insets(10, 0, 0, 0));

        container.getChildren().addAll(lblTieuDe, chiTietBox, btnHuyNgay);

        return container;
    }

    private void capNhatThongTinThanhToan() {
        if (lblTongTienPhongValue == null || lblTongTienCocGiaTri == null)
            return;

        double tongTien = tongThanhTien;
        double tienHoan = 0.0;
        for (ChiTietPhieuDatPhong ct : ctpdpDaChon) {
            tienHoan += chiTietPhieuDatPhong_Controller.tinhTienHoan(ct);
        }

        lblTongTienPhongValue.setText(String.format("%,.0f VND", tongTien));
        lblTongTienCocGiaTri.setText(String.format("%,.0f VND", tienHoan));
    }

    private VBox taoPhanLyDoHuyPhong() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e5e7eb; -fx-border-radius: 10; -fx-border-width: 0");
        HBox.setHgrow(container, Priority.ALWAYS);

        Label lblTieuDe = new Label("Lý do hủy phòng");
        lblTieuDe.setFont(Font.font("System", FontWeight.NORMAL, 14));

        txtLyDoHuyPhong = new TextArea();
        txtLyDoHuyPhong.setPrefHeight(150);
        txtLyDoHuyPhong.setMaxHeight(200);
        txtLyDoHuyPhong.setWrapText(true);
        txtLyDoHuyPhong.setStyle(
                "-fx-border-color: #e5e7eb; -fx-border-radius: 5; -fx-background-radius: 5; -fx-control-inner-background: white;");

        container.getChildren().addAll(lblTieuDe, txtLyDoHuyPhong);

        return container;
    }
}