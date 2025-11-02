package view.Phong;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import controller.GiaHanPhong_Controller;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
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
import model.KhachHang;

/**
 * Giao diện gia hạn phòng - Thiết kế mới theo pattern NhanPhong_GUI
 */
public class GiaHanPhong_GUI extends BorderPane {

    // Constants
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String PHONE_REGEX = "^0[0-9]{9,10}$";
    
    private TextField txtTimKiem;
    private TableView<RoomExtensionRow> tablePhongDangO;
    private VBox containerPhongDaChon;
    private VBox scrollableContentPhongDaChon;
    private GiaHanPhong_Controller controller;
    private List<ChiTietPhieuDatPhong> danhSachPhongDangO;
    private ObservableList<RoomExtensionRow> danhSachPhongDangOData;
    private List<ChiTietPhieuDatPhong> danhSachPhongDaChon;
    private HBox containerThongTinKhachHang;
    private KhachHang khachHangHienTai;
    private Button btnGiaHan;

    public GiaHanPhong_GUI() {
        try {
        this.controller = new GiaHanPhong_Controller();
            this.danhSachPhongDangO = new ArrayList<>();
            this.danhSachPhongDangOData = FXCollections.observableArrayList();
            this.danhSachPhongDaChon = new ArrayList<>();
            this.khachHangHienTai = null;
        
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f5f7fa;");
            
            try {
        getStylesheets().add(getClass().getResource("/css/Button.css").toExternalForm());
        getStylesheets().add(getClass().getResource("/css/Table.css").toExternalForm());
                getStylesheets().add(getClass().getResource("/css/NhanPhong.css").toExternalForm());
            } catch (Exception e) {
                System.err.println("Không thể tải CSS: " + e.getMessage());
            }

        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(10));
        
            // Phần tìm kiếm ở trên
        VBox topSection = taoVungTren();
        
        // Phần nội dung chính: bảng phòng và panel gia hạn
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
            
            // Auto-load tất cả phòng đang ở khi vào trang
            Platform.runLater(() -> {
                try {
                    tuDongLoadDanhSachPhong();
                } catch (Exception e) {
                    hienThiThongBao(Alert.AlertType.ERROR, "Lỗi khởi tạo", 
                        "Không thể khởi tạo giao diện: " + (e.getMessage() != null ? e.getMessage() : "Lỗi không xác định"));
                }
            });
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi hệ thống", 
                "Không thể khởi tạo giao diện gia hạn phòng: " + (e.getMessage() != null ? e.getMessage() : "Lỗi không xác định"));
            e.printStackTrace();
        }
    }
    
    /**
     * Tự động load danh sách phòng đang ở khi vào trang
     */
    private void tuDongLoadDanhSachPhong() {
        try {
            if (controller == null) {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi hệ thống", 
                    "Controller chưa được khởi tạo. Vui lòng khởi động lại ứng dụng.");
                return;
            }
            
            danhSachPhongDangO = controller.layTatCaPhongDangO();
            if (danhSachPhongDangO == null) {
                danhSachPhongDangO = new ArrayList<>();
            }
            capNhatBangPhongDangO();
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Không thể tải danh sách phòng đang ở.");
            e.printStackTrace();
        }
    }
    
    /**
     * Tạo vùng trên: Tìm kiếm và thông tin khách hàng
     */
    private VBox taoVungTren() {
        VBox container = new VBox(15);
        
        Label lblTieuDe = taoLabelTieuDe("GIA HẠN PHÒNG", 20);
        
        HBox mainBox = new HBox(20);
        mainBox.setAlignment(Pos.CENTER_LEFT);
        
        HBox searchBox = new HBox(10);
        searchBox.setPadding(new Insets(12));
        searchBox.setAlignment(Pos.CENTER_LEFT);
        
        Label lblTimKiem = taoLabel("Số điện thoại:", 14, true);
        
        txtTimKiem = taoTextField("Nhập số điện thoại khách hàng", 200, 35);
        txtTimKiem.setOnAction(e -> thucHienTimKiem());
        
        Button btnTimKiem = taoButton("Tìm kiếm", 100, 35, "btn");
        btnTimKiem.setOnAction(e -> thucHienTimKiem());

        searchBox.getChildren().addAll(lblTimKiem, txtTimKiem, btnTimKiem);
        
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
     * Tạo vùng trái: Bảng danh sách phòng đang ở
     */
    private VBox taoVungTrai() {
        VBox container = new VBox(10);
        
        // Tiêu đề và button làm mới
        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label lblTieuDe = taoLabelTieuDe("DANH SÁCH PHÒNG ĐANG Ở", 16);
        
        Button btnLamMoi = taoButton("🔄 Làm mới", 110, 30, "btn-small");
        btnLamMoi.setOnAction(e -> {
            try {
                if (txtTimKiem != null && !txtTimKiem.getText().trim().isEmpty()) {
                    thucHienTimKiem();
                } else {
                    tuDongLoadDanhSachPhong();
                }
            } catch (Exception ex) {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                    "Không thể làm mới dữ liệu.");
                ex.printStackTrace();
            }
        });
        
        headerBox.getChildren().addAll(lblTieuDe, btnLamMoi);

        tablePhongDangO = taoBangPhongDangO();
        
        VBox tableBox = new VBox(tablePhongDangO);
        VBox.setVgrow(tableBox, javafx.scene.layout.Priority.ALWAYS);

        container.getChildren().addAll(headerBox, tableBox);
        return container;
    }

    /**
     * Tạo bảng phòng đang ở với các cột: checkbox, số phòng, thời gian trả, nút xem chi tiết
     */
    private TableView<RoomExtensionRow> taoBangPhongDangO() {
        tablePhongDangO = new TableView<>();
        tablePhongDangO.setMaxWidth(Double.MAX_VALUE);
        tablePhongDangO.setPrefHeight(450);
        tablePhongDangO.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablePhongDangO.getStyleClass().add("container-white");
        
        // Cột checkbox
        TableColumn<RoomExtensionRow, Boolean> colCheckbox = new TableColumn<>("");
        colCheckbox.setPrefWidth(50);
        colCheckbox.setCellValueFactory(param -> param.getValue().selectedProperty());
        colCheckbox.setCellFactory(column -> {
            TableCell<RoomExtensionRow, Boolean> cell = new TableCell<RoomExtensionRow, Boolean>() {
                private CheckBox checkBox = new CheckBox();
                
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        checkBox.setSelected(item != null && item);
                        checkBox.setOnAction(e -> {
                            try {
                                RoomExtensionRow row = getTableView().getItems().get(getIndex());
                                if (row != null && row.chiTietPhieuDatPhong != null) {
                                    if (checkBox.isSelected()) {
                                        themPhongVaoDanhSachDaChon(row.chiTietPhieuDatPhong);
                                    } else {
                                        xoaPhongKhoiDanhSachDaChon(row.chiTietPhieuDatPhong);
                                    }
                                }
                            } catch (Exception ex) {
                                System.err.println("Lỗi khi xử lý checkbox: " + ex.getMessage());
                            }
                        });
                        setGraphic(checkBox);
                        setAlignment(Pos.CENTER);
                    }
                }
            };
            return cell;
        });

        // Cột số phòng
        TableColumn<RoomExtensionRow, String> colSoPhong = new TableColumn<>("Số phòng");
        colSoPhong.setCellValueFactory(new PropertyValueFactory<>("soPhong"));
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

        // Cột thời gian trả phòng
        TableColumn<RoomExtensionRow, String> colThoiGianTra = new TableColumn<>("Thời gian trả phòng");
        colThoiGianTra.setCellValueFactory(new PropertyValueFactory<>("thoiGianTraPhong"));
        colThoiGianTra.setPrefWidth(200);

        // Cột có thể gia hạn đến
        TableColumn<RoomExtensionRow, String> colCoTheGiaHanDen = new TableColumn<>("Có thể gia hạn đến");
        colCoTheGiaHanDen.setCellValueFactory(new PropertyValueFactory<>("coTheGiaHanDen"));
        colCoTheGiaHanDen.setPrefWidth(200);
        colCoTheGiaHanDen.setCellFactory(column -> {
            TableCell<RoomExtensionRow, String> cell = new TableCell<RoomExtensionRow, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        if (item.equals("Full time")) {
                            setStyle("-fx-text-fill: #22c55e; -fx-font-weight: bold; -fx-font-size: 12px;");
                        } else {
                            setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold; -fx-font-size: 12px;");
                        }
                    }
                }
            };
            return cell;
        });

        // Cột nút xem chi tiết
        TableColumn<RoomExtensionRow, Void> colXemChiTiet = new TableColumn<>("Chi tiết");
        colXemChiTiet.setPrefWidth(100);
        colXemChiTiet.setCellFactory(column -> {
            TableCell<RoomExtensionRow, Void> cell = new TableCell<RoomExtensionRow, Void>() {
                private Button btnXemChiTiet = new Button("Xem");
                
                {
                    btnXemChiTiet.getStyleClass().add("btn-small");
                    btnXemChiTiet.setPrefWidth(70);
                    btnXemChiTiet.setPrefHeight(25);
                    btnXemChiTiet.setOnAction(e -> {
                        try {
                            RoomExtensionRow row = getTableView().getItems().get(getIndex());
                            if (row != null && row.chiTietPhieuDatPhong != null) {
                                hienThiModalChiTietPhong(row.chiTietPhieuDatPhong);
                            }
                        } catch (Exception ex) {
                            System.err.println("Lỗi khi click xem chi tiết: " + ex.getMessage());
                        }
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(btnXemChiTiet);
                    }
                }
            };
            return cell;
        });

        tablePhongDangO.getColumns().addAll(colCheckbox, colSoPhong, colThoiGianTra, colCoTheGiaHanDen, colXemChiTiet);
        tablePhongDangO.setItems(danhSachPhongDangOData);
        
        // Style header
        apDungStyleTableHeader(tablePhongDangO);

        return tablePhongDangO;
    }

    /**
     * Tạo vùng phải: Hiển thị thông tin khách hàng và phòng đã chọn để gia hạn
     */
    private VBox taoVungPhai() {
        VBox container = new VBox(15);
        container.setPrefWidth(400);
        
        Label lblTieuDe = taoLabelTieuDe("THÔNG TIN GIA HẠN", 16);

        containerPhongDaChon = new VBox(10);
        containerPhongDaChon.setPrefWidth(400);
        containerPhongDaChon.setPrefHeight(450);
        containerPhongDaChon.getStyleClass().add("container-white");

        scrollableContentPhongDaChon = new VBox(10);
        
        ScrollPane scrollPane = taoScrollPane(scrollableContentPhongDaChon, 380);
        containerPhongDaChon.getChildren().add(scrollPane);
        
        Label lblEmpty = taoLabelEmpty("Chưa có phòng nào được chọn");
        scrollableContentPhongDaChon.getChildren().add(lblEmpty);

        btnGiaHan = taoButtonConfirm("XÁC NHẬN GIA HẠN", 400, 40);
        btnGiaHan.setOnAction(e -> thucHienGiaHanNgay());
        btnGiaHan.setDisable(true);

        container.getChildren().addAll(lblTieuDe, containerPhongDaChon, btnGiaHan);
        return container;
    }
    
    /**
     * Thực hiện tìm kiếm theo số điện thoại
     */
    private void thucHienTimKiem() {
        try {
            if (controller == null) {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi hệ thống", 
                    "Controller chưa được khởi tạo. Vui lòng khởi động lại ứng dụng.");
                return;
            }
            
            if (txtTimKiem == null) {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi hệ thống", 
                    "Trường tìm kiếm chưa được khởi tạo.");
                return;
            }
            
            String soDienThoai = txtTimKiem.getText().trim();
            if (soDienThoai.isEmpty()) {
                // Nếu rỗng, load lại tất cả phòng đang ở
                tuDongLoadDanhSachPhong();
                hienThiThongTinKhachHang(null);
                return;
            }
            
            if (!isValidPhoneNumber(soDienThoai)) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", 
                    "Số điện thoại không hợp lệ. Số điện thoại phải gồm 10-11 chữ số và bắt đầu bằng 0.");
                return;
            }
            
            // Tìm khách hàng theo số điện thoại
            khachHangHienTai = controller.timKhachHangTheoSoDienThoai(soDienThoai);
            
            // Hiển thị thông tin khách hàng (dù có phòng đang ở hay không)
            hienThiThongTinKhachHang(khachHangHienTai);
            
            // Lấy danh sách phòng đang ở theo số điện thoại
            danhSachPhongDangO = controller.timDatPhongHienTaiTheoSoDienThoai(soDienThoai);
            if (danhSachPhongDangO == null) {
                danhSachPhongDangO = new ArrayList<>();
            }
            capNhatBangPhongDangO();
            
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Đã xảy ra lỗi khi tìm kiếm.");
            e.printStackTrace();
        }
    }
    
    /**
     * Hiển thị thông tin khách hàng bên phải (dù có phòng đang ở hay không)
     */
    private void hienThiThongTinKhachHang(KhachHang khachHang) {
        try {
            if (containerThongTinKhachHang == null) {
                        return;
                    }
                    
            containerThongTinKhachHang.getChildren().clear();
            
            if (khachHang == null) {
                containerThongTinKhachHang.setVisible(false);
                containerThongTinKhachHang.setManaged(false);
                        return;
                    }
                    
            Label lblTen = taoLabel("Khách hàng: " + safeString(khachHang.getTenKhachHang()), 14, true);
            
            Button btnXemChiTiet = taoButton("Xem chi tiết", 100, 30, "btn-small");
            btnXemChiTiet.setOnAction(e -> hienThiModalChiTietKhachHang(khachHang));
            
            containerThongTinKhachHang.getChildren().addAll(lblTen, btnXemChiTiet);
            containerThongTinKhachHang.setVisible(true);
            containerThongTinKhachHang.setManaged(true);
            
        } catch (Exception e) {
            if (containerThongTinKhachHang != null) {
                containerThongTinKhachHang.setVisible(false);
                containerThongTinKhachHang.setManaged(false);
            }
        }
    }
    
    /**
     * Cập nhật bảng phòng đang ở
     */
    private void capNhatBangPhongDangO() {
        try {
            if (tablePhongDangO == null || danhSachPhongDangOData == null) {
                        return;
                    }
                    
            Platform.runLater(() -> {
                try {
                    danhSachPhongDangOData.clear();
                    
                    if (danhSachPhongDangO == null || danhSachPhongDangO.isEmpty()) {
                        tablePhongDangO.refresh();
                        return;
                    }
                    
                    for (ChiTietPhieuDatPhong ctpdp : danhSachPhongDangO) {
                        if (!isValidChiTietPhieuDatPhong(ctpdp)) {
                            continue;
                        }
                        
                        try {
                            String soPhong = safeString(ctpdp.getPhong().getSoPhong(), "-");
                            String thoiGianTra = formatThoiGian(ctpdp.getThoiGianTraPhong());
                            
                            // Tính toán thời gian gia hạn tối đa
                            String coTheGiaHanDen = tinhThoiGianGiaHanToiDa(ctpdp);
                            
                            danhSachPhongDangOData.add(new RoomExtensionRow(soPhong, thoiGianTra, coTheGiaHanDen, ctpdp));
                        } catch (Exception e) {
                            System.err.println("Lỗi khi thêm phòng vào bảng: " + e.getMessage());
                        }
                    }
                    
                    tablePhongDangO.refresh();
                } catch (Exception e) {
                    hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                        "Không thể cập nhật bảng phòng đang ở.");
                    e.printStackTrace();
                }
            });
            
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Không thể cập nhật bảng phòng đang ở.");
            e.printStackTrace();
        }
    }
    
    /**
     * Tính toán thời gian gia hạn tối đa cho phòng
     * Trả về "Full time" nếu không có ai đặt trong tương lai
     * Trả về thời gian (đến sớm hơn 2 giờ từ thời gian nhận phòng tiếp theo) nếu có người đặt
     */
    private String tinhThoiGianGiaHanToiDa(ChiTietPhieuDatPhong ctpdp) {
        try {
            if (!isValidChiTietPhieuDatPhong(ctpdp) || 
                ctpdp.getThoiGianTraPhong() == null ||
                ctpdp.getPhong() == null ||
                ctpdp.getPhong().getMaPhong() == null) {
                return "-";
            }
            
            if (controller == null) {
                return "-";
            }
            
            LocalDateTime thoiGianTraHienTai = ctpdp.getThoiGianTraPhong();
            LocalDateTime thoiGianGiaHanToiDa = controller.layThoiGianGiaHanToiDa(
                ctpdp.getPhong().getMaPhong(), 
                thoiGianTraHienTai
            );
            
            if (thoiGianGiaHanToiDa == null) {
                // Không có đặt phòng tiếp theo, có thể gia hạn không giới hạn
                return "Full time";
            } else {
                // Có đặt phòng tiếp theo, hiển thị thời gian tối đa (đã trừ 2 giờ)
                return formatThoiGian(thoiGianGiaHanToiDa);
            }
        } catch (Exception e) {
            System.err.println("Lỗi tính thời gian gia hạn tối đa: " + e.getMessage());
            return "-";
        }
    }
    
    /**
     * Thêm phòng vào danh sách đã chọn
     */
    private void themPhongVaoDanhSachDaChon(ChiTietPhieuDatPhong ctpdp) {
        try {
            if (ctpdp == null) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", 
                    "Thông tin phòng không hợp lệ.");
                return;
            }
            
            if (!isValidChiTietPhieuDatPhong(ctpdp)) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", 
                    "Thông tin phòng không đầy đủ.");
                return;
            }
            
            if (danhSachPhongDaChon == null) {
                danhSachPhongDaChon = new ArrayList<>();
            }
            
            if (!isPhongTrongDanhSach(ctpdp, danhSachPhongDaChon)) {
                danhSachPhongDaChon.add(ctpdp);
                capNhatHienThiPhongDaChon();
            }
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Không thể thêm phòng vào danh sách đã chọn.");
            e.printStackTrace();
        }
    }

    /**
     * Xóa phòng khỏi danh sách đã chọn
     */
    private void xoaPhongKhoiDanhSachDaChon(ChiTietPhieuDatPhong ctpdp) {
        try {
            if (!isValidChiTietPhieuDatPhong(ctpdp)) {
                return;
            }
            
            if (danhSachPhongDaChon == null) {
                return;
            }
            
            danhSachPhongDaChon.removeIf(p -> p != null && p.getPhong() != null && p.getPhong().getMaPhong() != null 
                && p.getPhong().getMaPhong().equals(ctpdp.getPhong().getMaPhong()));
            
            capNhatHienThiPhongDaChon();
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Không thể xóa phòng khỏi danh sách đã chọn.");
            e.printStackTrace();
        }
    }
    
    /**
     * Kiểm tra tất cả card có hợp lệ không để enable/disable nút gia hạn
     */
    private void kiemTraTatCaCardHopLe() {
        try {
            if (btnGiaHan == null) {
                return;
            }
            
            if (scrollableContentPhongDaChon == null || scrollableContentPhongDaChon.getChildren().isEmpty()) {
                btnGiaHan.setDisable(true);
                return;
            }
            
            boolean coCardHopLe = false;
            
            for (javafx.scene.Node node : scrollableContentPhongDaChon.getChildren()) {
                if (!(node instanceof VBox)) {
                    continue;
                }
                
                VBox card = (VBox) node;
                Object userData = card.getUserData();
                
                if (userData instanceof ExtensionInfo) {
                    ExtensionInfo info = (ExtensionInfo) userData;
                    if (info.hopLe && info.thoiGianGiaHanMoi != null) {
                        coCardHopLe = true;
                        break;
                    }
                }
            }
            
            btnGiaHan.setDisable(!coCardHopLe);
        } catch (Exception e) {
            System.err.println("Lỗi khi kiểm tra card hợp lệ: " + e.getMessage());
            if (btnGiaHan != null) {
                btnGiaHan.setDisable(true);
            }
        }
    }
    
    /**
     * Cập nhật hiển thị phòng đã chọn
     */
    private void capNhatHienThiPhongDaChon() {
        try {
            if (scrollableContentPhongDaChon == null) {
                return;
            }
            
            scrollableContentPhongDaChon.getChildren().clear();
            
            if (danhSachPhongDaChon == null || danhSachPhongDaChon.isEmpty()) {
                Label lblEmpty = taoLabelEmpty("Chưa có phòng nào được chọn");
                scrollableContentPhongDaChon.getChildren().add(lblEmpty);
                kiemTraTatCaCardHopLe();
                return;
            }
            
            for (ChiTietPhieuDatPhong ctpdp : danhSachPhongDaChon) {
                if (isValidChiTietPhieuDatPhong(ctpdp)) {
                    VBox card = taoCardPhongDaChon(ctpdp);
                    if (card != null) {
                        scrollableContentPhongDaChon.getChildren().add(card);
                    }
                }
        }
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Không thể cập nhật hiển thị phòng đã chọn.");
            e.printStackTrace();
        }
    }
    
    /**
     * Tạo card phòng đã chọn với DatePicker có ràng buộc thời gian
     */
    private VBox taoCardPhongDaChon(ChiTietPhieuDatPhong ctpdp) {
        try {
            if (!isValidChiTietPhieuDatPhong(ctpdp) || ctpdp.getThoiGianTraPhong() == null) {
                return null;
            }
            
            VBox card = new VBox(10);
            card.getStyleClass().add("card-room");
            card.setPrefWidth(370);
            card.setUserData(ctpdp);
            
            Label lblSoPhong = taoLabel("Phòng: " + safeString(ctpdp.getPhong().getSoPhong(), "-"), 16, true);
            Label lblThoiGianTra = taoLabel("Trả phòng: " + formatThoiGian(ctpdp.getThoiGianTraPhong()), 12, false);
            
            VBox dateTimeContainer = new VBox(5);
            
            Label lblGiaHanDen = taoLabel("Gia hạn đến:", 12, false);
            lblGiaHanDen.setPrefWidth(100);
            
            HBox dateTimeBox = new HBox(10);
            dateTimeBox.setAlignment(Pos.CENTER_LEFT);
            
            DatePicker datePicker = new DatePicker();
            datePicker.setPrefWidth(150);
            datePicker.setValue(ctpdp.getThoiGianTraPhong().toLocalDate());
            
            ComboBox<String> cboGio = new ComboBox<>();
            for (int i = 0; i < 24; i++) {
                cboGio.getItems().add(String.format("%02d:00", i));
            }
            cboGio.setValue(String.format("%02d:00", ctpdp.getThoiGianTraPhong().getHour()));
            cboGio.setPrefWidth(100);
            
            dateTimeBox.getChildren().addAll(datePicker, cboGio);
            dateTimeContainer.getChildren().addAll(lblGiaHanDen, dateTimeBox);
            
            // Lấy thời gian gia hạn tối đa (2 giờ trước khi có đặt phòng tiếp theo)
            LocalDateTime thoiGianTraHienTai = ctpdp.getThoiGianTraPhong();
            LocalDateTime thoiGianGiaHanToiDa = controller.layThoiGianGiaHanToiDa(
                ctpdp.getPhong().getMaPhong(), 
                thoiGianTraHienTai
            );
            
            // Set ràng buộc cho DatePicker
            if (thoiGianGiaHanToiDa != null) {
                datePicker.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        if (date != null && !empty) {
                            LocalDateTime selectedDateTime = LocalDateTime.of(date, LocalTime.MIDNIGHT);
                            if (selectedDateTime.isBefore(thoiGianTraHienTai)) {
                                setDisable(true);
                                setStyle("-fx-background-color: #ffcccc;");
                            } else if (selectedDateTime.isAfter(thoiGianGiaHanToiDa)) {
                                setDisable(true);
                                setStyle("-fx-background-color: #ffcccc;");
                            }
                        }
                    }
                });
            } else {
                // Không có giới hạn, chỉ không cho chọn ngày trước thời gian trả phòng hiện tại
                datePicker.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        if (date != null && !empty) {
                            if (date.isBefore(thoiGianTraHienTai.toLocalDate())) {
                                setDisable(true);
                                setStyle("-fx-background-color: #ffcccc;");
                            }
                        }
                    }
                });
            }
            
            Label lblGiaTienThem = new Label("Giá tiền ở thêm: -");
            lblGiaTienThem.setStyle("-fx-font-size: 12px;");
            
            Label lblThongBaoLoi = new Label();
            lblThongBaoLoi.setStyle("-fx-font-size: 11px; -fx-text-fill: #ef4444;");
            lblThongBaoLoi.setWrapText(true);
            lblThongBaoLoi.setVisible(false);
            lblThongBaoLoi.setManaged(false);
            
            // Cập nhật giá tiền và validation khi thay đổi ngày/giờ
            Runnable capNhatGiaTienVaValidation = () -> {
                try {
                    if (datePicker.getValue() == null || cboGio.getValue() == null) {
                        lblThongBaoLoi.setVisible(false);
                        lblThongBaoLoi.setManaged(false);
                        lblGiaTienThem.setText("Giá tiền ở thêm: -");
                        card.setUserData(new ExtensionInfo(ctpdp, null, 0, false));
                        kiemTraTatCaCardHopLe();
                        return;
                    }
                    
                    LocalDateTime thoiGianGiaHanMoi = LocalDateTime.of(
                        datePicker.getValue(),
                        LocalTime.parse(cboGio.getValue())
                    );
                    
                    boolean hopLe = true;
                    String thongBaoLoi = "";
                    
                    // Kiểm tra thời gian gia hạn phải sau thời gian trả hiện tại
                    if (thoiGianGiaHanMoi.isBefore(thoiGianTraHienTai) || 
                        thoiGianGiaHanMoi.isEqual(thoiGianTraHienTai)) {
                        hopLe = false;
                        thongBaoLoi = "Thời gian gia hạn phải sau thời gian trả phòng hiện tại (" + 
                            formatThoiGian(thoiGianTraHienTai) + ")";
                    }
                    // Kiểm tra thời gian gia hạn không được vượt quá giới hạn
                    else if (thoiGianGiaHanToiDa != null && thoiGianGiaHanMoi.isAfter(thoiGianGiaHanToiDa)) {
                        hopLe = false;
                        thongBaoLoi = "Thời gian gia hạn vượt quá giới hạn cho phép (tối đa đến " + 
                            formatThoiGian(thoiGianGiaHanToiDa) + ")";
                    }
                    
                    // Hiển thị thông báo lỗi
                    if (!hopLe) {
                        lblThongBaoLoi.setText(thongBaoLoi);
                        lblThongBaoLoi.setVisible(true);
                        lblThongBaoLoi.setManaged(true);
                        lblGiaTienThem.setText("Giá tiền ở thêm: -");
                        card.setUserData(new ExtensionInfo(ctpdp, null, 0, false));
                    } else {
                        lblThongBaoLoi.setVisible(false);
                        lblThongBaoLoi.setManaged(false);
                        
                        Duration thoiGianGiaHan = Duration.between(thoiGianTraHienTai, thoiGianGiaHanMoi);
                        double giaTienThem = tinhGiaTienThem(ctpdp, thoiGianGiaHan);
                        lblGiaTienThem.setText(String.format("Giá tiền ở thêm: %.0f VND", giaTienThem));
                        
                        card.setUserData(new ExtensionInfo(ctpdp, thoiGianGiaHanMoi, giaTienThem, true));
                    }
                    
                    // Kiểm tra tất cả card có hợp lệ không để enable/disable nút gia hạn
                    kiemTraTatCaCardHopLe();
                } catch (Exception e) {
                    System.err.println("Lỗi tính giá tiền: " + e.getMessage());
                    lblThongBaoLoi.setText("Lỗi xảy ra khi tính toán");
                    lblThongBaoLoi.setVisible(true);
                    lblThongBaoLoi.setManaged(true);
                    card.setUserData(new ExtensionInfo(ctpdp, null, 0, false));
                    kiemTraTatCaCardHopLe();
                }
            };
            
            datePicker.setOnAction(e -> capNhatGiaTienVaValidation.run());
            cboGio.setOnAction(e -> capNhatGiaTienVaValidation.run());
            
            HBox buttonBox = new HBox();
            buttonBox.setAlignment(Pos.CENTER_RIGHT);
            Button btnXoa = taoButton("Xóa", 70, 30, "btn-huy");
            btnXoa.setOnAction(e -> {
                xoaPhongKhoiDanhSachDaChon(ctpdp);
                kiemTraTatCaCardHopLe();
            });
            buttonBox.getChildren().add(btnXoa);
            
            card.getChildren().addAll(lblSoPhong, lblThoiGianTra, dateTimeContainer, lblThongBaoLoi, lblGiaTienThem, buttonBox);
            
            // Tính giá tiền và validation ban đầu
            capNhatGiaTienVaValidation.run();
            
            return card;
        } catch (Exception e) {
            System.err.println("Lỗi khi tạo card phòng: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Tính giá tiền ở thêm dựa trên thời gian gia hạn
     */
    private double tinhGiaTienThem(ChiTietPhieuDatPhong ctpdp, Duration thoiGianGiaHan) {
        try {
            if (!isValidChiTietPhieuDatPhong(ctpdp) || 
                ctpdp.getPhong().getLoaiPhong() == null) {
                return 0;
            }
            
            double giaPhong = ctpdp.getPhong().getLoaiPhong().getGia();
            if (giaPhong <= 0) {
                return 0;
            }
            
            long tongGio = thoiGianGiaHan.toHours();
            long tongPhut = thoiGianGiaHan.toMinutes();
            
            double giaTheoGio = giaPhong / 24.0;
            double giaTheoPhut = giaTheoGio / 60.0;
            
            double giaTienThem = (tongGio * giaTheoGio) + ((tongPhut % 60) * giaTheoPhut);
            
            return giaTienThem;
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Thực hiện gia hạn ngay cho tất cả phòng đã chọn
     */
    private void thucHienGiaHanNgay() {
        try {
            if (controller == null) {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi hệ thống", 
                    "Controller chưa được khởi tạo.");
                return;
            }
            
            if (danhSachPhongDaChon == null || danhSachPhongDaChon.isEmpty()) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", 
                    "Không có phòng nào để gia hạn.");
                return;
            }
            
            int soPhongThanhCong = 0;
            int soPhongThatBai = 0;
            
            for (javafx.scene.Node node : scrollableContentPhongDaChon.getChildren()) {
                try {
                    if (!(node instanceof VBox)) {
                        continue;
                    }
                    
                    VBox card = (VBox) node;
                    Object userData = card.getUserData();
                    
                    if (!(userData instanceof ExtensionInfo)) {
                        soPhongThatBai++;
                        continue;
                    }
            
                    ExtensionInfo info = (ExtensionInfo) userData;
                    
                    // Kiểm tra card có hợp lệ không
                    if (!info.hopLe || info.thoiGianGiaHanMoi == null) {
                        soPhongThatBai++;
                        continue;
                    }
                    
                    ChiTietPhieuDatPhong ctpdp = info.ctpdp;
                    LocalDateTime thoiGianGiaHanMoi = info.thoiGianGiaHanMoi;
                    
                    if (!isValidChiTietPhieuDatPhong(ctpdp) || 
                        ctpdp.getPhieuDatPhong() == null ||
                        ctpdp.getPhieuDatPhong().getMaPhieuDatPhong() == null) {
                soPhongThatBai++;
                continue;
            }
            
                boolean thanhCong = controller.giaHanDen(
                    ctpdp.getPhieuDatPhong().getMaPhieuDatPhong(),
                    ctpdp.getPhong().getMaPhong(),
                        thoiGianGiaHanMoi
                );
                
                if (thanhCong) {
                    soPhongThanhCong++;
                } else {
                    soPhongThatBai++;
                }
            } catch (Exception e) {
                soPhongThatBai++;
                    System.err.println("Lỗi khi gia hạn phòng: " + e.getMessage());
                }
            }
            
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
        
        if (soPhongThanhCong > 0) {
            // Xóa danh sách phòng đã chọn
            danhSachPhongDaChon.clear();
                capNhatHienThiPhongDaChon();
                
                // Load lại dữ liệu từ database sau khi gia hạn thành công
                Platform.runLater(() -> {
                    try {
                        if (txtTimKiem != null && !txtTimKiem.getText().trim().isEmpty()) {
                            // Nếu đang tìm kiếm, load lại theo số điện thoại
                            thucHienTimKiem();
                        } else {
                            // Nếu không tìm kiếm, load lại tất cả phòng đang ở
                            tuDongLoadDanhSachPhong();
        }
        } catch (Exception ex) {
                        System.err.println("Lỗi khi load lại dữ liệu sau gia hạn: " + ex.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Đã xảy ra lỗi khi thực hiện gia hạn.");
            e.printStackTrace();
        }
    }
    
    /**
     * Hiển thị modal chi tiết phòng
     */
    private void hienThiModalChiTietPhong(ChiTietPhieuDatPhong ctpdp) {
        try {
            if (!isValidChiTietPhieuDatPhong(ctpdp)) {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                    "Thông tin phòng không hợp lệ.");
                return;
            }
            
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Chi tiết phòng");
            
            VBox content = new VBox(15);
            content.setPadding(new Insets(20));
            
            Label lblTieuDe = taoLabelTieuDe("THÔNG TIN PHÒNG", 16);
            
            VBox infoBox = new VBox(10);
            infoBox.getStyleClass().add("info-box");
            
            Label lblSoPhong = taoLabel("Số phòng: " + safeString(ctpdp.getPhong().getSoPhong()), 14, false);
            Label lblLoaiPhong = taoLabel("Loại phòng: " + safeString(ctpdp.getPhong().getLoaiPhong().getTenLoaiPhong()), 14, false);
            Label lblTang = taoLabel("Tầng: " + ctpdp.getPhong().getTang(), 14, false);
            Label lblGia = taoLabel("Giá: " + String.format("%.0f VND", ctpdp.getPhong().getLoaiPhong().getGia()), 14, false);
            Label lblThoiGianNhan = taoLabel("Thời gian nhận: " + formatThoiGian(ctpdp.getThoiGianNhanPhong()), 14, false);
            Label lblThoiGianTra = taoLabel("Thời gian trả: " + formatThoiGian(ctpdp.getThoiGianTraPhong()), 14, false);
            Label lblSoNguoi = taoLabel("Số người: " + Math.max(0, ctpdp.getSoNguoi()), 14, false);
            
            infoBox.getChildren().addAll(lblSoPhong, lblLoaiPhong, lblTang, lblGia, 
                lblThoiGianNhan, lblThoiGianTra, lblSoNguoi);
            
            content.getChildren().addAll(lblTieuDe, infoBox);
            
            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.CLOSE);
            dialog.showAndWait();
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Không thể hiển thị chi tiết phòng.");
            e.printStackTrace();
        }
    }
    
    /**
     * Hiển thị modal chi tiết khách hàng
     */
    private void hienThiModalChiTietKhachHang(KhachHang khachHang) {
        try {
            if (khachHang == null || controller == null) {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                    "Thông tin khách hàng không hợp lệ.");
                return;
            }
            
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Chi tiết khách hàng");
            
            VBox content = new VBox(15);
            content.setPadding(new Insets(20));
            
            Label lblThongTinKhachHang = taoLabelTieuDe("THÔNG TIN KHÁCH HÀNG", 16);
            
            VBox infoBox = new VBox(10);
            infoBox.getStyleClass().add("info-box");
            
            Label lblTen = taoLabel("Họ tên: " + safeString(khachHang.getTenKhachHang()), 14, false);
            Label lblCCCD = taoLabel("CCCD: " + safeString(khachHang.getCCCD()), 14, false);
            Label lblSDT = taoLabel("Số điện thoại: " + safeString(khachHang.getSoDienThoai()), 14, false);
            Label lblEmail = taoLabel("Email: " + safeString(khachHang.getEmail()), 14, false);
            
            infoBox.getChildren().addAll(lblTen, lblCCCD, lblSDT, lblEmail);
            
            content.getChildren().addAll(lblThongTinKhachHang, infoBox);
            
            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.CLOSE);
            dialog.showAndWait();
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Không thể hiển thị chi tiết khách hàng.");
            e.printStackTrace();
        }
    }
    
    // Helper methods
    private Label taoLabelTieuDe(String text, int fontSize) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: " + fontSize + "px; -fx-font-weight: bold;");
        return label;
    }
    
    private Label taoLabel(String text, int fontSize, boolean bold) {
        Label label = new Label(text);
        String style = "-fx-font-size: " + fontSize + "px;";
        if (bold) {
            style += " -fx-font-weight: bold;";
        }
        if (fontSize == 12) {
            style += " -fx-text-fill: #64748b;";
        } else if (fontSize == 14 && bold) {
            style += " -fx-text-fill: #1e293b;";
        } else if (fontSize == 16 && bold) {
            style += " -fx-text-fill: #1e293b;";
        }
        label.setStyle(style);
        return label;
    }
    
    private Label taoLabelEmpty(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #9ca3af; -fx-padding: 20;");
        label.setAlignment(Pos.CENTER);
        return label;
    }
    
    private TextField taoTextField(String prompt, double width, double height) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefWidth(width);
        field.setPrefHeight(height);
        field.setAlignment(Pos.CENTER_LEFT);
        return field;
    }
    
    private Button taoButton(String text, double width, double height, String cssClass) {
        Button btn = new Button(text);
        btn.setPrefWidth(width);
        btn.setPrefHeight(height);
        if (cssClass != null && !cssClass.isEmpty()) {
            btn.getStyleClass().add(cssClass);
        }
        return btn;
    }
    
    private Button taoButtonConfirm(String text, double width, double height) {
        Button btn = new Button(text);
        btn.setPrefWidth(width);
        btn.setPrefHeight(height);
        btn.getStyleClass().add("btn-luu");
        return btn;
    }
    
    private ScrollPane taoScrollPane(VBox content, double height) {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPrefHeight(height);
        return scrollPane;
    }
    
    private String formatThoiGian(LocalDateTime thoiGian) {
        if (thoiGian == null) {
            return "-";
        }
        try {
            return thoiGian.format(DATE_TIME_FORMATTER);
        } catch (Exception e) {
            System.err.println("Lỗi format thời gian: " + e.getMessage());
            return "-";
        }
    }
    
    private String safeString(String value) {
        return safeString(value, "-");
    }
    
    private String safeString(String value, String defaultValue) {
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }
    
    private boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches(PHONE_REGEX);
    }
    
    private boolean isValidChiTietPhieuDatPhong(ChiTietPhieuDatPhong ctpdp) {
        return ctpdp != null && ctpdp.getPhong() != null;
    }
    
    /**
     * Áp dụng style cho table header (màu xanh, chữ trắng)
     */
    private void apDungStyleTableHeader(TableView<?> table) {
        Platform.runLater(() -> {
            javafx.scene.Node header = table.lookup(".column-header-background");
            if (header != null) {
                header.setStyle("-fx-background-color: #3b82f6;");
            }
            for (javafx.scene.Node node : table.lookupAll(".column-header")) {
                node.setStyle("-fx-background-color: #3b82f6; -fx-border-color: rgba(255,255,255,0.3); -fx-border-width: 0 0 0 1;");
            }
            for (javafx.scene.Node node : table.lookupAll(".column-header .label")) {
                if (node instanceof Label) {
                    ((Label) node).setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                }
            }
        });
    }
    
    private boolean isPhongTrongDanhSach(ChiTietPhieuDatPhong ctpdp, List<ChiTietPhieuDatPhong> danhSach) {
        if (ctpdp == null || ctpdp.getPhong() == null || ctpdp.getPhong().getMaPhong() == null) {
            return false;
        }
        
        return danhSach.stream()
            .anyMatch(p -> p != null && p.getPhong() != null && p.getPhong().getMaPhong() != null
                && p.getPhong().getMaPhong().equals(ctpdp.getPhong().getMaPhong()));
    }
    
    private void hienThiThongBao(Alert.AlertType loai, String tieuDe, String noiDung) {
        try {
            Alert alert = new Alert(loai);
            alert.setTitle(tieuDe);
            alert.setHeaderText(null);
            alert.setContentText(noiDung != null ? noiDung : "");
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Không thể hiển thị thông báo: " + tieuDe + " - " + noiDung);
            e.printStackTrace();
        }
    }

    // Model classes
    public static class RoomExtensionRow {
        private final String soPhong;
        private final String thoiGianTraPhong;
        private final String coTheGiaHanDen;
        private final ChiTietPhieuDatPhong chiTietPhieuDatPhong;
        private final SimpleBooleanProperty selected = new SimpleBooleanProperty(false);

        public RoomExtensionRow(String soPhong, String thoiGianTraPhong, String coTheGiaHanDen, ChiTietPhieuDatPhong chiTietPhieuDatPhong) {
            this.soPhong = soPhong;
            this.thoiGianTraPhong = thoiGianTraPhong;
            this.coTheGiaHanDen = coTheGiaHanDen;
            this.chiTietPhieuDatPhong = chiTietPhieuDatPhong;
        }

        public String getSoPhong() { return soPhong; }
        public String getThoiGianTraPhong() { return thoiGianTraPhong; }
        public String getCoTheGiaHanDen() { return coTheGiaHanDen; }
        public ChiTietPhieuDatPhong getChiTietPhieuDatPhong() { return chiTietPhieuDatPhong; }
        public SimpleBooleanProperty selectedProperty() { return selected; }
    }
    
    
    private static class ExtensionInfo {
        public final ChiTietPhieuDatPhong ctpdp;
        public final LocalDateTime thoiGianGiaHanMoi;
        public final double giaTienThem;
        public final boolean hopLe;
        
        public ExtensionInfo(ChiTietPhieuDatPhong ctpdp, LocalDateTime thoiGianGiaHanMoi, double giaTienThem, boolean hopLe) {
            this.ctpdp = ctpdp;
            this.thoiGianGiaHanMoi = thoiGianGiaHanMoi;
            this.giaTienThem = giaTienThem;
            this.hopLe = hopLe;
        }
    }
}
