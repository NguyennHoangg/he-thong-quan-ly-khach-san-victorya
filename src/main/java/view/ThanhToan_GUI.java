package view;

import controller.KhuyenMai_Controller;
import controller.ThanhToan_Controller;
import javafx.application.Platform;
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
import model.KhuyenMai;
import model.PhieuDatPhong;
import payment.controller.MoMoPaymentService;
import payment.controller.QRCodeGenerator;
import payment.model.Payment;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class ThanhToan_GUI extends BorderPane {

    private ThanhToan_Controller thanhToan_Controller = new ThanhToan_Controller();
    private KhuyenMai_Controller khuyenMai_Controller = new KhuyenMai_Controller();

    private TextField txtNhapCCCD;
    private Button btnTimKiem;
    private TableView<ChiTietPhieuDatPhong> tablePhong;
    private ObservableList<ChiTietPhieuDatPhong> dataList;
    private TableView<KhuyenMai> tableKhuyenMai;
    private ObservableList<KhuyenMai> khuyenMaiList;
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
    private long tongTien;
    private long tongTienHoaDon;
    
    // MoMo Payment fields
    private Payment currentPayment;
    private Timer statusCheckTimer;
    private Label lblMoMoStatus;
    private ProgressIndicator momoProgressIndicator;

    private PhieuDatPhong phieuDatPhong;
    private KhuyenMai khuyenMai;

    public ThanhToan_GUI() {
        this.phieuDatPhong = new PhieuDatPhong();
        this.khuyenMai = new KhuyenMai();
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
        btnTimKiem.setOnAction(e -> loadDataBangPhong(txtNhapCCCD.getText()));
        
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
        colPhong.setCellValueFactory(cellData -> {
            if (cellData.getValue().getPhong() != null) {
                return new SimpleStringProperty(cellData.getValue().getPhong().getSoPhong());
            } else {
                return new SimpleStringProperty("");
            }
        });
        colPhong.setPrefWidth(100);
        colPhong.setStyle("-fx-alignment: CENTER;");

        TableColumn<ChiTietPhieuDatPhong, String> colLoaiPhong = new TableColumn<>("Loại Phòng");
        colLoaiPhong.setCellValueFactory(cellData -> {
            if (cellData.getValue().getPhong() != null && cellData.getValue().getPhong().getLoaiPhong() != null) {
                return new SimpleStringProperty(cellData.getValue().getPhong().getLoaiPhong().getTenLoaiPhong());
            } else {
                return new SimpleStringProperty("");
            }
        });
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
        colTongTien.setCellValueFactory(cellData -> {
            // Sử dụng method tinhThanhTien() đã sửa (bao gồm cả tiền dịch vụ)
            double thanhTien = cellData.getValue().tinhThanhTien();
            String formatted = String.format("%,.0f VNĐ", thanhTien).replace(",", ".");
            return new SimpleStringProperty(formatted);
        });
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

    /**
     * Load dữ liệu bảng phòng theo CCCD khách hàng
     * @param CCCD CCCD khách hàng
     */
    public void loadDataBangPhong(String CCCD){
        phieuDatPhong = thanhToan_Controller.getPhieuDatPhongTheoCCCD(CCCD);
        dataList = FXCollections.observableArrayList();
        for(ChiTietPhieuDatPhong ct : phieuDatPhong.getDsachPhieuDatPhong()){
            dataList.add(ct);
        }
        tablePhong.setItems(dataList);
        tongTien = phieuDatPhong.tinhTongTien();

        // Cập nhật lại phần tổng kết tiền - Sửa logic lấy container
        HBox mainContainer = (HBox) this.getCenter();
        VBox leftSide = (VBox) mainContainer.getChildren().get(0);
        VBox newSummaryBox = taoPhanTongKet();
        if (leftSide.getChildren().size() >= 3) {
            leftSide.getChildren().set(2, newSummaryBox);
        }
    }

    /**
     * Load danh sách khuyến mãi vào bảng khuyến mãi
     */
    public void loadDataDanhSachKhuyenMai(){
        List<KhuyenMai> dsachKhuyenMai = khuyenMai_Controller.getAll();
        khuyenMaiList = FXCollections.observableArrayList();
        for(KhuyenMai km : dsachKhuyenMai){
            khuyenMaiList.add(km);
        }
        tableKhuyenMai.setItems(khuyenMaiList);
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
        
        boxTongTien = taoLabelTongKet("Tổng tiền:", String.format("%,d VNĐ", tongTien).replace(",", "."), false);
        // heSo trong DB đã là số thập phân (0.1 = 10%), dùng trực tiếp
        double heSoGiam = (khuyenMai != null && khuyenMai.getHeSo() > 0) ? khuyenMai.getHeSo() : 0.0;
        boxKhuyenMai = taoLabelTongKet("Khuyến mãi:", heSoGiam > 0 ? String.format("%.0f%%", heSoGiam * 100) : "0%", false);
        boxVAT = taoLabelTongKet("VAT:", "10%", false);

        Separator separator = new Separator();
        separator.setPrefWidth(300);

        tongTienHoaDon = (long)((tongTien + (tongTien * 0.1)) - (tongTien * heSoGiam));
        boxTotal = taoLabelTongKet("Total:", String.format("%,d VNĐ", tongTienHoaDon).replace(",", "."), true);
        
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
    tableKhuyenMai = new TableView<>();
    tableKhuyenMai.setPrefHeight(400);
    tableKhuyenMai.setPrefWidth(650);

    // Load CSS
    tableKhuyenMai.getStylesheets().add(getClass().getResource("/css/ThanhToan.css").toExternalForm());
    tableKhuyenMai.getStyleClass().add("payment-table");

    // Load data for table
    loadDataDanhSachKhuyenMai();

    // Columns
        TableColumn<KhuyenMai, String> colMaKM = new TableColumn<>("Mã KM");
        colMaKM.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMaKhuyenMai()));
        colMaKM.setPrefWidth(100);
        
        TableColumn<KhuyenMai, String> colTenKM = new TableColumn<>("Tên Khuyến Mãi");
        colTenKM.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTenKhuyenMai()));
        colTenKM.setPrefWidth(250);
        
        TableColumn<KhuyenMai, String> colPhanTram = new TableColumn<>("% Giảm Giá");
        colPhanTram.setCellValueFactory(cellData -> {
            double phanTram = cellData.getValue().getHeSo() * 100; // Chuyển 0.1 thành 10%
            String formatted = String.format("%.0f%%", phanTram);
            return new SimpleStringProperty(formatted);
        });
        colPhanTram.setPrefWidth(120);
        colPhanTram.setStyle("-fx-alignment: CENTER;");
        
        TableColumn<KhuyenMai, String> colTrangThai = new TableColumn<>("Trạng Thái");
        colTrangThai.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().isTrangThai() ? "Đang áp dụng" : "Hết hạn");
        });
        colTrangThai.setPrefWidth(130);
        colTrangThai.setStyle("-fx-alignment: CENTER;");
        
        // Custom cell factory cho trạng thái
        colTrangThai.setCellFactory(column -> new TableCell<KhuyenMai, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Đang áp dụng")) {
                        setStyle("-fx-text-fill: #16a34a; -fx-font-weight: 600;");
                    } else {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: 600;");
                    }
                }
            }
        });
        
        // Thêm các cột vào bảng khuyến mãi
        tableKhuyenMai.getColumns().add(colMaKM);
        tableKhuyenMai.getColumns().add(colTenKM);
        tableKhuyenMai.getColumns().add(colPhanTram);
        tableKhuyenMai.getColumns().add(colTrangThai);
        
       
        
        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnChon = new Button("Chọn");
        btnChon.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 600; -fx-background-radius: 6; -fx-padding: 8 25; -fx-cursor: hand;");
        btnChon.setOnAction(e -> {
            KhuyenMai selected = tableKhuyenMai.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (selected.isTrangThai()) {
                    khuyenMai = selected; // Gán khuyến mãi đã chọn
                    lblKhuyenMaiSelected.setText(selected.getTenKhuyenMai() + " (" + String.format("%.0f%%", selected.getHeSo() * 100) + ")");
                    lblKhuyenMaiSelected.setStyle("-fx-font-size: 14px; -fx-text-fill: #16a34a; -fx-font-weight: 600;");
                    // Cập nhật lại phần tổng kết tiền - Sửa logic lấy container
                    HBox mainContainer = (HBox) ThanhToan_GUI.this.getCenter();
                    VBox leftSide = (VBox) mainContainer.getChildren().get(0);
                    VBox newSummaryBox = taoPhanTongKet();
                    if (leftSide.getChildren().size() >= 3) {
                        leftSide.getChildren().set(2, newSummaryBox);
                    }
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
        
        container.getChildren().addAll(title, tableKhuyenMai, buttonBox);
        
        Scene scene = new Scene(container);
        modal.setScene(scene);
        modal.showAndWait();
    }

    /**
     * Tạo label hiển thị tổng kết tiền (tổng tiền, VAT, khuyến mãi, total)
     * @param title Tiêu đề 
     * @param value Giá trị 
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
        
        // Khởi tạo QR Code Image
        qrCodeImage = new ImageView();
        qrCodeImage.setFitWidth(240);
        qrCodeImage.setFitHeight(240);
        qrCodeImage.setPreserveRatio(true);
        qrContainer.getChildren().add(qrCodeImage);
        
        // Payment button
        btnThanhToan = new Button("Thanh Toán");
        btnThanhToan.setPrefWidth(350);
        btnThanhToan.setPrefHeight(55);
        btnThanhToan.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        btnThanhToan.setOnMouseEntered(e -> btnThanhToan.setStyle("-fx-background-color: #15803d; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));
        btnThanhToan.setOnMouseExited(e -> btnThanhToan.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));
        btnThanhToan.setOnAction(e -> xuLyThanhToan());
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        rightSide.getChildren().addAll(title, paymentMethod, boxThanhToanTienMat, qrContainer, spacer, btnThanhToan);
        return rightSide;
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
     * Xử lý thanh toán theo phương thức được chọn
     */
    private void xuLyThanhToan() {
        if (rbMomo.isSelected()) {
            xuLyThanhToanMoMo();
        } else if (rbTienMat.isSelected()) {
            xuLyThanhToanTienMat();
        }
    }
    
    /**
     * Xử lý thanh toán MoMo
     */
    private void xuLyThanhToanMoMo() {
        try {
            String orderInfo = "Thanh toan phong PDP-20102025-003 Khach san Victorya"; 
            
            
            // Disable button và hiển thị loading
            btnThanhToan.setDisable(true);
            
            // Tạo progress indicator nếu chưa có
            if (momoProgressIndicator == null) {
                momoProgressIndicator = new ProgressIndicator();
                momoProgressIndicator.setPrefSize(60, 60);
            }
            
            // Tạo status label nếu chưa có
            if (lblMoMoStatus == null) {
                lblMoMoStatus = new Label();
                lblMoMoStatus.setWrapText(true);
                lblMoMoStatus.setAlignment(Pos.CENTER);
                lblMoMoStatus.setMaxWidth(280);
                lblMoMoStatus.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-text-alignment: center;");
            }
            
            // Thêm vào container nếu chưa có
            if (!qrContainer.getChildren().contains(momoProgressIndicator)) {
                qrContainer.getChildren().addAll(momoProgressIndicator, lblMoMoStatus);
            }
            
            // Hiển thị loading
            qrCodeImage.setVisible(false);
            momoProgressIndicator.setVisible(true);
           
            
            // Gọi API trong background thread
            new Thread(() -> {
                try {
                    currentPayment = MoMoPaymentService.createPayment(tongTien, orderInfo);
                    
                    Platform.runLater(() -> {
                        if (currentPayment.getResultCode() == 0) {
                            try {
                                // Tạo QR MoMo Deeplink - QR thanh toán thực sự  
                                // Format deeplink MoMo: momo://transfer?phone=XXXXXXXXXX&amount=XXXX&note=XXXX

                                System.out.println("� Tạo QR MoMo Deeplink:");
                                System.out.println("🔗 PayURL từ MoMo API: " + currentPayment.getPayUrl());
                                
                                Image qrImage = QRCodeGenerator.generateQRCodeImage(currentPayment.getPayUrl(), 240, 240);
                                qrCodeImage.setImage(qrImage);
                                qrCodeImage.setVisible(true);
                                momoProgressIndicator.setVisible(false);
                                
                                lblMoMoStatus.setText("� QR THANH TOÁN MOMO\n✅ Quét để mở app MoMo\n💰 Thanh toán " + String.format("%,d VNĐ", tongTien).replace(",", "."));
                                lblMoMoStatus.setStyle("-fx-font-size: 11px; -fx-text-fill: #16a34a; -fx-text-alignment: center; -fx-font-weight: 600;");
                                
                                // Vẫn bắt đầu kiểm tra trạng thái để biết khi nào thanh toán thành công
                                startStatusCheck();
                                
                                System.out.println("✅ QR Business MoMo đã tạo thành công!");
                                System.out.println("📱 Quét QR này bằng app MoMo sẽ mở ngay màn hình thanh toán!");
                                startStatusCheck();
                                
                             
                                
                            } catch (Exception e) {
                                showError("Lỗi tạo QR code: " + e.getMessage());
                                resetMoMoPayment();
                                e.printStackTrace();
                            }
                        } else {
                            showError("Lỗi tạo thanh toán:\nMã lỗi: " + currentPayment.getResultCode() + 
                                     "\n" + currentPayment.getMessage());
                            resetMoMoPayment();
                        }
                        btnThanhToan.setDisable(false);
                    });
                    
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        showError("Không thể kết nối đến MoMo:\n" + e.getMessage());
                        resetMoMoPayment();
                        btnThanhToan.setDisable(false);
                    });
                    e.printStackTrace();
                }
            }).start();
            
        } catch (Exception e) {
            showError("Lỗi: " + e.getMessage());
            btnThanhToan.setDisable(false);
        }
    }
    
    /**
     * Xử lý thanh toán tiền mặt
     */
    private void xuLyThanhToanTienMat() {
        try {
            String tienNhanText = txtTienNhan.getText().trim().replace(".", "").replace(",", "");
            if (tienNhanText.isEmpty()) {
                showError("Vui lòng nhập số tiền nhận!");
                return;
            }
            
            long tienNhan = Long.parseLong(tienNhanText);
            
            if (tienNhan < tongTien) {
                showError("Số tiền nhận chưa đủ!");
                return;
            }
            
            // Xử lý thanh toán tiền mặt
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thanh toán thành công");
            alert.setHeaderText("✅ Thanh toán tiền mặt thành công!");
            alert.setContentText(String.format(
                "Tổng tiền: %,d VNĐ\n" +
                "Tiền nhận: %,d VNĐ\n" +
                "Tiền trả lại: %,d VNĐ",
                tongTien, tienNhan, tienNhan - tongTien
            ).replace(",", "."));
            alert.showAndWait();
            
            System.out.println("✅ Thanh toán tiền mặt thành công!");
            
        } catch (NumberFormatException e) {
            showError("Số tiền không hợp lệ!");
        }
    }
    
    /**
     * Bắt đầu kiểm tra trạng thái thanh toán MoMo
     */
    private void startStatusCheck() {
        stopStatusCheck();
        
        statusCheckTimer = new Timer();
        statusCheckTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkPaymentStatus();
            }
        }, 3000, 3000); // Kiểm tra mỗi 3 giây
    }
    
    /**
     * Kiểm tra trạng thái thanh toán
     */
    private void checkPaymentStatus() {
        if (currentPayment == null) return;
        
        new Thread(() -> {
            try {
                Payment status = MoMoPaymentService.queryPaymentStatus(
                    currentPayment.getOrderId(),
                    currentPayment.getRequestId()
                );
                
                Platform.runLater(() -> {
                    if (status.getResultCode() == 0) {
                        // Thanh toán thành công
                        System.out.println("🎉 THANH TOÁN MOMO THÀNH CÔNG!");
                        System.out.println("💳 Mã giao dịch: " + status.getTransId());
                        
                        lblMoMoStatus.setText("🎉 THANH TOÁN THÀNH CÔNG!\nMã GD: " + status.getTransId());
                        lblMoMoStatus.setStyle("-fx-font-size: 14px; -fx-text-fill: #16a34a; -fx-text-alignment: center; -fx-font-weight: bold;");
                        
                        stopStatusCheck();
                        
                        // Hiển thị thông báo thành công
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Thanh toán thành công");
                        alert.setHeaderText("🎉 Thanh toán MoMo thành công!");
                        alert.setContentText(String.format(
                            "Mã giao dịch MoMo: %s\n" +
                            "Mã đơn hàng: %s\n" +
                            "Số tiền: %,d VNĐ\n" +
                            "Loại thanh toán: %s",
                            status.getTransId(),
                            status.getOrderId(),
                            status.getAmount(),
                            status.getPayType() != null ? status.getPayType() : "MoMo"
                        ).replace(",", "."));
                        alert.showAndWait();
                        
                    } else if (status.getResultCode() == 1006) {
                        // Thanh toán thất bại
                        
                        lblMoMoStatus.setText("❌ Thanh toán thất bại");
                        lblMoMoStatus.setStyle("-fx-font-size: 12px; -fx-text-fill: #ef4444; -fx-text-alignment: center;");
                        
                        stopStatusCheck();
                    }
                    // Các mã khác = đang chờ, tiếp tục kiểm tra
                });
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    /**
     * Dừng kiểm tra trạng thái
     */
    private void stopStatusCheck() {
        if (statusCheckTimer != null) {
            statusCheckTimer.cancel();
            statusCheckTimer = null;
            System.out.println("⏹️ Dừng kiểm tra trạng thái");
        }
    }
    
    /**
     * Reset trạng thái thanh toán MoMo
     */
    private void resetMoMoPayment() {
        if (momoProgressIndicator != null) {
            momoProgressIndicator.setVisible(false);
        }
        if (lblMoMoStatus != null) {
            lblMoMoStatus.setVisible(false);
        }
        qrCodeImage.setVisible(true);
        stopStatusCheck();
        currentPayment = null;
    }
    
    /**
     * Hiển thị thông báo lỗi
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText("❌ Có lỗi xảy ra");
        alert.setContentText(message);
        alert.showAndWait();
    }

   
}
