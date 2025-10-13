package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class HuyPhong_GUI extends BorderPane {
    private TextField txtNhapCCCD;
    private Button btnTimKiem;
    private TextArea txtLyDoHuyPhong;
    private Button btnHuyNgay;
    private VBox danhSachPhongContainer;

    private double giaPhongDaChon;

    private Label lblTongTienPhongValue;
    private Label lblTongTienCocGiaTri;
    private final double phanTramCoc = 0.3; // 30%

    public HuyPhong_GUI() {
        khoiTaoGiaoDien();
    }

    private void khoiTaoGiaoDien() {
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: #f0f2f5;");

        VBox mainContainer = new VBox(20);
        VBox.setVgrow(mainContainer, Priority.ALWAYS);

        VBox topSection = taoPhanTimKiem();
        ScrollPane centerSection = taoPhanDanhSachPhong();
        VBox.setVgrow(centerSection, Priority.ALWAYS);

        HBox bottomSection = taoPhanDuoi();

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
        btnTimKiem.setStyle(
                "-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-weight: bold; -fx-cursor: hand;");
        btnTimKiem.setOnMouseEntered(e -> btnTimKiem.setStyle(
                "-fx-background-color: #1d4ed8; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-weight: bold;"));
        btnTimKiem.setOnMouseExited(e -> btnTimKiem.setStyle(
                "-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-weight: bold;"));

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
        themPhongMau();

        ScrollPane scrollPane = new ScrollPane(danhSachPhongContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setMinHeight(300);
        scrollPane.setMaxHeight(600);
        scrollPane.setPrefHeight(600);

        return scrollPane;
    }

    private void themPhongMau() {
        HBox phong1 = taoPhongItem("Phòng thường", "10/10/2025", "2 ngày 1 đêm",
                "2 người lớn", "2.067.000VND");

        HBox phong2 = taoPhongItem("Phòng VIP", "12/10/2025", "2 ngày 2 đêm",
                "4 người lớn", "4.133.000VND");

        HBox phong3 = taoPhongItem("Phòng Deluxe", "15/10/2025", "3 ngày 2 đêm",
                "2 người lớn", "2.850.000VND");

        danhSachPhongContainer.getChildren().addAll(phong1, phong2, phong3);
    }

    private HBox taoPhongItem(String tenPhong, String ngayNhanPhong,
            String thoiGian, String soKhach, String gia) {

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

        Region hinhAnh = new Region();
        hinhAnh.setPrefSize(120, 100);
        hinhAnh.setStyle("""
                -fx-background-color: #d1d5db;
                -fx-background-radius: 5;
                """);

        VBox thongTinPhong = new VBox(10);
        thongTinPhong.setAlignment(Pos.CENTER_LEFT);
        thongTinPhong.setMinWidth(400);
        HBox.setHgrow(thongTinPhong, Priority.ALWAYS);

        Label lblTenPhong = new Label(tenPhong);
        lblTenPhong.setStyle("-fx-text-fill: #111827; -fx-font-weight: bold; -fx-font-size: 16px;");

        HBox thongTinChiTiet = new HBox(40);
        thongTinChiTiet.setAlignment(Pos.CENTER_LEFT);

        VBox nhanPhongBox = new VBox(3);
        Label lblNhanPhong = new Label("Nhận phòng:");
        lblNhanPhong.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");
        Label lblNgayNhan = new Label(ngayNhanPhong);
        lblNgayNhan.setStyle("-fx-font-size: 13px; -fx-text-fill: #111827; -fx-font-weight: 600;");
        nhanPhongBox.getChildren().addAll(lblNhanPhong, lblNgayNhan);

        VBox thoiGianBox = new VBox(3);
        Label lblThoiGianTitle = new Label("Thời gian:");
        lblThoiGianTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");
        Label lblThoiGianValue = new Label(thoiGian);
        lblThoiGianValue.setStyle("-fx-font-size: 13px; -fx-text-fill: #111827; -fx-font-weight: 600;");
        thoiGianBox.getChildren().addAll(lblThoiGianTitle, lblThoiGianValue);

        VBox soKhachBox = new VBox(3);
        Label lblSoKhachTitle = new Label("Số lượng khách:");
        lblSoKhachTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");
        Label lblSoKhachValue = new Label(soKhach);
        lblSoKhachValue.setStyle("-fx-font-size: 13px; -fx-text-fill: #111827; -fx-font-weight: 600;");
        soKhachBox.getChildren().addAll(lblSoKhachTitle, lblSoKhachValue);

        thongTinChiTiet.getChildren().addAll(nhanPhongBox, thoiGianBox, soKhachBox);

        Label lblGia = new Label(gia);
        lblGia.setStyle("-fx-text-fill: #374151; -fx-font-size: 14px; -fx-font-weight: 600;");

        thongTinPhong.getChildren().addAll(lblTenPhong, thongTinChiTiet, lblGia);

        RadioButton radioBtn = new RadioButton();
        radioBtn.setStyle("-fx-cursor: hand;");

        // Xử lý sự kiện khi chọn radioButton hủy
        radioBtn.setOnAction(e -> {
            if (radioBtn.isSelected()) {
                giaPhongDaChon += chuyenDoiGia(gia);
            } else {
                giaPhongDaChon -= chuyenDoiGia(gia);
            }
            capNhatThongTinThanhToan();
        });

        container.getChildren().addAll(hinhAnh, thongTinPhong, radioBtn);

        return container;
    }

    private double chuyenDoiGia(String giaChu) {
        try {
            return Double.parseDouble(giaChu.replace(".", "").replace("VND", "").trim());
        } catch (Exception e) {
            return 0;
        }
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

    private VBox taoPhanThanhToan() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e5e7eb; -fx-border-radius: 10;");

        Label lblTieuDe = new Label("Tiền hoàn trả");
        lblTieuDe.setFont(Font.font("System", FontWeight.SEMI_BOLD, 15));
        lblTieuDe.setStyle("-fx-text-fill: #10b981;");

        VBox chiTietBox = new VBox(12);

        // Tổng tiền phòng
        HBox tongTienBox = new HBox();
        Label lblTongTienTitle = new Label("Tổng tiền phòng");
        lblTongTienPhongValue = new Label("0 VND");
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        tongTienBox.getChildren().addAll(lblTongTienTitle, spacer1, lblTongTienPhongValue);

        // Cọc
        HBox cocBox = taoHangThanhToan("Cọc", "30%", false);

        Separator sep = new Separator();

        // Total price
        HBox totalBox = new HBox();
        Label lblTongTienCoc = new Label("Tổng tiền cọc");
        lblTongTienCoc.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblTongTienCocGiaTri = new Label("0 VND");
        lblTongTienCocGiaTri.setFont(Font.font("System", FontWeight.BOLD, 14));
        // Khối căn khoảng cách
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        totalBox.getChildren().addAll(lblTongTienCoc, spacer2, lblTongTienCocGiaTri);

        chiTietBox.getChildren().addAll(tongTienBox, cocBox, sep, totalBox);

        btnHuyNgay = new Button("Hủy ngay");
        btnHuyNgay.setPrefWidth(Double.MAX_VALUE);
        btnHuyNgay.setPrefHeight(45);
        btnHuyNgay.setStyle(
                "-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-weight: bold;");
        VBox.setMargin(btnHuyNgay, new Insets(10, 0, 0, 0));

        container.getChildren().addAll(lblTieuDe, chiTietBox, btnHuyNgay);

        return container;
    }

    private HBox taoHangThanhToan(String label, String value, boolean isBold) {
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        Label lblLeft = new Label(label);
        if (isBold)
            lblLeft.setFont(Font.font("System", FontWeight.BOLD, 14));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblRight = new Label(value);
        if (isBold)
            lblRight.setFont(Font.font("System", FontWeight.BOLD, 14));

        container.getChildren().addAll(lblLeft, spacer, lblRight);
        return container;
    }

    private void capNhatThongTinThanhToan() {
        if (lblTongTienPhongValue == null || lblTongTienCocGiaTri == null)
            return;

        double tongTien = giaPhongDaChon;
        double tienHoan = tongTien * (1 - phanTramCoc);

        lblTongTienPhongValue.setText(String.format("%,.0f VND", tongTien));
        lblTongTienCocGiaTri.setText(String.format("%,.0f VND", tienHoan));
    }
}