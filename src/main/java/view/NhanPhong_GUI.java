package view;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import controller.NhanPhong_Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
import model.PhieuDatPhong;

/**
 * Giao diện nhận phòng
 */
public class NhanPhong_GUI extends BorderPane {

    private TextField txtCCCD;
    private TableView<RoomCheckInRow> tablePhongChoNhan;
    private VBox containerThongTinKhach;
    private NhanPhong_Controller controller;
    private PhieuDatPhong phieuDatPhongHienTai;
    private List<RoomCheckInRow> danhSachPhongDaChon;
    
    // Labels hiển thị thông tin khách hàng
    private Label lblTenKhach;
    private Label lblSDT;
    private Label lblEmail;
    private Label lblMaPhieu;

    public NhanPhong_GUI() {
        this.controller = new NhanPhong_Controller();
        this.phieuDatPhongHienTai = null;
        this.danhSachPhongDaChon = new ArrayList<>();
        
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
        
        Label lblTieuDe = new Label("Tìm kiếm phòng chờ nhận");
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
        tablePhongChoNhan = new TableView<>();
        tablePhongChoNhan.setPrefWidth(540);
        tablePhongChoNhan.setPrefHeight(380);
        tablePhongChoNhan.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        // Cho phép chọn nhiều dòng
        tablePhongChoNhan.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        
        // Lắng nghe thay đổi selection
        tablePhongChoNhan.getSelectionModel().getSelectedItems().addListener(
            (javafx.collections.ListChangeListener.Change<? extends RoomCheckInRow> change) -> {
                capNhatDanhSachPhongDaChon();
            }
        );
        
        // Custom row factory để toggle selection khi click
        tablePhongChoNhan.setRowFactory(tv -> {
            javafx.scene.control.TableRow<RoomCheckInRow> row = new javafx.scene.control.TableRow<>();
            
            final int[] lastClickedIndex = {-1};
            
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    int index = row.getIndex();
                    
                    // Nếu click vào cùng 1 row 2 lần liên tiếp → toggle
                    if (lastClickedIndex[0] == index && tablePhongChoNhan.getSelectionModel().isSelected(index)) {
                        tablePhongChoNhan.getSelectionModel().clearSelection(index);
                        lastClickedIndex[0] = -1;
                    } else {
                        tablePhongChoNhan.getSelectionModel().select(index);
                        lastClickedIndex[0] = index;
                    }
                }
            });
            return row;
        });

        TableColumn<RoomCheckInRow, String> colSoPhong = new TableColumn<>("Số phòng");
        colSoPhong.setCellValueFactory(new PropertyValueFactory<>("soPhong"));
        colSoPhong.setPrefWidth(90);

        TableColumn<RoomCheckInRow, String> colLoaiPhong = new TableColumn<>("Loại phòng");
        colLoaiPhong.setCellValueFactory(new PropertyValueFactory<>("loaiPhong"));
        colLoaiPhong.setPrefWidth(120);

        TableColumn<RoomCheckInRow, String> colTang = new TableColumn<>("Tầng");
        colTang.setCellValueFactory(new PropertyValueFactory<>("tang"));
        colTang.setPrefWidth(70);

        TableColumn<RoomCheckInRow, String> colGiaTheoNgay = new TableColumn<>("Giá theo ngày");
        colGiaTheoNgay.setCellValueFactory(new PropertyValueFactory<>("giaTheoNgay"));
        colGiaTheoNgay.setPrefWidth(130);

        TableColumn<RoomCheckInRow, String> colThoiGianNhan = new TableColumn<>("Thời gian nhận");
        colThoiGianNhan.setCellValueFactory(new PropertyValueFactory<>("thoiGianNhan"));
        colThoiGianNhan.setPrefWidth(130);

        tablePhongChoNhan.getColumns().add(colSoPhong);
        tablePhongChoNhan.getColumns().add(colLoaiPhong);
        tablePhongChoNhan.getColumns().add(colTang);
        tablePhongChoNhan.getColumns().add(colGiaTheoNgay);
        tablePhongChoNhan.getColumns().add(colThoiGianNhan);

        VBox container = new VBox(tablePhongChoNhan);
        return container;
    }

    private VBox taoVungPhai() {
        VBox container = new VBox(20);
        
        Label lblTieuDe = new Label("Thông tin khách hàng");
        lblTieuDe.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        VBox cardKhachHang = taoThongTinKhach();
        
        containerThongTinKhach = new VBox(15);
        containerThongTinKhach.setPadding(new Insets(20));
        containerThongTinKhach.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        containerThongTinKhach.setPrefWidth(560);
        containerThongTinKhach.setMinHeight(200);
        
        Label lblTomTat = new Label("Chọn phòng từ bảng bên trái để nhận phòng");
        lblTomTat.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b; -fx-padding: 10 0 0 0;");
        lblTomTat.setWrapText(true);
        containerThongTinKhach.getChildren().add(lblTomTat);

        Button btnNhanPhong = new Button("Nhận phòng");
        btnNhanPhong.setPrefWidth(560);
        btnNhanPhong.setPrefHeight(45);
        btnNhanPhong.getStyleClass().add("btn");
        btnNhanPhong.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-size: 16px;");
        btnNhanPhong.setOnAction(e -> thucHienNhanPhong());

        container.getChildren().addAll(lblTieuDe, cardKhachHang, containerThongTinKhach, btnNhanPhong);
        return container;
    }

    private VBox taoThongTinKhach() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        // Tên khách hàng
        HBox rowTen = new HBox(10);
        Label lblTenLabel = new Label("Tên khách hàng:");
        lblTenLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b; -fx-font-weight: 500; -fx-min-width: 140;");
        lblTenKhach = new Label("---");
        lblTenKhach.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        rowTen.getChildren().addAll(lblTenLabel, lblTenKhach);
        
        // Số điện thoại
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
        
        // Mã phiếu đặt phòng
        HBox rowMaPhieu = new HBox(10);
        Label lblMaPhieuLabel = new Label("Mã phiếu đặt phòng:");
        lblMaPhieuLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b; -fx-font-weight: 500; -fx-min-width: 140;");
        lblMaPhieu = new Label("---");
        lblMaPhieu.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #3b82f6;");
        rowMaPhieu.getChildren().addAll(lblMaPhieuLabel, lblMaPhieu);
        
        card.getChildren().addAll(rowTen, rowSDT, rowEmail, rowMaPhieu);
        return card;
    }

    /**
     * Thực hiện tìm kiếm phòng theo CCCD
     */
    private void thucHienTimKiem() {
        String cccd = txtCCCD.getText().trim();
        if (cccd.isEmpty()) {
            System.out.println("Vui long nhap CCCD");
            hienThiThongBao(Alert.AlertType.WARNING, "Canh bao", "Vui long nhap CCCD khach hang");
            return;
        }
        
        System.out.println("Tim kiem phong cho nhan voi CCCD: " + cccd);
        
        // Gọi controller để tìm phòng chờ nhận
        phieuDatPhongHienTai = controller.timPhieuDatPhongChoNhanTheoCCCD(cccd);
        
        if (phieuDatPhongHienTai != null && 
            phieuDatPhongHienTai.getDsachPhieuDatPhong() != null && 
            !phieuDatPhongHienTai.getDsachPhieuDatPhong().isEmpty()) {
            
            // Hiển thị thông tin khách hàng
            hienThiThongTinKhach(phieuDatPhongHienTai.getKhachHang());
            
            // Hiển thị danh sách phòng
            hienThiBangPhong();
            
            // Xóa danh sách phòng đã chọn
            danhSachPhongDaChon.clear();
            
            hienThiThongBao(Alert.AlertType.INFORMATION, "Thanh cong", 
                "Tim thay " + phieuDatPhongHienTai.getDsachPhieuDatPhong().size() + 
                " phong cho nhan\nChon phong de nhan phong");
        } else {
            System.out.println("Khong tim thay phong cho nhan cho CCCD: " + cccd);
            // Xóa dữ liệu cũ
            tablePhongChoNhan.getItems().clear();
            xoaThongTinKhach();
            hienThiThongBao(Alert.AlertType.INFORMATION, "Thong bao", 
                "Khong tim thay phong cho nhan cho CCCD: " + cccd);
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
        }
        
        if (phieuDatPhongHienTai != null) {
            lblMaPhieu.setText(phieuDatPhongHienTai.getMaPhieuDatPhong());
        }
    }
    
    /**
     * Xóa thông tin khách hàng
     */
    private void xoaThongTinKhach() {
        lblTenKhach.setText("---");
        lblSDT.setText("---");
        lblEmail.setText("---");
        lblMaPhieu.setText("---");
    }
    
    /**
     * Hiển thị danh sách phòng lên bảng
     */
    private void hienThiBangPhong() {
        ObservableList<RoomCheckInRow> data = FXCollections.observableArrayList();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (ChiTietPhieuDatPhong ctpdp : phieuDatPhongHienTai.getDsachPhieuDatPhong()) {
            String soPhong = ctpdp.getPhong().getSoPhong();
            String loaiPhong = ctpdp.getPhong().getLoaiPhong().getTenLoaiPhong();
            String tang = "Tầng " + ctpdp.getPhong().getTang();
            String giaTheoNgay = String.format("%.0f VND", ctpdp.getPhong().getLoaiPhong().getGia());
            String thoiGianNhan = ctpdp.getThoiGianNhanPhong() != null ? 
                ctpdp.getThoiGianNhanPhong().format(formatter) : "---";
            
            RoomCheckInRow row = new RoomCheckInRow(
                soPhong, 
                loaiPhong, 
                tang, 
                giaTheoNgay, 
                thoiGianNhan,
                ctpdp.getPhong().getMaPhong()
            );
            
            data.add(row);
        }
        
        tablePhongChoNhan.setItems(data);
    }

    /**
     * Cập nhật danh sách phòng đã chọn
     */
    private void capNhatDanhSachPhongDaChon() {
        var selectedItems = tablePhongChoNhan.getSelectionModel().getSelectedItems();
        
        danhSachPhongDaChon.clear();
        danhSachPhongDaChon.addAll(selectedItems);
        
        // Cập nhật label tóm tắt
        capNhatLabelTomTat();
        
        System.out.println("Da chon " + danhSachPhongDaChon.size() + " phong de nhan");
    }
    
    /**
     * Cập nhật label tóm tắt phòng đã chọn
     */
    private void capNhatLabelTomTat() {
        if (containerThongTinKhach.getChildren().size() >= 1) {
            Label lblTomTat = (Label) containerThongTinKhach.getChildren().get(0);
            
            if (danhSachPhongDaChon.isEmpty()) {
                lblTomTat.setText("Chọn phòng từ bảng bên trái để nhận phòng");
                lblTomTat.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b; -fx-padding: 10 0 0 0;");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Đã chọn ").append(danhSachPhongDaChon.size()).append(" phòng: ");
                for (int i = 0; i < danhSachPhongDaChon.size(); i++) {
                    sb.append(danhSachPhongDaChon.get(i).getSoPhong());
                    if (i < danhSachPhongDaChon.size() - 1) {
                        sb.append(", ");
                    }
                }
                lblTomTat.setText(sb.toString());
                lblTomTat.setStyle("-fx-font-size: 13px; -fx-text-fill: #10b981; -fx-font-weight: bold; -fx-padding: 10 0 0 0;");
            }
        }
    }
    
    /**
     * Thực hiện nhận phòng
     */
    private void thucHienNhanPhong() {
        if (danhSachPhongDaChon == null || danhSachPhongDaChon.isEmpty()) {
            System.out.println("Khong co phong nao duoc chon de nhan");
            hienThiThongBao(Alert.AlertType.WARNING, "Canh bao", 
                "Vui long chon phong tu bang ben trai de nhan phong");
            return;
        }
        
        List<String> danhSachMaPhong = new ArrayList<>();
        for (RoomCheckInRow row : danhSachPhongDaChon) {
            danhSachMaPhong.add(row.getMaPhong());
        }
        
        int soPhongThanhCong = controller.nhanNhieuPhong(danhSachMaPhong);
        
        System.out.println("Ket qua nhan phong: " + soPhongThanhCong + " thanh cong");
        
        // Hiển thị thông báo kết quả
        if (soPhongThanhCong == danhSachMaPhong.size()) {
            hienThiThongBao(Alert.AlertType.INFORMATION, "Thanh cong", 
                "Nhan phong thanh cong " + soPhongThanhCong + " phong!");
            
            // Xóa dữ liệu và làm mới giao diện
            lamMoiGiaoDien();
        } else if (soPhongThanhCong > 0) {
            hienThiThongBao(Alert.AlertType.WARNING, "Canh bao", 
                "Nhan phong thanh cong " + soPhongThanhCong + "/" + danhSachMaPhong.size() + " phong");
            
            // Cập nhật lại danh sách
            capNhatLaiDuLieu();
        } else {
            hienThiThongBao(Alert.AlertType.ERROR, "That bai", 
                "Nhan phong that bai!");
        }
    }
    
    /**
     * Làm mới giao diện sau khi nhận phòng thành công
     */
    private void lamMoiGiaoDien() {
        // Xóa danh sách phòng
        tablePhongChoNhan.getItems().clear();
        tablePhongChoNhan.getSelectionModel().clearSelection();
        
        // Xóa thông tin khách hàng
        xoaThongTinKhach();
        
        // Xóa CCCD đã nhập
        txtCCCD.clear();
        
        // Xóa danh sách phòng đã chọn
        danhSachPhongDaChon.clear();
        
        // Xóa phiếu đặt phòng hiện tại
        phieuDatPhongHienTai = null;
        
        // Cập nhật label tóm tắt
        capNhatLabelTomTat();
        
        System.out.println("Da lam moi giao dien");
    }
    
    /**
     * Cập nhật lại dữ liệu từ database sau khi nhận một số phòng
     */
    private void capNhatLaiDuLieu() {
        // Lấy CCCD đang tìm kiếm
        String cccd = txtCCCD.getText().trim();
        
        if (cccd.isEmpty()) {
            System.out.println("Khong co CCCD de cap nhat");
            return;
        }
        
        System.out.println("Cap nhat lai du lieu cho CCCD: " + cccd);
        
        // Gọi lại controller để lấy dữ liệu mới từ database
        phieuDatPhongHienTai = controller.timPhieuDatPhongChoNhanTheoCCCD(cccd);
        
        if (phieuDatPhongHienTai != null && 
            phieuDatPhongHienTai.getDsachPhieuDatPhong() != null && 
            !phieuDatPhongHienTai.getDsachPhieuDatPhong().isEmpty()) {
            
            // Cập nhật lại bảng
            hienThiBangPhong();
            
            // Xóa selection
            tablePhongChoNhan.getSelectionModel().clearSelection();
            
            // Xóa danh sách phòng đã chọn
            danhSachPhongDaChon.clear();
            
            // Cập nhật label tóm tắt
            capNhatLabelTomTat();
            
            System.out.println("Da cap nhat " + phieuDatPhongHienTai.getDsachPhieuDatPhong().size() + " phong");
        } else {
            System.out.println("Khong con phong nao cho nhan cho CCCD: " + cccd);
            // Xóa dữ liệu hiển thị nếu không còn phòng
            lamMoiGiaoDien();
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
        private final String soPhong, loaiPhong, tang, giaTheoNgay, thoiGianNhan, maPhong;

        public RoomCheckInRow(String soPhong, String loaiPhong, String tang, 
                              String giaTheoNgay, String thoiGianNhan, String maPhong) {
            this.soPhong = soPhong;
            this.loaiPhong = loaiPhong;
            this.tang = tang;
            this.giaTheoNgay = giaTheoNgay;
            this.thoiGianNhan = thoiGianNhan;
            this.maPhong = maPhong;
        }

        public String getSoPhong() { return soPhong; }
        public String getLoaiPhong() { return loaiPhong; }
        public String getTang() { return tang; }
        public String getGiaTheoNgay() { return giaTheoNgay; }
        public String getThoiGianNhan() { return thoiGianNhan; }
        public String getMaPhong() { return maPhong; }
    }
}

