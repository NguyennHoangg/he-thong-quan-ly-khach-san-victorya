package view.Phong;

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
    private TextField tfTimKiem;
    private Button btnTimKiem;
    public TextArea txtLyDoHuyPhong;
    private Button btnHuy;

    private VBox vboxDanhSachPhong;
    private ScrollPane cuonDanhSach;

    private double tongThanhTien = 0.0; // tổng tiền tạm
    private Label lblTongTienPhongValue;
    private Label lblTongTienCocGiaTri;

    private final ChiTietPhieuDatPhong_Controller chiTietController = new ChiTietPhieuDatPhong_Controller();

    private final DateTimeFormatter dinhDangNgayGio = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public final List<ChiTietPhieuDatPhong> danhSachDaChon = new ArrayList<>();

    private final Image anhThuong = new Image(getClass().getResource("/img/Thuong.jpg").toExternalForm());
    private final Image anhVip = new Image(getClass().getResource("/img/VIP.jpg").toExternalForm());
    private Button btnLamMoi;

    public HuyPhong_GUI() {
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: #ffffffff;");

        VBox khungChinh = new VBox(20);
        VBox.setVgrow(khungChinh, Priority.ALWAYS);

        VBox khuVucTimKiem = taoPhanTimKiem();
        cuonDanhSach = taoPhanDanhSachPhong();

        HBox khuVucDuoi = taoPhanDuoi();

        // Nạp stylesheet (nếu có)
        try {
            khungChinh.getStylesheets().add(getClass().getResource("/css/Button.css").toExternalForm());
        } catch (Exception ex) {
            // nếu file css không tìm thấy, bỏ qua
        }

        khungChinh.getChildren().addAll(khuVucTimKiem, cuonDanhSach, khuVucDuoi);
        this.setCenter(khungChinh);
    }

    private VBox taoPhanTimKiem() {
        VBox hop = new VBox(15);

        HBox hopTimKiem = new HBox(10);
        hopTimKiem.setAlignment(Pos.CENTER_LEFT);

        tfTimKiem = new TextField();
        tfTimKiem.setPromptText("Nhập CCCD hoặc số phòng cần tìm");
        tfTimKiem.setPrefWidth(350);
        tfTimKiem.setPrefHeight(40);
        tfTimKiem.setStyle(
                "-fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #d1d5db; -fx-background-color: white; -fx-padding: 0 15;");

        btnTimKiem = new Button("Tìm kiếm");
        btnTimKiem.setPrefHeight(40);
        btnTimKiem.setPrefWidth(110);
        btnTimKiem.getStyleClass().addAll("btn");
        btnTimKiem.setOnAction(e -> {
            String maPhong = tfTimKiem.getText();
            hienThiPhong("Đã đặt", maPhong);

            danhSachDaChon.clear();
            tongThanhTien = 0;
            capNhatThongTinThanhToan();
        });

        btnLamMoi = new Button("🔄 Làm mới");
        btnLamMoi.setPrefHeight(40);
        btnLamMoi.setPrefWidth(110);
        btnLamMoi.getStyleClass().addAll("btn");
        btnLamMoi.setOnAction(e -> {
            lamMoi();
        });

        tfTimKiem.setOnAction(e -> btnTimKiem.fire());

        hopTimKiem.getChildren().addAll(tfTimKiem, btnTimKiem, btnLamMoi);
        hop.getChildren().add(hopTimKiem);

        return hop;
    }

    private ScrollPane taoPhanDanhSachPhong() {
        vboxDanhSachPhong = new VBox(15);
        vboxDanhSachPhong.setPadding(new Insets(20));
        vboxDanhSachPhong.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e5e7eb; -fx-border-radius: 10;");

        Label tieuDe = new Label("Danh sách phòng đã đặt");
        tieuDe.setStyle("-fx-font-size: 25px; -fx-font-weight: bold;");
        VBox.setMargin(tieuDe, new Insets(10, 0, 0, 0));

        vboxDanhSachPhong.getChildren().add(tieuDe);

        // Hiển thị lần đầu không hiển thị để giảm tải RAM, không load hết database ngay
        // lần đầu tiên
        // hienThiPhong("Đã đặt", null);

        ScrollPane cuon = new ScrollPane(vboxDanhSachPhong);
        cuon.setFitToWidth(true);
        cuon.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        cuon.setMinHeight(300);
        cuon.setMaxHeight(600);
        cuon.setPrefHeight(600);

        return cuon;
    }

    public void hienThiPhong(String trangThai, String timKiem) {
        vboxDanhSachPhong.getChildren().clear();

        Label lblTieuDe = new Label("Danh sách phòng đã đặt");
        lblTieuDe.setStyle("-fx-text-fill: #484848; -fx-font-weight: bold; -fx-font-size: 16px;");
        lblTieuDe.setPadding(new Insets(0, 0, 10, 0));
        vboxDanhSachPhong.getChildren().add(lblTieuDe);

        List<ChiTietPhieuDatPhong> dsPhongDaLoc = chiTietController.layDanhSachPhongDaLoc(trangThai, "Tốt", timKiem);

        for (ChiTietPhieuDatPhong ctpdp : dsPhongDaLoc) {
            vboxDanhSachPhong.getChildren().add(taoPhongItem(ctpdp));
        }

        if (dsPhongDaLoc.isEmpty()) {
            Label lblThongBao = new Label("Không có phòng phù hợp.");
            lblThongBao.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 14px;");
            vboxDanhSachPhong.getChildren().add(lblThongBao);
        }
    }

    private HBox taoPhongItem(ChiTietPhieuDatPhong chiTiet) {
        HBox hop = new HBox(15);
        hop.setPadding(new Insets(15));
        hop.setAlignment(Pos.CENTER_LEFT);
        hop.setStyle(
                "-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #e5e7eb; -fx-border-radius: 8; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1);");

        ImageView hinhAnh;
        if (chiTiet.getPhong().getLoaiPhong().getTenLoaiPhong().equalsIgnoreCase("Phòng VIP")) {
            hinhAnh = new ImageView(anhVip);
        } else {
            hinhAnh = new ImageView(anhThuong);
        }
        hinhAnh.setFitWidth(120);
        hinhAnh.setFitHeight(100);
        hinhAnh.setStyle("-fx-background-radius: 5;");

        VBox thongTin = new VBox(10);
        thongTin.setAlignment(Pos.CENTER_LEFT);
        thongTin.setMinWidth(400);
        HBox.setHgrow(thongTin, Priority.ALWAYS);

        Label lblSoPhong = new Label(chiTiet.getPhong().getSoPhong());
        lblSoPhong.setStyle("-fx-text-fill: #111827; -fx-font-weight: bold; -fx-font-size: 16px;");

        HBox hopChiTiet = new HBox(40);
        hopChiTiet.setAlignment(Pos.CENTER_LEFT);

        String ngayNhan = chiTiet.getThoiGianNhanPhong().format(dinhDangNgayGio);
        VBox nhanPhongBox = taoCotThongTin("Nhận phòng:", ngayNhan);
        VBox thoiGianBox = taoCotThongTin("Thời gian:", chiTietController.tinhNgay(chiTiet.getSoGioLuuTru()));

        hopChiTiet.getChildren().addAll(nhanPhongBox, thoiGianBox);

        String tienStr = String.format("%,.0f VND", chiTiet.tinhThanhTien());
        Label lblGia = new Label(tienStr);
        lblGia.setStyle("-fx-text-fill: #374151; -fx-font-size: 14px; -fx-font-weight: 600;");

        thongTin.getChildren().addAll(lblSoPhong, hopChiTiet, lblGia);

        RadioButton rbtnChon = new RadioButton();
        rbtnChon.setStyle("-fx-cursor: hand;");
        rbtnChon.setUserData(chiTiet);

        rbtnChon.setOnAction(e -> {
            ChiTietPhieuDatPhong ct = (ChiTietPhieuDatPhong) rbtnChon.getUserData();

            if (rbtnChon.isSelected()) {
                if (!danhSachDaChon.contains(ct))
                    danhSachDaChon.add(ct);
            } else {
                danhSachDaChon.remove(ct);
            }

            // Tính tổng bằng vòng lặp (giữ nguyên theo yêu cầu)
            double tongTam = 0;
            for (ChiTietPhieuDatPhong item : danhSachDaChon) {
                tongTam += item.tinhThanhTien();
            }
            tongThanhTien = tongTam;
            capNhatThongTinThanhToan();
        });

        hop.getChildren().addAll(hinhAnh, thongTin, rbtnChon);

        return hop;
    }

    private HBox taoPhanDuoi() {
        HBox hop = new HBox(20);
        hop.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(hop, Priority.ALWAYS);

        VBox vLyDo = taoPhanLyDoHuyPhong();
        VBox vThanhToan = taoPhanThanhToan();
        vThanhToan.setPrefWidth(350);

        hop.getChildren().addAll(vLyDo, vThanhToan);
        return hop;
    }

    private VBox taoPhanThanhToan() {
        VBox hop = new VBox(15);
        hop.setPadding(new Insets(20));
        hop.setStyle(
                "-fx-border-color: #EC221F; -fx-border-width: 1; -fx-border-radius: 14; -fx-background-radius: 14; -fx-background-color: white;");

        Label lblTieuDe = new Label("Tiền hoàn trả");
        lblTieuDe.setFont(Font.font("System", FontWeight.SEMI_BOLD, 15));
        lblTieuDe.setStyle("-fx-text-fill: #EC221F;");

        VBox chiTietBox = new VBox(12);

        // Tổng tiền phòng
        HBox hopTongTien = new HBox();
        Label lblTongTienTitle = new Label("Tổng tiền phòng");
        lblTongTienPhongValue = new Label("0 VND");
        Region khoang = new Region();
        HBox.setHgrow(khoang, Priority.ALWAYS);
        hopTongTien.getChildren().addAll(lblTongTienTitle, khoang, lblTongTienPhongValue);

        // Cọc
        HBox hopCoc = new HBox();
        Label lblCoc = new Label("Cọc");
        Label lblCocGiaTri = new Label("30%");
        Region khoang2 = new Region();
        HBox.setHgrow(khoang2, Priority.ALWAYS);
        hopCoc.getChildren().addAll(lblCoc, khoang2, lblCocGiaTri);

        Separator sep = new Separator();

        // Tổng tiền cọc
        HBox hopTotal = new HBox();
        Label lblTongTienCoc = new Label("Tổng tiền hoàn trả");
        lblTongTienCoc.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblTongTienCocGiaTri = new Label("0 VND");
        lblTongTienCocGiaTri.setFont(Font.font("System", FontWeight.BOLD, 14));
        Region khoang3 = new Region();
        HBox.setHgrow(khoang3, Priority.ALWAYS);
        hopTotal.getChildren().addAll(lblTongTienCoc, khoang3, lblTongTienCocGiaTri);

        chiTietBox.getChildren().addAll(hopTongTien, hopCoc, sep, hopTotal);

        btnHuy = new Button("Hủy");
        btnHuy.setPrefWidth(Double.MAX_VALUE);
        btnHuy.setPrefHeight(45);
        btnHuy.getStyleClass().add("btn-huy");
        btnHuy.setOnAction(e -> {
            if (!danhSachDaChon.isEmpty()) {
                chiTietController.setDsPhongHuy(danhSachDaChon);
                String lyDo = txtLyDoHuyPhong.getText();
                HuyPhong_Modal modal = new HuyPhong_Modal(danhSachDaChon, lyDo);
                modal.hienThi();
                lamMoi();
            } else {
                Alert thongBao = new Alert(Alert.AlertType.WARNING);
                thongBao.setTitle("Thông báo");
                thongBao.setHeaderText(null);
                thongBao.setContentText("Vui lòng chọn phòng muốn hủy");
                thongBao.showAndWait();
            }
        });

        VBox.setMargin(btnHuy, new Insets(10, 0, 0, 0));

        hop.getChildren().addAll(lblTieuDe, chiTietBox, btnHuy);
        return hop;
    }

    private void capNhatThongTinThanhToan() {
        if (lblTongTienPhongValue == null || lblTongTienCocGiaTri == null)
            return;

        double tienHoan = 0.0;
        for (ChiTietPhieuDatPhong ct : danhSachDaChon) {
            tienHoan += chiTietController.tinhTienHoan(ct);
        }

        lblTongTienPhongValue.setText(String.format("%,.0f VND", tongThanhTien));
        lblTongTienCocGiaTri.setText(String.format("%,.0f VND", tienHoan));
    }

    private VBox taoPhanLyDoHuyPhong() {
        VBox hop = new VBox(15);
        hop.setPadding(new Insets(20));
        hop.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e5e7eb; -fx-border-radius: 10; -fx-border-width: 0");
        HBox.setHgrow(hop, Priority.ALWAYS);

        Label lblTieuDe = new Label("Lý do hủy phòng");
        lblTieuDe.setFont(Font.font("System", FontWeight.NORMAL, 14));

        txtLyDoHuyPhong = new TextArea();
        txtLyDoHuyPhong.setPrefHeight(150);
        txtLyDoHuyPhong.setMaxHeight(200);
        txtLyDoHuyPhong.setWrapText(true);
        txtLyDoHuyPhong.setStyle(
                "-fx-border-color: #e5e7eb; -fx-border-radius: 5; -fx-background-radius: 5; -fx-control-inner-background: white;");

        hop.getChildren().addAll(lblTieuDe, txtLyDoHuyPhong);
        return hop;
    }

    private void lamMoi() {
        txtLyDoHuyPhong.clear();
        tfTimKiem.clear();
        danhSachDaChon.clear();
        tongThanhTien = 0;
        capNhatThongTinThanhToan();
        hienThiPhong("Đã đặt", null);
    }

    // Hàm tái sử dụng: tạo cột thông tin có tiêu đề nhỏ và giá trị
    private VBox taoCotThongTin(String tieuDe, String giaTri) {
        Label lblTieu = new Label(tieuDe);
        lblTieu.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");

        Label lblGiaTri = new Label(giaTri);
        lblGiaTri.setStyle("-fx-font-size: 13px; -fx-text-fill: #111827; -fx-font-weight: 600;");

        VBox cot = new VBox(3, lblTieu, lblGiaTri);
        return cot;
    }
}
