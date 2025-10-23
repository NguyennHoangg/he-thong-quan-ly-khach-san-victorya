package view;

import controller.ThanhToan_Controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.ChiTietPhieuDatPhong;
import model.PhieuDatPhong;

/**
 * Giao diện thanh toán - hiển thị thông tin phòng, dịch vụ và xử lý thanh toán
 * Hỗ trợ thanh toán qua Momo (QR code) và tiền mặt
 */
public class ThanhToan_GUI extends BorderPane {

    private ThanhToan_Controller thanhToan_Controller = new ThanhToan_Controller();

    private TextField txtNhapCCCD;
    private Button btnTimKiem;
    private TableView<ChiTietPhieuDatPhong> tablePhong;
    private ObservableList<ChiTietPhieuDatPhong> dataList;
    private HBox boxTongTien;
    private HBox boxKhuyenMai;
    private HBox boxVAT;
    private HBox boxTotal;
    private RadioButton rbTienMat;
    private RadioButton rbMomo;
    private ImageView qrCodeImage;
    private Button btnThanhToan;
    private Button btnChonKhuyenMai;
    private Label lblKhuyenMaiSelected;
    private VBox boxThanhToanTienMat;
    private TextField txtTienNhan;
    private Label lblTienTraLai;
    private StackPane qrContainer;


    private PhieuDatPhong phieuDatPhong;

    public ThanhToan_GUI() {
        this.phieuDatPhong = new PhieuDatPhong();
        khoiTao();
    }

    /**
     * Khởi tạo giao diện thanh toán
     */
    private void khoiTao() {
        this.setPadding(new Insets(20));
        
        // Container chính
        HBox mainContainer = new HBox(20);
        mainContainer.setPadding(new Insets(20));
        
        // Bên trái - Bảng thông tin phòng
        VBox leftSide = taoBenTrai();
        
        // Bên phải - Thanh toán
        VBox rightSide = taoBenPhai();
        
        mainContainer.getChildren().addAll(leftSide, rightSide);
        this.setCenter(mainContainer);
    }

    /**
     * Tạo giao diện bên trái (bảng phòng + tổng tiền)
     */
    private VBox taoBenTrai() {
        VBox leftSide = new VBox(15);
        leftSide.setPrefWidth(850);
        
        // Ô tìm kiếm
        HBox searchBox = taoOTimKiem();
        
        // Bảng phòng
        tablePhong = taoBangPhong();
        
        // Phần tổng kết
        VBox summaryBox = taoPhanTongKet();
        
        leftSide.getChildren().addAll(searchBox, tablePhong, summaryBox);
        return leftSide;
    }

    /**
     * Tạo ô tìm kiếm CCCD khách hàng
     */
    private HBox taoOTimKiem() {
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(15));
        searchBox.setStyle("-fx-background-color: #F7F9FC; -fx-background-radius: 8;");
        
        txtNhapCCCD = new TextField();
        txtNhapCCCD.setPromptText("Nhập CCCD khách hàng");
        txtNhapCCCD.setPrefWidth(300);
        txtNhapCCCD.setPrefHeight(40);
        txtNhapCCCD.setStyle("-fx-background-color: #f7f7f7; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 0 15;");
        
        btnTimKiem = new Button("Tìm kiếm");
        btnTimKiem.setPrefHeight(40);
        btnTimKiem.setPrefWidth(120);
        btnTimKiem.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
        btnTimKiem.setOnAction(e -> loadData(txtNhapCCCD.getText()));
        
        searchBox.getChildren().addAll(txtNhapCCCD, btnTimKiem);
        return searchBox;
    }

    /**
     * Tạo bảng hiển thị thông tin phòng và dịch vụ
     */
    private TableView<ChiTietPhieuDatPhong> taoBangPhong() {
        TableView<ChiTietPhieuDatPhong> table = new TableView<>();
        table.setPrefHeight(500);
        table.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        
        // Load CSS cho bảng
        table.getStylesheets().add(getClass().getResource("/css/ThanhToan.css").toExternalForm());
        table.getStyleClass().add("payment-table");
        
        // Placeholder khi không có dữ liệu
        Label placeholder = new Label("Chưa có dữ liệu");
        placeholder.setStyle("-fx-font-size: 16px; -fx-text-fill: #9ca3af; -fx-padding: 50;");
        table.setPlaceholder(placeholder);
        
        // Các cột - giống y hệt trong ảnh
        TableColumn<ChiTietPhieuDatPhong, String> colPhong = new TableColumn<>("Phòng");
        colPhong.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhong().getSoPhong()));
        colPhong.setPrefWidth(100);
        colPhong.setStyle("-fx-alignment: CENTER;");
        
        TableColumn<ChiTietPhieuDatPhong, String> colLoaiPhong = new TableColumn<>("Loại Phòng");
        colLoaiPhong.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhong().getLoaiPhong().getTenLoaiPhong()));
        colLoaiPhong.setPrefWidth(130);
        colLoaiPhong.setStyle("-fx-alignment: CENTER;");
        
        TableColumn<ChiTietPhieuDatPhong, String> colDichVu = new TableColumn<>("Dịch vụ");
        colDichVu.setCellValueFactory(cellData -> {
            String dichVuStr = cellData.getValue().getDsachDichVu().stream()
                .map(dv -> dv.getTenDichVu())
                .collect(java.util.stream.Collectors.joining(", "));
            return new SimpleStringProperty(dichVuStr.isEmpty() ? "Không có" : dichVuStr);
        });
        colDichVu.setPrefWidth(160);
        colDichVu.setStyle("-fx-alignment: CENTER;");
        
        TableColumn<ChiTietPhieuDatPhong, String> colThoiGian = new TableColumn<>("Thời gian lưu trú");
        colThoiGian.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getSoGioLuuTru())));
        colThoiGian.setPrefWidth(320);
        colThoiGian.setStyle("-fx-alignment: CENTER;");
        
        TableColumn<ChiTietPhieuDatPhong, String> colTongTien = new TableColumn<>("Tổng tiền");
        colTongTien.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getThanhTien())));
        colTongTien.setPrefWidth(130);
        colTongTien.setStyle("-fx-alignment: CENTER;");
        
        // Thêm các cột vào bảng
        table.getColumns().add(colPhong);
        table.getColumns().add(colLoaiPhong);
        table.getColumns().add(colDichVu);
        table.getColumns().add(colThoiGian);
        table.getColumns().add(colTongTien);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        
        return table;
    }

    public void loadData(String CCCD){
        phieuDatPhong = thanhToan_Controller.getPhieuDatPhongTheoCCCD(CCCD);
        dataList = FXCollections.observableArrayList();
        for(ChiTietPhieuDatPhong ct : phieuDatPhong.getDsachPhieuDatPhong()){
            dataList.add(ct);
        }
        tablePhong.setItems(dataList);
    }

    /**
     * Tạo phần tổng kết tiền (tổng tiền, khuyến mãi, VAT, total)
     */
    private VBox taoPhanTongKet() {
        VBox summary = new VBox(10);
        summary.setPadding(new Insets(20));
        summary.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        summary.setAlignment(Pos.CENTER_RIGHT);
        
        // Box chọn khuyến mãi
        HBox khuyenMaiSelector = taoOChonKhuyenMai();
        
        boxTongTien = taoLabelTongKet("Tổng tiền:", "2.300.000", false);
        boxKhuyenMai = taoLabelTongKet("Khuyến mãi:", "0%", false);
        boxVAT = taoLabelTongKet("VAT:", "10%", false);
        
        Separator separator = new Separator();
        separator.setPrefWidth(300);
        
        boxTotal = taoLabelTongKet("Total:", "2.530.000", true);
        
        summary.getChildren().addAll(khuyenMaiSelector, boxTongTien, boxKhuyenMai, boxVAT, separator, boxTotal);
        return summary;
    }
    
    /**
     * Tạo ô chọn mã khuyến mãi với button mở modal
     */
    private HBox taoOChonKhuyenMai() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: #f3f4f6; -fx-background-radius: 8;");
        
        Label lblTitle = new Label("Mã khuyến mãi:");
        lblTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #374151;");
        
        lblKhuyenMaiSelected = new Label("Chưa chọn");
        lblKhuyenMaiSelected.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");
        
        btnChonKhuyenMai = new Button("Chọn");
        btnChonKhuyenMai.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-size: 13px; -fx-background-radius: 5; -fx-cursor: hand; -fx-padding: 5 15;");
        btnChonKhuyenMai.setOnAction(e -> hienModalKhuyenMai());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        box.getChildren().addAll(lblTitle, lblKhuyenMaiSelected, spacer, btnChonKhuyenMai);
        return box;
    }
    
    /**
     * Hiển thị modal chọn khuyến mãi
     * Modal chứa bảng danh sách khuyến mãi với tên, % giảm giá, trạng thái
     */
    private void hienModalKhuyenMai() {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Chọn Khuyến Mãi");
        
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: white;");
        
        Label title = new Label("Danh Sách Khuyến Mãi");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");
        
        // Table khuyến mãi
        TableView<KhuyenMaiRow> tableKM = new TableView<>();
        tableKM.setPrefHeight(400);
        tableKM.setPrefWidth(650);
        
        // Load CSS
        tableKM.getStylesheets().add(getClass().getResource("/css/ThanhToan.css").toExternalForm());
        tableKM.getStyleClass().add("payment-table");
        
        // Columns
        TableColumn<KhuyenMaiRow, String> colMaKM = new TableColumn<>("Mã KM");
        colMaKM.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMaKM()));
        colMaKM.setPrefWidth(100);
        
        TableColumn<KhuyenMaiRow, String> colTenKM = new TableColumn<>("Tên Khuyến Mãi");
        colTenKM.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTenKM()));
        colTenKM.setPrefWidth(250);
        
        TableColumn<KhuyenMaiRow, String> colPhanTram = new TableColumn<>("% Giảm Giá");
        colPhanTram.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhanTramGiam()));
        colPhanTram.setPrefWidth(120);
        colPhanTram.setStyle("-fx-alignment: CENTER;");
        
        TableColumn<KhuyenMaiRow, String> colTrangThai = new TableColumn<>("Trạng Thái");
        colTrangThai.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTrangThai()));
        colTrangThai.setPrefWidth(130);
        colTrangThai.setStyle("-fx-alignment: CENTER;");
        
        // Custom cell factory cho trạng thái
        colTrangThai.setCellFactory(column -> new TableCell<KhuyenMaiRow, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Hoạt động")) {
                        setStyle("-fx-text-fill: #16a34a; -fx-font-weight: 600;");
                    } else {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: 600;");
                    }
                }
            }
        });
        
        // Thêm các cột vào bảng khuyến mãi
        tableKM.getColumns().add(colMaKM);
        tableKM.getColumns().add(colTenKM);
        tableKM.getColumns().add(colPhanTram);
        tableKM.getColumns().add(colTrangThai);
        
        // Sample data khuyến mãi
        ObservableList<KhuyenMaiRow> dataKM = FXCollections.observableArrayList(
            new KhuyenMaiRow("KM001", "Khuyến mãi mùa hè", "10%", "Hoạt động"),
            new KhuyenMaiRow("KM002", "Giảm giá cuối tuần", "15%", "Hoạt động"),
            new KhuyenMaiRow("KM003", "Ưu đãi khách VIP", "20%", "Hoạt động"),
            new KhuyenMaiRow("KM004", "Khuyến mãi sinh nhật", "25%", "Không hoạt động"),
            new KhuyenMaiRow("KM005", "Giảm giá lễ tết", "30%", "Hoạt động")
        );
        tableKM.setItems(dataKM);
        
        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnChon = new Button("Chọn");
        btnChon.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 6; -fx-padding: 8 25; -fx-cursor: hand;");
        btnChon.setOnAction(e -> {
            KhuyenMaiRow selected = tableKM.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (selected.getTrangThai().equals("Hoạt động")) {
                    lblKhuyenMaiSelected.setText(selected.getTenKM() + " (" + selected.getPhanTramGiam() + ")");
                    lblKhuyenMaiSelected.setStyle("-fx-font-size: 14px; -fx-text-fill: #16a34a; -fx-font-weight: 600;");
                    modal.close();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Cảnh báo");
                    alert.setHeaderText(null);
                    alert.setContentText("Khuyến mãi này không hoạt động!");
                    alert.showAndWait();
                }
            }
        });
        
        Button btnHuy = new Button("Hủy");
        btnHuy.setStyle("-fx-background-color: #6b7280; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 6; -fx-padding: 8 25; -fx-cursor: hand;");
        btnHuy.setOnAction(e -> modal.close());
        
        buttonBox.getChildren().addAll(btnHuy, btnChon);
        
        container.getChildren().addAll(title, tableKM, buttonBox);
        
        Scene scene = new Scene(container);
        modal.setScene(scene);
        modal.showAndWait();
    }

    /**
     * Tạo label hiển thị tổng kết tiền (tổng tiền, VAT, khuyến mãi, total)
     * @param title Tiêu đề (VD: "Tổng tiền:", "VAT:")
     * @param value Giá trị (VD: "2.300.000", "10%")
     * @param isTotal True nếu là dòng Total (sẽ in đậm và to hơn)
     */
    private HBox taoLabelTongKet(String title, String value, boolean isTotal) {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setSpacing(20);
        
        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: " + (isTotal ? "18" : "14") + "px; -fx-font-weight: " + (isTotal ? "bold" : "normal") + "; -fx-text-fill: #666;");
        
        Label lblValue = new Label(value);
        lblValue.setStyle("-fx-font-size: " + (isTotal ? "20" : "16") + "px; -fx-font-weight: bold; -fx-text-fill: " + (isTotal ? "#2563eb" : "#333") + ";");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        box.getChildren().addAll(lblTitle, spacer, lblValue);
        return box;
    }

    /**
     * Tạo giao diện bên phải (phương thức thanh toán, QR code, tiền mặt, button thanh toán)
     */
    private VBox taoBenPhai() {
        VBox rightSide = new VBox(25);
        rightSide.setPrefWidth(420);
        rightSide.setMaxWidth(420);
        rightSide.setPadding(new Insets(30, 25, 25, 25));
        rightSide.setAlignment(Pos.TOP_CENTER);
        rightSide.setStyle("-fx-background-color: #FAFAFA; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);");
        
        // Title
        Label title = new Label("Thanh Toán");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        title.setAlignment(Pos.CENTER);
        
        // Payment method
        VBox paymentMethod = taoPhuongThucThanhToan();
        
        // Cash payment box (tiền nhận vào, tiền trả lại, buttons làm tròn)
        boxThanhToanTienMat = taoBoxThanhToanTienMat();
        boxThanhToanTienMat.setVisible(false);
        boxThanhToanTienMat.setManaged(false);
        
        // QR Code Container
        qrContainer = new StackPane();
        qrContainer.setPrefSize(280, 280);
        qrContainer.setMaxSize(280, 280);
        qrContainer.setAlignment(Pos.CENTER);
        qrContainer.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;");
        
        // QR Code Image - tạo QR code giả với pattern đen trắng
        qrCodeImage = taoHinhQRCode();
        qrContainer.getChildren().add(qrCodeImage);
        
        // Payment button
        btnThanhToan = new Button("Thanh Toán");
        btnThanhToan.setPrefWidth(350);
        btnThanhToan.setPrefHeight(55);
        btnThanhToan.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        btnThanhToan.setOnMouseEntered(e -> btnThanhToan.setStyle("-fx-background-color: #15803d; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));
        btnThanhToan.setOnMouseExited(e -> btnThanhToan.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        rightSide.getChildren().addAll(title, paymentMethod, boxThanhToanTienMat, qrContainer, spacer, btnThanhToan);
        return rightSide;
    }
    
    /**
     * Tạo hình ảnh QR code (pattern giả hoặc load từ resources)
     */
    private ImageView taoHinhQRCode() {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(240);
        imageView.setFitHeight(240);
        imageView.setPreserveRatio(true);
        
        // Tạo QR code pattern (giống hình)
        try {
            // Thử load QR code từ resources nếu có
            Image qrImage = new Image(getClass().getResourceAsStream("/img/momo-qr.png"));
            imageView.setImage(qrImage);
        } catch (Exception e) {
            // Nếu không có, tạo canvas QR pattern đơn giản
            javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(240, 240);
            javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();
            
            // Background trắng
            gc.setFill(javafx.scene.paint.Color.WHITE);
            gc.fillRect(0, 0, 240, 240);
            
            // Vẽ pattern QR giả (3 góc vuông lớn + random pattern)
            gc.setFill(javafx.scene.paint.Color.BLACK);
            
            // Góc trên trái
            gc.fillRect(10, 10, 60, 60);
            gc.setFill(javafx.scene.paint.Color.WHITE);
            gc.fillRect(20, 20, 40, 40);
            gc.setFill(javafx.scene.paint.Color.BLACK);
            gc.fillRect(30, 30, 20, 20);
            
            // Góc trên phải
            gc.fillRect(170, 10, 60, 60);
            gc.setFill(javafx.scene.paint.Color.WHITE);
            gc.fillRect(180, 20, 40, 40);
            gc.setFill(javafx.scene.paint.Color.BLACK);
            gc.fillRect(190, 30, 20, 20);
            
            // Góc dưới trái
            gc.fillRect(10, 170, 60, 60);
            gc.setFill(javafx.scene.paint.Color.WHITE);
            gc.fillRect(20, 180, 40, 40);
            gc.setFill(javafx.scene.paint.Color.BLACK);
            gc.fillRect(30, 190, 20, 20);
            
            // Random QR pattern ở giữa
            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < 20; j++) {
                    if (Math.random() > 0.5) {
                        gc.fillRect(10 + i * 11, 10 + j * 11, 10, 10);
                    }
                }
            }
            
            javafx.scene.image.WritableImage qrPattern = new javafx.scene.image.WritableImage(240, 240);
            canvas.snapshot(null, qrPattern);
            imageView.setImage(qrPattern);
        }
        
        return imageView;
    }

    /**
     * Tạo phần chọn phương thức thanh toán (Radio: Tiền mặt / Momo)
     */
    private VBox taoPhuongThucThanhToan() {
        VBox paymentBox = new VBox(12);
        paymentBox.setAlignment(Pos.CENTER_LEFT);
        paymentBox.setPadding(new Insets(0, 0, 10, 0));
        
        Label label = new Label("Chọn phương thức thanh toán:");
        label.setStyle("-fx-font-size: 15px; -fx-font-weight: 600; -fx-text-fill: #374151;");
        
        ToggleGroup group = new ToggleGroup();
        
        // Tiền mặt
        rbTienMat = new RadioButton("Tiền mặt");
        rbTienMat.setToggleGroup(group);
        rbTienMat.setStyle("-fx-font-size: 14px; -fx-text-fill: #4b5563;");
        
        // Momo
        rbMomo = new RadioButton("Momo");
        rbMomo.setToggleGroup(group);
        rbMomo.setSelected(true);
        rbMomo.setStyle("-fx-font-size: 14px; -fx-text-fill: #4b5563; -fx-font-weight: 600;");
        
        // Listener để thay đổi QR code hoặc cash payment box
        rbMomo.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                // Show QR code, hide cash payment
                qrContainer.setVisible(true);
                qrContainer.setManaged(true);
                boxThanhToanTienMat.setVisible(false);
                boxThanhToanTienMat.setManaged(false);
            }
        });
        
        rbTienMat.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                // Hide QR code, show cash payment
                qrContainer.setVisible(false);
                qrContainer.setManaged(false);
                boxThanhToanTienMat.setVisible(true);
                boxThanhToanTienMat.setManaged(true);
            }
        });
        
        paymentBox.getChildren().addAll(label, rbTienMat, rbMomo);
        return paymentBox;
    }
    
    /**
     * Tạo box thanh toán tiền mặt (tiền nhận vào, buttons gợi ý làm tròn, tiền trả lại)
     */
    private VBox taoBoxThanhToanTienMat() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #e5e7eb; -fx-border-width: 1; -fx-border-radius: 8;");
        
        // Tiền nhận vào
        VBox tienNhanBox = new VBox(8);
        Label lblTienNhanTitle = new Label("Tiền nhận vào:");
        lblTienNhanTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #374151;");
        
        txtTienNhan = new TextField();
        txtTienNhan.setPromptText("Nhập số tiền nhận từ khách");
        txtTienNhan.setPrefHeight(40);
        txtTienNhan.setStyle("-fx-font-size: 16px; -fx-background-color: #f9fafb; -fx-border-color: #d1d5db; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 0 12;");
        
        // Listener để tính tiền trả lại
        txtTienNhan.textProperty().addListener((obs, oldVal, newVal) -> {
            tinhTienTraLai();
        });
        
        tienNhanBox.getChildren().addAll(lblTienNhanTitle, txtTienNhan);
        
        // Buttons làm tròn - GỢI Ý SỐ TIỀN CỤ THỂ
        Label lblLamTron = new Label("Gợi ý làm tròn:");
        lblLamTron.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #6b7280;");
        
        FlowPane buttonPane = new FlowPane(8, 8);
        buttonPane.setPrefWrapLength(300);
        
        long tongTien = 2323000; // TODO: Get from actual total
        
        // Tạo buttons với số tiền gợi ý cụ thể
        Button btn50 = taoButtonGoiY(tongTien, 50);
        Button btn100 = taoButtonGoiY(tongTien, 100);
        Button btn500 = taoButtonGoiY(tongTien, 500);
        Button btn1000 = taoButtonGoiY(tongTien, 1000);
        Button btn5000 = taoButtonGoiY(tongTien, 5000);
        Button btn10000 = taoButtonGoiY(tongTien, 10000);
        Button btn50000 = taoButtonGoiY(tongTien, 50000);
        Button btn100000 = taoButtonGoiY(tongTien, 100000);
        
        buttonPane.getChildren().addAll(btn50, btn100, btn500, btn1000, btn5000, btn10000, btn50000, btn100000);
        
        // Tiền trả lại
        VBox tienTraLaiBox = new VBox(8);
        Label lblTienTraLaiTitle = new Label("Tiền trả lại:");
        lblTienTraLaiTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #374151;");
        
        lblTienTraLai = new Label("0 VNĐ");
        lblTienTraLai.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #16a34a; -fx-padding: 10; -fx-background-color: #f0fdf4; -fx-background-radius: 6;");
        lblTienTraLai.setMaxWidth(Double.MAX_VALUE);
        lblTienTraLai.setAlignment(Pos.CENTER);
        
        tienTraLaiBox.getChildren().addAll(lblTienTraLaiTitle, lblTienTraLai);
        
        box.getChildren().addAll(tienNhanBox, lblLamTron, buttonPane, tienTraLaiBox);
        return box;
    }
    
    /**
     * Tạo button gợi ý số tiền làm tròn
     * @param totalAmount Tổng tiền cần thanh toán
     * @param roundTo Làm tròn lên bội số này (VD: 50, 100, 1000...)
     * @return Button với text là số tiền gợi ý (VD: "2.35K", "2.4M")
     */
    private Button taoButtonGoiY(long totalAmount, int roundTo) {
        // Làm tròn lên bội số của roundTo
        long suggestedAmount = ((totalAmount + roundTo - 1) / roundTo) * roundTo;
        
        // Format số tiền hiển thị trên button
        String displayText;
        if (suggestedAmount >= 1000000) {
            // Hiển thị dạng triệu: 2.5M, 3M
            double millions = suggestedAmount / 1000000.0;
            if (millions == (long)millions) {
                displayText = String.format("%.0fM", millions);
            } else {
                displayText = String.format("%.1fM", millions).replace(",", ".");
            }
        } else if (suggestedAmount >= 1000) {
            // Hiển thị dạng nghìn: 2.5K, 500K
            double thousands = suggestedAmount / 1000.0;
            if (thousands == (long)thousands) {
                displayText = String.format("%.0fK", thousands);
            } else {
                displayText = String.format("%.1fK", thousands).replace(",", ".");
            }
        } else {
            // Số nhỏ: hiển thị đầy đủ
            displayText = String.valueOf(suggestedAmount);
        }
        
        Button btn = new Button(displayText);
        btn.setStyle("-fx-background-color: #e0e7ff; -fx-text-fill: #3b82f6; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 6 12;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #c7d2fe; -fx-text-fill: #2563eb; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 6 12;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #e0e7ff; -fx-text-fill: #3b82f6; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 6 12;"));
        
        btn.setOnAction(e -> {
            txtTienNhan.setText(String.format("%,d", suggestedAmount).replace(",", "."));
        });
        
        return btn;
    }
    
    /**
     * Tính toán và hiển thị tiền trả lại
     * Đọc số tiền nhận vào từ TextField, trừ đi tổng tiền, hiển thị kết quả
     * Màu xanh nếu đủ tiền, màu đỏ nếu chưa đủ
     */
    private void tinhTienTraLai() {
        try {
            String tienNhanText = txtTienNhan.getText().trim().replace(".", "").replace(",", "");
            if (tienNhanText.isEmpty()) {
                lblTienTraLai.setText("0 VNĐ");
                lblTienTraLai.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #16a34a; -fx-padding: 10; -fx-background-color: #f0fdf4; -fx-background-radius: 6;");
                return;
            }
            
            long tienNhan = Long.parseLong(tienNhanText);
            long tongTien = 2530000; // TODO: Get from actual total
            
            long tienTraLai = tienNhan - tongTien;
            
            if (tienTraLai < 0) {
                lblTienTraLai.setText("Chưa đủ!");
                lblTienTraLai.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #ef4444; -fx-padding: 10; -fx-background-color: #fee2e2; -fx-background-radius: 6;");
            } else {
                lblTienTraLai.setText(String.format("%,d VNĐ", tienTraLai).replace(",", "."));
                lblTienTraLai.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #16a34a; -fx-padding: 10; -fx-background-color: #f0fdf4; -fx-background-radius: 6;");
            }
        } catch (NumberFormatException ex) {
            lblTienTraLai.setText("Số không hợp lệ");
            lblTienTraLai.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #ef4444; -fx-padding: 10; -fx-background-color: #fee2e2; -fx-background-radius: 6;");
        }
    }

    /**
     * Inner class cho dữ liệu bảng phòng thanh toán
     */
    public static class RoomPaymentRow {
        private String phong;
        private String loaiPhong;
        private String dichVu;
        private String thoiGian;
        private String tongTien;

        public RoomPaymentRow(String phong, String loaiPhong, String dichVu, String thoiGian, String tongTien) {
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
    
    /**
     * Inner class cho dữ liệu bảng khuyến mãi trong modal
     */
    public static class KhuyenMaiRow {
        private String maKM;
        private String tenKM;
        private String phanTramGiam;
        private String trangThai;

        public KhuyenMaiRow(String maKM, String tenKM, String phanTramGiam, String trangThai) {
            this.maKM = maKM;
            this.tenKM = tenKM;
            this.phanTramGiam = phanTramGiam;
            this.trangThai = trangThai;
        }

        public String getMaKM() { return maKM; }
        public String getTenKM() { return tenKM; }
        public String getPhanTramGiam() { return phanTramGiam; }
        public String getTrangThai() { return trangThai; }
    }
}
