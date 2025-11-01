package view.Phong;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import controller.GiaHanPhong_Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.ChiTietPhieuDatPhong;

/**
 * Giao diện gia hạn phòng - phiên bản đơn giản
 */
public class GiaHanPhong_GUI extends BorderPane {

    private TextField txtSoDienThoai;
    private TableView<RoomExtensionRow> tablePhongGiaHan;
    private VBox containerChonThoiGian;
    private VBox scrollableContent;
    private GiaHanPhong_Controller controller;
    private List<ChiTietPhieuDatPhong> danhSachPhongHienTai;
    private List<ChiTietPhieuDatPhong> danhSachPhongDaChon;
    private HBox containerThongTinKhachHang;

    public GiaHanPhong_GUI() {
        this.controller = new GiaHanPhong_Controller();
        this.danhSachPhongHienTai = null;
        this.danhSachPhongDaChon = new java.util.ArrayList<>();
        
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f5f7fa;");
        getStylesheets().add(getClass().getResource("/css/Button.css").toExternalForm());
        getStylesheets().add(getClass().getResource("/css/Table.css").toExternalForm());

        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(10));
        
        // Phần tìm kiếm và thông tin khách hàng ở trên
        VBox topSection = taoVungTren();
        
        // Phần nội dung chính: bảng phòng và panel gia hạn
        HBox contentSection = new HBox(20);
        contentSection.setAlignment(Pos.TOP_CENTER);
        
        VBox leftPanel = taoVungTrai();
        VBox rightPanel = taoVungPhai();
        
        // Responsive: bảng trái mở rộng, bảng phải vừa với nội dung
        HBox.setHgrow(leftPanel, javafx.scene.layout.Priority.ALWAYS);
        leftPanel.setMaxWidth(Double.MAX_VALUE);
        // Bảng phải không mở rộng, chỉ vừa với nội dung

        contentSection.getChildren().addAll(leftPanel, rightPanel);
        mainContainer.getChildren().addAll(topSection, contentSection);
        setCenter(mainContainer);
    }
    
    /**
     * Tạo vùng trên: Tìm kiếm và thông tin khách hàng
     */
    private VBox taoVungTren() {
        VBox container = new VBox(15);
        
        // Tiêu đề
        Label lblTieuDe = new Label("GIA HẠN PHÒNG");
        lblTieuDe.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Box tìm kiếm và thông tin khách hàng
        HBox mainBox = new HBox(20);
        mainBox.setAlignment(Pos.CENTER_LEFT);
        
        // Phần tìm kiếm (bên trái)
        HBox searchBox = new HBox(10);
        searchBox.setPadding(new Insets(12));
        searchBox.setAlignment(Pos.CENTER_LEFT);
        
        Label lblTimKiem = new Label("CCCD:");
        lblTimKiem.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        txtSoDienThoai = new TextField();
        txtSoDienThoai.setPromptText("Nhập CCCD");
        txtSoDienThoai.setPrefWidth(200);
        txtSoDienThoai.setPrefHeight(35);
        txtSoDienThoai.setAlignment(Pos.CENTER_LEFT);
        txtSoDienThoai.setOnAction(e -> thucHienTimKiem());

        Button btnTimKiem = new Button("Tìm kiếm");
        btnTimKiem.setPrefHeight(35);
        btnTimKiem.setPrefWidth(100);
        btnTimKiem.getStyleClass().add("btn");
        btnTimKiem.setOnAction(e -> thucHienTimKiem());

        searchBox.getChildren().addAll(lblTimKiem, txtSoDienThoai, btnTimKiem);
        
        // Phần thông tin khách hàng (bên phải)
        containerThongTinKhachHang = new HBox(15);
        containerThongTinKhachHang.setAlignment(Pos.CENTER_LEFT);
        containerThongTinKhachHang.setPadding(new Insets(12));
        containerThongTinKhachHang.setVisible(false);
        containerThongTinKhachHang.setManaged(false);
        
        mainBox.getChildren().addAll(searchBox, containerThongTinKhachHang);
        
        container.getChildren().addAll(lblTieuDe, mainBox);
        return container;
    }
    
    /**
     * Hiển thị thông tin khách hàng (tên và số điện thoại)
     */
    private void hienThiThongTinKhachHang() {
        try {
            containerThongTinKhachHang.getChildren().clear();
            
            if (danhSachPhongHienTai == null || danhSachPhongHienTai.isEmpty()) {
                containerThongTinKhachHang.setVisible(false);
                containerThongTinKhachHang.setManaged(false);
                return;
            }
            
            // Lấy thông tin khách hàng từ phòng đầu tiên
            ChiTietPhieuDatPhong ctpdp = danhSachPhongHienTai.get(0);
            if (ctpdp == null || ctpdp.getPhieuDatPhong() == null 
                || ctpdp.getPhieuDatPhong().getKhachHang() == null) {
                containerThongTinKhachHang.setVisible(false);
                containerThongTinKhachHang.setManaged(false);
                return;
            }
            
            model.KhachHang kh = ctpdp.getPhieuDatPhong().getKhachHang();
            
            Label lblTen = new Label("Tên: " + (kh.getTenKhachHang() != null ? kh.getTenKhachHang() : "-"));
            lblTen.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
            
            Label lblSDT = new Label("SĐT: " + (kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "-"));
            lblSDT.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
            
            containerThongTinKhachHang.getChildren().addAll(lblTen, lblSDT);
            containerThongTinKhachHang.setVisible(true);
            containerThongTinKhachHang.setManaged(true);
            
        } catch (Exception e) {
            containerThongTinKhachHang.setVisible(false);
            containerThongTinKhachHang.setManaged(false);
        }
    }
    

    private VBox taoVungTrai() {
        VBox container = new VBox(15);
        container.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(container, javafx.scene.layout.Priority.ALWAYS);
        
        Label lblTieuDe = new Label("DANH SÁCH PHÒNG ĐANG SỬ DỤNG");
        lblTieuDe.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        VBox tableBox = taoBangPhong();
        VBox.setVgrow(tableBox, javafx.scene.layout.Priority.ALWAYS);

        container.getChildren().addAll(lblTieuDe, tableBox);
        return container;
    }

    private VBox taoBangPhong() {
        tablePhongGiaHan = new TableView<>();
        tablePhongGiaHan.setMaxWidth(Double.MAX_VALUE);
        tablePhongGiaHan.setPrefHeight(450);
        tablePhongGiaHan.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablePhongGiaHan.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        // Style header của TableView thành xanh dương
        tablePhongGiaHan.setId("tablePhongGiaHan");
        
        // Cho phép chọn nhiều dòng
        tablePhongGiaHan.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        
        // Lắng nghe thay đổi selection
        tablePhongGiaHan.getSelectionModel().getSelectedItems().addListener(
            (javafx.collections.ListChangeListener.Change<? extends RoomExtensionRow> change) -> {
                capNhatPanelGiaHanTheoLuaChon();
            }
        );
        
        // Custom row factory để toggle selection khi click
        tablePhongGiaHan.setRowFactory(tv -> {
            javafx.scene.control.TableRow<RoomExtensionRow> row = new javafx.scene.control.TableRow<>();
            
            final int[] lastClickedIndex = {-1};
            
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    int index = row.getIndex();
                    
                    // Nếu click vào cùng 1 row 2 lần liên tiếp → toggle
                    if (lastClickedIndex[0] == index && tablePhongGiaHan.getSelectionModel().isSelected(index)) {
                        tablePhongGiaHan.getSelectionModel().clearSelection(index);
                        lastClickedIndex[0] = -1;
                    } else {
                        tablePhongGiaHan.getSelectionModel().select(index);
                        lastClickedIndex[0] = index;
                    }
                }
            });
            return row;
        });

        TableColumn<RoomExtensionRow, String> colSoPhong = new TableColumn<>("Số phòng");
        colSoPhong.setCellValueFactory(new PropertyValueFactory<>("soPhong"));
        // In đậm số phòng
        colSoPhong.setCellFactory(column -> {
            TableCell<RoomExtensionRow, String> cell = new TableCell<RoomExtensionRow, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
                    }
                }
            };
            return cell;
        });

        TableColumn<RoomExtensionRow, String> colLoaiPhong = new TableColumn<>("Loại phòng");
        colLoaiPhong.setCellValueFactory(new PropertyValueFactory<>("loaiPhong"));

        TableColumn<RoomExtensionRow, String> colTang = new TableColumn<>("Tầng");
        colTang.setCellValueFactory(new PropertyValueFactory<>("tang"));

        TableColumn<RoomExtensionRow, String> colGia = new TableColumn<>("Giá");
        colGia.setCellValueFactory(new PropertyValueFactory<>("gia"));

        tablePhongGiaHan.getColumns().add(colSoPhong);
        tablePhongGiaHan.getColumns().add(colLoaiPhong);
        tablePhongGiaHan.getColumns().add(colTang);
        tablePhongGiaHan.getColumns().add(colGia);
        
        // Style header TableView thành xanh dương sau khi render
        javafx.application.Platform.runLater(() -> {
            javafx.scene.Node header = tablePhongGiaHan.lookup(".column-header-background");
            if (header != null) {
                header.setStyle("-fx-background-color: #3b82f6;");
            }
            // Style các column header
            for (javafx.scene.Node node : tablePhongGiaHan.lookupAll(".column-header")) {
                node.setStyle("-fx-background-color: #3b82f6; -fx-border-color: rgba(255,255,255,0.3); -fx-border-width: 0 0 0 1;");
            }
            // Style text trong header
            for (javafx.scene.Node node : tablePhongGiaHan.lookupAll(".column-header .label")) {
                if (node instanceof Label) {
                    ((Label) node).setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                }
            }
        });

        VBox container = new VBox(tablePhongGiaHan);
        return container;
    }

    private VBox taoVungPhai() {
        VBox container = new VBox(15);
        // Không set MaxWidth để container chỉ vừa với nội dung
        
        Label lblTieuDe = new Label("THÔNG TIN GIA HẠN");
        lblTieuDe.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        containerChonThoiGian = new VBox(0);
        // Tính tổng width các cột: 80 + 150 + 140 + 170 + padding = ~560
        containerChonThoiGian.setPrefWidth(560);
        containerChonThoiGian.setPrefHeight(450);
        containerChonThoiGian.setStyle("-fx-background-color: white; -fx-background-radius: 8;");

        // Phần scrollable: header + các dòng thông tin
        scrollableContent = new VBox(0);
        
        HBox headerRow = taoHeaderChonThoiGian();
        scrollableContent.getChildren().add(headerRow);
        
        // ScrollPane cho phần cuộn
        ScrollPane scrollPane = new ScrollPane(scrollableContent);
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPrefHeight(450);
        
        containerChonThoiGian.getChildren().addAll(scrollPane);

        Button btnGiaHan = new Button("XÁC NHẬN GIA HẠN");
        btnGiaHan.setPrefWidth(560);
        btnGiaHan.setPrefHeight(35);
        btnGiaHan.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        btnGiaHan.setOnMouseEntered(e -> btnGiaHan.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;"));
        btnGiaHan.setOnMouseExited(e -> btnGiaHan.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;"));
        btnGiaHan.setOnAction(e -> thucHienGiaHanNgay());

        container.getChildren().addAll(lblTieuDe, containerChonThoiGian, btnGiaHan);
        return container;
    }

    
    /**
     * Tạo header cho bảng gia hạn - 5 cột (xanh lá)
     */
    private HBox taoHeaderChonThoiGian() {
        HBox header = new HBox(0);
        header.setPadding(new Insets(12));
        header.setStyle("-fx-background-color: #22c55e; -fx-border-color: #16a34a; -fx-border-width: 0 0 1 0;");

        Label lblSoPhong = new Label("Số phòng");
        lblSoPhong.setPrefWidth(80);
        lblSoPhong.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: white; -fx-padding: 0 10 0 0;");

        Label lblNgayTraPhong = new Label("Ngày trả phòng");
        lblNgayTraPhong.setPrefWidth(150);
        lblNgayTraPhong.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: white; -fx-border-color: transparent transparent transparent rgba(255,255,255,0.3); -fx-border-width: 0 0 0 1; -fx-padding: 0 10 0 15;");

        Label lblGiaHanDen = new Label("Gia hạn đến");
        lblGiaHanDen.setPrefWidth(140);
        lblGiaHanDen.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: white; -fx-border-color: transparent transparent transparent rgba(255,255,255,0.3); -fx-border-width: 0 0 0 1; -fx-padding: 0 10 0 15;");

        Label lblGiaTienThem = new Label("Giá tiền ở thêm");
        lblGiaTienThem.setPrefWidth(170);
        lblGiaTienThem.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: white; -fx-border-color: transparent transparent transparent rgba(255,255,255,0.3); -fx-border-width: 0 0 0 1; -fx-padding: 0 10 0 15;");

        header.getChildren().addAll(lblSoPhong, lblNgayTraPhong, lblGiaHanDen, lblGiaTienThem);
        return header;
    }

    private HBox taoDongPhongGiaHan(String soPhong, String ngayTraPhong, String ngayGiaHan, 
                                     ChiTietPhieuDatPhong chiTietPhieuDatPhong, double giaTienThem) {
        HBox row = new HBox(0);
        row.setPadding(new Insets(12));
        row.setStyle("-fx-border-color: transparent transparent #e5e7eb transparent; -fx-border-width: 0 0 1 0;");

        Label lblSoPhong = new Label(soPhong);
        lblSoPhong.setPrefWidth(80);
        lblSoPhong.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 0 10 0 0;");

        Label lblNgayTra = new Label(ngayTraPhong);
        lblNgayTra.setPrefWidth(150);
        lblNgayTra.setStyle("-fx-font-size: 12px; -fx-border-color: transparent transparent transparent #e5e7eb; -fx-border-width: 0 0 0 1; -fx-padding: 0 10 0 15;");

        Button btnChonGiaHan = new Button(ngayGiaHan);
        btnChonGiaHan.setPrefWidth(120);
        btnChonGiaHan.setPrefHeight(30);
        btnChonGiaHan.setStyle("-fx-font-size: 12px;");
        btnChonGiaHan.setOnAction(e -> hienThiChonGiaHan(btnChonGiaHan, row, chiTietPhieuDatPhong));
        
        // Label hiển thị "ở thêm X ngày" (ban đầu ẩn)
        Label lblOThemNgay = new Label("");
        lblOThemNgay.setStyle("-fx-font-size: 11px; -fx-text-fill: #6b7280; -fx-padding: 2 0 0 0;");
        lblOThemNgay.setAlignment(Pos.CENTER);
        
        VBox vboxGiaHan = new VBox(3);
        vboxGiaHan.setPrefWidth(140);
        vboxGiaHan.setAlignment(Pos.CENTER_LEFT);
        vboxGiaHan.setStyle("-fx-border-color: transparent transparent transparent #e5e7eb; -fx-border-width: 0 0 0 1; -fx-padding: 0 10 0 15;");
        vboxGiaHan.getChildren().addAll(btnChonGiaHan, lblOThemNgay);

        Label lblGiaTien = new Label(giaTienThem > 0 ? String.format("%.0f VND", giaTienThem) : "-");
        lblGiaTien.setPrefWidth(170);
        lblGiaTien.setAlignment(Pos.CENTER_RIGHT);
        lblGiaTien.setStyle("-fx-font-size: 12px; -fx-border-color: transparent transparent transparent #e5e7eb; -fx-border-width: 0 0 0 1; -fx-padding: 0 10 0 15;");

        row.getChildren().addAll(lblSoPhong, lblNgayTra, vboxGiaHan, lblGiaTien);
        return row;
    }
    
    private void hienThiChonGiaHan(Button btnTarget, HBox rowContainer, ChiTietPhieuDatPhong chiTietPhieuDatPhong) {
        try {
            if (btnTarget == null || rowContainer == null || chiTietPhieuDatPhong == null) {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                    "Dữ liệu không hợp lệ!");
                return;
            }
            
            if (chiTietPhieuDatPhong.getThoiGianTraPhong() == null) {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                    "Không thể lấy thời gian trả phòng!");
                return;
            }
            
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Chọn thời gian gia hạn");
            
            VBox root = new VBox(15);
            root.setPadding(new Insets(20));
            root.setAlignment(Pos.CENTER);
            
            Label title = new Label("Chọn thời gian gia hạn");
            title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            
            DatePicker datePicker = new DatePicker();
            datePicker.setPrefWidth(250);
            
            ComboBox<String> cboGio = new ComboBox<>();
            for (int i = 0; i < 24; i++) {
                cboGio.getItems().add(String.format("%02d:00", i));
            }
            cboGio.setValue("14:00");
            cboGio.setPrefWidth(250);
            
            Button btnXacNhan = new Button("Xác nhận");
            btnXacNhan.getStyleClass().add("btn");
            btnXacNhan.setPrefWidth(120);
            btnXacNhan.setOnAction(e -> {
                try {
                    if (datePicker.getValue() == null || cboGio.getValue() == null) {
                        hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", 
                            "Vui lòng chọn đầy đủ ngày và giờ gia hạn!");
                        return;
                    }
                    
                    if (chiTietPhieuDatPhong == null || chiTietPhieuDatPhong.getThoiGianTraPhong() == null) {
                        hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                            "Không thể lấy thông tin phòng!");
                        dialog.close();
                        return;
                    }
                    
                    // Tạo thời gian kết thúc mới
                    LocalDateTime gioKetThucMoi = LocalDateTime.of(
                        datePicker.getValue(),
                        java.time.LocalTime.parse(cboGio.getValue())
                    );
                    
                    // Tính thời gian gia hạn (từ thời gian kết thúc cũ đến mới)
                    LocalDateTime gioKetThucCu = chiTietPhieuDatPhong.getThoiGianTraPhong();
                    Duration thoiGianGiaHan = Duration.between(gioKetThucCu, gioKetThucMoi);
                    
                    if (thoiGianGiaHan.isNegative() || thoiGianGiaHan.isZero()) {
                        hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", 
                            "Thời gian gia hạn phải lớn hơn thời gian trả phòng hiện tại!");
                        return;
                    }
                    
                    // Tính giá tiền ở thêm
                    double giaTienThem = tinhGiaTienThem(chiTietPhieuDatPhong, thoiGianGiaHan);
                    
                    // Cập nhật UI
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    btnTarget.setText(gioKetThucMoi.format(formatter));
                    // Lưu thời gian đã chọn và thông tin giá tiền
                    btnTarget.setUserData(gioKetThucMoi.toString());
                    
                    // Tính số ngày ở thêm
                    long tongGio = thoiGianGiaHan.toHours();
                    long ngay = tongGio / 24;
                    long gio = tongGio % 24;
                    
                    // Cập nhật các Label trong row
                    if (rowContainer.getChildren().size() < 4) {
                        hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                            "Cấu trúc dữ liệu không hợp lệ!");
                        return;
                    }
                    
                    javafx.scene.Node vboxNode = rowContainer.getChildren().get(2);
                    if (!(vboxNode instanceof VBox)) {
                        hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                            "Cấu trúc dữ liệu không hợp lệ!");
                        return;
                    }
                    
                    VBox vboxGiaHan = (VBox) vboxNode;
                    if (vboxGiaHan.getChildren().size() < 2) {
                        hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                            "Cấu trúc dữ liệu không hợp lệ!");
                        return;
                    }
                    
                    Label lblOThemNgay = (Label) vboxGiaHan.getChildren().get(1);
                    
                    // Hiển thị "Ở thêm X ngày Y giờ" phía dưới button (không hiển thị phút)
                    StringBuilder textOThem = new StringBuilder("Ở thêm ");
                    boolean coDuLieu = false;
                    
                    if (ngay > 0) {
                        textOThem.append(ngay).append(" ngày");
                        coDuLieu = true;
                    }
                    
                    if (gio > 0) {
                        if (coDuLieu) {
                            textOThem.append(" ");
                        }
                        textOThem.append(gio).append(" giờ");
                        coDuLieu = true;
                    }
                    
                    if (!coDuLieu) {
                        textOThem.append("0 giờ");
                    }
                    
                    lblOThemNgay.setText(textOThem.toString());
                    lblOThemNgay.setVisible(true);
                    
                    javafx.scene.Node giaTienNode = rowContainer.getChildren().get(3);
                    
                    if (giaTienNode instanceof Label) {
                        Label lblGiaTien = (Label) giaTienNode;
                        lblGiaTien.setText(String.format("%.0f VND", giaTienThem));
                    }
                    
                    // Lưu thông tin giá tiền vào row container
                    rowContainer.setUserData(new ExtensionPriceInfo(giaTienThem));
                    
                    dialog.close();
                } catch (Exception ex) {
                    hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                        "Đã xảy ra lỗi khi chọn thời gian gia hạn: " + ex.getMessage());
                }
            });
            
            root.getChildren().addAll(title, datePicker, cboGio, btnXacNhan);
            dialog.setScene(new javafx.scene.Scene(root, 350, 280));
            dialog.showAndWait();
        } catch (Exception ex) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Đã xảy ra lỗi khi hiển thị dialog chọn thời gian: " + ex.getMessage());
        }
    }
    
    /**
     * Tính giá tiền ở thêm dựa trên thời gian gia hạn
     */
    private double tinhGiaTienThem(ChiTietPhieuDatPhong chiTietPhieuDatPhong, Duration thoiGianGiaHan) {
        try {
            if (chiTietPhieuDatPhong == null || chiTietPhieuDatPhong.getPhong() == null || 
                chiTietPhieuDatPhong.getPhong().getLoaiPhong() == null) {
                return 0;
            }
            
            double giaPhong = chiTietPhieuDatPhong.getPhong().getLoaiPhong().getGia();
            if (giaPhong <= 0) {
                return 0;
            }
            
            long tongGio = thoiGianGiaHan.toHours();
            long tongPhut = thoiGianGiaHan.toMinutes();
            
            // Tính giá theo giờ (chia giá ngày cho 24)
            double giaTheoGio = giaPhong / 24.0;
            double giaTheoPhut = giaTheoGio / 60.0;
            
            // Tính giá tiền ở thêm
            double giaTienThem = (tongGio * giaTheoGio) + ((tongPhut % 60) * giaTheoPhut);
            
            return giaTienThem;
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Class lưu thông tin giá tiền gia hạn
     */
    private static class ExtensionPriceInfo {
        public final double giaTienThem;
        
        public ExtensionPriceInfo(double giaTienThem) {
            this.giaTienThem = giaTienThem;
        }
    }

    /**
     * Thực hiện tìm kiếm phòng theo CCCD
     */
    private void thucHienTimKiem() {
        try {
            String cccd = txtSoDienThoai.getText().trim();
            if (cccd.isEmpty()) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập CCCD khách hàng");
                return;
            }
            
            // Gọi controller để tìm phòng
            danhSachPhongHienTai = controller.timDatPhongHienTaiTheoCCCD(cccd);
        
        if (danhSachPhongHienTai != null && !danhSachPhongHienTai.isEmpty()) {
            
            hienThiBangPhong();
            hienThiThongTinKhachHang();
            // Xóa danh sách phòng đã chọn
            danhSachPhongDaChon.clear();
            // Xóa panel gia hạn, chỉ giữ header với hướng dẫn
            scrollableContent.getChildren().clear();
            scrollableContent.getChildren().add(taoHeaderChonThoiGian());
            
            Label lblHuongDan = new Label("Chọn phòng từ bảng bên trái để gia hạn");
            lblHuongDan.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280; -fx-padding: 40;");
            lblHuongDan.setAlignment(Pos.CENTER);
            scrollableContent.getChildren().add(lblHuongDan);
            
            hienThiThongBao(Alert.AlertType.INFORMATION, "Thành công", 
                "Tìm thấy " + danhSachPhongHienTai.size() + " phòng đang hoạt động\nVui lòng chọn phòng để gia hạn");
        } else {
            // Xóa dữ liệu cũ
            tablePhongGiaHan.getItems().clear();
            scrollableContent.getChildren().clear();
            scrollableContent.getChildren().add(taoHeaderChonThoiGian());
            
            // Ẩn thông tin khách hàng
            containerThongTinKhachHang.setVisible(false);
            containerThongTinKhachHang.setManaged(false);
            
            Label lblEmpty = new Label("Không tìm thấy phòng đang hoạt động");
            lblEmpty.setStyle("-fx-font-size: 14px; -fx-text-fill: #9ca3af; -fx-padding: 40;");
            lblEmpty.setAlignment(Pos.CENTER);
            scrollableContent.getChildren().add(lblEmpty);
            
            hienThiThongBao(Alert.AlertType.INFORMATION, "Thông báo", 
                "Không tìm thấy phòng nào đang hoạt động cho CCCD: " + cccd);
        }
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Đã xảy ra lỗi khi tìm kiếm phòng: " + e.getMessage());
        }
    }
    
    /**
     * Hiển thị danh sách phòng lên bảng
     */
    private void hienThiBangPhong() {
        try {
            if (danhSachPhongHienTai == null || danhSachPhongHienTai.isEmpty()) {
                tablePhongGiaHan.setItems(FXCollections.observableArrayList());
                return;
            }
            
            ObservableList<RoomExtensionRow> data = FXCollections.observableArrayList();
            
            for (ChiTietPhieuDatPhong ctpdp : danhSachPhongHienTai) {
                if (ctpdp == null || ctpdp.getPhong() == null || ctpdp.getPhong().getLoaiPhong() == null) {
                    continue;
                }
                
                String soPhong = ctpdp.getPhong().getMaPhong();
                String loaiPhong = ctpdp.getPhong().getLoaiPhong().getTenLoaiPhong();
                String tang = "Tầng " + ctpdp.getPhong().getTang();
                String gia = String.format("%.0f VND", ctpdp.getPhong().getLoaiPhong().getGia());
                
                data.add(new RoomExtensionRow(soPhong, loaiPhong, tang, gia));
            }
            
            tablePhongGiaHan.setItems(data);
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Đã xảy ra lỗi khi hiển thị danh sách phòng: " + e.getMessage());
        }
    }

    /**
     * Hiển thị panel gia hạn với dữ liệu động (deprecated - không dùng nữa)
     */
    
    /**
     * Cập nhật panel gia hạn theo phòng đã chọn từ bảng
     */
    private void capNhatPanelGiaHanTheoLuaChon() {
        try {
            // Lấy danh sách phòng đã chọn từ bảng
            var selectedItems = tablePhongGiaHan.getSelectionModel().getSelectedItems();
            
            if (selectedItems.isEmpty()) {
                return;
            }
            
            // Xóa dữ liệu cũ (chỉ xóa phần scrollable, giữ header và footer)
            scrollableContent.getChildren().clear();
            scrollableContent.getChildren().add(taoHeaderChonThoiGian());
            
            // Cập nhật danh sách phòng đã chọn
            danhSachPhongDaChon.clear();
            
            // Thêm các phòng đã chọn vào panel
            for (RoomExtensionRow row : selectedItems) {
                if (row == null || row.getSoPhong() == null) {
                    continue;
                }
                
                // Tìm ChiTietPhieuDatPhong tương ứng
                ChiTietPhieuDatPhong ctpdp = timPhongTheoMa(row.getSoPhong());
                if (ctpdp != null && ctpdp.getPhong() != null && ctpdp.getThoiGianTraPhong() != null) {
                    danhSachPhongDaChon.add(ctpdp);
                    
                    String soPhong = ctpdp.getPhong().getMaPhong();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    String ngayTraPhong = ctpdp.getThoiGianTraPhong().format(formatter);
                    
                    HBox rowBox = taoDongPhongGiaHan(soPhong, ngayTraPhong, "Chọn thời gian", ctpdp, 0);
                    scrollableContent.getChildren().add(rowBox);
                }
            }
            
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Đã xảy ra lỗi khi cập nhật panel gia hạn: " + e.getMessage());
        }
    }
    
    /**
     * Tìm ChiTietPhieuDatPhong theo mã phòng
     */
    private ChiTietPhieuDatPhong timPhongTheoMa(String maPhong) {
        try {
            if (danhSachPhongHienTai == null || maPhong == null || maPhong.isEmpty()) {
                return null;
            }
            
            for (ChiTietPhieuDatPhong ctpdp : danhSachPhongHienTai) {
                if (ctpdp != null && ctpdp.getPhong() != null && 
                    ctpdp.getPhong().getMaPhong() != null &&
                    ctpdp.getPhong().getMaPhong().equals(maPhong)) {
                    return ctpdp;
                }
            }
            
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Thực hiện gia hạn ngay cho tất cả phòng
     */
    private void thucHienGiaHanNgay() {
        try {
            if (danhSachPhongDaChon == null || danhSachPhongDaChon.isEmpty()) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", 
                    "Vui lòng chọn phòng từ bảng bên trái để gia hạn");
                return;
            }
            
            if (scrollableContent == null || scrollableContent.getChildren().size() <= 1) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", 
                    "Không có phòng nào được chọn để gia hạn");
                return;
            }
            
            int soPhongThanhCong = 0;
            int soPhongThatBai = 0;
            
            for (int i = 1; i < scrollableContent.getChildren().size(); i++) {
                try {
                    javafx.scene.Node node = scrollableContent.getChildren().get(i);
                    if (!(node instanceof HBox)) {
                        continue;
                    }
                    
                    HBox row = (HBox) node;
                    if (row.getChildren().size() < 3) {
                        continue;
                    }
                    
                    javafx.scene.Node vboxNode = row.getChildren().get(2);
                    if (!(vboxNode instanceof VBox)) {
                        continue;
                    }
                    
                    VBox vboxGiaHan = (VBox) vboxNode;
                    if (vboxGiaHan.getChildren().isEmpty()) {
                        continue;
                    }
                    
                    javafx.scene.Node btnNode = vboxGiaHan.getChildren().get(0);
                    if (!(btnNode instanceof Button)) {
                        continue;
                    }
                    
                    Button btnGiaHan = (Button) btnNode;
                    
                    // Lấy thông tin phòng tương ứng từ danh sách đã chọn
                    if (i - 1 >= danhSachPhongDaChon.size()) {
                        continue;
                    }
                    
                    ChiTietPhieuDatPhong ctpdp = danhSachPhongDaChon.get(i - 1);
                    if (ctpdp == null || ctpdp.getPhong() == null) {
                        soPhongThatBai++;
                        continue;
                    }
            
            // Kiểm tra xem đã chọn thời gian gia hạn chưa
            String userData = (String) btnGiaHan.getUserData();
            LocalDateTime gioKetThucMoi;
            
            if (userData != null && userData.startsWith("APPLIED:")) {
                // Đã được áp dụng rồi, bỏ qua (đã gia hạn trong database)
                continue;
            } else if (userData != null && !userData.equals("Chon thoi gian") && !userData.isEmpty()) {
                // Người dùng đã chọn thời gian qua modal nhưng chưa áp dụng
                try {
                    gioKetThucMoi = LocalDateTime.parse(userData);
                } catch (Exception e) {
                    // Nếu parse lỗi, báo lỗi và bỏ qua phòng này
                    soPhongThatBai++;
                    hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", 
                        "Lỗi định dạng thời gian cho phòng " + ctpdp.getPhong().getMaPhong() + ": " + e.getMessage());
                    continue;
                }
            } else {
                // Chưa chọn thời gian, thông báo thất bại
                soPhongThatBai++;
                continue;
            }
            
            // Gia hạn phòng
            try {
                boolean thanhCong = controller.giaHanDen(
                    ctpdp.getPhieuDatPhong().getMaPhieuDatPhong(),
                    ctpdp.getPhong().getMaPhong(),
                    gioKetThucMoi
                );
                
                if (thanhCong) {
                    soPhongThanhCong++;
                    // Đánh dấu đã áp dụng
                    btnGiaHan.setUserData("APPLIED:" + gioKetThucMoi.toString());
                } else {
                    soPhongThatBai++;
                }
            } catch (Exception e) {
                soPhongThatBai++;
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                    "Lỗi khi gia hạn phòng " + ctpdp.getPhong().getMaPhong() + ": " + e.getMessage());
            }
                } catch (Exception ex) {
                    // Lỗi khi xử lý phòng này, bỏ qua và tiếp tục
                    soPhongThatBai++;
                    continue;
                }
            }
        
        
        // Hiển thị thông báo kết quả
        if (soPhongThanhCong > 0 && soPhongThatBai == 0) {
            hienThiThongBao(Alert.AlertType.INFORMATION, "Thành công", 
                "Gia hạn thành công " + soPhongThanhCong + " phòng!");
        } else if (soPhongThanhCong > 0 && soPhongThatBai > 0) {
            hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", 
                "Gia hạn thành công " + soPhongThanhCong + " phòng\n" +
                "Gia hạn thất bại " + soPhongThatBai + " phòng");
        } else if (soPhongThatBai > 0) {
            hienThiThongBao(Alert.AlertType.ERROR, "Thất bại", 
                "Gia hạn thất bại tất cả " + soPhongThatBai + " phòng!");
        }
        
        // Xóa dữ liệu bên phải và cập nhật lại từ database
        if (soPhongThanhCong > 0) {
            // Xóa panel bên phải
            containerChonThoiGian.getChildren().clear();
            containerChonThoiGian.getChildren().add(taoHeaderChonThoiGian());
            
            Label lblHuongDan = new Label("Chọn phòng từ bảng bên trái để gia hạn");
            lblHuongDan.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280; -fx-padding: 40;");
            lblHuongDan.setAlignment(Pos.CENTER);
            containerChonThoiGian.getChildren().add(lblHuongDan);
            
            // Xóa danh sách phòng đã chọn
            danhSachPhongDaChon.clear();
            
            // Bỏ selection trên bảng
            tablePhongGiaHan.getSelectionModel().clearSelection();
            
            // Cập nhật lại dữ liệu bảng từ database
            capNhatLaiDuLieu();
        }
        } catch (Exception ex) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Đã xảy ra lỗi khi thực hiện gia hạn: " + ex.getMessage());
        }
    }
    
    /**
     * Cập nhật lại dữ liệu từ database sau khi gia hạn
     */
    private void capNhatLaiDuLieu() {
        try {
            // Lấy CCCD đang tìm kiếm
            String cccd = txtSoDienThoai.getText().trim();
            
            if (cccd.isEmpty()) {
                return;
            }
            
            // Gọi lại controller để lấy dữ liệu mới từ database
            danhSachPhongHienTai = controller.timDatPhongHienTaiTheoCCCD(cccd);
        
        if (danhSachPhongHienTai != null && !danhSachPhongHienTai.isEmpty()) {
            // Cập nhật lại bảng
            hienThiBangPhong();
            // Xóa panel bên phải và hiển thị hướng dẫn
            scrollableContent.getChildren().clear();
            scrollableContent.getChildren().add(taoHeaderChonThoiGian());
            
            Label lblHuongDan = new Label("Chọn phòng từ bảng bên trái để gia hạn");
            lblHuongDan.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280; -fx-padding: 40;");
            lblHuongDan.setAlignment(Pos.CENTER);
            scrollableContent.getChildren().add(lblHuongDan);
        } else {
            // Xóa dữ liệu hiển thị nếu không còn phòng
            tablePhongGiaHan.getItems().clear();
            scrollableContent.getChildren().clear();
            scrollableContent.getChildren().add(taoHeaderChonThoiGian());
        }
        } catch (Exception ex) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Đã xảy ra lỗi khi cập nhật lại dữ liệu: " + ex.getMessage());
        }
    }
    
    /**
     * Hiển thị thông báo Alert
     */
    private void hienThiThongBao(Alert.AlertType loai, String tieuDe, String noiDung) {
        Alert alert = new Alert(loai);
        alert.setTitle(tieuDe);
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
    }

    // Model cho dữ liệu bảng
    public static class RoomExtensionRow {
        private final String soPhong, loaiPhong, tang, gia;

        public RoomExtensionRow(String soPhong, String loaiPhong, String tang, String gia) {
            this.soPhong = soPhong;
            this.loaiPhong = loaiPhong;
            this.tang = tang;
            this.gia = gia;
        }

        public String getSoPhong() { return soPhong; }
        public String getLoaiPhong() { return loaiPhong; }
        public String getTang() { return tang; }
        public String getGia() { return gia; }
    }
}
