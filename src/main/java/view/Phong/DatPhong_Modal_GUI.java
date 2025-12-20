package view.Phong;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Spinner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Phong;
import model.DichVu;
import model.ChiTietPhieuDatPhong;
import model.KhachHang;
import model.PhieuDatPhong;
import model.LoaiDatPhong;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

import controller.DichVu_Controller;
import controller.KhachHang_Controller;
import controller.PhieuDatPhong_Controller;
import view.CaLamViec_GUI;

/**
 * Modal đặt phòng với footer cố định chứa nút Hủy / Xác nhận
 */
public class DatPhong_Modal_GUI extends BorderPane {

    // ======== State ========
    private TableView<PhongDatModel> roomTable;
    private final ObservableList<PhongDatModel> roomData = FXCollections.observableArrayList();

    private final List<ChiTietPhieuDatPhong> chiTietPhieuDatPhongList;
    private final Runnable onSuccessCallback;

    // fields để autofill
    private TextField cccdField;
    private TextField hoTenField;
    private TextField sdtField;
    private TextField emailField;
    
    // Ca làm việc
    private CaLamViec_GUI caLamViecGUI;

    // ======== Ctor ========
    /**
     * @param chiTietPhieuDatPhongList danh sách chi tiết đã chọn từ trang Đặt phòng
     * @param onSuccessCallback callback khi đặt phòng thành công
     */
    public DatPhong_Modal_GUI(List<ChiTietPhieuDatPhong> chiTietPhieuDatPhongList, Runnable onSuccessCallback) {
        this.chiTietPhieuDatPhongList = chiTietPhieuDatPhongList;
        this.onSuccessCallback = onSuccessCallback;
        init();
    }
    
    public DatPhong_Modal_GUI(List<ChiTietPhieuDatPhong> chiTietPhieuDatPhongList, 
                               CaLamViec_GUI caLamViecGUI, 
                               Runnable onSuccessCallback) {
        this.chiTietPhieuDatPhongList = chiTietPhieuDatPhongList;
        this.caLamViecGUI = caLamViecGUI;
        this.onSuccessCallback = onSuccessCallback;
        init();
    }

    // ======== Init layout ========
    private void init() {
        VBox mainContainer = buildMainContainer();

        // Bọc nội dung vào ScrollPane để phần content cuộn độc lập với footer
        ScrollPane scroller = new ScrollPane(mainContainer);
        scroller.setFitToWidth(true);
        scroller.setFitToHeight(true);
        scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        setCenter(scroller);
        setBottom(buildFooterBar()); // FOOTER CỐ ĐỊNH

        // load dữ liệu
        loadChiTietPhieuDatPhong();
        javafx.application.Platform.runLater(this::updateSummary);
    }

    /** Phần nội dung chính (không có nút ở đây nữa) */
    private VBox buildMainContainer() {
        VBox main = new VBox(20);
        main.setPadding(new Insets(30));
        main.setStyle("-fx-background-color: #F5F7FA;");

        Label mainTitle = new Label("THÔNG TIN ĐẶT PHÒNG");
        mainTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        mainTitle.setStyle("-fx-text-fill: #2C3E50;");

        VBox userInfo = createUserInfo();
        VBox roomInfo = createRoomInfoTable();

        main.getChildren().addAll(mainTitle, userInfo, roomInfo);
        return main;
    }

    /** Footer cố định chứa nút Hủy / Xác nhận */
    private HBox buildFooterBar() {
        HBox footer = new HBox(12);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(10, 16, 10, 16));
        footer.setStyle(
                "-fx-background-color: linear-gradient(to top, #ffffff, #f7f9fb);" +
                        "-fx-border-color: #E1E6EC transparent transparent transparent;" +
                        "-fx-border-width: 1 0 0 0;"
        );

        Button cancelBtn = styledButton("Hủy", "#95a5a6", "#7f8c8d", 90, 32);
        cancelBtn.setOnAction(e -> ((javafx.stage.Stage) getScene().getWindow()).close());

        Button confirmBtn = styledButton("Xác nhận đặt phòng", "#3498db", "#2980b9", 150, 32);
        confirmBtn.setOnAction(e -> xacNhanDatPhong());

        footer.getChildren().addAll(cancelBtn, confirmBtn);
        return footer;
    }

    /** Helper tạo button styled */
    private Button styledButton(String text, String color, String hover, double w, double h) {
        Button b = new Button(text);
        b.setPrefSize(w, h);
        b.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-radius: 6;"
        );
        b.setOnMouseEntered(e -> b.setStyle(
                "-fx-background-color: " + hover + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-radius: 6;"
        ));
        b.setOnMouseExited(e -> b.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-radius: 6;"
        ));
        return b;
    }

    // =========================================================
    // ============ DỮ LIỆU & THÀNH PHẦN HIỂN THỊ =============
    // =========================================================

    /** Load list chi tiết vào bảng */
    private void loadChiTietPhieuDatPhong() {
        if (chiTietPhieuDatPhongList != null && !chiTietPhieuDatPhongList.isEmpty()) {
            for (ChiTietPhieuDatPhong ct : chiTietPhieuDatPhongList) {
                addChiTietPhieuDatPhong(ct);
            }
        }
    }

    /** Bảng phòng + tổng tiền */
    private VBox createRoomInfoTable() {
        VBox container = new VBox(15);
        container.setStyle(
                "-fx-padding: 25;" +
                        "-fx-background-color: white;" +
                        "-fx-border-color: #D5DBDB;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );

        Label header = new Label("DANH SÁCH PHÒNG ĐÃ CHỌN");
        header.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        header.setStyle("-fx-text-fill: #34495E;");

        roomTable = new TableView<>();
        roomTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        roomTable.setStyle("-fx-background-color: white;-fx-font-size:11px;");
        VBox.setVgrow(roomTable, javafx.scene.layout.Priority.ALWAYS);

        TableColumn<PhongDatModel, String> soPhongCol = new TableColumn<>("Số Phòng");
        soPhongCol.setCellValueFactory(new PropertyValueFactory<>("soPhong"));
        soPhongCol.setPrefWidth(77);
        soPhongCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<PhongDatModel, String> loaiPhongCol = new TableColumn<>("Loại Phòng");
        loaiPhongCol.setCellValueFactory(new PropertyValueFactory<>("loaiPhong"));
        loaiPhongCol.setPrefWidth(94);
        loaiPhongCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<PhongDatModel, String> checkInCol = new TableColumn<>("Check-in");
        checkInCol.setCellValueFactory(new PropertyValueFactory<>("checkIn"));
        checkInCol.setPrefWidth(111);
        checkInCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<PhongDatModel, String> checkOutCol = new TableColumn<>("Check-out");
        checkOutCol.setCellValueFactory(new PropertyValueFactory<>("checkOut"));
        checkOutCol.setPrefWidth(111);
        checkOutCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<PhongDatModel, String> soGioCol = new TableColumn<>("Số giờ");
        soGioCol.setCellValueFactory(new PropertyValueFactory<>("soGioLuuTru"));
        soGioCol.setPrefWidth(60);
        soGioCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<PhongDatModel, String> dichVuCol = new TableColumn<>("Dịch vụ");
        dichVuCol.setCellValueFactory(new PropertyValueFactory<>("dichVu"));
        dichVuCol.setPrefWidth(187);
        dichVuCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); }
                else {
                    Label l = new Label(item);
                    l.setWrapText(true);
                    l.setMaxWidth(210);
                    setGraphic(l);
                }
            }
        });

        TableColumn<PhongDatModel, String> tongTienCol = new TableColumn<>("Tổng tiền");
        tongTienCol.setCellValueFactory(new PropertyValueFactory<>("tongTien"));
        tongTienCol.setPrefWidth(102);
        tongTienCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    setStyle("-fx-alignment: CENTER; -fx-text-fill:#E74C3C; -fx-font-weight:bold;");
                }
            }
        });

        TableColumn<PhongDatModel, Void> actionCol = new TableColumn<>("Chọn DV");
        actionCol.setPrefWidth(77);
        actionCol.setCellFactory(col -> new TableCell<>() {
            final Button btn = styledButton("Chọn DV", "#4CAF50", "#45a049", 78, 28);
            { btn.setOnAction(e -> {
                PhongDatModel m = getTableView().getItems().get(getIndex());
                openChonDichVuDialog(m);
            });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : btn);
                setAlignment(Pos.CENTER);
            }
        });

        roomTable.getColumns().addAll(
                soPhongCol, loaiPhongCol, checkInCol, checkOutCol, soGioCol, dichVuCol, tongTienCol, actionCol
        );
        roomTable.setItems(roomData);

        HBox summaryBox = createSummaryBox();

        container.getChildren().addAll(header, roomTable, summaryBox);
        return container;
    }

    /** Box tổng kết tiền */
    private HBox createSummaryBox() {
        HBox summaryBox = new HBox(30);
        summaryBox.setAlignment(Pos.CENTER_RIGHT);
        summaryBox.setPadding(new Insets(15, 0, 0, 0));
        summaryBox.setStyle("-fx-background-color:#F8F9FA;-fx-padding:15;-fx-background-radius:6;");

        VBox content = new VBox(8);
        content.setAlignment(Pos.CENTER_RIGHT);

        HBox tong = new HBox(10);
        tong.setAlignment(Pos.CENTER_RIGHT);
        Label tongLbl = new Label("Tổng tiền:");
        tongLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        tongLbl.setStyle("-fx-text-fill:#2C3E50;");
        Label tongVal = new Label("0 VNĐ"); tongVal.setId("tongTienValue");
        tongVal.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        tongVal.setStyle("-fx-text-fill:#E74C3C;");
        tong.getChildren().addAll(tongLbl, tongVal);

        HBox coc = new HBox(10);
        coc.setAlignment(Pos.CENTER_RIGHT);
        Label cocLbl = new Label("Tiền cọc (30%):");
        cocLbl.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        cocLbl.setStyle("-fx-text-fill:#34495E;");
        Label cocVal = new Label("0 VNĐ"); cocVal.setId("tienCocValue");
        cocVal.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        cocVal.setStyle("-fx-text-fill:#27AE60;");
        coc.getChildren().addAll(cocLbl, cocVal);

        content.getChildren().addAll(tong, coc);
        summaryBox.getChildren().add(content);

        roomData.addListener((javafx.collections.ListChangeListener.Change<? extends PhongDatModel> c) -> updateSummary());
        return summaryBox;
    }

    /** Cập nhật tổng & cọc */
    private void updateSummary() {
        double tong = 0;
        for (PhongDatModel m : roomData) {
            String s = m.getTongTien().replace(" VNĐ", "").replace(",", "").replace(".", "");
            try { tong += Double.parseDouble(s); } catch (NumberFormatException ignore) {}
        }
        double coc = tong * 0.3;

        Label tongLbl = (Label) lookup("#tongTienValue");
        Label cocLbl  = (Label) lookup("#tienCocValue");
        if (tongLbl != null) tongLbl.setText(String.format("%,.0f VNĐ", tong));
        if (cocLbl  != null) cocLbl.setText(String.format("%,.0f VNĐ", coc));
    }

    // =========================================================
    // =============== LOGIC + HỖ TRỢ FORM KH ==================
    // =========================================================
    private VBox createUserInfo() {
        VBox box = new VBox(20);
        box.setStyle(
                "-fx-padding: 25;" +
                        "-fx-background-color: white;" +
                        "-fx-border-color: #D5DBDB;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );

        Label header = new Label("THÔNG TIN KHÁCH HÀNG");
        header.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        header.setStyle("-fx-text-fill:#34495E;");

        HBox row = new HBox(40); row.setAlignment(Pos.TOP_LEFT);

        VBox left = new VBox(20); left.setPrefWidth(468);
        VBox cccdBox = createFormField("CCCD:", "Nhập số căn cước công dân", true);
        cccdField = (TextField) cccdBox.getChildren().get(1);
        setupCCCDAutocomplete();

        VBox hoTenBox = createFormField("Họ và tên:", "Nhập họ và tên đầy đủ", true);
        hoTenField = (TextField) hoTenBox.getChildren().get(1);

        VBox sdtBox = createFormField("Số điện thoại:", "Nhập số điện thoại", true);
        sdtField = (TextField) sdtBox.getChildren().get(1);
        left.getChildren().addAll(cccdBox, hoTenBox, sdtBox);

        VBox right = new VBox(20); right.setPrefWidth(468);
        VBox emailBox = createFormField("Email:", "Nhập địa chỉ email", false);
        emailField = (TextField) emailBox.getChildren().get(1);
        VBox diaChiBox = createFormField("Địa chỉ:", "Nhập địa chỉ chi tiết", false);
        VBox ghiChuBox = createFormField("Ghi chú:", "Nhập ghi chú (nếu có)", false);
        right.getChildren().addAll(emailBox, diaChiBox, ghiChuBox);

        row.getChildren().addAll(left, right);
        box.getChildren().addAll(header, row);
        return box;
    }

    private VBox createFormField(String labelText, String promptText, boolean required) {
        VBox fieldBox = new VBox(8);

        HBox labelWrap = new HBox(5); labelWrap.setAlignment(Pos.CENTER_LEFT);
        Label label = new Label(labelText);
        label.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        label.setStyle("-fx-text-fill:#2C3E50;");
        labelWrap.getChildren().add(label);
        if (required) {
            Label req = new Label("*");
            req.setStyle("-fx-text-fill:#E74C3C; -fx-font-size:12px; -fx-font-weight:bold;");
            labelWrap.getChildren().add(req);
        }

        TextField tf = new TextField();
        tf.setPromptText(promptText);
        tf.setPrefHeight(30);
        tf.setStyle("-fx-background-color:#F8F9FA;-fx-border-color:#D5DBDB;-fx-border-radius:5;-fx-background-radius:5;-fx-padding:6 10;-fx-font-size:12px;");
        tf.focusedProperty().addListener((o, oldV, newV) -> {
            if (newV) {
                tf.setStyle("-fx-background-color:white;-fx-border-color:#3498db;-fx-border-width:2;-fx-border-radius:5;-fx-background-radius:5;-fx-padding:6 10;-fx-font-size:12px;");
            } else {
                tf.setStyle("-fx-background-color:#F8F9FA;-fx-border-color:#D5DBDB;-fx-border-radius:5;-fx-background-radius:5;-fx-padding:6 10;-fx-font-size:12px;");
            }
        });

        fieldBox.getChildren().addAll(labelWrap, tf);
        return fieldBox;
    }

    /** Autocomplete CCCD */
    private void setupCCCDAutocomplete() {
        ContextMenu suggestions = new ContextMenu();
        cccdField.textProperty().addListener((obs, oldV, newV) -> {
            suggestions.hide();
            if (newV != null && !newV.trim().isEmpty()) {
                List<KhachHang> list = timKiemKhachHangTheoCCCD(newV.trim());
                if (!list.isEmpty()) {
                    suggestions.getItems().clear();
                    int n = Math.min(list.size(), 10);
                    for (int i = 0; i < n; i++) {
                        KhachHang kh = list.get(i);
                        String text = kh.getCCCD() + " - " + kh.getTenKhachHang() + " - " + kh.getSoDienThoai();
                        MenuItem mi = new MenuItem(text);
                        mi.setStyle("-fx-font-size:11px; -fx-padding:6 10;");
                        mi.setOnAction(e -> {
                            cccdField.setText(kh.getCCCD());
                            hoTenField.setText(kh.getTenKhachHang());
                            sdtField.setText(kh.getSoDienThoai());
                            emailField.setText(kh.getEmail() != null ? kh.getEmail() : "");
                            suggestions.hide();
                        });
                        suggestions.getItems().add(mi);
                    }
                    if (!suggestions.isShowing()) suggestions.show(cccdField, javafx.geometry.Side.BOTTOM, 0, 0);
                }
            }
        });
        cccdField.focusedProperty().addListener((o, oldF, newF) -> { if (!newF) suggestions.hide(); });
    }

    private List<KhachHang> timKiemKhachHangTheoCCCD(String prefix) {
        return KhachHang_Controller.timKhachHangTheoCCCDStartsWith(prefix);
    }

   
    /** Mở dialog chọn dịch vụ */
    private void openChonDichVuDialog(PhongDatModel phongDat) {
        try {
            javafx.stage.Stage dialog = new javafx.stage.Stage();
            dialog.setTitle("Chọn dịch vụ - Phòng " + phongDat.getSoPhong());
            dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);

            ChonDichVu_Dialog content = new ChonDichVu_Dialog(phongDat);
            javafx.scene.Scene scene = new javafx.scene.Scene(content, 800, 600);
            dialog.setScene(scene);
            dialog.setResizable(false);
            dialog.showAndWait();

            updateSummary();
            roomTable.refresh();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /** Thêm một CT vào bảng */
    private void addChiTietPhieuDatPhong(ChiTietPhieuDatPhong ct) {
        Phong phong = ct.getPhong();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String checkIn  = ct.getThoiGianNhanPhong().format(fmt);
        String checkOut = ct.getThoiGianTraPhong().format(fmt);
        String soGio    = calculateSoGioLuuTruFromHours(ct.getSoGioLuuTru());
        String loai     = phong.getLoaiPhong().getTenLoaiPhong();

        double tongTienPhong = ct.tinhThanhTien();
        String tongTienStr = String.format("%,.0f VNĐ", tongTienPhong);

        String dvStr = "Chưa chọn";
        List<DichVu> ds = ct.getDsachDichVu();
        boolean isVIP = loai != null && loai.equalsIgnoreCase("VIP");
        if (ds != null && !ds.isEmpty()) {
            if (isVIP) {
                dvStr = "Tất cả dịch vụ";
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < ds.size(); i++) {
                    sb.append(ds.get(i).getTenDichVu());
                    if (i < ds.size() - 1) sb.append(", ");
                }
                dvStr = sb.toString();
            }
        }

        PhongDatModel model = new PhongDatModel(
                phong.getSoPhong(), loai, checkIn, checkOut, soGio, dvStr, tongTienStr, phong, ct
        );
        roomData.add(model);
    }

    private String calculateSoGioLuuTruFromHours(int hours) {
        if (hours < 24) return hours + " giờ";
        int days = hours / 24, rem = hours % 24;
        return rem > 0 ? (days + " ngày " + rem + "h") : (days + " ngày");
    }

    /** Xác nhận đặt phòng */
    private void xacNhanDatPhong() {
        String cccd = cccdField.getText().trim();
        String hoTen = hoTenField.getText().trim();
        String sdt = sdtField.getText().trim();
        String email = emailField.getText().trim();

        if (cccd.isEmpty() || hoTen.isEmpty() || sdt.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập đầy đủ thông tin bắt buộc (CCCD, Họ tên, SĐT)!");
            return;
        }
        if (roomData.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn ít nhất một phòng!");
            return;
        }

        try {
            KhachHang_Controller khCtl = new KhachHang_Controller();
            KhachHang kh = KhachHang_Controller.timKhachHangTheoCCCD(cccd);

            if (kh == null) {
                KhachHang khMoi = new KhachHang();
                khMoi.setCCCD(cccd);
                khMoi.setTenKhachHang(hoTen);
                khMoi.setSoDienThoai(sdt);
                khMoi.setEmail(email);
                khMoi.setNgayTao(java.time.LocalDate.now());

                StringBuilder msg = new StringBuilder();
                boolean ok = khCtl.themKhachHang(khMoi, msg);
                if (!ok) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm khách hàng mới!\n" + msg);
                    return;
                }
                kh = KhachHang_Controller.timKhachHangTheoCCCD(cccd);
            }

            String maPhieu = PhieuDatPhong_Controller.generateMaPhieuDatPhong(java.time.LocalDate.now());
            List<ChiTietPhieuDatPhong> dsCT = new ArrayList<>();
            for (PhongDatModel m : roomData) {
                ChiTietPhieuDatPhong ct = m.getChiTietPhieuDatPhong();
                ct.setPhieuDatPhong(new PhieuDatPhong(maPhieu));
                if (ct.getLoaiDatPhong() == null) ct.setLoaiDatPhong(new LoaiDatPhong("LDP01"));
                dsCT.add(ct);
            }

            long tong = tinhTongTien();
            long coc  = (long) (tong * 0.3);

            PhieuDatPhong phieu = new PhieuDatPhong(maPhieu, kh, java.time.LocalDate.now(), dsCT, "Đã đặt", coc);
            boolean success = PhieuDatPhong_Controller.themPhieuDatPhong(phieu);

            if (success) {
                // Tạo và mở file PDF phiếu xác nhận
                try {
                    java.io.File pdfFile = utils.PhieuXacNhanDatPhong_PDFGenerator.generateAndOpenPDF(phieu);
                    
                    if (pdfFile != null && pdfFile.exists()) {
                        showAlert(Alert.AlertType.INFORMATION, "Thành công", 
                            "Đặt phòng thành công!\n" +
                            "Mã phiếu: " + maPhieu + "\n" +
                            "File phiếu xác nhận đã được tạo và mở tự động.");
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Cảnh báo", 
                            "Đặt phòng thành công nhưng không thể tạo file PDF phiếu xác nhận.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.WARNING, "Cảnh báo", 
                        "Đặt phòng thành công nhưng có lỗi khi tạo PDF: " + ex.getMessage());
                }
                
                resetForm();
                
                // Gọi callback
                if (onSuccessCallback != null) onSuccessCallback.run();
                
                // Đóng dialog
                ((javafx.stage.Stage) getScene().getWindow()).close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tạo phiếu đặt phòng. Vui lòng thử lại!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Đã xảy ra lỗi: " + ex.getMessage());
        }
    }

    private long tinhTongTien() {
        double tong = 0;
        for (PhongDatModel m : roomData) tong += m.getChiTietPhieuDatPhong().tinhThanhTien();
        return (long) tong;
    }

    private void resetForm() {
        roomData.clear();
        cccdField.clear(); hoTenField.clear(); sdtField.clear(); emailField.clear();
        Label tong = (Label) lookup("#tongTienValue");
        Label coc  = (Label) lookup("#tienCocValue");
        if (tong != null) tong.setText("0 VNĐ");
        if (coc  != null) coc.setText("0 VNĐ");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }
    
    

    /** Wrapper dịch vụ + số lượng */
    public static class DichVuWithQuantity {
        private final DichVu dichVu;
        private final javafx.beans.property.IntegerProperty soLuong = new javafx.beans.property.SimpleIntegerProperty(1);
        private final javafx.beans.property.BooleanProperty selected = new javafx.beans.property.SimpleBooleanProperty(false);
        public DichVuWithQuantity(DichVu dv) { this.dichVu = dv; }
        public DichVu getDichVu() { return dichVu; }
        public int getSoLuong() { return soLuong.get(); }
        public void setSoLuong(int v) { soLuong.set(v); }
        public javafx.beans.property.IntegerProperty soLuongProperty() { return soLuong; }
        public boolean isSelected() { return selected.get(); }
        public void setSelected(boolean v) { selected.set(v); }
        public javafx.beans.property.BooleanProperty selectedProperty() { return selected; }
        public String getMaDichVu() { return dichVu.getMaDichVu(); }
        public String getTenDichVu() { return dichVu.getTenDichVu(); }
        public double getGia() { return dichVu.getGia(); }
        public double getThanhTien() { return dichVu.getGia() * soLuong.get(); }
    }

    /** Dialog chọn dịch vụ */
    public static class ChonDichVu_Dialog extends BorderPane {
        private final PhongDatModel phongDatModel;
        private final ObservableList<DichVuWithQuantity> availableDichVu = FXCollections.observableArrayList();

        public ChonDichVu_Dialog(PhongDatModel phongDat) {
            this.phongDatModel = phongDat;
            List<DichVu> selected = phongDat.getSelectedDichVu();
            loadAllDichVuFromDatabase(selected);
            init();
        }

        private void loadAllDichVuFromDatabase(List<DichVu> selectedList) {
            try {
                DichVu_Controller ctl = new DichVu_Controller();
                List<DichVu> all = ctl.getDsDichVu();
                for (DichVu dv : all) {
                    DichVuWithQuantity w = new DichVuWithQuantity(dv);
                    if (selectedList != null && selectedList.stream().anyMatch(s -> s.getMaDichVu().equals(dv.getMaDichVu()))) {
                        w.setSelected(true);
                    }
                    availableDichVu.add(w);
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Không thể tải danh sách dịch vụ: " + e.getMessage()).showAndWait();
            }
        }

        private void init() {
            setStyle("-fx-background-color:#F5F7FA;");
            VBox main = new VBox(20);
            main.setPadding(new Insets(30));

            Label title = new Label("CHỌN DỊCH VỤ");
            title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
            title.setStyle("-fx-text-fill:#2C3E50;");

            VBox list = createDichVuList();
            HBox actions = createActionButtons();

            main.getChildren().addAll(title, list, actions);
            setCenter(main);
        }

        private VBox createDichVuList() {
            VBox box = new VBox(15);
            box.setStyle("-fx-background-color:white;-fx-padding:20;-fx-background-radius:8;-fx-effect:dropshadow(gaussian, rgba(0,0,0,0.1), 10,0,0,2);");

            Label lbl = new Label("Danh sách dịch vụ:");
            lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            lbl.setStyle("-fx-text-fill:#34495E;");

            TableView<DichVuWithQuantity> tv = new TableView<>();
            tv.setEditable(true);
            tv.setPrefHeight(298);
            tv.setItems(availableDichVu);

            TableColumn<DichVuWithQuantity, Boolean> sel = new TableColumn<>("Chọn");
            sel.setPrefWidth(60);
            sel.setCellFactory(c -> new javafx.scene.control.cell.CheckBoxTableCell<>());
            sel.setCellValueFactory(cd -> cd.getValue().selectedProperty());

            TableColumn<DichVuWithQuantity, String> ma = new TableColumn<>("Mã DV");
            ma.setPrefWidth(85);
            ma.setCellValueFactory(new PropertyValueFactory<>("maDichVu"));

            TableColumn<DichVuWithQuantity, String> ten = new TableColumn<>("Tên dịch vụ");
            ten.setPrefWidth(213);
            ten.setCellValueFactory(new PropertyValueFactory<>("tenDichVu"));

            TableColumn<DichVuWithQuantity, Integer> sl = new TableColumn<>("SL");
            sl.setPrefWidth(68);
            sl.setCellValueFactory(cd -> cd.getValue().soLuongProperty().asObject());
            sl.setCellFactory(c -> new TableCell<>() {
                private Spinner<Integer> sp;
                @Override protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getTableRow() == null || getTableRow().getItem() == null) { setGraphic(null); return; }
                    DichVuWithQuantity w = getTableRow().getItem();
                    if (sp == null) {
                        sp = new Spinner<>(1, 99, w.getSoLuong());
                        sp.setEditable(true);
                        sp.setPrefWidth(60);
                        sp.valueProperty().addListener((o, ov, nv) -> { if (nv != null) w.setSoLuong(nv); });
                    } else sp.getValueFactory().setValue(w.getSoLuong());
                    setGraphic(sp);
                }
            });

            TableColumn<DichVuWithQuantity, Double> gia = new TableColumn<>("Đơn giá");
            gia.setPrefWidth(102);
            gia.setCellValueFactory(new PropertyValueFactory<>("gia"));
            gia.setCellFactory(c -> new TableCell<>() {
                @Override protected void updateItem(Double v, boolean empty) {
                    super.updateItem(v, empty);
                    setText(empty || v == null ? null : String.format("%,.0f", v));
                }
            });

            TableColumn<DichVuWithQuantity, Double> tt = new TableColumn<>("Thành tiền");
            tt.setPrefWidth(111);
            tt.setCellValueFactory(cd -> {
                DichVuWithQuantity w = cd.getValue();
                var prop = new javafx.beans.property.SimpleDoubleProperty(w.getThanhTien());
                w.soLuongProperty().addListener((o, ov, nv) -> prop.set(w.getThanhTien()));
                return prop.asObject();
            });
            tt.setCellFactory(c -> new TableCell<>() {
                @Override protected void updateItem(Double v, boolean empty) {
                    super.updateItem(v, empty);
                    setText(empty || v == null ? null : String.format("%,.0f VNĐ", v));
                }
            });

            tv.getColumns().addAll(sel, ma, ten, sl, gia, tt);
            tv.setPlaceholder(new Label("Không có dịch vụ nào"));

            box.getChildren().addAll(lbl, tv);
            return box;
        }

        private HBox createActionButtons() {
            HBox box = new HBox(12);
            box.setAlignment(Pos.CENTER_RIGHT);

            Button cancel = new Button("Hủy");
            cancel.setPrefSize(100, 35);
            cancel.setOnAction(e -> ((javafx.stage.Stage) getScene().getWindow()).close());
            cancel.setStyle("-fx-background-color:#95a5a6;-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:5;");
            cancel.setOnMouseEntered(e -> cancel.setStyle("-fx-background-color:#7f8c8d;-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:5;"));
            cancel.setOnMouseExited(e -> cancel.setStyle("-fx-background-color:#95a5a6;-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:5;"));

            Button ok = new Button("Xác nhận");
            ok.setPrefSize(120, 35);
            ok.setStyle("-fx-background-color:#3498db;-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:5;");
            ok.setOnMouseEntered(e -> ok.setStyle("-fx-background-color:#2980b9;-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:5;"));
            ok.setOnMouseExited(e -> ok.setStyle("-fx-background-color:#3498db;-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:5;"));
            ok.setOnAction(e -> {
                List<DichVu> selected = availableDichVu.stream()
                        .filter(DichVuWithQuantity::isSelected)
                        .map(DichVuWithQuantity::getDichVu)
                        .collect(java.util.stream.Collectors.toList());

                phongDatModel.setSelectedDichVu(selected);

                String display = availableDichVu.stream()
                        .filter(DichVuWithQuantity::isSelected)
                        .map(w -> w.getSoLuong() > 1 ? (w.getTenDichVu() + "*" + w.getSoLuong()) : w.getTenDichVu())
                        .collect(java.util.stream.Collectors.joining(", "));
                phongDatModel.setDichVu(display.isEmpty() ? "Không có" : display);

                ChiTietPhieuDatPhong ct = phongDatModel.getChiTietPhieuDatPhong();
                double giaPhong = ct.getSoGioLuuTru() * ct.getPhong().getLoaiPhong().getGia();
                double tongDV = availableDichVu.stream()
                        .filter(DichVuWithQuantity::isSelected)
                        .mapToDouble(DichVuWithQuantity::getThanhTien)
                        .sum();
                double tongMoi = giaPhong + tongDV;
                phongDatModel.setTongTien(String.format("%,.0f VNĐ", tongMoi));

                ((javafx.stage.Stage) getScene().getWindow()).close();
            });

            box.getChildren().addAll(cancel, ok);
            return box;
        }
    }

    /** Model hiển thị 1 phòng đã chọn */
    public static class PhongDatModel {
        private String soPhong, loaiPhong, checkIn, checkOut, soGioLuuTru, dichVu, tongTien;
        private Phong phong;
        private List<DichVu> selectedDichVu;
        private final ChiTietPhieuDatPhong chiTietPhieuDatPhong;

        public PhongDatModel(String soPhong, String loaiPhong, String checkIn, String checkOut,
                             String soGioLuuTru, String dichVu, String tongTien,
                             Phong phong, ChiTietPhieuDatPhong chiTietPhieuDatPhong) {
            this.soPhong = soPhong;
            this.loaiPhong = loaiPhong;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
            this.soGioLuuTru = soGioLuuTru;
            this.dichVu = dichVu;
            this.tongTien = tongTien;
            this.phong = phong;
            this.chiTietPhieuDatPhong = chiTietPhieuDatPhong;
            this.selectedDichVu = chiTietPhieuDatPhong.getDsachDichVu() != null
                    ? new ArrayList<>(chiTietPhieuDatPhong.getDsachDichVu())
                    : new ArrayList<>();
        }

        public String getSoPhong() { return soPhong; }
        public String getLoaiPhong() { return loaiPhong; }
        public String getCheckIn() { return checkIn; }
        public String getCheckOut() { return checkOut; }
        public String getSoGioLuuTru() { return soGioLuuTru; }
        public String getDichVu() { return dichVu; }
        public String getTongTien() { return tongTien; }
        public Phong getPhong() { return phong; }
        public List<DichVu> getSelectedDichVu() { return selectedDichVu; }
        public ChiTietPhieuDatPhong getChiTietPhieuDatPhong() { return chiTietPhieuDatPhong; }

        public void setDichVu(String v) { this.dichVu = v; }
        public void setTongTien(String v) { this.tongTien = v; }
        public void setSelectedDichVu(List<DichVu> list) {
            this.selectedDichVu = list;
            if (chiTietPhieuDatPhong != null) {
                chiTietPhieuDatPhong.setDsachDichVu(list);
                double tongMoi = chiTietPhieuDatPhong.tinhThanhTien();
                this.tongTien = String.format("%,.0f VNĐ", tongMoi);
            }
            if (list == null || list.isEmpty()) this.dichVu = "Chưa chọn";
            else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < list.size(); i++) {
                    sb.append(list.get(i).getTenDichVu());
                    if (i < list.size() - 1) sb.append(", ");
                }
                this.dichVu = sb.toString();
            }
        }
    }

    /** Wrapper phòng + thời gian (nếu cần dùng ngoài) */
    public static class PhongWithDateTime {
        private Phong phong; private String checkIn; private String checkOut;
        public PhongWithDateTime(Phong phong, String checkIn, String checkOut) {
            this.phong = phong; this.checkIn = checkIn; this.checkOut = checkOut;
        }
        public Phong getPhong() { return phong; }
        public String getCheckIn() { return checkIn; }
        public String getCheckOut() { return checkOut; }
        public void setPhong(Phong p) { this.phong = p; }
        public void setCheckIn(String s) { this.checkIn = s; }
        public void setCheckOut(String s) { this.checkOut = s; }
    }
}

