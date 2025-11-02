package view;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import controller.NhanPhong_Controller;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
 * Giao diện nhận phòng (check-in) - Thiết kế mới
 */
public class NhanPhong_GUI extends BorderPane {
    
    // Constants
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String PHONE_REGEX = "^0[0-9]{9,10}$";
    private static final int TIMER_UPDATE_INTERVAL = 60000; // 60 giây

    private TextField txtTimKiem;
    private TableView<RoomCheckInRow> tablePhongChoNhan;
    private VBox containerPhongDaChon;
    private VBox scrollableContentPhongDaChon;
    private NhanPhong_Controller controller;
    private List<ChiTietPhieuDatPhong> danhSachPhongChoNhan;
    private ObservableList<RoomCheckInRow> danhSachPhongChoNhanData;
    private List<ChiTietPhieuDatPhong> danhSachPhongDaChon;
    private HBox containerThongTinKhachHang;
    private KhachHang khachHangHienTai;
    private java.util.Timer timerCapNhatThoiGian;

    public NhanPhong_GUI() {
        try {
            this.controller = new NhanPhong_Controller();
            this.danhSachPhongChoNhan = new ArrayList<>();
            this.danhSachPhongChoNhanData = FXCollections.observableArrayList();
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
            
            // Auto-load tất cả phòng chờ nhận khi vào trang
            Platform.runLater(() -> {
                try {
                    tuDongLoadDanhSachPhong();
                    batDauCapNhatThoiGian();
                } catch (Exception e) {
                    hienThiThongBao(Alert.AlertType.ERROR, "Lỗi khởi tạo", 
                        "Không thể khởi tạo giao diện: " + (e.getMessage() != null ? e.getMessage() : "Lỗi không xác định"));
                }
            });
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi hệ thống", 
                "Không thể khởi tạo giao diện nhận phòng: " + (e.getMessage() != null ? e.getMessage() : "Lỗi không xác định"));
            e.printStackTrace();
        }
    }
    
    /**
     * Tự động load danh sách phòng chờ nhận khi vào trang
     */
    private void tuDongLoadDanhSachPhong() {
        try {
            if (controller == null) {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi hệ thống", 
                    "Controller chưa được khởi tạo. Vui lòng khởi động lại ứng dụng.");
                return;
            }
            
            danhSachPhongChoNhan = controller.layTatCaPhongChoNhan();
            if (danhSachPhongChoNhan == null) {
                danhSachPhongChoNhan = new ArrayList<>();
            }
            capNhatBangPhongChoNhan();
        } catch (NullPointerException e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi dữ liệu", 
                "Dữ liệu không hợp lệ khi tải danh sách phòng.");
            e.printStackTrace();
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Không thể tải danh sách phòng chờ nhận.");
            e.printStackTrace();
        }
    }
    
    /**
     * Bắt đầu timer cập nhật thời gian chờ mỗi phút
     */
    private void batDauCapNhatThoiGian() {
        try {
            if (timerCapNhatThoiGian != null) {
                timerCapNhatThoiGian.cancel();
            }
            timerCapNhatThoiGian = new java.util.Timer();
            timerCapNhatThoiGian.scheduleAtFixedRate(new java.util.TimerTask() {
                @Override
                public void run() {
                    try {
                        Platform.runLater(() -> capNhatThoiGianCho());
                    } catch (Exception e) {
                        System.err.println("Lỗi cập nhật thời gian: " + e.getMessage());
                    }
                }
            }, TIMER_UPDATE_INTERVAL, TIMER_UPDATE_INTERVAL);
        } catch (Exception e) {
            System.err.println("Không thể khởi động timer cập nhật thời gian: " + e.getMessage());
        }
    }
    
    /**
     * Cập nhật cột thời gian chờ trong bảng
     */
    private void capNhatThoiGianCho() {
        try {
            if (tablePhongChoNhan == null || danhSachPhongChoNhanData == null) {
                return;
            }
            
            for (RoomCheckInRow row : danhSachPhongChoNhanData) {
                if (row != null && row.chiTietPhieuDatPhong != null 
                    && row.chiTietPhieuDatPhong.getThoiGianNhanPhong() != null) {
                    String thoiGianCho = tinhThoiGianCho(row.chiTietPhieuDatPhong.getThoiGianNhanPhong());
                    row.setThoiGianCho(thoiGianCho);
                }
            }
            
            // Refresh table
            tablePhongChoNhan.refresh();
        } catch (Exception e) {
            // Không hiển thị lỗi khi cập nhật thời gian để tránh spam
            System.err.println("Lỗi cập nhật thời gian chờ: " + e.getMessage());
        }
    }
    
    /**
     * Tạo vùng trên: Tìm kiếm và thông tin khách hàng
     */
    private VBox taoVungTren() {
        VBox container = new VBox(15);
        
        Label lblTieuDe = taoLabelTieuDe("NHẬN PHÒNG", 20);
        
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
     * Tạo vùng trái: Bảng danh sách phòng chờ nhận
     */
    private VBox taoVungTrai() {
        VBox container = new VBox(10);
        
        // Tiêu đề và button làm mới
        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label lblTieuDe = taoLabelTieuDe("DANH SÁCH PHÒNG CHỜ NHẬN", 16);
        
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

        tablePhongChoNhan = taoBangPhongChoNhan();
        
        VBox tableBox = new VBox(tablePhongChoNhan);
        VBox.setVgrow(tableBox, javafx.scene.layout.Priority.ALWAYS);

        container.getChildren().addAll(headerBox, tableBox);
        return container;
    }

    /**
     * Tạo bảng phòng chờ nhận với các cột: checkbox, số phòng, thời gian nhận phòng, thời gian chờ, nút xem chi tiết
     */
    private TableView<RoomCheckInRow> taoBangPhongChoNhan() {
        tablePhongChoNhan = new TableView<>();
        tablePhongChoNhan.setMaxWidth(Double.MAX_VALUE);
        tablePhongChoNhan.setPrefHeight(450);
        tablePhongChoNhan.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablePhongChoNhan.getStyleClass().add("container-white");
        
        // Cột checkbox
        TableColumn<RoomCheckInRow, Boolean> colCheckbox = new TableColumn<>("");
        colCheckbox.setPrefWidth(50);
        colCheckbox.setCellValueFactory(param -> param.getValue().selectedProperty());
        colCheckbox.setCellFactory(column -> {
            TableCell<RoomCheckInRow, Boolean> cell = new TableCell<RoomCheckInRow, Boolean>() {
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
                                RoomCheckInRow row = getTableView().getItems().get(getIndex());
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
        TableColumn<RoomCheckInRow, String> colSoPhong = new TableColumn<>("Số phòng");
        colSoPhong.setCellValueFactory(new PropertyValueFactory<>("soPhong"));
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

        // Cột thời gian nhận phòng
        TableColumn<RoomCheckInRow, String> colThoiGianNhan = new TableColumn<>("Thời gian nhận phòng");
        colThoiGianNhan.setCellValueFactory(new PropertyValueFactory<>("thoiGianNhanPhong"));
        colThoiGianNhan.setPrefWidth(200);

        // Cột thời gian chờ
        TableColumn<RoomCheckInRow, String> colThoiGianCho = new TableColumn<>("Thời gian chờ");
        colThoiGianCho.setCellValueFactory(new PropertyValueFactory<>("thoiGianCho"));
        colThoiGianCho.setPrefWidth(150);
        colThoiGianCho.setCellFactory(column -> {
            TableCell<RoomCheckInRow, String> cell = new TableCell<RoomCheckInRow, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold; -fx-font-size: 12px;");
                    }
                }
            };
            return cell;
        });

        // Cột nút xem chi tiết
        TableColumn<RoomCheckInRow, Void> colXemChiTiet = new TableColumn<>("Chi tiết");
        colXemChiTiet.setPrefWidth(100);
        colXemChiTiet.setCellFactory(column -> {
            TableCell<RoomCheckInRow, Void> cell = new TableCell<RoomCheckInRow, Void>() {
                private Button btnXemChiTiet = new Button("Xem");
                
                {
                    btnXemChiTiet.getStyleClass().add("btn-small");
                    btnXemChiTiet.setPrefWidth(70);
                    btnXemChiTiet.setPrefHeight(25);
                    btnXemChiTiet.setOnAction(e -> {
                        RoomCheckInRow row = getTableView().getItems().get(getIndex());
                        if (row != null && row.chiTietPhieuDatPhong != null) {
                            hienThiModalChiTietPhong(row.chiTietPhieuDatPhong);
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

        tablePhongChoNhan.getColumns().addAll(colCheckbox, colSoPhong, colThoiGianNhan, colThoiGianCho, colXemChiTiet);
        tablePhongChoNhan.setItems(danhSachPhongChoNhanData);
        
        // Style header
        apDungStyleTableHeader(tablePhongChoNhan);

        return tablePhongChoNhan;
    }

    /**
     * Tạo vùng phải: Hiển thị thông tin khách hàng và phòng đã chọn
     */
    private VBox taoVungPhai() {
        VBox container = new VBox(15);
        container.setPrefWidth(400);
        
        Label lblTieuDe = taoLabelTieuDe("THÔNG TIN NHẬN PHÒNG", 16);

        containerPhongDaChon = new VBox(10);
        containerPhongDaChon.setPrefWidth(400);
        containerPhongDaChon.setPrefHeight(450);
        containerPhongDaChon.getStyleClass().add("container-white");

        scrollableContentPhongDaChon = new VBox(10);
        
        ScrollPane scrollPane = taoScrollPane(scrollableContentPhongDaChon, 380);
        containerPhongDaChon.getChildren().add(scrollPane);
        
        Label lblEmpty = taoLabelEmpty("Chưa có phòng nào được chọn");
        scrollableContentPhongDaChon.getChildren().add(lblEmpty);

        Button btnNhanPhong = taoButtonConfirm("XÁC NHẬN NHẬN PHÒNG", 400, 40);
        btnNhanPhong.setOnAction(e -> thucHienNhanPhong());

        container.getChildren().addAll(lblTieuDe, containerPhongDaChon, btnNhanPhong);
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
                // Nếu rỗng, load lại tất cả phòng chờ nhận
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
            
            // Hiển thị thông tin khách hàng (dù có phòng chờ nhận hay không)
            hienThiThongTinKhachHang(khachHangHienTai);
            
            // Lấy danh sách phòng chờ nhận theo số điện thoại
            danhSachPhongChoNhan = controller.layPhongChoNhanTheoSoDienThoai(soDienThoai);
            if (danhSachPhongChoNhan == null) {
                danhSachPhongChoNhan = new ArrayList<>();
            }
            capNhatBangPhongChoNhan();
            
        } catch (NullPointerException e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi dữ liệu", 
                "Dữ liệu không hợp lệ khi tìm kiếm.");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", 
                "Dữ liệu đầu vào không hợp lệ.");
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Đã xảy ra lỗi khi tìm kiếm.");
            e.printStackTrace();
        }
    }
    
    /**
     * Hiển thị thông tin khách hàng bên phải (dù có phòng chờ nhận hay không)
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
     * Cập nhật bảng phòng chờ nhận
     */
    private void capNhatBangPhongChoNhan() {
        try {
            if (tablePhongChoNhan == null || danhSachPhongChoNhanData == null) {
                return;
            }
            
            // Clear danh sách trên JavaFX Application Thread
            Platform.runLater(() -> {
                try {
                    danhSachPhongChoNhanData.clear();
                    
                    if (danhSachPhongChoNhan == null || danhSachPhongChoNhan.isEmpty()) {
                        // Refresh bảng ngay cả khi không có dữ liệu
                        tablePhongChoNhan.refresh();
                        return;
                    }
                    
                    for (ChiTietPhieuDatPhong ctpdp : danhSachPhongChoNhan) {
                        if (!isValidChiTietPhieuDatPhong(ctpdp)) {
                            continue;
                        }
                        
                        try {
                            String soPhong = safeString(ctpdp.getPhong().getSoPhong(), "-");
                            String thoiGianNhan = formatThoiGian(ctpdp.getThoiGianNhanPhong());
                            String thoiGianCho = tinhThoiGianCho(ctpdp.getThoiGianNhanPhong());
                            
                            danhSachPhongChoNhanData.add(new RoomCheckInRow(soPhong, thoiGianNhan, thoiGianCho, ctpdp));
                        } catch (Exception e) {
                            System.err.println("Lỗi khi thêm phòng vào bảng: " + e.getMessage());
                        }
                    }
                    
                    // Refresh bảng sau khi cập nhật
                    tablePhongChoNhan.refresh();
                } catch (Exception e) {
                    hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                        "Không thể cập nhật bảng phòng chờ nhận.");
                    e.printStackTrace();
                }
            });
            
        } catch (NullPointerException e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi dữ liệu", 
                "Dữ liệu không hợp lệ khi cập nhật bảng phòng.");
            e.printStackTrace();
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Không thể cập nhật bảng phòng chờ nhận.");
            e.printStackTrace();
        }
    }
    
    /**
     * Tính thời gian chờ còn lại tính đến thời điểm cuối cùng có thể nhận (thoiGianNhanPhong + 6 giờ)
     * Ví dụ: Phòng có thoiGianNhanPhong = 14:00, hiện tại = 15:00, thì còn 5 giờ đến 20:00
     */
    private String tinhThoiGianCho(LocalDateTime thoiGianNhanPhong) {
        try {
            if (thoiGianNhanPhong == null) {
                return "-";
            }
            
            LocalDateTime thoiGianHienTai = LocalDateTime.now();
            // Thời điểm cuối cùng có thể nhận là thoiGianNhanPhong + 6 giờ
            LocalDateTime thoiGianKetThuc = thoiGianNhanPhong.plusHours(6);
            
            Duration duration = Duration.between(thoiGianHienTai, thoiGianKetThuc);
            long totalMinutes = duration.toMinutes();
            
            if (totalMinutes < 0) {
                // Đã quá thời gian nhận
                long quaPhut = Math.abs(totalMinutes);
                long quaGio = quaPhut / 60;
                long conPhut = quaPhut % 60;
                return String.format("Quá %d giờ %d phút", quaGio, conPhut);
            } else {
                long gio = totalMinutes / 60;
                long phut = totalMinutes % 60;
                return String.format("Còn %d giờ %d phút", gio, phut);
            }
        } catch (Exception e) {
            // Trả về giá trị mặc định nếu có lỗi
            System.err.println("Lỗi tính thời gian chờ: " + e.getMessage());
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
            
            if (ctpdp.getPhong() == null || ctpdp.getPhong().getMaPhong() == null) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", 
                    "Thông tin phòng không đầy đủ.");
                return;
            }
            
            if (danhSachPhongDaChon == null) {
                danhSachPhongDaChon = new ArrayList<>();
            }
            
            // Kiểm tra xem đã có trong danh sách chưa
            boolean daTonTai = danhSachPhongDaChon.stream()
                .anyMatch(p -> p != null && p.getPhong() != null && p.getPhong().getMaPhong() != null 
                    && p.getPhong().getMaPhong().equals(ctpdp.getPhong().getMaPhong()));
            
            if (!daTonTai) {
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
            if (ctpdp == null || ctpdp.getPhong() == null || ctpdp.getPhong().getMaPhong() == null) {
                return;
            }
            
            if (danhSachPhongDaChon == null) {
                return;
            }
            
            danhSachPhongDaChon.removeIf(p -> 
                p != null && p.getPhong() != null && p.getPhong().getMaPhong() != null 
                && p.getPhong().getMaPhong().equals(ctpdp.getPhong().getMaPhong()));
            
            capNhatHienThiPhongDaChon();
            
            // Uncheck trong bảng
            if (tablePhongChoNhan != null && danhSachPhongChoNhanData != null) {
                for (RoomCheckInRow row : danhSachPhongChoNhanData) {
                    if (row != null && row.chiTietPhieuDatPhong != null 
                        && row.chiTietPhieuDatPhong.getPhong() != null
                        && row.chiTietPhieuDatPhong.getPhong().getMaPhong() != null
                        && row.chiTietPhieuDatPhong.getPhong().getMaPhong().equals(ctpdp.getPhong().getMaPhong())) {
                        row.setSelected(false);
                    }
                }
            }
        } catch (Exception e) {
            // Không hiển thị lỗi khi xóa để tránh làm gián đoạn người dùng
            System.err.println("Lỗi khi xóa phòng khỏi danh sách: " + e.getMessage());
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
                scrollableContentPhongDaChon.getChildren().add(taoLabelEmpty("Chưa có phòng nào được chọn"));
                return;
            }
            
            for (ChiTietPhieuDatPhong ctpdp : danhSachPhongDaChon) {
                if (!isValidChiTietPhieuDatPhong(ctpdp)) {
                    continue;
                }
                
                try {
                    VBox card = taoCardPhongDaChon(ctpdp);
                    if (card != null) {
                        scrollableContentPhongDaChon.getChildren().add(card);
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi khi tạo card phòng: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Không thể cập nhật hiển thị phòng đã chọn.");
            e.printStackTrace();
        }
    }
    
    /**
     * Tạo card hiển thị phòng đã chọn
     */
    private VBox taoCardPhongDaChon(ChiTietPhieuDatPhong ctpdp) {
        try {
            VBox card = new VBox(10);
            card.getStyleClass().add("card-room");
            card.setPrefWidth(370);
            
            Label lblSoPhong = taoLabel("Phòng: " + safeString(ctpdp.getPhong().getSoPhong(), "-"), 16, true);
            Label lblThoiGian = taoLabel("Nhận: " + formatThoiGian(ctpdp.getThoiGianNhanPhong()), 12, false);
            Label lblSoNguoi = taoLabel("Số người: " + Math.max(0, ctpdp.getSoNguoi()), 12, false);
            
            HBox buttonBox = new HBox();
            buttonBox.setAlignment(Pos.CENTER_RIGHT);
            Button btnXoa = taoButton("Xóa", 70, 30, "btn-huy");
            btnXoa.setOnAction(e -> xoaPhongKhoiDanhSachDaChon(ctpdp));
            buttonBox.getChildren().add(btnXoa);
            
            card.getChildren().addAll(lblSoPhong, lblThoiGian, lblSoNguoi, buttonBox);
            card.setUserData(ctpdp);
            
            return card;
        } catch (Exception e) {
            System.err.println("Lỗi khi tạo card phòng: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Hiển thị modal chi tiết phòng
     */
    private void hienThiModalChiTietPhong(ChiTietPhieuDatPhong ctpdp) {
        try {
            if (ctpdp == null) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", 
                    "Thông tin phòng không hợp lệ.");
                return;
            }
            
            if (ctpdp.getPhieuDatPhong() == null) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", 
                    "Thông tin phiếu đặt phòng không hợp lệ.");
                return;
            }
            
            if (ctpdp.getPhieuDatPhong().getKhachHang() == null) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", 
                    "Thông tin khách hàng không hợp lệ.");
                return;
            }
            
            KhachHang khachHang = ctpdp.getPhieuDatPhong().getKhachHang();
            hienThiModalChiTietKhachHang(khachHang);
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Không thể hiển thị chi tiết phòng.");
            e.printStackTrace();
        }
    }
    
    /**
     * Hiển thị modal chi tiết khách hàng với danh sách phòng đã đặt
     */
    private void hienThiModalChiTietKhachHang(KhachHang khachHang) {
        try {
            if (khachHang == null || khachHang.getSoDienThoai() == null || khachHang.getSoDienThoai().trim().isEmpty()) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", 
                    "Không có thông tin khách hàng để hiển thị.");
                return;
            }
            
            if (controller == null) {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi hệ thống", 
                    "Controller chưa được khởi tạo.");
                return;
            }
            
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Chi tiết khách hàng");
            dialog.setWidth(800);
            dialog.setHeight(600);
            
            VBox content = new VBox(20);
            content.setPadding(new Insets(20));
            
            Label lblThongTinKhachHang = taoLabelTieuDe("THÔNG TIN KHÁCH HÀNG", 16);
            
            VBox infoBox = new VBox(10);
            infoBox.getStyleClass().add("info-box");
            
            Label lblTen = taoLabel("Họ tên: " + safeString(khachHang.getTenKhachHang()), 14, false);
            Label lblCCCD = taoLabel("CCCD: " + safeString(khachHang.getCCCD()), 14, false);
            Label lblSDT = taoLabel("Số điện thoại: " + safeString(khachHang.getSoDienThoai()), 14, false);
            Label lblEmail = taoLabel("Email: " + safeString(khachHang.getEmail()), 14, false);
            
            infoBox.getChildren().addAll(lblTen, lblCCCD, lblSDT, lblEmail);
            
            Label lblDanhSachPhong = taoLabelTieuDe("DANH SÁCH PHÒNG ĐÃ ĐẶT", 16);
            
            // Lấy tất cả phòng đã đặt
            List<ChiTietPhieuDatPhong> dsPhongDaDat = null;
            try {
                dsPhongDaDat = controller.layTatCaPhongDaDatTheoSoDienThoai(khachHang.getSoDienThoai());
            } catch (Exception e) {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                    "Không thể lấy danh sách phòng đã đặt.");
                e.printStackTrace();
                return;
            }
            
            if (dsPhongDaDat == null) {
                dsPhongDaDat = new ArrayList<>();
            }
            
            TableView<RoomDetailRow> tablePhongDaDat = new TableView<>();
            tablePhongDaDat.setPrefHeight(300);
            
            TableColumn<RoomDetailRow, String> colSoPhong = new TableColumn<>("Số phòng");
            colSoPhong.setCellValueFactory(new PropertyValueFactory<>("soPhong"));
            colSoPhong.setPrefWidth(100);
            
            TableColumn<RoomDetailRow, String> colThoiGianNhan = new TableColumn<>("Thời gian nhận");
            colThoiGianNhan.setCellValueFactory(new PropertyValueFactory<>("thoiGianNhan"));
            colThoiGianNhan.setPrefWidth(150);
            
            TableColumn<RoomDetailRow, String> colThoiGianTra = new TableColumn<>("Thời gian trả");
            colThoiGianTra.setCellValueFactory(new PropertyValueFactory<>("thoiGianTra"));
            colThoiGianTra.setPrefWidth(150);
            
            TableColumn<RoomDetailRow, String> colTrangThai = new TableColumn<>("Trạng thái nhận");
            colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThaiNhan"));
            colTrangThai.setPrefWidth(150);
            colTrangThai.setCellFactory(column -> {
                TableCell<RoomDetailRow, String> cell = new TableCell<RoomDetailRow, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item);
                            if (item.contains("Sẵn sàng")) {
                                setStyle("-fx-text-fill: #22c55e; -fx-font-weight: bold;");
                            } else if (item.contains("Chưa đến")) {
                                setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                            } else if (item.contains("Quá")) {
                                setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                            } else {
                                setStyle("-fx-text-fill: #64748b;");
                            }
                        }
                    }
                };
                return cell;
            });
            
            tablePhongDaDat.getColumns().addAll(colSoPhong, colThoiGianNhan, colThoiGianTra, colTrangThai);
            
            ObservableList<RoomDetailRow> data = FXCollections.observableArrayList();
            LocalDateTime thoiGianHienTai = LocalDateTime.now();
            
            for (ChiTietPhieuDatPhong ctpdp : dsPhongDaDat) {
                if (!isValidChiTietPhieuDatPhong(ctpdp)) {
                    continue;
                }
                
                try {
                    String soPhong = safeString(ctpdp.getPhong().getSoPhong(), "-");
                    String thoiGianNhan = formatThoiGian(ctpdp.getThoiGianNhanPhong());
                    String thoiGianTra = formatThoiGian(ctpdp.getThoiGianTraPhong());
                    String trangThaiNhan = xacDinhTrangThaiNhan(ctpdp.getThoiGianNhanPhong(), thoiGianHienTai);
                    
                    data.add(new RoomDetailRow(soPhong, thoiGianNhan, thoiGianTra, trangThaiNhan));
                } catch (Exception e) {
                    System.err.println("Lỗi khi thêm phòng vào bảng: " + e.getMessage());
                }
            }
            
            tablePhongDaDat.setItems(data);
            
            content.getChildren().addAll(lblThongTinKhachHang, infoBox, lblDanhSachPhong, tablePhongDaDat);
            
            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.CLOSE);
            dialog.showAndWait();
        } catch (NullPointerException e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi dữ liệu", 
                "Dữ liệu không hợp lệ khi hiển thị chi tiết khách hàng.");
            e.printStackTrace();
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Không thể hiển thị chi tiết khách hàng.");
            e.printStackTrace();
        }
    }
    
    /**
     * Xác định trạng thái nhận phòng: "Sẵn sàng nhận", "Chưa đến thời gian nhận", "Quá thời gian nhận"
     * Trạng thái "Sẵn sàng nhận" khi: (thoiGianNhanPhong - 1 giờ) <= thời điểm hiện tại <= (thoiGianNhanPhong + 6 giờ)
     * Ví dụ: Phòng có thoiGianNhanPhong = 14:00 thì được nhận trong khoảng 13:00-20:00
     */
    private String xacDinhTrangThaiNhan(LocalDateTime thoiGianNhanPhong, LocalDateTime thoiGianHienTai) {
        try {
            if (thoiGianNhanPhong == null || thoiGianHienTai == null) {
                return "-";
            }
            
            // Khoảng thời gian cho phép nhận được tính từ thoiGianNhanPhong
            LocalDateTime thoiGianBatDau = thoiGianNhanPhong.minusHours(1); // Sớm hơn 1 giờ so với thoiGianNhanPhong
            LocalDateTime thoiGianKetThuc = thoiGianNhanPhong.plusHours(6); // Trễ hơn 6 giờ so với thoiGianNhanPhong
            
            // Kiểm tra xem thời điểm hiện tại có nằm trong khoảng cho phép nhận không
            if (thoiGianHienTai.isBefore(thoiGianBatDau)) {
                return "Chưa đến thời gian nhận";
            } else if (thoiGianHienTai.isAfter(thoiGianKetThuc)) {
                return "Quá thời gian nhận";
            } else {
                return "Sẵn sàng nhận";
            }
        } catch (Exception e) {
            // Trả về giá trị mặc định nếu có lỗi
            System.err.println("Lỗi xác định trạng thái nhận phòng: " + e.getMessage());
            return "-";
        }
    }
    
    /**
     * Thực hiện nhận phòng
     */
    private void thucHienNhanPhong() {
        try {
            if (controller == null) {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi hệ thống", 
                    "Controller chưa được khởi tạo. Vui lòng khởi động lại ứng dụng.");
                return;
            }
            
            if (danhSachPhongDaChon == null || danhSachPhongDaChon.isEmpty()) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", 
                    "Không có phòng nào để nhận.");
                return;
            }
            
            // Validate dữ liệu phòng trước khi nhận
            int soPhongHopLe = 0;
            int soPhongKhongHopLe = 0;
            
            for (ChiTietPhieuDatPhong ctpdp : danhSachPhongDaChon) {
                if (ctpdp == null || ctpdp.getPhieuDatPhong() == null 
                    || ctpdp.getPhieuDatPhong().getMaPhieuDatPhong() == null
                    || ctpdp.getPhong() == null || ctpdp.getPhong().getMaPhong() == null) {
                    soPhongKhongHopLe++;
                } else {
                    soPhongHopLe++;
                }
            }
            
            if (soPhongHopLe == 0) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", 
                    "Không có phòng nào hợp lệ để nhận.");
                return;
            }
            
            if (soPhongKhongHopLe > 0) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", 
                    "Có " + soPhongKhongHopLe + " phòng có dữ liệu không hợp lệ. Chỉ nhận " + soPhongHopLe + " phòng hợp lệ.");
            }
            
            // Nhận tất cả phòng đã chọn
            boolean thanhCong = false;
            try {
                thanhCong = controller.nhanNhieuPhong(danhSachPhongDaChon);
            } catch (Exception e) {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                    "Không thể thực hiện nhận phòng. Vui lòng thử lại.");
                e.printStackTrace();
                return;
            }
            
            if (thanhCong) {
                hienThiThongBao(Alert.AlertType.INFORMATION, "Thành công", 
                    "Nhận phòng thành công cho " + soPhongHopLe + " phòng!");
                
                // Xóa dữ liệu hiển thị
                danhSachPhongDaChon.clear();
                capNhatHienThiPhongDaChon();
                
                // Reload danh sách phòng chờ nhận
                try {
                    if (txtTimKiem != null && !txtTimKiem.getText().trim().isEmpty()) {
                        thucHienTimKiem();
                    } else {
                        tuDongLoadDanhSachPhong();
                    }
                } catch (Exception e) {
                    // Nếu reload lỗi, chỉ in log, không hiển thị lỗi cho người dùng vì đã nhận phòng thành công
                    System.err.println("Lỗi reload danh sách phòng: " + e.getMessage());
                }
                
                // Uncheck tất cả checkbox
                if (danhSachPhongChoNhanData != null) {
                    for (RoomCheckInRow row : danhSachPhongChoNhanData) {
                        if (row != null) {
                            row.setSelected(false);
                        }
                    }
                }
            } else {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                    "Nhận phòng thất bại. Vui lòng kiểm tra lại và thử lại.");
            }
        } catch (NullPointerException e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi dữ liệu", 
                "Dữ liệu không hợp lệ khi nhận phòng.");
            e.printStackTrace();
        } catch (Exception e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", 
                "Đã xảy ra lỗi khi nhận phòng.");
            e.printStackTrace();
        }
    }
    
    /**
     * Hiển thị thông báo Alert
     */
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
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Tạo Label tiêu đề
     */
    private Label taoLabelTieuDe(String text, int fontSize) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: " + fontSize + "px; -fx-font-weight: bold;");
        return label;
    }
    
    /**
     * Tạo Label với style
     */
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
    
    /**
     * Tạo Label empty state
     */
    private Label taoLabelEmpty(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #9ca3af; -fx-padding: 20;");
        label.setAlignment(Pos.CENTER);
        return label;
    }
    
    /**
     * Tạo TextField
     */
    private TextField taoTextField(String prompt, double width, double height) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefWidth(width);
        field.setPrefHeight(height);
        field.setAlignment(Pos.CENTER_LEFT);
        return field;
    }
    
    /**
     * Tạo Button với CSS class
     */
    private Button taoButton(String text, double width, double height, String cssClass) {
        Button btn = new Button(text);
        btn.setPrefWidth(width);
        btn.setPrefHeight(height);
        if (cssClass != null && !cssClass.isEmpty()) {
            btn.getStyleClass().add(cssClass);
        }
        return btn;
    }
    
    /**
     * Tạo Button xác nhận (xanh lá)
     */
    private Button taoButtonConfirm(String text, double width, double height) {
        Button btn = new Button(text);
        btn.setPrefWidth(width);
        btn.setPrefHeight(height);
        btn.getStyleClass().add("btn-luu");
        return btn;
    }
    
    /**
     * Tạo ScrollPane
     */
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
    
    /**
     * Format thời gian an toàn
     */
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
    
    /**
     * Safe string - trả về giá trị hoặc "-"
     */
    private String safeString(String value) {
        return safeString(value, "-");
    }
    
    /**
     * Safe string - trả về giá trị hoặc defaultValue
     */
    private String safeString(String value, String defaultValue) {
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }
    
    /**
     * Validate số điện thoại
     */
    private boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches(PHONE_REGEX);
    }
    
    /**
     * Validate ChiTietPhieuDatPhong
     */
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

    /**
     * Model cho dữ liệu bảng phòng chờ nhận
     */
    public static class RoomCheckInRow {
        private final String soPhong;
        private final String thoiGianNhanPhong;
        private String thoiGianCho;
        private final ChiTietPhieuDatPhong chiTietPhieuDatPhong;
        private final javafx.beans.property.SimpleBooleanProperty selected = new javafx.beans.property.SimpleBooleanProperty(false);

        public RoomCheckInRow(String soPhong, String thoiGianNhanPhong, String thoiGianCho, ChiTietPhieuDatPhong chiTietPhieuDatPhong) {
            this.soPhong = soPhong;
            this.thoiGianNhanPhong = thoiGianNhanPhong;
            this.thoiGianCho = thoiGianCho;
            this.chiTietPhieuDatPhong = chiTietPhieuDatPhong;
        }

        public String getSoPhong() { return soPhong; }
        public String getThoiGianNhanPhong() { return thoiGianNhanPhong; }
        public String getThoiGianCho() { return thoiGianCho; }
        public void setThoiGianCho(String thoiGianCho) { this.thoiGianCho = thoiGianCho; }
        public javafx.beans.property.BooleanProperty selectedProperty() { return selected; }
        public boolean isSelected() { return selected.get(); }
        public void setSelected(boolean selected) { this.selected.set(selected); }
    }
    
    /**
     * Model cho dữ liệu bảng phòng đã đặt trong modal
     */
    public static class RoomDetailRow {
        private final String soPhong;
        private final String thoiGianNhan;
        private final String thoiGianTra;
        private final String trangThaiNhan;

        public RoomDetailRow(String soPhong, String thoiGianNhan, String thoiGianTra, String trangThaiNhan) {
            this.soPhong = soPhong;
            this.thoiGianNhan = thoiGianNhan;
            this.thoiGianTra = thoiGianTra;
            this.trangThaiNhan = trangThaiNhan;
        }

        public String getSoPhong() { return soPhong; }
        public String getThoiGianNhan() { return thoiGianNhan; }
        public String getThoiGianTra() { return thoiGianTra; }
        public String getTrangThaiNhan() { return trangThaiNhan; }
    }
}
