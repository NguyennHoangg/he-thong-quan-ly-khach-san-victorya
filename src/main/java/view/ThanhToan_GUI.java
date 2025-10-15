package view;

import java.util.Arrays;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Trang thanh toán - phiên bản đơn giản
 */
public class ThanhToan_GUI extends BorderPane {

    private TextField txtNhapCCCD;
    private TableView<BillRow> table = new TableView<>();
    private Label lblTong = new Label("2.300.000");
    private Label lblKM = new Label("0%");
    private Label lblVAT = new Label("10%");
    private Label lblTotal = new Label("2.530.000");

    public ThanhToan_GUI() {
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f0f2f5;");
        getStylesheets().add(getClass().getResource("/css/Button.css").toExternalForm());

        Label lblTieuDe = new Label("Thanh toán");
        lblTieuDe.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-padding: 10;");

        HBox containChinh = new HBox(20);
        VBox containTrai = new VBox(20);
        containTrai.setPadding(new Insets(10, 20, 10, 20));
        
        VBox containPhai = new VBox(20);
        containPhai.setAlignment(Pos.CENTER);
        containPhai.setPadding(new Insets(10, 20, 10, 20));

        containTrai.getChildren().addAll(lblTieuDe, taoPhanTimKiem(), taoBang(), taoChonKhuyenMai());
        containPhai.getChildren().addAll(taoTongTien(), taoTheThanhToan());
        containChinh.getChildren().addAll(containTrai, containPhai);
        
        setCenter(containChinh);
    }

    private HBox taoPhanTimKiem() {
        HBox container = new HBox(10);
        container.setPadding(new Insets(15));
        container.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        txtNhapCCCD = new TextField();
        txtNhapCCCD.setPromptText("Nhập CCCD");
        txtNhapCCCD.setPrefWidth(350);
        txtNhapCCCD.setPrefHeight(35);

        Button btnTimKiem = new Button("Tìm kiếm");
        btnTimKiem.setPrefHeight(35);
        btnTimKiem.getStyleClass().add("btn");

        container.getChildren().addAll(txtNhapCCCD, btnTimKiem);
        return container;
    }

    private VBox taoBang() {
        TableColumn<BillRow, String> colPhong = new TableColumn<>("Phòng");
        colPhong.setCellValueFactory(new PropertyValueFactory<>("phong"));
        colPhong.setPrefWidth(100);

        TableColumn<BillRow, String> colLoai = new TableColumn<>("Loại Phòng");
        colLoai.setCellValueFactory(new PropertyValueFactory<>("loaiPhong"));
        colLoai.setPrefWidth(150);

        TableColumn<BillRow, String> colDV = new TableColumn<>("Dịch vụ");
        colDV.setCellValueFactory(new PropertyValueFactory<>("dichVu"));
        colDV.setPrefWidth(180);

        TableColumn<BillRow, String> colTG = new TableColumn<>("Thời gian lưu trú");
        colTG.setCellValueFactory(new PropertyValueFactory<>("thoiGian"));
        colTG.setPrefWidth(180);

        TableColumn<BillRow, String> colTong = new TableColumn<>("Tổng tiền");
        colTong.setCellValueFactory(new PropertyValueFactory<>("tongTien"));
        colTong.setPrefWidth(150);

        table.getColumns().addAll(colPhong, colLoai, colDV, colTG, colTong);
        table.setPrefWidth(720);
        table.setPrefHeight(340);
        table.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        seedSample();

        return new VBox(table);
    }

    private HBox taoChonKhuyenMai() {
        HBox container = new HBox(15);
        container.setPadding(new Insets(15));
        container.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #ffd700; -fx-border-width: 2;");
        container.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Mã khuyến mãi");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnSelect = new Button("✨ Chọn mã khuyến mãi");
        btnSelect.setPrefHeight(40);
        btnSelect.setStyle("-fx-background-color: #f0f0f0; -fx-font-size: 14px;");
        btnSelect.setOnAction(e -> showSelectPromotionDialog(btnSelect));

        container.getChildren().addAll(title, spacer, btnSelect);
        return container;
    }

    private void showSelectPromotionDialog(Button target) {
        Stage dlg = new Stage();
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.setTitle("Chọn khuyến mãi");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white;");

        Label title = new Label("Chọn khuyến mãi");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<PromotionOption> tv = new TableView<>();
        tv.setPrefHeight(300);

        TableColumn<PromotionOption, String> cName = new TableColumn<>("Tên KM");
        cName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<PromotionOption, String> cMax = new TableColumn<>("Giảm tối đa");
        cMax.setCellValueFactory(new PropertyValueFactory<>("maxOff"));

        TableColumn<PromotionOption, String> cDesc = new TableColumn<>("Mô tả");
        cDesc.setCellValueFactory(new PropertyValueFactory<>("description"));

        tv.getColumns().addAll(cName, cMax, cDesc);
        tv.getItems().addAll(
            new PromotionOption("KM10", "Giảm 10%", "100.000đ", "Tất cả phòng"),
            new PromotionOption("NOEL", "Giảm 15%", "150.000đ", "Phòng VIP"),
            new PromotionOption("FAMILY", "Giảm 50.000đ", "50.000đ", "Từ 2 phòng")
        );

        Button btnChon = new Button("Chọn");
        btnChon.getStyleClass().add("btn");
        btnChon.setPrefWidth(120);
        btnChon.setOnAction(e -> {
            PromotionOption p = tv.getSelectionModel().getSelectedItem();
            if (p != null) {
                target.setText(p.name);
                lblKM.setText(p.maxOff);
                dlg.close();
            }
        });

        root.getChildren().addAll(title, tv, btnChon);
        dlg.setScene(new javafx.scene.Scene(root, 600, 450));
        dlg.showAndWait();
    }

    public static class PromotionOption {
        public final String code, name, maxOff, description;
        
        public PromotionOption(String code, String name, String maxOff, String description) {
            this.code = code;
            this.name = name;
            this.maxOff = maxOff;
            this.description = description;
        }

        public String getCode() { return code; }
        public String getName() { return name; }
        public String getMaxOff() { return maxOff; }
        public String getDescription() { return description; }
    }

    private VBox taoTongTien() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(20));
        container.setPrefSize(360, 180);
        container.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #4ade80; -fx-border-width: 2;");

        Label lblTieuDe = new Label("Tổng tiền");
        lblTieuDe.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #22c55e;");

        container.getChildren().addAll(
            lblTieuDe,
            taoDongTong("Tổng tiền", lblTong),
            taoDongTong("Khuyến mãi", lblKM),
            taoDongTong("VAT", lblVAT),
            taoDongTong("Total", lblTotal)
        );

        return container;
    }

    private HBox taoDongTong(String title, Label value) {
        HBox line = new HBox();
        line.setAlignment(Pos.CENTER_LEFT);
        
        Label l = new Label(title);
        l.setStyle("-fx-font-size: 15px;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        value.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        line.getChildren().addAll(l, spacer, value);
        return line;
    }

    private VBox taoTheThanhToan() {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefSize(360, 250);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #ddd; -fx-border-width: 1;");

        Label lblTitle = new Label("Phương thức thanh toán");
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button btnCash = new Button("Tiền mặt");
        btnCash.setPrefWidth(300);
        btnCash.setPrefHeight(45);
        btnCash.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 8;");
        btnCash.setOnAction(e -> showCashDialog());

        Label sub2 = new Label("Hoặc");
        sub2.setAlignment(Pos.CENTER);

        Button btnMomo = new Button("Momo");
        btnMomo.setPrefWidth(300);
        btnMomo.setPrefHeight(45);
        btnMomo.setStyle("-fx-background-color: #8b5cf6; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 8;");
        btnMomo.setOnAction(e -> showMomoDialog());

        card.getChildren().addAll(lblTitle, btnCash, sub2, btnMomo);
        return card;
    }

    private void showCashDialog() {
        Stage dlg = new Stage();
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.setTitle("Thanh toán tiền mặt");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white;");

        Label title = new Label("Thanh toán tiền mặt");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox rowTotal = createRow("Tiền cần thu", new Label(lblTotal.getText()));
        Label lbNhan = new Label("0");
        lbNhan.setStyle("-fx-font-weight: bold;");
        HBox rowNhan = createRow("Tiền nhận", lbNhan);

        Label lbThoi = new Label("0");
        lbThoi.setStyle("-fx-font-weight: bold; -fx-text-fill: #22c55e;");

        VBox denomGroup = new VBox(10);
        long[] currentNhan = {0};
        long[][] groups = {{500_000, 200_000, 100_000}, {50_000, 20_000, 10_000}, {5_000, 2_000, 1_000}};
        
        for (long[] row : groups) {
            HBox r = new HBox(8);
            for (long d : row) {
                Button b = new Button((d/1000) + "k");
                b.setPrefWidth(80);
                b.setPrefHeight(35);
                b.getStyleClass().add("btn");
                b.setOnAction(e -> {
                    currentNhan[0] += d;
                    lbNhan.setText(formatVnd(currentNhan[0]));
                    long thoi = Math.max(0, currentNhan[0] - parseVnd(lblTotal.getText()));
                    lbThoi.setText(formatVnd(thoi));
                });
                r.getChildren().add(b);
            }
            denomGroup.getChildren().add(r);
        }

        HBox rowChange = createRow("Tiền thối", lbThoi);

        Button btnXacNhan = new Button("Xác nhận");
        btnXacNhan.getStyleClass().add("btn");
        btnXacNhan.setPrefWidth(120);
        btnXacNhan.setOnAction(e -> dlg.close());

        root.getChildren().addAll(title, rowTotal, rowNhan, denomGroup, rowChange, btnXacNhan);
        dlg.setScene(new javafx.scene.Scene(root, 500, 450));
        dlg.showAndWait();
    }

    private HBox createRow(String label, Label value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(label);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        value.setStyle("-fx-font-weight: bold;");
        row.getChildren().addAll(lbl, spacer, value);
        return row;
    }

    private void showMomoDialog() {
        Stage dlg = new Stage();
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.setTitle("Thanh toán Momo");

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white;");

        Label title = new Label("Quét mã QR để thanh toán");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Rectangle qrBox = new Rectangle(300, 300);
        qrBox.setFill(Color.web("#f5f5f5"));
        qrBox.setStroke(Color.web("#ddd"));
        qrBox.setArcWidth(10);
        qrBox.setArcHeight(10);

        Button btnClose = new Button("Đóng");
        btnClose.getStyleClass().add("btn");
        btnClose.setPrefWidth(120);
        btnClose.setOnAction(e -> dlg.close());

        root.getChildren().addAll(title, qrBox, btnClose);
        dlg.setScene(new javafx.scene.Scene(root, 400, 450));
        dlg.showAndWait();
    }

    private long parseVnd(String s) {
        if (s == null) return 0;
        String digits = s.replaceAll("[^0-9]", "");
        return digits.isEmpty() ? 0 : Long.parseLong(digits);
    }

    private String formatVnd(long v) {
        return String.format("%,d", v).replace(',', '.');
    }

    private void seedSample() {
        table.getItems().setAll(Arrays.asList(
            new BillRow("101", "Thường", "Ăn sáng", "36 giờ", "1.000.000"),
            new BillRow("201", "VIP", "Tất cả dịch vụ", "36 giờ", "1.000.000"),
            new BillRow("301", "VIP", "Tất cả dịch vụ", "8 giờ", "300.000")
        ));
    }

    public static class BillRow {
        private final String phong, loaiPhong, dichVu, thoiGian, tongTien;

        public BillRow(String phong, String loaiPhong, String dichVu, String thoiGian, String tongTien) {
            this.phong = phong;
            this.loaiPhong = loaiPhong;
            this.dichVu = dichVu;
            this.thoiGian = thoiGian;
            this.tongTien = tongTien;
        }

        public String getPhong() { return phong; }
        public String getLoaiPhong() { return loaiPhong; }
        public String getDichVu() { return dichVu; }
        public String getThoiGian() { return thoiGian; }
        public String getTongTien() { return tongTien; }
    }
}