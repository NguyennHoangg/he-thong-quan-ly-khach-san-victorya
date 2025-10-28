package view;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * Giao diện gia hạn phòng - Phiên bản mới với thiết kế đẹp hơn
 */
public class GiaHanPhong_GUI_New extends BorderPane {

    private TextField txtCCCD;
    private TableView<RoomExtensionRow> tablePhong;
    private VBox containerThongTin;
    private GiaHanPhong_Controller controller;
    private List<ChiTietPhieuDatPhong> danhSachPhongHienTai;
    private Map<String, ExtensionInfo> extensionData; // Lưu thông tin gia hạn cho từng phòng
    
    // Labels thông tin khách hàng
    private Label lblTenKhach;
    private Label lblSDT;
    private Label lblEmail;
    private Label lblSoPhongDangO;

    public GiaHanPhong_GUI_New() {
        this.controller = new GiaHanPhong_Controller();
        this.danhSachPhongHienTai = new ArrayList<>();
        this.extensionData = new HashMap<>();
        
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f0f2f5;");
        getStylesheets().add(getClass().getResource("/css/Button.css").toExternalForm());

        HBox mainContainer = new HBox(20);
        mainContainer.setAlignment(Pos.TOP_CENTER);

        VBox leftPanel = taoVungTrai();
        VBox rightPanel = taoVungPhai();

        mainContainer.getChildren().addAll(leftPanel, rightPanel);
        setCenter(mainContainer);
    }

    private VBox taoVungTrai() {
        VBox container = new VBox(20);
        
        Label lblTieuDe = new Label("Chọn phòng gia hạn");
        lblTieuDe.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        HBox searchBox = new HBox(10);
        searchBox.setPadding(new Insets(15));
        searchBox.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        txtCCCD = new TextField();
        txtCCCD.setPromptText("CCCD khách hàng (VD: 123456789012)");
        txtCCCD.setPrefWidth(400);
        txtCCCD.setPrefHeight(35);

        Button btnTimKiem = new Button("Tìm kiếm");
        btnTimKiem.setPrefHeight(35);
        btnTimKiem.getStyleClass().add("btn");
        btnTimKiem.setOnAction(e -> thucHienTimKiem());

        searchBox.getChildren().addAll(txtCCCD, btnTimKiem);
        
        VBox tableBox = taoBangPhong();

        container.getChildren().addAll(lblTieuDe, searchBox, tableBox);
        return container;
    }

    private VBox taoBangPhong() {
        tablePhong = new TableView<>();
        tablePhong.setPrefWidth(540);
        tablePhong.setPrefHeight(380);
        tablePhong.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        // Cho phép chọn nhiều dòng
        tablePhong.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        
        // Lắng nghe thay đổi selection
        tablePhong.getSelectionModel().getSelectedItems().addListener(
            (javafx.collections.ListChangeListener.Change<? extends RoomExtensionRow> change) -> {
                capNhatThongTinGiaHan();
            }
        );
        
        // Toggle selection
        tablePhong.setRowFactory(tv -> {
            javafx.scene.control.TableRow<RoomExtensionRow> row = new javafx.scene.control.TableRow<>();
            final int[] lastClickedIndex = {-1};
            
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    int index = row.getIndex();
                    if (lastClickedIndex[0] == index && tablePhong.getSelectionModel().isSelected(index)) {
                        tablePhong.getSelectionModel().clearSelection(index);
                        lastClickedIndex[0] = -1;
                    } else {
                        tablePhong.getSelectionModel().select(index);
                        lastClickedIndex[0] = index;
                    }
                }
            });
            return row;
        });

        // Columns
        TableColumn<RoomExtensionRow, String> colSoPhong = new TableColumn<>("Phòng");
        colSoPhong.setCellValueFactory(new PropertyValueFactory<>("soPhong"));
        colSoPhong.setPrefWidth(80);

        TableColumn<RoomExtensionRow, String> colLoaiPhong = new TableColumn<>("Loại");
        colLoaiPhong.setCellValueFactory(new PropertyValueFactory<>("loaiPhong"));
        colLoaiPhong.setPrefWidth(100);

        TableColumn<RoomExtensionRow, String> colThoiGianTra = new TableColumn<>("Trả phòng");
        colThoiGianTra.setCellValueFactory(new PropertyValueFactory<>("thoiGianTra"));
        colThoiGianTra.setPrefWidth(140);

        TableColumn<RoomExtensionRow, String> colConLai = new TableColumn<>("Còn lại");
        colConLai.setCellValueFactory(new PropertyValueFactory<>("thoiGianConLai"));
        colConLai.setPrefWidth(100);

        TableColumn<RoomExtensionRow, String> colGia = new TableColumn<>("Giá/ngày");
        colGia.setCellValueFactory(new PropertyValueFactory<>("giaTheoNgay"));
        colGia.setPrefWidth(120);

        tablePhong.getColumns().addAll(colSoPhong, colLoaiPhong, colThoiGianTra, colConLai, colGia);

        VBox container = new VBox(tablePhong);
        return container;
    }

    private VBox taoVungPhai() {
        VBox container = new VBox(20);
        
        Label lblTieuDe = new Label("Thông tin khách hàng");
        lblTieuDe.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        VBox cardKhachHang = taoCardThongTinKhach();
        
        containerThongTin = new VBox(15);
        containerThongTin.setPadding(new Insets(20));
        containerThongTin.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        containerThongTin.setPrefWidth(560);
        containerThongTin.setMinHeight(250);
        
        Label lblHuongDan = new Label("Chọn phòng từ bảng bên trái để gia hạn");
        lblHuongDan.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b; -fx-padding: 10 0 0 0;");
        lblHuongDan.setWrapText(true);
        containerThongTin.getChildren().add(lblHuongDan);
        
        Button btnGiaHan = new Button("Gia hạn phòng");
        btnGiaHan.setPrefWidth(560);
        btnGiaHan.setPrefHeight(45);
        btnGiaHan.getStyleClass().add("btn");
        btnGiaHan.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-size: 16px;");
        btnGiaHan.setOnAction(e -> thucHienGiaHan());

        container.getChildren().addAll(lblTieuDe, cardKhachHang, containerThongTin, btnGiaHan);
        return container;
    }

    private VBox taoCardThongTinKhach() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        // Tên khách
        HBox rowTen = new HBox(10);
        Label lblTenLabel = new Label("Tên khách hàng:");
        lblTenLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b; -fx-font-weight: 500; -fx-min-width: 140;");
        lblTenKhach = new Label("---");
        lblTenKhach.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        rowTen.getChildren().addAll(lblTenLabel, lblTenKhach);
        
        // SĐT
        HBox rowSDT = new HBox(10);
        Label lblSDTLabel = new Label("Số điện thoại:");
        lblSDTLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b; -fx-font-weight: 500; -fx-min-width: 140;");
        lblSDT = new Label("---");
        lblSDT.setStyle("-fx-font-size: 13px;");
        rowSDT.getChildren().addAll(lblSDTLabel, lblSDT);
        
        // Email
        HBox rowEmail = new HBox(10);
        Label lblEmailLabel = new Label("Email:");
        lblEmailLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b; -fx-font-weight: 500; -fx-min-width: 140;");
        lblEmail = new Label("---");
        lblEmail.setStyle("-fx-font-size: 13px;");
        rowEmail.getChildren().addAll(lblEmailLabel, lblEmail);
        
        // Số phòng đang ở
        HBox rowPhong = new HBox(10);
        Label lblPhongLabel = new Label("Phòng đang ở:");
        lblPhongLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b; -fx-font-weight: 500; -fx-min-width: 140;");
        lblSoPhongDangO = new Label("---");
        lblSoPhongDangO.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #3b82f6;");
        rowPhong.getChildren().addAll(lblPhongLabel, lblSoPhongDangO);
        
        card.getChildren().addAll(rowTen, rowSDT, rowEmail, rowPhong);
        return card;
    }

    /**
     * Tìm kiếm phòng theo CCCD
     */
    private void thucHienTimKiem() {
        String cccd = txtCCCD.getText().trim();
        if (cccd.isEmpty()) {
            hienThiThongBao(Alert.AlertType.WARNING, "Canh bao", "Vui long nhap CCCD khach hang");
            return;
        }
        
        System.out.println("Tim kiem phong dang o cho CCCD: " + cccd);
        
        danhSachPhongHienTai = controller.timDatPhongHienTaiTheoCCCD(cccd);
        
        if (danhSachPhongHienTai != null && !danhSachPhongHienTai.isEmpty()) {
            // Hiển thị thông tin khách hàng
            ChiTietPhieuDatPhong ctpdp = danhSachPhongHienTai.get(0);
            if (ctpdp.getPhieuDatPhong() != null && ctpdp.getPhieuDatPhong().getKhachHang() != null) {
                hienThiThongTinKhach(ctpdp.getPhieuDatPhong().getKhachHang());
            }
            
            hienThiBangPhong();
            extensionData.clear();
            tablePhong.getSelectionModel().clearSelection();
            
            hienThiThongBao(Alert.AlertType.INFORMATION, "Thanh cong", 
                "Tim thay " + danhSachPhongHienTai.size() + " phong dang o\nChon phong de gia han");
        } else {
            System.out.println("Khong tim thay phong cho CCCD: " + cccd);
            tablePhong.getItems().clear();
            xoaThongTinKhach();
            hienThiThongBao(Alert.AlertType.INFORMATION, "Thong bao", 
                "Khong tim thay phong dang o cho CCCD: " + cccd);
        }
    }
    
    /**
     * Hiển thị thông tin khách hàng
     */
    private void hienThiThongTinKhach(KhachHang khachHang) {
        if (khachHang != null) {
            lblTenKhach.setText(khachHang.getTenKhachHang());
            lblSDT.setText(khachHang.getSoDienThoai() != null ? khachHang.getSoDienThoai() : "---");
            lblEmail.setText(khachHang.getEmail() != null ? khachHang.getEmail() : "---");
            
            // Tổng số phòng đang ở
            if (danhSachPhongHienTai != null) {
                lblSoPhongDangO.setText(danhSachPhongHienTai.size() + " phòng");
            }
        }
    }
    
    /**
     * Xóa thông tin khách hàng
     */
    private void xoaThongTinKhach() {
        lblTenKhach.setText("---");
        lblSDT.setText("---");
        lblEmail.setText("---");
        lblSoPhongDangO.setText("---");
    }
    
    /**
     * Hiển thị danh sách phòng
     */
    private void hienThiBangPhong() {
        ObservableList<RoomExtensionRow> data = FXCollections.observableArrayList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (ChiTietPhieuDatPhong ctpdp : danhSachPhongHienTai) {
            String soPhong = ctpdp.getPhong().getSoPhong();
            String loaiPhong = ctpdp.getPhong().getLoaiPhong().getTenLoaiPhong();
            String thoiGianTra = ctpdp.getThoiGianTraPhong() != null ? 
                ctpdp.getThoiGianTraPhong().format(formatter) : "---";
            
            // Tính thời gian còn lại
            String thoiGianConLai = tinhThoiGianConLai(ctpdp.getThoiGianTraPhong());
            
            String giaTheoNgay = String.format("%,.0f VND", ctpdp.getPhong().getLoaiPhong().getGia());
            
            RoomExtensionRow row = new RoomExtensionRow(
                soPhong,
                loaiPhong,
                thoiGianTra,
                thoiGianConLai,
                giaTheoNgay,
                ctpdp.getPhong().getMaPhong()
            );
            
            data.add(row);
        }
        
        tablePhong.setItems(data);
    }
    
    /**
     * Tính thời gian còn lại
     */
    private String tinhThoiGianConLai(LocalDateTime thoiGianTra) {
        if (thoiGianTra == null) return "---";
        
        Duration duration = Duration.between(LocalDateTime.now(), thoiGianTra);
        long hours = duration.toHours();
        
        if (hours < 0) {
            return "Quá hạn";
        } else if (hours < 24) {
            return hours + " giờ";
        } else {
            long days = hours / 24;
            long remainingHours = hours % 24;
            return days + " ngày " + (remainingHours > 0 ? remainingHours + "h" : "");
        }
    }
    
    /**
     * Cập nhật thông tin gia hạn khi chọn phòng
     */
    private void capNhatThongTinGiaHan() {
        var selectedItems = tablePhong.getSelectionModel().getSelectedItems();
        
        containerThongTin.getChildren().clear();
        
        if (selectedItems.isEmpty()) {
            Label lblHuongDan = new Label("Chọn phòng từ bảng bên trái để gia hạn");
            lblHuongDan.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b; -fx-padding: 10 0 0 0;");
            lblHuongDan.setWrapText(true);
            containerThongTin.getChildren().add(lblHuongDan);
            return;
        }
        
        Label lblTitle = new Label("Chọn thời gian gia hạn (" + selectedItems.size() + " phòng)");
        lblTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 0 0 10 0;");
        containerThongTin.getChildren().add(lblTitle);
        
        for (RoomExtensionRow row : selectedItems) {
            ChiTietPhieuDatPhong ctpdp = timPhongTheoMa(row.getMaPhong());
            if (ctpdp != null) {
                VBox phongBox = taoFormGiaHanPhong(row, ctpdp);
                containerThongTin.getChildren().add(phongBox);
            }
        }
    }
    
    /**
     * Tạo form gia hạn cho 1 phòng
     */
    private VBox taoFormGiaHanPhong(RoomExtensionRow row, ChiTietPhieuDatPhong ctpdp) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(12));
        box.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 6;");
        
        // Header - Tên phòng và thời gian trả hiện tại
        HBox header = new HBox(10);
        Label lblPhong = new Label("Phòng " + row.getSoPhong());
        lblPhong.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        Label lblTraHienTai = new Label("(Trả: " + ctpdp.getThoiGianTraPhong().format(formatter) + ")");
        lblTraHienTai.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
        
        header.getChildren().addAll(lblPhong, lblTraHienTai);
        
        // Form chọn thời gian
        HBox formBox = new HBox(10);
        
        Label lblGiaHanDen = new Label("Gia hạn đến:");
        lblGiaHanDen.setStyle("-fx-font-size: 13px; -fx-min-width: 80;");
        
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(ctpdp.getThoiGianTraPhong().toLocalDate().plusDays(1));
        datePicker.setPrefWidth(140);
        
        ComboBox<String> cboGio = new ComboBox<>();
        for (int i = 0; i < 24; i++) {
            cboGio.getItems().add(String.format("%02d:00", i));
        }
        cboGio.setValue("14:00");
        cboGio.setPrefWidth(80);
        
        formBox.getChildren().addAll(lblGiaHanDen, datePicker, cboGio);
        
        extensionData.put(row.getMaPhong(), new ExtensionInfo(datePicker, cboGio, ctpdp));
        
        box.getChildren().addAll(header, formBox);
        return box;
    }
    
    /**
     * Tìm phòng theo mã
     */
    private ChiTietPhieuDatPhong timPhongTheoMa(String maPhong) {
        if (danhSachPhongHienTai == null) return null;
        
        for (ChiTietPhieuDatPhong ctpdp : danhSachPhongHienTai) {
            if (ctpdp.getPhong().getMaPhong().equals(maPhong)) {
                return ctpdp;
            }
        }
        return null;
    }
    
    /**
     * Thực hiện gia hạn
     */
    private void thucHienGiaHan() {
        if (extensionData.isEmpty()) {
            hienThiThongBao(Alert.AlertType.WARNING, "Canh bao", "Vui long chon phong de gia han");
            return;
        }
        
        int soPhongThanhCong = 0;
        int soPhongThatBai = 0;
        
        for (Map.Entry<String, ExtensionInfo> entry : extensionData.entrySet()) {
            String maPhong = entry.getKey();
            ExtensionInfo info = entry.getValue();
            
            try {
                LocalDate ngay = info.datePicker.getValue();
                String gio = info.cboGio.getValue();
                
                if (ngay == null || gio == null || gio.isEmpty()) {
                    soPhongThatBai++;
                    System.out.println("Phong " + info.ctpdp.getPhong().getSoPhong() + " - Chua chon du thoi gian");
                    continue;
                }
                
                LocalDateTime gioKetThucMoi = LocalDateTime.of(ngay, LocalTime.parse(gio));
                
                // Kiểm tra thời gian hợp lệ
                if (gioKetThucMoi.isBefore(info.ctpdp.getThoiGianTraPhong()) || 
                    gioKetThucMoi.isBefore(LocalDateTime.now())) {
                    soPhongThatBai++;
                    System.out.println("Phong " + info.ctpdp.getPhong().getSoPhong() + " - Thoi gian khong hop le");
                    continue;
                }
                
                // Gọi controller gia hạn
                boolean thanhCong = controller.giaHanDen(
                    info.ctpdp.getPhieuDatPhong().getMaPhieuDatPhong(),
                    maPhong,
                    gioKetThucMoi
                );
                
                if (thanhCong) {
                    soPhongThanhCong++;
                    System.out.println("Gia han thanh cong phong " + info.ctpdp.getPhong().getSoPhong());
                } else {
                    soPhongThatBai++;
                    System.out.println("Gia han that bai phong " + info.ctpdp.getPhong().getSoPhong());
                }
                
            } catch (Exception e) {
                soPhongThatBai++;
                System.out.println("Loi gia han phong: " + e.getMessage());
            }
        }
        
        // Hiển thị thông báo kết quả
        if (soPhongThanhCong > 0 && soPhongThatBai == 0) {
            hienThiThongBao(Alert.AlertType.INFORMATION, "Thanh cong", 
                "Gia han thanh cong " + soPhongThanhCong + " phong!");
        } else if (soPhongThanhCong > 0 && soPhongThatBai > 0) {
            hienThiThongBao(Alert.AlertType.WARNING, "Canh bao", 
                "Gia han thanh cong " + soPhongThanhCong + " phong\nGia han that bai " + soPhongThatBai + " phong");
        } else {
            hienThiThongBao(Alert.AlertType.ERROR, "That bai", 
                "Gia han that bai tat ca " + soPhongThatBai + " phong!");
        }
        
        if (soPhongThanhCong > 0) {
            capNhatLaiDuLieu();
        }
    }
    
    /**
     * Cập nhật lại dữ liệu sau khi gia hạn
     */
    private void capNhatLaiDuLieu() {
        String cccd = txtCCCD.getText().trim();
        if (cccd.isEmpty()) {
            return;
        }
        
        System.out.println("Cap nhat lai du lieu cho CCCD: " + cccd);
        
        danhSachPhongHienTai = controller.timDatPhongHienTaiTheoCCCD(cccd);
        
        if (danhSachPhongHienTai != null && !danhSachPhongHienTai.isEmpty()) {
            hienThiBangPhong();
            tablePhong.getSelectionModel().clearSelection();
            extensionData.clear();
            
            containerThongTin.getChildren().clear();
            Label lblHuongDan = new Label("Chọn phòng từ bảng bên trái để gia hạn");
            lblHuongDan.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b; -fx-padding: 10 0 0 0;");
            containerThongTin.getChildren().add(lblHuongDan);
            
            System.out.println("Da cap nhat " + danhSachPhongHienTai.size() + " phong");
        } else {
            System.out.println("Khong con phong nao dang o cho CCCD: " + cccd);
            tablePhong.getItems().clear();
            xoaThongTinKhach();
            extensionData.clear();
            containerThongTin.getChildren().clear();
        }
    }
    
    /**
     * Hiển thị thông báo
     */
    private void hienThiThongBao(Alert.AlertType loai, String tieuDe, String noiDung) {
        Alert alert = new Alert(loai);
        alert.setTitle(tieuDe);
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
    }

    // ===== INNER CLASSES =====
    
    /**
     * Model cho bảng phòng
     */
    public static class RoomExtensionRow {
        private final String soPhong, loaiPhong, thoiGianTra, thoiGianConLai, giaTheoNgay, maPhong;

        public RoomExtensionRow(String soPhong, String loaiPhong, String thoiGianTra,
                                String thoiGianConLai, String giaTheoNgay, String maPhong) {
            this.soPhong = soPhong;
            this.loaiPhong = loaiPhong;
            this.thoiGianTra = thoiGianTra;
            this.thoiGianConLai = thoiGianConLai;
            this.giaTheoNgay = giaTheoNgay;
            this.maPhong = maPhong;
        }

        public String getSoPhong() { return soPhong; }
        public String getLoaiPhong() { return loaiPhong; }
        public String getThoiGianTra() { return thoiGianTra; }
        public String getThoiGianConLai() { return thoiGianConLai; }
        public String getGiaTheoNgay() { return giaTheoNgay; }
        public String getMaPhong() { return maPhong; }
    }
    
    /**
     * Lưu thông tin gia hạn cho mỗi phòng
     */
    private static class ExtensionInfo {
        DatePicker datePicker;
        ComboBox<String> cboGio;
        ChiTietPhieuDatPhong ctpdp;
        
        ExtensionInfo(DatePicker datePicker, ComboBox<String> cboGio, ChiTietPhieuDatPhong ctpdp) {
            this.datePicker = datePicker;
            this.cboGio = cboGio;
            this.ctpdp = ctpdp;
        }
    }
}

