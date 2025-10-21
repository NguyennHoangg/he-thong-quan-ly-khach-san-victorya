package view;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private GiaHanPhong_Controller controller;
    private List<ChiTietPhieuDatPhong> danhSachPhongHienTai;
    private List<ChiTietPhieuDatPhong> danhSachPhongDaChon;

    public GiaHanPhong_GUI() {
        this.controller = new GiaHanPhong_Controller();
        this.danhSachPhongHienTai = null;
        this.danhSachPhongDaChon = new java.util.ArrayList<>();
        
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
        
        txtSoDienThoai = new TextField();
        txtSoDienThoai.setPromptText("CCCD khách hàng (VD: 999999999999)");
        txtSoDienThoai.setPrefWidth(400);
        txtSoDienThoai.setPrefHeight(35);

        Button btnTimKiem = new Button("Tìm kiếm");
        btnTimKiem.setPrefHeight(35);
        btnTimKiem.getStyleClass().add("btn");
        btnTimKiem.setOnAction(e -> thucHienTimKiem());

        searchBox.getChildren().addAll(txtSoDienThoai, btnTimKiem);
        
        VBox tableBox = taoBangPhong();

        container.getChildren().addAll(lblTieuDe, searchBox, tableBox);
        return container;
    }

    private VBox taoBangPhong() {
        tablePhongGiaHan = new TableView<>();
        tablePhongGiaHan.setPrefWidth(540);
        tablePhongGiaHan.setPrefHeight(380);
        tablePhongGiaHan.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
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
        colSoPhong.setPrefWidth(90);

        TableColumn<RoomExtensionRow, String> colLoaiPhong = new TableColumn<>("Loại phòng");
        colLoaiPhong.setCellValueFactory(new PropertyValueFactory<>("loaiPhong"));
        colLoaiPhong.setPrefWidth(120);

        TableColumn<RoomExtensionRow, String> colTang = new TableColumn<>("Tầng");
        colTang.setCellValueFactory(new PropertyValueFactory<>("tang"));
        colTang.setPrefWidth(100);

        TableColumn<RoomExtensionRow, String> colGiaTheoNgay = new TableColumn<>("Giá theo ngày");
        colGiaTheoNgay.setCellValueFactory(new PropertyValueFactory<>("giaTheoNgay"));
        colGiaTheoNgay.setPrefWidth(120);

        TableColumn<RoomExtensionRow, String> colGiaTheoGio = new TableColumn<>("Giá theo giờ");
        colGiaTheoGio.setCellValueFactory(new PropertyValueFactory<>("giaTheoGio"));
        colGiaTheoGio.setPrefWidth(110);

        tablePhongGiaHan.getColumns().add(colSoPhong);
        tablePhongGiaHan.getColumns().add(colLoaiPhong);
        tablePhongGiaHan.getColumns().add(colTang);
        tablePhongGiaHan.getColumns().add(colGiaTheoNgay);
        tablePhongGiaHan.getColumns().add(colGiaTheoGio);

        VBox container = new VBox(tablePhongGiaHan);
        return container;
    }

    private VBox taoVungPhai() {
        VBox container = new VBox(20);
        
        Label lblTieuDe = new Label("Chọn thời gian gia hạn");
        lblTieuDe.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        containerChonThoiGian = new VBox(0);
        containerChonThoiGian.setPadding(new Insets(15));
        containerChonThoiGian.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        containerChonThoiGian.setPrefWidth(560);
        containerChonThoiGian.setPrefHeight(380);

        HBox headerRow = taoHeaderChonThoiGian();
        containerChonThoiGian.getChildren().add(headerRow);

        Button btnGiaHan = new Button("Gia hạn ngay");
        btnGiaHan.setPrefWidth(560);
        btnGiaHan.setPrefHeight(45);
        btnGiaHan.getStyleClass().add("btn");
        btnGiaHan.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-size: 16px;");
        btnGiaHan.setOnAction(e -> thucHienGiaHanNgay());

        container.getChildren().addAll(lblTieuDe, containerChonThoiGian, btnGiaHan);
        return container;
    }

    private HBox taoHeaderChonThoiGian() {
        HBox header = new HBox(0);
        header.setPadding(new Insets(10));
        header.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");

        Label lblSoPhong = new Label("Số phòng");
        lblSoPhong.setPrefWidth(100);
        lblSoPhong.setStyle("-fx-font-weight: bold; -fx-padding: 0 10 0 0;");

        Label lblNgayTraPhong = new Label("Ngày trả phòng");
        lblNgayTraPhong.setPrefWidth(180);
        lblNgayTraPhong.setStyle("-fx-font-weight: bold; -fx-border-color: transparent transparent transparent #ddd; -fx-border-width: 0 0 0 1; -fx-padding: 0 10 0 15;");

        Label lblGiaHanDen = new Label("Gia hạn đến");
        lblGiaHanDen.setPrefWidth(180);
        lblGiaHanDen.setStyle("-fx-font-weight: bold; -fx-border-color: transparent transparent transparent #ddd; -fx-border-width: 0 0 0 1; -fx-padding: 0 10 0 15;");

        Label lblThoiGianHan = new Label("Thời gian hạn");
        lblThoiGianHan.setPrefWidth(100);
        lblThoiGianHan.setStyle("-fx-font-weight: bold; -fx-border-color: transparent transparent transparent #ddd; -fx-border-width: 0 0 0 1; -fx-padding: 0 10 0 15;");

        header.getChildren().addAll(lblSoPhong, lblNgayTraPhong, lblGiaHanDen, lblThoiGianHan);
        return header;
    }

    private HBox taoDongPhongGiaHan(String soPhong, String ngayTraPhong, String ngayGiaHan, String thoiGianHan, ChiTietPhieuDatPhong chiTietPhieuDatPhong) {
        HBox row = new HBox(0);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-border-color: transparent transparent #ddd transparent; -fx-border-width: 0 0 1 0;");

        Label lblSoPhong = new Label(soPhong);
        lblSoPhong.setPrefWidth(100);
        lblSoPhong.setStyle("-fx-font-weight: bold; -fx-padding: 0 10 0 0;");

        Label lblNgayTra = new Label(ngayTraPhong);
        lblNgayTra.setPrefWidth(180);
        lblNgayTra.setStyle("-fx-border-color: transparent transparent transparent #ddd; -fx-border-width: 0 0 0 1; -fx-padding: 0 10 0 15;");

        Label lblThoiGian = new Label(thoiGianHan);
        lblThoiGian.setPrefWidth(100);
        lblThoiGian.setAlignment(Pos.CENTER);
        lblThoiGian.setStyle("-fx-font-weight: bold; -fx-text-fill: #10b981; -fx-border-color: transparent transparent transparent #ddd; -fx-border-width: 0 0 0 1; -fx-padding: 0 10 0 15;");

        Button btnChonGiaHan = new Button(ngayGiaHan);
        btnChonGiaHan.setPrefWidth(165);
        btnChonGiaHan.setPrefHeight(32);
        btnChonGiaHan.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 5;");
        btnChonGiaHan.setOnAction(e -> hienThiChonGiaHan(btnChonGiaHan, lblThoiGian, chiTietPhieuDatPhong));
        
        VBox vboxGiaHan = new VBox(btnChonGiaHan);
        vboxGiaHan.setPrefWidth(180);
        vboxGiaHan.setAlignment(Pos.CENTER_LEFT);
        vboxGiaHan.setStyle("-fx-border-color: transparent transparent transparent #ddd; -fx-border-width: 0 0 0 1; -fx-padding: 0 10 0 15;");

        row.getChildren().addAll(lblSoPhong, lblNgayTra, vboxGiaHan, lblThoiGian);
        return row;
    }
    
    private void hienThiChonGiaHan(Button btnTarget, Label lblThoiGian, ChiTietPhieuDatPhong chiTietPhieuDatPhong) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Chọn thời gian gia hạn");
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white;");
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
        
        Button btnXacNhan = new Button("Xác nhận");
        btnXacNhan.getStyleClass().add("btn");
        btnXacNhan.setPrefWidth(120);
        btnXacNhan.setOnAction(e -> {
            if (datePicker.getValue() != null && cboGio.getValue() != null) {
                // Tạo thời gian kết thúc mới
                LocalDateTime gioKetThucMoi = LocalDateTime.of(
                    datePicker.getValue(),
                    java.time.LocalTime.parse(cboGio.getValue())
                );
                
                // Cập nhật UI
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                btnTarget.setText(gioKetThucMoi.format(formatter));
                // Lưu thời gian đã chọn (chưa áp dụng vào database)
                btnTarget.setUserData(gioKetThucMoi.toString());
                
                // Tính thời gian gia hạn (từ thời gian kết thúc cũ đến mới)
                LocalDateTime gioKetThucCu = chiTietPhieuDatPhong.getThoiGianTraPhong();
                Duration thoiGianGiaHan = Duration.between(gioKetThucCu, gioKetThucMoi);
                
                // Tính ngày, giờ, phút
                long tongGio = thoiGianGiaHan.toHours();
                long ngay = tongGio / 24;
                long gio = tongGio % 24;
                long phut = thoiGianGiaHan.toMinutes() % 60;
                
                // Hiển thị theo định dạng phù hợp
                StringBuilder thoiGianText = new StringBuilder();
                if (ngay > 0) {
                    thoiGianText.append(ngay).append(" ngày ");
                }
                if (gio > 0) {
                    thoiGianText.append(gio).append(" giờ ");
                }
                if (phut > 0 || thoiGianText.length() == 0) {
                    thoiGianText.append(phut).append(" phút");
                }
                
                lblThoiGian.setText(thoiGianText.toString().trim());
                
            dialog.close();
            }
        });
        
        root.getChildren().addAll(title, datePicker, cboGio, btnXacNhan);
        dialog.setScene(new javafx.scene.Scene(root, 350, 280));
        dialog.showAndWait();
    }

    /**
     * Thực hiện tìm kiếm phòng theo CCCD
     */
    private void thucHienTimKiem() {
        String cccd = txtSoDienThoai.getText().trim();
        if (cccd.isEmpty()) {
            System.out.println("Vui long nhap CCCD");
            hienThiThongBao(Alert.AlertType.WARNING, "Canh bao", "Vui long nhap CCCD khach hang");
            return;
        }
        
        System.out.println("Tim kiem phong cho CCCD: " + cccd);
        
        // Gọi controller để tìm phòng
        danhSachPhongHienTai = controller.timDatPhongHienTaiTheoCCCD(cccd);
        
        if (danhSachPhongHienTai != null && !danhSachPhongHienTai.isEmpty()) {
            hienThiBangPhong();
            // Xóa danh sách phòng đã chọn
            danhSachPhongDaChon.clear();
            // Xóa panel gia hạn, chỉ giữ header với hướng dẫn
            containerChonThoiGian.getChildren().clear();
            containerChonThoiGian.getChildren().add(taoHeaderChonThoiGian());
            Label lblHuongDan = new Label("Chon phong tu bang ben trai de gia han");
            lblHuongDan.setStyle("-fx-padding: 20; -fx-font-size: 14px; -fx-text-fill: #666;");
            containerChonThoiGian.getChildren().add(lblHuongDan);
            hienThiThongBao(Alert.AlertType.INFORMATION, "Thanh cong", 
                "Tim thay " + danhSachPhongHienTai.size() + " phong dang hoat dong\nChon phong de gia han");
        } else {
            System.out.println("Khong tim thay phong nao cho CCCD: " + cccd);
            // Xóa dữ liệu cũ
            tablePhongGiaHan.getItems().clear();
            containerChonThoiGian.getChildren().clear();
            containerChonThoiGian.getChildren().add(taoHeaderChonThoiGian());
            hienThiThongBao(Alert.AlertType.INFORMATION, "Thong bao", 
                "Khong tim thay phong nao dang hoat dong cho CCCD: " + cccd);
        }
    }
    
    /**
     * Hiển thị danh sách phòng lên bảng
     */
    private void hienThiBangPhong() {
        ObservableList<RoomExtensionRow> data = FXCollections.observableArrayList();
        
        for (ChiTietPhieuDatPhong ctpdp : danhSachPhongHienTai) {
            String soPhong = ctpdp.getPhong().getMaPhong();
            String loaiPhong = ctpdp.getPhong().getLoaiPhong().getTenLoaiPhong();
            String tang = "Tầng " + ctpdp.getPhong().getTang();
            String giaTheoNgay = String.format("%.0f VND", ctpdp.getPhong().getLoaiPhong().getGia());
            String giaTheoGio = String.format("%.0f VND", ctpdp.getPhong().getLoaiPhong().getGia() / 24);
            
            data.add(new RoomExtensionRow(soPhong, loaiPhong, tang, giaTheoNgay, giaTheoGio));
        }
        
        tablePhongGiaHan.setItems(data);
    }

    /**
     * Hiển thị panel gia hạn với dữ liệu động (deprecated - không dùng nữa)
     */
    @Deprecated
    private void hienThiPanelGiaHan() {
        // Xóa dữ liệu cũ
        containerChonThoiGian.getChildren().clear();
        containerChonThoiGian.getChildren().add(taoHeaderChonThoiGian());
        
        // Thêm dữ liệu mới
        for (ChiTietPhieuDatPhong ctpdp : danhSachPhongHienTai) {
            String soPhong = ctpdp.getPhong().getMaPhong();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String ngayTraPhong = ctpdp.getThoiGianTraPhong().format(formatter);
            
            HBox row = taoDongPhongGiaHan(soPhong, ngayTraPhong, "Chon thoi gian", "", ctpdp);
            containerChonThoiGian.getChildren().add(row);
        }
    }
    
    /**
     * Cập nhật panel gia hạn theo phòng đã chọn từ bảng
     */
    private void capNhatPanelGiaHanTheoLuaChon() {
        // Lấy danh sách phòng đã chọn từ bảng
        var selectedItems = tablePhongGiaHan.getSelectionModel().getSelectedItems();
        
        if (selectedItems.isEmpty()) {
            return;
        }
        
        // Xóa dữ liệu cũ
        containerChonThoiGian.getChildren().clear();
        containerChonThoiGian.getChildren().add(taoHeaderChonThoiGian());
        
        // Cập nhật danh sách phòng đã chọn
        danhSachPhongDaChon.clear();
        
        // Thêm các phòng đã chọn vào panel
        for (RoomExtensionRow row : selectedItems) {
            // Tìm ChiTietPhieuDatPhong tương ứng
            ChiTietPhieuDatPhong ctpdp = timPhongTheoMa(row.getSoPhong());
            if (ctpdp != null) {
                danhSachPhongDaChon.add(ctpdp);
                
                String soPhong = ctpdp.getPhong().getMaPhong();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                String ngayTraPhong = ctpdp.getThoiGianTraPhong().format(formatter);
                
                HBox rowBox = taoDongPhongGiaHan(soPhong, ngayTraPhong, "Chon thoi gian", "", ctpdp);
                containerChonThoiGian.getChildren().add(rowBox);
            }
        }
        
        System.out.println("Da chon " + danhSachPhongDaChon.size() + " phong de gia han");
    }
    
    /**
     * Tìm ChiTietPhieuDatPhong theo mã phòng
     */
    private ChiTietPhieuDatPhong timPhongTheoMa(String maPhong) {
        if (danhSachPhongHienTai == null) {
            return null;
        }
        
        for (ChiTietPhieuDatPhong ctpdp : danhSachPhongHienTai) {
            if (ctpdp.getPhong().getMaPhong().equals(maPhong)) {
                return ctpdp;
            }
        }
        
        return null;
    }
    
    /**
     * Thực hiện gia hạn ngay cho tất cả phòng
     */
    private void thucHienGiaHanNgay() {
        if (danhSachPhongDaChon == null || danhSachPhongDaChon.isEmpty()) {
            System.out.println("Khong co phong nao duoc chon de gia han");
            hienThiThongBao(Alert.AlertType.WARNING, "Canh bao", 
                "Vui long chon phong tu bang ben trai de gia han");
            return;
        }
        
        int soPhongThanhCong = 0;
        int soPhongThatBai = 0;
        
        for (int i = 1; i < containerChonThoiGian.getChildren().size(); i++) {
            HBox row = (HBox) containerChonThoiGian.getChildren().get(i);
            VBox vboxGiaHan = (VBox) row.getChildren().get(2);
            Button btnGiaHan = (Button) vboxGiaHan.getChildren().get(0);
            
            // Lấy thông tin phòng tương ứng từ danh sách đã chọn
            ChiTietPhieuDatPhong ctpdp = danhSachPhongDaChon.get(i - 1);
            
            // Kiểm tra xem đã chọn thời gian gia hạn chưa
            String userData = (String) btnGiaHan.getUserData();
            LocalDateTime gioKetThucMoi;
            
            if (userData != null && userData.startsWith("APPLIED:")) {
                // Đã được áp dụng rồi, bỏ qua (đã gia hạn trong database)
                System.out.println("Phong " + ctpdp.getPhong().getMaPhong() + " da duoc gia han roi");
                continue;
            } else if (userData != null && !userData.equals("Chon thoi gian") && !userData.isEmpty()) {
                // Người dùng đã chọn thời gian qua modal nhưng chưa áp dụng
                try {
                    gioKetThucMoi = LocalDateTime.parse(userData);
                } catch (Exception e) {
                    // Nếu parse lỗi, báo lỗi và bỏ qua phòng này
                    soPhongThatBai++;
                    System.out.println("Gia han that bai phong " + ctpdp.getPhong().getMaPhong() + " - Chua chon thoi gian");
                    continue;
                }
            } else {
                // Chưa chọn thời gian, thông báo thất bại
                soPhongThatBai++;
                System.out.println("Gia han that bai phong " + ctpdp.getPhong().getMaPhong() + " - Chua chon thoi gian");
                continue;
            }
            
            // Gia hạn phòng
            boolean thanhCong = controller.giaHanDen(
                ctpdp.getPhieuDatPhong().getMaPhieuDatPhong(),
                ctpdp.getPhong().getMaPhong(),
                gioKetThucMoi
            );
            
            if (thanhCong) {
                soPhongThanhCong++;
                // Đánh dấu đã áp dụng
                btnGiaHan.setUserData("APPLIED:" + gioKetThucMoi.toString());
                System.out.println("Gia han thanh cong phong " + ctpdp.getPhong().getMaPhong() + " den " + gioKetThucMoi);
            } else {
                soPhongThatBai++;
                System.out.println("Gia han that bai phong " + ctpdp.getPhong().getMaPhong());
            }
        }
        
        System.out.println("Ket qua gia han: " + soPhongThanhCong + " thanh cong, " + soPhongThatBai + " that bai");
        
        // Hiển thị thông báo kết quả
        if (soPhongThanhCong > 0 && soPhongThatBai == 0) {
            hienThiThongBao(Alert.AlertType.INFORMATION, "Thanh cong", 
                "Gia han thanh cong " + soPhongThanhCong + " phong!");
        } else if (soPhongThanhCong > 0 && soPhongThatBai > 0) {
            hienThiThongBao(Alert.AlertType.WARNING, "Canh bao", 
                "Gia han thanh cong " + soPhongThanhCong + " phong\n" +
                "Gia han that bai " + soPhongThatBai + " phong");
        } else if (soPhongThatBai > 0) {
            hienThiThongBao(Alert.AlertType.ERROR, "That bai", 
                "Gia han that bai tat ca " + soPhongThatBai + " phong!");
        }
        
        // Xóa dữ liệu bên phải và cập nhật lại từ database
        if (soPhongThanhCong > 0) {
            System.out.println("Dang cap nhat lai du lieu man hinh...");
            // Xóa panel bên phải
            containerChonThoiGian.getChildren().clear();
            containerChonThoiGian.getChildren().add(taoHeaderChonThoiGian());
            Label lblHuongDan = new Label("Chon phong tu bang ben trai de gia han");
            lblHuongDan.setStyle("-fx-padding: 20; -fx-font-size: 14px; -fx-text-fill: #666;");
            containerChonThoiGian.getChildren().add(lblHuongDan);
            
            // Xóa danh sách phòng đã chọn
            danhSachPhongDaChon.clear();
            
            // Bỏ selection trên bảng
            tablePhongGiaHan.getSelectionModel().clearSelection();
            
            // Cập nhật lại dữ liệu bảng từ database
            capNhatLaiDuLieu();
        }
    }
    
    /**
     * Cập nhật lại dữ liệu từ database sau khi gia hạn
     */
    private void capNhatLaiDuLieu() {
        // Lấy CCCD đang tìm kiếm
        String cccd = txtSoDienThoai.getText().trim();
        
        if (cccd.isEmpty()) {
            System.out.println("Khong co CCCD de cap nhat");
            return;
        }
        
        System.out.println("Cap nhat lai du lieu cho CCCD: " + cccd);
        
        // Gọi lại controller để lấy dữ liệu mới từ database
        danhSachPhongHienTai = controller.timDatPhongHienTaiTheoCCCD(cccd);
        
        if (danhSachPhongHienTai != null && !danhSachPhongHienTai.isEmpty()) {
            // Cập nhật lại bảng và panel
            hienThiBangPhong();
            hienThiPanelGiaHan();
            System.out.println("Da cap nhat " + danhSachPhongHienTai.size() + " phong");
        } else {
            System.out.println("Khong con phong nao dang hoat dong cho CCCD: " + cccd);
            // Xóa dữ liệu hiển thị nếu không còn phòng
            tablePhongGiaHan.getItems().clear();
            containerChonThoiGian.getChildren().clear();
            containerChonThoiGian.getChildren().add(taoHeaderChonThoiGian());
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
        private final String soPhong, loaiPhong, tang, giaTheoNgay, giaTheoGio;

        public RoomExtensionRow(String soPhong, String loaiPhong, String tang, String giaTheoNgay, String giaTheoGio) {
            this.soPhong = soPhong;
            this.loaiPhong = loaiPhong;
            this.tang = tang;
            this.giaTheoNgay = giaTheoNgay;
            this.giaTheoGio = giaTheoGio;
        }

        public String getSoPhong() { return soPhong; }
        public String getLoaiPhong() { return loaiPhong; }
        public String getTang() { return tang; }
        public String getGiaTheoNgay() { return giaTheoNgay; }
        public String getGiaTheoGio() { return giaTheoGio; }
    }
}
