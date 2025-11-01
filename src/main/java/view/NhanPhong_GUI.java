package view;

import java.time.format.DateTimeFormatter;
import java.util.List;

import controller.NhanPhong_Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.ChiTietPhieuDatPhong;
import model.PhieuDatPhong;

/**
 * Giao diện nhận phòng (check-in)
 */
public class NhanPhong_GUI extends BorderPane {

    private TextField txtTimKiem;
    private TableView<RoomCheckInRow> tablePhongChuaNhan;
    private VBox containerThongTinNhan;
    private VBox scrollableContent;
    private NhanPhong_Controller controller;
    private PhieuDatPhong phieuDatPhongHienTai;
    private List<ChiTietPhieuDatPhong> danhSachPhongChuaNhan;
    private HBox containerThongTinKhachHang;

    public NhanPhong_GUI() {
        this.controller = new NhanPhong_Controller();
        this.phieuDatPhongHienTai = null;
        this.danhSachPhongChuaNhan = new java.util.ArrayList<>();
        
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f5f7fa;");
        getStylesheets().add(getClass().getResource("/css/Button.css").toExternalForm());
        getStylesheets().add(getClass().getResource("/css/Table.css").toExternalForm());

        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(10));
        
        // Phần tìm kiếm ở trên
        VBox topSection = taoVungTren();
        
        // Phần nội dung chính: bảng phòng và panel nhận phòng
        HBox contentSection = new HBox(20);
        contentSection.setAlignment(Pos.TOP_CENTER);
        
        VBox leftPanel = taoVungTrai();
        VBox rightPanel = taoVungPhai();
        
        // Responsive: bảng trái mở rộng, bảng phải vừa với nội dung
        HBox.setHgrow(leftPanel, javafx.scene.layout.Priority.ALWAYS);
        leftPanel.setMaxWidth(Double.MAX_VALUE);

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
        Label lblTieuDe = new Label("NHẬN PHÒNG");
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
        
        txtTimKiem = new TextField();
        txtTimKiem.setPromptText("Nhập CCCD khách hàng");
        txtTimKiem.setPrefWidth(200);
        txtTimKiem.setPrefHeight(35);
        txtTimKiem.setAlignment(Pos.CENTER_LEFT);
        txtTimKiem.setOnAction(e -> thucHienTimKiem());
        
        Button btnTimKiem = new Button("Tìm kiếm");
        btnTimKiem.setPrefHeight(35);
        btnTimKiem.setPrefWidth(100);
        btnTimKiem.getStyleClass().add("btn");
        btnTimKiem.setOnAction(e -> thucHienTimKiem());
        
        searchBox.getChildren().addAll(lblTimKiem, txtTimKiem, btnTimKiem);
        
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
            if (containerThongTinKhachHang == null) {
                return;
            }
            
            containerThongTinKhachHang.getChildren().clear();
            
            if (phieuDatPhongHienTai == null || phieuDatPhongHienTai.getKhachHang() == null) {
                containerThongTinKhachHang.setVisible(false);
                containerThongTinKhachHang.setManaged(false);
                return;
            }
            
            model.KhachHang kh = phieuDatPhongHienTai.getKhachHang();
            
            Label lblTen = new Label("Tên: " + (kh.getTenKhachHang() != null ? kh.getTenKhachHang() : "-"));
            lblTen.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
            
            Label lblSDT = new Label("SĐT: " + (kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "-"));
            lblSDT.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
            
            containerThongTinKhachHang.getChildren().addAll(lblTen, lblSDT);
            containerThongTinKhachHang.setVisible(true);
            containerThongTinKhachHang.setManaged(true);
            
        } catch (NullPointerException e) {
            if (containerThongTinKhachHang != null) {
                containerThongTinKhachHang.setVisible(false);
                containerThongTinKhachHang.setManaged(false);
            }
            // Không hiển thị thông báo lỗi cho người dùng vì đây là phần phụ
        } catch (Exception e) {
            if (containerThongTinKhachHang != null) {
                containerThongTinKhachHang.setVisible(false);
                containerThongTinKhachHang.setManaged(false);
            }
            // Không hiển thị thông báo lỗi cho người dùng vì đây là phần phụ
        }
    }

    /**
     * Tạo vùng trái: Bảng danh sách phòng chưa nhận
     */
    private VBox taoVungTrai() {
        VBox container = new VBox(10);
        
        Label lblTieuDe = new Label("DANH SÁCH PHÒNG CHƯA NHẬN");
        lblTieuDe.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        tablePhongChuaNhan = taoBangPhong();
        
        VBox tableBox = new VBox(tablePhongChuaNhan);
        VBox.setVgrow(tableBox, javafx.scene.layout.Priority.ALWAYS);

        container.getChildren().addAll(lblTieuDe, tableBox);
        return container;
    }

    private TableView<RoomCheckInRow> taoBangPhong() {
        tablePhongChuaNhan = new TableView<>();
        tablePhongChuaNhan.setMaxWidth(Double.MAX_VALUE);
        tablePhongChuaNhan.setPrefHeight(450);
        tablePhongChuaNhan.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablePhongChuaNhan.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        
        TableColumn<RoomCheckInRow, String> colSoPhong = new TableColumn<>("Số phòng");
        colSoPhong.setCellValueFactory(new PropertyValueFactory<>("soPhong"));
        // In đậm số phòng
        colSoPhong.setCellFactory(column -> {
            TableCell<RoomCheckInRow, String> cell = new TableCell<RoomCheckInRow, String>() {
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

        TableColumn<RoomCheckInRow, String> colLoaiPhong = new TableColumn<>("Loại phòng");
        colLoaiPhong.setCellValueFactory(new PropertyValueFactory<>("loaiPhong"));

        TableColumn<RoomCheckInRow, String> colTang = new TableColumn<>("Tầng");
        colTang.setCellValueFactory(new PropertyValueFactory<>("tang"));

        TableColumn<RoomCheckInRow, String> colNgayTra = new TableColumn<>("Ngày trả phòng");
        colNgayTra.setCellValueFactory(new PropertyValueFactory<>("ngayTraPhong"));

        tablePhongChuaNhan.getColumns().add(colSoPhong);
        tablePhongChuaNhan.getColumns().add(colLoaiPhong);
        tablePhongChuaNhan.getColumns().add(colTang);
        tablePhongChuaNhan.getColumns().add(colNgayTra);
        
        // Style header TableView thành xanh dương sau khi render
        javafx.application.Platform.runLater(() -> {
            javafx.scene.Node header = tablePhongChuaNhan.lookup(".column-header-background");
            if (header != null) {
                header.setStyle("-fx-background-color: #3b82f6;");
            }
            // Style các column header
            for (javafx.scene.Node node : tablePhongChuaNhan.lookupAll(".column-header")) {
                node.setStyle("-fx-background-color: #3b82f6; -fx-border-color: rgba(255,255,255,0.3); -fx-border-width: 0 0 0 1;");
            }
            // Style text trong header
            for (javafx.scene.Node node : tablePhongChuaNhan.lookupAll(".column-header .label")) {
                if (node instanceof Label) {
                    ((Label) node).setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                }
            }
        });

        return tablePhongChuaNhan;
    }

    private VBox taoVungPhai() {
        VBox container = new VBox(15);
        
        Label lblTieuDe = new Label("THÔNG TIN NHẬN PHÒNG");
        lblTieuDe.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        containerThongTinNhan = new VBox(0);
        containerThongTinNhan.setPrefWidth(560);
        containerThongTinNhan.setPrefHeight(450);
        containerThongTinNhan.setStyle("-fx-background-color: white; -fx-background-radius: 8;");

        // Phần scrollable: header + các dòng thông tin
        scrollableContent = new VBox(0);
        
        HBox headerRow = taoHeaderThongTinNhan();
        scrollableContent.getChildren().add(headerRow);
        
        // ScrollPane cho phần cuộn
        ScrollPane scrollPane = new ScrollPane(scrollableContent);
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPrefHeight(450);
        
        containerThongTinNhan.getChildren().addAll(scrollPane);

        Button btnNhanPhong = new Button("XÁC NHẬN NHẬN PHÒNG");
        btnNhanPhong.setPrefWidth(560);
        btnNhanPhong.setPrefHeight(35);
        btnNhanPhong.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        btnNhanPhong.setOnMouseEntered(e -> btnNhanPhong.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;"));
        btnNhanPhong.setOnMouseExited(e -> btnNhanPhong.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;"));
        btnNhanPhong.setOnAction(e -> thucHienNhanPhong());

        container.getChildren().addAll(lblTieuDe, containerThongTinNhan, btnNhanPhong);
        return container;
    }
    
    /**
     * Tạo header cho bảng thông tin nhận phòng - 4 cột (xanh lá)
     */
    private HBox taoHeaderThongTinNhan() {
        HBox header = new HBox(0);
        header.setPadding(new Insets(12));
        header.setStyle("-fx-background-color: #22c55e; -fx-border-color: #16a34a; -fx-border-width: 0 0 1 0;");

        Label lblSoPhong = new Label("Số phòng");
        lblSoPhong.setPrefWidth(80);
        lblSoPhong.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: white; -fx-padding: 0 10 0 0;");

        Label lblNgayTraPhong = new Label("Ngày trả phòng");
        lblNgayTraPhong.setPrefWidth(180);
        lblNgayTraPhong.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: white; -fx-border-color: transparent transparent transparent rgba(255,255,255,0.3); -fx-border-width: 0 0 0 1; -fx-padding: 0 10 0 15;");

        Label lblSoNguoi = new Label("Số người");
        lblSoNguoi.setPrefWidth(100);
        lblSoNguoi.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: white; -fx-border-color: transparent transparent transparent rgba(255,255,255,0.3); -fx-border-width: 0 0 0 1; -fx-padding: 0 10 0 15;");

        Label lblTrangThai = new Label("Trạng thái");
        lblTrangThai.setPrefWidth(200);
        lblTrangThai.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: white; -fx-border-color: transparent transparent transparent rgba(255,255,255,0.3); -fx-border-width: 0 0 0 1; -fx-padding: 0 10 0 15;");

        header.getChildren().addAll(lblSoPhong, lblNgayTraPhong, lblSoNguoi, lblTrangThai);
        return header;
    }

    private HBox taoDongThongTinNhan(String soPhong, String ngayTraPhong, int soNguoi, 
                                     ChiTietPhieuDatPhong chiTietPhieuDatPhong) {
        HBox row = new HBox(0);
        row.setPadding(new Insets(12));
        row.setStyle("-fx-border-color: transparent transparent #e5e7eb transparent; -fx-border-width: 0 0 1 0;");

        Label lblSoPhong = new Label(soPhong);
        lblSoPhong.setPrefWidth(80);
        lblSoPhong.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 0 10 0 0;");

        Label lblNgayTra = new Label(ngayTraPhong);
        lblNgayTra.setPrefWidth(180);
        lblNgayTra.setStyle("-fx-font-size: 12px; -fx-border-color: transparent transparent transparent #e5e7eb; -fx-border-width: 0 0 0 1; -fx-padding: 0 10 0 15;");

        Label lblSoNguoi = new Label(String.valueOf(soNguoi));
        lblSoNguoi.setPrefWidth(100);
        lblSoNguoi.setAlignment(Pos.CENTER);
        lblSoNguoi.setStyle("-fx-font-size: 12px; -fx-border-color: transparent transparent transparent #e5e7eb; -fx-border-width: 0 0 0 1; -fx-padding: 0 10 0 15;");

        Label lblTrangThai = new Label("Chưa nhận");
        lblTrangThai.setPrefWidth(200);
        lblTrangThai.setStyle("-fx-font-size: 12px; -fx-text-fill: #f59e0b; -fx-border-color: transparent transparent transparent #e5e7eb; -fx-border-width: 0 0 0 1; -fx-padding: 0 10 0 15;");

        row.getChildren().addAll(lblSoPhong, lblNgayTra, lblSoNguoi, lblTrangThai);
        return row;
    }
    
    /**
     * Thực hiện tìm kiếm phiếu đặt phòng theo CCCD
     */
    private void thucHienTimKiem() {
        try {
            // Kiểm tra controller có null không
            if (controller == null) {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi hệ thống", 
                    "Controller chưa được khởi tạo. Vui lòng khởi động lại ứng dụng.");
                return;
            }
            
            String cccd = txtTimKiem.getText().trim();
            if (cccd.isEmpty()) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập CCCD khách hàng");
                return;
            }
            
            // Validate format CCCD (12 số)
            if (!cccd.matches("\\d{12}")) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", 
                    "CCCD không hợp lệ. CCCD phải gồm 12 chữ số.");
                return;
            }
            
            // Gọi controller để tìm phiếu đặt phòng theo CCCD
            phieuDatPhongHienTai = controller.timPhieuDatPhongTheoCCCD(cccd);
        
            if (phieuDatPhongHienTai != null && phieuDatPhongHienTai.getDsachPhieuDatPhong() != null 
                && !phieuDatPhongHienTai.getDsachPhieuDatPhong().isEmpty()) {
                
                danhSachPhongChuaNhan = phieuDatPhongHienTai.getDsachPhieuDatPhong();
                hienThiBangPhong();
                hienThiThongTinNhan();
                hienThiThongTinKhachHang();
                
                hienThiThongBao(Alert.AlertType.INFORMATION, "Thành công", 
                    "Tìm thấy " + danhSachPhongChuaNhan.size() + " phòng chưa nhận\nVui lòng xác nhận nhận phòng");
            } else {
                // Xóa dữ liệu cũ
                tablePhongChuaNhan.getItems().clear();
                if (scrollableContent != null) {
                    scrollableContent.getChildren().clear();
                    scrollableContent.getChildren().add(taoHeaderThongTinNhan());
                }
                
                // Ẩn thông tin khách hàng
                if (containerThongTinKhachHang != null) {
                    containerThongTinKhachHang.setVisible(false);
                    containerThongTinKhachHang.setManaged(false);
                }
                
                if (scrollableContent != null) {
                    Label lblEmpty = new Label("Không tìm thấy phòng chưa nhận");
                    lblEmpty.setStyle("-fx-font-size: 14px; -fx-text-fill: #9ca3af; -fx-padding: 40;");
                    lblEmpty.setAlignment(Pos.CENTER);
                    scrollableContent.getChildren().add(lblEmpty);
                }
                
                hienThiThongBao(Alert.AlertType.INFORMATION, "Thông báo", 
                    "Không tìm thấy phòng chưa nhận cho CCCD: " + cccd);
            }
        } catch (NullPointerException e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại thông tin.");
            e.printStackTrace();
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Đã xảy ra lỗi khi tìm kiếm: " + (e.getMessage() != null ? e.getMessage() : "Lỗi không xác định"));
            e.printStackTrace();
        }
    }
    
    /**
     * Hiển thị danh sách phòng lên bảng
     */
    private void hienThiBangPhong() {
        try {
            if (tablePhongChuaNhan == null) {
                return;
            }
            
            if (danhSachPhongChuaNhan == null || danhSachPhongChuaNhan.isEmpty()) {
                tablePhongChuaNhan.setItems(FXCollections.observableArrayList());
                return;
            }
            
            ObservableList<RoomCheckInRow> data = FXCollections.observableArrayList();
            
            for (ChiTietPhieuDatPhong ctpdp : danhSachPhongChuaNhan) {
                if (ctpdp == null) {
                    continue;
                }
                
                if (ctpdp.getPhong() == null || ctpdp.getPhong().getLoaiPhong() == null) {
                    continue;
                }
                
                String soPhong = ctpdp.getPhong().getSoPhong();
                if (soPhong == null || soPhong.isEmpty()) {
                    soPhong = "-";
                }
                
                String loaiPhong = ctpdp.getPhong().getLoaiPhong().getTenLoaiPhong();
                if (loaiPhong == null || loaiPhong.isEmpty()) {
                    loaiPhong = "-";
                }
                
                int tangValue = ctpdp.getPhong().getTang();
                String tang = "Tầng " + tangValue;
                
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                String ngayTra = ctpdp.getThoiGianTraPhong() != null 
                    ? ctpdp.getThoiGianTraPhong().format(formatter) 
                    : "-";
                
                data.add(new RoomCheckInRow(soPhong, loaiPhong, tang, ngayTra));
            }
            
            tablePhongChuaNhan.setItems(data);
        } catch (NullPointerException e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Dữ liệu phòng không hợp lệ. Vui lòng kiểm tra lại.");
            e.printStackTrace();
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Đã xảy ra lỗi khi hiển thị danh sách phòng: " + (e.getMessage() != null ? e.getMessage() : "Lỗi không xác định"));
            e.printStackTrace();
        }
    }

    /**
     * Hiển thị thông tin nhận phòng
     */
    private void hienThiThongTinNhan() {
        try {
            if (scrollableContent == null) {
                return;
            }
            
            if (danhSachPhongChuaNhan == null || danhSachPhongChuaNhan.isEmpty()) {
                scrollableContent.getChildren().clear();
                scrollableContent.getChildren().add(taoHeaderThongTinNhan());
                
                Label lblEmpty = new Label("Không có phòng nào cần nhận");
                lblEmpty.setStyle("-fx-font-size: 14px; -fx-text-fill: #9ca3af; -fx-padding: 40;");
                lblEmpty.setAlignment(Pos.CENTER);
                scrollableContent.getChildren().add(lblEmpty);
                return;
            }
            
            scrollableContent.getChildren().clear();
            scrollableContent.getChildren().add(taoHeaderThongTinNhan());
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            for (ChiTietPhieuDatPhong ctpdp : danhSachPhongChuaNhan) {
                if (ctpdp == null) {
                    continue;
                }
                
                if (ctpdp.getPhong() == null) {
                    continue;
                }
                
                String soPhong = ctpdp.getPhong().getSoPhong();
                if (soPhong == null || soPhong.isEmpty()) {
                    soPhong = "-";
                }
                
                String ngayTraPhong = ctpdp.getThoiGianTraPhong() != null 
                    ? ctpdp.getThoiGianTraPhong().format(formatter) 
                    : "-";
                
                int soNguoi = ctpdp.getSoNguoi();
                if (soNguoi < 0) {
                    soNguoi = 0;
                }
                
                HBox rowBox = taoDongThongTinNhan(soPhong, ngayTraPhong, soNguoi, ctpdp);
                scrollableContent.getChildren().add(rowBox);
            }
            
        } catch (NullPointerException e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Dữ liệu phòng không hợp lệ. Vui lòng kiểm tra lại.");
            e.printStackTrace();
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Đã xảy ra lỗi khi hiển thị thông tin nhận phòng: " + (e.getMessage() != null ? e.getMessage() : "Lỗi không xác định"));
            e.printStackTrace();
        }
    }
    
    /**
     * Thực hiện nhận phòng cho tất cả phòng
     */
    private void thucHienNhanPhong() {
        try {
            // Kiểm tra controller có null không
            if (controller == null) {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi hệ thống", 
                    "Controller chưa được khởi tạo. Vui lòng khởi động lại ứng dụng.");
                return;
            }
            
            if (danhSachPhongChuaNhan == null || danhSachPhongChuaNhan.isEmpty()) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", 
                    "Không có phòng nào để nhận");
                return;
            }
            
            // Kiểm tra dữ liệu hợp lệ trước khi nhận phòng
            int soPhongHopLe = 0;
            int soPhongKhongHopLe = 0;
            StringBuilder danhSachPhongLoi = new StringBuilder();
            
            for (ChiTietPhieuDatPhong ctpdp : danhSachPhongChuaNhan) {
                if (ctpdp == null) {
                    soPhongKhongHopLe++;
                    continue;
                }
                
                if (ctpdp.getPhieuDatPhong() == null || ctpdp.getPhieuDatPhong().getMaPhieuDatPhong() == null 
                    || ctpdp.getPhieuDatPhong().getMaPhieuDatPhong().isEmpty()) {
                    soPhongKhongHopLe++;
                    if (ctpdp.getPhong() != null && ctpdp.getPhong().getSoPhong() != null) {
                        danhSachPhongLoi.append(ctpdp.getPhong().getSoPhong()).append(", ");
                    }
                    continue;
                }
                
                if (ctpdp.getPhong() == null || ctpdp.getPhong().getMaPhong() == null 
                    || ctpdp.getPhong().getMaPhong().isEmpty()) {
                    soPhongKhongHopLe++;
                    continue;
                }
                
                soPhongHopLe++;
            }
            
            if (soPhongKhongHopLe > 0) {
                String thongBao = "Có " + soPhongKhongHopLe + " phòng có dữ liệu không hợp lệ";
                if (danhSachPhongLoi.length() > 0) {
                    thongBao += ":\n" + danhSachPhongLoi.toString().replaceAll(", $", "");
                }
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", thongBao);
                
                if (soPhongHopLe == 0) {
                    return; // Không có phòng nào hợp lệ để nhận
                }
            }
            
            // Nhận tất cả phòng
            boolean thanhCong = controller.nhanNhieuPhong(danhSachPhongChuaNhan);
            
            if (thanhCong) {
                String thongBaoThanhCong = "Nhận phòng thành công cho " + soPhongHopLe + " phòng!";
                if (soPhongKhongHopLe > 0) {
                    thongBaoThanhCong += "\n(Lưu ý: " + soPhongKhongHopLe + " phòng không hợp lệ đã được bỏ qua)";
                }
                hienThiThongBao(Alert.AlertType.INFORMATION, "Thành công", thongBaoThanhCong);
                
                // Xóa dữ liệu hiển thị
                if (tablePhongChuaNhan != null) {
                    tablePhongChuaNhan.getItems().clear();
                }
                if (scrollableContent != null) {
                    scrollableContent.getChildren().clear();
                    scrollableContent.getChildren().add(taoHeaderThongTinNhan());
                    
                    Label lblEmpty = new Label("Đã nhận phòng thành công");
                    lblEmpty.setStyle("-fx-font-size: 14px; -fx-text-fill: #22c55e; -fx-padding: 40;");
                    lblEmpty.setAlignment(Pos.CENTER);
                    scrollableContent.getChildren().add(lblEmpty);
                }
                
                // Xóa dữ liệu
                phieuDatPhongHienTai = null;
                danhSachPhongChuaNhan.clear();
                
                // Ẩn thông tin khách hàng
                if (containerThongTinKhachHang != null) {
                    containerThongTinKhachHang.setVisible(false);
                    containerThongTinKhachHang.setManaged(false);
                }
                
                // Xóa text tìm kiếm
                if (txtTimKiem != null) {
                    txtTimKiem.clear();
                }
            } else {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                    "Nhận phòng thất bại! Có thể một số phòng đã được nhận hoặc có lỗi xảy ra. Vui lòng kiểm tra lại và thử lại.");
            }
        } catch (NullPointerException e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Dữ liệu phòng không hợp lệ. Vui lòng kiểm tra lại thông tin.");
            e.printStackTrace();
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Đã xảy ra lỗi khi nhận phòng: " + (e.getMessage() != null ? e.getMessage() : "Lỗi không xác định"));
            e.printStackTrace();
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
    public static class RoomCheckInRow {
        private final String soPhong, loaiPhong, tang, ngayTraPhong;

        public RoomCheckInRow(String soPhong, String loaiPhong, String tang, String ngayTraPhong) {
            this.soPhong = soPhong;
            this.loaiPhong = loaiPhong;
            this.tang = tang;
            this.ngayTraPhong = ngayTraPhong;
        }

        public String getSoPhong() { return soPhong; }
        public String getLoaiPhong() { return loaiPhong; }
        public String getTang() { return tang; }
        public String getNgayTraPhong() { return ngayTraPhong; }
    }
}

