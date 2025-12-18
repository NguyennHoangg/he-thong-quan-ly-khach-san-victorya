package view;

import controller.KhachHang_Controller;
import model.KhachHang;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class QuanLiKhachHang_GUI extends BorderPane {
    private TextField tfTenKhachHang = new TextField();
    private TextField tfCCCD = new TextField();
    private TextField tfEmail = new TextField();
    private TextField tfSoDienThoai = new TextField();
    
    // Fields tìm kiếm
    private TextField tfTimTen = new TextField();
    private TextField tfTimCCCD = new TextField();
    private TextField tfTimEmail = new TextField();
    private TextField tfTimSDT = new TextField();
    
    private Button btnThem = new Button("Thêm khách hàng");
    private Button btnSua = new Button("Sửa thông tin");
    private Button btnXoa = new Button("Xóa khách hàng");
    private Button btnMoi = new Button("Xóa trắng");
    private Button btnLamMoi = new Button("Làm mới");
    private Button btnTimKiem = new Button("Tìm kiếm");
    
    private HBox khuVucNut = new HBox(10);
    
    private TableView<KhachHang> bangKhachHang = new TableView<>();
    private KhachHang_Controller kh_ctrl = new KhachHang_Controller();
    
    private String maKhachHangDangChon = null;

    public QuanLiKhachHang_GUI() {
        setPadding(new Insets(16));
        VBox container = new VBox(12);
        container.getChildren().addAll(taoFormNhapLieu(), taoBang());
        setCenter(container);
        container.getStylesheets().add(getClass().getResource("/css/Label.css").toExternalForm());
        container.getStylesheets().add(getClass().getResource("/css/Control.css").toExternalForm());
        container.getStylesheets().add(getClass().getResource("/css/Button.css").toExternalForm());
        container.getStylesheets().add(getClass().getResource("/css/Table.css").toExternalForm());
        
        taiDanhSachKhachHang();
    }

    private Node taoFormNhapLieu() {
        // --- Thiết lập TextField nhập liệu ---
        tfTenKhachHang.setPromptText("Nhập tên khách hàng");
        tfCCCD.setPromptText("Nhập căn cước công dân (12 số)");
        tfEmail.setPromptText("Nhập email");
        tfSoDienThoai.setPromptText("Nhập số điện thoại");

        // --- Thiết lập TextField tìm kiếm ---
        tfTimTen.setPromptText("Tìm theo tên");
        tfTimCCCD.setPromptText("Tìm theo CCCD");
        tfTimEmail.setPromptText("Tìm theo email");
        tfTimSDT.setPromptText("Tìm theo số điện thoại");

        for (TextField tf : new TextField[] { tfTenKhachHang, tfCCCD, tfEmail, tfSoDienThoai, 
                tfTimTen, tfTimCCCD, tfTimEmail, tfTimSDT }) {
            tf.getStyleClass().add("text-field");
        }

        // --- Label ---
        Label lblTen = new Label("Tên khách hàng");
        Label lblCCCD = new Label("CCCD");
        Label lblEmail = new Label("Email");
        Label lblSoDienThoai = new Label("Số điện thoại");
        
        Label lblTimTen = new Label("Tìm theo tên");
        Label lblTimCCCD = new Label("Tìm theo CCCD");
        Label lblTimEmail = new Label("Tìm theo email");
        Label lblTimSDT = new Label("Tìm theo số điện thoại");

        // --- Kích thước đồng nhất ---
        double ngang = 220;
        double doc = 35;

        for (TextField tf : new TextField[] { tfTenKhachHang, tfCCCD, tfEmail, tfSoDienThoai, 
                tfTimTen, tfTimCCCD, tfTimEmail, tfTimSDT }) {
            tf.setPrefWidth(ngang);
            tf.setPrefHeight(doc);
        }

        // --- Nút ---
        btnThem.getStyleClass().add("btn-luu");
        btnSua.getStyleClass().add("btn-luu");
        btnXoa.getStyleClass().add("btn-huy");
        btnMoi.getStyleClass().add("btn-lam-moi");
        btnLamMoi.getStyleClass().add("btn-small");
        btnTimKiem.getStyleClass().add("btn");

        btnThem.setOnAction(e -> xuLyThem());
        btnSua.setOnAction(e -> xuLySua());
        btnXoa.setOnAction(e -> xuLyXoa());
        btnMoi.setOnAction(e -> lamMoi());
        btnLamMoi.setOnAction(e -> taiDanhSachKhachHang());
        btnTimKiem.setOnAction(e -> xuLyTimKiem());
        
        // Tìm kiếm khi nhấn Enter
        tfTimTen.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) xuLyTimKiem();
        });
        tfTimCCCD.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) xuLyTimKiem();
        });
        tfTimEmail.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) xuLyTimKiem();
        });
        tfTimSDT.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) xuLyTimKiem();
        });

        // --- Validation khi focus out ---
        tfTenKhachHang.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) validateTenKhachHang();
        });
        tfCCCD.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) validateCCCD();
        });
        tfSoDienThoai.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) validateSoDienThoai();
        });
        tfEmail.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) validateEmail();
        });

        // --- Khởi tạo button area ---
        capNhatKhuVucNut();

        // --- GridPane ---
        GridPane formGrid = new GridPane();
        formGrid.setHgap(20);
        formGrid.setVgap(12);

        // Hàng 1: Thông tin nhập liệu (4 cột)
        formGrid.add(taoKhuVucLabel(lblTen, tfTenKhachHang), 0, 0);
        formGrid.add(taoKhuVucLabel(lblCCCD, tfCCCD), 1, 0);
        formGrid.add(taoKhuVucLabel(lblEmail, tfEmail), 2, 0);
        formGrid.add(taoKhuVucLabel(lblSoDienThoai, tfSoDienThoai), 3, 0);

        // Hàng 2: Button area
        khuVucNut.setAlignment(Pos.CENTER_LEFT);
        formGrid.add(khuVucNut, 0, 1, 4, 1);
        GridPane.setMargin(khuVucNut, new Insets(10, 0, 0, 0));

        // Hàng 3: Tìm kiếm (4 cột)
        formGrid.add(taoKhuVucLabel(lblTimTen, tfTimTen), 0, 2);
        formGrid.add(taoKhuVucLabel(lblTimCCCD, tfTimCCCD), 1, 2);
        formGrid.add(taoKhuVucLabel(lblTimEmail, tfTimEmail), 2, 2);
        formGrid.add(taoKhuVucLabel(lblTimSDT, tfTimSDT), 3, 2);

        // Hàng 4: Button tìm kiếm
        HBox khuVucTimKiem = new HBox(10, btnTimKiem);
        khuVucTimKiem.setAlignment(Pos.CENTER_LEFT);
        formGrid.add(khuVucTimKiem, 0, 3, 4, 1);
        GridPane.setMargin(khuVucTimKiem, new Insets(10, 0, 0, 0));

        return formGrid;
    }
    
    private void capNhatKhuVucNut() {
        khuVucNut.getChildren().clear();
        if (maKhachHangDangChon == null) {
            // Chưa chọn: chỉ hiển thị "Thêm khách hàng" và "Làm mới"
            khuVucNut.getChildren().addAll(btnThem, btnMoi);
            btnThem.setVisible(true);
            btnSua.setVisible(false);
            btnXoa.setVisible(false);
        } else {
            // Đã chọn: hiển thị "Sửa thông tin", "Xóa khách hàng", "Làm mới"
            khuVucNut.getChildren().addAll(btnSua, btnXoa, btnMoi);
            btnThem.setVisible(false);
            btnSua.setVisible(true);
            btnXoa.setVisible(true);
        }
    }

    private VBox taoKhuVucLabel(Label lbl, Node control) {
        lbl.getStyleClass().add("label");
        VBox box = new VBox(6, lbl, control);
        box.setPrefWidth(360);
        return box;
    }

    private VBox taoBang() {
        // Cột Mã khách hàng
        TableColumn<KhachHang, String> colMaKH = new TableColumn<>("Mã KH");
        colMaKH.setCellValueFactory(new PropertyValueFactory<>("maKhachHang"));
        colMaKH.setPrefWidth(100);

        // Cột Tên khách hàng
        TableColumn<KhachHang, String> colTenKH = new TableColumn<>("Tên khách hàng");
        colTenKH.setCellValueFactory(new PropertyValueFactory<>("tenKhachHang"));
        colTenKH.setPrefWidth(180);

        // Cột CCCD
        TableColumn<KhachHang, String> colCCCD = new TableColumn<>("CCCD");
        colCCCD.setCellValueFactory(new PropertyValueFactory<>("CCCD"));
        colCCCD.setPrefWidth(130);

        // Cột Email
        TableColumn<KhachHang, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(200);

        // Cột Số điện thoại
        TableColumn<KhachHang, String> colSoDienThoai = new TableColumn<>("Số điện thoại");
        colSoDienThoai.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        colSoDienThoai.setPrefWidth(150);

        // Cột Ngày tạo
        TableColumn<KhachHang, String> colNgayTao = new TableColumn<>("Ngày tạo");
        colNgayTao.setCellValueFactory(cellData -> {
            LocalDate ngayTao = cellData.getValue().getNgayTao();
            String text = ngayTao != null ? ngayTao.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "-";
            return new ReadOnlyStringWrapper(text);
        });
        colNgayTao.setPrefWidth(130);

        bangKhachHang.getColumns().add(colMaKH);
        bangKhachHang.getColumns().add(colTenKH);
        bangKhachHang.getColumns().add(colCCCD);
        bangKhachHang.getColumns().add(colEmail);
        bangKhachHang.getColumns().add(colSoDienThoai);
        bangKhachHang.getColumns().add(colNgayTao);
        bangKhachHang.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        bangKhachHang.setPrefHeight(440);
        bangKhachHang.getStyleClass().add("table");

        // Sự kiện chọn dòng
        bangKhachHang.getSelectionModel().selectedItemProperty().addListener((obs, cu, moi) -> {
            if (moi != null) {
                maKhachHangDangChon = moi.getMaKhachHang();
                tfTenKhachHang.setText(moi.getTenKhachHang());
                tfCCCD.setText(moi.getCCCD());
                tfEmail.setText(moi.getEmail() != null ? moi.getEmail() : "");
                tfSoDienThoai.setText(moi.getSoDienThoai());
            } else {
                maKhachHangDangChon = null;
            }
            capNhatKhuVucNut();
        });

        // Header với tiêu đề và nút làm mới
        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label lblTieuDe = new Label("DANH SÁCH KHÁCH HÀNG");
        lblTieuDe.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        btnLamMoi.setPrefWidth(110);
        btnLamMoi.setPrefHeight(30);
        
        headerBox.getChildren().addAll(lblTieuDe, btnLamMoi);
        
        ScrollPane scrollPane = new ScrollPane(bangKhachHang);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefHeight(350);
        scrollPane.setStyle("-fx-background-color: white;");
        
        VBox container = new VBox(10);
        container.getChildren().addAll(headerBox, scrollPane);
        
        return container;
    }

    private void taiDanhSachKhachHang() {
        List<KhachHang> dsKH = kh_ctrl.getDsKhachHang();
        ObservableList<KhachHang> danhSachMaster = FXCollections.observableArrayList(dsKH);
        bangKhachHang.setItems(danhSachMaster);
    }

    public void xuLyThem() {
        String tenKH = tfTenKhachHang.getText().trim();
        String cccd = tfCCCD.getText().trim();
        String email = tfEmail.getText().trim();
        String soDienThoai = tfSoDienThoai.getText().trim();

        KhachHang kh = new KhachHang();
        kh.setTenKhachHang(tenKH);
        kh.setCCCD(cccd);
        kh.setEmail(email.isEmpty() ? null : email);
        kh.setSoDienThoai(soDienThoai);
        kh.setNgayTao(LocalDate.now());

        StringBuilder loiNhan = new StringBuilder();
        boolean hopLe = kh_ctrl.kiemTra(kh, loiNhan);
        if (!hopLe) {
            Alert canhBao = new Alert(Alert.AlertType.ERROR);
            canhBao.setTitle("Cảnh báo");
            canhBao.setHeaderText(null);
            canhBao.setContentText(loiNhan.toString());
            canhBao.showAndWait();
            return;
        }
        
        // Thêm mới
        if (kh_ctrl.themKhachHang(kh, loiNhan)) {
            Alert thongBao = new Alert(Alert.AlertType.INFORMATION);
            thongBao.setTitle("Thông báo");
            thongBao.setContentText(loiNhan.toString());
            thongBao.setHeaderText(null);
            thongBao.showAndWait();
            lamMoi();
        } else {
            Alert thongBao = new Alert(Alert.AlertType.ERROR);
            thongBao.setTitle("Thông báo");
            thongBao.setContentText(loiNhan.toString());
            thongBao.setHeaderText(null);
            thongBao.showAndWait();
        }
    }

    public void xuLySua() {
        if (maKhachHangDangChon == null) {
            Alert canhBao = new Alert(Alert.AlertType.WARNING);
            canhBao.setTitle("Cảnh báo");
            canhBao.setHeaderText(null);
            canhBao.setContentText("Vui lòng chọn khách hàng cần sửa!");
            canhBao.showAndWait();
            return;
        }

        String tenKH = tfTenKhachHang.getText().trim();
        String cccd = tfCCCD.getText().trim();
        String email = tfEmail.getText().trim();
        String soDienThoai = tfSoDienThoai.getText().trim();

        KhachHang kh = new KhachHang();
        kh.setMaKhachHang(maKhachHangDangChon);
        kh.setTenKhachHang(tenKH);
        kh.setCCCD(cccd);
        kh.setEmail(email.isEmpty() ? null : email);
        kh.setSoDienThoai(soDienThoai);

        StringBuilder loiNhan = new StringBuilder();
        boolean hopLe = kh_ctrl.kiemTra(kh, loiNhan);
        if (!hopLe) {
            Alert canhBao = new Alert(Alert.AlertType.ERROR);
            canhBao.setTitle("Cảnh báo");
            canhBao.setHeaderText(null);
            canhBao.setContentText(loiNhan.toString());
            canhBao.showAndWait();
            return;
        }
        
        // Sửa thông tin
        if (kh_ctrl.suaKhachHang(kh, loiNhan)) {
            Alert thongBao = new Alert(Alert.AlertType.INFORMATION);
            thongBao.setTitle("Thông báo");
            thongBao.setContentText(loiNhan.toString());
            thongBao.setHeaderText(null);
            thongBao.showAndWait();
            lamMoi();
        } else {
            Alert thongBao = new Alert(Alert.AlertType.ERROR);
            thongBao.setTitle("Thông báo");
            thongBao.setContentText(loiNhan.toString());
            thongBao.setHeaderText(null);
            thongBao.showAndWait();
        }
    }

    private void lamMoi() {
        maKhachHangDangChon = null;
        tfTenKhachHang.clear();
        tfCCCD.clear();
        tfEmail.clear();
        tfSoDienThoai.clear();
        tfTimTen.clear();
        tfTimCCCD.clear();
        tfTimEmail.clear();
        tfTimSDT.clear();

        bangKhachHang.getSelectionModel().clearSelection();
        taiDanhSachKhachHang();
        capNhatKhuVucNut();
        tfTenKhachHang.requestFocus();
    }

    public void xuLyTimKiem() {
        String ten = tfTimTen.getText().trim();
        String cccd = tfTimCCCD.getText().trim();
        String email = tfTimEmail.getText().trim();
        String soDienThoai = tfTimSDT.getText().trim();
        
        // Nếu tất cả đều rỗng -> hiển thị tất cả
        if (ten.isEmpty() && cccd.isEmpty() && email.isEmpty() && soDienThoai.isEmpty()) {
            taiDanhSachKhachHang();
            return;
        }
        
        List<KhachHang> dsTimDuoc = kh_ctrl.timKhachHangKetHop(ten, cccd, email, soDienThoai);
        if (dsTimDuoc != null && !dsTimDuoc.isEmpty()) {
            ObservableList<KhachHang> dsKH = FXCollections.observableArrayList(dsTimDuoc);
            bangKhachHang.setItems(dsKH);
        } else {
            Alert thongBao = new Alert(Alert.AlertType.INFORMATION);
            thongBao.setTitle("Thông báo");
            thongBao.setContentText("Không tìm thấy khách hàng");
            thongBao.setHeaderText(null);
            thongBao.showAndWait();
        }
    }

    public void xuLyXoa() {
        KhachHang khDaChon = bangKhachHang.getSelectionModel().getSelectedItem();
        if (khDaChon == null) {
            Alert thongBao = new Alert(Alert.AlertType.WARNING);
            thongBao.setTitle("Cảnh báo");
            thongBao.setContentText("Vui lòng chọn khách hàng cần xóa!");
            thongBao.setHeaderText(null);
            thongBao.showAndWait();
            return;
        }

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Xác nhận");
        conf.setHeaderText("Xóa khách hàng");
        conf.setContentText("Bạn có chắc chắn muốn xóa khách hàng " + khDaChon.getTenKhachHang() + "?");
        conf.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        if (conf.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            StringBuilder loiNhan = new StringBuilder();
            if (kh_ctrl.xoaKhachHang(khDaChon, loiNhan)) {
                Alert thongBao = new Alert(Alert.AlertType.INFORMATION);
                thongBao.setTitle("Thông báo");
                thongBao.setContentText(loiNhan.toString());
                thongBao.setHeaderText(null);
                thongBao.showAndWait();
                lamMoi();
            } else {
                Alert thongBao = new Alert(Alert.AlertType.ERROR);
                thongBao.setTitle("Thông báo");
                thongBao.setContentText(loiNhan.toString());
                thongBao.setHeaderText(null);
                thongBao.showAndWait();
            }
        }
    }

    private void validateTenKhachHang() {
        String ten = tfTenKhachHang.getText().trim();
        if (ten.isEmpty()) return;
        if (!ten.matches("^[\\p{L}\\s]+$")) {
            setErrorStyle(tfTenKhachHang);
            hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Tên khách hàng chỉ được chứa chữ cái và khoảng trắng");
            tfTenKhachHang.requestFocus();
        } else {
            resetFieldStyle(tfTenKhachHang);
        }
    }

    private void validateCCCD() {
        String cccd = tfCCCD.getText().trim();
        if (cccd.isEmpty()) return;
        if (!cccd.matches("^[0-9]{12}$")) {
            setErrorStyle(tfCCCD);
            hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "CCCD phải gồm đúng 12 chữ số");
            tfCCCD.requestFocus();
        } else {
            resetFieldStyle(tfCCCD);
        }
    }

    private void validateSoDienThoai() {
        String sdt = tfSoDienThoai.getText().trim();
        if (sdt.isEmpty()) return;
        if (!sdt.matches("^[0-9]{10,11}$")) {
            setErrorStyle(tfSoDienThoai);
            hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Số điện thoại phải gồm 10-11 chữ số");
            tfSoDienThoai.requestFocus();
        } else {
            resetFieldStyle(tfSoDienThoai);
        }
    }

    private void validateEmail() {
        String email = tfEmail.getText().trim();
        if (email.isEmpty()) {
            resetFieldStyle(tfEmail);
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
            setErrorStyle(tfEmail);
            hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Email không hợp lệ");
            tfEmail.requestFocus();
        } else {
            resetFieldStyle(tfEmail);
        }
    }

    private void setErrorStyle(TextField field) {
        field.setStyle("-fx-border-color: #ef4444; -fx-border-width: 2;");
    }

    private void resetFieldStyle(TextField field) {
        field.setStyle(null);
    }

    private void hienThiThongBao(Alert.AlertType loai, String tieuDe, String noiDung) {
        try {
            Alert alert = new Alert(loai);
            alert.setTitle(tieuDe);
            alert.setHeaderText(null);
            alert.setContentText(noiDung);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Không thể hiển thị thông báo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

